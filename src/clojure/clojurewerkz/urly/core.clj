(ns clojurewerkz.urly.core
  (:refer-clojure :exclude [resolve])
  (:require [clojure.stacktrace :as strace])
  (:import [clojurewerkz.urly UrlLike]
           [java.net URI URL]
           [com.google.common.net InternetDomainName]))


(defn- uri-or-nil
  "Returns a java.net.URI instance or nil if URI failed to parse"
  [^String s]
  (try
    (URI. s)
    (catch java.net.URISyntaxException e
      nil)))

(defprotocol UrlLikeFactory
  (^clojurewerkz.urly.UrlLike url-like [input] "Instantiates a new UrlLike object"))

(declare eliminate-extra-protocol-prefixes)
(extend-protocol UrlLikeFactory
  URI
  (url-like [^URI input]
    (UrlLike/fromURI input))

  URL
  (url-like [^URL input]
    (UrlLike/fromURL input))

  String
  (url-like [^String input]
    ;; first try detecting cases like "google.com", which java.net.URI does parse but not the way
    ;; you typically want ("google.com" is parsed as path, not hostname). If that fails, 
    (try
      (let [idn (InternetDomainName/from input)]
        (UrlLike/from idn))
      (catch IllegalArgumentException iae
        ;; if that fails, try other parsing strategies
        (let [inputs [(eliminate-extra-protocol-prefixes input)
                      (eliminate-extra-protocol-prefixes (.replaceAll input " " "%20"))]]
          (when-let [s (some uri-or-nil inputs)]
            (url-like s))))))

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
    (when-let [s (.getScheme input)]
      (.toLowerCase s)))
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
    (protocol-of (.toURI input)))
  (host-of [^URL input]
    (host-of (.toURI input)))
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
    (protocol-of (url-like input)))
  (host-of [^String input]
    (host-of (url-like input)))
  (port-of [^String input]
    (port-of (url-like input)))
  (user-info-of [^String input]
    (user-info-of (url-like input)))
  (path-of [^String input]
    (path-of (url-like input)))
  (query-of [^String input]
    (query-of (url-like input)))
  (fragment-of [^String input]
    (fragment-of (url-like input)))
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

  UrlLike
  (absolute? [^UrlLike input]
    (absolute? (.toURI input)))

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


(defprotocol Mutation
  (without-query-string-and-fragment [input] "Strips off query string and fragment. Returns value of the same type as input."))

(extend-protocol Mutation
  URI
  (without-query-string-and-fragment [^URI input]
    (.toURI ^UrlLike (without-query-string-and-fragment (UrlLike/fromURI input))))

  URL
  (without-query-string-and-fragment [^URL input]
    (.toURL ^UrlLike (without-query-string-and-fragment (UrlLike/fromURL input))))

  String
  (without-query-string-and-fragment [^String input]
    (.toString ^UrlLike (without-query-string-and-fragment (url-like input))))

  UrlLike
  (without-query-string-and-fragment [^UrlLike input]
    (.withoutQueryStringAndFragment input)))



;;
;; Broken real world URLs/URIs
;;

(def ^:const extra-protocol-regexp #"^(?i:https?://)+(https?)://")
(def ^:const extra-protocol-re-str "^(?i:https?://)+(https?)://")

(defn eliminate-extra-protocol-prefixes
  [^String s]
  (let [[all proto] (re-find extra-protocol-regexp s)]
    (if proto
      (.replaceFirst s extra-protocol-re-str (str proto "://"))
      s)))