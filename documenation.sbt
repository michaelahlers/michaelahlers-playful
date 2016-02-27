scalacOptions in(Compile, doc) ++=
  "-author" ::
    "-groups" ::
    "-implicits" ::
    Nil

/** See http://scala-sbt.org/0.13/docs/Howto-Scaladoc.html for details. */
autoAPIMappings := true

/** See http://stackoverflow.com/a/20919304/700420 for details. */
apiMappings ++= {
  def findDependency(organization: String, name: String): Seq[File] =
    for {
      entry: Attributed[File] <- (fullClasspath in Compile).value
      module: ModuleID <- entry.get(moduleID.key)
      if module.organization == organization
      if module.name.startsWith(name)
    } yield entry.data

  val mappings: Seq[(File, URL)] =
  //findDependency("org.scala-lang", "scala-library").map(_ -> url(s"http://scala-lang.org/api/${scalaVersion.value}/")) ++
    findDependency("com.typesafe.play", "play-iteratee").map(_ -> url("http://playframework.com/documentation/2.4.x/api/scala/")) ++
      findDependency("com.typesafe.play", "play-json").map(_ -> url("http://playframework.com/documentation/2.4.x/api/scala/"))

  mappings.toMap
}
