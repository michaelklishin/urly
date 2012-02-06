(defproject clojurewerkz/urly "1.0.0-SNAPSHOT"
  :description "A tiny Clojure library that parses URIs, URLs and relative values that real world HTML may contain"
  :license     { :name "Eclipse Public License" }
  :dependencies [[org.clojure/clojure "1.3.0"]]
  :multi-deps {
               "1.4" [[org.clojure/clojure "1.4.0-beta1"]]
               }
  :source-path        "src/clojure"
  :java-source-path   "src/java"
  :warn-on-reflection true)
