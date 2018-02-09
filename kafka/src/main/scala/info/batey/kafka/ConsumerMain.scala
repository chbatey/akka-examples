package info.batey.kafka

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

object ConsumerMain extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materialiser = ActorMaterializer()

  val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("group1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")

  val sub = Subscriptions.topics("test1")

//  val source = RestartSource.withBackoff(
//    minBackoff = 3.seconds,
//    maxBackoff = 30.seconds,
//    randomFactor = 0.2)( () => {
//    println("Creating consumer")
//    Consumer.plainSource(consumerSettings, sub)
//  })

 val source: Source[ConsumerRecord[Array[Byte], String], Consumer.Control] = Consumer.plainSource(consumerSettings, sub)

  val exit: Future[Done] = source.runForeach {
    record => println(s"Record: $record")
  }

  exit.onComplete(msg => {
    println("Future has finished")
    println(msg)
    system.terminate()
  })

}
