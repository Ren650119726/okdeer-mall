package com.okdeer.mall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude=DataSourceAutoConfiguration.class)
@ComponentScan(basePackages="com.okdeer")
public class Application {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		long statTime = System.currentTimeMillis();
		logger.info("开始启动服务~~~~~~~~~~~~~~~~");
		SpringApplication.run(Application.class, args);
		long time = System.currentTimeMillis() - statTime;
		logger.info("服务启动完成,消耗时间：" + time + "毫秒");
	}

}
