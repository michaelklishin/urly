(ns clojurewerkz.urly.test.core
  (:refer-clojure :exclude [resolve])
  (:use [clojurewerkz.urly.core]
        [clojure.test])
  (:import [java.net URI URL]
           [clojurewerkz.urly UrlLike]))


(deftest test-path-normalization
  (doseq [s ["" "/" nil]]
    (is (= "" (UrlLike/normalizePath s))))
  (is (= "/login" (UrlLike/normalizePath "/LOGIN"))))

(deftest test-protocol-normalization
  (is (= "https" (UrlLike/normalizePath "HTTpS"))))


;;
;; Parts accessors
;;

(deftest test-protocol-of
  (is (= "http"  (protocol-of (URI. "http://clojure.org"))))
  (is (= "https" (protocol-of (URL. "htTPS://Www.clojure.org"))))
  (is (= "http"  (protocol-of "http://clojure.org")))
  (is (= "https" (protocol-of "HTTPS://CLOJURE.org"))))

(deftest test-host-of
  (is (= "clojure.org"     (host-of (URI. "http://clojure.org"))))
  (is (= "www.clojure.org" (host-of (URL. "https://Www.clojure.org"))))
  (is (= "clojure.org"     (host-of "http://clojure.org")))
  (is (= "clojure.org"     (host-of "https://CLOJURE.org"))))

(deftest test-port-of
  (is (= -1   (port-of (URI. "http://clojure.org"))))
  (is (= 7474 (port-of (URL. "https://Www.clojure.org:7474"))))
  (is (= 443  (port-of "http://clojure.org:443")))
  (is (= -1   (port-of "https://CLOJURE.org"))))

(deftest test-user-info-of
  (is (nil?            (user-info-of (URI. "http://clojure.org"))))
  (is (= "guest:guest" (user-info-of (URL. "https://guest:guest@Www.clojure.org:7474"))))
  (is (= "guest:"      (user-info-of "http://guest:@clojure.org:443")))
  (is (nil?            (user-info-of "https://CLOJURE.org"))))

(deftest test-path-of
  (is (= ""           (path-of (URI. "http://clojure.org"))))
  (is (= ""           (path-of (URL. "https://Www.clojure.org/"))))
  (is (= "/protocols" (path-of "http://clojure.org/Protocols")))
  (is (= ""           (path-of "https://TWITTER.com/#!/a/path/"))))

(deftest test-query-of
  (is (nil?              (query-of (URI. "http://clojure.org"))))
  (is (= ""              (query-of (URL. "https://Www.clojure.org/?"))))
  (is (= "query=clojure" (query-of "http://google.com/search?query=clojure")))
  (is (nil?              (query-of "https://TWITTER.com/#!/a/path/"))))

(deftest test-fragment-of
  (is (nil?              (fragment-of (URI. "http://clojure.org"))))
  (is (= ""              (fragment-of (URL. "https://Www.clojure.org/#"))))
  (is (= "doc"           (fragment-of (URL. "https://Www.clojure.org/?search=some#doc"))))
  (is (= "!/a/path/"     (fragment-of "https://TWITTER.com/#!/a/path/"))))


;;
;; resolution
;;

(deftest test-resolve
  (is (= (resolve (URI. "http://clojure.org") (URI. "/Protocols"))                   (URI. "http://clojure.org/Protocols")))
  (is (= (resolve (URI. "http://clojure.org") "/Protocols")                          (URI. "http://clojure.org/Protocols")))
  (is (= (resolve (URI. "http://clojure.org") (URL. "http://clojure.org/Protocols")) (URI. "http://clojure.org/Protocols")))
  (is (= (resolve "http://clojure.org"        (URI. "/Protocols"))                   (URI. "http://clojure.org/Protocols")))
  (is (= (resolve "http://clojure.org"        (URL. "http://clojure.org/Protocols")) (URI. "http://clojure.org/Protocols"))))

