package info.batey.akka

import akka.actor.{Actor, ActorSystem, Props}

import scala.io.StdIn

object Unhandled extends App {

  class SillyActor extends Actor {

    def receive: Receive = {
      case s: String =>
        println("I only like strings")
    }

  }

  val system = ActorSystem()

  val actor = system.actorOf(Props[SillyActor])

  actor ! "hi"
  actor ! 5
  actor ! 10

  StdIn.readLine()
  system.terminate()
}
