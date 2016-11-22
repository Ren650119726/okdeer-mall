/** 
 *@Project: okdeer-mall-system 
 *@Author: guocp
 *@Date: 2016年11月17日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.risk.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.risk.enums.IsPreferential;
import com.okdeer.mall.risk.mapper.RiskOrderRecordMapper;
import com.okdeer.mall.risk.po.RiskOrderRecordPo;
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
@Service
public class RiskOrderRecordServiceImpl extends BaseServiceImpl implements RiskOrderRecordService {

	@Autowired
	private RiskOrderRecordMapper riskOrderRecordMapper;

	@Override
	public IBaseMapper getBaseMapper() {
		return riskOrderRecordMapper;
	}

	@Override
	public RiskOrderRecordPo findByLoginName(String loginName, IsPreferential isPreferential) {
		RiskOrderRecordPo record = riskOrderRecordMapper.findByParam(loginName, "loginName", isPreferential.getCode());
		record.setTels(riskOrderRecordMapper.findTelsByParam(loginName, "loginName", isPreferential.getCode()));
		return record;
	}

	@Override
	public RiskOrderRecordPo findByDeviceId(String deviceId, IsPreferential isPreferential) {
		RiskOrderRecordPo record = riskOrderRecordMapper.findByParam(deviceId, "deviceId", isPreferential.getCode());
		record.setTels(riskOrderRecordMapper.findTelsByParam(deviceId, "deviceId", isPreferential.getCode()));
		record.setLoginNames(riskOrderRecordMapper.findLoginNamesByParam(deviceId, isPreferential.getCode()));
		return record;
	}

	@Override
	public RiskOrderRecordPo findByPayAccount(String payAccount, IsPreferential isPreferential) {
		RiskOrderRecordPo record = riskOrderRecordMapper.findByParam(payAccount, "payAccount",
				isPreferential.getCode());
		record.setTels(riskOrderRecordMapper.findTelsByParam(payAccount, "payAccount", isPreferential.getCode()));
		return record;
	}

}
