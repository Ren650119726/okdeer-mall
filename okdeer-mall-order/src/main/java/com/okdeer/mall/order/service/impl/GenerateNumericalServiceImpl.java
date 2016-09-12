
package com.okdeer.mall.order.service.impl;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.common.utils.DateUtils;
import com.okdeer.mall.order.entity.GenerateNumericalNumber;
import com.okdeer.mall.order.mapper.GenerateNumericalMapper;
import com.okdeer.mall.order.service.GenerateNumericalService;
import com.okdeer.mall.order.service.GenerateNumericalServiceApi;
import com.okdeer.mall.order.utils.OrderNoUtils;
import com.yschome.base.common.exception.ServiceException;
import com.yschome.base.redis.IRedisTemplateWrapper;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-17 15:22:36
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *    重构V4.1			2016-07-14			maojj			添加新的编号生成规则
 *    重构V4.1			2016-07-19			maojj			添加事务控制
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.GenerateNumericalServiceApi")
@org.springframework.stereotype.Service
class GenerateNumericalServiceImpl implements GenerateNumericalService, GenerateNumericalServiceApi {

	// Begin added by maojj 2016-07-14
	private static final Logger logger = LoggerFactory.getLogger(GenerateNumericalServiceImpl.class);

	/**
	 * 编号生成重复次数
	 */
	private static final int REPEAT_TIMES = 32;

	/**
	 * 创建线程副本，存储当前线程循环生成编号的次数
	 */
	private final ThreadLocal<Integer> failCounter = new ThreadLocal<Integer>() {

		@Override
		protected Integer initialValue() {
			return new Integer(1);
		}
	};
	// End added by maojj

	@Resource
	private GenerateNumericalMapper generateNumericalOrderMapper;

	// Begin 增加Redis处理类 add by zengj
	/**
	 * Redis处理类
	 */
	@Autowired
	private IRedisTemplateWrapper<String, Object> redisTemplateWrapper;
	// @Autowired
	// private org.springframework.data.redis.core.RedisTemplate redisTemplate;
	// End 增加Redis处理类 add by zengj

	@Override
	public String generateNumericalNumber(Map<String, String> map) throws ServiceException {

		String gen_num = generateNumericalOrderMapper.generateNumericalNumber(map);

		return gen_num;

	}

	/**
	 * @desc 生成编号
	 *
	 * @param numberType 编号类型
	 * @return
	 * @throws ServiceException
	 */
	@Override
	public String generateNumber(String numberType) throws ServiceException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("numerical_type", numberType);
		map.put("numerical_order", "");
		return generateNumericalOrderMapper.generateNumericalNumber(map);
	}

	@Override
	public String generateRandOrderNo(String numberType) throws ServiceException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("numerical_type", numberType);
		map.put("numerical_order", "");
		return generateNumericalOrderMapper.generateRandOrderNo(map);
	}

	// Begin added by maojj 2016-07-14
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public synchronized String generateNumberAndSave(String numberType) {
		// 生成订单编号
		String orderNo = genNo(numberType);
		GenerateNumericalNumber genNumber = new GenerateNumericalNumber();
		genNumber.setNumericalOrder(orderNo);
		genNumber.setNumericalType(numberType + failCounter.get());
		genNumber.setCreateTime(new Date());
		try {
			generateNumericalOrderMapper.saveNumericalNumber(genNumber);
		} catch (Exception e) {
			// 如果保存失败，则循环生成并保存，循环次数不能超过32次
			if (failCounter.get() > REPEAT_TIMES) {
				return null;
			}
			failCounter.set(failCounter.get() + 1);
			logger.warn("生成编号重复次数：" + failCounter.get() + "---------" + orderNo);
			return generateNumberAndSave(numberType);
		}
		return orderNo;
	}

	/**
	 * @Description: 编号生成规则：2位编号前缀+yyMMdd+8位序列
	 * @param numberType 编号前缀
	 * @return String  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private String genNo(String numberType) {
		StringBuilder sb = new StringBuilder();
		sb.append(numberType);
		sb.append(DateUtils.getDate("yyMMdd"));
		sb.append(genSeq());
		return sb.toString();
	}

	/**
	 * @Description: 生成8位序列。序列生成规则：当前时间到当天0点0分0秒的毫秒偏移量。偏移量不足8位是左补0
	 * @return String  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private String genSeq() {
		long timeOfCurrentDay = getTimeOfCurrentDay();
		long currentTime = System.currentTimeMillis();
		long offsetTime = currentTime - timeOfCurrentDay;
		DecimalFormat df = new DecimalFormat("00000000");
		return df.format(offsetTime);
	}

	/**
	 * @Description:当天0点0分0秒的时间值，以毫秒位单位
	 * @return long  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private long getTimeOfCurrentDay() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}
	// End added by maojj 2016-07-14

	// Begin 1.0.Z 调整订单编号生成规则 add by zengj
	/**
	 * 
	 * @Description: 生成订单编号
	 * @param prefix 订单编号前缀
	 * @param branchCode 店铺机构编码
	 * @param posId POS机ID
	 * @return   新的订单编号
	 * @author zengj
	 * @date 2016年9月5日
	 */
	@Override
	public String generateOrderNo(String prefix, String branchCode, String posId) {
		// 获取当前年月日
		String dateStr = DateUtils.getDate("yyMMdd");
		// redis中存储的key
		String redisKey = prefix + branchCode + dateStr;
		// 实现自增长
		Long seq = redisTemplateWrapper.incr(redisKey);
		// 如果该key是初始化的时候，将该key的过期时间设置为一天，一天后该key会失效
		if (seq == 1L) {
			redisTemplateWrapper.expire(redisKey, 86400);
		}

		// 新的订单号,规则为：为XS+5位店铺编码+2位POS机ID+6位日期+4位流水号
		StringBuilder newOrderNo = new StringBuilder(prefix);
		newOrderNo.append(branchCode);
		newOrderNo.append(posId);
		newOrderNo.append(dateStr);
		newOrderNo.append(String.format("%0" + OrderNoUtils.SEQ_SIZE + "d", seq));
		return newOrderNo.toString();
	}
	// End 1.0.Z 调整订单编号生成规则 add by zengj
}