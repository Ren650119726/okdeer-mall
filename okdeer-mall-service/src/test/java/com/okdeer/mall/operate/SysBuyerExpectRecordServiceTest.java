package com.okdeer.mall.operate;

import java.util.Date;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.operate.entity.SysBuyerExpectRecord;
import com.okdeer.mall.operate.service.SysBuyerExpectRecordService;

import net.sf.json.JSONObject;

/**
 * 
 * ClassName: SysBuyerExpectRecordServiceTest 
 * @Description: 用户期待记录测试
 * @author tangy
 * @date 2017年1月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     2.0.0          2017年1月8日                               tangy
 */
public class SysBuyerExpectRecordServiceTest extends BaseServiceTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SysBuyerExpectRecordServiceTest.class);
	
	/**
	 * 用户期待记录
	 */
	@Autowired
	private SysBuyerExpectRecordService sysBuyerExpectRecordService;
	
	//@Test
	public void addTest(){
		SysBuyerExpectRecord sysBuyerExpectRecord = new SysBuyerExpectRecord();
		sysBuyerExpectRecord.setId(UuidUtils.getUuid());
		sysBuyerExpectRecord.setCategoryIds("1,2,3");
		sysBuyerExpectRecord.setCategoryNames("测试1,测试2,测试3");
		sysBuyerExpectRecord.setCityName("深圳市");
		sysBuyerExpectRecord.setCreateTime(new Date());
		sysBuyerExpectRecord.setMachineCode("123456789");
		sysBuyerExpectRecord.setUserId("02a0b1027472497faad29b8a1997de7e");
		sysBuyerExpectRecord.setUserPhone("13888888888");
		sysBuyerExpectRecord.setLatitude(113.892427);
		sysBuyerExpectRecord.setLongitude(22.571266);
		LOGGER.info(sysBuyerExpectRecord.getId());
		try {
			sysBuyerExpectRecordService.add(sysBuyerExpectRecord);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		
	}
	
	@Test
	public void findByIdTest(){
		String id = "8a94e75c597be2d001597be2d1e00002";
		try {
			SysBuyerExpectRecord sysBuyerExpectRecord = sysBuyerExpectRecordService.findById(id);
		    LOGGER.info(JSONObject.fromObject(sysBuyerExpectRecord).toString());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}
	
	@Test
	public void findNumberByMachineCodeTest(){
		String machineCode = "123456789";
		int count = sysBuyerExpectRecordService.findNumberByMachineCode(machineCode);
		LOGGER.info("count:"+ count);
	}
	
}
