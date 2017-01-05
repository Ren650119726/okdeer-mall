/** 
 *@Project: okdeer-mall-service 
 *@Author: tangzj02
 *@Date: 2016年12月30日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate;

import java.util.ArrayList;
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
import com.okdeer.common.utils.BaseResult;
import com.okdeer.mall.Application;
import com.okdeer.mall.operate.dto.HomeIconParamDto;
import com.okdeer.mall.operate.entity.ColumnHomeIcon;
import com.okdeer.mall.operate.entity.ColumnSelectArea;
import com.okdeer.mall.operate.enums.HomeIconPlace;
import com.okdeer.mall.operate.enums.HomeIconTaskType;
import com.okdeer.mall.operate.enums.SelectAreaType;
import com.okdeer.mall.operate.service.ColumnHomeIconService;

/**
 * ClassName: ColumnHomeIconServiceTest 
 * @Description: 首页ICON服务API测试
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
public class ColumnHomeIconServiceTest {

	/** 日志对象 */
	private static final Logger log = LoggerFactory.getLogger(ColumnHomeIconServiceTest.class);

	@Autowired
	private ColumnHomeIconService homeIconService;

	@Test
	public void test1Save() {
		try {
			log.info("测试保存首页ICON");
			ColumnHomeIcon entity = new ColumnHomeIcon();
			entity.setBannerUrl("www.okdeer.com");
			entity.setIconUrl("www.okdeer.com");
			entity.setName("单元测试数据");
			entity.setPlace(HomeIconPlace.four);
			entity.setTaskType(HomeIconTaskType.goods);
			entity.setTaskScope(SelectAreaType.city);

			List<ColumnSelectArea> areaList = new ArrayList<>();
			ColumnSelectArea area1 = new ColumnSelectArea();
			area1.setAreaType(SelectAreaType.city);
			area1.setCityId(UuidUtils.getUuid());
			area1.setProvinceId(UuidUtils.getUuid());
			area1.setProvinceName("测试省1");
			area1.setCityName("测试城市1");
			areaList.add(area1);

			ColumnSelectArea area2 = new ColumnSelectArea();
			area2.setAreaType(SelectAreaType.city);
			area2.setCityId(UuidUtils.getUuid());
			area2.setProvinceId(UuidUtils.getUuid());
			area2.setProvinceName("测试省2");
			area2.setCityName("测试城市2");
			areaList.add(area2);

			List<String> goodsList = new ArrayList<>();
			for (int i = 10; i < 15; i++) {
				goodsList.add(i + "");
			}

			BaseResult result = homeIconService.save(entity, areaList, goodsList);
			log.info("测试 保存首页ICON ：{}", result);
			Assert.assertTrue("测试 保存首页ICON失败", result.getStatus().equals("0"));
		} catch (Exception e) {
			log.error("测试 保存首页ICON异常:{}", e);
		}
	}

	@Test
	public void test2FindList() {
		try {
			log.info("测试获取首页ICON列表");
			HomeIconParamDto paramDto = new HomeIconParamDto();
			List<String> ids = new ArrayList<>();
			ids.add("8a94e7ce596d9a1701596d9a5a750006");
			// paramDto.setAddStartTime(new Date());
			// paramDto.setAddEndTime(new Date());
			// paramDto.setUpdateStartTime(new Date());
			// paramDto.setUpdateEndTime(new Date());
			List<ColumnHomeIcon> list = homeIconService.findList(paramDto);
			Assert.assertTrue("测试获取首页ICON列表", list.size() > 0);
		} catch (Exception e) {
			log.error("查询首页ICON列表异常:{}", e);
		}
	}
}
