package info.batey.akka.typed

import akka.typed.SupervisorStrategy
import akka.typed.Behavior
import akka.typed.scaladsl._

object TypedDeferredApp extends App {
  println("Time to defer")

  val deferred: Behavior[String] = Actor.deferred { ctx =>
    println("Not called")
    Behavior.empty[String]
  }

  val immutable = Actor.immutable[String] { (ctx, msg) =>
    Actor.same
  }

  val supervised: Behavior[String] = Actor.supervise(immutable)
    .onFailure(SupervisorStrategy.restart)


}
