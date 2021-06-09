package com.coolbeevip.graphql.blog.api.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {

  private String id;
  private String title;
  private String text;
  private String category;
  private String authorId;
}
