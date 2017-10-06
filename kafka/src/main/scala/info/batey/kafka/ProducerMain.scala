package info.batey.kafka

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import scala.concurrent.ExecutionContext.Implicits.global

object ProducerMain extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materialiser = ActorMaterializer()

  val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
    .withBootstrapServers("localhost:9092")

  val done = Source(1 to 100)
    .map(_.toString)
    .map { elem =>
      new ProducerRecord[Array[Byte], String]("test1", elem)
    }
    .runWith(Producer.plainSink(producerSettings))

  done.onComplete(_ => {
    println("And we're done")
    system.terminate()
  }
  )
}
