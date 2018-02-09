package info.batey.akka.persistence

import akka.persistence.journal.{EventAdapter, EventSeq, SingleEventSeq, Tagged}
/*
import info.batey.akka.persistence.TypedPersistenceApp.{Deposited, Withdrawn}

class TypedTaggerEventAdapter extends EventAdapter {

  override def fromJournal(event: Any, manifest: String): EventSeq = {
    event match {
      case Tagged(a, _) => SingleEventSeq(a)
      case a => SingleEventSeq(a)
    }
  }

  override def manifest(event: Any): String = ""

  override def toJournal(event: Any): Any = {
    event match {
      case w@Withdrawn(a) if a > 1000 =>
        Tagged(w, Set("large"))
      case d@Deposited(a) if a > 1000 =>
        Tagged(d, Set("large"))
      case d@Deposited(a) if a == 42 =>
        Tagged(d, Set("one", "two", "three", "four"))
      case a =>
        Tagged(a, Set("boring"))
    }
  }
}
*/
