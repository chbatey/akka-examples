package info.batey.akka.streams.grouping

import java.util.UUID

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import akka.stream.stage._

object Grouping extends App {

  implicit val system = ActorSystem()
  implicit val materialiser = ActorMaterializer()

  case class Session(id: String, timestamp: Long)

  // in chunks of 10

  val fakeData: Source[Session, NotUsed] = Source(1 to 100)
    .map(i => Session(UUID.randomUUID().toString, i))

//   GroupBy won't work as the number of sub streams are unbounded
//  fakeData.groupBy(???, ???)

  // Batch not what we want either, as it is only for when the down stream is slower
//  fakeData.batch(???, ???)

//  fakeData.groupedWithin()

}
/*
class Batcher[I, O](val chunkSize: Int) extends GraphStage[FlowShape[I, O]] {
  val in = Inlet[I]("Chunker.in")
  val out = Outlet[O]("Chunker.out")
  override val shape = FlowShape.of(in, out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {
    private var buffer =

    setHandler(out, new OutHandler {
      override def onPull(): Unit = {
        if (isClosed(in)) emitChunk()
        else pull(in)
      }
    })
    setHandler(in, new InHandler {
      override def onPush(): Unit = {
        val elem = grab(in)
        buffer ++= elem
        emitChunk()
      }

      override def onUpstreamFinish(): Unit = {
        if (buffer.isEmpty) completeStage()
        else {
          // There are elements left in buffer, so
          // we keep accepting downstream pulls and push from buffer until emptied.
          //
          // It might be though, that the upstream finished while it was pulled, in which
          // case we will not get an onPull from the downstream, because we already had one.
          // In that case we need to emit from the buffer.
          if (isAvailable(out)) emitChunk()
        }
      }
    })

    private def emitChunk(): Unit = {
      if (buffer.isEmpty) {
        if (isClosed(in)) completeStage()
        else pull(in)
      } else {
        val (chunk, nextBuffer) = buffer.splitAt(chunkSize)
        buffer = nextBuffer
        push(out, chunk)
      }
    }

  }
}
*/
