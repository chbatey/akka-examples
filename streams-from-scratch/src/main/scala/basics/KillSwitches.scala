package basics

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object KillSwitches extends App {

  implicit val system = ActorSystem()
  implicit val materialiser = ActorMaterializer()


}
