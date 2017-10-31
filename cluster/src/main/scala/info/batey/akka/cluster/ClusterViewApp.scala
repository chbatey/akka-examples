package info.batey.akka.cluster

import akka.actor.ActorSystem
import akka.cluster.Cluster

object ClusterViewApp extends App {

  val system = ActorSystem()

  val cluster = Cluster(system)

}
