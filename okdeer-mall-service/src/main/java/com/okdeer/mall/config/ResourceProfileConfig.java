package com.okdeer.mall.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
@ImportResource({"classpath:/META-INF/spring-mall-conf.xml"})
public class ResourceProfileConfig {

}