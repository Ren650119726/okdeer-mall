package com.okdeer.mall.system.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.mall.system.entity.SysUserInvitationCode;
import com.okdeer.mall.system.entity.SysUserInvitationCodeVo;
import com.okdeer.mall.system.entity.SysUserInvitationRecord;
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
public class InvitationCodeServiceImpl implements InvitationCodeServiceApi,InvitationCodeService {

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
		Date beginTime = invitationCodeVo.getBeginTime();
		Date endTime = invitationCodeVo.getEndTime();
		if (invitationCodeVo.getIds() != null && invitationCodeVo.getIds().length <= 0) {
			invitationCodeVo.setIds(null);
		}
		if (beginTime == null && endTime != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(endTime);
			//三个月前
			cal.add(Calendar.DAY_OF_MONTH, -3);
			beginTime = cal.getTime();
		}
		if (beginTime != null) {
			//页面搜索时间框传来的时间
			Date sta = DateUtils.getDateStart(beginTime);
			invitationCodeVo.setBeginTime(sta);
		} 
		if (endTime != null) {
			Date end = DateUtils.getDateEnd(endTime);
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
		Date beginTime = invitationCodeVo.getBeginTime();
		Date endTime = invitationCodeVo.getEndTime();
		if (invitationCodeVo.getIds() != null && invitationCodeVo.getIds().length <= 0) {
			invitationCodeVo.setIds(null);
		}
		if (beginTime == null && endTime != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(endTime);
			//三个月前
			cal.add(Calendar.DAY_OF_MONTH, -3);
			beginTime = cal.getTime();
		}
		if (beginTime != null) {
			//页面搜索时间框传来的时间
			Date sta = DateUtils.getDateStart(beginTime);
			invitationCodeVo.setBeginTime(sta);
		} 
		if (endTime != null) {
			Date end = DateUtils.getDateEnd(endTime);
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
		if (sysUserInvitationRecordVo.getIdsRecord() != null && sysUserInvitationRecordVo.getIdsRecord().length <= 0) {
			sysUserInvitationRecordVo.setIdsRecord(null);
		}
		Date beginTimeRecord = sysUserInvitationRecordVo.getBeginTimeRecord();
		Date endTimeRecord = sysUserInvitationRecordVo.getEndTimeRecord();
		if (beginTimeRecord == null && endTimeRecord != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(endTimeRecord);
			//三个月前
			cal.add(Calendar.DAY_OF_MONTH, -3);
			beginTimeRecord = cal.getTime();
		}
		if (beginTimeRecord != null) {
			//页面搜索时间框传来的时间
			Date sta = DateUtils.getDateStart(beginTimeRecord);
			sysUserInvitationRecordVo.setBeginTimeRecord(sta);
		} 
		if (endTimeRecord != null) {
			Date end = DateUtils.getDateEnd(endTimeRecord);
			sysUserInvitationRecordVo.setEndTimeRecord(end);
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
		if (invitationRecordVo.getIdsRecord() != null && invitationRecordVo.getIdsRecord().length <= 0) {
			invitationRecordVo.setIdsRecord(null);
		}
		Date beginTimeRecord = invitationRecordVo.getBeginTimeRecord();
		Date endTimeRecord = invitationRecordVo.getEndTimeRecord();
		if (beginTimeRecord == null && endTimeRecord != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(endTimeRecord);
			//三个月前
			cal.add(Calendar.DAY_OF_MONTH, -3);
			beginTimeRecord = cal.getTime();
		}
		if (beginTimeRecord != null) {
			//页面搜索时间框传来的时间
			Date sta = DateUtils.getDateStart(beginTimeRecord);
			invitationRecordVo.setBeginTimeRecord(sta);
		} 
		if (endTimeRecord != null) {
			Date end = DateUtils.getDateEnd(endTimeRecord);
			invitationRecordVo.setEndTimeRecord(end);
		}
		List<SysUserInvitationRecordVo> list = sysUserInvitationRecordMapper.findByQueryRecordVo(invitationRecordVo);
		return list;
	}

	@Override
	public void saveCode(SysUserInvitationCode sysUserInvitationCode) throws ServiceException {
		sysUserInvitationCodeMapper.saveCode(sysUserInvitationCode);
		
	}

	@Override
	public void updateCode(SysUserInvitationCode sysUserInvitationCode) throws ServiceException {
		sysUserInvitationCodeMapper.updateCode(sysUserInvitationCode);
		
	}

	@Override
	public void saveCodeRecord(SysUserInvitationRecord sysUserInvitationRecord) throws ServiceException {
		sysUserInvitationRecordMapper.saveCodeRecord(sysUserInvitationRecord);
		
	}

	@Override
	public void updateCodeRecord(SysUserInvitationRecord sysUserInvitationRecord) throws ServiceException {
		sysUserInvitationRecordMapper.updateCodeRecord(sysUserInvitationRecord);
		
	}

	@Override
	public SysUserInvitationCode findInvitationCode(String invitationCode) {
		SysUserInvitationCode  sysUser = sysUserInvitationCodeMapper.selectInvitationByCode(invitationCode);
		
		return sysUser;
	}

	@Override
	public SysUserInvitationCode findInvitationById(String sysBuyerUserId) {
		SysUserInvitationCode sysUser = sysUserInvitationCodeMapper.selectInvitationById(sysBuyerUserId);
		return sysUser;
	}

	@Override
	public void insertInvitationRecord(SysUserInvitationRecord sysUserInvitationRecord) throws Exception {
		sysUserInvitationRecordMapper.saveCodeRecord(sysUserInvitationRecord);
	}

}
