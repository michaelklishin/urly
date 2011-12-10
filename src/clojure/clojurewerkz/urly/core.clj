(ns clojurewerkz.urly.core
  (:import [clojurewerkz.urly UrlLike]
           [java.net URI URL]))


(defprotocol UrlLikeFactory
  (^UrlLike url-like [input] "Instantiates a new UrlLike object"))

(extend-protocol UrlLikeFactory
  URI
  (url-like [^URI input]
    (UrlLike/fromURI input))

  URL
  (url-like [^URL input]
    (UrlLike/fromURL input)))



(defprotocol PartsAccessors
  (^String host-of [input] "Returns host of given input"))


(extend-protocol PartsAccessors
  URI
  (host-of [^URI input]
    (-> input .getHost .toLowerCase))

  URL
  (host-of [^URL input]
    (-> input .getHost .toLowerCase))

  String
  (host-of [^String input]
    ;; TODO: switch to UrlLike once it supports
    ;;       strings + most of edge cases
    (host-of (URI. input))))
