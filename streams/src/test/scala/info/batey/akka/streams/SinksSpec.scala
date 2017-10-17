package info.batey.akka.streams

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem, Cancellable}
import akka.stream.scaladsl._
import akka.stream.testkit._
import akka.stream.testkit.scaladsl._
import akka.stream.{ActorMaterializer, Materializer, _}
import akka.testkit.TestKit
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}

class SinksSpec extends TestKit(ActorSystem("BasicsSpec")) with WordSpecLike with Matchers with ScalaFutures with BeforeAndAfterAll {

  implicit val materialiser: Materializer = ActorMaterializer()

  override def afterAll(): Unit = {
    shutdown()
  }

  def toListSink[A]: Sink[A, Future[List[A]]] = Sink.fold(List.empty[A])((acc, next) => next :: acc)

  val source = Source(1 to 3)

  "All the sinks" should {
    "" in {
      source.runWith(Sink.foreach(println))
    }

  }
}
