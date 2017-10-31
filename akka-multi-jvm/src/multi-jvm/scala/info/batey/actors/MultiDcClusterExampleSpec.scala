package info.batey.actors

import akka.cluster.{Cluster, MemberStatus}
import akka.remote.testkit._
import akka.testkit.ImplicitSender
import com.typesafe.config.ConfigFactory
import info.batey.fixtures.STMultiNodeSpec

import scala.concurrent.duration._

object MultiDcClusterExampleSpec extends MultiNodeConfig {
  commonConfig(ConfigFactory.parseString(
    s"""
    akka.actor.provider = cluster
    akka.actor.warn-about-java-serializer-usage = off
    akka.coordinated-shutdown.terminate-actor-system = off
    akka.cluster {
      jmx.enabled                         = off
    }
    akka.cluster.multi-data-center {
       cross-data-center-gossip-probability = 0.5
    }
    akka.loglevel = INFO
    akka.log-dead-letters = off
    akka.log-dead-letters-during-shutdown = off
    akka.loggers = ["akka.testkit.TestEventListener"]
    """))

  val one = role("one")
  val two = role("two")
  val three = role("three")

  val four = role("four")
  val five = role("five")
  val six = role("six")

  val seven = role("seven")
  val eight = role("eight")
  val nine = role("nine")

  nodeConfig(one, two, three) {
    ConfigFactory.parseString("akka.cluster.multi-data-center.self-data-center = DC1")
  }

  nodeConfig(four, five, six) {
    ConfigFactory.parseString("akka.cluster.multi-data-center.self-data-center = DC2")
  }

  nodeConfig(seven, eight, nine) {
    ConfigFactory.parseString("akka.cluster.multi-data-center.self-data-center = DC3")
  }
}

class MultiDcClusterExampleSpecMultiJvmNode1 extends MultiDcClusterExampleSpec
class MultiDcClusterExampleSpecMultiJvmNode2 extends MultiDcClusterExampleSpec
class MultiDcClusterExampleSpecMultiJvmNode3 extends MultiDcClusterExampleSpec
class MultiDcClusterExampleSpecMultiJvmNode4 extends MultiDcClusterExampleSpec
class MultiDcClusterExampleSpecMultiJvmNode5 extends MultiDcClusterExampleSpec
class MultiDcClusterExampleSpecMultiJvmNode6 extends MultiDcClusterExampleSpec
class MultiDcClusterExampleSpecMultiJvmNode7 extends MultiDcClusterExampleSpec
class MultiDcClusterExampleSpecMultiJvmNode8 extends MultiDcClusterExampleSpec
class MultiDcClusterExampleSpecMultiJvmNode9 extends MultiDcClusterExampleSpec

abstract class MultiDcClusterExampleSpec extends MultiNodeSpec(MultiDcClusterExampleSpec) with STMultiNodeSpec with ImplicitSender {

  import MultiDcClusterExampleSpec._

  def initialParticipants = roles.size
  val cluster = Cluster(system)

  "Something" must {
    "join node one" in {
      runOn(one) {
        cluster.join(node(one).address)
      }
      enterBarrier("node one up")
    }

    "all nodes joi" in {

      runOn(two, three, four, five, six, seven, eight, nine) {
        cluster.join(node(one).address)
      }

    }

    "wait for all nodes to be up" in {
      within(20.seconds) {
        awaitAssert({
          cluster.state.members.filter(_.status == MemberStatus.Up) should have size 9
        })
      }
      enterBarrier("all done")
    }
  }

}

