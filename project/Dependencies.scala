import sbt._

object Dependencies {
  val akkaVersion = "2.5-SNAPSHOT"
  val akkaActors = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaPersistence = "com.typesafe.akka" %% "akka-persistence" % akkaVersion
  val akkaPersistentQuery = "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion
  val akkaPersistenceCassandra = "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.59-SNAPSHOT"
  val akkaRemoting = "com.typesafe.akka" %% "akka-remote" % akkaVersion
  val akkaTyped = "com.typesafe.akka" %% "akka-typed" % akkaVersion
  val akkaStreams = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.0.10"
  val akkaRE = "com.lightbend.akka" %% "akka-persistence-multi-dc" % "1.1-M4"
  val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % akkaVersion
  val reactiveKafka = "com.typesafe.akka" %% "akka-stream-kafka" % "0.18-SNAPSHOT"
  val slf4jSimple = "org.slf4j" % "slf4j-simple" % "1.7.25"
  val leveldb = "org.iq80.leveldb" % "leveldb" % "0.7"
  val leveldbJni = "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"

  // Commercial
  val diagnostics = "com.lightbend.akka" %% "akka-diagnostics" % "1.1-M4"

  val akkaStreamsTestkit = "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion
  val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion
  val akkaTypedTestkit = "com.typesafe.akka" %% "akka-typed-testkit" % akkaVersion
  val akkaMultiNodeTestKit = "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion
  val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3"

  val commonDeps = Seq(akkaActors, akkaRemoting, diagnostics)
  val commonTestDeps = Seq(akkaTestkit, akkaStreamsTestkit)

  val typedDeps = Seq(akkaTyped, akkaTyped)
  val typedTestDeps = Seq(akkaTypedTestkit, scalaTest).map(_ % "test")

  val reDeps = Seq(akkaRE)
  val reTestDeps = Seq(akkaRE).map(_ % "test")

  val clusterDeps = Seq(akkaCluster)
  val clusterTestDeps = Seq(scalaTest).map(_ % "test")

  val multiNodeAkka = Seq(akkaMultiNodeTestKit, scalaTest)

  val kafkaDeps = Seq(reactiveKafka, slf4jSimple, akkaStreams)

  val streamsDeps = Seq(akkaStreams)
  val streamsTestDeps = Seq(scalaTest, akkaStreamsTestkit, akkaTestkit).map(_ % "test")

  val typedPersistenceDeps = Seq(akkaPersistence, akkaPersistenceCassandra, akkaTyped, akkaPersistentQuery)
  val persistenceDeps = Seq(akkaPersistence, akkaPersistenceCassandra, leveldb, leveldbJni, akkaPersistentQuery)
}
