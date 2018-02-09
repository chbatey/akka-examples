package info.batey.akka.streams.tcp

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object OutboundConnection extends App {

  implicit val system = ActorSystem()
  implicit val materialiser = ActorMaterializer()


  // Make an outbound connection but receive all messages along the same "pipe"

}
