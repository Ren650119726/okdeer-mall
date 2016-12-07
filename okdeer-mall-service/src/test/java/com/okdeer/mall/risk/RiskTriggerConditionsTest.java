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

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.risk.entity.RiskOrderRecord;
import com.okdeer.mall.risk.enums.IsPreferential;
import com.okdeer.mall.risk.enums.PayAccountType;
import com.okdeer.mall.risk.service.RiskBlackService;
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
	
	/**
	 * 黑名单管理service类
	 */
	@Autowired
	private RiskBlackService riskBlackService;
	
	//@Test//(timeout=1000)
	public void testIsTrigger() throws Exception{
		RiskOrderRecord riskOrder = new RiskOrderRecord();
		riskOrder.setId(UuidUtils.getUuid());
		riskOrder.setLoginName("13510797332");
		riskOrder.setDeviceId("742344a14405");
		riskOrder.setPayAccount("2088412478571351");
		riskOrder.setPayAccountType(PayAccountType.ALIPAY);
		riskOrder.setFacePrice(new BigDecimal("1.50"));
		riskOrder.setTel("13510797332");
		riskOrder.setCreateTime(new Date());
		riskOrder.setIsPreferential(IsPreferential.NO);
		boolean istrigger = riskTriggerConditions.isTrigger(riskOrder);
		assertEquals(false, istrigger);
	}
	
	//测试问题 黑名单删除但是依然被限制问题
	@Test//(timeout=1000)
	public void testIsTrigger2() throws Exception{
		String id="8a94e40558a8a6770158a8e3a92f0003";
		riskBlackService.delete(id);
		RiskOrderRecord riskOrder = new RiskOrderRecord();
		riskOrder.setId(UuidUtils.getUuid());
		riskOrder.setLoginName("18682441937");
		riskOrder.setDeviceId("e0191d965725");
		riskOrder.setPayAccount("fdgh45_t67");
		riskOrder.setPayAccountType(PayAccountType.ALIPAY);
		riskOrder.setFacePrice(new BigDecimal("1.50"));
		riskOrder.setTel("18682441937");
		riskOrder.setCreateTime(new Date());
		riskOrder.setIsPreferential(IsPreferential.NO);
		boolean istrigger = riskTriggerConditions.isTrigger(riskOrder);
		assertEquals(false, istrigger);
	}
}
