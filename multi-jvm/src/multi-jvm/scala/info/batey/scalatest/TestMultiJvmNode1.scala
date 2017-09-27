package info.batey.scalatest

import org.scalatest.{Matchers, WordSpec}

class TestMultiJvmNode1 extends WordSpec with Matchers {
  "node 1" must {
    "do something exciting" in {
      println("I am node 1")
    }
  }
}

class TestMultiJvmNode2 extends WordSpec with Matchers {
  "node 2" must {
    "do something else" in {
      new Exception().printStackTrace(System.out)
      println("Node 1 sucks")
    }
  }
}
