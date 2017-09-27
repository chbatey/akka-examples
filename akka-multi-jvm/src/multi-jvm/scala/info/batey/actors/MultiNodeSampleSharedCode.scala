package info.batey.actors

import akka.actor.{Actor, ActorIdentity, ActorLogging, ActorSelection, Identify, Props}
import akka.remote.testkit._
import akka.testkit.ImplicitSender
import info.batey.actors.MultiNodeSharedCodeSample.Greeter
import info.batey.fixtures.STMultiNodeSpec

object SharedCodeConfig extends MultiNodeConfig {
  val greeterNode = role("greeter")
  val greetedNode = role("greeted")
}

/*
 * A common way to use multi-jvm is to have the same class be run on all nodes
 * and use `runOn`
 */
class SharedCodeSampleSpecMultiJvmNode1 extends SharedCodeSample
class SharedCodeSampleSpecMultiJvmNode2 extends SharedCodeSample

class SharedCodeSample extends MultiNodeSpec(SharedCodeConfig) with STMultiNodeSpec with ImplicitSender {
  import SharedCodeConfig._
  def initialParticipants = roles.size
  "greeter" must {
    "must say hello via selection" in {
      val greeterName = "greeter"
      runOn(greeterNode) {
        system.actorOf(Props[Greeter], greeterName)
        // Both nodes will block here until they both get here.
        enterBarrier("greeter ready - 1")
      }

      runOn(greetedNode) {
        // Both nodes will block here until they both get here.
        enterBarrier("greeter ready - 1")
        val greeter: ActorSelection = system.actorSelection(node(greeterNode) / "user" / greeterName)
        greeter ! "Christopher"
        expectMsg("Why hello Christopher")
      }
    }

    "must say hello via an actor ref" in {
      val greeterName = "greeter-ref"
      runOn(greeterNode) {
        system.actorOf(Props[Greeter], greeterName)
        enterBarrier("greeter ready - 2")
      }

      runOn(greetedNode) {
        // Both nodes will block here until they both get here.
        enterBarrier("greeter ready - 2")
        val greeter: ActorSelection = system.actorSelection(node(greeterNode) / "user" / greeterName)
        greeter ! Identify()
        val (ActorIdentity(_, Some(greeterRef))) = expectMsgType[ActorIdentity]
        greeterRef ! "Christopher"
        expectMsg("Why hello Christopher")
      }
    }
  }
}

object MultiNodeSharedCodeSample {
  class Greeter extends Actor with ActorLogging {
    def receive = {
      case s: String =>
        println("I shall greet! " + sender())
        sender() ! s"Why hello $s"
    }
  }
}

