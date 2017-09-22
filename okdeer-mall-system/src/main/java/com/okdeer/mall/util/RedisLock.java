package com.okdeer.mall.util;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RedisLock {
	private static final Logger logger = LoggerFactory.getLogger(RedisLock.class);

	/**
	 * 锁的键前缀
	 */
	private static final String LOCK_KEY_PREFIX = "MALL:LOCK";

	/**
	 * 锁的键格式：前缀 + 应用提供的key
	 */
	private static final String LOCK_KEY_FORMAT = "%s:%s";

	/**
	 * 默认过期时间60s
	 */
	private static final long DEFAULT_EXPIRE_AFTER = 60;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	public boolean tryLock(List<String> keyList, long timeOut){
		return tryLock(keyList,timeOut,TimeUnit.SECONDS);
	}

	/**
	 * @Description: 批量加锁
	 * @param keyList
	 * @param timeOut
	 * @param unit
	 * @return   
	 * @author maojj
	 * @date 2017年9月18日
	 */
	public boolean tryLock(List<String> keyList, long timeOut, TimeUnit unit) {
		if (CollectionUtils.isEmpty(keyList)) {
			return false;
		}
		try {
			long expire = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(timeOut, unit);
			boolean acquired = false;
			for (String key : keyList) {
				while (!(acquired = this.obtainLock(key, timeOut, unit)) && System.currentTimeMillis() < expire) {
					Thread.sleep(100);
				}
				if(!acquired){
					// 如果获取锁失败，则直接返回false
					return false;
				}
			} 
			return true;
		} catch (Exception e) {
			logger.error("根据键{}获取锁失败:{}",keyList,e);
			return false;
		}
	}
	
	public boolean tryLock(String key){
		return tryLock(key,DEFAULT_EXPIRE_AFTER);
	}

	public boolean tryLock(String key, long timeOut) {
		return tryLock(key, timeOut, TimeUnit.SECONDS);
	}

	public boolean tryLock(String key, long timeOut, TimeUnit unit) {
		try {
			long expire = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(timeOut, unit);
			boolean acquired = false;
			while (!(acquired = this.obtainLock(key, timeOut, unit)) && System.currentTimeMillis() < expire) {
				Thread.sleep(100);
			}
			return acquired;
		} catch (Exception e) {
			logger.error("根据键{}获取锁失败:{}",key,e);
			return false;
		}
	}

	/**
	 * @Description:使用redis的setnx(), get(), getset()方法，用于分布式锁，解决死锁问题
	 * 步骤一：setnx(lockKey,当前时间+过期超时时间)，如果返回true，则获取锁成功，如果返回false没有获取锁，转到第二步
	 * 步骤二：get(lockKey)获取oldExpireTime,并将这个value与当时间系统时间进行比较，如果小于当前系统时间，认为这个锁已经过期，可以允许别的请求重新获取。
	 * 步骤三：计算newExpireTime=当前时间+过期超时时间，然后getset(lockKey,newExpireTime)会返回当前lockKey的值currentExpireTime
	 * 步骤四：判断currentExpireTime与oldExpireTime是否相等，如果相等，说明getset设置成功，获取到锁。如果不等，说明锁又被别的请求取走。返回失败或者重试
	 * 步骤五：获取到锁之后，线程处理自己的业务，处理完毕，比较自己处理的时间和对于锁设置的超时时间，如果小于锁设置的超时时间，delete释放锁，如果大于，则不用处理。
	 * @param key 键值
	 * @param timeout 过期时间
	 * @param unit 时间单位
	 * @return   
	 * @author maojj
	 * @date 2017年9月18日
	 */
	public boolean obtainLock(String key, long timeout, TimeUnit unit) {
		// 过期时间
		long expire = TimeUnit.MILLISECONDS.convert(timeout, unit);
		// 锁状态
		boolean lockStatus = false;
		// 键的值
		long value = System.currentTimeMillis() + expire;
		long oldExpireTime = 0L;
		long newExpireTime = 0L;
		long currentExpireTime = 0L;
		// 拿到锁的key
		String lockKey = wrapp(key);
		lockStatus = stringRedisTemplate.boundValueOps(lockKey).setIfAbsent(String.valueOf(value));
		if (!lockStatus) {
			oldExpireTime = Long.parseLong(get(lockKey));
			if (oldExpireTime < System.currentTimeMillis()) {
				// 如果redis中的过期时间小于当前时间，说明锁已过期，可被用于使用
				// 计算新生成的锁对应的过期时间
				newExpireTime = System.currentTimeMillis() + expire;
				currentExpireTime = Long.parseLong(getSet(lockKey, String.valueOf(newExpireTime)));
				return currentExpireTime == oldExpireTime;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	private String wrapp(String key) {
		return String.format(LOCK_KEY_FORMAT, LOCK_KEY_PREFIX, key);
	}

	private String get(String key) {
		String value = stringRedisTemplate.boundValueOps(key).get();
		return StringUtils.isEmpty(value) ? "0" : value;
	}

	private String getSet(String key, String value) {
		String oldValue = stringRedisTemplate.boundValueOps(key).getAndSet(value);
		return StringUtils.isEmpty(oldValue) ? "0" : oldValue;
	}

	public void unLock(List<String> keyList){
		if(CollectionUtils.isEmpty(keyList)){
			return;
		}
		keyList.forEach(key -> unLock(key));
	}
	
	public void unLock(String key) {
		try {
			String lockKey = wrapp(key);
			long oldExpireTime = Long.parseLong(get(lockKey));
			if (oldExpireTime > System.currentTimeMillis()) {
				stringRedisTemplate.delete(lockKey);
			}
		} catch (Exception e) {
			// 如果释放锁时发生异常，只需要输出日志即可
			logger.error("释放锁发生异常：{}",e);
		}
	}
}
