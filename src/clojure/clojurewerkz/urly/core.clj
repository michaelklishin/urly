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
  (^String protocol-of  [input] "Returns protocol of given input")
  (^String host-of      [input] "Returns host of given input")
  (^String port-of      [input] "Returns port of given input")
  (^String user-info-of [input] "Returns user information of given input")
  (^String path-of      [input] "Returns path of given input")
  (^String query-of     [input] "Returns query string of given input")
  (^String fragment-of  [input] "Returns fragment of given input"))


(extend-protocol PartsAccessors
  URI
  (protocol-of [^URI input]
    (-> input .getScheme .toLowerCase))
  (host-of [^URI input]
    (-> input .getHost .toLowerCase))
  (port-of [^URI input]
    (.getPort input))
  (user-info-of [^URI input]
    (.getUserInfo input))
  (path-of [^URI input]
    (UrlLike/normalizePath (.getPath input)))
  (query-of [^URI input]
    (.getQuery input))
  (fragment-of [^URI input]
    (.getFragment input))


  URL
  (protocol-of [^URL input]
    (-> input .getProtocol .toLowerCase))
  (host-of [^URL input]
    (-> input .getHost .toLowerCase))
  (port-of [^URL input]
    (.getPort input))
  (user-info-of [^URL input]
    (.getUserInfo input))
  (path-of [^URL input]
    (UrlLike/normalizePath (.getPath input)))
  (query-of [^URL input]
    (.getQuery input))
  (fragment-of [^URL input]
    (.getRef input))


  String
  ;; TODO: switch to UrlLike once it supports
  ;;       strings + most of edge cases
  (protocol-of [^String input]
    (protocol-of (URI. input)))
  (host-of [^String input]
    (host-of (URI. input)))
  (port-of [^String input]
    (port-of (URI. input)))
  (user-info-of [^String input]
    (user-info-of (URI. input)))
  (path-of [^String input]
    (path-of (URI. input)))
  (query-of [^String input]
    (query-of (URI. input)))
  (fragment-of [^String input]
    (fragment-of (URI. input))))
