/** 
 *@Project: okdeer-mall-system 
 *@Author: yangq
 *@Date: 2016年10月4日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */

package com.okdeer.mall.system.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.system.entity.SysUserInvitationRecordVo;
import com.okdeer.mall.system.mapper.SysUserInvitationRecordMapper;
import com.okdeer.mall.system.service.SysUserInvitationRecordApi;

/**
 * ClassName: SysUserInvitationRecordServiceImpl 
 * @Description: 
 * @author yangq
 * @date 2016年10月4日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.system.service.SysUserInvitationRecordApi")
public class SysUserInvitationRecordServiceImpl implements SysUserInvitationRecordApi {

	@Resource
	private SysUserInvitationRecordMapper sysUserInvitationRecordMapper;

	@Override
	public List<SysUserInvitationRecordVo> selectSysUserInvitationRecordList(String invitationCodeId) throws Exception {

		List<SysUserInvitationRecordVo> invitationRecord = new ArrayList<SysUserInvitationRecordVo>();
		invitationRecord = sysUserInvitationRecordMapper.selectInvitationFirstOrderById(invitationCodeId);

		return invitationRecord;
	}

}
