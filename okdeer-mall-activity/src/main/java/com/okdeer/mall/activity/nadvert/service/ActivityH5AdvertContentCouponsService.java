package com.okdeer.mall.activity.nadvert.service;

import java.util.List;

import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertContentCoupons;

public interface ActivityH5AdvertContentCouponsService {
	
	/**
	 * @Description: 批量保存h5活动内容>>代金券活动
	 * @param entitys void
	 * @throws
	 * @author mengsj
	 * @date 2017年8月11日
	 */
	void batchSave(List<ActivityH5AdvertContentCoupons> entitys) throws Exception;
	
	/**
	 * @Description: 通过活动id，活动内容id查询与活动内容关联的代金券活动
	 * @param activityId
	 * @param contentId
	 * @return List<ActivityH5AdvertContentCoupons>
	 * @throws
	 * @author mengsj
	 * @date 2017年8月11日
	 */
	List<ActivityH5AdvertContentCoupons> findByActId(String activityId,String contentId);
	
	/**
	 * @Description: 删除与h5活动内容关联的代金券活动
	 * @param activityId
	 * @param contentId void
	 * @throws
	 * @author mengsj
	 * @date 2017年8月12日
	 */
	void deleteByActId(String activityId,String contentId) throws Exception;
}
