/** 
 *@Project: okdeer-mall-service 
 *@Author: xuzq01
 *@Date: 2016年11月21日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.Application;
import com.okdeer.mall.risk.dto.RiskBlackDto;
import com.okdeer.mall.risk.entity.RiskBlack;
import com.okdeer.mall.risk.entity.RiskBlack;
import com.okdeer.mall.risk.service.RiskBlackService;

/**
 * ClassName: RiskBlackServiceTest 
 * @Description: 黑名单管理service测试类
 * BaseServiceTest 可以继承 不用再每个写注解
 * @author xuzq01
 * @date 2016年11月21日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2		 2016年11月21日		xuzq01				黑名单管理service测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class RiskBlackServiceTest {
	/**
	 * 黑名单管理service类
	 */
	@Resource
	private RiskBlackService riskBlackService;
	
	@Test
	public void findBlackListTest() {
		RiskBlackDto riskBlackDto = new RiskBlackDto();
		riskBlackDto.setAccountType(0);
		//riskBlackDto.setAccountType(1);
		//riskBlackDto.setAccountType(2);
		//riskBlackDto.setAccountType(3);
		riskBlackDto.setAccount("测试2");
		int pageNumber = 1;
		int pageSize=10;
		PageUtils<RiskBlack> list = riskBlackService.findBlackList(riskBlackDto, pageNumber, pageSize);
		assertTrue(list.getList().size()>0);
	}
	
	//@Test
	@Rollback
	public void addBatchTest() {
		Date date = new Date();
		//存在的黑名单
		RiskBlack r1 = new RiskBlack();
		r1.setAccount("测试1");
		r1.setAccountType(0);
		r1.setDisabled(0);
		r1.setCreateUserId("1");
		r1.setUpdateUserId("1");
		r1.setCreateTime(date);
		r1.setUpdateTime(date);
		//不存在的黑名单
		RiskBlack r2 = new RiskBlack();
		r2.setAccount("测试3");
		r2.setAccountType(4);
		r2.setDisabled(0);
		r2.setCreateUserId("1");
		r2.setUpdateUserId("1");
		r2.setCreateTime(date);
		r2.setUpdateTime(date);
		List<RiskBlack> riskBlackList = new ArrayList<RiskBlack>();
		riskBlackList.add(r1);
		riskBlackList.add(r2);
		riskBlackService.addBatch(riskBlackList);
	}
	
	//@Test
	public void findBlackListByParamsTest() {
		Map<String,Object> map = new HashMap<String,Object>();
		riskBlackService.findBlackListByParams(map);
	}
	//@Test
	@Rollback
	public void deleteBatchByIdsTest() {
		List<String> ids = new ArrayList<String>();
		ids.add("8a94e405587178d001587179e1630001");
		ids.add("sdfs");
		String updateUserId ="1213";
		Date updateTime = new Date();
		riskBlackService.deleteBatchByIds(ids, updateUserId, updateTime);
	}
	@Test
	public void findAllBlackMobileTest(){
		Set<RiskBlack> riskBlackSet = riskBlackService.findAllBlackMobile();
		assertTrue(riskBlackSet.size()>0);
	}
	
	@Test
	public void findAllBlackDeviceTest(){
		Set<RiskBlack> riskBlackSet = riskBlackService.findAllBlackDevice();
		assertTrue(riskBlackSet.size()>0);
	}
	
	@Test
	public void findAllBlackPayAccountTest(){
		Set<RiskBlack> riskBlackSet = riskBlackService.findAllBlackPayAccount();
		assertTrue(riskBlackSet.size()>0);
	}
	
	@Test
	public void findAllBlackLoginAccountTest(){
		Set<RiskBlack> riskBlackSet = riskBlackService.findAllBlackLoginAccount();
		assertTrue(riskBlackSet.size()>0);
	}
}
