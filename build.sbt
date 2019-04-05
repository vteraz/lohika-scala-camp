
name := "lohika-scala-camp"

version := "0.1"

scalaVersion := "2.12.8"

resolvers ++= Seq(
  "bintray-sbt-plugin-releases" at "http://dl.bintray.com/content/sbt/sbt-plugin-releases",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Artima Maven Repository" at "http://repo.artima.com/releases"
)

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

enablePlugins(JavaAppPackaging)