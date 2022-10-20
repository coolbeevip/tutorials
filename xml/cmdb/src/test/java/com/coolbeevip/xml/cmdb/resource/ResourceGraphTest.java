package com.coolbeevip.xml.cmdb.resource;

import com.coolbeevip.xml.cmdb.Resource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
public class ResourceGraphTest {
  int analyticLoopLimit = 1000;

  @Test
  @SneakyThrows
  public void listPreInspectors() {
    Set<String> inspectors = new HashSet<>();
    Map<String, Resource> resourceMap = loadResourceFromXml();
    resourceMap.forEach((k,v) -> {
      try{
        if(v.getPreInspectors()!=null){
          v.getPreInspectors().getInspectors().forEach(inspector -> {
            inspectors.add(inspector.getId());
          });
        }
      }catch (Exception e){
        log.error("{}", k,e);
      }
    });
    inspectors.forEach(p -> log.info("PreInspector.inspector {}", p));
  }

  @Test
  @SneakyThrows
  public void listPostInspectorsInspector() {
    Set<String> inspectors = new HashSet<>();
    Map<String, Resource> resourceMap = loadResourceFromXml();
    resourceMap.forEach((k,v) -> {
      try{
        if(v.getPostInspectors()!=null){
          v.getPostInspectors().getInspectors().forEach(inspector -> {
            inspectors.add(inspector.getId());
          });
        }
      }catch (Exception e){
        log.error("{}", k,e);
      }
    });
    inspectors.forEach(p -> log.info("PostInspectors.inspector {}", p));
  }

  @Test
  @SneakyThrows
  public void listPostInspectorsRecordInspector() {
    Set<String> inspectors = new HashSet<>();
    Map<String, Resource> resourceMap = loadResourceFromXml();
    resourceMap.forEach((k,v) -> {
      try{
        if(v.getPostInspectors()!=null){
          v.getPostInspectors().getRecordInspectors().forEach(inspector -> {
            inspectors.add(inspector.getId());
          });
        }
      }catch (Exception e){
        log.error("{}", k,e);
      }
    });
    inspectors.forEach(p -> log.info("PostInspectors.recordInspector {}", p));
  }


  @Test
  @SneakyThrows
  public void listAttrTempPlugin() {
    Set<String> plugins = new HashSet<>();
    Map<String, Resource> resourceMap = loadResourceFromXml();
    resourceMap.forEach((k,v) -> {
      try{
        if(v.getTable().getAttrTemps()!=null){
          v.getTable().getAttrTemps().forEach(attrTemp -> {
            if(attrTemp.getPlugin()!=null){
              plugins.add(attrTemp.getPlugin().getId());
            }

          });
        }
      }catch (Exception e){
        log.error("{}", k,e);
      }

    });
    plugins.forEach(p -> log.info("AttrTemp plugin {}", p));
  }

  @Test
  @SneakyThrows
  public void listInspectorTest() {

    Set<String> distinctInspectors = new HashSet<>();
    Map<String, Map<String, Set<String>>> resourceInspectors = new HashMap<>();
    Map<String, Resource> resourceMap = loadResourceFromXml();
    resourceMap.forEach((k, v) -> {
      if(!v.getId().equals(v.getCacheId())){
        log.info("CACHE {}: id={}, cacheId={}", k, v.getId(), v.getCacheId());
      }
      if(v.getDeletable()!=null && v.getDeletable().equals("N")){
        log.info("DELETABLE {}: deletable={}", k, v.getDeletable());
      }
      Map<String, Set<String>> resourceInspector = new HashMap<>();

      // ExtractInspector
      Set<String> inspectors = new HashSet<>();
      if (v.getExtractInspector() != null) {
        inspectors.add(v.getExtractInspector().getId());
      }
      resourceInspector.put("ExtractInspector", inspectors);

      // PreInspectors.inspector
      Set<String>  preInspectorsInspectors = new HashSet<>();
      if (v.getPreInspectors() != null && v.getPreInspectors().getInspectors() != null && !v.getPreInspectors().getInspectors().isEmpty()) {
        v.getPreInspectors().getInspectors().forEach(inspector -> {
          preInspectorsInspectors.add(inspector.getId());
        });
      }
      resourceInspector.put("PreInspectors.inspector", preInspectorsInspectors);


      Set<String>  postInspectorsInspectors = new HashSet<>();
      Set<String>  postInspectorsRecordInspectors = new HashSet<>();
      if (v.getPostInspectors() != null) {
        // PostInspectors.inspector
        if (v.getPostInspectors().getInspectors() != null && !v.getPostInspectors().getInspectors().isEmpty()) {
          v.getPostInspectors().getInspectors().forEach(inspector -> {
            postInspectorsInspectors.add(inspector.getId());
          });
        }
        resourceInspector.put("PostInspectors.inspector", postInspectorsInspectors);

        // PostInspectors.recordInspector
        if (v.getPostInspectors().getRecordInspectors() != null && !v.getPostInspectors().getRecordInspectors().isEmpty()) {
          v.getPostInspectors().getRecordInspectors().forEach(inspector -> {
            postInspectorsRecordInspectors.add(inspector.getId());
          });
        }
        resourceInspector.put("PostInspectors.recordInspector", postInspectorsRecordInspectors);
      }
      resourceInspectors.put(k, resourceInspector);
      distinctInspectors.addAll(inspectors);
    });

    distinctInspectors.forEach(s -> log.info("{}", s));

    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(resourceInspectors);
    log.info("{}", json);
  }

