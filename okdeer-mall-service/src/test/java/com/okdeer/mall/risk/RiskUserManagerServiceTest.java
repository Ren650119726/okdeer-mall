/** 
 *@Project: okdeer-mall-service 
 *@Author: xuzq01
 *@Date: 2016年11月21日 
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
import com.okdeer.mall.risk.dto.RiskUserManagerDto;
import com.okdeer.mall.risk.entity.RiskBlack;
import com.okdeer.mall.risk.entity.RiskUserManager;
import com.okdeer.mall.risk.service.RiskUserManagerService;

/**
 * ClassName: RiskUserManagerServiceTest 
 * @Description: 风控人员管理service测试类
 * @author xuzq01
 * @date 2016年11月21日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2		 2016年11月21日		xuzq01				风控人员管理service测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class RiskUserManagerServiceTest {
	
	/**
	 * 风控人员管理service类
	 */
	@Resource
	private RiskUserManagerService riskUserManagerService;
	
	//@Test
	public void findUserListTest() {
		RiskUserManagerDto userManagerDto = new RiskUserManagerDto();
		//userManagerDto.setEmail("34534654");
		//userManagerDto.setIsAcceptMail(0);
		//userManagerDto.setIsAcceptMessage(0);
		///userManagerDto.setTelephone("123424435");
		userManagerDto.setUserName("他回过头");
		int pageNumber = 1;
		int pageSize=10;
		PageUtils<RiskUserManager> list = riskUserManagerService.findUserList(userManagerDto, pageNumber, pageSize);
		assertTrue(list.getList().size()>0);
	}
	
	//@Test
	@Rollback
	public void deleteBatchByIdsTest() {
		List<String> ids = new ArrayList<String>();
		ids.add("8a94e40558679888015867c870e60001");
		ids.add("dsf34546");
		String updateUserId ="1213";
		Date updateTime = new Date();
		riskUserManagerService.deleteBatchByIds(ids, updateUserId, updateTime);
	}
	@Test
	@Rollback
	public void findCountByTelephoneOrEmailTest() {
		RiskUserManager riskUserManager= new RiskUserManager();
		riskUserManager.setTelephone("13566666664");
		riskUserManager.setEmail("123@qq.com");
		
		int count = riskUserManagerService.findCountByTelephoneOrEmail(riskUserManager);
		assertTrue(count>0);
	}
	
}
