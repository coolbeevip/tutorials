package com.coolbeevip.http;


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MyClient4X {
    CloseableHttpClient httpClientGlobal;

    public MyClient4X() {
        PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager(30, TimeUnit.SECONDS);
        poolingConnManager.setMaxTotal(100);
        poolingConnManager.setDefaultMaxPerRoute(20);
        this.httpClientGlobal = HttpClients.custom().setConnectionManager(poolingConnManager).build();
    }

    public int get() {
        int code = 0;
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpclient.execute(getTestHttpPost())) {
                code = response.getStatusLine().getStatusCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code;
    }

    public int get2() throws IOException {
        int code = 0;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(getTestHttpPost());
            code = response.getStatusLine().getStatusCode();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return code;
    }

    public int get3() throws IOException {
        int code = 0;
        CloseableHttpResponse response = null;
        try {
            response = httpClientGlobal.execute(getTestHttpPost());
            code = response.getStatusLine().getStatusCode();
        } finally {
            if (response != null) {
                response.close();
            }
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