  @Test
  @SneakyThrows
  public void test() {

    int nodeCount = 87;
    Map<String, Resource> resourceMap = loadResourceFromXml();
    assertThat(resourceMap.size(), Matchers.is(nodeCount));

    int relationshipCount = 170;
    List<ResourceRelationship> resourceRelationships = analyticRelationship("ROOT", resourceMap, analyticLoopLimit);
    assertThat(resourceRelationships.size(), Matchers.is(relationshipCount));

    /**
     * 构造一个有向无环图
     * */
    MutableValueGraph<String, Integer> graph = ValueGraphBuilder.directed()
        .nodeOrder(ElementOrder.<String>insertion()) // 节点按插入顺序输出
        .expectedNodeCount(nodeCount) // 预期节点数
        .allowsSelfLoops(false) // 不允许子环
        .build();
    // 插入边（自动初始化点）
    resourceRelationships.forEach(r -> {
      graph.putEdgeValue(r.getParentId(), r.getId(), 1);
    });
    log.info("initialized graph {}", graph);
    assertThat(graph.nodes().size(), Matchers.is(nodeCount + 1));
    assertThat(graph.edges().size(), Matchers.is(relationshipCount));
    assertThat(graph.isDirected(), Matchers.is(true)); // 有向图
    assertThat(Graphs.hasCycle(graph.asGraph()), Matchers.is(false)); // 验证是否存在环

    originDAGTest(graph);
    shortestPathTest(graph);

    MutableValueGraph<String, Integer> newGraph = parserDeep(graph);
    writeActivityUml(newGraph.asGraph(), Paths.get("target/trans-origin-level.puml"));
  }

  /**
   * 根据节点层级更新边权重
   */
  private MutableValueGraph<String, Integer> parserDeep(MutableValueGraph<String, Integer> graph) {
    MutableValueGraph<String, Integer> graphNew = ValueGraphBuilder.directed()
        .nodeOrder(ElementOrder.<String>insertion()) // 节点按插入顺序输出
        .expectedNodeCount(graph.nodes().size()) // 预期节点数
        .allowsSelfLoops(false) // 不允许子环
        .build();
    List<ResourceRelationship> resourceRelationships = new LinkedList<>();
    traverserLevel(graph, "ROOT", 0, resourceRelationships);

    resourceRelationships.forEach(r -> {
      graphNew.putEdgeValue(r.getParentId(), r.getId(), 1);
    });

    return graphNew;
  }

  private void traverserLevel(MutableValueGraph<String, Integer> graph, String parentNode, int level, List<ResourceRelationship> resourceRelationships) {
    Set<String> children = graph.successors(parentNode);
    level++;
    for (String child : children) {
      log.info("{} -> {} {}", parentNode, child, level);
      resourceRelationships.add(new ResourceRelationship(child, parentNode, level));
      traverserLevel(graph, child, level, resourceRelationships);
    }
  }

  public void originDAGTest(MutableValueGraph<String, Integer> graph) throws IOException {
    writeActivityUml(graph.asGraph(), Paths.get("target/trans-origin.puml"));
  }

