package com.okdeer.mall.activity.nadvert.service;

import java.util.List;

import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertRole;

/**
 * ClassName: ActivityH5AdvertRoleService 
 * @Description: h5活动规则服务接口
 * @author mengsj
 * @date 2017年8月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
 
public interface ActivityH5AdvertRoleService{
	/**
	 * @Description: 批量保存ActivityH5AdvertRole对象数据
	 * @param entity void
	 * @throws
	 * @author mengsj
	 * @date 2017年8月10日
	 */
	void saveBatch(List<ActivityH5AdvertRole> entitys) throws Exception;
	
	/**
	 * @Description: 通过h5活动id查询活动规则内容对象
	 * @param id
	 * @return ActivityH5Advert
	 * @throws
	 * @author mengsj
	 * @date 2017年8月11日
	 */
	List<ActivityH5AdvertRole> findByActId(String activityId);
	
	/**
	 * @Description: 删除h5活动规则
	 * @param activityId
	 * @param contentId void
	 * @throws
	 * @author mengsj
	 * @date 2017年8月12日
	 */
	void deleteByActId(String activityId) throws Exception;
}
