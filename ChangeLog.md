## Changes between Urly 1.0.0-rc3 and 1.0.0-rc4

No changes yet.


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
