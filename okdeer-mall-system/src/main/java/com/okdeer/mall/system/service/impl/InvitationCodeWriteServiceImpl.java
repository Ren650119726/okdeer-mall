/** 
 *@Project: okdeer-mall-system 
 *@Author: wangf01
 *@Date: 2016年9月12日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */

package com.okdeer.mall.system.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.system.entity.SysUserInvitationCode;
import com.okdeer.mall.system.mapper.SysUserInvitationCodeMapper;
import com.okdeer.mall.system.service.InvitationCodeWriteServiceApi;
import com.yschome.base.common.utils.UuidUtils;

/**
 * ClassName: InvitationCodeWriteServiceImpl 
 * @Description: TODO
 * @author wangf01
 * @date 2016年9月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.system.service.InvitationCodeWriteServiceApi")
@Transactional(rollbackFor = Exception.class)
public class InvitationCodeWriteServiceImpl implements InvitationCodeWriteServiceApi {

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.system.service.InvitationCodeWriteServiceApi#save(com.okdeer.mall.system.entity.SysUserInvitationCode)
	 */
	@Override
	public int save(SysUserInvitationCode entity) throws Exception {
		Date date = new Date();
		entity.setId(UuidUtils.getUuid());
		entity.setInvitationUserNum(0);
		entity.setFirstOrderUserNum(0);
		entity.setCreateTime(date);
		entity.setUpdateTime(date);
		return sysUserInvitationCodeMapper.save(entity);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.system.service.InvitationCodeWriteServiceApi#update(com.okdeer.mall.system.entity.SysUserInvitationCode)
	 */
	@Override
	public int update(SysUserInvitationCode entity) throws Exception {
		Date date = new Date();
		entity.setUpdateTime(date);
		return sysUserInvitationCodeMapper.update(entity);
	}

	/**
	 * 引用SysUserInvitationCodeMapper
	 */
	@Resource
	private SysUserInvitationCodeMapper sysUserInvitationCodeMapper;

}
