/** 
 *@Project: okdeer-mall-system 
 *@Author: guocp
 *@Date: 2016年11月17日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.risk.mapper.RiskTriggerRecordMapper;
import com.okdeer.mall.risk.service.RiskTriggerRecordService;


/**
 * ClassName: RiskTriggerRecordServiceImpl 
 * @Description: TODO
 * @author guocp
 * @date 2016年11月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class RiskTriggerRecordServiceImpl extends BaseServiceImpl implements RiskTriggerRecordService {

	@Autowired
	private RiskTriggerRecordMapper riskTriggerRecordMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return riskTriggerRecordMapper;
	}

}
