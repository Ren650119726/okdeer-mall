
/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2016年11月4日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.risk.dto.RiskBlackDto;
import com.okdeer.mall.risk.entity.RiskBlack;
import com.okdeer.mall.risk.mapper.RiskBlackMapper;
import com.okdeer.mall.risk.service.RiskBlackService;

/**
 * ClassName: RiskBlackApi 
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
public class RiskBlackServiceImpl extends BaseServiceImpl implements RiskBlackService{

	//private static final Logger LOGGER = Logger.getLogger(RiskWhiteServiceImpl.class);
	
	/**
	 * 获取皮肤mapper
	 */
	@Autowired
	RiskBlackMapper riskBlackMapper;
	

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.RiskBlackService#findBlackList(com.okdeer.mall.risk.dto.RiskBlackDto, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public PageUtils<RiskBlack> findBlackList(RiskBlackDto blackManagerDto, Integer pageNumber, Integer pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<RiskBlack> result = riskBlackMapper.findBlackList(blackManagerDto);
		if (result == null) {
			result = new ArrayList<RiskBlack>();
		}
		return new PageUtils<RiskBlack>(result);
		
	}


	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.BaseServiceImpl#getBaseMapper()
	 */
	@Override
	public IBaseMapper getBaseMapper() {
		return riskBlackMapper;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.RiskBlackService#addBath(java.util.List)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addBath(List<RiskBlack> riskBlackList) {
		riskBlackMapper.addBatch(riskBlackList);
		
	}
	
	@Override
	public List<RiskBlack> findBlackListByParams(Map<String,Object> map){
		return riskBlackMapper.findBlackListByParams(map);
	}
}
