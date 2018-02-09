package info.batey.akka.cassandra

import akka.actor.{ActorLogging, Props}
import akka.persistence.PersistentActor
import akka.persistence.journal.Tagged

class Persistor(val persistenceId: String) extends PersistentActor with ActorLogging {
  override def receiveRecover: Receive = {
    case s: String => processEvent(s)
    case s: Tagged => processEvent(s)
  }
  override def receiveCommand: Receive = {
    case s: String =>
      persist(s) { e =>
        processEvent(e)
      }
    case tagged: Tagged =>
       persist(tagged) { e =>
        processEvent(e)
      }
  }

  def processEvent(e: Any): Unit = {
    log.info("Event: {}", e)
  }
}

object Persistor {
  def props(id: String): Props = Props(new Persistor(id))
}