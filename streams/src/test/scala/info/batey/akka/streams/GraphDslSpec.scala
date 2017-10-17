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


class GraphDslSpec extends TestKit(ActorSystem("GraphDslSpec")) with WordSpecLike
  with Matchers
  with ScalaFutures
  with BeforeAndAfterAll {

  implicit val materialiser: Materializer = ActorMaterializer()

  override def afterAll(): Unit = {
    shutdown()
  }

  "GraphDsl" should {
    "allow faning out" in {
      val oneToOneHundred = Source(1 to 100)

      val odds: Sink[Int, Future[Done]] = Sink.foreach((o: Int) => println(s"Odd: $o"))
      val evens: Sink[Int, Future[Done]] = Sink.foreach((e: Int) => println(s"Even: $e"))

      val evenAndOdd: RunnableGraph[NotUsed] = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
        import GraphDSL.Implicits._

        val bcast: UniformFanOutShape[Int, Int] = builder.add(Broadcast[Int](2))
        oneToOneHundred ~> bcast.in
        val toEvent = bcast.out(0) ~> Flow[Int].filter(_ % 2 == 0) ~> evens
        val toOdd = bcast.out(1) ~> Flow[Int].filter(_ % 2 != 0) ~> odds

        ClosedShape
      })

      val ran: NotUsed = evenAndOdd.run()
    }
  }

}
