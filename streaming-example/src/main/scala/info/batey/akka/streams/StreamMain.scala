package info.batey.akka.streams

import java.util.concurrent.TimeUnit

import akka.Done
import akka.actor._
import akka.stream.scaladsl._
import akka.stream._
import com.codahale.metrics.jmx.JmxReporter
import com.codahale.metrics.{ConsoleReporter, Histogram, MetricRegistry}

import scala.concurrent.{Future, Promise}
import scala.concurrent.duration._

object StreamMain extends App {

  val metrics = new MetricRegistry
  val rate = metrics.meter("overall")
  val read = metrics.meter("read")
  val write = metrics.meter("write")


  implicit val system = ActorSystem()
  val materialisationSettings = ActorMaterializerSettings(system)
  implicit val materialiser = ActorMaterializer(materialisationSettings)
  implicit val ec = system.dispatcher

  def delayedFuture[T](value: T, delay: FiniteDuration): Future[T] = {
    val promise = Promise[T]
    system.scheduler.scheduleOnce(delay) {
      promise.success(value)
    }
    promise.future
  }

  val source = Source.unfoldResource[Int, Int](() => 0, i => Some(i + 1), _ => ())

  val outstanding = 500

  val processing = source
    .map { msg =>
//      actorSystem.log.info(s"$msg: From kafka: " + Thread.currentThread())
      msg
    }
    .mapAsyncUnordered(outstanding)(msg => {
      // read from cassandra
//      actorSystem.log.info(s"$msg: DB read: " + Thread.currentThread())
      read.mark()
      delayedFuture(msg, 1000.millis)
    })
    .map(s => {
      // important processing
      s
    })
    .mapAsyncUnordered(outstanding)(msg => {
      //write to cassandra
//      actorSystem.log.info(s"$msg: DB write: " + Thread.currentThread())
      write.mark()
      delayedFuture(msg, 1000.millis)
    })

  val processingBlocking = source
    .map(msg => {
      // read from cassandra
//      actorSystem.log.info(s"$msg: DB read: " + Thread.currentThread())
      read.mark()
      Thread.sleep(500)
      msg
    })
    .map(s => {
      // important processing
      s
    })
    .map(msg => {
      //write to cassandra
      system.log.info(s"$msg: DB write: " + Thread.currentThread())
      write.mark()
      Thread.sleep(500)
      msg
    })

  /*
  TODO
  - Throttling
  - Ordering
   */

  val sink: Sink[Int, Future[Done]] = Sink.foreach(msg => {
    rate.mark()
  })

  processing.runWith(Sink.foreach(msg => {
    //actorSystem.log.info(s"$msg: Finished: " + Thread.currentThread())
    rate.mark()
  }))

//  source.runWith(sink)


  val consoleReporter = ConsoleReporter.forRegistry(metrics)
    .convertRatesTo(TimeUnit.SECONDS)
    .convertDurationsTo(TimeUnit.MILLISECONDS)
    .build()

  consoleReporter.start(5, TimeUnit.SECONDS)

  val jmxReporter = JmxReporter.forRegistry(metrics)
    .convertRatesTo(TimeUnit.SECONDS)
    .convertDurationsTo(TimeUnit.MILLISECONDS)
    .build()
  jmxReporter.start()

}
