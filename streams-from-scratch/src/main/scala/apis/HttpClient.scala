package apis

import akka.NotUsed
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString

import scala.concurrent.Future

object HttpClientFutureAndSource {
  case class HttpResponse(headers: Map[String, String], body: Source[ByteString, NotUsed])
  // Future represents when the http response initially comes back
  // Body is sent back via a Source of bytes
  def httpRequest(url: String): Future[HttpResponse] = ???
}

object HttpClientSink {

  // Client will write the response into the sink
  def httpRequest(url: String, body: Sink[ByteString, NotUsed]): Unit = ???
}

object HttpClientApp {

}