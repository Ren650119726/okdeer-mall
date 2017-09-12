/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2017年9月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.system.api.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.system.dto.SysBuyerFirstOrderRecordDto;
import com.okdeer.mall.system.entity.SysBuyerFirstOrderRecord;
import com.okdeer.mall.system.service.SysBuyerFirstOrderRecordApi;
import com.okdeer.mall.system.service.SysBuyerFirstOrderRecordService;


/**
 * ClassName: SysBuyerFirstOrderRecordApiImpl 
 * @Description: 用户首单记录表api实现类
 * @author xuzq01
 * @date 2017年9月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version = "1.0.0")
public class SysBuyerFirstOrderRecordApiImpl implements SysBuyerFirstOrderRecordApi {

	@Autowired
	private SysBuyerFirstOrderRecordService sysBuyerFirstOrderRecordService;
	
	@Override
	public SysBuyerFirstOrderRecordDto findByUserId(String userId) {
		SysBuyerFirstOrderRecord record = sysBuyerFirstOrderRecordService.findByUserId(userId);
		return BeanMapper.map(record, SysBuyerFirstOrderRecordDto.class);
	}

}
