package info.batey.akka.cassandra

import java.util.concurrent.ExecutionException

import akka.Done
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.persistence.cassandra.EventsByTagMigration
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.journal.Tagged
import akka.persistence.query.{NoOffset, PersistenceQuery}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.util.Timeout
import com.datastax.driver.core.exceptions.InvalidQueryException

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.io.StdIn
import scala.util.{Failure, Success}

object Version80Migration extends App {
  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher

  val migrator = EventsByTagMigration(system)

  val schemaMigration: Future[Done] = for {
    _ <- migrator.createTables()
    done <- migrator.addTagsColumn().recover { case i: ExecutionException if i.getMessage.contains("conflicts with an existing column") => Done}
  } yield done

  val dataMigration = for {
   _ <- schemaMigration
    done <- migrator.migrateToTagViews()
  } yield done

  dataMigration.onComplete {
    case Success(_) => system.log.info("Migration finished")
    case Failure(t) => system.log.error(t, "Migration failed")
  }

  Await.ready(dataMigration, Duration.Inf)
  system.log.info("Migration finished")
  StdIn.readLine()
  system.terminate()
}

object Version80 extends App {
  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val timeOut = Timeout(10.second)
  implicit val materialiser = ActorMaterializer()
  val p1 = system.actorOf(Persistor.props("p1"))
  val allDone = Future.sequence((0 until 5).map { i =>
    p1 ? Tagged(s"Version80Event$i", Set("version80", "tag"))
  })
  allDone.onComplete(println)
  Await.ready(allDone, Duration.Inf)

  val readJournal =
    PersistenceQuery(system).readJournalFor[CassandraReadJournal](
      "cassandra-query-journal")

  readJournal.currentEventsByTag("tag", NoOffset).to(Sink.foreach(println)).run()

  StdIn.readLine()
  system.terminate()
}
