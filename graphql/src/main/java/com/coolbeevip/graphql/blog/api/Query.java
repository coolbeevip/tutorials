package com.coolbeevip.graphql.blog.api;

import com.coolbeevip.graphql.blog.api.bean.Author;
import com.coolbeevip.graphql.blog.api.bean.Post;
import com.coolbeevip.graphql.blog.storage.AuthorDao;
import com.coolbeevip.graphql.blog.storage.PostDao;
import graphql.kickstart.tools.GraphQLQueryResolver;

import java.util.List;

/**
 * @author zhanglei
 */
public class Query implements GraphQLQueryResolver {

  private PostDao postDao;

  private AuthorDao authorDao;

  public Query(PostDao postDao, AuthorDao authorDao) {
    this.authorDao = authorDao;
    this.postDao = postDao;
  }

  public List<Post> recentPosts(int count, int offset) {
    return postDao.getRecentPosts(count, offset);
  }

  public Author getAuthor(String authorId) {
    return authorDao.getAuthor(authorId).get();
  }

  public List<Author> getAllAuthor() {
    return authorDao.getAllAuthor();
  }
}
