package info.batey.akka.streams

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, SubstreamCancelStrategy}
import akka.stream.scaladsl.{RunnableGraph, Sink, Source, SubFlow}

object SubStreams extends App {

  implicit val system = ActorSystem()
  implicit val materialiser = ActorMaterializer()
  val grouped = Source(1 to 10).groupBy(3, _ % 3)
  val runnable: RunnableGraph[NotUsed] = grouped.to(Sink.foreach({ i =>
    println(i)
  }))
  //  runnable.run


  val words = Source(List("one", "two", "three", "\n", "two", "three", "\n", "three", "\n", "four", "four", "four", "four"))

  // word count
  val counts: Source[(String, Int), NotUsed] = words
    .groupBy(100, identity)
    //transform each element to pair with number of words in it
    .map(_ -> 1)
    // add counting logic to the streams
    .reduce((l, r) => (l._1, l._2 + r._2))
    // get a stream of word counts
    .mergeSubstreams

  //  counts.runForeach(println)

  //  Thread.sleep(2 * 1000)

  println("Line count")
  val lineCount = words.splitAfter(_ == "\n")
    .filter(_ != "\n")
    .map(_ => 1)
    .reduce(_ + _)

  lineCount.to(Sink.foreach(println)).run

}
