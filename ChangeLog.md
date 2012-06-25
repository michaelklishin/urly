## Changes between Urly 2.0.0-alpha3 and 2.0.0-alpha4

No changes yet.

## Changes between Urly 2.0.0-alpha2 and 2.0.0-alpha3

Added nil bubbling for PartsAccessors, which allows client to decide what to do with nil. That's
especially useful when Url is invalid, therefore urly is not able to parse it.



## Changes between Urly 2.0.0-alpha1 and 2.0.0-alpha2

### New functions

`clojurewerkz.urly.core/count-segments` can be used to calculate number of segments in the path:

``` clojure
(count-segments "http://apple.com") ;; => 0
(count-segments "http://apple.com/") ;; => 0
(count-segments"/") ;; => 0
(count-segments "http://apple.com/iphone") ;; => 1
(count-segments "/iphone") ;; => 1
(count-segments "http://apple.com/iphone/") ;; => 1
(count-segments "/iphone/") ;; => 1
(count-segments "http://store.apple.com/us/browse/home/shop_mac/family/mac_pro") ;; => 6
(count-segments (url-like "http://store.apple.com/us/browse/home/shop_mac/family/macbook_pro")) ;; => 6
(count-segments (java.net.URI. "http://store.apple.com/us/browse/home/shop_mac/family/macbook_pro")) ;; => 6
(count-segments (java.net.URL. "http://store.apple.com/us/browse/home/shop_mac/family/macbook_pro")) ;; => 6
(count-segments (url-like "http://www.amazon.com/Clojure-Programming-ebook/dp/B007Q4T040/ref=tmm_kin_title_0?ie=UTF8&m=A2JEPUQV26074G&qid=1337080272&sr=8-1")) ;; => 4
```



## Changes between Urly 1.0.0 and 2.0.0-alpha1

### UrlLike#withWww

`UrlLike#withWww` does the opposite of `UrlLike#withoutWww`



## Changes between Urly 1.0.0-rc6 and 1.0.0

### Java API improvements

`clojurewerkz.urly.UrlLike` now has several additional methods:

 * `UrlLike#withoutQuery`
 * `UrlLike#withoutFragment`


### New functions

`clojurewerkz.urly.core/without-query-string` and `clojurewerkz.urly.core/without-fragment` were added, similar in purpose with
`clojurewerkz.urly.core/without-query-string-and-fragment`.

`clojurewerkz.urly.core/absolutize` and `clojurewerkz.urly.core/normalize-url` were extracted from [Crawlista](https://github.com/michaelklishin/crawlista)


## Changes between Urly 1.0.0-rc5 and 1.0.0-rc6

### UrlLike#getAuthority

Authority (section 3.2 in [RFC 3986]()) is now recalculated when hostname, port or user info are mutated.


### UrlLike#withoutWww

`clojurewerkz.urly.UrlLike#withoutWww` is a Java API addition that eliminates `www.`, `www.2`, `www.11` and similar
prefixes from the hostname.



## Changes between Urly 1.0.0-rc4 and 1.0.0-rc5

### Eliminated reflection warnings

Reflection warnings for `clojurewerkz.urly.core/resolve` were eliminated.


### Thousands new test cases

Urly test suite now has about 3K more new test cases for the parser.



## Changes between Urly 1.0.0-rc3 and 1.0.0-rc4

### clojurewerkz.urly.core/resolve now supports UrlLike

`clojurewerkz.urly.core/resolve` now supports UrlLike instances


## Changes between Urly 1.0.0-rc2 and 1.0.0-rc3

### Query mutation and encoding improvements

New functions allow for query string mutation via function (much like Clojure atoms) and URL-encoding
(note that Clojure API [uses UTF-8 encoding](http://docs.oracle.com/javase/7/docs/api/java/net/URLEncoder.html#encode%28java.lang.String%29)):

 * `clojurewerkz.urly.core/mutate-query-with`
 * `clojurewerkz.urly.core/encode-query`
 * `clojurewerkz.urly.core/encode-fragment`


### Java API improvements

`clojurewerkz.urly.UrlLike` now has several additional methods:

 * `UrlLike#hasQuery`
 * `UrlLike#hasFragment`
 * `UrlLike#hasNonDefaultPort`
 * `UrlLike#encodeQuery`


## Changes between Urly 1.0.0-rc1 and 1.0.0-rc2

Extra protocol elimination (for example `http://https://github.com` => `https://github.com`) is now
case-insensitive (will recognize `http` as well as `HTTp`).


## Changes between Urly 1.0.0-beta9 and 1.0.0-rc1

`clojurewerkz.urly.core/url-like` now uses fully-qualified class name for its return type hint.
This is a usability improvement: this way namespaces that use it won't have to import it.


## Changes between Urly 1.0.0-beta8 and 1.0.0-beta9

### UrlLike/homepageOf now uses the same default port value as java.net.URI

UrlLike/homepageOf used to use default port value of 80. Starting with 1.0.0-beta9, it uses the same
default prot as java.net.URI (-1).


### url-like now treats inputs that are valid Internet domain names specially

`urly.core/url-like` now recognizes cases like "google.com" or "amazon.co.uk" (Internet domain names) and
uses the input to assign UrlLike instance **host** (java.net.URI assigns **path**), which is what you typically
want.

In cases where you want classic behavior, use `UrlLike/from` with a URI instance:

``` clojure
(UrlLike/from (java.net.URI. "amazon.de"))
```


## Changes between Urly 1.0.0-beta7 and 1.0.0-beta8

`urly.core/url-like` how handles cases when URL parts (typically query string) have unescaped spaces in them


## Changes between Urly 1.0.0-beta6 and 1.0.0-beta7

`urly.core/protocol-of` how correctly returns nil for inputs that have no protocol


## Changes between Urly 1.0.0-beta5 and 1.0.0-beta6

`urly.core/absolute?` and `urly.core/relative?` now work for `urly.UrlLike` instances



## Changes between Urly 1.0.0-beta4 and 1.0.0-beta5

Added `urly.core/eliminate-extra-protocol-prefixes` that turns URLs like `http://https://example.com` into `https://example.com`


## Changes between Urly 1.0.0-beta3 and 1.0.0-beta4

### JDK 6 Compatibility

Urly now correctly uses JDK 6 as Java compilation target.



## Changes between Urly 1.0.0-beta2 and 1.0.0-beta3

### Leiningen 2

Urly now uses [Leiningen 2](https://github.com/technomancy/leiningen/wiki/Upgrading).
