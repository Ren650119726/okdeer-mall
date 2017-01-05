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

import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.common.utils.BaseResult;
import com.okdeer.mall.Application;
import com.okdeer.mall.operate.dto.AppRecommendParamDto;
import com.okdeer.mall.operate.entity.ColumnAppRecommend;
import com.okdeer.mall.operate.entity.ColumnAppRecommendGoods;
import com.okdeer.mall.operate.entity.ColumnSelectArea;
import com.okdeer.mall.operate.enums.AppRecommendPlace;
import com.okdeer.mall.operate.enums.AppRecommendStatus;
import com.okdeer.mall.operate.enums.SelectAreaType;
import com.okdeer.mall.operate.service.ColumnAppRecommendService;

/**
 * ClassName: ColumnAppRecommendServiceTest 
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
public class ColumnAppRecommendServiceTest {

	/** 日志对象 */
	private static final Logger log = LoggerFactory.getLogger(ColumnAppRecommendServiceTest.class);

	@Autowired
	private ColumnAppRecommendService recommendService;

	@Test
	public void test1Save() {
		try {
			log.info("测试保存APP端服务商品推荐信息");
			ColumnAppRecommend entity = new ColumnAppRecommend();
			entity.setTitle("测试服务推荐");
			entity.setAreaType(SelectAreaType.city);
			entity.setCoverPicUrl("www.okdeer.com");
			entity.setPlace(AppRecommendPlace.find);
			entity.setStatus(AppRecommendStatus.show);

			List<ColumnSelectArea> areaList = new ArrayList<>();
			ColumnSelectArea area1 = new ColumnSelectArea();
			area1.setAreaType(SelectAreaType.city);
			area1.setCityId(DateUtils.getDateRandom());
			area1.setProvinceId(DateUtils.getDateRandom());
			area1.setProvinceName("测试省1");
			area1.setCityName("测试城市1");
			areaList.add(area1);
			ColumnSelectArea area2 = new ColumnSelectArea();
			area2.setAreaType(SelectAreaType.city);
			area2.setCityId(DateUtils.getDateRandom());
			area2.setProvinceId(DateUtils.getDateRandom());
			area2.setProvinceName("测试省2");
			area2.setCityName("测试城市2");
			areaList.add(area2);

			List<ColumnAppRecommendGoods> goodsList = new ArrayList<>();
			ColumnAppRecommendGoods goods1 = new ColumnAppRecommendGoods();
			goods1.setIsShow(1);
			goods1.setStoreSkuId(UuidUtils.getUuid());
			goods1.setSort(0);
			goodsList.add(goods1);
			ColumnAppRecommendGoods goods2 = new ColumnAppRecommendGoods();
			goods2.setIsShow(1);
			goods2.setStoreSkuId(UuidUtils.getUuid());
			goods2.setSort(0);
			goodsList.add(goods2);

			BaseResult result = recommendService.save(entity, areaList, goodsList);
			log.info("保存APP端服务商品推荐信息 ：{}", result);
			Assert.assertTrue("测试保存APP端服务商品推荐信息失败", result.getStatus().equals("0"));
		} catch (Exception e) {
			log.error("测试保存APP端服务商品推荐信息异常:{}", e);
		}
	}

	@Test
	public void test2FindList() {
		try {
			log.info("测试获取APP端服务商品推荐与商品关联列表");
			AppRecommendParamDto paramDto = new AppRecommendParamDto();
			List<String> ids = new ArrayList<>();
			ids.add("8a94e7ce5968c3f4015968c471d50005");
			// paramDto.setAddStartTime(new Date());
			// paramDto.setAddEndTime(new Date());
			// paramDto.setUpdateStartTime(new Date());
			// paramDto.setUpdateEndTime(new Date());
			List<ColumnAppRecommend> list = recommendService.findList(paramDto);
			Assert.assertTrue("获取APP端服务商品推荐与商品关联列表失败", list.size() > 0);
		} catch (Exception e) {
			log.error("查询获取APP端服务商品推荐与商品关联列表异常:{}", e);
		}
	}

	@Test
	public void test3DeleteByIds() {
		try {
			List<String> ids = new ArrayList<>();
			ids.add("1");
			log.info("测试根据APP端服务商品推荐ID删除数据");
			Integer result = recommendService.deleteByIds(ids);
			Assert.assertNotNull("根据APP端服务商品推荐ID删除数据失败", result);
		} catch (Exception e) {
			log.error("根据APP端服务商品推荐ID删除数据异常:{}", e);
		}
	}
}
