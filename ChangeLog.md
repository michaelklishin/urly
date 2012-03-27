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
