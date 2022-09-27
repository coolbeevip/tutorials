package com.coolbeevip.xml.cmdb;

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
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
    ResourceNode<Resource> rootNode = new ResourceNode<>(new Resource());

    Map<String, ResourceNode> treeCache = new HashMap<>();
    Map<String, Resource> unknownCache = new HashMap<>();
    Set<String> treeCacheIds = new HashSet<>();
    Map<String, AtomicInteger> treeCacheIdsRelatedIdCount = new HashMap<>();

    File file = Paths.get("src/main/resources/xml").toFile();
    for (File xml : file.listFiles()) {
      if (xml.isFile()) {
        JAXBContext ctx = JAXBContext.newInstance(Resource.class);
        Resource resource = (Resource) ctx.createUnmarshaller().unmarshal(new FileReader(xml));
        unknownCache.put(resource.getId(), resource);
      }
    }


    int max_loop = 1000;
    int loop = 0;
    while (!unknownCache.isEmpty()) {
      Iterator<String> resourceIdIter = unknownCache.keySet().iterator();
      while (resourceIdIter.hasNext()) {
        String resourceId = resourceIdIter.next();
        Resource resource = unknownCache.get(resourceId);

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
        log.info("FIND {} => {}", resource.getId(), relatedResourceIds.stream().collect(Collectors.joining(",")));

        if (relatedResourceIds.isEmpty()) {
          // 无关联，写入跟节点
          treeCache.put(resource.getId(), rootNode.addChild(resource));
          resourceIdIter.remove();
        } else {
          // 有关联
          if(!treeCacheIdsRelatedIdCount.containsKey(resource.getId())){
            treeCacheIdsRelatedIdCount.put(resource.getId(),new AtomicInteger());
          }

          for (String relatedId : relatedResourceIds) {

            Comparable<Resource> searchFCriteria = new Comparable<Resource>() {
              @Override
              public int compareTo(Resource o) {
                if (o == null || o.getId() == null){
                  return 1;
                }
                boolean nodeOk = o.getId().equals(relatedId);
                return nodeOk ? 0 : 1;
              }
            };

//            ResourceNode<Resource> relatedNode = rootNode.findTreeNode(searchFCriteria);
//            if(relatedNode!=null){
//              treeCache.put(resource.getId(),relatedNode.addChild(resource));
//              treeCacheIdsRelatedIdCount.get(resource.getId()).incrementAndGet();
//              log.info("PUT {} => {}", relatedId, resource.getId());
//            }

            if (treeCache.containsKey(relatedId) && !treeCacheIds.contains(relatedId+"->"+resource.getId())) {
              treeCache.put(resource.getId(), treeCache.get(relatedId).addChild(resource));
              log.info("PUT {} => {}", relatedId, resource.getId());
              treeCacheIds.add(relatedId+"->"+resource.getId());
              treeCacheIdsRelatedIdCount.get(resource.getId()).incrementAndGet();
            }
          }

          if (relatedResourceIds.size() == treeCacheIdsRelatedIdCount.get(resource.getId()).get() && treeCache.containsKey(resourceId)) {
         // if (relatedResourceIds.size() == treeCacheIdsRelatedIdCount.get(resource.getId()).get()) {
            resourceIdIter.remove();
          }
        }
      }

      if (loop > max_loop) {
        log.error("超过最大循环次数，仍有部分数据没有找到上级对象 {}", unknownCache.keySet().stream().collect(Collectors.joining(",")));
        break;
      }
      loop++;
    }


    for (ResourceNode<Resource> resourceNode : rootNode) {
      if (resourceNode.getLevel() == 1) {
        log.info("=> {}", resourceNode.data.getId());
      } else {
        log.info("{}{}", createIndent(resourceNode.getLevel()), resourceNode.data.getId());
      }
    }

    assertThat(treeCache.size(), Matchers.is(87));
  }

  private static String createIndent(int depth) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < depth; i++) {
      sb.append('\t');
    }
    return sb.toString();
  }
}
