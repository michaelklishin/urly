(ns clojurewerkz.urly.core
  (:refer-clojure :exclude [resolve])
  (:import [clojurewerkz.urly UrlLike]
           [java.net URI URL]
           [com.google.common.net InternetDomainName]))


(defprotocol UrlLikeFactory
  (^UrlLike url-like [input] "Instantiates a new UrlLike object"))

(extend-protocol UrlLikeFactory
  URI
  (url-like [^URI input]
    (UrlLike/fromURI input))

  URL
  (url-like [^URL input]
    (UrlLike/fromURL input))

  String
  (url-like [^String input]
    (try
      (url-like (URI. input))
      (catch java.net.URISyntaxException e
        ;; TODO: fallback parsing strategies. MK.
        )))

  UrlLike
  (url-like [^UrlLike input]
    input))



(defprotocol PartsAccessors
  (^String protocol-of  [input] "Returns protocol of given input")
  (^String host-of      [input] "Returns host of given input")
  (^String port-of      [input] "Returns port of given input")
  (^String user-info-of [input] "Returns user information of given input")
  (^String path-of      [input] "Returns path of given input")
  (^String query-of     [input] "Returns query string of given input")
  (^String fragment-of  [input] "Returns fragment of given input")
  (^String tld-of       [input] "Returns top-level domain (public suffix) name of given input"))


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
    (UrlLike/pathOrDefault (.getPath input)))
  (query-of [^URI input]
    (.getQuery input))
  (fragment-of [^URI input]
    (.getFragment input))
  (tld-of [^URI input]
    (tld-of (UrlLike/fromURI input)))
  


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
    (UrlLike/pathOrDefault (.getPath input)))
  (query-of [^URL input]
    (.getQuery input))
  (fragment-of [^URL input]
    (.getRef input))
  (tld-of [^URL input]
    (tld-of (UrlLike/fromURL input)))


  UrlLike
  (protocol-of [^UrlLike input]
    (.getProtocol input))
  (host-of [^UrlLike input]
    (.getHost input))
  (port-of [^UrlLike input]
    (.getPort input))
  (user-info-of [^UrlLike input]
    (.getUserInfo input))
  (path-of [^UrlLike input]
    (.getPath input))
  (query-of [^UrlLike input]
    (.getQuery input))
  (fragment-of [^UrlLike input]
    (.getFragment input))
  (tld-of [^UrlLike input]
    (.getTld input))

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
    (fragment-of (URI. input)))
  (tld-of [^String input]
    (let [idn (InternetDomainName/from input)]
      (when (.hasPublicSuffix idn)
        (-> idn .publicSuffix .name)))))


;; protocols dispatch on the 1st argument, here we need to dispatch on
;; first two. MK.
(defmulti  resolve (fn [base other] [(type base) (type other)]))
(defmethod resolve [java.net.URI java.net.URI]
  [base other]
  (.resolve ^URI base ^URI other))
(defmethod resolve [java.net.URI String]
  [base other]
  (.resolve ^URI base ^String other))
(defmethod resolve [java.net.URI java.net.URL]
  [base other]
  (.resolve ^URI base (.toURI ^URL other)))
(defmethod resolve [String java.net.URI]
  [base other]
  (.resolve (URI. base) ^URI other))
(defmethod resolve [String String]
  [base other]
  (.resolve (URI. base) (URI. other)))
(defmethod resolve [String String]
  [base other]
  (.resolve (URI. base) (URI. other)))
(defmethod resolve [String java.net.URL]
  [base other]
  (.resolve (URI. base) (.toURI ^URL other)))



(defprotocol IsAbsolute
  (absolute? [input] "Returns true if this URI/URL is absolute"))

(extend-protocol IsAbsolute
  URI
  (absolute? [^URI input]
    (.isAbsolute input))

  URL
  (absolute? [^URL input]
    (absolute? (.toURI input)))

  String
  (absolute? [^String input]
    (absolute? (URI. input))))


(def relative? (complement absolute?))


(defn as-map
  "Returns a map of components (:protocol, :host, :port, :user-info, :path, :query, :fragment, :tld) for given input"
  [input]
  (let [urly (url-like input)]
    { :protocol (protocol-of urly) :host (host-of urly) :port (port-of urly) :user-info (user-info-of urly)
     :path (path-of urly) :query (query-of urly) :fragment (fragment-of urly) :tld (tld-of urly) }))
