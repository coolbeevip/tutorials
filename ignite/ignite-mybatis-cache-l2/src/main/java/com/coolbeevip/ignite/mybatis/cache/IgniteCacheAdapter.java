package com.coolbeevip.ignite.mybatis.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.Cache;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteIllegalStateException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.util.concurrent.locks.ReadWriteLock;

@Slf4j
public final class IgniteCacheAdapter implements Cache {
  private final String id;
  private final ReadWriteLock readWriteLock = new DummyReadWriteLock();
  private static final Ignite ignite;
  private final IgniteCache<Object, Object> cache;
  public static String CFG_PATH = IgniteCacheAdapter.class.getClassLoader().getResource("ignite-local-cache-config.xml").getFile();
  //public static String CFG_PATH = IgniteCacheAdapter.class.getClassLoader().getResource("ignite-client-cache-config.xml");
  //public static String CFG_PATH = IgniteCacheAdapter.class.getClassLoader().getResource("ignite-cluster-cache-config.xml");

  static {
    boolean started = false;
    try {
      Ignition.ignite();
      started = true;
    } catch (IgniteIllegalStateException e) {
      log.debug("Using the Ignite instance that has been already started.");
      log.trace("" + e);
    }
    if (started) {
      ignite = Ignition.ignite();
    } else {
      ignite = Ignition.start(CFG_PATH);
    }
  }

  @SuppressWarnings("unchecked")
  public IgniteCacheAdapter(String id) {
    if (id == null) {
      throw new IllegalArgumentException("Cache instances require an ID");
    }

    CacheConfiguration<Object, Object> cacheCfg = null;

    try {
      DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
      new XmlBeanDefinitionReader(factory).loadBeanDefinitions(new FileSystemResource(new File(CFG_PATH)));
      cacheCfg = (CacheConfiguration<Object, Object>) factory.getBean("templateCacheCfg");
      cacheCfg.setEvictionPolicy(null);
      cacheCfg.setCacheLoaderFactory(null);
      cacheCfg.setCacheWriterFactory(null);
      cacheCfg.setName(id);
      cacheCfg.setStatisticsEnabled(true);
    } catch (NoSuchBeanDefinitionException | BeanDefinitionStoreException e) {
      // initializes the default cache.
      log.warn("Initializing the default cache. Consider properly configuring '" + CFG_PATH + "' instead.");
      log.trace("" + e);

      cacheCfg = new CacheConfiguration<>(id);
    }

    cache = ignite.getOrCreateCache(cacheCfg);

    this.id = id;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public void putObject(Object key, Object value) {
    cache.put(key, value);
  }

  @Override
  public Object getObject(Object key) {
    return cache.get(key);
  }

  @Override
  public Object removeObject(Object key) {
    return cache.remove(key);
  }

  @Override
  public void clear() {
    cache.clear();
  }

  @Override
  public int getSize() {
    return cache.size(CachePeekMode.PRIMARY);
  }

  @Override
  public ReadWriteLock getReadWriteLock() {
    return readWriteLock;
  }
}