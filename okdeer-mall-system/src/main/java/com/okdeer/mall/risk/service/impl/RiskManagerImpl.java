/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2016年11月4日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.risk.dto.UserManagerDto;
import com.okdeer.mall.risk.entity.RiskUserManager;
import com.okdeer.mall.risk.mapper.RiskUserManagerMapper;
import com.okdeer.mall.risk.service.IRiskManagerService;

/**
 * ClassName: ISkinManagerServiceApi 
 * @Description: 风控管理人员service实现类
 * @author xuzq01
 * @date 2016年11月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2			2016年11月15日		xuzq01				风控管理人员service实现类
 */
@Service
public class RiskManagerImpl extends BaseServiceImpl implements IRiskManagerService{
	
	//private static final Logger LOGGER = Logger.getLogger(RiskManagerImpl.class);
	
	/**
	 * 风控管理mapper
	 */
	@Autowired
	RiskUserManagerMapper riskUserManagerMapper;
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.IBaseService#add(java.lang.Object)
	 */
	@Override
	public <Entity> int add(Entity entity) throws Exception {
		return riskUserManagerMapper.add(entity);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.IBaseService#update(java.lang.Object)
	 */
	@Override
	public <Entity> int update(Entity entity) throws Exception {
		return riskUserManagerMapper.update(entity);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.IBaseService#delete(java.lang.String)
	 */
	@Override
	public int delete(String id) throws Exception {
		return riskUserManagerMapper.delete(id);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.IBaseService#findById(java.lang.String)
	 */
	@Override
	public <Entity> Entity findById(String id) throws Exception {
		return riskUserManagerMapper.findById(id);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.BaseServiceImpl#getBaseMapper()
	 */
	@Override
	public IBaseMapper getBaseMapper() {
		return riskUserManagerMapper;
	}

	/**
	 * 获取风控人员list
	 * @see com.okdeer.mall.risk.service.IRiskManagerService#findUserList(com.okdeer.mall.risk.dto.UserManagerDto, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public PageUtils<RiskUserManager> findUserList(UserManagerDto userManagerDto, Integer pageNumber,
			Integer pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<RiskUserManager> result = riskUserManagerMapper.findUserList(userManagerDto);
		if (result == null) {
			result = new ArrayList<RiskUserManager>();
		}
		return new PageUtils<RiskUserManager>(result);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.IRiskManagerService#deleteBatchByIds(java.util.List, java.lang.String, java.util.Date)
	 */
	@Override
	public void deleteBatchByIds(List<String> ids, String updateUserId, Date updateTime) {
		riskUserManagerMapper.deleteBatchByIds(ids,updateUserId,updateTime);
		
	}
	
}
