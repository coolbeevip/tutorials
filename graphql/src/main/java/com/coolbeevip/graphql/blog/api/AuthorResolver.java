package com.coolbeevip.graphql.blog.api;


import com.coolbeevip.graphql.blog.api.bean.Author;
import com.coolbeevip.graphql.blog.api.bean.Post;
import com.coolbeevip.graphql.blog.storage.PostDao;
import graphql.kickstart.tools.GraphQLResolver;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhanglei
 */
public class AuthorResolver implements GraphQLResolver<Author> {

  private PostDao postDao;

  public AuthorResolver(PostDao postDao) {
    this.postDao = postDao;
  }

  public List<Post> getPosts(Author author) {
    return postDao.getAllPosts().stream().filter(p -> p.getAuthorId().equals(author.getId()))
        .collect(
            Collectors.toList());
  }
}
