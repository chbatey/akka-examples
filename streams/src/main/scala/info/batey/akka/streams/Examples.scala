package info.batey.akka.streams

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Keep, MergeHub, Sink, Source, SourceQueueWithComplete}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.io.StdIn

object Examples extends App {

  val config = ConfigFactory.parseString(
    """
      | akka.loglevel = DEBUG
    """.stripMargin)

  implicit val system = ActorSystem("Examples", config)
  implicit val materialisr = ActorMaterializer()
  implicit val timeout = Timeout(12.seconds)

  val x: Source[Int, ActorRef] = Source.actorRef[Int](10, OverflowStrategy.dropNew)

  val y: ActorRef = x.toMat(Sink.foreach(println))(Keep.left).run()

  y ! "hello"
  y ! "there"

  StdIn.readLine()

  val xx = Source.queue[String](100, OverflowStrategy.dropNew)
  val yy: SourceQueueWithComplete[String] = xx.toMat(Sink.foreach(println))(Keep.left).run()

  println(Await.result(yy.offer("cat"), 1.second))
  println(Await.result(yy.offer("dog"), 2.second))

  StdIn.readLine()

  MergeHub.source


  StdIn.readLine()
  system.terminate()



}
