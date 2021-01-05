package com.coolbeevip.graphql.blog.api;

import com.coolbeevip.graphql.blog.api.bean.Author;
import com.coolbeevip.graphql.blog.api.bean.Post;
import com.coolbeevip.graphql.blog.storage.AuthorDao;
import com.coolbeevip.graphql.blog.storage.PostDao;
import graphql.kickstart.tools.GraphQLMutationResolver;
import java.util.UUID;

/**
 * @author zhanglei
 */
public class Mutation implements GraphQLMutationResolver {

  private PostDao postDao;

  private AuthorDao authorDao;

  public Mutation(PostDao postDao,AuthorDao authorDao) {
    this.postDao = postDao;
    this.authorDao = authorDao;
  }

  public Post writePost(String title, String text, String category,
    String authorId) {
    Post post = Post.builder()
      .id(UUID.randomUUID().toString())
      .title(title)
      .text(text)
      .category(category)
      .authorId(authorId)
      .build();
    postDao.savePost(post);

    return post;
  }

  public Author addAuthor(String id, String name) {
    Author author = Author.builder()
      .id(id)
      .name(name)
      .build();
    authorDao.saveAuthor(author);
    return author;
  }
}
