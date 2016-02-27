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
    findManagedDependency("com.typesafe.play", "play-iteratee") -> url("http://playframework.com/documentation/2.4.x/api/scala/"),
    findManagedDependency("com.typesafe.play", "play-json") -> url("http://playframework.com/documentation/2.4.x/api/scala/")
  )
}
