/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @项目名称: yschome-mall 
 * @文件名称: TradeOrderActivityService.java 
 * @Date: 2016年3月31日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
package com.okdeer.mall.order.service;

import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.enums.ActivityBelongType;

/**
 * 订单关联活动service
 * @pr yschome-mall
 * @author guocp
 * @date 2016年3月31日 下午2:03:17
 */
public interface TradeOrderActivityService {

	/**
	 * 查询活动创建者userId
	 *
	 * @param order 订单
	 */
	String findActivityUserId(TradeOrder order) throws Exception;

	/**
	 * 返回活动归属
	 * @return 
	 */
	ActivityBelongType findActivityType(TradeOrder order) throws Exception;

}
