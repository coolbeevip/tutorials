package com.coolbeevip.graphql.blog;

import com.coolbeevip.graphql.blog.api.AuthorResolver;
import com.coolbeevip.graphql.blog.api.Mutation;
import com.coolbeevip.graphql.blog.api.PostResolver;
import com.coolbeevip.graphql.blog.api.Query;
import com.coolbeevip.graphql.blog.storage.AuthorDao;
import com.coolbeevip.graphql.blog.storage.PostDao;
import com.coolbeevip.graphql.blog.storage.StorageLoader;
import graphql.kickstart.tools.boot.GraphQLJavaToolsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhanglei
 */
@Configuration
@AutoConfigureBefore(GraphQLJavaToolsAutoConfiguration.class)
public class GraphQLAutoConfiguration {

  @Bean
  AuthorDao authorDao() {
    return new AuthorDao();
  }

  @Bean
  PostDao postDao() {
    return new PostDao();
  }

  @Bean
  Mutation mutation(PostDao postDao, AuthorDao authorDao) {
    return new Mutation(postDao, authorDao);
  }

  @Bean
  Query query(PostDao postDao, AuthorDao authorDao) {
    return new Query(postDao, authorDao);
  }

  @Bean
  PostResolver postResolver(AuthorDao authorDao) {
    return new PostResolver(authorDao);
  }

  @Bean
  AuthorResolver authorResolver(PostDao postDao){
    return new AuthorResolver(postDao);
  }

  @Bean
  StorageLoader storageLoad(PostDao postDao, AuthorDao authorDao) {
    return new StorageLoader(postDao, authorDao);
  }
}
