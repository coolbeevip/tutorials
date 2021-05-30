package org.coolbeevip;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import io.vertx.core.Vertx;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("/hello")
public class GreetingResource {

  @Inject
  Vertx vertx;

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("{name}")
  public CompletionStage<String> greeting(@PathParam("name") String name) {
    // When complete, return the content to the client
    CompletableFuture<String> future = new CompletableFuture<>();

    long start = System.nanoTime();

    // Delay reply by 10ms
    vertx.setTimer(50, l -> {
      // Compute elapsed time in milliseconds
      long duration = MILLISECONDS.convert(System.nanoTime() - start, NANOSECONDS);

      // Format message
      String message = String.format("Hello %s! (%d ms)%n", name, duration);

      // Complete
      future.complete(message);
    });

    return future;
  }
}