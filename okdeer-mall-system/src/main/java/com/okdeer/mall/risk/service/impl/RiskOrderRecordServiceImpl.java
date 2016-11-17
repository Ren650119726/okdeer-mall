/** 
 *@Project: okdeer-mall-system 
 *@Author: guocp
 *@Date: 2016年11月17日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.risk.mapper.RiskOrderRecordMapper;
import com.okdeer.mall.risk.service.RiskOrderRecordService;


/**
 * ClassName: RiskOrderRecordServiceImpl 
 * @Description: 风控记录实现
 * @author guocp
 * @date 2016年11月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public class RiskOrderRecordServiceImpl extends BaseServiceImpl implements RiskOrderRecordService {

	@Autowired
	private RiskOrderRecordMapper riskOrderRecordMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return riskOrderRecordMapper;
	}



}
