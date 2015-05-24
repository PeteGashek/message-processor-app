name := """message-processor-app"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-testkit" % "2.3.4" % "test",
  "com.typesafe.play.plugins" %% "play-plugins-redis" % "2.3.1"
)

resolvers += "org.sedis" at "http://pk11-scratch.googlecode.com/svn/trunk"