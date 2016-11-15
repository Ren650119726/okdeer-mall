/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2016年11月14日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.chargesetting.api.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.chargesetting.dto.WhiteManagerDto;
import com.okdeer.mall.chargesetting.entity.RiskWhite;
import com.okdeer.mall.chargesetting.service.IWhiteListApi;
import com.okdeer.mall.chargesetting.service.IWhiteListService;


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
	 * @see com.okdeer.mall.chargesetting.service.IWhiteListApi#findWhiteList(com.okdeer.mall.chargesetting.dto.WhiteManagerDto, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public PageUtils<RiskWhite> findWhiteList(WhiteManagerDto whiteManagerDto, Integer pageNumber,
			Integer pageSize) {
		
		return whiteListService.findWhiteList(whiteManagerDto, pageNumber, pageSize);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.chargesetting.service.IWhiteListApi#delete(java.lang.String, java.lang.String)
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
	 * @see com.okdeer.mall.chargesetting.service.IWhiteListApi#add(java.lang.String, java.lang.String)
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
	 * @see com.okdeer.mall.chargesetting.service.IWhiteListApi#selectWhiteByAccount(java.lang.String)
	 */
	@Override
	public int selectWhiteByAccount(String account) {
		return whiteListService.selectWhiteByAccount(account);
	}

}
