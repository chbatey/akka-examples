package info.batey.akka.persistence

import akka.actor.ActorSystem
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.{EventEnvelope, NoOffset, PersistenceQuery}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.{Done, NotUsed}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object EventsByTagApp extends App {

  println("Time to get the tags")

  implicit val system = ActorSystem()
  implicit val materialiser = ActorMaterializer()

  val queries: CassandraReadJournal = PersistenceQuery(system)
    .readJournalFor[CassandraReadJournal](CassandraReadJournal.Identifier)

  val currentById: Source[EventEnvelope, NotUsed] =
    queries.currentEventsByPersistenceId("typedPersistence", 0, Long.MaxValue)

  val doneById: Future[Done] = currentById.runForeach(println)
  Await.ready(doneById, 10.seconds)

  println("Now by tag")

  val boringEvents: Source[EventEnvelope, NotUsed] = queries.eventsByTag("boring", NoOffset)
  val doneTags = boringEvents.runForeach(println)
  Await.ready(doneTags, 10.seconds)

  println("No more boring events")

  system.terminate()
}
