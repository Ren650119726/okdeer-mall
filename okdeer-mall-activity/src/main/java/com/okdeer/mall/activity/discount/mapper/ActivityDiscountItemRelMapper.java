/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityJoinRecordMapper.java
 * @Date 2017-10-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.discount.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.archive.goods.store.dto.GoodsStoreActivitySkuDto;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.discount.dto.ActivityGoodsParamDto;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountItemRel;

public interface ActivityDiscountItemRelMapper extends IBaseMapper {

	/**
	 * @Description: 通过活动id删除
	 * @author zhangkn
	 * @date 2017年12月6日
	 */
	void deleteByActivityId(String activityId);
	
	/**
	 * @Description: 批量添加
	 * @param list
	 * @author zhangkn
	 * @date 2017年12月6日
	 */
	void addBatch(List<ActivityDiscountItemRel> list);
	
	/**
	 * @Description: 通过活动id加载列表
	 * @param activityId
	 * @return
	 * @author zhangkn
	 * @date 2017年12月6日
	 */
	List<ActivityDiscountItemRel> findByActivityId(@Param("activityId")String activityId,
			@Param("activityItemId")String activityItemId);
	
	/**
	 * @Description: 查询梯度下非正常价格购买商品 如满赠或加价购
	 * @param activityId 活动id
	 * @param activityItemId 梯度id
	 * @param storeId 店铺id
	 * @author tuzhd
	 * @date 2017年12月12日
	 */
	List<ActivityDiscountItemRel> findNotNormalById(@Param("activityId")String activityId,
			@Param("activityItemId")String activityItemId,@Param("storeId")String storeId);
	
	/**
	 * @Description: 查询梯度下非正常价格购买商品列表
	 * @param activityId 活动id
	 * @param activityItemId 梯度id
	 * @param storeId 店铺id
	 * @author tuzhd
	 * @date 2017年12月12日
	 */
	List<GoodsStoreActivitySkuDto> findGoodsByActivityId(ActivityGoodsParamDto dto);
	
	
	/**
	 * @Description: 根据id集合查询
	 * @param idList
	 * @author tuzhd
	 * @date 2017年7月27日
	 */
	List<ActivityDiscountItemRel> findByActivityIdList(@Param("idList")List<String> idList);
}