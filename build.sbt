
name := "lohika-scala-camp"

version := "0.1"

scalaVersion := "2.12.8"

resolvers ++= Seq(
  "bintray-sbt-plugin-releases" at "http://dl.bintray.com/content/sbt/sbt-plugin-releases",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
)

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.4.1"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

enablePlugins(JavaAppPackaging)