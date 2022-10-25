package com.coolbeevip.xml.cmdb.resource;

import com.coolbeevip.structures.tree.Node;
import com.coolbeevip.structures.tree.NodeFormatter;
import com.coolbeevip.structures.tree.OperateType;
import com.coolbeevip.xml.cmdb.Resource;
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
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    //Map<String, AtomicInteger> treeCacheIdsRelatedIdCount = new HashMap<>();

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
    List<ResourceRelationship> resourceRelationship = new ArrayList<>();

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
          // 无关联，写入根节点
          resourceRelationship.add(new ResourceRelationship(resource.getId(), "CMDB", 0));
          resourceIdIter.remove();
        } else {
          // 有关联
//          if (!treeCacheIdsRelatedIdCount.containsKey(resource.getId())) {
//            treeCacheIdsRelatedIdCount.put(resource.getId(), new AtomicInteger());
//          }
          for (String relatedId : relatedResourceIds) {
            resourceRelationship.add(new ResourceRelationship(resource.getId(), relatedId, 0));
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
    deepList(nodeMaxLevels, treeCache, tree, resourceRelationship, "CMDB", 0);

    writeUml(tree, Paths.get("target/cmdb-origin.puml"));

    /**
     * 压缩处理，相同的节点仅保留最深的节点
     * */
    tree.breadthFirstTraversal(node -> {
      if (nodeMaxLevels.containsKey(node.data.getId())) {
        int maxLevel = nodeMaxLevels.get(node.data.getId());
        if (maxLevel > node.getLevel()) {
          node.getParent().removeChild(node);
        }
      }
    });
    writeUml(tree, Paths.get("target/cmdb-activity.puml"));

    assertThat(treeCache.size(), Matchers.is(87));
  }

  private void deepList(Map<String, Integer> nodeMaxLevels, Map<String, Resource> treeCache, Node<Resource> parentNode, List<ResourceRelationship> resourceRelationship, String parentId, int level) {
    List<ResourceRelationship> resourceRelationshipTmp = resourceRelationship.stream()
        .filter(r -> r.getParentId().equals(parentId))
        .collect(Collectors.toList());
    for (ResourceRelationship tmp : resourceRelationshipTmp) {
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

      deepList(nodeMaxLevels, treeCache, tmpParentNode, resourceRelationship, tmp.getId(), level);
      level--;
    }
  }

  private void writeUml(Node node, Path path) throws IOException {
    NodeFormatter formatter = new ResourcePlantUmlActivityFormatter(OperateType.DEPTH);
    String text = node.writeValueAsString(formatter);
    log.info("{}", text);
    try (FileWriter fileWriter = new FileWriter(path.toFile());
         PrintWriter printWriter = new PrintWriter(fileWriter)) {
      printWriter.print(text);
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
