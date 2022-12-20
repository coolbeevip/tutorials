package com.coolbeevip.design.patterns.behavioral.iterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConcreteProfileIterator implements ProfileIterator {
  private int currentPosition = 0;
  private List<String> ids = new ArrayList<>();
  private List<Profile> profiles = new ArrayList<>();

  @Override
  public boolean hasNext() {
    lazyLoad();
    return currentPosition < ids.size();
  }

  @Override
  public Profile next() {
    if (!hasNext()) {
      return null;
    }
    String id = this.ids.get(this.currentPosition);
    Profile result;
    if (profiles.size() > currentPosition) {

      result = profiles.get(currentPosition);
    } else {
      // 此处模拟根据 id 获取 profile
      result = new Profile(id, "User-" + id);
      this.profiles.add(result);
    }
    this.currentPosition++;
    return result;
  }

  @Override
  public void reset() {
    this.currentPosition = 0;
  }

  private void lazyLoad() {
    if (ids.size() == 0) {
      // 此处模拟获取集合索引
      List<String> profiles = Arrays.asList("1", "2", "3", "4");
      for (String profile : profiles) {
        this.ids.add(profile);
      }
    }
  }
}
