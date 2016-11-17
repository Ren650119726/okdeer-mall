/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2016年11月15日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.api.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.risk.dto.BlackManagerDto;
import com.okdeer.mall.risk.entity.RiskBlack;
import com.okdeer.mall.risk.service.IBlackListApi;
import com.okdeer.mall.risk.service.IBlackListService;


/**
 * ClassName: BlackListApiImpl 
 * @Description: 黑名单管理api实现类
 * @author xuzq01
 * @date 2016年11月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2			2016年11月15日		xuzq01				黑名单管理api实现类
 */
@Service(version="1.0.0")
public class BlackListApiImpl implements IBlackListApi {

	@Autowired 
	IBlackListService blackListService;
	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see com.okdeer.mall.risk.service.IBlackListApi#deleteBlackById(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteBlackById(String accountId, String id) throws Exception {
		blackListService.delete(accountId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.IBlackListApi#findBlackList(com.okdeer.mall.risk.dto.BlackManagerDto, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public PageUtils<RiskBlack> findBlackList(BlackManagerDto blackManagerDto, Integer pageNumber, Integer pageSize) {
		return blackListService.findBlackList(blackManagerDto, pageNumber, pageSize);
	}

	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see com.okdeer.mall.risk.service.IBlackListApi#addBlack(java.lang.String, java.lang.String)
	 */
	@Override
	public void addBlack(RiskBlack riskBlack,String userId) throws Exception {
		String blackId = UuidUtils.getUuid();
		Date date = new Date();
		riskBlack.setId(blackId);
		riskBlack.setCreateUserId(userId);
		riskBlack.setUpdateUserId(userId);
		riskBlack.setCreateTime(date);
		riskBlack.setUpdateTime(date);
		riskBlack.setDisabled(0);
		blackListService.add(riskBlack);

	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.IBlackListApi#addBlackBatch(java.lang.String, java.lang.String)
	 */
	@Override
	public void addBlackBatch(String riskBlack, String id) {
		List<RiskBlack> riskBlackList = new ArrayList<RiskBlack>();
		blackListService.addBath(riskBlackList);

	}

}
