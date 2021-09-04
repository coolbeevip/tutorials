package com.coolbeevip.shardingsphere.configuration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.coolbeevip.shardingsphere.mybatis.repository")
public class MybatisConfiguration {

}