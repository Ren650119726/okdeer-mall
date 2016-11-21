/** 
 *@Project: okdeer-mall-service 
 *@Author: xuzq01
 *@Date: 2016年11月18日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.Application;
import com.okdeer.mall.risk.dto.RiskWhiteDto;
import com.okdeer.mall.risk.entity.RiskWhite;
import com.okdeer.mall.risk.service.RiskWhiteService;

/**
 * ClassName: RiskWhiteServiceTest 
 * @Description: 白名单管理service测试类
 * @author xuzq01
 * @date 2016年11月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2			2016年11月18日		xuzq01				白名单管理service测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class RiskWhiteServiceTest {
	/**
	 * 黑名单管理service类
	 */
	@Resource
	private RiskWhiteService riskWhiteService;
	
	@Test
	public void findWhiteListTest() {
		RiskWhiteDto riskWhiteDto = new RiskWhiteDto();
		riskWhiteDto.setAccountType(0);
		riskWhiteDto.setTelephoneAccount("fdg");
		int pageNumber = 1;
		int pageSize=10;
		PageUtils<RiskWhite> list = riskWhiteService.findWhiteList(riskWhiteDto, pageNumber, pageSize);
		assertTrue(list.getList().size()>0);
	}
	
	@Test
	@Rollback
	public void addBatchTest() {
		Date date = new Date();
		//存在的黑名单
		RiskWhite r1 = new RiskWhite();
		r1.setTelephoneAccount("fdg");
		r1.setAccountType(0);
		r1.setDisabled(0);
		r1.setCreateUserId("1");
		r1.setUpdateUserId("1");
		r1.setCreateTime(date);
		r1.setUpdateTime(date);
		//不存在的黑名单
		RiskWhite r2 = new RiskWhite();
		r2.setTelephoneAccount("测试3");
		r2.setAccountType(4);
		r2.setDisabled(0);
		r2.setCreateUserId("1");
		r2.setUpdateUserId("1");
		r2.setCreateTime(date);
		r2.setUpdateTime(date);
		List<RiskWhite> riskWhiteList = new ArrayList<RiskWhite>();
		riskWhiteList.add(r1);
		riskWhiteList.add(r2);
		riskWhiteService.addBatch(riskWhiteList);
	}
	
	@Test
	@Rollback
	public void deleteBatchByIdsTest() {
		List<String> ids = new ArrayList<String>();
		ids.add("8a94e4055876c6e0015876cb18a80001");
		ids.add("sdfsadfadf");
		String updateUserId ="1213";
		Date updateTime = new Date();
		riskWhiteService.deleteBatchByIds(ids, updateUserId, updateTime);
	}
}
