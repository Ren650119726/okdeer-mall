/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityH5AdvertContentGoodsMapper.java
 * @Date 2017-08-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.nadvert.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertContentGoods;

public interface ActivityH5AdvertContentGoodsMapper extends IBaseMapper {
	
	/**
	 * @Description: 批量保存h5活动内容类型>>商品列表
	 * @param entitys void
	 * @throws
	 * @author mengsj
	 * @date 2017年8月11日
	 */
	void batchSave(@Param("list")List<ActivityH5AdvertContentGoods> entitys);
	
	/**
	 * @Description: 通过活动id，活动内容id查询与活动内容关联的商品列表
	 * @param activityId
	 * @param contentId
	 * @param goodsType
	 * @return List<ActivityH5AdvertContentGoods>
	 * @throws
	 * @author mengsj
	 * @date 2017年8月12日
	 */
	List<ActivityH5AdvertContentGoods> findByActId(@Param("activityId")String activityId,@Param("contentId")String contentId,@Param("goodsType")Integer goodsType);
	
	/**
	 * @Description: 删除与h5活动内容关联的商品对象
	 * @param activityId
	 * @param contentId void
	 * @throws
	 * @author mengsj
	 * @date 2017年8月12日
	 */
	void deleteByActId(@Param("activityId")String activityId,@Param("contentId")String contentId);

	/**
	 * @Description: 根据h5活动id查询关联的便利店商品列表
	 * @param storeId 店铺id
	 * @param activityId h5活动id
	 * @param contentId h5活动内容id
	 * @return List<ActivityH5AdvertContentGoods>
	 * @throws
	 * @author mengsj
	 * @date 2017年8月26日
	 */
	List<ActivityH5AdvertContentGoods> findBldGoodsByActivityId(@Param("storeId")String storeId,@Param("activityId")String activityId, @Param("contentId")String contentId);

}