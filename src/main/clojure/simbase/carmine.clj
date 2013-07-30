(ns simbase.carmine "Clojure Redis client & message queue."
  {:author "Peter Taoussanis"}
  (:refer-clojure :exclude [time get set keys type sync sort eval])
  (:require [clojure.string :as str]
            [simbase.commands :as commands]
            [taoensso.carmine
             (utils       :as utils)
             (protocol    :as protocol)
             (connections :as conns)]
            [taoensso.timbre      :as timbre]
            [taoensso.nippy.tools :as nippy-tools]))

;;;; Connections

(defmacro wcar
  "Evaluates body in the context of a thread-bound pooled connection to Redis
  server. Sends Redis commands to server as pipeline and returns the server's
  response. Releases connection back to pool when done.

  `conn` arg is a map with connection pool and spec options:
    {:pool {} :spec {:host \"127.0.0.1\" :port 6379}} ; Default
    {:pool {} :spec {:uri \"redis://redistogo:pass@panga.redistogo.com:9475/\"}}
    {:pool {} :spec {:host \"127.0.0.1\" :port 6379
                     :password \"secret\"
                     :timeout-ms 6000
                     :db 3}}

  A `nil` or `{}` `conn` or opts will use defaults. A `:none` pool can be used
  to skip connection pooling. For other pool options, Ref. http://goo.gl/EiTbn."
  [conn & body]
  `(let [{pool-opts# :pool spec-opts# :spec} ~conn
         [pool# conn#] (conns/pooled-conn pool-opts# spec-opts#)]
     (try
       (let [response# (protocol/with-context conn# ~@body)]
         (conns/release-conn pool# conn#)
         response#)
       (catch Exception e# (conns/release-conn pool# conn# e#) (throw e#)))))

(comment (wcar {} (ping) "not-a-Redis-command" (ping))
         (with-open [p (conns/conn-pool {})]
           (wcar {:pool p} (ping) (ping))))

;;;; Misc

;;; (number? x) for Carmine < v0.11.x backwards compatiblility
(defn as-long   [x] (when x (if (number? x) (long   x) (Long/parseLong     x))))
(defn as-double [x] (when x (if (number? x) (double x) (Double/parseDouble x))))
(defn as-bool   [x] (when x
                      (cond (or (true? x) (false? x))            x
                            (or (= x "false") (= x "0") (= x 0)) false
                            (or (= x "true")  (= x "1") (= x 1)) true
                            :else
                            (throw (Exception. (str "Couldn't coerce as bool: "
                                                    x))))))

(defmacro parse
  "Wraps body so that replies to any wrapped Redis commands will be parsed with
  `(f reply)`. Replaces any current parser; removes parser when `f` is nil."
  [f & body] `(binding [protocol/*parser* ~f] ~@body))

(defmacro parse-long    [& body] `(parse as-long   ~@body))
(defmacro parse-double  [& body] `(parse as-double ~@body))
(defmacro parse-bool    [& body] `(parse as-bool   ~@body))
(defmacro parse-keyword [& body] `(parse keyword   ~@body))
(defmacro parse-raw     [& body] `(parse (with-meta identity {:raw? true}) ~@body))

(defn kname
  "Joins keywords, integers, and strings to form an idiomatic compound Redis key
  name.

  Suggested key naming style:
    * \"category:subcategory:id:field\" basic form.
    * Singular category names (\"account\" rather than \"accounts\").
    * Dashes for long names (\"email-address\" rather than \"emailAddress\", etc.)."

  [& parts] (str/join ":" (map utils/keyname (filter identity parts))))

(comment (kname :foo/bar :baz "qux" nil 10))

(utils/defalias raw            protocol/raw)
(utils/defalias with-thaw-opts nippy-tools/with-thaw-opts)
(utils/defalias freeze         nippy-tools/wrap-for-freezing
  "Forces argument of any type (incl. keywords, simple numbers, and binary types)
  to be subject to automatic de/serialization with Nippy.")

(defn return
  "Special command that takes any value and returns it unchanged as part of
  an enclosing `wcar` pipeline response."
  [value]
  (let [vfn (constantly value)]
    (swap! (:parser-queue protocol/*context*) conj
           (with-meta (if-let [p protocol/*parser*] (comp p vfn) vfn)
             {:dummy-reply? true}))))

(comment (wcar {} (return :foo) (ping) (return :bar))
         (wcar {} (parse name (return :foo)) (ping) (return :bar)))

(defmacro with-replies
  "Alpha - subject to change.
  Evaluates body, immediately returning the server's response to any contained
  Redis commands (i.e. before enclosing `wcar` ends). Ignores any parser
  in enclosing (not _enclosed_) context.

  As an implementation detail, stashes and then `return`s any replies already
  queued with Redis server: i.e. should be compatible with pipelining."
  {:arglists '([:as-pipeline & body] [& body])}
  [& [s1 & sn :as sigs]]
  (let [as-pipeline? (= s1 :as-pipeline)
        body (if as-pipeline? sn sigs)]
    `(let [stashed-replies# (protocol/get-replies true)]
       (try (parse nil ~@body) ; Herewith dragons; tread lightly
            (protocol/get-replies ~as-pipeline?)
            (finally
             ;; doseq here broken with Clojure <1.5, Ref. http://goo.gl/5DvRt
             (parse nil (dorun (map return stashed-replies#))))))))

(comment (wcar {} (echo 1) (println (with-replies (ping))) (echo 2))
         (wcar {} (echo 1) (println (with-replies :as-pipeline (ping))) (echo 2)))

;;;; Standard commands

(commands/defcommands) ; This kicks ass - big thanks to Andreas Bielk!

;;;; Helper commands

(defn redis-call
  "Sends low-level requests to Redis. Useful for DSLs, certain kinds of command
  composition, and for executing commands that haven't yet been added to the
  official `commands.json` spec.

  (redis-call [:set \"foo\" \"bar\"] [:get \"foo\"])"
  [& requests]
  (doseq [[cmd & args] requests]
    (let [cmd-parts (-> cmd name str/upper-case (str/split #"-"))]
      (protocol/send-request (into (vec cmd-parts) args)))))

(comment (wcar {} (redis-call [:set "foo" "bar"] [:get "foo"]
                              [:config-get "*max-*-entries*"])))

(defmacro atomically
  "Executes all Redis commands in body as a single transaction and returns
  server response vector or an empty vector if transaction failed.

  Body may contain a (discard) call to abort transaction."
  [watch-keys & body]
  `(do
     (with-replies ; discard "OK" and "QUEUED" replies
       (when-let [wk# (seq ~watch-keys)] (apply watch wk#))
       (multi)
       ~@body)

     ;; Body discards will result in an (exec) exception:
     (parse #(if (instance? Exception %) [] %) (exec))))

(defmacro ensure-atomically
  "Repeatedly calls `atomically` on body until transaction succeeds or
  given limit is hit, in which case an exception will be thrown."
  [{:keys [max-tries]
    :or   {max-tries 100}}
   watch-keys & body]
  `(let [watch-keys# ~watch-keys
         max-idx#    ~max-tries]
     (loop [idx# 0]
       (let [result# (with-replies (atomically watch-keys# ~@body))]
         (if (not= [] result#)
           (remember result#)
           (if (= idx# max-idx#)
             (throw (Exception. (str "`ensure-atomically` failed after " idx#
                                     " attempts")))
             (recur (inc idx#))))))))

(comment (wcar {} (ensure-atomically {} [:foo] (set :foo "new-val") (get :foo))))