(deftest test-absolute?
  (are [input] (is (absolute? input))
       (URI. "http://clojure.org")
       (URI. "http://clojure.org/Protocols")
       (URL. "http://clojure.org")
       (URL. "http://clojure.org/Protocols")
       "http://clojure.org"
       "http://clojure.org/Protocols")
  (are [input] (is (not (absolute? input)))
       (URI. "//clojure.org")
       (URI. "/Protocols")
       "//clojure.org"
       "/Protocols"))

(deftest test-relative?
  (are [input] (is (not (relative? input)))
       (URI. "http://clojure.org")
       (URI. "http://clojure.org/Protocols")
       (URL. "http://clojure.org")
       (URL. "http://clojure.org/Protocols")
       "http://clojure.org"
       "http://clojure.org/Protocols")
  (are [input] (is (relative? input))
       (URI. "//clojure.org")
       (URI. "/Protocols")
       "//clojure.org"
       "/Protocols"))

;;
;; UrlLike
;;

(deftest test-instantiating-url-like-from-uri1
  (let [uri      (URI. "http://apple.com/iphone")
        url-like (UrlLike/fromURI uri)
        host     "apple.com"]
    (is (= host             (.getHost uri)))
    (is (= host             (.getHost url-like)))
    (is (= (.getScheme uri) (.getScheme   url-like)))
    (is (= (.getScheme uri) (.getProtocol url-like)))
    (is (= (.getHost uri)   (.getHost url-like)))
    (is (= (.getPath uri)   (.getPath url-like)))
    (is (= "/iphone"        (.getPath url-like)))
    (is (= (.getPath uri)   (.getFile url-like)))))

(deftest test-instantiating-url-like-from-uri
  (let [uri       (URI. "http://blahblah.smackernews.org")
        url-like  (UrlLike/fromURI uri)
        path      ""
        host      "blahblah.smackernews.org"]
    (is (= host      (.getHost uri)))
    (is (= host      (.getHost url-like)))
    (is (= path      (.getPath uri)))
    (is (= path      (.getPath url-like)))
    (is (nil? (.getQuery uri)))
    (is (nil? (.getQuery url-like)))
    (is (nil?  (.getRef url-like)))
    (is (nil?  (.getFragment url-like)))))

(deftest test-instantiating-url-like-from-https-uri
  (let [uri       (URI. "HTTPS://blahblah.smackernews.org")
        url-like  (UrlLike/fromURI uri)
        path      ""
        host      "blahblah.smackernews.org"
        protocol  "https"]
    (is (= "HTTPS"   (.getScheme uri)))
    (is (= protocol  (.getScheme url-like)))
    (is (= protocol  (.getProtocol url-like)))
    (is (= host      (.getHost uri)))
    (is (= host      (.getHost url-like)))
    (is (= path      (.getPath uri)))
    (is (= path      (.getPath url-like)))
    (is (nil? (.getQuery uri)))
    (is (nil? (.getQuery url-like)))
    (is (nil?  (.getRef url-like)))
    (is (nil?  (.getFragment url-like)))))

(deftest test-instantiating-url-like-from-relative-uri
  (let [uri       (URI. "/LOGIN")
        url-like  (UrlLike/fromURI uri)
        path      "/login"
        host      nil
        protocol  nil]
    (is (= protocol  (.getScheme uri)))
    (is (= protocol  (.getScheme url-like)))
    (is (= protocol  (.getProtocol url-like)))
    (is (= host      (.getHost uri)))
    (is (= host      (.getHost url-like)))
    (is (= "/LOGIN"  (.getPath uri)))
    (is (= path      (.getPath url-like)))
    (is (nil? (.getQuery uri)))
    (is (nil? (.getQuery url-like)))
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
        urly      (url-like url)]
    (is (= "/"     (.getPath url)))
    (is (= ""      (.getPath urly)))
    (is (nil? (.getQuery url)))
    (is (nil? (.getQuery urly)))
    (is (nil?  (.getRef url)))
    (is (nil?  (.getRef urly)))
    (is (nil?  (.getFragment urly)))))


