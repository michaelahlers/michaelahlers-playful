organization := "ahlers.michael"

name := "playful"

description := "Odds and ends for developers using Lightbend's Play Framework."

licenses += "MIT" -> url("http://opensource.org/licenses/MIT")

homepage := Some(url("http://github.com/michaelahlers/michaelahlers-playful"))

startYear := Some(2016)

developers :=
  Developer("michaelahlers", "Michael Ahlers", "michael@ahlers.co", url("http://michaelahlers.org")) ::
    Nil

scmInfo :=
  Some(ScmInfo(
    browseUrl = url("http://github.com/michaelahlers/michaelahlers-playful"),
    connection = "scm:git:https://github.com:michaelahlers/michaelahlers-playful.git",
    devConnection = Some("scm:git:git@github.com:michaelahlers/michaelahlers-playful.git")
  ))

scalaVersion := "2.11.7"

scalacOptions ++=
  "-feature" ::
    "-target:jvm-1.8" ::
    Nil

scalacOptions in Test ++=
  Nil

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

logBuffered := false

/** Parallel execution tests defeats the value of benchmark tests. */
parallelExecution in Benchmark := false

resolvers += "Sonatype (releases)" at "https://oss.sonatype.org/content/repositories/releases"

resolvers += "Sonatype (snapshots)" at "https://oss.sonatype.org/content/repositories/snapshots"

/** Compile and runtime dependencies. */
libraryDependencies ++=
  "ch.qos.logback" % "logback-classic" % "1.1.3" ::
    "com.typesafe.play" %% "play-iteratees" % "2.4.6" ::
    "com.typesafe.play" %% "play-json" % "2.4.6" ::
    "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0" ::
    Nil

/** Test dependencies. */
libraryDependencies ++=
  "com.storm-enroute" %% "scalameter" % "0.7" % "test" ::
    "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % "test" ::
    "org.scalactic" %% "scalactic" % "2.2.6" % "test" ::
    "org.scalatest" %% "scalatest" % "2.2.6" % "test" ::
    Nil

publishMavenStyle := true

/** Test artifacts are desired. */
publishArtifact in Test := true

publishTo := {
  val host = "https://oss.sonatype.org"
  if (isSnapshot.value) Some("snapshots" at s"$host/content/repositories/snapshots")
  else Some("releases" at s"$host/service/local/staging/deploy/maven2")
}

pomIncludeRepository := { _ => false }

lazy val Benchmark = config("bench") extend Test

lazy val playful =
  (project in file("."))
    .enablePlugins(BuildInfoPlugin)
    .settings(
      buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
      buildInfoPackage := "ahlers.michael.playful"
    )
    .configs(Benchmark)
    .settings(inConfig(Benchmark)(Defaults.testSettings): _*)
