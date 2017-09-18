/** 
 *@Project: okdeer-mall-test 
 *@Author: xuzq01
 *@Date: 2017年9月13日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.order.api;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.jxc.common.utils.DateUtils;
import com.okdeer.mall.activity.dto.ActivityPinMoneyDto;
import com.okdeer.mall.activity.dto.ActivityPinMoneyQueryDto;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.order.dto.TradePinMoneyObtainDto;
import com.okdeer.mall.order.dto.TradePinMoneyQueryDto;
import com.okdeer.mall.order.dto.TradePinMoneyUseDto;
import com.okdeer.mall.order.service.TradePinMoneyApi;

/**
 * ClassName: TradePinMoneyApiTest 
 * @Description: 用户零花钱服务测试类
 * @author xuzq01
 * @date 2017年9月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Transactional
public class TradePinMoneyApiTest extends BaseServiceTest {

	@Autowired
	private TradePinMoneyApi tradePinMoneyApi;
	
	@Test
	public void findMyUsableTotal() {
		String userId =("141102938903bd0f97c9a9694854bd8c");
		Date nowDate = DateUtils.getCurrDate();
		BigDecimal usableTotal = tradePinMoneyApi.findMyUsableTotal(userId, nowDate);
		assertNotNull(usableTotal);
	}
	
	@Test
	public void findObtainList() {
		String userId =("141102938903bd0f97c9a9694854bd8c");
		PageUtils<TradePinMoneyObtainDto> obtainDto = tradePinMoneyApi.findObtainList(userId, 1, 10);
		assertNotNull(obtainDto);
	}
	
	@Test
	public void findUseList() {
		String userId =("141102938903bd0f97c9a9694854bd8c");
		PageUtils<TradePinMoneyUseDto> useDto = tradePinMoneyApi.findUseList(userId, 1, 10);
		assertNotNull(useDto);
	}
	
	@Test
	public void findObtainPageList() {
		TradePinMoneyQueryDto paramDto = new TradePinMoneyQueryDto();
		paramDto.setUserPhone("18613138749");
		paramDto.setCreateTimeStart(DateUtils.parse("2017-08-25 05:00:00"));
		paramDto.setCreateTimeEnd(DateUtils.parse("2017-09-13 17:00:00"));
		PageUtils<TradePinMoneyObtainDto> pageDto = tradePinMoneyApi.
				findObtainPageList(paramDto, 1, 10);
		assertNotNull(pageDto);
	}
	
	@Test
	public void findObtainListCount() {
		TradePinMoneyQueryDto paramDto = new TradePinMoneyQueryDto();
		paramDto.setUserPhone("18613138749");
		paramDto.setCreateTimeStart(DateUtils.parse("2017-08-25 05:00:00"));
		paramDto.setCreateTimeEnd(DateUtils.parse("2017-09-13 17:00:00"));
		paramDto.setUserId("141102938903bd0f97c9a9694854bd8c");
		paramDto.setOrderId("402801555e190028015e1c2e24110110");
		paramDto.setActivityId("402801505e13629d015e136e4e3e0003");
		Integer obtainCount = tradePinMoneyApi.findObtainListCount(paramDto);
		assertNotNull(obtainCount);
	}
	
	@Test
	public void findUsePageList() {
		TradePinMoneyQueryDto paramDto = new TradePinMoneyQueryDto();
		paramDto.setUserPhone("18613138749");
		paramDto.setCreateTimeStart(DateUtils.parse("2017-09-01 05:00:00"));
		paramDto.setCreateTimeEnd(DateUtils.parse("2017-09-13 17:00:00"));
		
		PageUtils<TradePinMoneyUseDto> useDto = tradePinMoneyApi.findUsePageList(paramDto, 1, 10);
		assertNotNull(useDto);
	}
	
	@Test
	public void findUseListCountTest(){
		TradePinMoneyQueryDto paramDto = new TradePinMoneyQueryDto();
		paramDto.setUserPhone("18613138749");
		Calendar date = Calendar.getInstance();
		date.add(Calendar.WEEK_OF_MONTH, -2);
		paramDto.setCreateTimeStart(date.getTime());
		paramDto.setCreateTimeEnd(new Date());
		Integer count = tradePinMoneyApi.findUseListCount(paramDto);
		assertNotNull(count);
	}
	
	@Rollback(true)
	@Test
	public void addObtainRecord() throws Exception {
		ActivityPinMoneyQueryDto dto = new ActivityPinMoneyQueryDto();
		dto.setOrderId("402801555e190028015e1c2e24110110");
		dto.setUserId("141102938903bd0f97c9a9694854bd8c");

		ActivityPinMoneyDto moneyDto = new ActivityPinMoneyDto();
		moneyDto.setId(UuidUtils.getUuid());
		moneyDto.setValidDay(30);
		
		String deviceId = "452BA470-5D1F-4A3A-BBB4-8859CF2571EE";
		BigDecimal pinMoney = new BigDecimal("0.02");
		//无返回参数
		tradePinMoneyApi.addObtainRecord(dto, deviceId, moneyDto, pinMoney);

	}
	
	@Test
	public void findPinMoneyObtainAmount() {
		TradePinMoneyQueryDto paramDto = new TradePinMoneyQueryDto();
		paramDto.setCreateTimeStart(DateUtils.parse("2017-08-25 05:00:00"));
		paramDto.setCreateTimeEnd(DateUtils.parse("2017-09-13 17:00:00"));
		paramDto.setActivityId("402801505e13629d015e136e4e3e0003");
		BigDecimal amount = tradePinMoneyApi.findPinMoneyObtainAmount(paramDto);
		assertNotNull(amount);
	}
}
