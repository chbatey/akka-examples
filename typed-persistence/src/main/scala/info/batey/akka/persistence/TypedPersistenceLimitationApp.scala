package info.batey.akka.persistence

import akka.actor.ActorSystem
import akka.typed._
import akka.typed.persistence.scaladsl.PersistentActor
import akka.typed.persistence.scaladsl.PersistentActor.{Persist, PersistNothing}
import akka.typed.scaladsl._
import akka.typed.scaladsl.adapter._
import akka.util.Timeout

import scala.concurrent.duration._
import scala.io.StdIn
/*
object TypedPersistenceLimitationApp extends App {
  implicit val timeout: Timeout = Timeout(1.second)
  case class Balance(balance: Long)

  sealed trait Command
  final case class Deposit(a: Long) extends Command
  final case class GetBalance(replyTo: ActorRef[Balance]) extends Command

  sealed trait Event
  final case class Deposited(a: Long) extends Event
  final case class Withdrawn(a: Long) extends Event

  val bankBehaviour: Behavior[Command] =
    Actor.supervise(PersistentActor.immutable[Command, Event, Balance](
      persistenceId = "typedPersistenceLimitation",
      initialState = Balance(0),
      actions = PersistentActor.Actions { (ctx, cmd, state) ⇒ {
        cmd match {
          case Deposit(d) =>
            val evt = Deposited(d)
            if (d > 100) {
              Persist(evt)
            } else {
              Persist(evt)
            }
          case GetBalance(replyTo) =>
            replyTo ! state
            PersistNothing()
        }
      }
      },
      applyEvent = (evt, state) ⇒ {
        evt match {
          case Deposited(d) => state.copy(balance = state.balance + d)
          case Withdrawn(d) => state.copy(balance = state.balance - d)
        }
      })).onFailure(SupervisorStrategy.restart)


  val driver = Actor.immutable[String] { (ctx, msg) => {
    msg match {
      case "go" =>
        val driverRef = system.spawn(iWantTheBalance, "spy")
        val myBankRef = ctx.spawn(bankBehaviour, "my-bank")
        myBankRef ! Deposit(1)
        myBankRef ! Deposit(2)
        myBankRef ! Deposit(100)
        myBankRef ! GetBalance(driverRef)
        myBankRef ! Deposit(1000)
      case other =>
        println(other)
    }
    Actor.same
  }
  }

  val iWantTheBalance: Behavior[Balance] =
    Actor.immutable { (ctx, msg) =>
      println(msg)
      Actor.same
    }

  val system = ActorSystem("Untyped")
  val driverRef: ActorRef[String] = system.spawn(driver, "driver")
  driverRef ! "go"

  println("waiting...")
  StdIn.readLine()
  system.terminate()
  println("done")
}
*/
