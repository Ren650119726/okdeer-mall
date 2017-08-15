
package com.okdeer.mall.activity.coupons.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.mall.activity.coupons.entity.ActivityCollectXffqRelation;
import com.okdeer.mall.activity.coupons.mapper.ActivityCollectXffqRelationMapper;
import com.okdeer.mall.activity.coupons.service.ActivityCollectXffqRelationService;

/**
 * ClassName: ActivityCollectXffqRelationServiceImpl 
 * @Description: 
 * @author zhangkn
 * @date 2017年6月23日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.3			 2017年6月23日 			zhagnkn
 */
@Service
public class ActivityCollectXffqRelationServiceImpl implements ActivityCollectXffqRelationService {

	/**
	 * 注入mapper
	 */
	@Autowired
	private ActivityCollectXffqRelationMapper ActivityCollectXffqRelationMapper;

	@Override
	public List<ActivityCollectXffqRelation> findByCollectId(String collectId) {
		return ActivityCollectXffqRelationMapper.findByCollectId(collectId);
	}

	
}
