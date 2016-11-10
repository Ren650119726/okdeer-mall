package com.okdeer.mall.activity.serviceGoodsRecommend.mapper;

import java.util.List;
import com.okdeer.mall.activity.serviceGoodsRecommend.entity.ActivityServiceGoodsRecommendArea;

/**
 * @pr yscm
 * @desc 服务商品推荐关联地区
 * @author zhangkn
 * @date 2016-11-08 下午3:12:43
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 */
public interface ActivityServiceGoodsRecommendAreaMapper {
	

	/**
	 * @Description: 批量插入ActivityServiceGoodsRecommendArea
	 * @param areaList
	 * @throws Exception
	 * @author YSCGD
	 * @date 2016年11月8日
	 */
	void addBatch(List<ActivityServiceGoodsRecommendArea> areaList) throws Exception;

	/**
	 * @Description: 根据活动id删除记录
	 * @param activityId
	 * @author YSCGD
	 * @date 2016年11月8日
	 */
	void deleteByActivityId(String activityId);
	
	/**
	 * @Description: 根据活动id获取列表
	 * @param activityId
	 * @return
	 * @author YSCGD
	 * @date 2016年11月8日
	 */
	public List<ActivityServiceGoodsRecommendArea> listByActivityId(String activityId);
	
}
