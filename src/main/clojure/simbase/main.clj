(ns simbase.main
  (:require [clj-pid.core :as pid])
  (:gen-class :main true))

(def logger (org.slf4j.LoggerFactory/getLogger (class com.guokr.simbase.SimBase)))

(defn- load-config []
  (try
    (let [yaml (org.yaml.snakeyaml.Yaml.)]
      (.load yaml (java.io.FileReader. "config/server.yaml")))
    (catch java.io.IOException e
      (.warn logger "config file server.yaml was not found, loading the default config"))))

(defn -main [& args]
  (let [config (merge {"cronInterval" 120000 "port" 7654 "pidfile" "log/pid"} (load-config))
        pid-file (get config "pidfile")
        port  (get config "port")]
    (println "------------------------------------------------")
    (println "Simbase[" (pid/current) "] started on port" port)
    (println "------------------------------------------------")
    (pid/save pid-file)
    (pid/delete-on-shutdown! pid-file)

    (let [context  (java.util.HashMap. config)
                database (com.guokr.simbase.SimBase. context)
          server   (org.wahlque.net.server.Server. context
                     (org.wahlque.net.action.ActionRegistry/getInstance))]
            (.put context "simbase" database)
      (try
              (.run server)
        (catch java.lang.Throwable e (do
          (.error logger "Server Error!" e)
                (System/exit -1)))))))

