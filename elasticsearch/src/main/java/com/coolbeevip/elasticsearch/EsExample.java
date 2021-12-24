package com.coolbeevip.elasticsearch;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

/**
 docker run --rm --name es781 -p 9200:9200 -e "discovery.type=single-node" -e "ES_JAVA_OPTS=-Xms2g -Xmx2g -Xss256k" elasticsearch:7.8.1
 */
public class EsExample {

  public static void main(String[] args) throws IOException {

    // create client
    RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost("127.0.0.1", 9200));
    RestHighLevelClient client = new RestHighLevelClient(restClientBuilder);

    //  create index
    String indexName = "chats";
    if(!client.indices().exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT)){
      CreateIndexRequest request = new CreateIndexRequest(indexName);
      client.indices().create(request, RequestOptions.DEFAULT);
    }

    // terms query
    SearchRequest searchRequest = new SearchRequest();
    List<String> names = IntStream.range(0,1000000).mapToObj(n -> UUID.randomUUID().toString()).collect(Collectors.toList());
    SearchSourceBuilder builder = new SearchSourceBuilder()
        .postFilter(QueryBuilders.termsQuery("name", names));
    searchRequest.source(builder);
    searchRequest.indices(indexName);

    // concurrency
    int numThreads = 1;
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    for (int i = 0; i < numThreads; i++) {
      executor.submit(new Runnable() {
        @Override
        public void run() {
          try {
            long begin = System.currentTimeMillis();
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            System.out.println(response.status()+ " "+(System.currentTimeMillis()-begin));
          }catch (Exception e){
            e.printStackTrace();
          }
        }
      });
    }
  }

}