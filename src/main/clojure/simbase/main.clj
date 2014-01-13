(ns simbase.main
  (:require [clj-pid.core :as pid])
  (:gen-class :main true))

(def logger (org.slf4j.LoggerFactory/getLogger (class com.guokr.simbase.SimBase)))

(defn- load-config []
  (try
    (let [yaml (org.yaml.snakeyaml.Yaml.)]
      (.load yaml (java.io.FileReader. "config/simbase.yaml")))
    (catch java.io.IOException e 
      (.warn logger "config file simbase.yaml was not found, loading the default config"))))

(defn -main [& args]
  (let [config (merge {"server" {"ip" "0.0.0.0" "port" 7654} "pidfile" "log/pid"} (load-config))
        pid-file (get config "pidfile")
        port  (get config "server" "port")]
    (println "------------------------------------------------")
    (println "Simbase[" (pid/current) "] started on port" port)
    (println "------------------------------------------------")
    (pid/save pid-file)
    (pid/delete-on-shutdown! pid-file)

    (let [context  (com.guokr.simbase.SimConfig. config)
			    database (com.guokr.simbase.SimBase. context)]
      (try
			  (.run database)
        (catch java.lang.Throwable e (do
          (.error logger "Server Error!" e)
                (System/exit -1)))))))

