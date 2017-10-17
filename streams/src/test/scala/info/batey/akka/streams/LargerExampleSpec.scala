package info.batey.akka.streams

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import akka.stream.testkit.TestSubscriber
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.testkit.scaladsl._
import akka.testkit.TestKit
import akka.{Done, NotUsed}
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration._

class LargerExampleSpec extends TestKit(ActorSystem("LargerExapleSpec")) with WordSpecLike with Matchers with ScalaFutures with BeforeAndAfterAll {

  implicit val materialiser: Materializer = ActorMaterializer()

  override def afterAll(): Unit = {
    shutdown()
  }

  def toListSink[A]: Sink[A, Future[List[A]]] = Sink.fold(List.empty[A])((acc, next) => next :: acc)

  val source: Source[Int, NotUsed] = Source(1 to 10000)

  val factorials: Source[Int, NotUsed] = source.scan(1)((acc, next) => acc * next).drop(1)

  "factorials" should {
    "be right" in {
      val fac = factorials.take(5).toMat(toListSink)(Keep.right)
      val ran: Future[List[Int]] = fac.run()
      ran.futureValue.reverse should equal(List(1, 2, 6, 24, 120))
    }

    "throttling" in {
//      val done: Future[Done] = factorials
//        .zipWith(Source(1 to 10))((num, idx) => s"$idx => $num")
//        .throttle(1, 1.second, 1, ThrottleMode.Shaping)
//        .runForeach(println)

//      Await.ready(done, 12.seconds)
    }
  }

}
