addSbtPlugin("com.typesafe.sbt" % "sbt-multi-jvm" % "0.3.8")

addSbtPlugin("com.lightbend.cinnamon" % "sbt-cinnamon" % "2.7.4")

credentials += Credentials(Path.userHome / ".lightbend" / "commercial.credentials")

resolvers += Resolver.url("lightbend-commercial",
  url("https://repo.lightbend.com/commercial-releases"))(Resolver.ivyStylePatterns)