package com.coolbeevip.graphql.blog.storage;

import com.coolbeevip.graphql.blog.api.bean.Author;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author zhanglei
 */
public class AuthorDao {

  private List<Author> authors = new ArrayList<>();

  public Optional<Author> getAuthor(String id) {
    return authors.stream().filter(author -> id.equals(author.getId())).findFirst();
  }

  public void saveAuthor(Author author) {
    authors.add(0, author);
  }

  public List<Author> getAllAuthor() {
    return new ArrayList<>(authors);
  }
}
