/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2016年11月15日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.api.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.risk.dto.RiskUserManagerDto;
import com.okdeer.mall.risk.entity.RiskUserManager;
import com.okdeer.mall.risk.service.RiskUserManagerApi;
import com.okdeer.mall.risk.service.RiskUserManagerService;


/**
 * ClassName: RiskUserManagerApiImpl 
 * @Description: 风控人员管理api实现类
 * @author xuzq01
 * @date 2016年11月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2			2016年11月15日		xuzq01			风控人员管理api实现类
 */
@Service(version="1.0.0")
public class RiskUserManagerApiImpl implements RiskUserManagerApi {

	@Autowired 
	RiskUserManagerService riskManagerService;
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.RiskUserManagerApi#findUserList(com.okdeer.mall.risk.dto.RiskUserManagerDto, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public PageUtils<RiskUserManager> findUserList(RiskUserManagerDto userManagerDto, Integer pageNumber,
			Integer pageSize) {
		return riskManagerService.findUserList(userManagerDto,pageNumber,pageSize);
	}

	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see com.okdeer.mall.risk.service.RiskUserManagerApi#addUser(java.lang.String, java.lang.String)
	 */
	@Override
	public void addUser(RiskUserManager riskUserManager, String createUserId) throws Exception {
		String userId = UuidUtils.getUuid();
		Date date = new Date();
		riskUserManager.setId(userId);
		riskUserManager.setCreateUserId(createUserId);
		riskUserManager.setUpdateUserId(createUserId);
		riskUserManager.setCreateTime(date);
		riskUserManager.setUpdateTime(date);
		riskUserManager.setDisabled(0);
		riskManagerService.add(riskUserManager);

	}

	/**
	 * 根据id逻辑删除风控人员
	 * @throws Exception 
	 * @see com.okdeer.mall.risk.service.RiskUserManagerApi#deleteBatchByIds(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteBatchByIds(String userIds, String updateUserId) throws Exception {
		Date updateTime = new Date();
		List<String> ids = (List<String>) java.util.Arrays.asList(userIds.split(","));
		
		riskManagerService.deleteBatchByIds(ids,updateUserId,updateTime);

	}

	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see com.okdeer.mall.risk.service.RiskUserManagerApi#findUserById(java.lang.String)
	 */
	@Override
	public RiskUserManager findUserById(String id) throws Exception {
		
		return riskManagerService.findById(id);
	}

	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see com.okdeer.mall.risk.service.RiskUserManagerApi#updateUser(com.okdeer.mall.risk.dto.RiskUserManagerDto, java.lang.String)
	 */
	@Override
	public void updateUser(RiskUserManager riskUserManager, String updateUserId) throws Exception {
		Date date = new Date();
		riskUserManager.setUpdateUserId(updateUserId);
		riskUserManager.setUpdateTime(date);
		riskManagerService.update(riskUserManager);
	}

	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see com.okdeer.mall.risk.service.RiskUserManagerApi#deleteUserById(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteUserById(String userId, String id) throws Exception {
		riskManagerService.delete(userId);
		
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.RiskUserManagerApi#findCountByTelephoneOrEmail(com.okdeer.mall.risk.entity.RiskUserManager)
	 */
	@Override
	public int findCountByTelephoneOrEmail(RiskUserManager riskUserManager) {
		return riskManagerService.findCountByTelephoneOrEmail(riskUserManager);
	}

}
