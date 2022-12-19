package com.coolbeevip.design.patterns.structural.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoProxy implements Video {
  private Video video = new VideoService();
  private Map<String,String> videoCached = new HashMap<>();

  @Override
  public List<String> listVideos() {
    return video.listVideos();
  }

  @Override
  public String getVideoInfo(String id) {
    return video.getVideoInfo(id);
  }

  @Override
  public String downloadVideo(String id) {
    if(videoCached.containsKey(id)){
      return videoCached.get(id) + "...我来自缓存";
    } else {
      String v = video.downloadVideo(id);
      this.videoCached.put(id, v);
      return v;
    }
  }
}
