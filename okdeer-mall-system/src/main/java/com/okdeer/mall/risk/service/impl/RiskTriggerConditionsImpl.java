/** 
 *@Project: okdeer-mall-system 
 *@Author: guocp
 *@Date: 2016年11月17日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.mall.risk.entity.RiskOrderRecord;
import com.okdeer.mall.risk.po.RiskOrderRecordPo;
import com.okdeer.mall.risk.service.RiskOrderRecordService;
import com.okdeer.mall.risk.service.RiskTriggerConditions;


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
@Service
public class RiskTriggerConditionsImpl implements RiskTriggerConditions {

	@Autowired
	private RiskOrderRecordService riskOrderRecordService;
	
	@Override
	public boolean isTrigger(RiskOrderRecord riskOrder) throws Exception {
		
		//新增订单记录
		riskOrderRecordService.add(riskOrder);
		
		//判断是否触发次数限制
		RiskOrderRecordPo list = riskOrderRecordService.findByLoginName(riskOrder.getLoginName());
		
		List<RiskOrderRecordPo> list1 = riskOrderRecordService.findByDeviceId(riskOrder.getDeviceId());
		
		List<RiskOrderRecordPo> list2 = riskOrderRecordService.findByPayAccount(riskOrder.getPayAccount());
		return false;
	}

}
