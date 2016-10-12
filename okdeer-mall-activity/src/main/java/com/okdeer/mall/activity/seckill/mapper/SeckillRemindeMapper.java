/** 
 *@Project: okdeer-mall-activity 
 *@Author: yangq
 *@Date: 2016年9月27日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.seckill.mapper;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.activity.seckill.entity.SeckillReminde;

/**
 * ClassName: SeckillRemindeMapper 
 * @Description: TODO
 * @author yangq
 * @date 2016年9月27日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface SeckillRemindeMapper {

	void insertSeckillReminde(SeckillReminde seckillReminde);
	
	void updateRemindeStatus(@Param("seckillId") String seckillId, @Param("settingValue") Integer settingValue);
	
	SeckillReminde selectSeckillRemindeByActivityId(@Param("activityId") String activityId);
	
	void updateSeckillReminde(SeckillReminde reminde);
	
}
