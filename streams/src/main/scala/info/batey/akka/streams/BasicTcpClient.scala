package info.batey.akka.streams

import java.net.InetSocketAddress

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Tcp, _}
import akka.util.ByteString

import scala.concurrent.{Future, Promise}
import scala.io.StdIn

object BasicTcpClient extends App {

  implicit val system = ActorSystem()
  implicit val materialiser = ActorMaterializer()
  implicit val ec = system.dispatcher

  // can test with netcat: nc -l 9999
  val msg = "Welcome"
  val inet = new InetSocketAddress("localhost", 9999)

  val tcpFlow: Flow[ByteString, ByteString, Future[Tcp.OutgoingConnection]] = Tcp().outgoingConnection(inet)

  // This source sends a welcome message and then uses a promise from Source.maybe
  // to allow control over when the source completes, either with a final msg or no msg
  val source: Source[ByteString, Promise[Option[ByteString]]] =
    Source.single(ByteString(msg)).concatMat(Source.maybe[ByteString])(Keep.right)


  // This flow completes the stream if a msg END\n is received
  val untilEnd: Flow[ByteString, ByteString, NotUsed] = Flow[ByteString].takeWhile(_ != ByteString("END\n"))

  val (promise, result) =
    source
      .via(tcpFlow)
      .via(untilEnd)
      .toMat(Sink.foreach(msg => {
        println("Msg from server: " + msg)
      }))(Keep.both).run

  result onComplete { result =>
    println(s"COMPLETE!! $result")
    system.terminate()
  }

  var goodByteMessage = StdIn.readLine()
  if (goodByteMessage.isEmpty) {
    // None means close without sending a msg to the sever
    promise.success(None)
  } else {
    // Some means send a message before closing
    promise.success(Some(ByteString(goodByteMessage)))
  }
}
