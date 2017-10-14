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


class GraphDslSpec extends TestKit("GraphDslSpec") with WordLikeSpec with Matchers with ScalaFutures with BeforeAndAfterAll {

  implicit val materialiser: Materializer = ActorMaterializer()

  override def afterAll(): Unit = {
    shutdown()
  }

  "GraphDsl" should {
    "allow faning out" in {
      val oneToOneHundred = Source(1 to 100)

      val evenAndOdd = RunnableGraph.fromGraph(GraphDSL.create() { implicit buildler =>
        ???
      })

    }
  }

}
