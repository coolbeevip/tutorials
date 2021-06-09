package org.acme.quickstart;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/hello")
public class GreetingResource {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Inject
  GreetingService service;

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/greeting/{name}")
  public String greeting(@PathParam("name") String name) {
    return service.greeting(name);
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String hello() {
    LOG.info("request hello");
    return "hello\n";
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/async")
  public CompletionStage<String> asyncHello() {
    return CompletableFuture.supplyAsync(() -> {
      return "hello\n";
    });
  }
}