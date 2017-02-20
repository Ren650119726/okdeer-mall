/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivitySaleRemindMapper.java
 * @Date 2017-02-15 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.coupons.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.coupons.bo.ActivitySaleRemindBo;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleRemind;

/**
 * 
 * ClassName: ActivitySaleRemindMapper 
 * @Description: 活动安全库存提醒人
 * @author tangy
 * @date 2017年2月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     2.1.0          2017年2月15日                               tangy             新增
 */
public interface ActivitySaleRemindMapper extends IBaseMapper {

	/**
	 * 
	 * @Description: 根据活动id查询安全库存预警联系人
	 * @param saleId 活动id
	 * @return List<ActivitySaleRemindBo>  
	 * @author tangy
	 * @date 2017年2月20日
	 */
	List<ActivitySaleRemindBo> findActivitySaleRemindBySaleId(@Param("saleId")String saleId);
	
	/**
	 * 
	 * @Description: 批量插入
	 * @param list   活动商品安全库存提醒人
	 * @return int  
	 * @author tangy
	 * @date 2017年2月20日
	 */
	int insertSelectiveBatch(List<ActivitySaleRemind> list);
	
	/**
	 * 
	 * @Description: 删除活动安全库存提醒联系人
	 * @param saleId 活动id
	 * @return int  
	 * @author tangy
	 * @date 2017年2月20日
	 */
	int deleteBySaleId(@Param("saleId")String saleId);
	
}