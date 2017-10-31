package info.batey.actors

import akka.actor.{Actor, ActorIdentity, ActorRef, ActorSystem, Identify, Props}
import akka.pattern.{AskTimeoutException, ask, pipe}
import akka.remote.testconductor.RoleName
import akka.remote.testkit.{MultiNodeConfig, MultiNodeSpec}
import akka.testkit.ImplicitSender
import info.batey.actors.FailureSample.{CachingClient, UnreliableServer}
import info.batey.actors.FailureSample.UnreliableServer._
import info.batey.fixtures.STMultiNodeSpec
import akka.actor.Actor
import akka.util.Timeout
import info.batey.actors.FailureSample.CachingClient.Response

import scala.concurrent.duration._
import scala.concurrent.duration.{Duration, FiniteDuration}

object FailureConfig extends MultiNodeConfig {
  val clientNode = role("client")
  val serverNode = role("server")
}

class FailureSampleMultiJvmClient extends FailureSample
class FailureSampleMultiJvmServer extends FailureSample

class FailureSample extends MultiNodeSpec(FailureConfig) with STMultiNodeSpec with ImplicitSender {

  import FailureConfig._

  def initialParticipants = roles.size

  "a caching client" must {
    "get from the server" in {
      runOn(clientNode) {
        enterBarrier("server ready 1")
        val server = identify(serverNode, "us1")
        val client = system.actorOf(Props(classOf[CachingClient], server))

        client ! Get("1")

        expectMsg(Response(Value("apple"), stale = false))
      }

      runOn(serverNode) {
        val server = system.actorOf(Props[UnreliableServer], "us1")
        println(s"Server started: $server")
        enterBarrier("server ready 1")
      }

      enterBarrier("done 1")
    }

    "use a cached result if server doesn't response" in {
      runOn(clientNode) {
        enterBarrier("server ready 1")
        val server = identify(serverNode, "us-slow")
        val client = system.actorOf(Props(classOf[CachingClient], server))
        client ! Get("1")
//        expectMsg(Response(Value("apple"), )
        //FIXME finish
      }

      runOn(serverNode) {
        val server = system.actorOf(Props(classOf[UnreliableServer], 10.seconds), "us-slow")
        println(s"Server started: $server")
        enterBarrier("server ready 1")
      }

      enterBarrier("done 1")
    }

    "use a cached result if the server is down" in {

    }
  }


  def identify(role: RoleName, actorName: String): ActorRef = {
    val path = node(role) / "user" / actorName
    println(s"Looking up ActorRef for path $path")
    system.actorSelection(path) ! Identify(actorName)
    val actorIdentity = expectMsgType[ActorIdentity]
    assert(actorIdentity.ref.isDefined, s"Unable to Identify actor: $actorName on node: $role")
    actorIdentity.ref.get
  }
}

object FailureSample {

  class CachingClient(server: ActorRef) extends Actor {

    import context._

    implicit val timeout: Timeout = Timeout(1.second)

    def receive = state(Map.empty[String, Value])

    def state(s: Map[String, Value]): Receive = {
      case get @ Get(key) =>
        val replyTo = sender()
        (server ? get).mapTo[Value]
          .map(Response(_, stale = false))
          .recover {
            case _: AskTimeoutException =>
              s.getOrElse(key, NotFound)
          }
          .pipeTo(replyTo)
    }
  }

  object CachingClient {
    case class Response(value: Value, stale: Boolean)
  }

  class UnreliableServer(delay: FiniteDuration) extends Actor {

    import context._

    def this() {
      this(Duration.Zero)
    }

    val database = Map(
      "1" -> Value("apple"),
      "2" -> Value("coffee"),
    )

    def receive: Receive = {
      case Get(key) =>
        val response = database.getOrElse(key, NotFound)
        delay match {
          case Duration.Zero =>
            sender() ! response
          case other =>
            context.system.scheduler.scheduleOnce(other, sender(), response)
        }
    }
  }

  object UnreliableServer {
    case class Get(key: String)

    sealed trait Response
    final case class Value(a: String) extends Response
    final case object NotFound extends Response
  }
}

