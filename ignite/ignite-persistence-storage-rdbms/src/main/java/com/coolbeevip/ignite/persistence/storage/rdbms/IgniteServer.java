package com.coolbeevip.ignite.persistence.storage.rdbms;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.store.jdbc.CacheJdbcPojoStoreFactory;
import org.apache.ignite.cache.store.jdbc.JdbcType;
import org.apache.ignite.cache.store.jdbc.JdbcTypeField;
import org.apache.ignite.cache.store.jdbc.dialect.MySQLDialect;
import org.apache.ignite.configuration.CacheConfiguration;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.coolbeevip.ignite.persistence.storage.rdbms.IgniteConstant.CACHE_NAME;
import static com.coolbeevip.ignite.persistence.storage.rdbms.IgniteConstant.CACHE_TABLE_NAME;
import static com.coolbeevip.ignite.persistence.storage.rdbms.IgniteConstant.SQL_SCHEMA;

public class IgniteServer {

  public static void main(String[] args){
    destroyCache();
    //createCache();
  }

  private static void createCache(){
    try(Ignite ignite = Ignition.start("/Users/zhanglei/coolbeevip/tutorials/ignite/ignite-persistence-storage-rdbms/src/main/resources/ignite-config.xml")){

      CacheConfiguration<Integer, Address> cacheConfiguration = new CacheConfiguration<>();
      cacheConfiguration.setName(CACHE_NAME);
      cacheConfiguration.setSqlSchema(SQL_SCHEMA);
      cacheConfiguration.setCacheMode(CacheMode.PARTITIONED);
      cacheConfiguration.setAtomicityMode(CacheAtomicityMode.ATOMIC);
      cacheConfiguration.setReadThrough(true);
      cacheConfiguration.setWriteThrough(true);

      CacheJdbcPojoStoreFactory cacheStoreFactory = new CacheJdbcPojoStoreFactory();
      cacheStoreFactory.setDataSourceBean("nc_resource");
      cacheStoreFactory.setDialect(new MySQLDialect());

      JdbcType jdbcType = new JdbcType();
      jdbcType.setCacheName(CACHE_NAME);
      jdbcType.setKeyType(String.class);
      jdbcType.setValueType(Address.class);
      jdbcType.setDatabaseTable(CACHE_TABLE_NAME);

      // key field
      JdbcTypeField keyField = new JdbcTypeField(Types.VARCHAR,"uuid",String.class,"uuid");
      jdbcType.setKeyFields(keyField);

      // value field
      List<JdbcTypeField> valuesFields = new ArrayList<>();
      valuesFields.add(new JdbcTypeField(Types.INTEGER,"address_level",Integer.class,"addressLevel"));
      valuesFields.add(new JdbcTypeField(Types.VARCHAR,"name",String.class,"name"));
      jdbcType.setValueFields(valuesFields.toArray(new JdbcTypeField[0]));

      cacheStoreFactory.setTypes(jdbcType);
      cacheConfiguration.setCacheStoreFactory(cacheStoreFactory);

      // queryEntities
      List<QueryEntity> queryEntities = new ArrayList<>();
      QueryEntity queryEntity = new QueryEntity();
      queryEntity.setKeyType(String.class.getName());
      queryEntity.setValueType(Address.class.getName());
      queryEntity.setKeyFieldName("uuid");
      queryEntity.setKeyFields(new HashSet(Arrays.asList("uuid")));
      queryEntity.addQueryField("uuid",String.class.getName(),null);
      queryEntity.addQueryField("name",String.class.getName(),null);
      queryEntity.addQueryField("address_level",Integer.class.getName(),null);
      queryEntities.add(queryEntity);
      cacheConfiguration.setQueryEntities(queryEntities);

      IgniteCache<Integer, Address> addressCache = ignite.getOrCreateCache(cacheConfiguration);
      addressCache.loadCache(null);
      TimeUnit.MINUTES.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static void destroyCache(){
    try(Ignite ignite = Ignition.start("/Users/zhanglei/coolbeevip/tutorials/ignite/ignite-persistence-storage-rdbms/src/main/resources/ignite-config.xml")){
      ignite.destroyCache(CACHE_NAME);
    }
  }
}