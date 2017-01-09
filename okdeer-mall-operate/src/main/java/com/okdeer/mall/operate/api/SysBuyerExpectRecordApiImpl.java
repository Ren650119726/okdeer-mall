package com.okdeer.mall.operate.api;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.operate.dto.SysBuyerExpectRecordDto;
import com.okdeer.mall.operate.entity.SysBuyerExpectRecord;
import com.okdeer.mall.operate.service.SysBuyerExpectRecordApi;
import com.okdeer.mall.operate.service.SysBuyerExpectRecordService;

/**
 * 
 * ClassName: SysBuyerExpectRecordApiImpl 
 * @Description: 用户期待
 * @author tangy
 * @date 2017年1月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     2.0.0          2017年1月8日                               tangy
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.operate.service.SysBuyerExpectRecordApi")
public class SysBuyerExpectRecordApiImpl implements SysBuyerExpectRecordApi {

	/**
	 * 用户期待记录
	 */
	@Autowired
	private SysBuyerExpectRecordService sysBuyerExpectRecordService;
	
	@Override
	public void addSysBuyerExpectRecord(SysBuyerExpectRecordDto sysBuyerExpectRecordDto) throws Exception {
		if (sysBuyerExpectRecordDto != null) {
			SysBuyerExpectRecord sysBuyerExpectRecord = BeanMapper.map(sysBuyerExpectRecordDto, SysBuyerExpectRecord.class);
			sysBuyerExpectRecordService.add(sysBuyerExpectRecord);
		}		
	}

	@Override
	public int findNumByMachineCode(String machineCode) {
		return sysBuyerExpectRecordService.findNumberByMachineCode(machineCode);
	}

}
