/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2016年11月15日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.api.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.risk.dto.ActiveRecorderDto;
import com.okdeer.mall.risk.service.IActiveRecorderApi;
import com.okdeer.mall.risk.service.IActiveRecorderService;


/**
 * ClassName: ActiveRecorderApiImpl 
 * @Description: 拦截记录api实现类
 * @author xuzq01
 * @date 2016年11月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2			2016年11月15日		xuzq01				拦截记录api实现类
 */
@Service(version="1.0.0")
public class ActiveRecorderApiImpl implements IActiveRecorderApi {

	@Autowired 
	IActiveRecorderService activeRecorderService;
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.IActiveRecorderApi#findUserList(com.okdeer.mall.risk.dto.ActiveRecorderDto, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public PageUtils<ActiveRecorderDto> findUserList(ActiveRecorderDto activeRecorderDto, Integer pageNumber,
			Integer pageSize) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.IActiveRecorderApi#deleteRecorderByIds(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteRecorderByIds(String accountId, String id) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.risk.service.IActiveRecorderApi#addRecorderToBlack(java.lang.String, java.lang.String)
	 */
	@Override
	public void addRecorderToBlack(String account, String id) {
		// TODO Auto-generated method stub
		
	}

}
