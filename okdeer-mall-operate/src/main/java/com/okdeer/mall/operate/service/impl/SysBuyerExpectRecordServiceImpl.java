package com.okdeer.mall.operate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.operate.mapper.SysBuyerExpectRecordMapper;
import com.okdeer.mall.operate.service.SysBuyerExpectRecordService;

/**
 * 
 * ClassName: SysBuyerExpectRecordServiceImpl 
 * @Description: 用户期待记录
 * @author tangy
 * @date 2017年1月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     2.0.0          2017年1月8日                               tangy
 */
@Service
public class SysBuyerExpectRecordServiceImpl extends BaseServiceImpl implements SysBuyerExpectRecordService {

	/**
	 * mapper注入
	 */
	@Autowired
	private SysBuyerExpectRecordMapper sysBuyerExpectRecordMapper;	
	
	@Override
	public IBaseMapper getBaseMapper() {
		return sysBuyerExpectRecordMapper;
	}

	@Override
	public int findNumberByMachineCode(String machineCode) {
		return sysBuyerExpectRecordMapper.findNumberByMachineCode(machineCode);
	}

}
