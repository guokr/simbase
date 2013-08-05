(defproject com.guokr/simbase "0.0.0"

    :description "A clojure document similarity server"
    :url "https://github.com/guokr/simbase/"

    :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "same as Clojure"}

    :dependencies [[org.clojure/clojure "1.5.1"]
                   [com.taoensso/carmine "2.0.0"]
                   [net.sf.trove4j/trove4j "3.0.3"]
                   [com.esotericsoftware.kryo/kryo "2.21"]];;TODO:add org.slf4j 
;    :dev-dependencies [[net.sf.trove4j/trove4j "3.0.3" :classifier "sources"]]
    :source-paths ["src/main/clojure" "trove"]
    :java-source-paths ["src/main/java"]
    :resource-paths ["src/main/resources"]

    :test-paths ["src/test/clojure" "src/test/java"]

    :compile-path "targets/classes"
    :target-path "targets/"
    :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]

    :main com.guokr.simbase.SimBase)
