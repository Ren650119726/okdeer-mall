/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2016年11月4日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.risk.dto.RiskWhiteDto;
import com.okdeer.mall.risk.entity.RiskWhite;
import com.okdeer.mall.risk.mapper.RiskWhiteMapper;
import com.okdeer.mall.risk.service.RiskWhiteService;

/**
 * ClassName: RiskWhiteServiceImpl 
 * @Description: 白名单service实现类
 * @author xuzq01
 * @date 2016年11月4日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     V1.2			  2016年11月4日		 xuzq01			     白名单service实现类
 */
@Service
public class RiskWhiteServiceImpl extends BaseServiceImpl implements RiskWhiteService{
	
	private static final Logger LOGGER = Logger.getLogger(RiskWhiteServiceImpl.class);
	
	private String sync = "sync";
	/**
	 * 获取皮肤mapper
	 */
	@Autowired
	RiskWhiteMapper riskWhiteMapper;
	
	/**
	 * 白名单列表
	 */
	private Set<String> whites = null;
	
	/**
	 * 是否初始化
	 */
	private boolean isInitialize = false;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return riskWhiteMapper;
	}

	/**
	 * 检查初始数据或初始化
	 * @author guocp
	 * @date 2016年11月18日
	 */
	private void initialize() {
		if (!isInitialize) {
			synchronized (sync) {
				if (!isInitialize) {
					try {
						doInitialize();
						isInitialize = true;
					} catch (Exception e) {
						LOGGER.error("风控获取白名单初始设置异常", e);
					}
				}
			}
		}
	}

	public void resetSetting() {
		synchronized (sync) {
			isInitialize = false;
		}
	}
	/**
	 * 初始数据
	 * @throws Exception   
	 * @author xuzq01
	 * @date 2016年11月18日
	 */
	private void doInitialize() throws Exception {
		// 初始化设置对象
		this.whites = getBlackSet(riskWhiteMapper.findAllWhite());
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.RiskWhiteService#findWhiteList(com.okdeer.mall.risk.dto.RiskWhiteDto, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public PageUtils<RiskWhite> findWhiteList(RiskWhiteDto whiteManagerDto, Integer pageNumber,
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
	 * @see com.okdeer.mall.risk.service.RiskWhiteService#deleteBatchByIds(java.util.List, java.lang.String, java.util.Date)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteBatchByIds(List<String> ids, String updateUserId, Date updateTime) {
		riskWhiteMapper.deleteBatchByIds(ids,updateUserId,updateTime);
		resetSetting();
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.RiskWhiteService#addBatch(java.util.List)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addBatch(List<RiskWhite> riskWhiteList) {
		//查询是否存在白名单数据 存在直接更新 不存在批量增加
		List<RiskWhite>  riskList = new ArrayList<RiskWhite>();
		for(RiskWhite riskWhite:riskWhiteList){
			int count = riskWhiteMapper.findCountByAccount(riskWhite);
			if(count>0){
				riskWhiteMapper.update(riskWhite);
			}else{
				riskList.add(riskWhite);
			}
		}
		riskWhiteMapper.addBatch(riskList);	
		resetSetting();
	}
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.RiskWhiteService#findAllWhite()
	 */
	@Override
	public Set<String> findAllWhite() {
		if (!isInitialize) {
			initialize();
		}
		return whites;
	}
	
	private Set<String> getBlackSet(Set<RiskWhite> whiteSet){
		Set<String> set = new HashSet<String>();
		for(RiskWhite white : whiteSet){
			set.add(white.getTelephoneAccount());
		}
		return set;
	}
}
