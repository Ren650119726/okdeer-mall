/** 
 *@Project: okdeer-mall-system 
 *@Author: zhangkn
 *@Date: 2016年11月14日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.api.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.risk.dto.RiskTriggerRecordDto;
import com.okdeer.mall.risk.entity.RiskTriggerRecord;
import com.okdeer.mall.risk.service.RiskTriggerRecordApi;
import com.okdeer.mall.risk.service.RiskTriggerRecordService;


/**
 * ClassName: RiskTriggerRecordApiImpl 
 * @Description: 风控记录apiimpl
 * @author zhangkn
 * @date 2016年11月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version="1.0.0")
public class RiskTriggerRecordApiImpl implements RiskTriggerRecordApi {
	
	@Autowired
	RiskTriggerRecordService recordService;

	@Override
 	public PageUtils<RiskTriggerRecordDto> list(Map<String,Object> params,int pageNumber,int pageSize) 
			throws Exception {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<RiskTriggerRecord> list = recordService.list(params);
		PageUtils<RiskTriggerRecord> page = new PageUtils<RiskTriggerRecord>(list);
		
		//因为实体类不同,只能把page对象复制成另一个page对象
		List<RiskTriggerRecordDto> dtoList = BeanMapper.mapList(list, RiskTriggerRecordDto.class);
		PageUtils<RiskTriggerRecordDto> dtoPage = new PageUtils<RiskTriggerRecordDto>(dtoList);
		page.setList(null);
		BeanMapper.copy(page, dtoPage);
		dtoPage.setList(dtoList);
		return dtoPage;
	}
	
	@Override
	public void deleteBatch(List<String> ids) throws Exception{
		recordService.deleteBatch(ids);
	}

	@Override
	public RiskTriggerRecordDto findById(String id) throws Exception {
		 RiskTriggerRecord record = recordService.findById(id);
		 RiskTriggerRecordDto dto = new RiskTriggerRecordDto();
		 BeanMapper.copy(record, dto);
		 return dto;
	}
}