  public void shortestPathTest(MutableValueGraph<String, Integer> graph) throws IOException {
    List<ResourceRelationship> resourceRelationships = dijkstra(graph, "ROOT");
    MutableValueGraph<String, Integer> graph1 = ValueGraphBuilder.directed()
        .nodeOrder(ElementOrder.<String>insertion()) // 节点按插入顺序输出
        .expectedNodeCount(graph.nodes().size()) // 预期节点数
        .allowsSelfLoops(false) // 不允许子环
        .build();
    resourceRelationships.forEach(r -> {
      graph1.putEdgeValue(r.getParentId(), r.getId(), 1);
    });
    writeActivityUml(graph1.asGraph(), Paths.get("target/trans-shortest-paths.puml"));
  }


  private List<ResourceRelationship> dijkstra(MutableValueGraph<String, Integer> graph, String startNode) {

    List<ResourceRelationship> resourceRelationships = new ArrayList<>();

    Set<String> nodes = graph.nodes();
    Map<String, NodeExtra> nodeExtras = new HashMap<>(nodes.size());

    /**
     * 初始化extra
     * 初始最短路径：存在边时，为边的权值；没有边时为无穷大
     */
    for (String node : nodes) {
      NodeExtra extra = new NodeExtra();
      extra.nodeName = node;
      final int value = graph.edgeValueOrDefault(startNode, node, Integer.MAX_VALUE);
      extra.distance = value;
      extra.visited = false;
      if (value < Integer.MAX_VALUE) {
        extra.path.clear();
        extra.path.add(startNode);
        extra.path.add(node);
        extra.preNode = startNode;
      }
      nodeExtras.put(node, extra);
    }

    /**
     * 一开始可设置开始节点的最短路径为0
     */
    NodeExtra current = nodeExtras.get(startNode);
    current.distance = 0;
    current.visited = true;
    current.path.clear();
    current.path.add(startNode);
    current.preNode = startNode;

    /*需要循环节点数遍*/
    for (String node : nodes) {

      NodeExtra minExtra = null;
      int min = Integer.MAX_VALUE;

      /**
       * 找出起始点当前路径最短的节点
       */
      for (String notVisitedNode : nodes) {
        NodeExtra extra = nodeExtras.get(notVisitedNode);
        if (!extra.visited && extra.distance < min) {
          min = extra.distance;
          minExtra = extra;
        }
      }

      /**
       * 更新找到的最短路径节点的extra信息（获取的标志、路径信息）
       */
      if (minExtra != null) {
        minExtra.visited = true;
        //minExtra.path = nodeExtras.get(minExtra.preNode).path + " -> " + minExtra.nodeName;
        minExtra.path.clear();
        minExtra.path.addAll(nodeExtras.get(minExtra.preNode).path);
        minExtra.path.add(minExtra.nodeName);
        current = minExtra;
      }

      /**
       * 并入新查找到的节点后，更新与其相关节点的最短路径中间结果
       * if (D[j] + arcs[j][k] < D[k]) D[k] = D[j] + arcs[j][k]
       */
      Set<String> successors = graph.successors(current.nodeName); //只需循环当前节点的后继列表即可
      for (String notVisitedNode : successors) {
        NodeExtra extra = nodeExtras.get(notVisitedNode);
        if (!extra.visited) {
          final int value = current.distance + graph.edgeValueOrDefault(current.nodeName,
              notVisitedNode, 0);
          if (value < extra.distance) {
            extra.distance = value;
            extra.preNode = current.nodeName;
          }
        }
      }
    }

    /**
     * 输出起始节点到每个节点的最短路径以及路径的途径点信息
     */
    Set<String> keys = nodeExtras.keySet();
    for (String node : keys) {
      NodeExtra extra = nodeExtras.get(node);
      if (extra.distance < Integer.MAX_VALUE) {
        log.info(startNode + " -> " + node + ": min: " + extra.distance + ", path: " + extra.path.stream().collect(Collectors.joining(" -> ")));
        for (int i = 0; i < extra.path.size() - 1; i++) {
          resourceRelationships.add(new ResourceRelationship(extra.path.get(i + 1), extra.path.get(i), 0));
        }
      }
    }

    return resourceRelationships;
  }

