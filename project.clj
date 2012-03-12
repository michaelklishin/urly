(defproject clojurewerkz/urly "1.0.0-SNAPSHOT"
  :description "A tiny Clojure library that parses URIs, URLs and relative values that real world HTML may contain"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"}
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [com.google.guava/guava "11.0.1"]]
  :profiles {:all { :dependencies [[com.google.guava/guava "11.0.1"]] }
             :1.4 { :dependencies [[org.clojure/clojure "1.4.0-beta4"]] }}
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :test-selectors {:default (fn [v] (not (:time-consuming v))),
                   :focus   (fn [v] (:focus v))}
  :warn-on-reflection true)