(defn equal-part-by-part
  [^UrlLike a ^UrlLike b]
  (is (= (host-of a)      (host-of b)))
  (is (= (protocol-of a)  (protocol-of b)))
  (is (= (port-of a)      (port-of b)))
  (is (= (user-info-of a) (user-info-of b)))
  (is (= (path-of a)      (path-of b)))
  (is (= (query-of a)     (query-of b)))
  (is (= (fragment-of a)  (fragment-of b))))

(deftest test-mutation-of-hostname
  (let [url       (URL. "http://blahblah.smackernews.org/iphone")
        urly      (url-like url)
        expected  (url-like (URL. "http://apple.com/iphone"))
        mutated   (.mutateHost urly "appLE.com")]
    (is (equal-part-by-part expected mutated))
    (is (= (host-of expected)      (host-of (.mutateHostname urly "apple.com"))))))


(deftest test-mutation-of-protocol
  (let [url       (URL. "http://github.com/michaelklishin/urly")
        urly      (url-like url)
        expected  (url-like (URL. "https://github.com/michaelklishin/urly"))
        mutated   (.mutateProtocol urly "hTTps")]
    (is (equal-part-by-part expected mutated))))


(deftest test-mutation-of-port-as-int
  (let [url       (URL. "https://github.com:434/michaelklishin/urly")
        urly      (url-like url)
        expected  (url-like (URL. "https://github.com:443/michaelklishin/urly"))
        mutated   (.mutatePort urly 443)]
    (is (equal-part-by-part expected mutated))))


(deftest test-mutation-of-port-as-string
  (let [url       (URL. "https://github.com:434/michaelklishin/urly")
        urly      (url-like url)
        expected  (url-like (URL. "https://github.com:443/michaelklishin/urly"))
        mutated   (.mutatePort urly "443")]
    (is (equal-part-by-part expected mutated))))


(deftest test-mutation-of-user-info-of
  (let [url       (URL. "https://megacorp.internal")
        urly      (url-like url)
        expected  (url-like (URL. "https://guest:GUEST@megacorp.internal"))
        ;; username is not lowercased because passwords are case-sensitive.
        mutated   (.mutateUserInfo urly "guest:GUEST")]
    (is (equal-part-by-part expected mutated))))

(deftest test-mutation-of-path-without-slash
  (let [url       (URL. "https://github.com/michaelklishin/urly")
        urly      (url-like url)
        expected  (url-like (URL. "https://github.com/michaelklishin/monger"))
        mutated   (.mutatePath urly "michaelklishin/monger")]
    (is (equal-part-by-part expected mutated))))

(deftest test-mutation-of-path-with-slash
  (let [url       (URL. "https://github.com/michaelklishin/urly")
        urly      (url-like url)
        expected  (url-like (URL. "https://github.com/michaelklishin/monger"))
        mutated   (.mutatePath urly "/michaelklishin/monger")]
    (is (equal-part-by-part expected mutated))))


(deftest test-mutation-of-query-string
  (let [url       (URL. "https://secure.travis-ci.org/michaelklishin/urly.png?branch=master")
        urly      (url-like url)
        expected  (url-like (URL. "https://secure.travis-ci.org/michaelklishin/urly.png?branch=next"))
        mutated   (.mutateQuery urly "branch=next")]
    (is (equal-part-by-part expected mutated))))


(deftest test-mutation-of-fragment
  (let [url       (URL. "https://travis-ci.org/michaelklishin/urly#latest")
        urly      (url-like url)
        expected  (url-like (URL. "https://travis-ci.org/michaelklishin/urly#log"))
        mutated   (.mutateFragment urly "log")]
    (is (equal-part-by-part expected mutated))))
