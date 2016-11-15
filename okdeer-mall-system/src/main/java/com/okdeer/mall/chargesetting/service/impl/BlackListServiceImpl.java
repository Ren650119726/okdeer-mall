
/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2016年11月4日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.chargesetting.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.chargesetting.dto.BlackManagerDto;
import com.okdeer.mall.chargesetting.entity.RiskBlack;
import com.okdeer.mall.chargesetting.mapper.RiskBlackMapper;
import com.okdeer.mall.chargesetting.service.IBlackListService;

/**
 * ClassName: IBlackListApi 
 * @Description: 黑名单管理service实现类
 * @author xuzq01
 * @date 2016年11月4日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2			2016年11月4日			xuzq01				白名单管理service
 */
@Service
public class BlackListServiceImpl extends BaseServiceImpl implements IBlackListService{

	//private static final Logger LOGGER = Logger.getLogger(WhiteListServiceImpl.class);
	
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
		return riskBlackMapper.add(entity);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.IBaseService#update(java.lang.Object)
	 */
	@Override
	public <Entity> int update(Entity entity) throws Exception {
		return riskBlackMapper.update(entity);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.IBaseService#delete(java.lang.String)
	 */
	@Override
	public int delete(String id) throws Exception {
		return riskBlackMapper.delete(id);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.IBaseService#findById(java.lang.String)
	 */
	@Override
	public <Entity> Entity findById(String id) throws Exception {
		return riskBlackMapper.findById(id);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.chargesetting.service.IBlackListService#findBlackList(com.okdeer.mall.chargesetting.dto.BlackManagerDto, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public PageUtils<RiskBlack> findBlackList(BlackManagerDto blackManagerDto, Integer pageNumber, Integer pageSize) {
		return riskBlackMapper.findBlackList(blackManagerDto);
	}


	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.BaseServiceImpl#getBaseMapper()
	 */
	@Override
	public IBaseMapper getBaseMapper() {
		return riskBlackMapper;
	}
}
