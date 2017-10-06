package info.batey.kafka

import akka.{Done, NotUsed}
import akka.actor._
import akka.kafka.ConsumerMessage.CommittableOffsetBatch
import akka.kafka._
import akka.kafka.scaladsl.{Consumer, _}
import akka.stream._
import akka.stream.scaladsl._
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.StdIn

object AskTimeoutExample extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materialiser: ActorMaterializer = ActorMaterializer()

  val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
    .withBootstrapServers("localhost:9092")

  val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("group1")

  val consumerMaxPartition = 10

  val anything: Source[Future[Done], Consumer.Control] = Consumer.committablePartitionedSource(consumerSettings, Subscriptions.topics("test1"))
    .map { case (_, source: Source[ConsumerMessage.CommittableMessage[Array[Byte], String], NotUsed]) =>

      val eh: Source[String, NotUsed] = source
        .map { (m: ConsumerMessage.CommittableMessage[Array[Byte], String]) => {
          val p = ProducerMessage.Message(new ProducerRecord[Array[Byte], String]("one-partition", m.toString + " Mapped"), m.committableOffset)
          p
        }
        }
        .via(Producer.flow(producerSettings, producerSettings.createKafkaProducer()))
        .map(_.message.passThrough)
        .batch(20, CommittableOffsetBatch.empty.updated) { (batch, elem) => batch.updated(elem) }
        .mapAsync(producerSettings.parallelism)((m: CommittableOffsetBatch) => {
          println("Committing producer: " + m)
          val x = m.commitScaladsl()
          x.onFailure {
            case ex: Throwable => ex.printStackTrace(System.out)
          }
          x.map(_ => "cats")
        })


      val cats: Future[Done] = eh.toMat(Sink.ignore)(Keep.right).run()

      cats
    }

  val something = anything.mapAsyncUnordered(consumerMaxPartition)(identity)
    .to(Sink.ignore).run()

  something.isShutdown.onComplete { result =>
    println("Stopped")
    println(result)
    system.terminate()
  }

  println("gogogo")
  StdIn.readLine()
  something.shutdown()
}