  private List<ResourceRelationship> analyticRelationship(String rootId, Map<String, Resource> resourceMap, int loopLimit) throws Exception {
    int loop = 0;
    List<ResourceRelationship> resourceRelationship = new ArrayList<>();

    while (!resourceMap.isEmpty()) {
      Iterator<String> resourceIdIter = resourceMap.keySet().iterator();
      while (resourceIdIter.hasNext()) {
        String resourceId = resourceIdIter.next();
        Resource resource = resourceMap.get(resourceId);

        // 获取资源关联 ID 列表
        Stream<String> relatedResourceIdStream = Stream.empty();

        if (resource.getTable().getAttrs() != null) {
          relatedResourceIdStream = Stream.concat(relatedResourceIdStream,
              resource.getTable().getAttrs().stream().filter(a -> a.getPlugin() != null).map(a -> a.getPlugin()).filter(p -> p.getResourceId() != null).map(p -> p.getResourceId()));
        }
        if (resource.getTable().getAttrTemps() != null) {
          relatedResourceIdStream = Stream.concat(relatedResourceIdStream,
              resource.getTable().getAttrTemps().stream().filter(a -> a.getPlugin() != null).map(a -> a.getPlugin()).filter(p -> p.getResourceId() != null).map(p -> p.getResourceId()));
        }

        if (resource.getPreInspectors() != null) {
          relatedResourceIdStream = Stream.concat(relatedResourceIdStream,
              resource.getPreInspectors().getInspectors().stream().filter(inspector -> inspector.getResourceId() != null).map(inspector -> inspector.getResourceId()));
        }
        if (resource.getPostInspectors() != null) {
          relatedResourceIdStream = Stream.concat(relatedResourceIdStream,
              resource.getPostInspectors().getInspectors().stream().filter(inspector -> inspector.getResourceId() != null).map(inspector -> inspector.getResourceId()));
        }
        Set<String> relatedResourceIds = relatedResourceIdStream.collect(Collectors.toSet());

        if (relatedResourceIds.isEmpty()) {
          resourceRelationship.add(new ResourceRelationship(resource.getId(), rootId, 0));
          resourceIdIter.remove();
        } else {
          for (String relatedId : relatedResourceIds) {
            resourceRelationship.add(new ResourceRelationship(resource.getId(), relatedId, 0));
          }
          resourceIdIter.remove();
        }
      }

      if (loop > loopLimit) {
        throw new Exception("超过最大循环次数，仍有部分数据没有找到上级对象 " + resourceMap.keySet().stream().collect(Collectors.joining(",")));
      }
      loopLimit++;
    }
    return resourceRelationship;
  }

  private Map<String, Resource> loadResourceFromXml() throws JAXBException, FileNotFoundException {
    Map<String, Resource> resourceMap = new HashMap<>();

    File file = Paths.get("src/main/resources/xml").toFile();
    for (File xml : file.listFiles()) {
      if (xml.isFile()) {
        JAXBContext ctx = JAXBContext.newInstance(Resource.class);
        Resource resource = (Resource) ctx.createUnmarshaller().unmarshal(new FileReader(xml));
        resourceMap.put(resource.getId(), resource);
      }
    }
    return resourceMap;
  }

  private void writeActivityUml(Graph<String> graph, Path path) throws IOException {
    Set<String> linkedCache = new HashSet<>();
    StringJoiner joiner = new StringJoiner("\n");
    joiner.add("@startuml");
    Iterator<EndpointPair<String>> it = graph.edges().iterator();
    while (it.hasNext()) {
      EndpointPair<String> edge = it.next();
      String left = edge.nodeU();
      String right = edge.nodeV();
      if (left.equals("ROOT")) {
        left = "(*)";
      }
      String link = String.format("%s -->[1] %s", left, right);
      if (!linkedCache.contains(link)) {
        joiner.add(link);
        linkedCache.add(link);
      }


      if (graph.outDegree(edge.nodeV()) == 0) {
        // 出度为 0 表示为叶子节点
        link = String.format("%s -->[1] (*)", right);
        if (!linkedCache.contains(link)) {
          joiner.add(link);
          linkedCache.add(link);
        }
      }
    }
    joiner.add("@enduml");
    log.debug("{}", joiner);
    try (FileWriter fileWriter = new FileWriter(path.toFile());
         PrintWriter printWriter = new PrintWriter(fileWriter)) {
      printWriter.print(joiner);
    }
  }

  private void deepTree(MutableGraph<String> graph, String parent, int level, StringJoiner joiner) {
    Set<String> children = graph.successors(parent);
    joiner.add(createIndent(level, '*') + " " + parent);
    for (String child : children) {
      deepTree(graph, child, level++, joiner);
    }
  }

  private static String createIndent(int depth, char placeholder) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < depth; i++) {
      sb.append(placeholder);
    }
    return sb.toString();
  }
}
