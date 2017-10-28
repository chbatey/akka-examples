import sbt._

object Dependencies {
  val akkaVersion = "2.5.6"
  val akkaHttpVersion = "10.0.10"

  val akkaActors = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaPersistence = "com.typesafe.akka" %% "akka-persistence" % akkaVersion
  val akkaPersistentQuery = "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion
  val akkaPersistenceCassandra = "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.59-SNAPSHOT"
  val akkaRemoting = "com.typesafe.akka" %% "akka-remote" % akkaVersion
  val akkaTyped = "com.typesafe.akka" %% "akka-typed" % akkaVersion
  val akkaStreams = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  val akkaRE = "com.lightbend.akka" %% "akka-persistence-multi-dc" % "1.1-M4"
  val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % akkaVersion
  val akkaClusterSharding = "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion
  val clusterTools = "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion
  val akkaHttpManagement ="com.lightbend.akka" %% "akka-management-cluster-http" % "0.5"

  val slickAlpakka =  "com.lightbend.akka" %% "akka-stream-alpakka-slick" % "0.14"

  val reactiveKafka = "com.typesafe.akka" %% "akka-stream-kafka" % "0.18-SNAPSHOT"
  val slf4jSimple = "org.slf4j" % "slf4j-simple" % "1.7.25"
  val leveldb = "org.iq80.leveldb" % "leveldb" % "0.7"
  val leveldbJni = "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"

  // Commercial
  val diagnostics = "com.lightbend.akka" %% "akka-diagnostics" % "1.1-M4"

  val akkaStreamsTestkit = "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion
  val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion
  val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion
  val akkaTypedTestkit = "com.typesafe.akka" %% "akka-typed-testkit" % akkaVersion
  val akkaMultiNodeTestKit = "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion
  val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3"

  val commonDeps = Seq(akkaActors, akkaRemoting, diagnostics)
  val commonTestDeps = Seq(akkaTestkit, akkaStreamsTestkit)

  val typedDeps = Seq(akkaTyped, akkaTyped)
  val typedTestDeps = Seq(akkaTypedTestkit, scalaTest).map(_ % Test)

  val reDeps = Seq(akkaRE)
  val reTestDeps = Seq(akkaRE).map(_ % "test")

  val clusterDeps = Seq(akkaCluster, akkaHttp, akkaClusterSharding, akkaHttpManagement, clusterTools)
  val clusterTestDeps = Seq(scalaTest, akkaHttpTestkit).map(_ % Test)

  val multiNodeAkka = Seq(akkaMultiNodeTestKit, scalaTest)

  val kafkaDeps = Seq(reactiveKafka, slf4jSimple, akkaStreams)

  val streamsDeps = Seq(akkaStreams)
  val streamsTestDeps = Seq(scalaTest, akkaStreamsTestkit, akkaTestkit).map(_ % "test")

  val typedPersistenceDeps = Seq(akkaPersistence, akkaPersistenceCassandra, akkaTyped, akkaPersistentQuery)
  val persistenceDeps = Seq(akkaPersistence, akkaPersistenceCassandra, leveldb, leveldbJni, akkaPersistentQuery)

  val httpDeps = Seq(akkaHttp)
  val httpTestDeps = Seq(akkaHttpTestkit)

  val slickAlpakkaDeps = Seq(slickAlpakka)
}
