import Dependencies._
import com.typesafe.sbt.SbtMultiJvm.multiJvmSettings
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.3",
      version := "0.1.0-SNAPSHOT"
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
    libraryDependencies ++= clusterTestDeps
  )

lazy val clusterSharding = (project in file("cluster-sharding"))
  .settings(
    libraryDependencies ++= clusterDeps,
    libraryDependencies ++= clusterTestDeps,
    libraryDependencies ++= commonDeps,
    libraryDependencies ++= commonTestDeps
  )

lazy val clusterSingleton = (project in file("cluster-singleton"))
  .settings(
    libraryDependencies ++= clusterDeps,
    libraryDependencies ++= clusterTestDeps,
    libraryDependencies ++= commonDeps,
    libraryDependencies ++= commonTestDeps
  )

lazy val multiJvm = (project in file("multi-jvm"))
  .settings(
    libraryDependencies += scalaTest
  )
  .configs(MultiJvm)
  .settings(multiJvmSettings)

lazy val akkaMultiJvm = (project in file("akka-multi-jvm"))
  .settings(
    libraryDependencies ++= multiNodeAkka,
    multiJvmSettings
  )
  .configs(MultiJvm)


lazy val clusterTyped = (project in file("typed-cluster"))
  .settings(
    libraryDependencies ++= typedDeps,
    libraryDependencies ++= clusterDeps,
    libraryDependencies ++= typedTestDeps,
    libraryDependencies ++= clusterTestDeps,
    libraryDependencies ++= commonDeps
  )

lazy val basic = (project in file("basic"))
  .settings(
    libraryDependencies ++= commonDeps,
    libraryDependencies ++= commonTestDeps
  )

lazy val kafka = (project in file("kafka"))
  .settings(
    libraryDependencies ++= kafkaDeps,
    libraryDependencies ++= commonDeps,
    libraryDependencies ++= commonTestDeps
  )

lazy val streams = (project in file("streams"))
  .settings(
    libraryDependencies ++= streamsDeps,
    libraryDependencies ++= streamsTestDeps,
    libraryDependencies ++= commonDeps,
    libraryDependencies ++= commonTestDeps
  )

lazy val persistence = (project in file("persistence"))
  .settings(
    libraryDependencies ++= persistenceDeps,
    libraryDependencies ++= commonDeps,
    libraryDependencies ++= commonTestDeps
  )

lazy val typedPersistence = (project in file("typed-persistence"))
  .settings(
    libraryDependencies ++= typedPersistenceDeps,
    libraryDependencies ++= commonDeps,
    libraryDependencies ++= commonTestDeps
  )

lazy val akkaHttp = (project in file("http"))
  .settings(
    libraryDependencies ++= httpDeps,
    libraryDependencies ++= httpTestDeps,
    libraryDependencies ++= commonDeps,
    libraryDependencies ++= commonTestDeps
  )

lazy val slickAlpakka = (project in file("slick-alpakka"))
  .settings(
    libraryDependencies ++= slickAlpakkaDeps,
    libraryDependencies ++= commonDeps,
    libraryDependencies ++= commonTestDeps
  )

