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

	private RedisDefaultLockRegistry redisDefaultLockRegistry;

	@Bean(destroyMethod = "distroyRedisDefaultLock")
	public RedisLockRegistry redisLockRegistry(RedisConnectionFactory factory) {
		redisDefaultLockRegistry = new RedisDefaultLockRegistry();
		return new RedisLockRegistry(factory, "MALL-SERVICE", DEFAULT_EXPIRE_AFTER,
				redisDefaultLockRegistry);
	}

	public void distroyRedisDefaultLock() {
		logger.debug("销毁distroyRedisDefaultLock.......");
		if (redisDefaultLockRegistry != null) {
			redisDefaultLockRegistry.distory();
		}
	}

	private class RedisDefaultLockRegistry implements LockRegistry {

		/**
		 * 健值过期时间
		 */
		private static final long KEY_EXPIRE_TIME = 600000;

		Map<Object, LockInfo> lockTable = Maps.newConcurrentMap();

		private Thread thread = null;

		public RedisDefaultLockRegistry() {
			thread = new Thread(new RemoveExpireLockTask());
			thread.start();
		}

		public void distory() {
			//清空数据
			lockTable.clear();
			//中断线程
			if (thread != null && !thread.isInterrupted()) {
				thread.interrupt();
			}
		}

		private class RemoveExpireLockTask implements Runnable {

			@Override
			public void run() {
				while (true) {
					Map<Object, LockInfo> lockTables = RedisDefaultLockRegistry.this.lockTable;
					for (Object localkey : lockTables.keySet()) {
						LockInfo topicConfig = lockTables.get(localkey);
						if (isExpire(topicConfig.getLastGetTime())) {
							// 健已经超过10分钟没有使用了，删除掉
							LockInfo lockInfo = lockTable.remove(localkey);
							logger.debug("{}对应的本地锁已经删除，最后一次使用时间:{}", localkey, lockInfo.getLastGetTime());
						}
					}
					try {
						synchronized (this) {
							wait(60000);
						}
					} catch (InterruptedException e) {
						logger.error("线程中断", e);
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
				lockInfo.setLockKey(lockKey);
				lockInfo.setLock(new ReentrantLock());
				lockTable.put(lockKey, lockInfo);
			}
			lockInfo.setLastGetTime(new Date());
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
