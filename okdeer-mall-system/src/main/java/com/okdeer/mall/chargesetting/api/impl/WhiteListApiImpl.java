/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2016年11月14日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.chargesetting.api.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.chargesetting.dto.WhiteManagerDto;
import com.okdeer.mall.chargesetting.entity.RiskWhite;
import com.okdeer.mall.chargesetting.service.IWhiteListApi;
import com.okdeer.mall.chargesetting.service.IWhiteListService;


/**
 * ClassName: IWhiteListApiImpl 
 * @Description: TODO
 * @author xuzq01
 * @date 2016年11月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version="1.0.0")
public class WhiteListApiImpl implements IWhiteListApi {
	
	@Autowired
	IWhiteListService whiteListService;
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.chargesetting.service.IWhiteListApi#findWhiteList(com.okdeer.mall.chargesetting.dto.WhiteManagerDto, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public PageUtils<RiskWhite> findWhiteList(WhiteManagerDto whiteManagerDto, Integer pageNumber,
			Integer pageSize) {
		
		return whiteListService.findWhiteList(whiteManagerDto, pageNumber, pageSize);
	}
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.chargesetting.service.IWhiteListApi#add(com.okdeer.mall.chargesetting.dto.WhiteManagerDto, java.lang.String)
	 */
	@Override
	public void add(WhiteManagerDto whiteManagerDto, String userId) {
		String whiteId = UuidUtils.getUuid();
		whiteManagerDto.setId(whiteId);
		whiteManagerDto.setCreateUserId(userId);
		whiteManagerDto.setUpdateUserId(userId);
		Date date = new Date();
		whiteManagerDto.setCreateTime(date);
		whiteManagerDto.setUpdateTime(date);
		RiskWhite riskWhite= BeanMapper.map(whiteManagerDto, RiskWhite.class);
		try {
			whiteListService.add(riskWhite);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.chargesetting.service.IWhiteListApi#selectWhiteByAccount(com.okdeer.mall.chargesetting.dto.WhiteManagerDto)
	 */
	@Override
	public int selectWhiteByAccount(WhiteManagerDto whiteManagerDto) {
		
		return whiteListService.selectWhiteByAccount(whiteManagerDto);
	}
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.chargesetting.service.IWhiteListApi#delete(java.lang.String, java.lang.String)
	 */
	@Override
	public void delete(String whiteId, String userId) {
		try {
			whiteListService.delete(whiteId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.chargesetting.service.IWhiteListApi#add(java.lang.String, java.lang.String)
	 */
	@Override
	public void add(String account, String id) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.chargesetting.service.IWhiteListApi#selectWhiteByAccount(java.lang.String)
	 */
	@Override
	public int selectWhiteByAccount(String account) {
		// TODO Auto-generated method stub
		return 0;
	}

}
