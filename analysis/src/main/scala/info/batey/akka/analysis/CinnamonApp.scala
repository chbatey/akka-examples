package info.batey.akka.analysis

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import scala.io.StdIn

class Counter extends Actor with ActorLogging {
  def receive = count(0)

  def count(c: Int): Receive = {
    case i: Int =>
      log.info("increment {}", i)
      context.become(count(c + i))
  }
}

object CinnamonApp extends App {
  val as = ActorSystem()
  val c = as.actorOf(Props[Counter])
  (0 to 100).foreach { i => c ! i }
}
