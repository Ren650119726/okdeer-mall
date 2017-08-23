package com.okdeer.mall.activity.nadvert.service;

import java.util.List;

import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertContentAdv;

public interface ActivityH5AdvertContentAdvService {
	
	/**
	 * @Description: 批量保存h5活动内容类型>>广告图片
	 * @param entitys void
	 * @throws
	 * @author mengsj
	 * @date 2017年8月11日
	 */
	void batchSave(List<ActivityH5AdvertContentAdv> entitys) throws Exception;
	
	/**
	 * @Description: 通过活动id，活动内容id查询与活动内容关联的广告图片
	 * @param activityId
	 * @return List<ActivityH5AdvertContentAdv>
	 * @throws
	 * @author mengsj
	 * @date 2017年8月11日
	 */
	List<ActivityH5AdvertContentAdv> findByActId(String activityId,String contentId);
	
	/**
	 * @Description: 删除与h5活动内容关联的广告图片对象
	 * @param activityId
	 * @param contentId void
	 * @throws
	 * @author mengsj
	 * @date 2017年8月12日
	 */
	void deleteByActId(String activityId,String contentId) throws Exception;
}
