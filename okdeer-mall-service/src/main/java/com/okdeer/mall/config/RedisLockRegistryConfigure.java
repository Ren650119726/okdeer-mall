/** 
 *@Project: okdeer-mall-service 
 *@Author: guocp
 *@Date: 2017年5月3日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */

package com.okdeer.mall.config;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.util.Assert;

import com.google.common.collect.Maps;

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

	private static Logger logger = LoggerFactory.getLogger(RedisLockRegistryConfigure.class);

	private static final long DEFAULT_EXPIRE_AFTER = 60000;

	@Bean
	public RedisLockRegistry redisLockRegistry(RedisConnectionFactory factory) {
		RedisLockRegistry lockRegistry = new RedisLockRegistry(factory, "MALL-SERVICE", DEFAULT_EXPIRE_AFTER,
				new RedisDefaultLockRegistry());
		return lockRegistry;
	}

	private class RedisDefaultLockRegistry implements LockRegistry {

		/**
		 * 健值过期时间
		 */
		private static final long KEY_EXPIRE_TIME = 600000;

		Map<Object, LockInfo> lockTable = Maps.newConcurrentMap();

		public RedisDefaultLockRegistry() {
			Thread thread = new Thread(new RemoveExpireLockTask());
			thread.setDaemon(true);
			thread.start();
		}

		private class RemoveExpireLockTask implements Runnable {
			
			@Override
			public void run() {
				while(true){
					Map<Object, LockInfo> lockTable = RedisDefaultLockRegistry.this.lockTable;
					for (Object localkey : lockTable.keySet()) {
						LockInfo topicConfig = lockTable.get(localkey);
						if (isExpire(topicConfig.getLastGetTime())) {
							// 健已经超过10分钟没有使用了，删除掉
							LockInfo lockInfo = lockTable.remove(localkey);
							logger.info("{}对应的本地锁已经删除，最后一次使用时间:{}", localkey, lockInfo.getLastGetTime());
						}
					}
					try {
						wait(60000);
					} catch (InterruptedException e) {
						e.printStackTrace();
						Thread.currentThread().interrupt();
						break;
					}
				}
			}
			private boolean isExpire(Date date) {
				return System.currentTimeMillis() > (date.getTime() + KEY_EXPIRE_TIME);
			}
		}

		@Override
		public Lock obtain(Object lockKey) {
			Assert.notNull(lockKey, "'lockKey' must not be null");
			LockInfo lockInfo = lockTable.get(lockKey);
			if (lockInfo == null) {
				lockInfo = new LockInfo();
				lockInfo.setLastGetTime(new Date());
				lockInfo.setLockKey(lockKey);
				lockInfo.setLock(new ReentrantLock());
				lockTable.put(lockKey, lockInfo);
			}
			return lockInfo.getLock();
		}
	}

	private class LockInfo {

		private Object lockKey;

		private Lock lock;

		private Date lastGetTime;

		public Object getLockKey() {
			return lockKey;
		}

		public void setLockKey(Object lockKey) {
			this.lockKey = lockKey;
		}

		public Lock getLock() {
			return lock;
		}

		public void setLock(Lock lock) {
			this.lock = lock;
		}

		public Date getLastGetTime() {
			return lastGetTime;
		}

		public void setLastGetTime(Date lastGetTime) {
			this.lastGetTime = lastGetTime;
		}

	}

}
