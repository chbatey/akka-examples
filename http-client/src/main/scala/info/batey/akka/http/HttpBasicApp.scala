package info.batey.akka.http

import java.util.concurrent.TimeoutException

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.pattern.FutureTimeoutSupport
import akka.stream.ActorMaterializer

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object HttpBasicApp extends App with FutureTimeoutSupport {

  implicit val system = ActorSystem()
  implicit val materialiser = ActorMaterializer()


  val f = Future[Int] {
    Thread.sleep(10 * 1000 * 100)
    10
  }


  val x: Future[Int] = after(1.second, system.scheduler)(f)

  Await.result(x, 2.seconds)

  private def execute(request: HttpRequest, timeout: FiniteDuration): Future[HttpResponse] = {
    val response: Future[HttpResponse] = Http().singleRequest(request)

    val timedResponse: Future[HttpResponse] = withTimeout(response, timeout)

    timedResponse.failed.foreach {
      case _: TimeoutException =>
        response.foreach(toDiscard => toDiscard.discardEntityBytes())
    }

    timedResponse
  }

  private def withTimeout[T](f: Future[T], timeout: FiniteDuration): Future[T] =
    Future.firstCompletedOf(List(f, after(timeout, using = system.scheduler)(failWith(timeout))
    ))

  private def failWith[T](timeout: FiniteDuration): Future[T] =
    Future.failed[T](new java.util.concurrent.TimeoutException(s"Http request timed out after $timeout"))

}
