package info.batey.akka.persistence

import com.datastax.driver.core.utils.UUIDs

object Silly extends App {

  println(UUIDs.timeBased())
  println(UUIDs.timeBased())
  println(UUIDs.timeBased())
}
