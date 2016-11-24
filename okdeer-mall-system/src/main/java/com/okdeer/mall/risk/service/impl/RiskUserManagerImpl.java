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
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.risk.dto.RiskUserManagerDto;
import com.okdeer.mall.risk.entity.RiskUserManager;
import com.okdeer.mall.risk.mapper.RiskUserManagerMapper;
import com.okdeer.mall.risk.service.RiskUserManagerService;

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
public class RiskUserManagerImpl extends BaseServiceImpl implements RiskUserManagerService{
	
	//private static final Logger LOGGER = Logger.getLogger(RiskUserManagerImpl.class);
	
	/**
	 * 风控管理mapper
	 */
	@Autowired
	RiskUserManagerMapper riskUserManagerMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return riskUserManagerMapper;
	}
	
	@Override
	public List<RiskUserManager> findUserList(RiskUserManagerDto userManagerDto) {
		return riskUserManagerMapper.findUserList(userManagerDto);
	}

	/**
	 * 获取风控人员list
	 * @see com.okdeer.mall.risk.service.RiskUserManagerService#findUserList(com.okdeer.mall.risk.dto.RiskUserManagerDto, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public PageUtils<RiskUserManager> findUserList(RiskUserManagerDto userManagerDto, Integer pageNumber,
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
	 * @see com.okdeer.mall.risk.service.RiskUserManagerService#deleteBatchByIds(java.util.List, java.lang.String, java.util.Date)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteBatchByIds(List<String> ids, String updateUserId, Date updateTime) {
		riskUserManagerMapper.deleteBatchByIds(ids,updateUserId,updateTime);
		
	}

}
