package info.batey.akka.persistence

import akka.actor.{ActorSystem, Props}
import akka.persistence.PersistentActor
import info.batey.akka.persistence.AutomaticPersist.RealPersistentActor.{PrintName, SayName}

object AutomaticPersist extends App {

  trait PersistMe

  trait AutomaticPersister extends PersistentActor {

    val persistPredicate: Any => Boolean

    final override def receive = receiveCommand

    override def receiveCommand: Receive = {
      case cmd: PersistMe => persist(cmd) { evt =>
        println("Persisting: " + cmd)
        commandHandler(evt)
      }
      case cmd =>
        println("Not persisting: " + cmd)
        commandHandler(cmd)
    }

    def commandHandler(evt: Any): Unit
  }

  object RealPersistentActor {
    case class PrintName(name: String) extends PersistMe
    case class SayName(name: String)

  }

  class RealPersistentActor(val persistenceId: String) extends AutomaticPersister {
    override def receiveRecover = {
      case PrintName(name) =>
        println(s"recovering $name")
      case msg =>
        println(s"Other kinds of msg: $msg")
    }

    override def commandHandler(evt: Any): Unit = evt match {
      case PrintName(name) =>
        println(s"Already been persisted by AlwaysPersist, now I just print the name: $name")
    }
    override val persistPredicate = {
      case _:PrintName => true
      case _ => false
    }
  }

  val system = ActorSystem()
  val pa = system.actorOf(Props(new RealPersistentActor("p100")))

  pa ! PrintName("bella")
  pa ! PrintName("ruby")
  pa ! SayName("wilma")
}
