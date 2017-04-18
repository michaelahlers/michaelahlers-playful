# Playful [![Build Status](https://travis-ci.org/michaelahlers/michaelahlers-playful.svg?branch=master)](https://travis-ci.org/michaelahlers/michaelahlers-playful) [![Discuss](https://badges.gitter.im/michaelahlers/michaelahlers-playful.svg)](https://gitter.im/michaelahlers/michaelahlers-playful?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

[![Join the chat at https://gitter.im/michaelahlers/michaelahlers-playful](https://badges.gitter.im/michaelahlers/michaelahlers-playful.svg)](https://gitter.im/michaelahlers/michaelahlers-playful?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Odds and ends for developers using [Lightbend's](http://lightbend.com) [Play Framework](http://playframework.com).

## About

_Playful_ offers features its author wishes were part of APIs included with Play Framework. It's starting small and will expand incrementally, focusing first on augmenting the [iteratee](http://playframework.com/documentation/2.4.x/Iteratees) and [JSON](http://playframework.com/documentation/2.4.x/ScalaJson) libraries.

Everything you'll find here will be tested and considered production-ready (despite the low version number). What's not clear is whether this library will receive maintenance in the long term. (It may turn out to be redundant as Lightbend expands their offerings and adds features.) In the meantime, interested users might consider simply copying and pasting source code directly into their own projects. (Although updates will be published to public repositories.)

## Installation

Release binaries are by [Sonatype](http://oss.sonatype.org). If not already present, add these resolvers to your SBT project configuration:

```scala
resolvers += "Sonatype OSS (releases)" at "https://oss.sonatype.org/content/repositories/releases"
resolvers += "Sonatype OSS (snapshots)" at "https://oss.sonatype.org/content/repositories/snapshots"
```

And add Playful to your dependencies:

```scala
libraryDependencies += "com.github.michaelahlers" %% "playful" % "0.1.1-SNAPSHOT"
```

## Development

All contributions&mdash;advice, criticism, and assistance&mdash;are welcomed and strongly encouraged.

### Setup

Apart from requiring Oracle's Java SE Development Kit 8 (JDK 8), this project is “batteries included.” Simply start [Lightbend Activator](http://lightbend.com/activator) from the project's root to get started (installing [SBT](http://scala-sbt.org/0.13/tutorial/Setup.html) is optional). It's recommended to use one of the following resources to obtain Java.

- [Web Upd8 PPA](http://webupd8.org/2012/09/install-oracle-java-8-in-ubuntu-via-ppa.html)
  - Best packages for Debian-based distributions (_e.g._, Debian, Ubuntu, Mint)
- [Oracle Java SE Development Kit 8 Downloads](http://oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
  - Packages for RPM-based distributions (_e.g._, RHEL, Fedora, CentOS).
  - Installer packages for OS X.
  - Installer packages for Windows.

### Tests

From an SBT shell, the unit test suite is run with:

```
test
```

Specific tests with:

```
test-only ahlers.michael.playful.iteratee.EnumerateeOpsSpec
```

Test code coverage reports may be generated with:

```
;coverage;clean;test;coverageReport
```

Visit `target/scala-2.11/scoverage-report/index.html` (from the project's root) in your browser to see results.

### Benchmarks

There's currently a beginner's attempt at writing meaningful performance tests using [ScalaMeter](http://scalameter.github.io) in `src/bench`. Benchmarks can be run with:

```
bench:test
```

Visit `target/benchmarks/report/index.html` (from the project's root) in your browser to see results.

### Documentation

API documentation is available with copious developer notes is included. From an SBT shell, generate it with:

```
doc
```

Visit `target/scala-2.11/api/index.html` (from the project's root) in your browser to see results.

## Resources

### [Play Iteratees](http://playframework.com/documentation/2.4.x/Iteratees)

A reactive API for handling high volume streams.

### [Play JSON](http://playframework.com/documentation/2.4.x/ScalaJson)

An outstanding API for working with JSON in Scala.

### [ScalaMeter](http://scalameter.github.io/)

A benchmarking framework used to test performance of this library.
