package usecase.kp

import java.util.concurrent.TimeUnit

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.scaladsl._
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import com.codahale.metrics.MetricRegistry
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.io.StdIn

// Kafka to in process processing
object KafkaConsumer extends App {
  val metrics = new MetricRegistry
  val rate = metrics.meter("hist")

  import com.codahale.metrics.ConsoleReporter

  val reporter = ConsoleReporter.forRegistry(metrics).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build
  reporter.start(1, TimeUnit.SECONDS)

  implicit val system = ActorSystem()
  implicit val materialiser = ActorMaterializer()


  val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("group1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  // auto commit or with external offset, in this case just ignoring offsets
  val kafkaSource = Consumer.plainSource(consumerSettings, Subscriptions.topics("partition1"))
//  val kafkaSource = Consumer.plainSource(consumerSettings, Subscriptions.topics("partition25"))
//  val kafkaSource = Consumer.plainSource(consumerSettings, Subscriptions.topics("partition50"))

  //partition1  - 11, 11
  //partition25 - 21, 21
  //partition50 - 33, 36

  val records = 10000000
  val startTime = System.nanoTime()

  val (control: Consumer.Control, done: Future[Done]) = kafkaSource
    .map(_ => rate.mark())
    .take(records)
    .toMat(Sink.ignore)(Keep.both).run()

  Await.result(done, Duration.Inf)
  val endTime = System.nanoTime()
  system.log.info(s"Count: ${Await.result(done, 5.second)}. Took ${(endTime - startTime).nanos.toSeconds} seconds")
  system.terminate()
}
