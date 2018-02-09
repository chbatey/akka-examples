import Dependencies._
import com.typesafe.sbt.SbtMultiJvm.multiJvmSettings
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "info.batey",
      scalaVersion := "2.12.4",
      version := "0.1.0-SNAPSHOT"
    )),
    name := "akka-examples"
  )

lazy val analysis = (project in file("analysis"))
  .settings(
    libraryDependencies ++= analysisDeps,
    libraryDependencies ++= clusterDeps,
    libraryDependencies ++= clusterTestDeps,
    libraryDependencies ++= commonDeps,
    libraryDependencies ++= commonTestDeps,
    libraryDependencies += Cinnamon.library.cinnamonCHMetrics,
    libraryDependencies += Cinnamon.library.cinnamonAkka,
    libraryDependencies += Cinnamon.library.cinnamonCHMetricsHttpReporter,
    cinnamon in run := true
  ).enablePlugins(Cinnamon)

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
    libraryDependencies ++= commonDeps,
    libraryDependencies ++= commonTestDeps
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

lazy val multiDc = (project in file("cluster-multidc"))
  .settings(
    libraryDependencies ++= clusterDeps,
    libraryDependencies ++= clusterTestDeps,
    libraryDependencies ++= commonDeps,
    libraryDependencies ++= commonTestDeps
  )

lazy val multiJvm = (project in file("multi-jvm"))
  .settings(
    libraryDependencies += scalaTest,
    libraryDependencies ++= clusterDeps
  )
  .configs(MultiJvm)
  .settings(multiJvmSettings)

lazy val akkaMultiJvm = (project in file("akka-multi-jvm"))
  .settings(
    libraryDependencies ++= multiNodeAkka,
    libraryDependencies ++= clusterDeps,
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
    libraryDependencies ++= commonTestDeps,
    libraryDependencies += Cinnamon.library.cinnamonCHMetrics,
    libraryDependencies += Cinnamon.library.cinnamonAkka,
    cinnamon in run := true,
    cinnamon in test := true,
    cinnamonLogLevel := "INFO"
  )
  .enablePlugins(Cinnamon)

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
    libraryDependencies ++= commonTestDeps)

lazy val streamsFromScratch = (project in file("streams-from-scratch"))
  .settings(
    libraryDependencies ++= streamsFromScratchDeps,
    libraryDependencies ++= streamsTestDeps,
    libraryDependencies ++= commonTestDeps)

lazy val persistence = (project in file("persistence"))
  .settings(
    libraryDependencies ++= persistenceDeps,
    libraryDependencies ++= commonDeps,
    libraryDependencies ++= commonTestDeps
  )

lazy val persistencePre80 = (project in file("persistence-pre80"))
    .settings(
      libraryDependencies ++= Seq(Dependencies.akkaPersistenceCassandraOld),
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


lazy val httpClient = (project in file("http-client"))
  .settings(
    libraryDependencies ++= httpClientDeps,
    libraryDependencies ++= commonDeps,
    libraryDependencies ++= commonTestDeps
  )

lazy val httpWebSockets = (project in file("http-websockets"))
  .settings(
    libraryDependencies ++= httpClientDeps,
    libraryDependencies ++= commonDeps,
    libraryDependencies ++= commonTestDeps
  )

lazy val aeron = (project in file("aeron"))
  .settings(
    libraryDependencies ++= aeronDeps
  )

lazy val persistenceMigration = (project in file("persistence-migration"))
  .settings(
    scalaVersion := "2.11.11",
    libraryDependencies ++= persistenceMigrationDeps
  )

lazy val management = (project in file("management"))
  .settings(
    libraryDependencies ++= commonDeps,
    libraryDependencies ++= clusterDeps,
    libraryDependencies ++= managementDeps,
    libraryDependencies ++= commonTestDeps
  )

lazy val streamingExample = (project in file("streaming-example"))
    .settings(
      libraryDependencies ++= commonDeps,
      libraryDependencies ++= streamingExampleDeps
    )

lazy val asyncCrud = (project in file("async-crud"))
    .settings(
      libraryDependencies ++= asyncCrudDeps
    )
