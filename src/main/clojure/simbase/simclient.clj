(ns simbase.simclient
  (:require [simbase.carmine :as car]))

(def server1-conn {:pool {} :spec {:port 7654}})

(defmacro wcar* [& body] `(car/wcar server1-conn ~@body))
