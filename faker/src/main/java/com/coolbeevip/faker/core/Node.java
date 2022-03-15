package com.coolbeevip.faker.core;

import static com.coolbeevip.faker.core.Constants.OBJECT_NULL;

import com.coolbeevip.faker.Faker;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.MapType;
import java.util.Map;
import java.util.UUID;

public abstract class Node {

  protected final Faker faker;
  private final String id;
  protected ObjectMapper mapper = new ObjectMapper();

  protected ObjectNode json;

  protected Node(Faker faker) {
    this.faker = faker;
    this.id = UUID.randomUUID().toString();
    this.json = mapper.createObjectNode();
    this.json.put("version", OBJECT_NULL);
    this.json.put("timestamp", OBJECT_NULL);
    this.json.put("id", OBJECT_NULL);
    this.json.put("type", OBJECT_NULL);
  }

  public String getId() {
    return id;
  }

  public ObjectNode getJson() {
    return json;
  }

  public String toJSON() throws JsonProcessingException {
    mapper.configOverride(Map.class)
        .setInclude(Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL));
    MapType mapType = mapper.getTypeFactory()
        .constructMapType(Map.class, String.class, Object.class);
    Object mapValue = mapper.convertValue(this.json, mapType);
    return mapper.writeValueAsString(mapValue);
  }

  public Node type() {
    this.json.put("type", this.getClass().getSimpleName().toLowerCase());
    return this;
  }

  public Node id() {
    this.json.put("id", this.getId());
    return this;
  }

  public Node timestamp() {
    this.json.put("timestamp", System.nanoTime());
    return this;
  }

  public Node version(int version) {
    this.json.put("version", version);
    return this;
  }


}