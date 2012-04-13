(ns clojurewerkz.urly.test.third_party-test
  (:refer-clojure :exclude [resolve])
  (:use [clojurewerkz.urly.core]
        [clojure.test])
  (:import [java.net URI URL]
           [clojurewerkz.urly UrlLike]))


;;
;; Test suite compiled from multiple 3rd-party test suites,
;; RFC examples, et cetera. MK.
;;

(deftest test-example-set1
  (are [input m] (is (= (as-map (url-like input)) m))
       "//some_path"                       { :protocol nil,    :host nil,               :port -1, :user-info nil,       :path "/", :query nil, :fragment nil, :tld nil }
       "HTTP://www.example.com/"           { :protocol "http", :host "www.example.com", :port -1, :user-info nil,       :path "/", :query nil, :fragment nil, :tld "com" }
       "HtTP://www.EXAMPLE.com"            { :protocol "http", :host "www.example.com", :port -1, :user-info nil,       :path "/", :query nil, :fragment nil, :tld "com" }
       "http://user:pw@www.ExAmPlE.com/"   { :protocol "http", :host "www.example.com", :port -1, :user-info "user:pw", :path "/", :query nil, :fragment nil, :tld "com" }
       "http://USER:PW@www.example.com/"   { :protocol "http", :host "www.example.com", :port -1, :user-info "USER:PW", :path "/", :query nil, :fragment nil, :tld "com" }
       "http://user@www.example.com/"      { :protocol "http", :host "www.example.com", :port -1, :user-info "user",    :path "/", :query nil, :fragment nil, :tld "com" }
       "http://user%3Apw@www.ExAmPlE.com/" { :protocol "http", :host "www.example.com", :port -1, :user-info "user:pw", :path "/", :query nil, :fragment nil, :tld "com" }
       "HTTP://X.COM/Y"                    { :protocol "http", :host "x.com",           :port -1, :user-info nil,       :path "/Y", :query nil, :fragment nil, :tld "com" }
       "/foo/bar?baz=quux#frag"            { :protocol nil,    :host nil,               :port -1, :user-info nil,       :path "/foo/bar", :query "baz=quux", :fragment "frag", :tld nil }))
