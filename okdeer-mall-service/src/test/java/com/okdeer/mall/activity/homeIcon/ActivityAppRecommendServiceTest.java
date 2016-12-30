/** 
 *@Project: okdeer-mall-service
 *@Author: tangzj02
 *@Date: 2016年12月30日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.activity.homeIcon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.Application;
import com.okdeer.mall.activity.dto.AppRecommendParamDto;
import com.okdeer.mall.activity.entity.ActivityAppRecommend;
import com.okdeer.mall.activity.service.ActivityAppRecommendService;

/**
 * ClassName: ActivityAppRecommendServiceTest 
 * @Description: APP端服务商品推荐服务测试
 * @author tangzj02
 * @date 2016年12月30日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.0       2016-12-30         tangzj02                        添加
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ActivityAppRecommendServiceTest {

	/** 日志对象 */
	private static final Logger log = LoggerFactory.getLogger(ActivityAppRecommendServiceTest.class);

	@Autowired
	private ActivityAppRecommendService recommendService;

	@Test
	public void testFindList() {
		try {
			log.info("测试获取APP端服务商品推荐与商品关联列表");
			AppRecommendParamDto paramDto = new AppRecommendParamDto();
			paramDto.setAddStartTime(new Date());
			paramDto.setAddEndTime(new Date());
			paramDto.setUpdateStartTime(new Date());
			paramDto.setUpdateEndTime(new Date());
			List<ActivityAppRecommend> list = recommendService.findList(paramDto);
		} catch (Exception e) {
			log.error("查询获取APP端服务商品推荐与商品关联列表异常:{}", e);
		}
	}

	@Test
	public void testDeleteByIds() {
		try {
			List<String> ids = new ArrayList<>();
			for (int i = 1; i < 10; i++) {
				ids.add(UuidUtils.getUuid());
			}
			log.info("测试根据APP端服务商品推荐ID删除数据");
			int result = recommendService.deleteByIds(ids);
			//Assert.assertTrue("根据APP端服务商品推荐ID删除数据失败", result > 0);
		} catch (Exception e) {
			log.error("根据APP端服务商品推荐ID删除数据异常:{}", e);
		}
	}
}
