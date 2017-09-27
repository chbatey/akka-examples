import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.3",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "akka-examples"
  )

lazy val typed = (project in file("typed"))
  .settings(
    libraryDependencies ++= typedDeps,
    libraryDependencies ++= typedTestDeps
  )

lazy val re = (project in file("replicated-entity"))
  .settings(
    libraryDependencies ++= reDeps,
    libraryDependencies ++= reTestDeps
  )

lazy val cluster = (project in file("cluster"))
    .settings(
      libraryDependencies ++= clusterDeps,
      libraryDependencies ++= clusterTestDeps,
    )

lazy val multiJvm = (project in file("multi-jvm"))
    .settings(
      libraryDependencies += scalaTest
    )
