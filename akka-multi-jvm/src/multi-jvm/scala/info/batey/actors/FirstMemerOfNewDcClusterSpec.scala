package info.batey.actors

import java.util.concurrent.TimeUnit

import akka.cluster.{Cluster, MemberStatus}
import akka.remote.testkit._
import akka.testkit.ImplicitSender
import com.typesafe.config.ConfigFactory
import info.batey.fixtures.STMultiNodeSpec

import scala.concurrent.duration._

object FirstMemberOfNewDcClusterSpec extends MultiNodeConfig {
  commonConfig(ConfigFactory.parseString(
    s"""
    akka.actor.provider = cluster
    akka.actor.warn-about-java-serializer-usage = off
    akka.coordinated-shutdown.terminate-actor-system = off
    akka.cluster {
      jmx.enabled                         = off
    }
    akka.cluster.multi-data-center {
       cross-data-center-gossip-probability = 0.2
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

  nodeConfig(one, two, three) {
    ConfigFactory.parseString("akka.cluster.multi-data-center.self-data-center = DC1")
  }

  nodeConfig(four, five) {
    ConfigFactory.parseString("akka.cluster.multi-data-center.self-data-center = DC2")
  }

}

class FirstMemberOfNewDcClusterSpecMultiJvmNode1 extends FirstMemberOfNewDcClusterSpec
class FirstMemberOfNewDcClusterSpecMultiJvmNode2 extends FirstMemberOfNewDcClusterSpec
class FirstMemberOfNewDcClusterSpecMultiJvmNode3 extends FirstMemberOfNewDcClusterSpec
class FirstMemberOfNewDcClusterSpecMultiJvmNode4 extends FirstMemberOfNewDcClusterSpec
class FirstMemberOfNewDcClusterSpecMultiJvmNode5 extends FirstMemberOfNewDcClusterSpec

abstract class FirstMemberOfNewDcClusterSpec extends MultiNodeSpec(FirstMemberOfNewDcClusterSpec) with STMultiNodeSpec with ImplicitSender {

  import FirstMemberOfNewDcClusterSpec._

  def initialParticipants = roles.size
  val cluster = Cluster(system)

  "Something" must {
    "join node one" in {
      runOn(one) {
        cluster.join(node(one).address)
      }
      enterBarrier("node one up")
    }

    "all dc1 nodes join" in {
      runOn(two, three) {
        cluster.join(node(one).address)
      }
    }

    "all dc1 nodes see each other as up" in {
      runOn(two, three) {
        within(20.seconds) {
          awaitAssert({
            cluster.state.members.filter(_.status == MemberStatus.Up) should have size 3
          })
        }
      }
      enterBarrier("dc1 fully up")
    }

    "first member of new dc joins" in {
      runOn(four) {
        val startTime = System.nanoTime()
        log.info("Joining cluster")
        cluster.join(node(one).address)
        within(20.seconds) {
          awaitAssert({
            cluster.state.members.filter(_.status == MemberStatus.Up) should have size 4
            log.info("Joined, state {}", cluster.state)
          })
        }
        val totalTime = System.nanoTime() - startTime
        log.info("Joined and see every node as up, took: {}", Duration(totalTime, TimeUnit.NANOSECONDS).toMillis)
      }
      enterBarrier("node 4 joined dc")

    }

    "second member of new dc joins" in {
      runOn(five) {
        val startTime = System.nanoTime()
        log.info("Joining cluster")
        cluster.join(node(one).address)
        within(20.seconds) {
          awaitAssert({
            cluster.state.members.filter(_.status == MemberStatus.Up) should have size 5
            log.info("Joined, state {}", cluster.state)
          })
        }
        val totalTime = System.nanoTime() - startTime
        log.info("Joined and see every node as up, took: {}", Duration(totalTime, TimeUnit.NANOSECONDS).toMillis)
      }
      enterBarrier("node 5 joined dc")
    }
  }
}

