(defproject com.guokr/simbase "0.0.1"

    :description "A clojure document similarity server"
    :url "https://github.com/guokr/simbase/"

    :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "same as Clojure"}

    :dependencies [[org.clojure/clojure "1.5.1"]
                   [milieu "0.9.1"]
                   [clj-pid "0.1.1"]
                   [net.sf.trove4j/trove4j "3.0.3"]
                   [org.slf4j/slf4j-api "1.7.5"]
                   [org.slf4j/slf4j-log4j12 "1.7.5"]
                   [log4j/log4j "1.2.17"]
                   [com.esotericsoftware.kryo/kryo "2.21"]
                   [junit "4.0" :scope "test"]]
    :source-paths ["src/main/clojure"]
    :java-source-paths ["src/main/java"]
    :resource-paths ["src/main/resources"]

    :test-paths ["src/test/clojure" "src/test/java/benchmarks"]
    :test-selectors {:default (complement :integration)
                     :integration :integration
                     :all (constantly true)}
    :compile-path "target/classes"
    :target-path "target/"
    :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
    :jvm-opts ["-Xmx1g"]

    :aot [simbase]
    :main simbase.main)
