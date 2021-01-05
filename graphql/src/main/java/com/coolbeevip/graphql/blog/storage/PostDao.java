package com.coolbeevip.graphql.blog.storage;


import com.coolbeevip.graphql.blog.api.bean.Post;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhanglei
 */
public class PostDao {

  private List<Post> posts = new ArrayList<>();

  public List<Post> getRecentPosts(int count, int offset) {
    return posts.stream().skip(offset).limit(count).collect(Collectors.toList());
  }

  public void savePost(Post post) {
    posts.add(0, post);
  }

  public List<Post> getAllPosts(){
    return posts;
  }
}
