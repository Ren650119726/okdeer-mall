/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2016年11月4日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.risk.mapper.RiskBlackMapper;
import com.okdeer.mall.risk.service.IActiveRecorderService;

/**
 * ClassName: ISkinManagerServiceApi 
 * @Description: 拦截记录service实现类
 * @author xuzq01
 * @date 2016年11月4日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2			2016年11月15日		xuzq01				拦截记录service实现类
 */
@Service
public class ActiveRecorderImpl extends BaseServiceImpl implements IActiveRecorderService{

	private static final Logger LOGGER = Logger.getLogger(ActiveRecorderImpl.class);
	
	/**
	 * 获取皮肤mapper
	 */
	@Autowired
	RiskBlackMapper riskBlackMapper;
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.IBaseService#add(java.lang.Object)
	 */
	@Override
	public <Entity> int add(Entity entity) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.IBaseService#update(java.lang.Object)
	 */
	@Override
	public <Entity> int update(Entity entity) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.IBaseService#delete(java.lang.String)
	 */
	@Override
	public int delete(String id) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.IBaseService#findById(java.lang.String)
	 */
	@Override
	public <Entity> Entity findById(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.BaseServiceImpl#getBaseMapper()
	 */
	@Override
	public IBaseMapper getBaseMapper() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
