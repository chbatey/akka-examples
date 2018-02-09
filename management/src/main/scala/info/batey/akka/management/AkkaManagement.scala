package info.batey.akka.management

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.cluster.http.management.ClusterHttpManagement
import scala.concurrent.ExecutionContext.Implicits.global

object AkkaManagement extends App {

  val system = ActorSystem("AkkaManagement")
  val cluster = Cluster(system)

  val httpClusterManagement = ClusterHttpManagement(cluster)
  httpClusterManagement.start()

  val bindingFuture = httpClusterManagement.stop()
  bindingFuture.onComplete { _ => println("It's stopped") }
}
