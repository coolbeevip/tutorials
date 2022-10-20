package com.coolbeevip.xml.cmdb.resource;

import java.util.ArrayList;
import java.util.List;

public class NodeExtra {
  public String nodeName; //当前的节点名称
  public int distance; //开始点到当前节点的最短路径
  public boolean visited; //当前节点是否已经求的最短路径（S集合）
  public String preNode; //前一个节点名称
  public List<String> path = new ArrayList<>(); //路径的所有途径点
}
