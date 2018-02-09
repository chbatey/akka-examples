package info.batey.akka.persistence

import akka.Done
import akka.actor.{ActorSystem, Props}
import akka.persistence.{PersistentActor, RecoveryCompleted}
import akka.pattern._
import akka.persistence.journal.Tagged
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.Await

object Counter {
  case object Increment
  case object Incremented
  case object GetValue
}
class Counter(val persistenceId: String) extends PersistentActor {

  import Counter._

  var counter = 0

  def applyEvent(e: Incremented.type): Unit = {
    println(e)
    counter += 1
    println(s"Current state: $counter")
  }

  def receiveRecover: Receive = {
    case e: Incremented.type => applyEvent(e)
    case e: RecoveryCompleted =>
      println("Recovery completed: " + e)
    case e => throw new RuntimeException("wtf" + e)
  }

  def receiveCommand: Receive = {
    case Increment => persist(Tagged(Incremented, Set("myTag"))) { e =>
      applyEvent(e.payload.asInstanceOf[Incremented.type])
      sender() ! Done
    }
    case GetValue => sender() ! counter
  }
}

object BasicIncrementApp extends App {

  import Counter._

  val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val timeout = Timeout(10.second)
  val a = system.actorOf(Props(new Counter("a1")))

  val startingState = Await.result(a ? GetValue, 10.second)
  println(s"Starting state: $startingState")

  Await.ready(a ? Increment, 10.second)
//  Await.ready(a ? Increment, 10.second)
//  Await.ready(a ? Increment, 10.second)

  Thread.sleep(10000)

  system.terminate()
}
