package com.coolbeevip.http;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;

public class MyClient5X {
  CloseableHttpClient httpClientGlobal = HttpClients.createDefault();

  public int get() {
    int code = 0;
    HttpPost httpPost = getTestHttpPost();
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
        code = response.getCode();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return code;
  }

  private HttpPost getTestHttpPost() {
    HttpPost httpPost = new HttpPost("http://10.19.88.60:5005/nc-tools/encrypt");
    StringEntity entity = new StringEntity("{\"value\":\"123456\"}", ContentType.APPLICATION_JSON);
    httpPost.setEntity(entity);
    return httpPost;
  }
}
