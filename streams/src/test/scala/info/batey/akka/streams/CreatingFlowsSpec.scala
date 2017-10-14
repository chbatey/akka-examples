package info.batey.akka.streams

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.stream.{ActorMaterializer, Materializer}
import akka.testkit.TestKit
import akka.{Done, NotUsed}
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Future

class CreatingFlowsSpec extends TestKit(ActorSystem("CreatingFlowsSpec")) with WordSpecLike
  with Matchers
  with ScalaFutures
  with BeforeAndAfterAll {
  implicit val materialiser: Materializer = ActorMaterializer()

  override def afterAll(): Unit = {
    shutdown()
  }

  "Flow[T]" should {
    "reduce" in {
      val adder: Flow[Int, Int, NotUsed] = Flow[Int].reduce(_ + _)

      val last: Future[Int] = Source(1 to 3)
          .via(adder).runWith(Sink.last)

      last.futureValue should equal(6)
    }

    "collect" in {
      val evenTimesTen = Flow[Int].collect {
        case i if i % 2 == 0 => i * 10
      }

      val hrmm: RunnableGraph[Future[List[Int]]] = Source(1 to 5)
          .via(evenTimesTen)
          .toMat(Sink.fold(List.empty[Int])((acc, next) => next :: acc))(Keep.right)

      hrmm.run().futureValue should equal(List(40, 20))
    }

    "" in {
      //TODO
    }
  }
}
