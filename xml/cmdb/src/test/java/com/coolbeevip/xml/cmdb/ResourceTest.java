package com.coolbeevip.xml.cmdb;

import com.coolbeevip.xml.cmdb.tree.Node;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
public class ResourceTest {

  @Test
  @SneakyThrows
  public void marshalTest() {
    // marshal
    Resource request = new Resource();
    JAXBContext context = JAXBContext.newInstance(Resource.class);
    Marshaller mar = context.createMarshaller();
    mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    mar.marshal(request, new File("target/resource.xml"));
  }

  @Test
  @SneakyThrows
  public void validatorTest() {
    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Source schemaFile = new StreamSource(Paths.get("src/main/resources/xsd/resource.xsd").toFile());
    Schema schema = factory.newSchema(schemaFile);
    Validator validator = schema.newValidator();

    File file = Paths.get("src/main/resources/xml").toFile();
    for (File xml : file.listFiles()) {
      if (xml.isFile()) {
        try {
          validator.validate(new StreamSource(new FileReader(xml)));
        } catch (Exception e) {
          log.error("validator failed {}", xml.getAbsolutePath());
        }
      }
    }
  }

  @Test
  @SneakyThrows
  public void unmarshalTest() {
    File file = Paths.get("src/main/resources/xml").toFile();
    for (File xml : file.listFiles()) {
      if (xml.isFile()) {
        log.info("load xml {}", xml.getAbsolutePath());
        JAXBContext ctx = JAXBContext.newInstance(Resource.class);
        Resource resource = (Resource) ctx.createUnmarshaller().unmarshal(new FileReader(xml));
        log.info("{}", resource);
      }
    }
  }

  @Test
  @SneakyThrows
  public void dependencyTreeTest() {
    Resource root = new Resource();
    root.setId("CMDB");
    Node<Resource> tree = new Node<>(root);

    Map<String, Resource> treeCache = new HashMap<>();
    Map<String, Resource> unknownCache = new HashMap<>();
    Map<String, Integer> nodeMaxLevels = new HashMap<>();
    Map<String, AtomicInteger> treeCacheIdsRelatedIdCount = new HashMap<>();

    File file = Paths.get("src/main/resources/xml").toFile();
    for (File xml : file.listFiles()) {
      if (xml.isFile()) {
        JAXBContext ctx = JAXBContext.newInstance(Resource.class);
        Resource resource = (Resource) ctx.createUnmarshaller().unmarshal(new FileReader(xml));
        unknownCache.put(resource.getId(), resource);
        treeCache.put(resource.getId(), resource);
      }
    }

    int max_loop = 1000;
    int loop = 0;
    List<ResourceIndexTree> resourceIndexTree = new ArrayList<>();

    while (!unknownCache.isEmpty()) {
      Iterator<String> resourceIdIter = unknownCache.keySet().iterator();
      while (resourceIdIter.hasNext()) {
        String resourceId = resourceIdIter.next();
        Resource resource = unknownCache.get(resourceId);

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
          // 无关联，写入跟节点
          resourceIndexTree.add(new ResourceIndexTree(resource.getId(), "CMDB"));
          resourceIdIter.remove();
        } else {
          // 有关联
          if (!treeCacheIdsRelatedIdCount.containsKey(resource.getId())) {
            treeCacheIdsRelatedIdCount.put(resource.getId(), new AtomicInteger());
          }
          for (String relatedId : relatedResourceIds) {
            resourceIndexTree.add(new ResourceIndexTree(resource.getId(), relatedId));
          }
          resourceIdIter.remove();
        }
      }

      if (loop > max_loop) {
        log.error("超过最大循环次数，仍有部分数据没有找到上级对象 {}", unknownCache.keySet().stream().collect(Collectors.joining(",")));
        break;
      }
      loop++;
    }


    log.info("=========================================================================");

    deepList(nodeMaxLevels, treeCache, tree, resourceIndexTree, "CMDB", 0);

    tree.breadthFirstTraversal(node -> {
      if (nodeMaxLevels.containsKey(node.data.getId())) {
        int maxLevel = nodeMaxLevels.get(node.data.getId());
        if (maxLevel > node.getLevel()) {
          node.getParent().removeChild(node);
        }
      }
    });

    tree.depthFirstTraversal(node -> {
      System.out.println(createIndent(node.getLevel()) + " " + node.data.getId());
    });

    Set<String> cacheSet = new HashSet<>();
    try (FileWriter fileWriter = new FileWriter(Paths.get("cmdb-activity.puml").toFile());
         PrintWriter printWriter = new PrintWriter(fileWriter)) {
      printWriter.println("@startuml");
      tree.depthFirstTraversal(node -> {
        if (node.getParent() == null) {
          printWriter.println("(*) --> " + node.data.getId());
        } else {
          String line = node.getParent().data.getId() + " --> " + node.data.getId();
          if(!cacheSet.contains(line)){
            printWriter.println(line);
            cacheSet.add(line);
            if(node.isLeaf()){
              String endLine = node.data.getId()+" --> (*)";
              if(!cacheSet.contains(endLine)){
                printWriter.println(endLine);
                cacheSet.add(endLine);
              }
            }
          }
        }
      });

      printWriter.println("@enduml");
    }

    assertThat(treeCache.size(), Matchers.is(87));
  }

  private void deepList(Map<String, Integer> nodeMaxLevels, Map<String, Resource> treeCache, Node<Resource> parentNode, List<ResourceIndexTree> resourceIndexTree, String parentId, int level) {
    List<ResourceIndexTree> resourceIndexTreeTmp = resourceIndexTree.stream()
        .filter(r -> r.getParentId().equals(parentId))
        .collect(Collectors.toList());
    for (ResourceIndexTree tmp : resourceIndexTreeTmp) {
      level++;
      Node<Resource> tmpParentNode = parentNode.addChild(treeCache.get(tmp.getId()));
      //log.info("{} -> {}", tmpParentNode.data.getId(), tmpParentNode.getLevel());
      // 计算每类资源的最大深度
      if (nodeMaxLevels.containsKey(tmp.getId())) {
        if (tmpParentNode.getLevel() > nodeMaxLevels.get(tmp.getId())) {
          nodeMaxLevels.put(tmp.getId(), tmpParentNode.getLevel());
        }
      } else {
        nodeMaxLevels.put(tmp.getId(), 1);
      }

      deepList(nodeMaxLevels, treeCache, tmpParentNode, resourceIndexTree, tmp.getId(), level);
      level--;
    }
  }

  private static String createIndent(int depth) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < depth; i++) {
      sb.append('+');
    }
    return sb.toString();
  }
}
