/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2016年11月15日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.chargesetting.api.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.chargesetting.dto.UserManagerDto;
import com.okdeer.mall.chargesetting.entity.RiskUserManager;
import com.okdeer.mall.chargesetting.entity.RiskWhite;
import com.okdeer.mall.chargesetting.service.IBlackListService;
import com.okdeer.mall.chargesetting.service.IRiskManagerApi;
import com.okdeer.mall.chargesetting.service.IRiskManagerService;
import com.okdeer.mall.operate.entity.SkinManager;


/**
 * ClassName: RiskManagerApiImpl 
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
public class RiskManagerApiImpl implements IRiskManagerApi {

	@Autowired 
	IRiskManagerService riskManagerService;
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.chargesetting.service.IRiskManagerApi#findUserList(com.okdeer.mall.chargesetting.dto.UserManagerDto, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public PageUtils<RiskUserManager> findUserList(UserManagerDto userManagerDto, Integer pageNumber,
			Integer pageSize) {
		return riskManagerService.findUserList(userManagerDto,pageNumber,pageSize);
	}

	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see com.okdeer.mall.chargesetting.service.IRiskManagerApi#addUser(java.lang.String, java.lang.String)
	 */
	@Override
	public void addUser(UserManagerDto userManagerDto, String createUserId) throws Exception {
		String userId = UuidUtils.getUuid();
		Date date = new Date();
		userManagerDto.setId(userId);
		userManagerDto.setCreateUserId(createUserId);
		userManagerDto.setUpdateUserId(createUserId);
		userManagerDto.setCreateTime(date);
		userManagerDto.setUpdateTime(date);
		userManagerDto.setDisabled(0);
		RiskUserManager riskUserManager= BeanMapper.map(userManagerDto, RiskUserManager.class);
		riskManagerService.add(riskUserManager);

	}

	/**
	 * 根据id逻辑删除风控人员
	 * @throws Exception 
	 * @see com.okdeer.mall.chargesetting.service.IRiskManagerApi#deleteUserById(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteUserById(String userId, String id) throws Exception {
		riskManagerService.delete(userId);

	}

	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see com.okdeer.mall.chargesetting.service.IRiskManagerApi#findUserById(java.lang.String)
	 */
	@Override
	public RiskUserManager findUserById(String id) throws Exception {
		
		return riskManagerService.findById(id);
	}

	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see com.okdeer.mall.chargesetting.service.IRiskManagerApi#updateUser(com.okdeer.mall.chargesetting.dto.UserManagerDto, java.lang.String)
	 */
	@Override
	public void updateUser(UserManagerDto userManagerDto, String updateUserId) throws Exception {
		Date date = new Date();
		userManagerDto.setUpdateUserId(updateUserId);
		userManagerDto.setUpdateTime(date);
		RiskUserManager riskUserManager= BeanMapper.map(userManagerDto, RiskUserManager.class);
		riskManagerService.update(riskUserManager);
	}

}
