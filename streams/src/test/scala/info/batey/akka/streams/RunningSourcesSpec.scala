package info.batey.akka.streams

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.stream.{ActorMaterializer, Materializer}
import akka.testkit.TestKit
import akka.{Done, NotUsed}
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Future

class RunningSourcesSpec extends TestKit(ActorSystem("RunningSources")) with WordSpecLike
  with Matchers
  with ScalaFutures
  with BeforeAndAfterAll {
  implicit val materialiser: Materializer = ActorMaterializer()

  override def afterAll(): Unit = {
    shutdown()
  }

  "Running sources should" must {

    "be foldable" in {
      val source: Source[Int, NotUsed] = Source(1 to 5)
      source.runFold(0)(_ + _).futureValue should equal(15)
    }

    "be foldable via a async function" in {
      val source: Source[Int, NotUsed] = Source(1 to 5)
      val folded: Future[Int] = source.runFoldAsync(0)((acc, next) => {
        // okay not so async
        Future.successful(acc + next)
      })
      folded.futureValue should equal(15)
    }

    "be side effectable" in {
      val source: Source[Int, NotUsed] = Source(1 to 5)
      // just do something side effecty - ewww
      val done: Future[Done] = source.runForeach(println)
      done.futureValue should equal(Done)
    }

    "be reducable" in {
      val source: Source[Int, NotUsed] = Source(1 to 5)
      val reduced: Source[Int, NotUsed] = source.reduce(_ + _)

      val lastOption: RunnableGraph[Future[Option[Int]]] = reduced.toMat(Sink.lastOption)(Keep.right)
      val ran: Future[Option[Int]] = lastOption.run()
      ran.futureValue should equal(Some(15))
    }

    "be reducable in one step" in {
      val source: Source[Int, NotUsed] = Source(1 to 5)
      val runReduced: Future[Int] = source.runReduce(_ + _)
      runReduced.futureValue should equal(15)
    }

    "be runWith able to a queue" in {
      val source: Source[Int, NotUsed] = Source(1 to 5)
      val runToQueue: SinkQueueWithCancel[Int] = source.runWith(Sink.queue())
      runToQueue.pull().futureValue should equal(Some(1))
      runToQueue.pull().futureValue should equal(Some(2))
      runToQueue.pull().futureValue should equal(Some(3))
      runToQueue.pull().futureValue should equal(Some(4))
      runToQueue.pull().futureValue should equal(Some(5))
      runToQueue.pull().futureValue should equal(None)
    }
  }
}
