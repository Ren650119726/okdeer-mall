/** 
 *@Project: okdeer-archive-system 
 *@Author: wangf01
 *@Date: 2016年9月8日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */

package com.okdeer.mall.system.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.mall.system.entity.SysUserInvitationCodeVo;
import com.okdeer.mall.system.mapper.SysBuyerUserMapper;
import com.okdeer.mall.system.mapper.SysUserInvitationCodeMapper;
import com.okdeer.mall.system.mapper.SysUserMapper;
import com.okdeer.mall.system.service.ISysUserInvitationCodeServiceApi;
import com.yschome.base.common.utils.PageUtils;

/**
 * ClassName: SysUserInvitationCodeServiceImpl 
 * @Description: 邀请码服务的实现
 * @author wangf01
 * @date 2016年9月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.system.service.ISysUserInvitationCodeServiceApi")
@Transactional(readOnly = true)
public class SysUserInvitationCodeServiceImpl implements ISysUserInvitationCodeServiceApi {

	@SuppressWarnings("rawtypes")
	@Override
	public PageUtils<SysUserInvitationCodeVo> findByParam(Map param, Integer pageNumber, Integer pageSize)
			throws Exception {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		// 根据参数查询符合的邀请码记录
		List<SysUserInvitationCodeVo> list = sysUserInvitationCodeMapper.findByParam(param);
		PageUtils<SysUserInvitationCodeVo> page = new PageUtils<SysUserInvitationCodeVo>(list);
		return page;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<SysUserInvitationCodeVo> findByParam(Map param) throws Exception {
		// 根据参数查询符合的邀请码记录
		List<SysUserInvitationCodeVo> list = sysUserInvitationCodeMapper.findByParam(param);
		return list;
	}

	/**
	 * 引用SysUserInvitationCodeMapper
	 */
	@Resource
	private SysUserInvitationCodeMapper sysUserInvitationCodeMapper;

	/**
	 * 引用SysBuyerUserMapper
	 */
	@Resource
	private SysBuyerUserMapper sysBuyerUserMapper;

	/**
	 * 引用SysUserMapper
	 */
	@Resource
	private SysUserMapper sysUserMapper;
}
