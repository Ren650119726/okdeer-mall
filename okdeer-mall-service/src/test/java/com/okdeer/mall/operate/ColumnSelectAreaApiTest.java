/** 
 *@Project: okdeer-mall-service
 *@Author: tangzj02
 *@Date: 2016年12月30日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.okdeer.mall.Application;
import com.okdeer.mall.operate.dto.SelectAreaDto;
import com.okdeer.mall.operate.service.ColumnSelectAreaApi;

/**
 * ClassName: ColumnSelectAreaApiTest 
 * @Description: 栏目与地区关联API服务测试
 * @author tangzj02
 * @date 2017年01月03日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.0       2017-01-03         tangzj02                        添加
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ColumnSelectAreaApiTest {

	/** 日志对象 */
	private static final Logger log = LoggerFactory.getLogger(ColumnSelectAreaApiTest.class);

	@Autowired
	private ColumnSelectAreaApi selectAreaService;

	@Test
	public void testFindListByColumnId() {
		try {
			log.info("测试API服务 - 栏目与地区关联列表");
			List<SelectAreaDto> list = selectAreaService.findSelectAreaDtoListByColumnId("1");
			Assert.assertNotNull("API服务 - 栏目与地区关联列表失败", list);
		} catch (Exception e) {
			log.error("API服务 - 栏目与地区关联列表异常:{}", e);
		}
	}

}
