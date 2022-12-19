package com.coolbeevip.design.patterns.structural.proxy;

import java.util.List;

public interface Video {
  List<String> listVideos();

  String getVideoInfo(String id);

  String downloadVideo(String id);
}
