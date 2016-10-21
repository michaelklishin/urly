(defproject clojurewerkz/urly "2.0.0-alpha6-SNAPSHOT"
  :description "A tiny Clojure library that parses URIs, URLs and relative values that real world HTML may contain"
  :min-lein-version "2.5.1"
  :license {:name "Eclipse Public License"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.google.guava/guava "19.0"]]
  :profiles {:dev {:resource-paths ["test/resources"]}
             :1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}
             :1.6 {:dependencies [[org.clojure/clojure "1.6.0"]]}
             :1.7 {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :1.9 {:dependencies [[org.clojure/clojure "1.9.0-alpha13"]]}}
  :aliases  {"all" ["with-profile" "dev:dev,1.5:dev,1.7,1.8"]}
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :javac-options     ["-target" "1.6" "-source" "1.6"]
  :repositories {"sonatype" {:url "http://oss.sonatype.org/content/repositories/releases"
                             :snapshots false
                             :releases {:checksum :fail :update :always}}
                 "sonatype-snapshots" {:url "http://oss.sonatype.org/content/repositories/snapshots"
                               :snapshots true
                               :releases {:checksum :fail :update :always}}}
  :test-selectors {:default    (fn [v] (not (:time-consuming v)))
                   :focus      :focus
                   :core       :core
                   :mutation   :mutation
                   :resolution :resolution}
  :global-vars {*warn-on-reflection* true})
