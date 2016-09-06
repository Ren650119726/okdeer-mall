/** 
 *@Project: yschome-mall-activity 
 *@Author: lijun
 *@Date: 2016年7月13日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */    

package com.okdeer.mall.activity.seckill.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckillRange;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillRangeServiceApi;
import com.okdeer.mall.activity.seckill.mapper.ActivitySeckillRangeMapper;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillRangeService;

/**
 * ClassName: ActivitySeckillRangeServiceImpl 
 * @Description: 秒杀活动范围service实现类
 * @author lijun
 * @date 2016年7月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *      重构 4.1         2016年7月13日                                 lijun               新增
 *
 */
@Service(version = "1.0.0",interfaceName = "com.okdeer.mall.activity.seckill.service.ActivitySeckillRangeServiceApi")
public class ActivitySeckillRangeServiceImpl implements ActivitySeckillRangeService, ActivitySeckillRangeServiceApi {

	/**
	 * 日志输出
	 */
	private static final Logger logger = LoggerFactory.getLogger(ActivitySeckillRangeServiceImpl.class);
	
	/**
	 * 注入秒杀活动范围Mapper
	 */
	@Autowired
	ActivitySeckillRangeMapper activitySeckillRangeMapper;

	
	@Override
	public List<ActivitySeckillRange> findSeckillRangeAllBySeckillId(String seckillId) throws Exception {
		return activitySeckillRangeMapper.findBySeckillId(seckillId);
	}

}
