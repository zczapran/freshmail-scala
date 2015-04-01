import sbt.Keys._

name := "freshmail-scala"

version := "0.1"

scalaVersion := "2.11.6"

resolvers ++= Seq(
  "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.2"
  Seq(
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-client" % sprayV,
    "io.spray" %% "spray-json" % "1.3.1",
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "org.scalaz" %% "scalaz-core" % "7.1.0",
    "com.typesafe" % "config" % "1.2.1" % "test",
    "org.scalatest" %% "scalatest" % "2.2.1" % "test"
  )
}
