/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2016年12月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.prize.entity.ActivityPrizeWeight;

/**
 * ClassName: ActivityPrizeWeightApiImpl 
 * @Description: 活动奖品权重表
 * @author xuzq01
 * @date 2016年12月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2.3			2016年12月8日		xuzq01				活动奖品权重表Service
 */

public interface ActivityPrizeWeightService extends IBaseService {

	List<ActivityPrizeWeight> findAll();
}
