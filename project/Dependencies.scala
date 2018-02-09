import sbt._

object Dependencies {
  val akkaVersion = "2.5.9"
  val akkaHttpVersion = "10.0.10"
  val aeronVersion = "1.6.0"
  val cassandraPluginVersion = "0.80"
  val akkaHttpManagementVersion = "0.6"
  val kafkaStreamsVersion = "0.19"
  val metricsVersion = "4.0.0"
  val lagomVersion = "1.4.0"

  val akkaActors = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaPersistence = "com.typesafe.akka" %% "akka-persistence" % akkaVersion
  val akkaPersistentQuery = "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion
  val akkaPersistenceCassandra = "com.typesafe.akka" %% "akka-persistence-cassandra" % cassandraPluginVersion
  val akkaRemoting = "com.typesafe.akka" %% "akka-remote" % akkaVersion
  val akkaTyped = "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion
  val akkaStreams = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  val akkaRE = "com.lightbend.akka" %% "akka-persistence-multi-dc" % "1.1-M4"
  val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % akkaVersion
  val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  val akkaClusterSharding = "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion
  val clusterTools = "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion
  val akkaHttpManagement ="com.lightbend.akka" %% "akka-management-cluster-http" % akkaHttpManagementVersion
  val cassandraDriver = "com.datastax.cassandra" % "cassandra-driver-core" % "3.4.0"
  val dropwizardMetics = "io.dropwizard.metrics" % "metrics-core" % metricsVersion
  val dropwizardMetricsJmx = "io.dropwizard.metrics" % "metrics-jmx" % metricsVersion
  val lagomPersistence = "com.lightbend.lagom" %% "lagom-scaladsl-persistence" % lagomVersion
  val lagomPersistenceCassandra = "com.lightbend.lagom" %% "lagom-scaladsl-persistence-cassandra" % lagomVersion
  val alpakkaCassandra = "com.lightbend.akka" %% "akka-stream-alpakka-cassandra" % "0.16"
  val asyncPostgres = "com.github.mauricio" %% "postgresql-async" % "0.2.21"

  val slickAlpakka =  "com.lightbend.akka" %% "akka-stream-alpakka-slick" % "0.14"
  val reactiveKafka = "com.typesafe.akka" %% "akka-stream-kafka" % kafkaStreamsVersion
  val slf4jSimple = "org.slf4j" % "slf4j-simple" % "1.7.25"
  val leveldb = "org.iq80.leveldb" % "leveldb" % "0.7"
  val leveldbJni = "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8"
  val logback = "ch.qos.logback" % "logback-classic" % "1.1.7"

  val aeronDriver = "io.aeron" % "aeron-driver" % aeronVersion
  val aeronClient = "io.aeron" % "aeron-client" % aeronVersion

  // Commercial
  val commercialVersion = "1.1.0"
  val diagnostics = "com.lightbend.akka" %% "akka-diagnostics" % commercialVersion
  val sbe = "com.lightbend.akka" %% "akka-split-brain-resolver" % commercialVersion

  val akkaStreamsTestkit = "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion
  val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % akkaVersion
  val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion
  val akkaTypedTestkit = "com.typesafe.akka" %% "akka-testkit-typed" % akkaVersion
  val akkaMultiNodeTestKit = "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion
  val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3"
  val wiremock = "com.github.tomakehurst" % "wiremock" % "2.12.1"

  val commonDeps = Seq(akkaActors, akkaRemoting, logback, akkaSlf4j)
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

  val httpClientDeps = Seq(akkaHttp)
  val httpClientTestDeps = Seq(wiremock, akkaHttpTestkit).map(_ % Test)

  val aeronDeps = Seq(aeronClient, aeronDriver)

  val akkaPersistenceCassandraOld = "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.59"
  val persistenceMigrationDeps = Seq(akkaPersistenceCassandraOld)

  val managementDeps = Seq(akkaHttpManagement)

  val analysisDeps = Seq(diagnostics, sbe)

  val lagom = Seq(lagomPersistence, lagomPersistenceCassandra)

  val streamingExampleDeps = Seq(reactiveKafka, cassandraDriver, dropwizardMetics, dropwizardMetricsJmx) ++ lagom ++ commonDeps

  val streamsFromScratchDeps = Seq(alpakkaCassandra, cassandraDriver, dropwizardMetics, dropwizardMetricsJmx) ++ kafkaDeps ++ streamsDeps ++ commonDeps

  val asyncCrudDeps = Seq(cassandraDriver, akkaHttp, akkaHttpManagement, slickAlpakka) ++ commonDeps
}
