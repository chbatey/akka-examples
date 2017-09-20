package info.batey.akka.typed

import akka.typed.{ActorSystem, Behavior}
import akka.typed.testkit.{EffectfulActorContext, Inbox}
import info.batey.akka.typed.Examples.SayHelloToMe
import org.scalatest._

class ExamplesSpec extends WordSpec with Matchers with BeforeAndAfterAll {
  val system = ActorSystem(Behavior.empty, "HelloWorldSpec")

  override def afterAll(): Unit = {
    system.terminate()
  }

  "responding to sender" must {
    "be testable with a MailBox" in {
      val mailbox = Inbox[String]("hello mailbox")
      run(Examples.respondingToSender, SayHelloToMe(mailbox.ref))
      mailbox.receiveMsg() should equal("hello")
    }
  }

  def run[T](b: Behavior[T], msg: T): EffectfulActorContext[T] = {
    val ctx = new EffectfulActorContext[T]("test ctx", b, 1, system)
    ctx.run(msg)
    ctx
  }
}
