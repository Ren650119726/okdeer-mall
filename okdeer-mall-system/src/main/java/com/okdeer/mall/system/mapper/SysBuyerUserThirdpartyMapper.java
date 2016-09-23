package com.okdeer.mall.system.mapper;

import com.okdeer.archive.system.entity.SysBuyerUserThirdparty;
import com.okdeer.base.dal.IBaseCrudMapper;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-03-17 11:04:03
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface SysBuyerUserThirdpartyMapper extends IBaseCrudMapper {
	/**
	 * DESC: 根据openId删除三方平台与自平台账号关系
	 * @author LIU.W
	 * @param buyerUserThirdparty
	 * @return
	 */
	public int deleteByOpenId(SysBuyerUserThirdparty buyerUserThirdparty);
}