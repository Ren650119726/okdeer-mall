/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityPrizeWeightMapper.java
 * @Date 2016-12-08 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.prize.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.prize.entity.ActivityPrizeWeight;
import com.okdeer.mall.activity.prize.entity.ActivityPrizeWeightVo;
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
	 * 根据活动id查询所有奖品的比重信息 按顺序查询 顺序与奖品对应 
	 * @param activityId 活动id
	 * @return List<ActivityPrizeWeight>  
	 * @author tuzhd
	 * @date 2016年12月14日
	 */
	public List<ActivityPrizeWeight> findPrizesByactivityId(@Param("activityId")String activityId);
	
	/**
	 * 根据活动id扣减奖品数量
	 * @param id 奖品id
	 * @author tuzhd
	 * @date 2016年12月14日
	 */
	@Transactional(rollbackFor = Exception.class)
	public int updatePrizesNumber(String id);

	/**
	 * @Description: TODO
	 * @param activityPrizeWeightVo
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月11日
	 */
	public List<ActivityPrizeWeightVo> findPrizeRecordList(ActivityPrizeWeightVo activityPrizeWeightVo);
}