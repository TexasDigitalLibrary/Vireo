package org.tdl.vireo.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
 
@Configuration
@ComponentScan(basePackages = {"org.tdl.vireo"})
@PropertySource("classpath:/application.properties")
public class TestDataSourceConfiguration {

}