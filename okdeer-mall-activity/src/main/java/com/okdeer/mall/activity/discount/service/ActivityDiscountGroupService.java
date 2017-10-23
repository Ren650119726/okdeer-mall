/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityDiscountGroupMapper.java
 * @Date 2017-10-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.discount.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.archive.goods.base.entity.GoodsSpuCategory;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.discount.dto.ActivityGoodsGroupSkuDto;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountGroup;
/**
 * ClassName: ActivityDiscountGroupMapper 
 * @Description: 团购商品关联表接口
 * @author tuzhd
 * @date 2017年10月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *	   2.6.3			2017-10-17			tuzhd			团购商品关联表接口
 */
public interface ActivityDiscountGroupService extends IBaseService{
	
	/**
	 * @Description: 批量新增团购活动
	 * @param list   
	 * @author tuzhd
	 * @date 2017年10月12日
	 */
	void batchAdd(@Param("list")List<ActivityDiscountGroup> list);
	
	/**
	 * @Description: 根据活动id删除团购商品记录
	 * @param activityId
	 * @return int  
	 * @author tuzhd
	 * @date 2017年10月12日
	 */
	int deleteByActivityId(@Param("activityId")String activityId);
	
	/**
	 * @Description: 查询活动业务团购商品记录
	 * @author tuzhd
	 * @date 2017年10月12日日
	 */
	List<ActivityDiscountGroup> findByActivityId(@Param("activityId")String activityId);
	
	/**
	 * @Description: 根据店铺商品Id、活动Id查询团购商品信息
	 * @param activityId
	 * @param storeSkuId
	 * @return   
	 * @author tuzhd
	 * @date 2017年10月13日
	 */
	ActivityDiscountGroup findByActivityIdAndSkuId(@Param("activityId")String activityId,@Param("storeSkuId")String storeSkuId);

	/**
	 * @Description: 根据开团状态查询团购商品的分类
	 * @param status
	 * @author tuzhd
	 * @date 2017年10月17日
	 */
	PageUtils<GoodsSpuCategory> findGroupGoodsCategory(Integer pageNumber, Integer pageSize);

	/**
	 * @Description: 根据开团状态查询团购商品的分类
	 * @param status
	 * @author tuzhd
	 * @date 2017年10月17日
	 */
	PageUtils<ActivityGoodsGroupSkuDto> findGroupGoodsList(Map<String, Object> param, Integer pageNumber,
			Integer pageSize);

}