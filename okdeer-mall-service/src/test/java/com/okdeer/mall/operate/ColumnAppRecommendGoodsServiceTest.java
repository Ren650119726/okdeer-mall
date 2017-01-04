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
import com.okdeer.mall.Application;
import com.okdeer.mall.operate.dto.AppRecommendGoodsParamDto;
import com.okdeer.mall.operate.entity.ColumnAppRecommendGoods;
import com.okdeer.mall.operate.service.ColumnAppRecommendGoodsService;

/**
 * ClassName: ColumnAppRecommendGoodsServiceTest 
 * @Description: APP端服务商品推荐与商品关联服务测试
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
public class ColumnAppRecommendGoodsServiceTest {

	/** 日志对象 */
	private static final Logger log = LoggerFactory.getLogger(ColumnAppRecommendGoodsServiceTest.class);

	@Autowired
	private ColumnAppRecommendGoodsService recommendGoodsService;

	@Test
	public void test1InsertMore() {
		try {
			log.info("测试批量APP端服务商品推荐与商品关联数据");
			List<ColumnAppRecommendGoods> list = new ArrayList<ColumnAppRecommendGoods>();
			ColumnAppRecommendGoods recommend = null;
			for (int i = 1; i < 10; i++) {
				recommend = new ColumnAppRecommendGoods();
				recommend.setId(UuidUtils.getUuid());
				recommend.setRecommendId("1");
				recommend.setSort(0);
				recommend.setStoreSkuId(i + "");
				recommend.setIsShow(1);
				list.add(recommend);
			}

			int result = recommendGoodsService.insertMore(list);
			Assert.assertTrue("批量APP端服务商品推荐与商品关联数据失败", result > 0);
		} catch (Exception e) {
			log.error("批量插入APP端服务商品推荐与商品关联异常:{}", e);
		}
	}

	@Test
	public void test2FindList() {
		try {
			log.info("测试获取APP端服务商品推荐与商品关联列表");
			AppRecommendGoodsParamDto paramDto = new AppRecommendGoodsParamDto();
			paramDto.setIsShow(1);
			// paramDto.setRecommendId(1 + "");
			List<String> ids = new ArrayList<String>();
			ids.add("1");
			paramDto.setRecommendIds(ids);
			List<ColumnAppRecommendGoods> list = recommendGoodsService.findList(paramDto);
			Assert.assertNotNull(list);
		} catch (Exception e) {
			log.error("查询获取APP端服务商品推荐与商品关联列表异常:{}", e);
		}
	}

	@Test
	public void test3FindListByRecommendId() {
		try {
			log.info("测试获取APP端服务商品推荐与商品关联列表");
			List<ColumnAppRecommendGoods> list = recommendGoodsService.findListByRecommendId("1");
			Assert.assertNotNull(list);
		} catch (Exception e) {
			log.error("查询获取APP端服务商品推荐与商品关联列表异常:{}", e);
		}
	}

	@Test
	public void test4DeleteByRecommendId() {
		try {
			log.info("测试根据推荐ID删除与商品关联数据");
			int result = recommendGoodsService.deleteByRecommendId("1");
			Assert.assertTrue("根据推荐ID删除与商品关联数据失败", result > 0);
		} catch (Exception e) {
			log.error("根据推荐ID删除与商品关联数据异常:{}", e);
		}
	}

}
