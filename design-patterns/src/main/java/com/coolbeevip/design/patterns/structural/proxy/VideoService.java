package com.coolbeevip.design.patterns.structural.proxy;

import java.util.Arrays;
import java.util.List;

public class VideoService implements Video {
  @Override
  public List<String> listVideos() {
    return Arrays.asList("1", "2", "3");
  }

  @Override
  public String getVideoInfo(String id) {
    return "视频 " + id;
  }

  @Override
  public String downloadVideo(String id) {
    return "我是视频内容" + id;
  }
}
