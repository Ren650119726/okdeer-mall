/** 
 *@Project: okdeer-mall-service 
 *@Author: tangzj02
 *@Date: 2016年12月30日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate;

import java.util.ArrayList;
import java.util.Date;
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
import com.okdeer.mall.operate.dto.AppRecommendDto;
import com.okdeer.mall.operate.dto.AppRecommendGoodsDto;
import com.okdeer.mall.operate.dto.AppRecommendParamDto;
import com.okdeer.mall.operate.enums.AppRecommendPlace;
import com.okdeer.mall.operate.service.ColumnAppRecommendApi;

/**
 * ClassName: ColumnAppRecommendApiTest 
 * @Description: APP端服务商品推荐 API服务测试
 * @author tangzj02
 * @date 2017年01月04日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.0       2017-01-04         tangzj02                        添加
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ColumnAppRecommendApiTest {

	/** 日志对象 */
	private static final Logger log = LoggerFactory.getLogger(ColumnAppRecommendApiTest.class);

	@Autowired
	private ColumnAppRecommendApi columnAppRecommendApi;

	@Test
	public void testFindById() {
		try {
			log.info("测试API服务 - 根据ID查询APP端服务商品推荐");
			AppRecommendDto result = columnAppRecommendApi.findById("8a94e7ce5968c3f4015968c471d50005");
			Assert.assertNotNull("测试API服务 = 根据ID查询APP端服务商品推荐失败", result);
			log.info("测试API服务 - 根据ID查询APP端服务商品推荐 ：{}", result);
		} catch (Exception e) {
			log.error("测试API服务 = 根据ID查询APP端服务商品推荐异常:{}", e);
		}
	}

	@Test
	public void testFindListByCity() {
		try {
			log.info("测试API服务 - 根据城市ID和位置查询APP端服务商品推荐列表");
			List<AppRecommendDto> result = columnAppRecommendApi.findListByCity("1", "1", AppRecommendPlace.find);
			Assert.assertNotNull("测试API服务 = 根据城市ID和位置查询APP端服务商品推荐列表失败", result);
			log.info("测试API服务 - 根据城市ID和位置查询APP端服务商品推荐列表 ：{}", result);
		} catch (Exception e) {
			log.error("测试API服务 = 根据城市ID和位置查询APP端服务商品推荐列表异常:{}", e);
		}
	}

	@Test
	public void testFindList() {
		try {
			log.info("测试API服务 - 获取APP端服务商品推荐列表");
			AppRecommendParamDto paramDto = new AppRecommendParamDto();
			paramDto.setAddStartTime(new Date());
			paramDto.setAddEndTime(new Date());
			paramDto.setUpdateStartTime(new Date());
			paramDto.setUpdateEndTime(new Date());
			List<AppRecommendDto> list = columnAppRecommendApi.findList(paramDto);
			Assert.assertNotNull("测试API服务 = 查询APP端服务商品推荐失败", list);
		} catch (Exception e) {
			log.error("测试API服务 = 查询APP端服务商品推荐异常:{}", e);
		}
	}

	@Test
	public void testFindAppRecommendGoodsDtoListByRecommendId() {
		try {
			log.info("测试API服务 -  查询APP端服务商品推荐与商品关联列表");
			List<AppRecommendGoodsDto> list = columnAppRecommendApi
					.findAppRecommendGoodsDtoListByRecommendId("8a94e7ce5968c3f4015968c471d50005");
			Assert.assertNotNull("测试API服务 =  查询APP端服务商品推荐与商品关联列表失败", list);
		} catch (Exception e) {
			log.error("测试API服务 =  查询APP端服务商品推荐与商品关联列表异常:{}", e);
		}
	}

	@Test
	public void testDeleteByIds() {
		try {
			log.info("测试API服务 -  根据ID集合 批量删除  APP端服务商品推荐信息");
			List<String> ids = new ArrayList<>();
			for (int i = 1; i < 5; i++) {
				ids.add(i + "");
			}
			Integer result = columnAppRecommendApi.deleteByIds(ids);
			Assert.assertNotNull("测试API服务 =  根据ID集合 批量删除  APP端服务商品推荐信息失败", result);
		} catch (Exception e) {
			log.error("测试API服务 =  根据ID集合 批量删除  APP端服务商品推荐信息异常:{}", e);
		}
	}
}
