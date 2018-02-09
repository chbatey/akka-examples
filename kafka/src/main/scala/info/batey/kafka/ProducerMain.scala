package info.batey.kafka

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn
import scala.concurrent.duration._

object ProducerMain extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materialiser = ActorMaterializer()

  val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
    .withBootstrapServers("localhost:9092")

  val done = Source.tick(1.second, 100.millis, 1)
    .map(_.toString)
    .map { elem =>
      new ProducerRecord[Array[Byte], String]("test1", elem)
    }
    .runWith(Producer.plainSink(producerSettings))

  StdIn.readLine()

  done.onComplete(_ => {
    println("And we're done")
    system.terminate()
  }
  )
}
