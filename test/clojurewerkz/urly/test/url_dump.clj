(ns clojurewerkz.urly.test.url-dump
  (:refer-clojure :exclude [resolve])
  (:use clojurewerkz.urly.core
        clojure.test)
  (:require [clojure.java.io :as io]))

(deftest test-parsing-dumped-links
  (with-open [rdr (io/reader (io/resource "url_dump.txt"))]
      (doseq [x (line-seq rdr)]
        (let [urly (url-like x)]
          (is (host-of urly))))))
