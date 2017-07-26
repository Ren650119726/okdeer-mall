/** 
 *@Project: yschome-mall-activity 
 *@Author: lijun
 *@Date: 2016年7月13日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.seckill.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckillRecord;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillRecordServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.activity.seckill.mapper.ActivitySeckillRecordMapper;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillRecordService;

/**
 * ClassName: ActivitySeckillRecordServiceImpl 
 * @Description: 秒杀活动记录service实现类
 * @author lijun
 * @date 2016年7月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *      重构 4.1         2016年7月13日                                 lijun               新增
 *      重构 4.1         2016年7月14日                                 zengj               新增方法
 *      重构 4.1         2016年7月20日                                 luosm               新增方法
 *      重构 4.1        2016年7月23日                                 	wushp             逻辑删除秒杀记录
 */
@Service(version = "1.0.0",interfaceName = "com.okdeer.mall.activity.seckill.service.ActivitySeckillRecordServiceApi")
public class ActivitySeckillRecordServiceImpl implements ActivitySeckillRecordService, ActivitySeckillRecordServiceApi {
	
	/**
	 * 日志输出
	 */
	private static final Logger logger = LoggerFactory.getLogger(ActivitySeckillRecordServiceImpl.class);
	
	@Autowired
	private ActivitySeckillRecordMapper activitySeckillRecordMapper;

	/**
	 * 统计秒杀数量
	 * @Description: 
	 * @param params
	 * @return int 购买的秒杀数量
	 * @throws
	 * @author zengj
	 * @date 2016年7月14日
	 */
	@Override
	public int findSeckillCount(Map<String, Object> params) {
		return activitySeckillRecordMapper.findSeckillCount(params);
	}
	
	/**
	 * 新增秒杀活动买家购买记录
	 * @Description: 
	 * @param activitySeckillRecord   
	 * @return void  
	 * @throws
	 * @author zengj
	 * @date 2016年7月14日
	 */
	@Override
	public void add(ActivitySeckillRecord activitySeckillRecord) {
		activitySeckillRecordMapper.add(activitySeckillRecord);
	}
	
	// begin add by wushp
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.seckill.service.ActivitySeckillRecordServiceApi#updateStatusBySeckillId(java.lang.String)
	 */
	@Override
	public int updateStatusBySeckillId(String tradeOrderId) throws ServiceException {
		return activitySeckillRecordMapper.updateStatusBySeckillId(tradeOrderId);
	}
	// end add by wushp

	//begin add by luosm 2016-07-20
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.seckill.service.ActivitySeckillRecordService#findByActivitySeckillId(java.lang.String)
	 */
	@Override
	public int findByActivitySeckillId(String seckillId) {
		return activitySeckillRecordMapper.findByActivitySeckillId(seckillId);
	}
	//end add by luosm 2016-07-20

}
