package info.batey.akka

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

import scala.io.StdIn

object Main extends App {

  implicit val timeout = Timeout(1.second)
  val actorSystem = ActorSystem()
  import actorSystem.dispatcher

  println("Started")

  val a = actorSystem.actorOf(Props[BestActor])

  a ! "one"
  a ! "two"

  val x = (a ? "cat")

  x.onComplete(println)

  StdIn.readLine()

}

class BestActor extends Actor {
  def receive = {
    case msg => println(msg)
  }
}


