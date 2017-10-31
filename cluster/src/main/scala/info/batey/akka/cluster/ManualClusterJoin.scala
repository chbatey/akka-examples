package info.batey.akka.cluster

import akka.actor.{ActorSystem, Address}
import akka.cluster.Cluster

import scala.io.StdIn

object NodeOne extends App {

  val system = ActorSystem("Demo")

  val cluster = Cluster(system)
  println(cluster.selfAddress)
  cluster.join(cluster.selfAddress)

  StdIn.readLine()
  println(cluster.state)
  println("Telling node to leave")
  cluster.leave(Address("akka.tcp", "Demo", "127.0.0.1", 2551))
  println("Node left")

}

object NodeTwo extends App {
  val system = ActorSystem("Demo")

  val cluster = Cluster(system)
  cluster.join(Address("akka.tcp", "Demo", "127.0.0.1", 2550))

  Thread.sleep(1000)

  println(cluster.state)
}
