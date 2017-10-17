package info.batey.akka.typed

import akka.typed._
import akka.typed.scaladsl.AskPattern._
import akka.typed.scaladsl.Actor
import akka.util.Timeout

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object TypedImmutableApp extends App {
  println("Let's get typed")

  case class SayHello(replyTo: ActorRef[String])

  val guardianBehaviour = Actor.immutable[SayHello] { (ctx, msg) => {
    msg.replyTo ! "Hello"
    Actor.same
  }}

  val system = ActorSystem(guardianBehaviour, "ActorSystem")
  implicit val timeout = Timeout(1.second)
  implicit val scheduler = system.scheduler

  val greeting: Future[String] = system ? SayHello

  println(Await.result(greeting, 1.second))

  system.terminate()
}
