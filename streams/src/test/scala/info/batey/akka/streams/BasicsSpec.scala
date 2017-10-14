package info.batey.akka.streams

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import akka.{Done, NotUsed}
import akka.stream.scaladsl._
import akka.testkit.TestKit
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Future

class BasicsSpec extends TestKit(ActorSystem("BasicsSpec")) with WordSpecLike with Matchers with ScalaFutures with BeforeAndAfterAll {

  implicit val materialiser: Materializer = ActorMaterializer()

  override def afterAll(): Unit =  {
    shutdown()
  }

  def toListSink[A]: Sink[A, Future[List[A]]] = Sink.fold(List.empty[A])((acc, next) => next :: acc)

  "Basic streams" should {

    "have two params, one for the element, another is 'something else' for basic streams it will be NotUsed" in {
      val source: Source[Int, NotUsed] = Source(1 to 50)
      // Nothing has happened so far

      // Every Source, Flow and Sink has a Mat so anything we combine them
      // we decide if we want the Left, Right or Both

      // Sink.ignore's Mat is a Future[Done], so let's keep that
      val sinkIgnoreMat: RunnableGraph[Future[Done]] = source.toMat(Sink.ignore)(Keep.right)

      // Once we have attached a Sink to a Source we have something that is runnable
      // still nothing has happened

      // Until...
      val done: Future[Done] = sinkIgnoreMat.run()
      done.futureValue should equal(Done)
    }

    "did it actually happen? Let's try a different sink" in {
      val x: RunnableGraph[Future[List[Int]]] = Source(1 to 5)
        .toMat(toListSink[Int])(Keep.right)

      x.run().futureValue should equal(List(5, 4, 3, 2, 1))
    }

    "Sink ignore can be used if all you want to do is side effect" in {
      val lazyhat: RunnableGraph[(NotUsed, Future[Done])] = Source(1 to 5).map(i => {
        println(i)
        i
      }).toMat(Sink.ignore)(Keep.both)

      // Of course nothing has happened yet
      // until we call run, then we get both of the materialised values
      val atLast: (NotUsed, Future[Done]) = lazyhat.run()
    }

    "We can also use to()" in {
      val source: Source[Int, NotUsed] = Source(1 to 5)
      val lazyhat: RunnableGraph[NotUsed] = source.map(i => {
        println(i)
        i
      }).to(Sink.ignore)

      // which just keeps the first mat and ignores the sink's mat
      val mat: NotUsed = lazyhat.run()
    }

    "collecting" in {
      val source: Source[Int, NotUsed] = Source(1 to 5)
      // map and filter in one
      val collected: Source[Int, NotUsed] = source.collect {
        case i if i < 3 => i * 10
      }
      val notYet: RunnableGraph[NotUsed] = collected.to(Sink.foreach(println))
      val atLast: NotUsed = notYet.run()
    }
  }
}
