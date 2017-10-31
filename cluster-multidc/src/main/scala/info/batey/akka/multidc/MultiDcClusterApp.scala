package info.batey.akka.multidc

import akka.actor.ActorSystem
import akka.cluster.Cluster

object MultiDcClusterApp extends App {

  val system = ActorSystem()
  val cluster = Cluster(system)

}
