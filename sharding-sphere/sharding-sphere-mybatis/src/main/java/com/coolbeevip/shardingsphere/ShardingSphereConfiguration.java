package com.coolbeevip.shardingsphere;

import com.coolbeevip.shardingsphere.configuration.DataSourceConfiguration;
import com.coolbeevip.shardingsphere.configuration.MybatisConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@EnableAutoConfiguration
@Configuration
@ImportAutoConfiguration({DataSourceConfiguration.class, MybatisConfiguration.class})
public class ShardingSphereConfiguration {

}