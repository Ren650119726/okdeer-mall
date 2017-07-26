/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityAdvertStoreMapper.java
 * @Date 2017-04-12 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.advert.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.advert.bo.ActivityAdvertStoreBo;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertStore;
/**
 * ClassName: ActivityAdvertStoreMapper 
 * @Description: 关联店铺信息持久化类
 * @author tuzhd
 * @date 2017年4月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V2.2.2			2017-4-18			tuzhd				关联店铺信息持久化类
 */
public interface ActivityAdvertStoreMapper extends IBaseMapper {
	/**
	 * @Description: 批量添加关联店铺信息
	 * @param list   要插入的关联店铺集合
	 * @throws
	 * @author tuzhd
	 * @date 2017年4月17日
	 */
	void saveBatch(List<ActivityAdvertStore> list);
	
	/**
	 * @Description:查询店铺信息根据活动id
	 * @param activityAdverId
	 * @return List<ActivityAdvertStoreDto>  
	 * @author tuzhd
	 * @date 2017年4月19日
	 */
	List<ActivityAdvertStoreBo> findShopByAdvertId(String activityAdverId);
	
	/**
	 * @Description: 删除关联店铺信息by活动id
	 * @param activityAdvertId 活动id
	 * @return int  
	 * @throws
	 * @author tuzhd
	 * @date 2017年4月19日
	 */
	int deleteByActivityAdvertId(String activityAdvertId);
}