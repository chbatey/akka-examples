package info.batey.akka.streams

import java.nio.charset.StandardCharsets

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString
import com.typesafe.config.ConfigFactory

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

object TcpClient extends App {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val config = ConfigFactory.load()
  val logger = Logging(system, getClass)

  val STX: Byte = 0x02
  //Start transmission
  val ETX: Byte = 0x03
  //End transmission
  val US: Byte = 0x1F //Unit separator


  val buffer = ArrayBuffer[Byte]()

  def checksum(msg: Seq[Byte]): Byte = {
    var bFoundETX = false

    msg.foldLeft[Byte](0) {
      (acc, c) => {
        if (!bFoundETX) {
          if (c == ETX) {
            bFoundETX = true
          }
          (acc ^ c).toByte
        } else {
          acc
        }
      }
    }
  }

  val ACI = "ACI"
  // Account ID
  val clientId = 111111
  val ACE = "ACE"
  val ACS = "ACS"
  val REF = "REF"
  val DTE = "DTE"
  val RQN = "RQN"


  val units = Map(
    ACI -> clientId,
    REF -> "10.118.10.190",
    ACE -> "F",
    ACS -> "C",
    DTE -> "2017-10-26 15:08:59",
    RQN -> 1
  )

  buffer.append(STX)
  buffer.appendAll("Inquire".getBytes(StandardCharsets.US_ASCII))

  for ((name, value) <- units) {
    buffer.append(US)
    buffer.appendAll(s"${name}=${value}".getBytes(StandardCharsets.US_ASCII))
  }

  buffer.append(ETX)
  buffer.append(checksum(buffer))

  val message = buffer.toArray

  println(s"Sending message ${message.mkString("| ")}")

  val address = "localhost"
  private val tcpFlow: Flow[ByteString, ByteString, Future[Tcp.OutgoingConnection]] =
    Tcp().outgoingConnection(address, 9999)

  private val maybe: Source[Nothing, Promise[Option[Nothing]]] = Source.maybe

  val source: Source[ByteString, NotUsed] =
    Source.single(ByteString(message))
      .concat(maybe)
      .via(tcpFlow)

//  val result: Future[Done] = source.runWith(Sink.foreach(println))

//  result.onComplete(_ => {
//    println("done")
//  })

  val result = source.runFold(ByteString.empty)(_ ++ _)

  result.onComplete {
    case Success(successResult) =>
      println(s"Result: " + successResult.utf8String + successResult.length)
      println("Shutting down client")
      system.terminate()
    case Failure(e) =>
      println("Failure: " + e.getMessage)
      system.terminate()
  }
}
