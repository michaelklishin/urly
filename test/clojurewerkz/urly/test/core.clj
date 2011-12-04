(ns clojurewerkz.urly.test.core
  (:use [clojurewerkz.urly.core]
        [clojure.test])
  (:import [java.net URI URL]
           [clojurewerkz.urly UrlLike]))


(deftest test-path-normalization
  (doseq [s ["" "/" nil]]
    (is (= "" (UrlLike/normalizePath s)))))


(deftest test-instantiating-url-like-from-uri1
  (let [uri      (URI. "http://apple.com/iphone")
        url-like (UrlLike/fromURI uri)]
    (is (= (.getScheme uri) (.getScheme   url-like)))
    (is (= (.getScheme uri) (.getProtocol url-like)))
    (is (= (.getHost uri)   (.getHost url-like)))
    (is (= (.getPath uri)   (.getPath url-like)))
    (is (= "/iphone"        (.getPath url-like)))
    (is (= (.getPath uri)   (.getFile url-like)))))

(deftest test-instantiating-url-like-from-uri
  (let [uri       (URI. "http://blahblah.smackernews.org")
        url-like  (UrlLike/fromURI uri)
        path      ""]
    (is (= path      (.getPath uri)))
    (is (= path      (.getPath url-like)))
    (is (nil? (.getQuery uri)))
    (is (nil? (.getQuery url-like)))
    (is (= ""  (.getPath uri)))
    (is (nil?  (.getRef url-like)))
    (is (nil?  (.getFragment url-like)))))


(deftest test-instantiating-url-like-from-uri-with-query-string
  (let [uri       (URI. "http://clojuredocs.org/search?x=0&y=0&q=split")
        url-like  (url-like uri)
        query-str "x=0&y=0&q=split"
        path      "/search"]
    (is (= path      (.getPath uri)))
    (is (= path      (.getPath url-like)))
    (is (= query-str (.getQuery uri)))
    (is (= query-str (.getQuery url-like)))))

(deftest test-instantiating-url-like-from-uri-with-query-string-and-fragment
  (let [uri       (URI. "http://blahblah.smackernews.org/articles?id=123#comments")
        url-like  (url-like uri)
        query-str "id=123"
        path      "/articles"
        fragment  "comments"]
    (is (= path      (.getPath uri)))
    (is (= path      (.getPath url-like)))
    (is (= query-str (.getQuery uri)))
    (is (= query-str (.getQuery url-like)))
    (is (= fragment  (.getFragment uri)))
    (is (= fragment  (.getFragment url-like)))
    (is (= fragment  (.getRef      url-like)))))


(deftest test-instantiating-url-like-from-url-that-does-not-end-with-a-slash
  (let [url       (URL. "http://blahblah.smackernews.org")
        url-like  (url-like url)
        path      ""]
    (is (= path      (.getPath url)))
    (is (= path      (.getPath url-like)))
    (is (nil? (.getQuery url)))
    (is (nil? (.getQuery url-like)))
    (is (= ""  (.getPath url)))
    (is (nil?  (.getRef url-like)))
    (is (nil?  (.getFragment url-like)))))

(deftest test-instantiating-url-like-from-url-that-does-end-with-a-slash
  (let [url       (URL. "http://blahblah.smackernews.org/")
        url-like  (url-like url)]
    (is (= "/"     (.getPath url)))
    (is (= ""      (.getPath url-like)))
    (is (nil? (.getQuery url)))
    (is (nil? (.getQuery url-like)))
    (is (nil?  (.getRef url)))
    (is (nil?  (.getRef url-like)))
    (is (nil?  (.getFragment url-like)))))
