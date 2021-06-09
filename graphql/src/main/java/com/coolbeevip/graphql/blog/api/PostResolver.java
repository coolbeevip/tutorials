package com.coolbeevip.graphql.blog.api;


import com.coolbeevip.graphql.blog.api.bean.Author;
import com.coolbeevip.graphql.blog.api.bean.Post;
import com.coolbeevip.graphql.blog.storage.AuthorDao;
import graphql.kickstart.tools.GraphQLResolver;
import java.util.Optional;

/**
 * @author zhanglei
 */
public class PostResolver implements GraphQLResolver<Post> {

  private AuthorDao authorDao;

  public PostResolver(AuthorDao authorDao) {
    this.authorDao = authorDao;
  }

  public Optional<Author> getAuthor(Post post) {
    return authorDao.getAuthor(post.getAuthorId());
  }
}
