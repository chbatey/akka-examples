package info.batey.akka.http

import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future
import scala.io.StdIn

object HttpCoordinatedShutdown extends App {

  val config = ConfigFactory.parseString(
    """
      akka {
        loglevel = "DEBUG"
        http.server.request-timeout = 3 minutes
      }
    """.stripMargin)

  implicit val system = ActorSystem("Test", config)
  implicit val actorMaterialiser = ActorMaterializer()
  implicit val ec = system.dispatcher

  CoordinatedShutdown(system).addJvmShutdownHook {
    println("custom JVM shutdown hook...")
  }

  val route =
    path("hello") {
      get {
        complete(Future[String] {
          Thread.sleep(120 * 1000)
          "OK"
        })
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/")

  StdIn.readLine()
  bindingFuture.map(_.unbind()).onComplete(println)
  println("Shutting down")

}
