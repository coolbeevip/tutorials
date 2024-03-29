package com.coolbeevip.shardingsphere.configuration;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@MapperScan("com.coolbeevip.shardingsphere.mybatis.repository")
public class MybatisConfiguration {

  @Bean
  public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
    SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
    factoryBean.setDataSource(dataSource);

    Properties mybatisProperties = new Properties();
    mybatisProperties.setProperty("mapUnderscoreToCamelCase", "true");
    factoryBean.setConfigurationProperties(mybatisProperties);
    return factoryBean.getObject();
  }
}