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
import com.okdeer.mall.operate.entity.ColumnSelectArea;
import com.okdeer.mall.operate.enums.SelectAreaType;
import com.okdeer.mall.operate.service.ColumnSelectAreaService;

/**
 * ClassName: ColumnSelectAreaServiceTest 
 * @Description: 栏目与地区关联服务测试
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
public class ColumnSelectAreaServiceTest {

	/** 日志对象 */
	private static final Logger log = LoggerFactory.getLogger(ColumnSelectAreaServiceTest.class);

	@Autowired
	private ColumnSelectAreaService selectAreaService;

	@Test
	public void test1InsertMore() {
		try {
			log.info("测试批量插入活动与地区数据");
			List<ColumnSelectArea> list = new ArrayList<ColumnSelectArea>();
			ColumnSelectArea area = null;
			for (int i = 1; i < 10; i++) {
				area = new ColumnSelectArea();
				area.setId(UuidUtils.getUuid());
				area.setColumnType(2);
				area.setColumnId("1");
				area.setProvinceId(i + "");
				area.setProvinceName("测试省名称");
				if (i > 5) {
					area.setAreaType(SelectAreaType.province);
				} else {
					area.setAreaType(SelectAreaType.city);
					area.setCityId(1 + "");
					area.setCityName("测试测试名称");
				}
				list.add(area);
			}

			int result = selectAreaService.insertMore(list);
			Assert.assertTrue("批量插入活动与地区数据失败", result > 0);
		} catch (Exception e) {
			log.error("批量插入活动与地区数据异常:{}", e);
		}
	}

	@Test
	public void test2findColumnIdsByCity() {
		try {
			log.info("测试根据城市获取栏目ID集合");
			List<String> list = selectAreaService.findColumnIdsByCity("1", "1", 2);
			Assert.assertNotNull(list);
		} catch (Exception e) {
			log.error("根据城市获取栏目ID集合异常:{}", e);
		}
	}

	@Test
	public void test3FindListByColumnId() {
		try {
			log.info("测试获取活动与地区列表");
			List<ColumnSelectArea> list = selectAreaService.findListByColumnId("1");
			Assert.assertTrue("测试获取活动与地区列表失败", list.size() > 0);
		} catch (Exception e) {
			log.error("查询活动与地区列表异常:{}", e);
		}
	}

	@Test
	public void test4DeleteByColumnId() {
		try {
			log.info("测试根据活动ID删除数据");
			int result = selectAreaService.deleteByColumnId("1");
			Assert.assertTrue("根据活动ID删除数据失败", result > 0);
		} catch (Exception e) {
			log.error("根据活动ID删除数据异常:{}", e);
		}
	}
}
