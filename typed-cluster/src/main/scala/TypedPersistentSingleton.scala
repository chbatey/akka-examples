import akka.typed.cluster.{ClusterSingleton, ClusterSingletonSettings}
import akka.typed.persistence.scaladsl.PersistentActor
import akka.typed.persistence.scaladsl.PersistentActor.{CommandHandler, Effect}
import akka.typed.{ActorRef, ActorSystem, Behavior, Props}
import akka.actor.{ActorSystem => UActorSystem}
import akka.typed.scaladsl.adapter._

object TypedPersistentSingleton extends App {

  lazy val commandHandler: CommandHandler[Command, Event, State] =
    CommandHandler { (_, state, cmd) ⇒
      println(s"$cmd")
      Effect.none
    }

  lazy val behaviour: Behavior[Command] = PersistentActor.persistentEntity(_ => "Name", State(), commandHandler, eventHandler)
  private def eventHandler(state: State, event: Event): State = state

  val system = UActorSystem("ClusterSystem")
  val yo = spawnSingleton(system.toTyped)
  yo ! Command("do it")


  case class Command(cmd: String)
  case class Event(evt: String)

  case class State(map: Map[String, String] = Map.empty[String, String])

  def spawnSingleton(system: ActorSystem[Nothing]): ActorRef[Command] =
    ClusterSingleton(system)
      .spawn(
        behaviour,
        "SingletonName",
        Props.empty,
        ClusterSingletonSettings(system),
        Command("Good bye")
      )

  //PersistentActor.immutable(Name, State(), commandHandler, eventHandler)
}
