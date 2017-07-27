/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityAdvertDrawMapper.java
 * @Date 2017-04-11 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.prize.mapper;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.prize.entity.ActivityAdvertDraw;
/**
 * ClassName: ActivityAdvertDrawMapper 
 * @Description: 抽奖活动及H5活动持久化类
 * @author tuzhd
 * @date 2017年4月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 		V2.2.0			2017-4-13			tuzhd			抽奖活动及H5活动持久化类
 */
public interface ActivityAdvertDrawMapper extends IBaseMapper {
	
	/**
	 * @Description: 根据活动id及模板编号查询关联的抽奖活动
	 * @return ActivityAdvertSale  
	 * @author tuzhd
	 * @date 2017年4月13日
	 */
    ActivityAdvertDraw findAdvertDrawByIdNo(@Param("modelNo") int modelNo,@Param("activityAdvertId") String activityAdvertId);
	
	/**
	 * @Description: 删除关联抽奖信息by活动id
	 * @param activityAdvertId 活动id
	 * @return int  
	 * @throws
	 * @author tuzhd
	 * @date 2017年4月19日
	 */
	int deleteByActivityAdvertId(String activityAdvertId);
    
}