/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2016年12月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.prize.entity.ActivityPrizeRecord;

/**
 * ClassName: ActivityPrizeRecordApiImpl 
 * @Description: 中奖记录表
 * @author xuzq01
 * @date 2016年12月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2.3			2016年12月8日		xuzq01				中奖记录表Service
 */

public interface ActivityPrizeRecordService extends IBaseService {

	List<ActivityPrizeRecord> findByUserId(String userId);
	
	List<ActivityPrizeRecord> findPrizeRecord();
	
	int findCountByPrizeId(String prizeId);

}
