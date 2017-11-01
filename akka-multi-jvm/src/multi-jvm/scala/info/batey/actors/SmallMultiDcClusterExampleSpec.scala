package info.batey.actors

import akka.cluster.{Cluster, MemberStatus}
import akka.remote.testkit._
import akka.testkit.ImplicitSender
import com.typesafe.config.ConfigFactory
import info.batey.fixtures.STMultiNodeSpec

import scala.concurrent.duration._

object SmallMultiDcClusterExampleSpec extends MultiNodeConfig {
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

  nodeConfig(one, two) {
    ConfigFactory.parseString("akka.cluster.multi-data-center.self-data-center = DC1")
  }

  nodeConfig(three, four) {
    ConfigFactory.parseString("akka.cluster.multi-data-center.self-data-center = DC2")
  }

}

class SmallMultiDcClusterExampleSpecMultiJvmNode1 extends SmallMultiDcClusterExampleSpec
class SmallMultiDcClusterExampleSpecMultiJvmNode2 extends SmallMultiDcClusterExampleSpec
class SmallMultiDcClusterExampleSpecMultiJvmNode3 extends SmallMultiDcClusterExampleSpec
class SmallMultiDcClusterExampleSpecMultiJvmNode4 extends SmallMultiDcClusterExampleSpec

abstract class SmallMultiDcClusterExampleSpec extends MultiNodeSpec(SmallMultiDcClusterExampleSpec) with STMultiNodeSpec with ImplicitSender {

  import SmallMultiDcClusterExampleSpec._

  def initialParticipants = roles.size
  val cluster = Cluster(system)

  "Something" must {
    "join node one" in {
      runOn(one) {
        cluster.join(node(one).address)
      }
      enterBarrier("node one up")
    }

    "all nodes join" in {
      runOn(two, three, four) {
        cluster.join(node(one).address)
      }
    }

    "wait for all nodes to be up" in {
      within(20.seconds) {
        awaitAssert({
          cluster.state.members.filter(_.status == MemberStatus.Up) should have size 4
        })
      }
      log.info("Node sees all other nodes as up")
      enterBarrier("all done")
    }
  }
}

