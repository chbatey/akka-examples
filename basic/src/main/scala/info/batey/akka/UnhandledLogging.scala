package info.batey.akka

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object UnhandledLogging extends App {

  class HandleCat extends Actor {
    def receive = {
      case "cat" => println("handled")
    }
  }

  val config = ConfigFactory.parseString(
    """
     akka {
       loglevel = DEBUG
       actor {
        debug {
          unhandled = on
        }
       }
     }
    """.stripMargin)

  val system = ActorSystem("Unhandled", config)
  val ref = system.actorOf(Props[HandleCat], "catty")

  ref ! "cat"
  // [DEBUG] [akka://Unhandled/user/catty] unhandled message from Actor[akka://Unhandled/deadLetters]: dog
  ref ! "dog"

  system.terminate()
}
