/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2016年11月15日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.api.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.risk.dto.RiskBlackDto;
import com.okdeer.mall.risk.entity.RiskBlack;
import com.okdeer.mall.risk.service.RiskBlackApi;
import com.okdeer.mall.risk.service.RiskBlackService;


/**
 * ClassName: RiskBlackApiImpl 
 * @Description: 黑名单管理api实现类
 * @author xuzq01
 * @date 2016年11月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2			2016年11月15日		xuzq01				黑名单管理api实现类
 */
@Service(version="1.0.0")
public class RiskBlackApiImpl implements RiskBlackApi {

	@Autowired 
	RiskBlackService blackListService;
	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see com.okdeer.mall.risk.service.RiskBlackApi#deleteBlackById(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteBlackById(String accountId, String id) throws Exception {
		blackListService.delete(accountId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.RiskBlackApi#findBlackList(com.okdeer.mall.risk.dto.RiskBlackDto, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public PageUtils<RiskBlack> findBlackList(RiskBlackDto blackManagerDto, Integer pageNumber, Integer pageSize) {
		return blackListService.findBlackList(blackManagerDto, pageNumber, pageSize);
	}

	/**
	 * (non-Javadoc)
	 * @throws Exception 
	 * @see com.okdeer.mall.risk.service.RiskBlackApi#addBlack(java.lang.String, java.lang.String)
	 */
	@Override
	public void addBlack(RiskBlack riskBlack,String userId) throws Exception {
		String blackId = UuidUtils.getUuid();
		Date date = new Date();
		riskBlack.setId(blackId);
		riskBlack.setCreateUserId(userId);
		riskBlack.setUpdateUserId(userId);
		riskBlack.setCreateTime(date);
		riskBlack.setUpdateTime(date);
		riskBlack.setDisabled(0);
		blackListService.add(riskBlack);

	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.RiskBlackApi#addBlackBatch(java.lang.String, java.lang.String)
	 */
	@Override
	public void addBlackBatch(RiskBlackDto blackManagerDto, String userId) {
		
		List<RiskBlack> riskBlackList = new ArrayList<RiskBlack>();
		List<String> accountNameList = (List<String>) java.util.Arrays.asList(blackManagerDto.getAccount().split(","));
		for(String account:accountNameList){
			RiskBlack riskBlack = new RiskBlack();
			Date date = new Date();
			riskBlack.setId(UuidUtils.getUuid());
			riskBlack.setAccount(account);
			riskBlack.setAccountType(blackManagerDto.getAccountType());
			riskBlack.setDisabled(0);
			riskBlack.setCreateTime(date);
			riskBlack.setUpdateTime(date);
			riskBlack.setCreateUserId(userId);
			riskBlack.setUpdateUserId(userId);
			riskBlackList.add(riskBlack);
		}
		blackListService.addBath(riskBlackList);
	}
	
	@Override
	public void addBlackBatch(List<RiskBlackDto> blackDtoList, String userId){
		
		List<RiskBlack> riskBlackList = new ArrayList<RiskBlack>();
		for(RiskBlackDto dto : blackDtoList){
			RiskBlack riskBlack = new RiskBlack();
			Date date = new Date();
			riskBlack.setId(UuidUtils.getUuid());
			riskBlack.setAccount(dto.getAccount());
			riskBlack.setAccountType(dto.getAccountType());
			riskBlack.setDisabled(0);
			riskBlack.setCreateTime(date);
			riskBlack.setUpdateTime(date);
			riskBlack.setCreateUserId(userId);
			riskBlack.setUpdateUserId(userId);
			riskBlackList.add(riskBlack);
		}
		blackListService.addBath(riskBlackList);
	}
	
	@Override
	public List<RiskBlackDto> findBlackListByParams(Map<String,Object> map){
		List<RiskBlack> list = blackListService.findBlackListByParams(map);
		List<RiskBlackDto> dtoList = BeanMapper.mapList(list, RiskBlackDto.class);
		return dtoList;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.RiskBlackApi#deleteBatchByIds(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteBatchByIds(String accountIds, String updateUserId) {
		Date updateTime = new Date();
		List<String> ids = (List<String>) java.util.Arrays.asList(accountIds.split(","));; 
		
		blackListService.deleteBatchByIds(ids,updateUserId,updateTime);
		
	}

}
