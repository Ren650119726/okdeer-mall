package com.okdeer.mall.activity.discount.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.activity.discount.entity.ActivityDiscountRelationStore;
import com.okdeer.base.dal.IBaseCrudMapper;

/**
 * @DESC: 满减（满折）活动的范围关联的店铺mapper
 * @author wusw
 * @date  2016-03-05 11:00:23
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface ActivityDiscountRelationStoreMapper extends IBaseCrudMapper {
	
	/**
	 * 
	 * 批量新增满减（满折）活动的范围关联店铺信息
	 *
	 * @param list
	 */
	void insertRelationStoreBatch(@Param("list")List<ActivityDiscountRelationStore> list);
	
	/**
	 * 
	 *  根据活动id，删除满减（满折）活动的范围关联店铺信息（物理删除） 
	 *
	 * @param discountId
	 */
	void deleteByDiscountId(@Param("discountId")String discountId);
	
	
}