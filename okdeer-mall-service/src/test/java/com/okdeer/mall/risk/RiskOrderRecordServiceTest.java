/** 
 *@Project: okdeer-mall-service 
 *@Author: xuzq01
 *@Date: 2016年11月25日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.okdeer.mall.Application;
import com.okdeer.mall.risk.service.RiskOrderRecordService;

/**
 * ClassName: RiskOrderRecordServiceTest 
 * @Description: TODO
 * @author xuzq01
 * @date 2016年11月25日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class RiskOrderRecordServiceTest {
	/**
	 * 风控人员管理service类
	 */
	@Resource
	private RiskOrderRecordService riskOrderRecordService;
	
	@Test
	public void deleteByTimeTest() throws Exception{
		Date createTime = new Date();
		riskOrderRecordService.deleteByTime(createTime);
		
	}
}
