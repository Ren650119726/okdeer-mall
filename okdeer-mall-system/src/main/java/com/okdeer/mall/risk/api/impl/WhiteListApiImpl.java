/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2016年11月14日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.api.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.risk.dto.WhiteManagerDto;
import com.okdeer.mall.risk.entity.RiskWhite;
import com.okdeer.mall.risk.service.IWhiteListApi;
import com.okdeer.mall.risk.service.IWhiteListService;


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
@Service(version="1.0.0")
public class WhiteListApiImpl implements IWhiteListApi {
	
	@Autowired 
	IWhiteListService whiteListService;
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.IWhiteListApi#findWhiteList(com.okdeer.mall.risk.dto.WhiteManagerDto, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public PageUtils<RiskWhite> findWhiteList(WhiteManagerDto whiteManagerDto, Integer pageNumber,
			Integer pageSize) {
		
		return whiteListService.findWhiteList(whiteManagerDto, pageNumber, pageSize);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.IWhiteListApi#delete(java.lang.String, java.lang.String)
	 */
	@Override
	public void delete(String whiteId, String userId) {
		try {
			whiteListService.delete(whiteId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see com.okdeer.mall.risk.service.IWhiteListApi#add(java.lang.String, java.lang.String)
	 */
	@Override
	public void add(String account, String userId) throws Exception {
		String whiteId = UuidUtils.getUuid();
		Date date = new Date();
		RiskWhite riskWhite = new RiskWhite();
		riskWhite.setId(whiteId);
		riskWhite.setTelephoneAccount(account);
		riskWhite.setCreateUserId(userId);
		riskWhite.setUpdateUserId(userId);
		riskWhite.setCreateTime(date);
		riskWhite.setUpdateTime(date);
		riskWhite.setDisabled(0);
		whiteListService.add(riskWhite);
	}
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.IWhiteListApi#selectWhiteByAccount(java.lang.String)
	 */
	@Override
	public int selectWhiteByAccount(String account) {
		return whiteListService.selectWhiteByAccount(account);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.IWhiteListApi#addBatch(java.lang.String, java.lang.String)
	 */
	@Override
	public void addBatch(String telephoneAccount, String id) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see com.okdeer.mall.risk.service.IWhiteListApi#deleteById(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteById(String accountId, String id) throws Exception {
		whiteListService.delete(accountId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.IWhiteListApi#deleteBatchByIds(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteBatchByIds(String accountIds, String updateUserId) {
		Date updateTime = new Date();
		List<String> ids = (List<String>) java.util.Arrays.asList(accountIds.split(","));; 
		
		whiteListService.deleteBatchByIds(ids,updateUserId,updateTime);
		
	}

}
