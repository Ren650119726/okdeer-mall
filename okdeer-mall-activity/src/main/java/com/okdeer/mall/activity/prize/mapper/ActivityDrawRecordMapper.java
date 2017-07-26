/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityDrawRecordMapper.java
 * @Date 2016-12-08 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.prize.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;

/**
 * ClassName: ActivityDrawRecordMapper 
 * @Description: 活动抽奖记录mapper
 * @author xuzq01
 * @date 2016年12月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2.3			2016年12月8日		xuzq01				活动抽奖记录mapper
 */
 
public interface ActivityDrawRecordMapper extends IBaseMapper {
	
	/**
	 * 
	 * @Description: 通过用户id和活动id查询抽奖次数
	 * @param userId
	 * @param luckDrawId 模板后为抽奖活动id
	 * @return   
	 * @author xuzq01
	 * @date 2016年12月8日
	 */
	int findCountByUserIdAndActivityId(@Param("userId")String userId,@Param("ids")List<String> luckDrawId);
	
	

}