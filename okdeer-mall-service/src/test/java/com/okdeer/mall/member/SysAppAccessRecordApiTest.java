/** 
 *@Project: okdeer-mall-service 
 *@Author: tangzj02
 *@Date: 2017年1月10日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.member;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.member.member.entity.SysAppAccessRecordDto;
import com.okdeer.mall.member.member.service.SysAppAccessRecordApi;

/**
 * ClassName: SysAppAccessRecordApiTest 
 * @Description: APP设备访问记录接口测试
 * @author tangzj02
 * @date 2017年1月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *       V2.0		  2017年1月10日                   tangzj02                     添加
 */

public class SysAppAccessRecordApiTest extends BaseServiceTest {

	/** 日志对象 */
	private static final Logger log = LoggerFactory.getLogger(SysAppAccessRecordApiTest.class);

	@Autowired
	private SysAppAccessRecordApi sysAppAccessRecordApi;

	@Test
	public void testSave() {
		// 初始化APP设备访问记录信息
		SysAppAccessRecordDto dto = new SysAppAccessRecordDto();
		dto.setAppVersion("1.0");
		dto.setAppType(0);
		dto.setCityId("1");
		dto.setCityName("单元测试城市");
		dto.setMachineCode("0123456789");
		dto.setNetwork("WIFI");
		dto.setBrand("测试品牌");
		dto.setScreen("1024*768");
		try {
			int result = sysAppAccessRecordApi.save(dto);
			log.info("保存APP设备访问记录 ：{}", result);
			Assert.assertTrue("测试保存APP设备访问记录失败", result > 0);
		} catch (Exception e) {
			log.error("测试APP设备访问记录异常:{}", e);
		}
	}
}
