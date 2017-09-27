package info.batey.actors

import akka.actor.{Actor, ActorIdentity, ActorLogging, ActorSelection, Identify, Props}
import akka.remote.testkit._
import akka.testkit.ImplicitSender
import info.batey.actors.MultiNodeSample.Greeter
import info.batey.fixtures.STMultiNodeSpec

object SampleConfig extends MultiNodeConfig {
  val greeterNode = role("greeter")
  val greetedNode = role("greeted")
}


class DifferentCodeSpecMultiJvmNode1 extends MultiNodeSpec(SampleConfig) with STMultiNodeSpec with ImplicitSender {

  def initialParticipants = roles.size

  "greeter" must {
    "must say hello via selection" in {
      system.actorOf(Props[Greeter], "greeter")
      // Both nodes will block here until they both get here.
      enterBarrier("greeter ready - 1")
    }

    "must say hello via an actor ref" in {
      system.actorOf(Props[Greeter], "greeter-ref")
      enterBarrier("greeter ready - 2")

    }
  }
}

class DifferentCodeSpecMultiJvmNode2 extends MultiNodeSpec(SampleConfig) with STMultiNodeSpec with ImplicitSender {

  import SampleConfig._

  def initialParticipants = roles.size

  "greeter" must {
    "must say hello via selection" in {
      enterBarrier("greeter ready - 1")
      val greeter: ActorSelection = system.actorSelection(node(greeterNode) / "user" / "greeter")
      greeter ! "Christopher"
      expectMsg("Why hello Christopher")
    }

    "must say hello via an actor ref" in {
      enterBarrier("greeter ready - 2")
      val greeter: ActorSelection = system.actorSelection(node(greeterNode) / "user" / "greeter-ref")
      greeter ! Identify()
      val (ActorIdentity(_, Some(greeterRef))) = expectMsgType[ActorIdentity]
      greeterRef ! "Christopher"
      expectMsg("Why hello Christopher")
    }
  }
}

object MultiNodeSample {
  class Greeter extends Actor with ActorLogging {
    def receive = {
      case s: String =>
        sender() ! s"Why hello $s"
    }
  }
}

