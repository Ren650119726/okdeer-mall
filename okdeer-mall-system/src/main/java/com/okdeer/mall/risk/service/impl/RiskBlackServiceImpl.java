
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
import java.util.Set;

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
import com.okdeer.mall.risk.entity.RiskWhite;
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
	
	private String sync = "sync";
	/**
	 * 获取黑名单管理mapper
	 */
	@Autowired
	RiskBlackMapper riskBlackMapper;
	
	/**
	 * 黑名单充值手机号
	 */
	private Set<RiskBlack> blackMobiles = null;
	
	/**
	 * 黑名单设备
	 */
	private Set<RiskBlack> blackDevices = null;
	
	/**
	 * 黑名单支付账号
	 */
	private Set<RiskBlack> blackPayAccounts = null;
	
	/**
	 * 黑名单登录账号
	 */
	private Set<RiskBlack> blackLoginAccounts = null;
	
	/**
	 * 是否初始化
	 */
	private boolean isInitialize = false;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return riskBlackMapper;
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
		this.blackMobiles = riskBlackMapper.findAllBlackMobile();
		this.blackDevices = riskBlackMapper.findAllBlackDevice();
		this.blackPayAccounts = riskBlackMapper.findAllBlackPayAccount();
		this.blackLoginAccounts = riskBlackMapper.findAllBlackLoginAccount();
	}
	
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

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.RiskBlackService#findAllBlackMobile()
	 */
	@Override
	public Set<RiskBlack> findAllBlackMobile() {
		if (!isInitialize) {
			initialize();
		}
		return blackMobiles;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.RiskBlackService#findAllBlackDevice()
	 */
	@Override
	public Set<RiskBlack> findAllBlackDevice() {
		if (!isInitialize) {
			initialize();
		}
		return blackDevices;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.RiskBlackService#findAllBlackPayAccount()
	 */
	@Override
	public Set<RiskBlack> findAllBlackPayAccount() {
		if (!isInitialize) {
			initialize();
		}
		return blackPayAccounts;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.RiskBlackService#findAllBlackLoginAccount()
	 */
	@Override
	public Set<RiskBlack> findAllBlackLoginAccount() {
		if (!isInitialize) {
			initialize();
		}
		return blackLoginAccounts;
	}
}
