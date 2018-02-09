package info.batey.akka.persistence

import akka.NotUsed
import akka.actor.{ActorSystem, Props}
import akka.persistence.PersistentActor
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.{EventEnvelope, NoOffset, PersistenceQuery}
import akka.stream.{ActorMaterializer, scaladsl}
import akka.stream.scaladsl._

import scala.collection.immutable
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object AllPersistenceIds extends App {

  class SillyActor(val persistenceId: String) extends PersistentActor {
    override def receiveRecover = {
      case msg => println(msg)
    }
    override def receiveCommand =  {
      case msg => persist(msg)(_ => {})
    }
  }

  implicit val system = ActorSystem("Persistence")
  implicit val materialiser = ActorMaterializer()

  val p1 = system.actorOf(Props(new SillyActor("p1")))
  p1 ! "a"

  val p2 = system.actorOf(Props(new SillyActor("p2")))
  p2 ! "2"

 Thread.sleep(1000)

  val readJournal =
    PersistenceQuery(system).readJournalFor[CassandraReadJournal](
      "cassandra-query-journal")

  val all: Future[Seq[String]] = readJournal.currentPersistenceIds().runWith(Sink.seq[String])

  println("Persistence IDs" + Await.result(all, 10.seconds))

  val allEvents: Future[immutable.Seq[EventEnvelope]] = readJournal.currentEventsByPersistenceId("p1", 0, Long.MaxValue).runWith(Sink.seq)

  val x: Source[EventEnvelope, NotUsed] = readJournal.eventsByTag("", NoOffset)

  println("Events" + Await.result(allEvents, 10.seconds))

  system.terminate()
}
