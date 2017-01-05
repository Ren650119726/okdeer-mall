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

import com.okdeer.mall.Application;
import com.okdeer.mall.operate.entity.ColumnHomeIconGoods;
import com.okdeer.mall.operate.service.ColumnHomeIconGoodsService;

/**
 * ClassName: ColumnHomeIconServiceTest 
 * @Description: 首页ICON商品关联服务测试
 * @author tangzj02
 * @date 2017年01月05日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.0       2016-01-05         tangzj02                        添加
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ColumnHomeIconGoodsServiceTest {

	/** 日志对象 */
	private static final Logger log = LoggerFactory.getLogger(ColumnHomeIconGoodsServiceTest.class);

	@Autowired
	private ColumnHomeIconGoodsService homeIconGoodsService;

	@Test
	public void test1InsertMore() {
		try {
			log.info("测试  批量插入首页icon数据");
			List<ColumnHomeIconGoods> list = new ArrayList<ColumnHomeIconGoods>();
			ColumnHomeIconGoods goods = new ColumnHomeIconGoods();
			goods.setId("1");
			goods.setHomeIconId("1");
			goods.setStoreSkuId("1");
			list.add(goods);
			int result = homeIconGoodsService.insertMore(list);
			Assert.assertTrue("测试  批量插入首页icon数据失败", result > 0);
		} catch (Exception e) {
			log.error("测试   批量插入首页icon数据异常:{}", e);
		}
	}

	@Test
	public void test2FindListByHomeIconId() {
		try {
			log.info("测试  根据首页icon ID查询列表");
			List<ColumnHomeIconGoods> list = homeIconGoodsService.findListByHomeIconId("1");
			Assert.assertTrue("测试  根据首页icon ID查询列表失败", list.size() > 0);
		} catch (Exception e) {
			log.error("测试  根据首页icon ID查询列表异常:{}", e);
		}
	}

	@Test
	public void test3FindListByHomeIconIds() {
		try {
			log.info("测试  根据首页icon ID集合查询列表");
			List<String> ids = new ArrayList<String>();
			for (int i = 1; i < 10; i++) {
				ids.add(i + "");
			}
			List<ColumnHomeIconGoods> list = homeIconGoodsService.findListByHomeIconIds(ids);
			Assert.assertTrue("测试  根据首页icon ID集合查询列表失败", list.size() > 0);
		} catch (Exception e) {
			log.error("测试  根据首页icon ID集合查询列表异常:{}", e);
		}
	}

	@Test
	public void test4DeleteByHomeIconId() {
		try {
			log.info("测试  根据首页icon ID删除数据");
			List<String> ids = new ArrayList<String>();
			for (int i = 1; i < 10; i++) {
				ids.add(i + "");
			}
			int result = homeIconGoodsService.deleteByHomeIconId("1");
			Assert.assertTrue("测试  根据首页icon ID删除数据失败", result > 0);
		} catch (Exception e) {
			log.error("测试  根据首页icon ID删除数据异常:{}", e);
		}
	}
}
