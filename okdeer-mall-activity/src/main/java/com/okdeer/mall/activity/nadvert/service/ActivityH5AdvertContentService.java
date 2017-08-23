package com.okdeer.mall.activity.nadvert.service;

import java.util.List;

import com.okdeer.mall.activity.nadvert.bo.ActivityH5AdvertContentBo;

public interface ActivityH5AdvertContentService {
	
	/**
	 * @Description: 批量保存h5活动内容
	 * @param entitys void
	 * @throws
	 * @author mengsj
	 * @date 2017年8月11日
	 */
	void batchSave(List<ActivityH5AdvertContentBo> bos) throws Exception;
	
	/**
	 * @Description: 通过h5活动id查询h5活动内容
	 * @param id
	 * @return ActivityH5Advert
	 * @throws
	 * @author mengsj
	 * @date 2017年8月11日
	 */
	List<ActivityH5AdvertContentBo> findByActId(String activityId);
	
	/**
	 * @Description: 删除h5活动关联的h5活动内容
	 * @param activityId
	 * @param contentId void
	 * @throws
	 * @author mengsj
	 * @date 2017年8月12日
	 */
	void deleteByActId(String activityId) throws Exception;
}
