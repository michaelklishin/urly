(defproject clojurewerkz/urly "1.0.0"
  :description "A tiny Clojure library that parses URIs, URLs and relative values that real world HTML may contain"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"}
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [com.google.guava/guava "11.0.1"]]
  :profiles {:dev {:resource-paths ["test/resources"]}
             :1.4 { :dependencies [[org.clojure/clojure "1.4.0-beta7"]] }}
  :aliases  { "all" ["with-profile" "dev:dev,1.4"] }
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :javac-options      ["-target" "1.6" "-source" "1.6"]
  :test-selectors {:default    (fn [v] (not (:time-consuming v))),
                   :focus      :focus
                   :core       :core
                   :mutation   :mutation
                   :resolution :resolution}
  :warn-on-reflection true)