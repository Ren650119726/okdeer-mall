
package com.okdeer.mall.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.okdeer.base.redis.RedisTemplateWrapperImpl;
import com.okdeer.base.redis.util.KryoRedisSerializer;

/**
 * DESC: RedisTemplate 配置属性类
 * @author LIU.W
 * @DATE 2016年7月25日上午10:50:39
 * @version 0.1.0
 * @param <K>
 * @param <V>
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 */
@Configuration
public class RedisTemplateConfig {

	/**
	 * DESC: 初始化Redis配置
	 * @author LIU.W
	 * @return RedisProperties
	 *//*
		 * // @Bean // public RedisProperties redisProperties() { // return new
		 * RedisPropertiesAdapter(); // }
		 * 
		 * /** DESC: 初始化RedisTemplate
		 * 
		 * @author LIU.W
		 * 
		 * @param factory
		 * 
		 * @return
		 */
	// @Bean
	// public <K, V> RedisTemplate<K, V> redisTemplate(RedisConnectionFactory
	// factory) {
	// RedisTemplate<K, V> template = new RedisTemplate<K, V>();
	// template.setConnectionFactory(factory);
	// template.setKeySerializer(new StringRedisSerializer());
	// return template;
	// }

	/**
	 * DESC: 初始化RedisTemplateWrapper
	 * @author LIU.W
	 * @param redisTemplate
	 * @return
	 */
	@Bean(name = "redisTemplateWrapper")
	public <K, V> RedisTemplateWrapperImpl<K, V> redisTemplateWrapper(RedisTemplate<K, V> redisTemplate) {
		RedisTemplateWrapperImpl<K, V> redisTemplateWrapper = new RedisTemplateWrapperImpl<K, V>();
		redisTemplateWrapper.setRedisTemplate(redisTemplate);
		redisTemplateWrapper.setValueSerializer(new KryoRedisSerializer<V>());
		return redisTemplateWrapper;
	}

	/**
	 * DESC: redis配置适配器
	 * @author LIU.W
	 * @DATE 2016年7月25日下午15:32:55
	 * @version 0.1.0
	 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
	 */
	@ConfigurationProperties(prefix = "redis")
	public class RedisPropertiesAdapter extends RedisProperties {

	}
}
