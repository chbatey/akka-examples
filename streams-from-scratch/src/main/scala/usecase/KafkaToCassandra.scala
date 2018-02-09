package usecase

import akka.actor.ActorSystem
import akka.kafka.ConsumerSettings
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

//TODO
object KafkaToCassandra {
  // Stream a topic to a table
  // Regular kafka auto commit

  val config = ConfigFactory.parseString(
    """

    """.stripMargin)

  val system = ActorSystem()

  val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("group1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

}
