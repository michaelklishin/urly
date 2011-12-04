(ns clojurewerkz.urly.core
  (:import [clojurewerkz.urly UrlLike]))


(defprotocol UrlLikeFactory
  (^UrlLike url-like [input] "Instantiates a new UrlLike object"))

(extend-protocol UrlLikeFactory
  java.net.URI
  (url-like [^URI input]
    (UrlLike/fromURI input))

  java.net.URL
  (url-like [^URL input]
    (UrlLike/fromURL input)))
