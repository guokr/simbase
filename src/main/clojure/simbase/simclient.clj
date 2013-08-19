(ns simbase.simclient
  (:require [simbase.carmine :as car]))

(def simbase-conn {:pool {} :spec {:port 7654}})

(defmacro wcar* [& body] `(car/wcar simbase-conn ~@body))
