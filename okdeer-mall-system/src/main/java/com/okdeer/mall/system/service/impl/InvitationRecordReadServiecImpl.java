/** 
 *@Project: okdeer-mall-system 
 *@Author: wangf01
 *@Date: 2016年9月10日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */

package com.okdeer.mall.system.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.mall.system.entity.SysUserInvitationRecordVo;
import com.okdeer.mall.system.mapper.SysBuyerUserMapper;
import com.okdeer.mall.system.mapper.SysUserInvitationRecordMapper;
import com.okdeer.mall.system.service.InvitationRecordReadServiceApi;
import com.yschome.base.common.utils.PageUtils;

/**
 * ClassName: SysUserInvitationRecordServiecImpl 
 * @Description: 邀请码记录实现类
 * @author wangf01
 * @date 2016年9月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.system.service.ISysUserInvitationRecordServiceApi")
@Transactional(readOnly = true)
public class InvitationRecordReadServiecImpl implements InvitationRecordReadServiceApi {

	@SuppressWarnings("rawtypes")
	@Override
	public PageUtils<SysUserInvitationRecordVo> findByParam(Map param, Integer pageNumber, Integer pageSize)
			throws Exception {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		// 根据参数查询符合的邀请码记录
		List<SysUserInvitationRecordVo> list = sysUserInvitationRecordMapper.findByParam(param);
		PageUtils<SysUserInvitationRecordVo> page = new PageUtils<SysUserInvitationRecordVo>(list);
		return page;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<SysUserInvitationRecordVo> findByParam(Map param) throws Exception {
		// 根据参数查询符合的邀请码记录
		List<SysUserInvitationRecordVo> list = sysUserInvitationRecordMapper.findByParam(param);
		return list;
	}

	/**
	 * 引用SysUserInvitationRecordMapper
	 */
	@Resource
	private SysUserInvitationRecordMapper sysUserInvitationRecordMapper;

	/**
	 * 引用SysBuyerUserMapper
	 */
	@Resource
	private SysBuyerUserMapper sysBuyerUserMapper;
}
