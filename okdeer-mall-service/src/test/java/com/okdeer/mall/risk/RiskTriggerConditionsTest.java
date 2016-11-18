/** 
 *@Project: okdeer-mall-service 
 *@Author: guocp
 *@Date: 2016年11月17日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.risk.entity.RiskOrderRecord;
import com.okdeer.mall.risk.service.RiskTriggerConditions;


/**
 * ClassName: RiskTriggerConditionsTest 
 * @Description: TODO
 * @author guocp
 * @date 2016年11月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public class RiskTriggerConditionsTest extends BaseServiceTest {

	@Autowired
	private RiskTriggerConditions riskTriggerConditions;
	
	@Test//(timeout=1000)
	public void testIsTrigger() throws Exception{
		RiskOrderRecord riskOrder = new RiskOrderRecord();
		riskOrder.setId(UuidUtils.getUuid());
		riskOrder.setLoginName("186");
		riskOrder.setDeviceId("123");
		riskOrder.setPayAccount("123");
		riskOrder.setFacePrice(new BigDecimal("60"));
		riskOrder.setTel("18682310941");
		riskOrder.setCreateTime(new Date());
		boolean istrigger = riskTriggerConditions.isTrigger(riskOrder);
		assertEquals(false, istrigger);
	}
	
	
}
