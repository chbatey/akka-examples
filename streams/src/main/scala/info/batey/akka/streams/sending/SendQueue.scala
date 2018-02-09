package info.batey.akka.streams.sending

import akka.stream.scaladsl.Source

object SendQueue {

  Source.queue(10, ???)


}
