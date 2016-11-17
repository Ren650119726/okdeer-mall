/** 
 *@Project: okdeer-mall-system 
 *@Author: guocp
 *@Date: 2016年11月17日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.mall.risk.entity.RiskOrderRecord;
import com.okdeer.mall.risk.service.RiskConditionsTrigger;
import com.okdeer.mall.risk.service.RiskOrderRecordService;


/**
 * ClassName: RiskConditionsTriggerImpl 
 * @Description: 风控条件触发过滤
 * @author guocp
 * @date 2016年11月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class RiskConditionsTriggerImpl implements RiskConditionsTrigger {

	@Autowired
	private RiskOrderRecordService riskOrderRecordService;
	
	@Override
	public boolean isTrigger(RiskOrderRecord riskOrder) throws Exception {
		
		//新增订单记录
		riskOrderRecordService.add(riskOrder);
		
		
		return false;
	}

}
