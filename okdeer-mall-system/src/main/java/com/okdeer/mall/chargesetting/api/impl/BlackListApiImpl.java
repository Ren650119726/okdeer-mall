/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2016年11月15日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.chargesetting.api.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.chargesetting.dto.BlackManagerDto;
import com.okdeer.mall.chargesetting.entity.RiskBlack;
import com.okdeer.mall.chargesetting.service.IBlackListApi;
import com.okdeer.mall.chargesetting.service.IBlackListService;
import com.okdeer.mall.chargesetting.service.IWhiteListService;


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
	 * @see com.okdeer.mall.chargesetting.service.IBlackListApi#deleteBlackById(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteBlackById(String accountId, String id) throws Exception {
		blackListService.delete(accountId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.chargesetting.service.IBlackListApi#selectBlackByAccount(java.lang.String)
	 */
	@Override
	public int selectBlackByAccount(String account) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.chargesetting.service.IBlackListApi#findBlackList(com.okdeer.mall.chargesetting.dto.BlackManagerDto, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public PageUtils<RiskBlack> findBlackList(BlackManagerDto blackManagerDto, Integer pageNumber, Integer pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.chargesetting.service.IBlackListApi#addBlack(java.lang.String, java.lang.String)
	 */
	@Override
	public void addBlack(String account, String id) {
		// TODO Auto-generated method stub

	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.chargesetting.service.IBlackListApi#addBlackBatch(java.lang.String, java.lang.String)
	 */
	@Override
	public void addBlackBatch(String telephoneAccount, String id) {
		// TODO Auto-generated method stub

	}

}
