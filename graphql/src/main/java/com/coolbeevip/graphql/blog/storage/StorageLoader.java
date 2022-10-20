package com.coolbeevip.graphql.blog.storage;

import com.coolbeevip.graphql.blog.api.bean.Author;
import com.coolbeevip.graphql.blog.api.bean.Post;

import java.util.UUID;

/**
 * @author zhanglei
 */
public class StorageLoader {

  final PostDao postDao;
  final AuthorDao authorDao;

  public StorageLoader(PostDao postDao, AuthorDao authorDao) {
    this.postDao = postDao;
    this.authorDao = authorDao;
    Author author = Author.builder().id("1").name("zhanglei").build();
    authorDao.saveAuthor(author);

    Post post = Post.builder()
        .id(UUID.randomUUID().toString())
        .title("title-1")
        .text("text-1")
        .category("c-1")
        .authorId(author.getId())
        .build();
    postDao.savePost(post);
  }
}
