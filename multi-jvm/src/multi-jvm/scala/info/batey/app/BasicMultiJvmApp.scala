package info.batey.app

/**
  * To run: stb multi-jvm:run info.batey.app.Sample
  */

object SampleMultiJvmNode1 {
  def main(args: Array[String]): Unit = {
    println("Hello from node 1")
  }
}

object SampleMultiJvmNode2 {
  def main(args: Array[String]): Unit = {
    println("hello from node 2")
  }
}

