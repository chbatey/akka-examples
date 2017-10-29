package info.batey.akka.http.websockets

import akka.NotUsed
import akka.actor.{Actor, ActorRef, ActorSystem, Props, Status}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws._
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl._
import org.reactivestreams.Publisher
import scala.concurrent.duration._

import scala.io.StdIn
import scala.util.Random

object WebSocketsServerApp extends App {

  implicit val system = ActorSystem()
  implicit val materialiser = ActorMaterializer()
  implicit val ec = system.dispatcher

  val (outActor: ActorRef, publisher: Publisher[TextMessage.Strict]) = Source.actorRef[TextMessage](1, OverflowStrategy.dropBuffer)
    .map((down) => {
      println("Gots msg: " + down)
      TextMessage(down + " a cat")
    }).toMat(Sink.asPublisher(false))(Keep.both).run()

  val name = s"cat-${Random.nextInt(1000)}"

  class ActorFlowActor(outActor: ActorRef) extends Actor {
    override def receive = {
      case msg =>
        println("Actor has msg " + msg)
        outActor ! msg
//        context.system.scheduler.schedule(1.second, 1.second, outActor, msg)
    }
  }


  val greeter2: Flow[Message, Message, NotUsed] = Flow.fromSinkAndSourceCoupled[Message, Message](
    Sink.actorRef(system.actorOf(Props(
      new ActorFlowActor(outActor)), name),
      Status.Success(())),
    Source.fromPublisher(publisher)
  )

  def greeter =
    Flow[Message].mapConcat {
      case tm: TextMessage =>
        TextMessage(Source.single("Hello ") ++ tm.textStream ++ Source.single("!")) :: Nil
      case bm: BinaryMessage =>
        // ignore binary messages but drain content to avoid the stream being clogged
        bm.dataStream.runWith(Sink.ignore)
        Nil
    }

   val x: Flow[Message, Message, NotUsed] = greeter.recover {
     case e: Throwable =>
       println(e)
       TextMessage("sorry")
   }

  val websocketRoute =
    path("greeter") {
      handleWebSocketMessages(x)
    }

  val bindingFuture = Http().bindAndHandle(websocketRoute, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")

//  StdIn.readLine() // let it run until user presses return
//  bindingFuture
//    .flatMap(_.unbind()) // trigger unbinding from the port
//    .onComplete(_ => system.terminate()) // and shutdown when done

}
