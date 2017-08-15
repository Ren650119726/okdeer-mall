package com.okdeer.mall.activity.coupons.service;

import java.util.List;

import com.okdeer.mall.activity.coupons.entity.ActivityCollectXffqRelation;

/**
 * ClassName: ActivityCollectXffqRelationService 
 * @Description: 
 * @author zhangkn
 * @date 2017年6月23日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.3			 2017年6月23日 			zhagnkn
 */
public interface ActivityCollectXffqRelationService {
	/**
	 * @Description: 通过活动id查询列表
	 * @param collectId
	 * @return
	 * @author zhangkn
	 * @date 2017年6月23日
	 */
	List<ActivityCollectXffqRelation> findByCollectId(String collectId);
}
