package com.okdeer.mall.activity.coupons.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.okdeer.mall.activity.coupons.entity.ActivityCollectArea;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCommunity;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsRecordVo;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsVo;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;

/**
 * @pr yscm
 * @desc 代金券活动service
 * @author zhangkn
 * @date 2016-1-26 下午3:12:43
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 */
public interface ActivityCollectCouponsService {
	
	void save(ActivityCollectCoupons activityCollectCoupons);
	
	void save(ActivityCollectCoupons activityCollectCoupons,List<String> couponsIds,String areaIds) throws Exception;

	void updateDynamic(ActivityCollectCoupons activityCollectCoupons);
	
	void update(ActivityCollectCoupons activityCollectCoupons,List<String> activityIds,String areaIds);

	ActivityCollectCoupons get(String id);
	
	/**
	 * 
	 * @Description: 查询代金券活动列表
	 * @param map 参数
	 * @param pageNumber 当前页码
	 * @param pageSize 每页条数
	 * @return PageUtils<ActivityCollectCoupons>  
	 * @throws ServiceException service异常
	 * @author YSCGD
	 * @date 2016年7月13日
	 */
	PageUtils<ActivityCollectCoupons> list(Map<String,Object> map,int pageNumber,int pageSize) throws ServiceException;
	
	/**
	 * @desc 审核活动
	 * @param obj
	 */
	String updateApproval(ActivityCollectCoupons obj) throws Exception;
	
	void updateBatchStatus(String id,int status,String updateUserId,Date updateTime,String belongType) throws Exception;
	
	List<ActivityCollectArea> getAreaList(String collectCouponsId);
	List<String> getAreaIds(String collectCouponsId);
	
	List<ActivityCollectCommunity> getCommunityList(String collectCouponsId);
	List<String> getCommunityIds(String collectCouponsId);
	
	/**
	 * @desc 用于判断某个时间段内活动是否冲突
	 * @param map
	 * @return
	 */
	int countTimeQuantum(Map<String,Object> map);
	
	/**
	 * 
	 * 根据店铺id、活动状态、客户端限制类型，查询该店铺下的满减满折活动
	 * 
	 * @author wusw
	 * @param params
	 * @return
	 * @throws ServiceException
	 */
	List<ActivityCollectCouponsVo> findByStoreAndLimitType(Map<String,Object> params) throws ServiceException;
	
	/**
	 * @desc 查询出需要跑job的活动
	 * @return
	 */
	public List<ActivityCollectCoupons> listByJob();
	
	List<ActivityCollectCouponsRecordVo> findByUnusedOrExpires(Map<String,Object> params); 
	
	/**
	 * 
	 * 根据店铺id、活动状态、客户端限制类型等，统计代金券领券活动及代金券数量
	 * 
	 * @author wusw
	 * @param params
	 * @return
	 * @throws ServiceException
	 */
	int selectCountByStoreAndLimitType(Map<String,Object> params) throws ServiceException;
}
