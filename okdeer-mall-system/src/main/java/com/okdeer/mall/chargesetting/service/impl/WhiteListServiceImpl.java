/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2016年11月4日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.chargesetting.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.chargesetting.dto.WhiteManagerDto;
import com.okdeer.mall.chargesetting.entity.RiskWhite;
import com.okdeer.mall.chargesetting.mapper.RiskWhiteMapper;
import com.okdeer.mall.chargesetting.service.IWhiteListService;
import com.okdeer.mall.operate.dto.SkinManagerDto;

/**
 * ClassName: ISkinManagerServiceApi 
 * @Description: TODO
 * @author xuzq01
 * @date 2016年11月4日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class WhiteListServiceImpl extends BaseServiceImpl implements IWhiteListService{
	
	private static final Logger LOGGER = Logger.getLogger(WhiteListServiceImpl.class);
	
	/**
	 * 获取皮肤mapper
	 */
	@Autowired
	RiskWhiteMapper riskWhiteMapper;
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.chargesetting.service.IWhiteListService#findWhiteList(com.okdeer.mall.chargesetting.dto.WhiteManagerDto, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public PageUtils<RiskWhite> findWhiteList(WhiteManagerDto whiteManagerDto, Integer pageNumber,
			Integer pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<RiskWhite> result = riskWhiteMapper.findWhiteList(whiteManagerDto);
		if (result == null) {
			result = new ArrayList<RiskWhite>();
		}
		return new PageUtils<RiskWhite>(result);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.chargesetting.service.IWhiteListService#selectWhiteByAccount(com.okdeer.mall.chargesetting.dto.WhiteManagerDto)
	 */
	@Override
	public int selectWhiteByAccount(String account) {
		return riskWhiteMapper.selectWhiteByAccount(account);
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
