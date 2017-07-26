package com.okdeer.mall.system.service;

import com.okdeer.base.common.exception.ServiceException;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-03-17 11:04:03
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface SysBuyerUserThirdpartyService{
	
	/**
	 * DESC: 根据openID删除
	 * @author LIU.W
	 * @param openId
	 * @param openType
	 * @return
	 * @throws ServiceException
	 */
	public int removeByOpenId(String openId,Integer openType) 
			throws ServiceException;
	
}