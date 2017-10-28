package info.batey.akka.cluster

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.http.management.ClusterHttpManagement
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}
import akka.http.scaladsl._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import info.batey.akka.cluster.Counter.{EntityEnvelope, Get, Increment}

import scala.concurrent.Future
import scala.io.StdIn
import scala.concurrent.duration._

object HttpRoute {
  implicit val timeout = Timeout(1.second)
  def route(shardRegion: ActorRef) =
    path("counter" / IntNumber) {
      (i: Int) => {
        post {
          shardRegion ! EntityEnvelope(i, Increment)
          complete(StatusCodes.OK)
        } ~
        get {
          val count: Future[Int] = (shardRegion ? Get(i)).mapTo[Int]
          onComplete(count) { i =>
            complete(i.toString)
          }
        }
      }
    }
}

class Counter extends Actor {
  def receive = count(0)

  def count(i: Int): Receive = {
    case Increment =>
      context.become(count(i + 1))
    case g: Get =>
      sender() ! i
  }
}

object Counter {
  case object Increment
  case object Decrement
  final case class Get(counterId: Long)
  final case class EntityEnvelope(id: Long, payload: Any)

  val numberOfShards = 5

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case EntityEnvelope(id, payload) => (id.toString, payload)
    case msg @ Get(id) => (id.toString, msg)
  }

  val extractShardId: ShardRegion.ExtractShardId = {
    case EntityEnvelope(id, _) ⇒ (id % numberOfShards).toString
    case Get(id)               ⇒ (id % numberOfShards).toString
  }
}

object BasicShardingApp extends App {

  implicit val system = ActorSystem("sharding-app")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  ClusterHttpManagement(Cluster(system)).start()

  val counterRegion: ActorRef = ClusterSharding(system).start(
    typeName = "Counter",
    entityProps = Props[Counter],
    settings = ClusterShardingSettings(system),
    extractEntityId = Counter.extractEntityId,
    extractShardId = Counter.extractShardId)


  val bindingFuture = Http().bindAndHandle(HttpRoute.route(counterRegion), "localhost", 8080)

  println(s"Server online at http://localhost:8080/")

  StdIn.readLine()

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

}
