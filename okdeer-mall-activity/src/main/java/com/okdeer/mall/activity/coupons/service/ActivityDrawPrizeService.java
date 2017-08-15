
package com.okdeer.mall.activity.coupons.service;

import com.okdeer.mall.activity.prize.entity.ActivityPrizeWeight;

/**
 * 
 * ClassName: ActivityDrawPrizeService 
 * @Description: 抽奖服务类
 * @author tuzhd
 * @date 2016年11月23日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.1.7			2016-11-23			tuzhd			抽奖服务类
 */
public interface ActivityDrawPrizeService {

	/**
	 * @Description: 根据抽奖活动id抽取奖品
	 * @param luckDrawId 抽奖活动id
	 * @return
	 * @author zengjizu
	 * @date 2017年8月7日
	 */
	ActivityPrizeWeight drawByWithckDrawId(String luckDrawId) throws Exception;
}
