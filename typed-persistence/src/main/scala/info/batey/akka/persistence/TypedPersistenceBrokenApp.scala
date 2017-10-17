package info.batey.akka.persistence

import akka.actor.{ActorSystem, Scheduler}
import akka.typed.scaladsl.AskPattern._
import akka.typed.persistence.scaladsl.PersistentActor
import akka.typed.persistence.scaladsl.PersistentActor.{Persist, PersistNothing}
import akka.typed.scaladsl.adapter._
import akka.typed._
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.StdIn

object TypedPersistenceBrokenApp extends App {

  val system = ActorSystem("Persistence")
  implicit val timeout: Timeout = Timeout(1.second)
  implicit val scheduler: Scheduler = system.scheduler

  case class State(carers: Set[String])

  sealed trait Command
  final case class IThinkICare(who: String) extends Command
  case class WhoCares(reply: ActorRef[State]) extends Command

  sealed trait Event
  final case class ICare(who: String) extends Event

  val behavior: Behavior[Command] =
    PersistentActor.immutable[Command, Event, State](
      persistenceId = "p-2",
      initialState = State(Set.empty[String]),
      actions = PersistentActor.Actions { (ctx, cmd, state) => {
        cmd match {
          case IThinkICare(who) =>
            Persist(ICare(who))
          case WhoCares(iWantToKnow) =>
            iWantToKnow ! state
            PersistNothing()
        }
      }
      },
      applyEvent = (evt, state) => {
        evt match {
          case ICare(who) =>
            state.copy(carers = state.carers + who)
        }
      }
    )

  val ref: ActorRef[Command] = system.spawn(behavior, "the-best")

  ref ! IThinkICare("Christopher")

  val who: Future[State] = ref ? (WhoCares(_))

  StdIn.readLine()
  system.terminate()
}
