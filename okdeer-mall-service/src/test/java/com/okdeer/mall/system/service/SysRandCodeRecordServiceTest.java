/** 
 *@Project: okdeer-mall-service 
 *@Author: tangzj02
 *@Date: 2017年3月1日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.system.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.Application;

/**
 * ClassName: SysRandCodeRecordServiceTest 
 * @Description: 
 * @author tangzj02
 * @date 2017年3月1日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class SysRandCodeRecordServiceTest {

	/** 日志对象 */
	private static final Logger log = LoggerFactory.getLogger(SysRandCodeRecordServiceTest.class);

	@Autowired
	private SysRandCodeRecordService sysRandCodeRecordService;

	@Test
	public void testFindRecordByRandCode() {
		try {
			String recordCode = sysRandCodeRecordService.findRecordByRandCode();
			log.info("recordCode：{}", recordCode);
			assertNotNull("获取的随机码为空", recordCode);
		} catch (ServiceException e) {
			log.info("获取随机码异常：{}", e);
		}
	}

	@Test
	public void testDeleteRecordByRandCodeByCode() {
		try {
			sysRandCodeRecordService.deleteRecordByRandCodeByCode("q88c");
		} catch (ServiceException e) {
			log.info("删除随机码异常：{}", e);
		}
	}

	@Test
	public void testFindValidRandCodeList() {
		try {
			List<String> list = sysRandCodeRecordService.findValidRandCodeList();
			assertNotNull("获取的随机码列表为空", list);
			log.info("获取邀请码记录数：{}", list.size());
		} catch (ServiceException e) {
			log.info("获取的随机码列表异常：{}", e);
		}
	}
}
