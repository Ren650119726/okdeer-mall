/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2016年11月14日 
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
import com.okdeer.mall.risk.dto.RiskWhiteDto;
import com.okdeer.mall.risk.entity.RiskWhite;
import com.okdeer.mall.risk.service.RiskWhiteApi;
import com.okdeer.mall.risk.service.RiskWhiteService;


/**
 * ClassName: IWhiteListApiImpl 
 * @Description: 白名单api实现类
 * @author xuzq01
 * @date 2016年11月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.2          2016年11月14日                       xuzq01              	白名单api实现类
 */
@Service(version="1.2.0")
public class RiskWhiteApiImpl implements RiskWhiteApi {
	
	@Autowired 
	RiskWhiteService whiteListService;
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.RiskWhiteApi#findWhiteList(com.okdeer.mall.risk.dto.RiskWhiteDto, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public PageUtils<RiskWhite> findWhiteList(RiskWhiteDto whiteManagerDto, Integer pageNumber,
			Integer pageSize) {
		
		return whiteListService.findWhiteList(whiteManagerDto, pageNumber, pageSize);
	}

	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see com.okdeer.mall.risk.service.RiskWhiteApi#add(java.lang.String, java.lang.String)
	 */
	@Override
	public void add(String account, String userId) throws Exception {
		List<RiskWhite> riskWhiteList = new ArrayList<RiskWhite>();
		List<String> accountNameList = (List<String>) java.util.Arrays.asList(account.trim().split(","));
		for(String accountName:accountNameList){
			RiskWhite riskWhite = new RiskWhite();
			Date date = new Date();
			riskWhite.setId(UuidUtils.getUuid());
			riskWhite.setTelephoneAccount(accountName);
			riskWhite.setAccountType(0);
			riskWhite.setCreateUserId(userId);
			riskWhite.setUpdateUserId(userId);
			riskWhite.setCreateTime(date);
			riskWhite.setUpdateTime(date);
			riskWhite.setDisabled(0);
			riskWhiteList.add(riskWhite);
		}
		whiteListService.addBatch(riskWhiteList);
	}

	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see com.okdeer.mall.risk.service.RiskWhiteApi#deleteById(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteById(String accountId, String id) throws Exception {
		whiteListService.delete(accountId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.RiskWhiteApi#deleteBatchByIds(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteBatchByIds(String accountIds, String updateUserId) {
		Date updateTime = new Date();
		List<String> ids = (List<String>) java.util.Arrays.asList(accountIds.split(","));; 
		
		whiteListService.deleteBatchByIds(ids,updateUserId,updateTime);
		
	}

	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see com.okdeer.mall.risk.service.RiskWhiteApi#findWhiteById(java.lang.String)
	 */
	@Override
	public RiskWhite findWhiteById(String accountId) throws Exception {
		return whiteListService.findById(accountId);
	}

	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see com.okdeer.mall.risk.service.RiskWhiteApi#updateWhite(com.okdeer.mall.risk.entity.RiskWhite)
	 */
	@Override
	public void updateWhite(RiskWhite riskWhite) throws Exception {
		Date updateTime = new Date();
		riskWhite.setUpdateTime(updateTime);
		whiteListService.update(riskWhite);
	}

}
