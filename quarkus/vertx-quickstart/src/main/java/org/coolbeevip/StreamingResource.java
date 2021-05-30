package org.coolbeevip;

import io.vertx.reactivex.core.Vertx;
import java.util.Date;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.reactivestreams.Publisher;

@Path("/hello")
public class StreamingResource {

  @Inject
  Vertx vertx;

  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @Path("{name}/streaming")
  public Publisher<String> greeting(@PathParam(value="name") String name) {
    return vertx.periodicStream(2000).toFlowable()
        .map(l -> String.format("Hello %s! (%s)%n", name, new Date()));
  }
}
