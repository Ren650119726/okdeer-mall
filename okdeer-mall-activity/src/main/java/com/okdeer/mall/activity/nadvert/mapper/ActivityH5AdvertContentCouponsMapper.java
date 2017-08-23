/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityH5AdvertContentCouponsMapper.java
 * @Date 2017-08-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.nadvert.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertContentCoupons;

public interface ActivityH5AdvertContentCouponsMapper extends IBaseMapper {
	
	/**
	 * @Description: 批量保存h5活动内容>>代金券活动
	 * @param entitys void
	 * @throws
	 * @author mengsj
	 * @date 2017年8月11日
	 */
	void batchSave(@Param("list")List<ActivityH5AdvertContentCoupons> entitys);
	
	/**
	 * @Description: 通过活动id，活动内容id查询与活动内容关联的代金券活动
	 * @param activityId
	 * @param contentId
	 * @return List<ActivityH5AdvertContentCoupons>
	 * @throws
	 * @author mengsj
	 * @date 2017年8月12日
	 */
	List<ActivityH5AdvertContentCoupons> findByActId(@Param("activityId")String activityId,@Param("contentId")String contentId);
	
	/**
	 * @Description: 删除与h5活动内容关联的代金券活动
	 * @param activityId
	 * @param contentId void
	 * @throws
	 * @author mengsj
	 * @date 2017年8月12日
	 */
	void deleteByActId(@Param("activityId")String activityId,@Param("contentId")String contentId);
}