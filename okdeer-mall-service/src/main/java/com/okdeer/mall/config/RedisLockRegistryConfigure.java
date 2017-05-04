/** 
 *@Project: okdeer-mall-service 
 *@Author: guocp
 *@Date: 2017年5月3日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

/**
 * Redis锁
 * ClassName: RedisLockRegistryConfiguration 
 * @author guocp
 * @date 2017年5月3日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Configuration
public class RedisLockRegistryConfigure {
	
	@Bean
	public RedisLockRegistry redisLockRegistry(RedisConnectionFactory factory){
		RedisLockRegistry lockRegistry = new RedisLockRegistry(factory, "MALL-SERVICE");
		return lockRegistry;
	}
}
