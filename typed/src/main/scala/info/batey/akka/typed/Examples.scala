package info.batey.akka.typed

import akka.typed._
import akka.typed.scaladsl.Actor

object Examples {

  case class SayHelloToMe(me: ActorRef[String])

  val respondingToSender: Behavior[SayHelloToMe] = Actor.immutable { (ctx, msg) =>
    msg match {
      case SayHelloToMe(replyTo) => replyTo ! "hello"
    }
    Actor.same
  }
}
