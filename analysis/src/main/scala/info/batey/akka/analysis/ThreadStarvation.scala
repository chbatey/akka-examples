package info.batey.akka.analysis

import akka.actor.{Actor, ActorSystem}
import com.typesafe.config.ConfigFactory
import com.lightbend.akka.diagnostics.StarvationDetector

import scala.io.StdIn

object ThreadStarvation extends App {

  val config = ConfigFactory.parseString(
    """

    """.stripMargin)

  val actorSystem = ActorSystem("CatsAndDogs", config)

  StdIn.readLine()
  println("Starting second")

  val other = ActorSystem()

  StdIn.readLine()
  println("Starting third")

  val another = ActorSystem("dog")

  StarvationDetector.checkSystemDispatcher(another)

}


