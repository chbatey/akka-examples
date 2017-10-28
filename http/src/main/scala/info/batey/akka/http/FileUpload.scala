package info.batey.akka.http

import akka.Done
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.RequestContext
import akka.http.scaladsl.server.directives.FileInfo
import akka.stream.scaladsl.{Framing, Keep, Sink, Source}
import akka.util.ByteString

import scala.concurrent.Future

object FileUpload {
  val files = extractRequestContext { ctx: RequestContext =>
    implicit val materializer = ctx.materializer
    fileUpload("gc") {
      case (metadata: FileInfo, bytes: Source[ByteString, Any]) =>

        val lines: Source[String, Any] = bytes.via(Framing.delimiter(ByteString("\n"), 1024)).map(_.utf8String)

        val done: Future[Done] = lines.toMat(Sink.foreach(println))(Keep.right)
          .run()

        onSuccess(done) { _ => complete(StatusCodes.OK) }
    }
  }

}
