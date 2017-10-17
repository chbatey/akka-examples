package info.batey.akka.persistence

import akka.persistence.journal.{EventAdapter, EventSeq, SingleEventSeq, Tagged}
import info.batey.akka.persistence.PersistenceApp.Withdrawn

class TaggingEventHandler extends EventAdapter {

  override def fromJournal(event: Any, manifest: String): EventSeq = {
    println("from journal")
    event match {
      case Tagged(a, _) => SingleEventSeq(a)
      case a => SingleEventSeq(a)
    }
  }

  override def manifest(event: Any): String = ""

  override def toJournal(event: Any): Any = {
    println("to journal")
    event match {
      case Withdrawn(a) if a < 1000 =>
      case a => a
    }
  }
}
