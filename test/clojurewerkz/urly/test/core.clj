(ns clojurewerkz.urly.test.core
  (:refer-clojure :exclude [resolve])
  (:use [clojurewerkz.urly.core]
        [clojure.test])
  (:import [java.net URI URL]
           [clojurewerkz.urly UrlLike]))

(println (str "Using Clojure version " *clojure-version*))


(defn equal-part-by-part
  [^UrlLike a ^UrlLike b]
  (is (= (host-of a)      (host-of b)))
  (is (= (protocol-of a)  (protocol-of b)))
  (is (= (port-of a)      (port-of b)))
  (is (= (user-info-of a) (user-info-of b)))
  (is (= (path-of a)      (path-of b)))
  (is (= (query-of a)     (query-of b)))
  (is (= (fragment-of a)  (fragment-of b))))



(deftest test-path-normalization
  (doseq [s ["" "/" nil]]
    (is (= "/" (UrlLike/pathOrDefault s))))
  (is (= "/LOGIN" (UrlLike/pathOrDefault "/LOGIN"))))

(deftest test-protocol-normalization
  (is (= "https" (UrlLike/normalizeProtocol "HTTpS"))))

(deftest test-extra-protocol-prefixes
  (is (= (UrlLike/eliminateDoubleProtocol "http://https://pirmasenser-zeitung.de/content/abonnement/pz_card/index.shtml" "https")
         "https://pirmasenser-zeitung.de/content/abonnement/pz_card/index.shtml"))
  (are [i o] (is (= (eliminate-extra-protocol-prefixes i) o))
       "http://https://pirmasenser-zeitung.de/content/abonnement/pz_card/index.shtml" "https://pirmasenser-zeitung.de/content/abonnement/pz_card/index.shtml"
       "http://http://https://beyond-broken.com/" "https://beyond-broken.com/"
       "https://https://https://http://beyond-broken.com/" "http://beyond-broken.com/"
       "http://https://iq-shop.de/customer/account/?___sid=s" "https://iq-shop.de/customer/account/?___sid=s")
  (let [input "http://https://pirmasenser-zeitung.de/content/abonnement/pz_card/index.shtml"
        urly  (url-like input)]
    (is (= (protocol-of urly) "https"))
    (is (= (host-of urly) "pirmasenser-zeitung.de")))
  (let [input "http://https://http://pirmasenser-zeitung.de/content/abonnement/pz_card/index.shtml"
        urly  (url-like input)]
    (is (= (protocol-of urly) "http"))
    (is (= (host-of urly) "pirmasenser-zeitung.de"))))


;;
;; Parts accessors
;;

