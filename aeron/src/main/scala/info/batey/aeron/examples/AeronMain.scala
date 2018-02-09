package info.batey.aeron.examples

import java.util.concurrent.TimeUnit

import io.aeron.driver.MediaDriver
import io.aeron.{Aeron, Image, Publication}
import Config._
import io.aeron.logbuffer.Header
import org.agrona.DirectBuffer
import org.agrona.concurrent.BusySpinIdleStrategy

import scala.io.StdIn

object Config {
  val channel = "aeron:udp?endpoint=localhost:5555"
  val streamId = 1
}

object AeronDriver extends App {
  val mediaDriver = MediaDriver.launch()
  println("Media driver running...")
}

object AeronServer extends App {
  val context = new Aeron.Context()
  val aeron = Aeron.connect(context)

  val pub: Publication = aeron.addPublication(channel, streamId)

  import org.agrona.BufferUtil
  import org.agrona.concurrent.UnsafeBuffer

  val buffer = new UnsafeBuffer(BufferUtil.allocateDirectAligned(256, 64))

  println("Press enter to start sending...")
  StdIn.readLine()

  (1 to 1000) foreach { i =>
    val message = "Hello World! " + i
    val messageBytes = message.getBytes
    buffer.putBytes(0, messageBytes)

    val result = pub.offer(buffer)
    if (result < 0L) {
      if (result == Publication.BACK_PRESSURED) {
        System.out.println("Offer failed due to back pressure")
      }
      else if (result == Publication.NOT_CONNECTED) {
        System.out.println("Offer failed because publisher is not connected to subscriber")
      }
      else if (result == Publication.ADMIN_ACTION) {
        System.out.println("Offer failed because of an administration action in the system")
      }
      else if (result == Publication.CLOSED) {
        System.out.println("Offer failed publication is closed")
      }
      else if (result == Publication.MAX_POSITION_EXCEEDED) {
        System.out.println("Offer failed due to publication reaching max position")
      }
      else {
        System.out.println("Offer failed due to unknown reason")
      }
    }
    else {
      System.out.println("yay!")
    }

    if (!pub.isConnected()) {
      System.out.println("No active subscribers detected")
    }

    Thread.sleep(TimeUnit.SECONDS.toMillis(1));
  }
}

object AeronClient extends App {
  val context = new Aeron.Context()
    .availableImageHandler(image => println(s"Image available $image"))
    .unavailableImageHandler(image => println(s"Image unavailable: $image"))

  import io.aeron.logbuffer.FragmentHandler
  import java.util.concurrent.atomic.AtomicBoolean

  val fragmentHandler: FragmentHandler = printStringMessage(streamId)
  val running = new AtomicBoolean(true)
  val aeron = Aeron.connect(context)

  val sub = aeron.addSubscription(channel, streamId)

  subscriberLoop(fragmentHandler, image => printEndOfStreamImage(image), 100, running, new BusySpinIdleStrategy())(sub)

  import io.aeron.EndOfStreamHandler
  import io.aeron.Subscription
  import io.aeron.logbuffer.FragmentHandler
  import org.agrona.LangUtil
  import org.agrona.concurrent.IdleStrategy
  import java.util.concurrent.atomic.AtomicBoolean
  import java.util.function.Consumer

  def subscriberLoop(fragmentHandler: FragmentHandler, endOfStreamHandler: EndOfStreamHandler, limit: Int, running: AtomicBoolean, idleStrategy: IdleStrategy) = (subscription: Subscription) => {
    def foo(subscription: Subscription) = {
      try {
        var reachedEos = false
        while ( {
          running.get
        }) {
          val fragmentsRead = subscription.poll(fragmentHandler, limit)
          if (0 == fragmentsRead) if (!reachedEos && subscription.pollEndOfStreams(endOfStreamHandler) > 0) reachedEos = true
          idleStrategy.idle(fragmentsRead)
        }
      } catch {
        case ex: Exception =>
          LangUtil.rethrowUnchecked(ex)
      }
    }

    foo(subscription)
  }

  def printStringMessage(streamId: Int): FragmentHandler = (buffer: DirectBuffer, offset: Int, length: Int, header: Header) => {
    def foo(buffer: DirectBuffer, offset: Int, length: Int, header: Header) = {
      val data = new Array[Byte](length)
      buffer.getBytes(offset, data)
      System.out.println(String.format("Message to stream %d from session %d (%d@%d) <<%s>>", streamId, header.sessionId, length, offset, new String(data)))
    }
    foo(buffer, offset, length, header)
  }

  import io.aeron.Subscription

  def printEndOfStreamImage(image: Image): Unit = {
    val subscription = image.subscription
    System.out.println(String.format("End Of Stream image on %s streamId=%d sessionId=%d from %s", subscription.channel, subscription.streamId, image.sessionId, image.sourceIdentity))
  }
}
