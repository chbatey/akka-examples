package info.batey.akka.streams

import akka.actor.{ActorRef, ActorSystem, Cancellable}
import akka.stream.scaladsl._
import akka.stream._
import akka.stream.{ActorMaterializer, Materializer}
import akka.testkit.{TestKit, TestProbe}
import akka.stream.testkit._
import akka.stream.testkit.scaladsl._
import akka.{Done, NotUsed}
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.{Future, Promise}
import scala.concurrent.duration._

class SourcesSpec extends TestKit(ActorSystem("BasicsSpec")) with WordSpecLike with Matchers with ScalaFutures with BeforeAndAfterAll {

  implicit val materialiser: Materializer = ActorMaterializer()

  override def afterAll(): Unit = {
    shutdown()
  }

  def toListSink[A]: Sink[A, Future[List[A]]] = Sink.fold(List.empty[A])((acc, next) => next :: acc)

  "All the sources" should {
    "Iterable" in {
      Source(1 to 10)
      Source(List(1, 2, 3))
    }

    "From an actor (no back pressure, msgs are buffered with the given overflow strategy)" in {
      val source: Source[String, ActorRef] = Source.actorRef(2, OverflowStrategy.dropHead)

      val (sourceActor, sub) = source
        .toMat(TestSink.probe)(Keep.both)
        .run()

      sourceActor ! "cat"
      sourceActor ! "dog"

      sub.requestNext("cat")
      sub.requestNext("dog")
    }

    "Cycle: around we go " in {
      val source: Source[Int, NotUsed] = Source.cycle(() => (1 to 3).toIterator)

      val sub = source.toMat(TestSink.probe)(Keep.right).run()

      sub.requestNext(1)
      sub.requestNext(2)
      sub.requestNext(3)
      sub.requestNext(1)
    }

    "Maybe: use a promise to complete a source" in {
      val source: Source[String, Promise[Option[String]]] = Source.maybe[String]

      val (promise: Promise[Option[String]], sub) = source.toMat(TestSink.probe)(Keep.both).run()

      sub.request(1)
      sub.expectNoMessage(100.milliseconds)

      promise.success(Some("cats"))

      sub.expectNext("cats")
    }

    "Tick and cancel" in {
      // Source.tick()
      val ticker: Source[String, Cancellable] = Source.tick(100.millis, 100.millis, "Tick")

      val (cancel, sub) = ticker.toMat(TestSink.probe)(Keep.both).run()

      sub.requestNext("Tick")
      sub.requestNext("Tick")

      cancel.cancel()

      sub.expectComplete()
    }

    "Unfold: " in {
      val source: Source[Int, NotUsed] = Source.unfold(0) { x =>
        if (x < 3) Some((x + 1, x + 1))
        else None
      }

      val sub: TestSubscriber.Probe[Int] = source.toMat(TestSink.probe)(Keep.right).run()

      sub.requestNext(1)
      sub.requestNext(2)
      sub.requestNext(3)
      sub.request(1)
      sub.expectComplete()
    }
  }
}
