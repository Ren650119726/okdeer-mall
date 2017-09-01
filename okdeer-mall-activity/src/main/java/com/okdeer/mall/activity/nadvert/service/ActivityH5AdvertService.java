package com.okdeer.mall.activity.nadvert.service;

import java.util.Date;
import java.util.List;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.activity.nadvert.bo.ActivityH5AdvertBo;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5Advert;
import com.okdeer.mall.activity.nadvert.param.ActivityH5AdvertQParam;

/**
 * ClassName: ActivityH5AdvertService 
 * @Description: TODO
 * @author mengsj
 * @date 2017年8月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
 
public interface ActivityH5AdvertService{
	
	/**
	 * @Description: 保存ActivityH5Advert对象数据
	 * @param entity void
	 * @throws
	 * @author mengsj
	 * @date 2017年8月10日
	 */
	void save(ActivityH5AdvertBo bo) throws Exception;
	
	/**
	 * 
	 * @Description: 更新ActivityH5Advert
	 * @param entity
	 * @throws Exception void
	 * @throws
	 * @author mengsj
	 * @date 2017年8月11日
	 */
	void update(ActivityH5AdvertBo bo) throws Exception;
	
	/**
	 * 
	 * @Description: 更新ActivityH5Advert
	 * @param entity
	 * @throws Exception void
	 * @throws
	 * @author mengsj
	 * @date 2017年8月11日
	 */
	void updateNoContent(ActivityH5Advert bo) throws Exception;
	
	/**
	 * @Description: 通过id查询ActivityH5Advert对象
	 * @param id
	 * @return ActivityH5Advert
	 * @throws
	 * @author mengsj
	 * @date 2017年8月11日
	 */
	ActivityH5AdvertBo findById(String id);
	
	/**
	 * @Description: 删除h5活动
	 * @param id
	 * @throws Exception void
	 * @throws
	 * @author mengsj
	 * @date 2017年8月12日
	 */
	void deleteById(String id) throws Exception;
	
	/**
	 * @Description: 分页查找h5活动
	 * @param param
	 * @param pageNumber
	 * @param pageSize
	 * @return List<ActivityH5AdvertBo>
	 * @throws
	 * @author mengsj
	 * @date 2017年8月14日
	 */
	PageUtils<ActivityH5Advert> findByParam(ActivityH5AdvertQParam param,Integer pageNumber, Integer pageSize);
	
	/**
	 * @Description: 定时器查询未开始和进行中状态的广告活动列表
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月20日
	 */
	List<ActivityH5Advert> listByJob(Date currentTime);
	
	/**
	 * @Description: 修改h5活动状态
	 * @param entity
	 * @throws Exception void
	 * @throws
	 * @author mengsj
	 * @date 2017年8月15日
	 */
	void updateBatchStatus(ActivityH5Advert entity) throws Exception;

}
