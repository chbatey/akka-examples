package info.batey.akka.typed

import akka.actor.ActorSystem
import akka.typed.{ActorRef, Behavior}
import akka.typed.scaladsl.Actor
import akka.typed.scaladsl.adapter._

import scala.io.StdIn


object Interop extends App {

  case class Msg(replyTo: ActorRef[String], msg: String)

  val echoer = Actor.immutable[Msg] { (ctx, msg) =>
    msg match {
      case Msg(replyTo, m) =>
        println(s"I got a msg: $m")
        replyTo ! s"Hello, take it back: $m"
        Actor.same
    }
  }

  val system = ActorSystem()

  val adaptedActor = system.spawn(echoer, "something")
  val deadEnd = system.spawn(Behavior.ignore[String], "idonothing")

  var msg = ""
  while (msg != ":q") {
    msg = StdIn.readLine()
    adaptedActor ! Msg(deadEnd, msg)
  }

  system.terminate()
}
