
name := "lohika-scala-camp"

version := "0.1"

scalaVersion := "2.12.8"

resolvers ++= Seq(
    "bintray-sbt-plugin-releases" at "http://dl.bintray.com/content/sbt/sbt-plugin-releases",
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
)

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
libraryDependencies += "org.scalamock" %% "scalamock" % "4.1.0" % Test
libraryDependencies += "org.typelevel" %% "cats-core" % "1.6.0"

enablePlugins(JavaAppPackaging)