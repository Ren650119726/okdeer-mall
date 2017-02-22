package com.okdeer.mall.system.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.system.mapper.SysBuyerFirstOrderRecordMapper;
import com.okdeer.mall.system.service.SysBuyerFirstOrderRecordService;

@Service
public class SysBuyerFirstOrderRecordServiceImpl extends BaseServiceImpl implements SysBuyerFirstOrderRecordService {

	@Resource
	private SysBuyerFirstOrderRecordMapper sysBuyerFirstOrderRecordMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return sysBuyerFirstOrderRecordMapper;
	}

}
