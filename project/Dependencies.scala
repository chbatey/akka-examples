import sbt._

object Dependencies {
  val akkaVersion = "2.5.4"
  val akkaTyped = "com.typesafe.akka" %% "akka-typed" % akkaVersion
  val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.0.10"
  val akkaRE = "com.lightbend.akka" %% "akka-persistence-multi-dc" % "1.1-M3+2-962db618+20170927-1217"
  val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % "2.5.4"

  val akkaTypedTestkit = "com.typesafe.akka" %% "akka-typed-testkit" % akkaVersion
  val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3"

  val typedDeps = Seq(akkaTyped, akkaTyped)
  val typedTestDeps = Seq(akkaTypedTestkit, scalaTest).map(_ % "test")

  val reDeps = Seq(akkaRE)
  val reTestDeps = Seq(akkaRE).map(_ % "test")

  val clusterDeps = Seq(akkaCluster)
  val clusterTestDeps = Seq(scalaTest).map(_ % "test")
}
