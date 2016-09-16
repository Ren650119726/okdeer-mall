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
import com.okdeer.mall.system.entity.SysUserInvitationRecord;
import com.okdeer.mall.system.mapper.SysUserInvitationRecordMapper;
import com.okdeer.mall.system.service.InvitationRecordWriteServiceApi;
import com.yschome.base.common.utils.UuidUtils;

/**
 * ClassName: InvitationRecordWriteServiceImpl 
 * @Description: TODO
 * @author wangf01
 * @date 2016年9月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.system.service.InvitationRecordWriteServiceApi")
@Transactional(rollbackFor = Exception.class)
public class InvitationRecordWriteServiceImpl implements InvitationRecordWriteServiceApi {

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.system.service.InvitationRecordWriteServiceApi#save(com.okdeer.mall.system.entity.SysUserInvitationRecord)
	 */
	@Override
	public int save(SysUserInvitationRecord entity) throws Exception {
		Date date = new Date();
		entity.setId(UuidUtils.getUuid());
		entity.setCreateTime(date);
		entity.setUpdateTime(date);
		return sysUserInvitationRecordMapper.save(entity);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.system.service.InvitationRecordWriteServiceApi#update(com.okdeer.mall.system.entity.SysUserInvitationRecord)
	 */
	@Override
	public int update(SysUserInvitationRecord entity) throws Exception {
		Date date = new Date();
		entity.setUpdateTime(date);
		return sysUserInvitationRecordMapper.update(entity);
	}

	/**
	 * 引用SysUserInvitationRecordMapper
	 */
	@Resource
	private SysUserInvitationRecordMapper sysUserInvitationRecordMapper;
}
