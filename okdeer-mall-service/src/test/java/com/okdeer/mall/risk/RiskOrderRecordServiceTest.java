/** 
 *@Project: okdeer-mall-service 
 *@Author: xuzq01
 *@Date: 2016年11月25日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.risk;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.mall.Application;
import com.okdeer.mall.risk.entity.RiskOrderRecord;
import com.okdeer.mall.risk.enums.IsPreferential;
import com.okdeer.mall.risk.enums.PayAccountType;
import com.okdeer.mall.risk.service.RiskOrderRecordService;

/**
 * ClassName: RiskOrderRecordServiceTest 
 * @Description: TODO
 * @author xuzq01
 * @date 2016年11月25日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class RiskOrderRecordServiceTest {

	private static final Logger log = LoggerFactory.getLogger(RiskOrderRecordServiceTest.class);

	/**
	 * 风控人员管理service类
	 */
	@Resource
	private RiskOrderRecordService riskOrderRecordService;

	@Test
	public void deleteByTimeTest() throws Exception {
		Date createTime = new Date();
		riskOrderRecordService.deleteByTime(createTime);

	}

	/**
	 * 测试插入风控记录
	 * @throws Exception   
	 * @author guocp
	 * @date 2016年11月29日
	 */
	@Test
	public void addTest() throws Exception {
		int num = 1000;
		for (int i = 0; i < num; i++) {
			new Thread(() -> {
				String id = UuidUtils.getUuid();
				RiskOrderRecord riskOrder = new RiskOrderRecord();
				riskOrder.setCreateTime(new Date());
				riskOrder.setDeviceId("1234546");
				riskOrder.setFacePrice(new BigDecimal("100.00"));
				riskOrder.setId(id);
				riskOrder.setIsPreferential(IsPreferential.NO);
				riskOrder.setLoginName("186");
				riskOrder.setPayAccount("guocp");
				riskOrder.setPayAccountType(PayAccountType.ALIPAY);
				riskOrder.setTel("186");
				try {
					riskOrderRecordService.add(riskOrder);
					addSuccessCount.addAndGet(1);
				} catch (Exception e) {
					log.error("新增记录错误", e);
				}

				try {
					RiskOrderRecord riskOrderRecord = riskOrderRecordService.findById(id);
					if (riskOrderRecord == null) {
						log.warn("查询记录失败：{}", id);
						failCount.addAndGet(1);
					} else {
						successCount.addAndGet(1);
					}
				} catch (Exception e) {
					log.error("查询记录错误", e);
				}

			}).start();

		}
		Thread.sleep(10000);
		log.info("插入成功数据：{},查询失败：{}次,成功:{}次", String.valueOf(addSuccessCount), String.valueOf(failCount),
				String.valueOf(successCount.get()));
	}

	private AtomicInteger successCount = new AtomicInteger();

	private AtomicInteger failCount = new AtomicInteger();

	private AtomicInteger addSuccessCount = new AtomicInteger();
}
