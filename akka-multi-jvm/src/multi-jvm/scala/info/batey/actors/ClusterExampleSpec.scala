package info.batey.actors

import java.util.UUID

import akka.cluster.{Cluster, MemberStatus}
import akka.remote.testkit._
import akka.testkit.ImplicitSender
import com.typesafe.config.ConfigFactory
import info.batey.fixtures.STMultiNodeSpec

import scala.concurrent.duration._

object ClusterExampleSpec extends MultiNodeConfig {
  commonConfig(ConfigFactory.parseString(
    s"""
    akka.actor.provider = cluster
    akka.actor.warn-about-java-serializer-usage = off
    akka.coordinated-shutdown.terminate-actor-system = off
    akka.cluster {
      jmx.enabled                         = off
      gossip-interval                     = 200 ms
      leader-actions-interval             = 200 ms
      unreachable-nodes-reaper-interval   = 500 ms
      periodic-tasks-initial-delay        = 300 ms
      publish-stats-interval              = 0 s # always, when it happens
      failure-detector.heartbeat-interval = 500 ms
    }
    akka.loglevel = INFO
    akka.log-dead-letters = off
    akka.log-dead-letters-during-shutdown = off
    akka.remote {
      log-remote-lifecycle-events = off
      artery.advanced.flight-recorder {
        enabled=on
        destination=target/flight-recorder-${UUID.randomUUID().toString}.afr
      }
    }
    akka.loggers = ["akka.testkit.TestEventListener"]
    akka.test {
      single-expect-default = 5 s
    }

    """))

  val one = role("one")
  val two = role("two")
}

class ClusterExampleSpecMultiJvmNode1 extends ClusterExampleSpec
class ClusterExampleSpecMultiJvmNode2 extends ClusterExampleSpec

abstract class ClusterExampleSpec extends MultiNodeSpec(ClusterExampleSpec) with STMultiNodeSpec with ImplicitSender {

  import ClusterExampleSpec._

  def initialParticipants = roles.size
  val cluster = Cluster(system)
  "Something" must {
    "join node one" in {
      runOn(one) {
        cluster.join(node(one).address)
      }
    }


    enterBarrier("node one up")

    "join node two" in {

      runOn(two) {
        cluster.join(node(one).address)
      }

      within(20.seconds) {
        awaitAssert(cluster.state.members.filter(_.status == MemberStatus.Up) should have size 2)
      }

    }

    "do other stuff" in {
      enterBarrier("node two up")
      println(cluster.state)
      runOn(one) {
        cluster.leave(node(two).address)
        println("Node two has left")
      }

      enterBarrier("two is now gone")
      println("Node two should have left")

      Thread.sleep(10 * 1000)

      enterBarrier("finished sleep")
    }
  }
}

