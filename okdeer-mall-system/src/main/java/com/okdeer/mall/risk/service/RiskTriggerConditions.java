/** 
 *@Project: okdeer-mall-system 
 *@Author: guocp
 *@Date: 2016年11月17日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.service;

import com.okdeer.mall.risk.entity.RiskOrderRecord;

/**
 * ClassName: RiskConditionsTrigger 
 * @Description: 
 * @author guocp
 * @date 2016年11月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface RiskTriggerConditions {

	/**
	 * 是否触发风控
	 * @param riskOrder
	 * @return 返回值为true:触发风控,false:未触发风控
	 * @author guocp
	 * @throws Exception 
	 * @date 2016年11月17日
	 */
	boolean isTrigger(RiskOrderRecord riskOrder) throws Exception;
}
