organization := "ahlers.michael"

name := "playful"

version := "0.0.0"

scalaVersion := "2.11.7"

scalacOptions ++=
  "-feature" ::
    "-target:jvm-1.8" ::
    Nil

scalacOptions in Test ++=
  Nil

/** Compile and runtime dependencies. */
libraryDependencies ++=
  "ch.qos.logback" % "logback-classic" % "1.1.3" ::
    "com.typesafe.play" %% "play-iteratees" % "2.4.6" ::
    "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0" ::
    Nil

/** Test dependencies. */
libraryDependencies ++=
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % "test" ::
    "org.scalactic" %% "scalactic" % "2.2.6" % "test" ::
    "org.scalatest" %% "scalatest" % "2.2.6" % "test" ::
    Nil

scalacOptions in(Compile, doc) ++=
  "-author" ::
    "-groups" ::
    "-implicits" ::
    Nil

/** See http://scala-sbt.org/0.13/docs/Howto-Scaladoc.html for details. */
autoAPIMappings := true

/** See http://stackoverflow.com/a/20919304/700420 for details. */
apiMappings ++= {
  val cp: Seq[Attributed[File]] = (fullClasspath in Compile).value

  def findManagedDependency(organization: String, name: String): File =
    (for {
      entry <- cp
      module <- entry.get(moduleID.key)
      if module.organization == organization
      if module.name.startsWith(name)
    } yield entry.data).head

  Map(
    findManagedDependency("com.typesafe.play", "play-iteratee") -> url("http://playframework.com/documentation/2.4.x/api/scala/")
  )
}

lazy val playful =
  (project in file("."))
    .enablePlugins(BuildInfoPlugin)
    .settings(
      buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
      buildInfoPackage := "ahlers.michael.playful"
    )
