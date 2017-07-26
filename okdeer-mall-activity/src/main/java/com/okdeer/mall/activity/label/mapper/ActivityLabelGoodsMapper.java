package com.okdeer.mall.activity.label.mapper;

import java.util.List;

import com.okdeer.mall.activity.label.entity.ActivityLabelGoods;

/**
 * @pr yscm
 * @desc 服务标签活动关联商品
 * @author zhangkn
 * @date 2016-11-04 下午3:12:43
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 */
public interface ActivityLabelGoodsMapper {
	

	void addBatch(List<ActivityLabelGoods> activityLabelGoodsList) throws Exception;

	void deleteByActivityId(String activityId);
	
	List<ActivityLabelGoods> listByActivityId(String activityId);
	
}
