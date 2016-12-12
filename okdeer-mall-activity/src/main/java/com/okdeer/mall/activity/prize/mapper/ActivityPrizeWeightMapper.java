/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityPrizeWeightMapper.java
 * @Date 2016-12-08 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.prize.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.prize.entity.ActivityPrizeWeight;
/**
 * 
 * ClassName: ActivityPrizeWeightMapper 
 * @Description: 活动奖品权重表mapper
 * @author xuzq01
 * @date 2016年12月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2.3			2016年12月8日		xuzq01				活动奖品权重表mapper
 */
public interface ActivityPrizeWeightMapper extends IBaseMapper {

	/**
	 * 
	 * @Description: 查询所有奖品的比重信息
	 * @return   
	 * @author xuzq01
	 * @date 2016年12月8日
	 */
	public List<ActivityPrizeWeight> findAll();
}