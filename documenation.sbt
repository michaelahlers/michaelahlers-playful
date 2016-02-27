import sbt._

scalacOptions in(Compile, doc) ++=
  "-author" ::
    "-groups" ::
    "-implicits" ::
    Nil

/** See http://scala-sbt.org/0.13/docs/Howto-Scaladoc.html for details. */
autoAPIMappings := true

/** See http://stackoverflow.com/a/35673212/700420 for details. */
apiMappings ++= {
  def mappingsFor(organization: String, names: List[String], location: String, revision: (String) => String = identity): Seq[(File, URL)] =
    for {
      entry: Attributed[File] <- (fullClasspath in Compile).value
      module: ModuleID <- entry.get(moduleID.key)
      if module.organization == organization
      if names.exists(module.name.startsWith)
    } yield entry.data -> url(location.replace("${revision}", revision(module.revision)))

  /** Several of these may not be used in this project as this is a reusable set of mappings. */
  val mappings: Seq[(File, URL)] =
    mappingsFor("com.squants", List("squants"), "http://oss.sonatype.org/service/local/repositories/releases/archive/com/squants/squants_2.11/${revision}/squants_2.11-${revision}-javadoc.jar/!") ++
      mappingsFor("com.typesafe.akka", List("akka-actor"), "http://doc.akka.io/api/akka/${revision}/") ++
      mappingsFor("com.typesafe.play", List("play-iteratees", "play-json"), "http://playframework.com/documentation/${revision}/api/scala/index.html", _.replaceAll("[\\d]$", "x")) ++
      mappingsFor("org.scala-lang", List("scala-library"), "http://scala-lang.org/api/${revision}/")

  mappings.toMap
}
