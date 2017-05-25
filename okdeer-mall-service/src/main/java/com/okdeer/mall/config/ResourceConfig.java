package com.okdeer.mall.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({ "classpath:/META-INF/spring-dubbo.xml", "classpath:/META-INF/spring-mall-rocketmq.xml",
		 "classpath:/META-INF/spring-mall-job.xml",})
public class ResourceConfig {

}