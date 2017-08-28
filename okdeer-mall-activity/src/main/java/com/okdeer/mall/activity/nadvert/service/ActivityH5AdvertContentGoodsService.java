package com.okdeer.mall.activity.nadvert.service;

import java.util.List;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertContentGoods;

public interface ActivityH5AdvertContentGoodsService {
	
	/**
	 * @Description: 批量保存h5活动内容类型>>广告图片
	 * @param entitys void
	 * @throws
	 * @author mengsj
	 * @date 2017年8月11日
	 */
	void batchSave(List<ActivityH5AdvertContentGoods> entitys) throws Exception;
	
	/**
	 * @Description:  通过活动id，活动内容id查询与活动内容关联的商品列表
	 * @param activityId
	 * @param contentId
	 * @return List<ActivityH5AdvertContentGoods>
	 * @throws
	 * @author mengsj
	 * @date 2017年8月11日
	 */
	List<ActivityH5AdvertContentGoods> findByActId(String activityId,String contentId);
	
	/**
	 * @Description: 删除与h5活动内容关联的商品对象
	 * @param activityId
	 * @param contentId void
	 * @throws
	 * @author mengsj
	 * @date 2017年8月12日
	 */
	void deleteByActId(String activityId,String contentId) throws Exception;
	
	/**
	 * @Description: 查找h5活动关联的便利店商品
	 * @param storeId 店铺id
	 * @param activityId h5活动id
	 * @param contentId 活动内容id
	 * @param pageNumber
	 * @param pageSize
	 * @return PageUtils<ActivityH5AdvertDto>
	 * @throws
	 * @author mengsj
	 * @date 2017年8月26日
	 */
	PageUtils<ActivityH5AdvertContentGoods> findBldGoodsByActivityId(String storeId,String activityId,String contentId ,Integer pageNumber,Integer pageSize);
}
