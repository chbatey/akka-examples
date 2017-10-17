package info.batey.akka.persistence

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.persistence.PersistentActor
import akka.persistence.journal.{EventAdapter, EventSeq, SingleEventSeq, Tagged}
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.io.StdIn

object PersistenceApp extends App {

  case class Balance(balance: Long)

  sealed trait Command
  final case class Deposit(a: Long) extends Command
  final case class Withdraw(a: Long) extends Command
  final case object GetBalance extends Command

  sealed trait Event
  final case class Deposited(a: Long) extends Event
  final case class Withdrawn(a: Long) extends Event

  class BankAccount(val persistenceId: String) extends PersistentActor {
    var b = Balance(0)

    def applyEvent(evt: Event): Unit = {
      evt match {
        case Deposited(a) => b = b.copy(balance = b.balance + a)
        case Withdrawn(a) => b = b.copy(balance = b.balance - a)
      }
    }

    def receiveRecover: Receive = {
      case evt: Event => applyEvent(evt)
    }

    def receiveCommand: Receive = {
      case Deposit(amount) =>
        if (amount > 100) {
          persist(Tagged(Deposited(amount), Set("massive"))) { tagged =>
            applyEvent(tagged.payload.asInstanceOf[Deposited])
          }
        } else {
          persist(Deposited(amount))(applyEvent)
        }
      case Withdraw(amount) => persist(Withdrawn(amount))(applyEvent)
      case GetBalance =>
        println("Sending the state")
        sender() ! b
    }
  }


  val system = ActorSystem("Persistence")
  implicit val timeout: Timeout = Timeout(10.seconds)
  val pa: ActorRef = system.actorOf(Props(classOf[BankAccount], "my-bank-no-types"))

  pa ! Deposit(10)
  pa ! Deposit(15)
  pa ! Deposit(1000)
  pa ! Withdraw(5)

  val balance: Future[Balance] = (pa ? GetBalance).mapTo[Balance]

  println(Await.result(balance, 11.seconds))

  println("Press enter to exit")
  StdIn.readLine()
  println("And we're done")
}
