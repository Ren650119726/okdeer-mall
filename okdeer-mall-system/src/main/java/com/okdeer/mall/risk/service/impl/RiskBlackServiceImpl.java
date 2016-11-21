
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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(RiskBlackServiceImpl.class);
	
	/**
	 * 获取黑名单管理mapper
	 */
	@Autowired
	RiskBlackMapper riskBlackMapper;
	

	/**
	 * 获取管理名单列表
	 * @see com.okdeer.mall.risk.service.RiskBlackService#findBlackList(com.okdeer.mall.risk.dto.RiskBlackDto, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public PageUtils<RiskBlack> findBlackList(RiskBlackDto riskBlackDto, Integer pageNumber, Integer pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		LOGGER.info("query RiskBlack params:"+riskBlackDto);
		List<RiskBlack> result = riskBlackMapper.findBlackList(riskBlackDto);
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
	public void addBatch(List<RiskBlack> riskBlackList) {
		//查询是否存在黑名单数据 存在直接更新 不存在批量增加
		List<RiskBlack>  riskList = new ArrayList<RiskBlack>();
		for(RiskBlack riskBlack:riskBlackList){
			int count = riskBlackMapper.findCountByAccount(riskBlack);
			if(count>0){
				riskBlackMapper.update(riskBlack);
			}else{
				riskList.add(riskBlack);
			}
		}
	riskBlackMapper.addBatch(riskList);
	
	}
	
	@Override
	public List<RiskBlack> findBlackListByParams(Map<String,Object> map){
		return riskBlackMapper.findBlackListByParams(map);
	}


	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.RiskBlackService#deleteBatchByIds(java.util.List, java.lang.String, java.util.Date)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteBatchByIds(List<String> ids, String updateUserId, Date updateTime) {
		riskBlackMapper.deleteBatchByIds(ids,updateUserId,updateTime);
		
	}
}
