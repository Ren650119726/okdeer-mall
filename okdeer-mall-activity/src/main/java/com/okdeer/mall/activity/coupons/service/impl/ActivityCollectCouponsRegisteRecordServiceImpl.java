package com.okdeer.mall.activity.coupons.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsRegisteRecord;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsRegisteRecordVo;
import com.okdeer.mall.activity.coupons.mapper.ActivityCollectCouponsRegistRecordMapper;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsRegisteRecordService;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsRegisteRecordServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.PageUtils;

/**
 * ClassName: ActivityCollectCouponsRegisteRecordServiceImpl 
 * @Description: 邀请注册记录
 * @author zhulq
 * @date 2016年9月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.1			 2016年9月18日 			zhulq
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsRegisteRecordServiceApi")
public class ActivityCollectCouponsRegisteRecordServiceImpl 
          implements ActivityCollectCouponsRegisteRecordService, ActivityCollectCouponsRegisteRecordServiceApi {

	/**
	 * 邀请注册记录Mapper
	 */
	@Autowired
	private ActivityCollectCouponsRegistRecordMapper registeRecordMapper;
	
	@Transactional(readOnly = true)
	@Override
	public PageUtils<ActivityCollectCouponsRegisteRecordVo> findRegisteRecordPage(ActivityCollectCouponsRegisteRecordVo registeRecordVo,
			int pageNum, int pageSize) throws ServiceException {
		PageHelper.startPage(pageNum, pageSize, true);
		if (registeRecordVo.getBeginTimeQuery() != null) {
			//页面搜索时间框传来的时间
			Date sta = DateUtils.getDateStart(registeRecordVo.getBeginTimeQuery());
			registeRecordVo.setBeginTimeQuery(sta);
		} 
		if (registeRecordVo.getEndTimeQuery() != null) {
			Date end = DateUtils.getDateEnd(registeRecordVo.getEndTimeQuery());
			registeRecordVo.setEndTimeQuery(end);
		}
		List<ActivityCollectCouponsRegisteRecordVo> re = registeRecordMapper.findRegisteRecord(registeRecordVo);
		if (re == null) {
			re = new ArrayList<ActivityCollectCouponsRegisteRecordVo>();
		}
		PageUtils<ActivityCollectCouponsRegisteRecordVo> pageUtils = new PageUtils<ActivityCollectCouponsRegisteRecordVo>(re);
		return pageUtils;
	}

	@Transactional(readOnly = true)
	@Override
	public PageUtils<ActivityCollectCouponsRegisteRecordVo> findByUserId(String userQueryId, String activityId,
			int pageNum, int pageSize) throws ServiceException {
		PageHelper.startPage(pageNum, pageSize, true);
		List<ActivityCollectCouponsRegisteRecordVo> re = registeRecordMapper.findByUserId(userQueryId,activityId);
		if (re == null) {
			re = new ArrayList<ActivityCollectCouponsRegisteRecordVo>();
		}
		PageUtils<ActivityCollectCouponsRegisteRecordVo> pageUtils = new PageUtils<ActivityCollectCouponsRegisteRecordVo>(re);
		return pageUtils;
	}

	@Override
	public List<ActivityCollectCouponsRegisteRecordVo> findRegisteRecordForExport(
			ActivityCollectCouponsRegisteRecordVo registeRecordVo) throws ServiceException {
		if (registeRecordVo.getIds() != null && registeRecordVo.getIds().length <= 0) {
			registeRecordVo.setIds(null);
		}
		List<ActivityCollectCouponsRegisteRecordVo> voList = registeRecordMapper.findRegisteRecordForExport(registeRecordVo);
		return voList;
	}

	@Override
	public int selectActivityCouponsFaceValue() throws Exception {
		int result = registeRecordMapper.selectActivityCouponsFaceValue();
		return result;
	}

	@Transactional(readOnly=true)
	@Override
	public int selectInvitationNum(String userId) throws Exception {
		int result = registeRecordMapper.selectInvitationNum(userId);
		return result;
	}

	@Transactional(rollbackFor = Exception.class)
	public void saveRecord(ActivityCollectCouponsRegisteRecord registRecord) throws Exception {
		registeRecordMapper.saveRecord(registRecord);
	}

}
