
package com.okdeer.mall.activity.coupons.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectXffqRelation;
import com.okdeer.mall.activity.coupons.service.ActivityCollectXffqRelationApi;
import com.okdeer.mall.activity.coupons.service.ActivityCollectXffqRelationService;

/**
 * 
 * ClassName: ActivityCollectXffqRelationApiImpl 
 * @author tangy
 * @date 2017年2月21日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     2.0.0          2017年2月21日                               tangy             新增
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivityCollectXffqRelationApi")
public class ActivityCollectXffqRelationApiImpl implements ActivityCollectXffqRelationApi {

	/**
	 * 安全库存联系关联人
	 */
	@Autowired
	private ActivityCollectXffqRelationService activityCollectXffqRelationService;

	@Override
	public List<ActivityCollectXffqRelation> findByCollectId(String collectId) {
		return activityCollectXffqRelationService.findByCollectId(collectId);
	}


}
