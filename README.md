# Playful [![Build Status](https://travis-ci.org/michaelahlers/michaelahlers-playful.svg?branch=master)](https://travis-ci.org/michaelahlers/michaelahlers-playful)

Odds and ends for developers using [Lightbend's](http://lightbend.com) [Play Framework](http://playframework.com).

## Development

### Setup

Apart from requiring Oracle's Java SE Development Kit 8 (JDK 8), this project is “batteries included.” Simply start [Typesafe Activator](http://typesafe.com/activator) from the project's root to get started (installing [SBT](http://scala-sbt.org/0.13/tutorial/Setup.html) is optional). It's recommended to use one of the following resources to obtain Java.

- [Web Upd8 PPA](http://webupd8.org/2012/09/install-oracle-java-8-in-ubuntu-via-ppa.html)
  - Best packages for Debian-based distributions (_e.g._, Debian, Ubuntu, Mint)
- [Oracle Java SE Development Kit 8 Downloads](http://oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
  - Packages for RPM-based distributions (_e.g._, RHEL, Fedora, CentOS).
  - Installer packages for OS X.
  - Installer packages for Windows.

### Testing

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

## Documentation

API documentation is available with copious developer notes is included. From an SBT shell, generate it with:

```
doc
```

Visit `target/scala-2.11/api/index.html` (from the project's root) in your browser to see results.

## Benchmarking

There's currently a beginner's attempt at writing meaningful performance tests using [ScalaMeter](http://scalameter.github.io) in `src/bench`. Benchmarks can be run with:

```
bench:test
```

Visit `target/benchmarks/report/index.html` (from the project's root) in your browser to see results.

## Resources

### [Play Iteratees](http://playframework.com/documentation/2.4.x/Iteratees)

The Iteratee, Enumerator, and Enumeratee are powerful concepts for handling high volume streams. Playful provides a few additional enhancements to the core API.

### [ScalaMeter](http://scalameter.github.io/)

A benchmarking framework used to test performance of this library.
