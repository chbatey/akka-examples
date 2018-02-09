package info.batey.akka

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

import scala.io.StdIn

object Main extends App {

  implicit val timeout = Timeout(1.second)
  val system = ActorSystem()
  import system.dispatcher

  val a = system.actorOf(Props[BestActor], "the-best")

  a ! "one"
  a ! "two"

  val x = a ? "cat"

  x.onComplete(println)
}

class BestActor extends Actor {
  def receive = {
    case "cat" =>
      println("cat")
      sender() ! "you're the best"
    case msg =>
      println(msg)
  }
}


