package com.okdeer.mall.system.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.mall.operate.entity.ColumnOperation;
import com.okdeer.mall.system.entity.SysUserInvitationCodeVo;
import com.okdeer.mall.system.entity.SysUserInvitationRecordVo;
import com.okdeer.mall.system.mapper.SysUserInvitationCodeMapper;
import com.okdeer.mall.system.mapper.SysUserInvitationRecordMapper;
import com.okdeer.mall.system.service.InvitationCodeService;
import com.okdeer.mall.system.service.InvitationCodeServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.PageUtils;

/**
 * ClassName: InvitationCodeServiceImpl 
 * @Description: 邀请码管理impl
 * @author zhulq
 * @date 2016年9月19日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.1			 2016年9月19日 			zhulq
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.system.service.InvitationCodeServiceApi")
public class InvitationCodeServiceImpl implements InvitationCodeServiceApi,InvitationCodeService{

	/**
	 * 邀请码mapper
	 */
	@Autowired
	private SysUserInvitationCodeMapper sysUserInvitationCodeMapper; 
	
	/**
	 * 邀请记录mapper
	 */
	@Autowired
	private SysUserInvitationRecordMapper sysUserInvitationRecordMapper; 
	
	@Transactional(readOnly = true)
	@Override
	public PageUtils<SysUserInvitationCodeVo> findInvitationCodePage(SysUserInvitationCodeVo invitationCodeVo, 
			int pageNumber, int pageSize) throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true);
		if (invitationCodeVo.getIds() != null && invitationCodeVo.getIds().length <= 0) {
			invitationCodeVo.setIds(null);
		}
		if (invitationCodeVo.getBeginTime() != null) {
			//页面搜索时间框传来的时间
			Date sta = DateUtils.getDateStart(invitationCodeVo.getBeginTime());
			invitationCodeVo.setBeginTime(sta);
		} 
		if (invitationCodeVo.getEndTime() != null) {
			Date end = DateUtils.getDateEnd(invitationCodeVo.getEndTime());
			invitationCodeVo.setEndTime(end);
		}
		List<SysUserInvitationCodeVo> list = sysUserInvitationCodeMapper.findByQueryVo(invitationCodeVo);
		if (list == null) {
			list = new ArrayList<SysUserInvitationCodeVo>();
		}
		return new PageUtils<SysUserInvitationCodeVo>(list);
	}

	@Transactional(readOnly = true)
	@Override
	public List<SysUserInvitationCodeVo> findInvitationCodeForExport(SysUserInvitationCodeVo invitationCodeVo)
			throws ServiceException {
		if (invitationCodeVo.getIds() != null && invitationCodeVo.getIds().length <= 0) {
			invitationCodeVo.setIds(null);
		}
		if (invitationCodeVo.getBeginTime() != null) {
			//页面搜索时间框传来的时间
			Date sta = DateUtils.getDateStart(invitationCodeVo.getBeginTime());
			invitationCodeVo.setBeginTime(sta);
		} 
		if (invitationCodeVo.getEndTime() != null) {
			Date end = DateUtils.getDateEnd(invitationCodeVo.getEndTime());
			invitationCodeVo.setEndTime(end);
		}
		List<SysUserInvitationCodeVo> list = sysUserInvitationCodeMapper.findByQueryVo(invitationCodeVo);
		return list;
	}
	
	@Transactional(readOnly = true)
	@Override
	public PageUtils<SysUserInvitationRecordVo> findInvitationRecordPage(
			SysUserInvitationRecordVo sysUserInvitationRecordVo, int pageNumber, int pageSize) throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true);
		if (sysUserInvitationRecordVo.getIds() != null && sysUserInvitationRecordVo.getIds().length <= 0) {
			sysUserInvitationRecordVo.setIds(null);
		}
		if (sysUserInvitationRecordVo.getBeginTime() != null) {
			//页面搜索时间框传来的时间
			Date sta = DateUtils.getDateStart(sysUserInvitationRecordVo.getBeginTime());
			sysUserInvitationRecordVo.setBeginTime(sta);
		} 
		if (sysUserInvitationRecordVo.getEndTime() != null) {
			Date end = DateUtils.getDateEnd(sysUserInvitationRecordVo.getEndTime());
			sysUserInvitationRecordVo.setEndTime(end);
		}
		List<SysUserInvitationRecordVo> list = sysUserInvitationRecordMapper.findByQueryRecordVo(sysUserInvitationRecordVo);
		if (list == null) {
			list = new ArrayList<SysUserInvitationRecordVo>();
		}
		return new PageUtils<SysUserInvitationRecordVo>(list);
	}

	@Transactional(readOnly = true)
	@Override
	public List<SysUserInvitationRecordVo> findInvitationRecordForExport(SysUserInvitationRecordVo invitationRecordVo)
			throws ServiceException {
		if (invitationRecordVo.getIds() != null && invitationRecordVo.getIds().length <= 0) {
			invitationRecordVo.setIds(null);
		}
		if (invitationRecordVo.getBeginTime() != null) {
			//页面搜索时间框传来的时间
			Date sta = DateUtils.getDateStart(invitationRecordVo.getBeginTime());
			invitationRecordVo.setBeginTime(sta);
		} 
		if (invitationRecordVo.getEndTime() != null) {
			Date end = DateUtils.getDateEnd(invitationRecordVo.getEndTime());
			invitationRecordVo.setEndTime(end);
		}
		List<SysUserInvitationRecordVo> list = sysUserInvitationRecordMapper.findByQueryRecordVo(invitationRecordVo);
		return list;
	}

}
