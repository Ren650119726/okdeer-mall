package com.okdeer.mall.activity.discount.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.activity.discount.entity.ActivityDiscountCommunity;
import com.okdeer.base.dal.IBaseCrudMapper;

/**
 * @DESC: 满减活动小区关联mapper
 * @author wusw
 * @date  2016-01-31 09:33:09
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface ActivityDiscountCommunityMapper extends IBaseCrudMapper {
	/**
	 * 
	 * 批量新增活动与小区关联信息
	 * 
	 * @author wusw
	 * @param list
	 */
	void insertCommunityBatch(@Param("list")List<ActivityDiscountCommunity> list);
	
	/**
	 * 
	 * 根据活动id，删除活动与小区关联信息（物理删除） 
	 *
	 * @author wusw
	 * @param discountId 活动id
	 */
	void deleteByDiscountId(@Param("discountId")String discountId);
	
}