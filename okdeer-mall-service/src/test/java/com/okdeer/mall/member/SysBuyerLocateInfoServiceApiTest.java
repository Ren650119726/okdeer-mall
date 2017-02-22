package com.okdeer.mall.member;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.member.api.SysBuyerLocateInfoServiceApiImpl;
import com.okdeer.mall.member.member.dto.SysBuyerLocateInfoDto;

public class SysBuyerLocateInfoServiceApiTest extends BaseServiceTest {
	/** 日志对象 */
	private static final Logger log = LoggerFactory.getLogger(SysBuyerLocateInfoServiceApiTest.class);

	@Autowired
	private SysBuyerLocateInfoServiceApiImpl sysBuyerLocateInfoServiceApiImpl;

	@Test
	public void testSave() {
		// 初始化APP设备访问记录信息
		SysBuyerLocateInfoDto dto = new SysBuyerLocateInfoDto();
		dto.setUserId("0");
		dto.setMachineCode("0-0-0");
		dto.setProvinceId("19");
		dto.setProvinceName("不知道");
		dto.setCityId("291");
		dto.setCityName("不知道");
		dto.setLongitude(123.123);
		dto.setLatitude(234.234);
		dto.setRegisterSource("0");
		dto.setStoreCityId("291");
		try {
			sysBuyerLocateInfoServiceApiImpl.save(dto);
		} catch (Exception e) {
			log.error("测试APP设备访问记录异常:{}", e);
		}
	}
}
