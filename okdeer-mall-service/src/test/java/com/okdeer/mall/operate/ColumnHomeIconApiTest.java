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

import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.common.utils.BaseResult;
import com.okdeer.mall.Application;
import com.okdeer.mall.operate.dto.HomeIconDto;
import com.okdeer.mall.operate.dto.HomeIconParamDto;
import com.okdeer.mall.operate.dto.SelectAreaDto;
import com.okdeer.mall.operate.enums.HomeIconPlace;
import com.okdeer.mall.operate.enums.HomeIconTaskType;
import com.okdeer.mall.operate.enums.SelectAreaType;
import com.okdeer.mall.operate.service.ColumnHomeIconApi;

/**
 * ClassName: HomeIconApiTest 
 * @Description: 首页ICON服务测试
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
public class ColumnHomeIconApiTest {

	/** 日志对象 */
	private static final Logger log = LoggerFactory.getLogger(ColumnHomeIconApiTest.class);

	@Autowired
	private ColumnHomeIconApi homeIconService;

	@Test
	public void testSave() {
		try {
			log.info("测试API服务 - 保存首页ICON");
			HomeIconDto dto = new HomeIconDto();
			// dto.setId(UuidUtils.getUuid());
			dto.setBannerUrl("www.okdeer.com");

			List<String> goodsList = new ArrayList<>();
			for (int i = 10; i < 15; i++) {
				goodsList.add(i + "");
			}
			dto.setGoodsIds(goodsList);
			dto.setIconUrl("www.okdeer.com");
			dto.setName("单元测试数据");
			dto.setPlace(HomeIconPlace.five);
			dto.setTaskType(HomeIconTaskType.goods);
			dto.setTaskScope(SelectAreaType.city);

			List<SelectAreaDto> areaList = new ArrayList<>();
			SelectAreaDto areaDto1 = new SelectAreaDto();
			areaDto1.setAreaType(SelectAreaType.city);
			areaDto1.setCityId(DateUtils.getDateRandom());
			areaDto1.setProvinceId(DateUtils.getDateRandom());
			areaDto1.setProvinceName("测试省");
			areaList.add(areaDto1);
			SelectAreaDto areaDto2 = new SelectAreaDto();
			areaDto2.setAreaType(SelectAreaType.city);
			areaDto2.setCityId(DateUtils.getDateRandom());
			areaDto2.setProvinceId(DateUtils.getDateRandom());
			areaDto2.setProvinceName("测试城市");
			areaList.add(areaDto2);
			dto.setAreaList(areaList);
			BaseResult result = homeIconService.save(dto);
			Assert.assertNotNull("测试API服务 = 保存首页ICON失败", result);
			log.info("测试API服务 - 保存首页ICON ：{}", result);
		} catch (Exception e) {
			log.error("测试API服务 = 保存首页ICON异常:{}", e);
		}
	}

	@Test
	public void testFindById() {
		try {
			log.info("测试API服务 - 根据ID查询首页ICON");
			HomeIconDto result = homeIconService.findById("8a94e7ce5967277d015967277ff70002");
			Assert.assertNotNull("测试API服务 = 根据ID查询首页ICON失败", result);
			log.info("测试API服务 - 根据ID查询首页ICON ：{}", result);
		} catch (Exception e) {
			log.error("测试API服务 = 根据ID查询首页ICON异常:{}", e);
		}
	}

	@Test
	public void testFindList() {
		try {
			log.info("测试API服务 - 获取首页ICON列表");
			HomeIconParamDto paramDto = new HomeIconParamDto();
			paramDto.setAddStartTime(new Date());
			paramDto.setAddEndTime(new Date());
			paramDto.setUpdateStartTime(new Date());
			paramDto.setUpdateEndTime(new Date());
			List<HomeIconDto> list = homeIconService.findList(paramDto);
			Assert.assertNotNull("测试API服务 = 查询首页ICON列表失败", list);
		} catch (Exception e) {
			log.error("测试API服务 = 查询首页ICON列表异常:{}", e);
		}
	}

}
