/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityPrizeRecordMapper.java
 * @Date 2016-12-08 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.prize.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.prize.entity.ActivityPrizeRecord;
import com.okdeer.mall.activity.prize.entity.ActivityPrizeRecordVo;

/**
 * ClassName: ActivityPrizeRecordMapper 
 * @Description: 活动中奖记录mapper
 * @author xuzq01
 * @date 2016年12月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2.3			2016年12月8日		xuzq01				活动中奖记录mapper
 */
 
public interface ActivityPrizeRecordMapper extends IBaseMapper {

	/**
	 * 
	 * @Description: 通过用户id查询中奖记录
	 * @param userId
	 * @return   
	 * @author xuzq01
	 * @date 2016年12月8日
	 */
	public List<ActivityPrizeRecordVo> findByUserId(@Param("userId")String userId,@Param("activityId")String activityId);
	
	/**
	 * 
	 * @Description: 展示最新的十条中奖记录
	 * @param activityId
	 * @return   
	 * @author xuzq01
	 * @date 2016年12月8日
	 */
	public List<ActivityPrizeRecord> findPrizeRecord();
	
	/**
	 * 
	 * @Description: 通过奖品id查询奖品中奖数量
	 * @param prizeId
	 * @return   
	 * @author xuzq01
	 * @date 2016年12月8日
	 */
	public int findCountByPrizeId(String prizeId);

	/**
	 * @Description: 获取奖品记录列表
	 * @param activityPrizeRecordVo
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月10日
	 */
	public List<ActivityPrizeRecordVo> findPrizeRecordList(ActivityPrizeRecordVo activityPrizeRecordVo);
	
	/**
	 * @Description: 批量更新发放状态
	 * @param map   
	 * @return void  
	 * @author tuzhd
	 * @date 2017年6月20日
	 */
	public void updateBathOffer(Map<String,Object> map);
	
}