(deftest test-protocol-of
  (is (= "http"  (protocol-of (URI. "http://clojure.org"))))
  (is (= "https" (protocol-of (URL. "htTPS://Www.clojure.org"))))
  (is (= "http"  (protocol-of "http://clojure.org")))
  (is (= "http"  (protocol-of "clojure.org")))
  (is (nil? (protocol-of "/Protocols")))
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
  (is (= "/"          (path-of (URI. "http://clojure.org"))))
  (is (= "/"          (path-of (URL. "https://Www.clojure.org/"))))
  (is (= "/Protocols" (path-of "http://clojure.org/Protocols")))
  (is (= "/"          (path-of "https://TWITTER.com/#!/a/path/"))))

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
       (url-like "http://clojure.org/Protocols")
       "http://clojure.org"
       "http://clojure.org/Protocols")
  (are [input] (is (relative? input))
       (URI. "//clojure.org")
       (URI. "/Protocols")
       (url-like "clojure.org/Protocols")
       (url-like "//clojure.org/Protocols")
       (url-like "/Protocols")
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

(deftest test-instantiating-url-like-from-domain-name
  (let [uri      "arstechnica.com"
        url-like (UrlLike/homepageOf uri)
        host     "arstechnica.com"]
    (is (= host   (.getHost url-like)))
    (is (= "http" (.getScheme   url-like)))
    (is (= "http" (.getProtocol url-like)))
    (is (= host   (.getHost url-like)))
    (is (= "/"    (.getPath url-like)))))

(deftest test-instantiating-url-like-from-domain-name-and-protocol
  (let [uri   "github.com"
        urly  (UrlLike/homepageOf uri "https")
        host  "github.com"]
    (is (= host    (.getHost urly)))
    (is (= "https" (.getScheme   urly)))
    (is (= "https" (.getProtocol urly)))
    (is (= host    (.getHost urly)))
    (is (= "/"     (.getPath urly)))
    (is (= -1      (.getPort urly)))))

(deftest test-instantiating-url-like-from-uri2
  (let [uri       (URI. "http://blahblah.smackernews.org")
        url-like  (UrlLike/fromURI uri)
        path      "/"
        host      "blahblah.smackernews.org"]
    (is (= host      (.getHost uri)))
    (is (= host      (.getHost url-like)))
    (is (= ""        (.getPath uri)))
    (is (= path      (.getPath url-like)))
    (is (nil? (.getQuery uri)))
    (is (nil? (.getQuery url-like)))
    (is (nil?  (.getRef url-like)))
    (is (nil?  (.getFragment url-like)))))


(deftest test-instantiating-url-like-from-https-uri
  (let [uri       (URI. "HTTPS://blahblah.smackernews.org")
        url-like  (UrlLike/fromURI uri)
        path      "/"
        host      "blahblah.smackernews.org"
        protocol  "https"]
    (is (= "HTTPS"   (.getScheme uri)))
    (is (= protocol  (.getScheme url-like)))
    (is (= protocol  (.getProtocol url-like)))
    (is (= host      (.getHost uri)))
    (is (= host      (.getHost url-like)))
    (is (= ""        (.getPath uri)))
    (is (= path      (.getPath url-like)))
    (is (nil? (.getQuery uri)))
    (is (nil? (.getQuery url-like)))
    (is (nil?  (.getRef url-like)))
    (is (nil?  (.getFragment url-like)))))

(deftest test-instantiating-url-like-from-relative-uri
  (let [uri       (URI. "/LOGIN")
        url-like  (UrlLike/fromURI uri)
        path      "/LOGIN"
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
        urly      (url-like uri)
        query-str "x=0&y=0&q=split"
        path      "/search"]
    (is (= path      (.getPath uri)))
    (is (= path      (.getPath urly)))
    (is (= query-str (.getQuery uri)))
    (is (= query-str (.getQuery urly)))))


(deftest test-instantiating-url-like-from-uri-with-unescaped-query-string
  (let [s    "http://brokensite.com/index.php?cl=search&searchparam=arthritis besteck"
        uri  (URI. "http://brokensite.com/index.php?cl=search&searchparam=arthritis%20besteck")
        urly (url-like s)
        query-str "cl=search&searchparam=arthritis besteck"
        path      "/index.php"]
    (is (= query-str (query-of urly)))
    (is (= (host-of s) (host-of urly) (host-of uri)))))

(deftest test-instantiating-url-like-from-uri-with-query-string-and-fragment
  (let [uri       (URI. "http://blahblah.smackernews.org/articles?id=123#comments")
        urly      (url-like uri)
        query-str "id=123"
        path      "/articles"
        fragment  "comments"]
    (is (= path      (.getPath uri)))
    (is (= path      (.getPath urly)))
    (is (= query-str (.getQuery uri)))
    (is (= query-str (.getQuery urly)))
    (is (= fragment  (.getFragment uri)))
    (is (= fragment  (.getFragment urly)))
    (is (= fragment  (.getRef      urly)))))

(deftest test-instantiating-url-like-from-uri-that-only-has-domain-name
  (are [s] (is (let [urly (url-like s)]
                 (and (= (host-of urly) s)
                      (= (port-of urly) -1)
                      (= (protocol-of urly) "http"))))
       "google.com"
       "amazon.co.uk"
       "mootools.net"
       "defprotocol.org"
       "rubyamqp.info")
  (are [a b] (is (equal-part-by-part (url-like a) (url-like b)))
       "defprotocol.org" "http://defprotocol.org/"
       "defprotocol.org" "http://defprotocol.org"))


(deftest test-instantiating-url-like-from-url-that-does-not-end-with-a-slash
  (let [url     (URL. "http://blahblah.smackernews.org")
        urly    (url-like url)
        path    "/"]
    (is (= ""   (.getPath url)))
    (is (= path (.getPath urly)))
    (is (nil? (.getQuery url)))
    (is (nil? (.getQuery urly)))
    (is (= ""  (.getPath url)))
    (is (nil?  (.getRef urly)))
    (is (nil?  (.getFragment urly)))))

(deftest test-instantiating-url-like-from-url-that-does-end-with-a-slash
  (let [url       (URL. "http://blahblah.smackernews.org/")
        urly      (url-like url)]
    (is (= "/"     (.getPath url)))
    (is (= "/"     (.getPath urly)))
    (is (nil? (.getQuery url)))
    (is (nil? (.getQuery urly)))
    (is (nil?  (.getRef url)))
    (is (nil?  (.getRef urly)))
    (is (nil?  (.getFragment urly)))))

(deftest test-instantiating-url-like-from-a-string-url-that-has-extra-protocol-prefixes
  (let [url       "HTTPS://http://blahblah.smackernews.org/"
        urly      (url-like url)]
    (is (= "/"     (.getPath urly)))
    (is (= "blahblah.smackernews.org" (.getHost urly)))
    (is (= "http" (.getScheme urly)))
    (is (nil? (.getQuery urly)))
    (is (nil?  (.getRef urly)))
    (is (nil?  (.getFragment urly)))))

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


(deftest test-without-last-path-segment
  (is (= (path-of (.withoutLastPathSegment (url-like "http://giove.local/a/b/css")))
         (path-of (url-like "http://giove.local/a/b/")))))

(deftest test-without-query-string-and-fragment1
  (let [urly (.withoutQueryStringAndFragment (url-like "http://giove.local/a/b/css?query=string#fragment"))]
    (is (nil? (query-of    urly)))
    (is (nil? (fragment-of urly)))))

(deftest test-without-query-string-and-fragment2
  (is (= (without-query-string-and-fragment "http://giove.local/a/b/css?query=string#fragment") "http://giove.local/a/b/css"))
  (is (= (without-query-string-and-fragment "http://https://giove.local/a/b/css?query=string#fragment") "https://giove.local/a/b/css")))

(deftest test-without-query-string-and-fragment3
  (is (= (without-query-string-and-fragment (URI. "http://giove.local/a/b/css?query=string#fragment")) (URI. "http://giove.local/a/b/css"))))

(deftest test-without-query-string-and-fragment4
  (is (= (without-query-string-and-fragment (URL. "http://giove.local/a/b/css?query=string#fragment")) (URL. "http://giove.local/a/b/css"))))

(deftest test-to-uri
  (are [original expected] (is (= (.toURI (url-like original)) (URI. expected)))
       "http://www.giove.local"          "http://www.giove.local/"
       "http://www.giove.local/a/"       "http://www.giove.local/a/"
       "http://www.giove.local/a/1.html" "http://www.giove.local/a/1.html"
       "http://www.giove.local/a/1.html?query=string"          "http://www.giove.local/a/1.html?query=string"
       "http://www.giove.local/a/1.html?query=string#fragment" "http://www.giove.local/a/1.html?query=string#fragment")
  (is (= (-> (url-like "http://giove.local/a/b/css") .withoutLastPathSegment .toURI) (URI. "http://giove.local/a/b/"))))

(deftest test-to-url
  (are [original expected] (is (= (.toURL (url-like original)) (URL. expected)))
       "http://www.giove.local"          "http://www.giove.local/"
       "http://www.giove.local/a/"       "http://www.giove.local/a/"
       "http://www.giove.local/a/1.html" "http://www.giove.local/a/1.html"
       "http://www.giove.local/a/1.html?query=string"          "http://www.giove.local/a/1.html?query=string"
       "http://www.giove.local/a/1.html?query=string#fragment" "http://www.giove.local/a/1.html?query=string#fragment")
  (is (= (-> (url-like "http://giove.local/a/b/css") .withoutLastPathSegment .toURL) (URL. "http://giove.local/a/b/"))))

(deftest test-to-string
  (are [original expected] (is (= (str (url-like original)) expected))
       "http://www.giove.local"          "http://www.giove.local/"
       "http://www.giove.local/a/"       "http://www.giove.local/a/"))


(deftest test-tld-of
  (are [domain tld] (is (= (tld-of domain) tld))
       "giove.local"                "local"
       "giove"                      nil
       "clojure.org"                "org"
       "www.clojure.org"            "org"
       "www7.east.us.megacloud.com" "com"
       (URI. "http://clojure.org")  "org"
       (URL. "http://clojure.org")  "org"
       "juno.co.uk"                 "co.uk"
       (URI. "http://juno.co.uk")   "co.uk"))
