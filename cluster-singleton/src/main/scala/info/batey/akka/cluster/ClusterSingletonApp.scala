package info.batey.akka.cluster

import akka.actor.{Actor, ActorSystem, PoisonPill, Props}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}

import scala.io.StdIn

object ClusterSingletonApp extends App {

  class SillySingleton() extends Actor {
    println("I am alive")
    def receive = {
      case msg =>
        println(msg)
    }
  }

  val system = ActorSystem("singleton")

  val ref = system.actorOf(
    ClusterSingletonManager.props(
      singletonProps = Props(classOf[SillySingleton]),
      terminationMessage = PoisonPill,
      settings = ClusterSingletonManagerSettings(system)),
    name = "silly")

  val proxy = system.actorOf(
    ClusterSingletonProxy.props(
      singletonManagerPath = "/user/silly",
      settings = ClusterSingletonProxySettings(system)),
    name = "sillyProxy")

  proxy ! "hi"
  proxy ! "there"

  StdIn.readLine()
  system.terminate()
}
