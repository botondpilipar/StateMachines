name := "StateMachines"

version := "0.1"

scalaVersion := "2.13.4"

// ScalaTest integration
lazy val scalactic = "org.scalactic" %% "scalactic" % "3.2.2"
lazy val scalatest = "org.scalatest" %% "scalatest" % "3.2.2" % Test

libraryDependencies += scalactic
libraryDependencies += scalatest

// Test Configuration
logBuffered in Test := false


