package info.batey.akka.streams.tcp;

import akka.*;
import akka.actor.ActorSystem;
import akka.japi.function.Function;
import akka.stream.*;
import akka.stream.javadsl.*;
import akka.util.*;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/*
This is implementing receiving incoming (upstream) messages and sending ACK/NACK back to downstream.

Now what I need to do is to get access to SourceQueueWithComplete for downstream to be able to push messages.

I had a similar query in case 8879 for TCP client by using:
.mergeMat(Source.<VDMessageRoot>queue(5, OverflowStrategy.backpressure()), Keep.right());

How can I integrate mergeMat in this case?
 */
public class Something {

  public static CompletionStage<String> handleReceivedMessage(String input) {
    return CompletableFuture.completedFuture("hello");
  }

  public static Flow<ByteString, String, NotUsed> getDecoder() {
    return Flow.fromFunction((bs) -> "decoded");
  }

  public static Flow<String, ByteString, NotUsed> getEncoder() {
    return Flow.fromFunction((s) -> ByteString.fromArray(s.getBytes()));
  }

  public static void main(String[] args) throws Exception {
    ActorSystem system = ActorSystem.create();
    ActorMaterializer materializer = ActorMaterializer.create(system);

    String host = "localhost";
    int port = 9091;

    final Source<Tcp.IncomingConnection, CompletionStage<Tcp.ServerBinding>> connections =
      Tcp.get(system).bind(host, port);

    final Function<Throwable, Supervision.Directive> decider = exc -> {
//      logger.error("decider {}",exc);
      return Supervision.resume();
    };

    Source<String, SourceQueueWithComplete<String>> sourceQueue = Source.queue(10, OverflowStrategy.backpressure());

    Flow<ByteString, ByteString, SourceQueueWithComplete<String>> flow = Flow.of(ByteString.class)
      .via(getDecoder())
      .mapAsync(1, Something::handleReceivedMessage)
      .mergeMat(sourceQueue, Keep.right())
      .via(getEncoder());



//    CompletionStage<Tcp.ServerBinding> something = connections.to(Sink.foreach(connection -> {
//        connection.handleWith(flow, materializer);
//      }
//    )).run(materializer);

    // Loses the materialised value
//    CompletionStage<Done> what = connections.runForeach(c -> {
//      c.handleWith(flow, materializer);
//    }, materializer);

    connections.runForeach(c -> {
      SourceQueueWithComplete<String> sourceQ = c.handleWith(flow, materializer);

      sourceQ.offer("hello");
      sourceQ.offer("how");
      sourceQ.offer("are");
      sourceQ.offer("you");

      }, materializer);



    System.out.println("Running");
    System.in.read();
    System.out.println("Shutting down");


  }
}
