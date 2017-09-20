import sbt._

object Dependencies {
  val akkaVersion = "2.5.4"
  val akkaTyped = "com.typesafe.akka" %% "akka-typed" % akkaVersion
  val akktHttp = "com.typesafe.akka" %% "akka-http" % "10.0.10"

  val akkaTypedTestkit = "com.typesafe.akka" %% "akka-typed-testkit" % akkaVersion
  val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3"

  val deps = Seq(akkaTyped, akkaTyped)
  val testDeps = Seq(akkaTypedTestkit, scalaTest).map(_ % "test")
}
