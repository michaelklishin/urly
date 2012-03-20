# What is Urly

Urly is a tiny Clojure library that unifies parsing of URIs, URLs and URL-like values like relative href values
in real-world HTML.

## Why Urly Was Necessary

java.net.URI and java.net.URL in general do a great job of parsing valid (per RFCs) URIs and URLs. However, when
working with real world HTML markup, it is common to come across href attribute values that are not valid URIs or URLs but
are recognized and accepted by Web browsers. Normalization and resolution of such values cannot use java.net.URI or
java.net.URL because both will throw illegal format exceptions.

Urly tries to make this less painful.

## Supported Clojure versions

Urly is built from the ground up for Clojure 1.3 and up.



## Usage

### Installation

With Leiningen

    [clojurewerkz/urly "1.0.0-beta5"]

or, if you are comfortable with using snapshots,

    [clojurewerkz/urly "1.0.0-SNAPSHOT"]

New snapshots are [published to clojars.org](https://clojars.org/clojurewerkz/urly) every day (if there are any changes).


### clojurewerkz.urly.UrlLike

The central concept in Urly is the UrlLike class. It unifies java.net.URI and java.net.URL as much as practical
and also supports relative href attributes values like "/search?q=Clojure". UrlLike instances are immutable and
perform normalizations that are safe (for example, uses default pathname of "/" and lowercases protocol and hostnames but not pathnames).

TBD


## Urly is a Work In Progress

Urly is a Work In Progress. Please see our test suite for code examples.


## Continuous Integration

[![Continuous Integration status](https://secure.travis-ci.org/michaelklishin/urly.png)](http://travis-ci.org/michaelklishin/urly)


CI is hosted by [travis-ci.org](http://travis-ci.org)


## Development

Urly uses [Leiningen 2](https://github.com/technomancy/leiningen/blob/master/doc/TUTORIAL.md). Make
sure you have it installed and then run tests against Clojure 1.3.0 and 1.4.0[-beta4] using

    lein2 with-profile dev javac
    lein2 all test

Then create a branch and make your changes on it. Once you are done with your changes and all
tests pass, submit a pull request on Github.



## License

Copyright (C) 2011-2012 Michael S. Klishin

Distributed under the Eclipse Public License, the same as Clojure.
