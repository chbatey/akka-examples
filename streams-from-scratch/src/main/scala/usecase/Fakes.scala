package usecase

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.Flow

import scala.concurrent.Promise
import scala.concurrent.duration._
import scala.util.Random

object Fakes {

  val random = Random

  def delay500[T](implicit as: ActorSystem): Flow[T, T, NotUsed] = {
    implicit val es = as.dispatcher
    val p = Promise[T]()
    Flow[T].mapAsync(1)(t => {
      as.scheduler.scheduleOnce(random.nextInt(1000).millis, new Runnable {
        override def run(): Unit = p.success(t)
      })
      p.future
    })
  }

}
