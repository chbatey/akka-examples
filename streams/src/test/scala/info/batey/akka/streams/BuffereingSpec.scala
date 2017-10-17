package info.batey.akka.streams

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.stream.{ActorMaterializer, Materializer, OverflowStrategy}
import akka.testkit.TestKit
import akka.{Done, NotUsed}
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Future

class BuffereingSpec extends TestKit(ActorSystem("BufferingSpec")) with WordSpecLike with Matchers with ScalaFutures with BeforeAndAfterAll {

  implicit val materialiser: Materializer = ActorMaterializer()

  override def afterAll(): Unit = {
    shutdown()
  }

  def toListSink[A]: Sink[A, Future[List[A]]] = Sink.fold(List.empty[A])((acc, next) => next :: acc)

  "Buffering" should {
    "buffer, dropping oldest" in {
      val source: Source[Int, NotUsed] = Source(1 to 1000)

      val iBuffer: Source[Int, NotUsed] = source.
        buffer(10, OverflowStrategy.dropTail)

      // TODO

    }
  }

}
