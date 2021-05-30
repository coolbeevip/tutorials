package org.coolbeevip;

import io.vertx.core.json.JsonObject;


import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;
import java.util.concurrent.CompletableFuture;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.CompletionStage;

@Path("/swapi")
public class ResourceUsingWebClient {


  @Inject
  Vertx vertx;

  private WebClient client;

  @PostConstruct
  void initialize() {
    this.client = WebClient.create(vertx,
        new WebClientOptions().setDefaultHost("swapi.co").setDefaultPort(443).setSsl(true));
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{id}")
  public CompletionStage<JsonObject> getStarWarsCharacter(@PathParam("id") int id) {
    CompletableFuture<JsonObject> ret = new CompletableFuture<>();
    client.get("/api/people/" + id)
        .rxSend().subscribe(resp -> {
      if (resp.statusCode() == 200) {
        ret.complete(resp.bodyAsJsonObject());
      } else {
        ret.complete(new JsonObject()
            .put("code", resp.statusCode())
            .put("message", resp.bodyAsString()));
      }
    });
    return ret;
  }

}