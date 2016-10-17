package com.okdeer.mall.activity.coupons.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.archive.system.entity.SysSmsVerifyCode;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.ca.api.buyeruser.entity.SysBuyerUserDto;
import com.okdeer.common.consts.StaticConstants;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsRegisteRecord;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsRegisteRecordVo;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsType;
import com.okdeer.mall.activity.coupons.mapper.ActivityCollectCouponsRegistRecordMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsRegisteRecordService;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsRegisteRecordServiceApi;
import com.okdeer.mall.activity.coupons.vo.InvitationRegisterRecordVo;
import com.okdeer.mall.activity.coupons.vo.InvitationRegisterVo;
import com.okdeer.mall.system.service.SysBuyerUserService;

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
 *		V1.1.0			2016-10-15 			wushp			V1.1.0
 * 		Bug:14408		 2016年10月17日 	    maojj			邀请注册送代金券活动首页显示邀请记录（包括成功邀请人数、获得奖励、被邀请人头像、是否完成首单）
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsRegisteRecordServiceApi")
public class ActivityCollectCouponsRegisteRecordServiceImpl 
          implements ActivityCollectCouponsRegisteRecordService, ActivityCollectCouponsRegisteRecordServiceApi {

	/**
	 * 邀请注册记录Mapper
	 */
	@Autowired
	private ActivityCollectCouponsRegistRecordMapper registeRecordMapper;
	
	// Begin Bug:14408 added by maojj 2016-10-17
	/**
	 * 代金券活动mapper
	 */
	@Autowired
	private ActivityCouponsMapper activityCouponsMapper;
	
	/**
	 * 买家用户Service
	 */
	@Autowired
	private SysBuyerUserService sysBuyerUserService;
	
	@Autowired
	private ActivityCouponsRecordMapper activityCouponsRecordMapper;
	
	/**
	 * 用户信息图片域名
	 */
	@Value("${myinfoImagePrefix}")
	private String userInfoPicServerUrl;
	// End added by maojj 2016-10-17
	
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

	// begin add by wushp 20161015
	@Transactional(readOnly = true)
	@Override
	public ActivityCollectCouponsRegisteRecord selectByInviteId(String inviteId) throws Exception {
		return registeRecordMapper.selectByInviteId(inviteId);
	}

	@Override
	public int updateByPrimaryKeySelective(ActivityCollectCouponsRegisteRecord registRecord) throws Exception {
		return registeRecordMapper.updateByPrimaryKeySelective(registRecord);
	}
	// end add by wushp 20161015

	// Begin Bug:14408 added by maojj 2016-10-17
	@Override
	public InvitationRegisterVo findInviteRegisterRecord(String userId, String activityId) {
		InvitationRegisterVo inviteRegisterVo = new InvitationRegisterVo();
		// 获取邀请注册送代金券活动的总金额
		int totalFavour = activityCouponsMapper.selectFaceMoney(activityId);
		// 获取用户邀请注册记录
		List<InvitationRegisterRecordVo> invitationRecordList = registeRecordMapper.findInviteRegisterRecord(userId, activityId);
		// 完善买家头像图片地址信息并获取成功下单的总人数
		Integer orderedNum = parseInviteRecordList(invitationRecordList);
		// 邀请成功的总人数
		Integer totalInvitation = invitationRecordList.size();
		// 邀请注册获取的总奖励
		Integer rewardAmount = totalFavour * orderedNum;
		
		inviteRegisterVo.setTotalFavour(totalFavour);
		inviteRegisterVo.setRewardAmount(rewardAmount);
		inviteRegisterVo.setTotalInvitation(totalInvitation);
		inviteRegisterVo.setActivityId(activityId);
		inviteRegisterVo.setInvitationRecordList(invitationRecordList);
		return inviteRegisterVo;
	}
	
	/**
	 * @Description: 完善买家头像图片地址信息并获取成功下单的总人数
	 * @param invitationRecordList
	 * @return   
	 * @author maojj
	 * @date 2016年10月17日
	 */
	private int parseInviteRecordList(List<InvitationRegisterRecordVo> invitationRecordList){
		int orderedNum = 0;
		String picUrl = null;
		// 图片格式：图片域名+图片名称
		String picUrlFormat = "%s%s";
		for(InvitationRegisterRecordVo record : invitationRecordList){
			// 如果买家用户没有图片，则取默认图片值
			picUrl = StringUtils.isEmpty(record.getPicUrl()) ? StaticConstants.USER_PIC_DEFAULT : record.getPicUrl();
			record.setPicUrl(String.format(picUrlFormat, userInfoPicServerUrl,picUrl));
			if(record.getIsCompleteOrder() == 1){
				orderedNum++;
			}
		}
		return orderedNum;
	}
	
	@Override
	public void receiveInviteRegistFavour(Map<String, Object> requestParam) throws Exception {
		// 邀请人Id
		String userId = String.valueOf(requestParam.get("userId"));
		// 验证码
		String verifyCode = String.valueOf(requestParam.get("verifyCode"));
		// 邀请注册活动Id
		String activityId = String.valueOf(requestParam.get("activityId"));
		// 被邀请人手机号码
		String phone = String.valueOf(requestParam.get("phone"));
		// 使用的验证码Id
		String verifyCodeId = String.valueOf(requestParam.get("verifyCodeId"));
		// 被邀请人领取即新增该用户信息
		SysBuyerUserDto sysBuyerUserDto = new SysBuyerUserDto();
		sysBuyerUserDto.setPhone(phone);
		sysBuyerUserDto.setLoginName(phone);
		// 需要更新的验证码
		SysSmsVerifyCode sysSmsVerifyCodeUpdate = new SysSmsVerifyCode();
		sysSmsVerifyCodeUpdate.setId(verifyCodeId);
		sysSmsVerifyCodeUpdate.setStatus(1);
		// 保存被邀请人用户信息并更新验证码
		String inviteesId = sysBuyerUserService.addSysBuyerSync(sysBuyerUserDto, sysSmsVerifyCodeUpdate, null);
		// 创建代金券活动邀请注册记录
		ActivityCollectCouponsRegisteRecord registRecord = new ActivityCollectCouponsRegisteRecord();
		registRecord.setId(UuidUtils.getUuid());
		registRecord.setActivityId(activityId);
		registRecord.setUserId(userId);
		registRecord.setInviteId(inviteesId);
		registRecord.setCreateTime(new Date());
		
		// 查询注册活动所赠送的代金券信息
		List<ActivityCoupons> couponslist = activityCouponsMapper.selectByActivityId(activityId);
		// 创建给用户赠送的代金券
		List<ActivityCouponsRecord> couponsRecordList = new ArrayList<ActivityCouponsRecord>();
		ActivityCouponsRecord couponsRecord = null;
		for(ActivityCoupons coupons : couponslist){
			couponsRecord = new ActivityCouponsRecord();
			couponsRecord.setId(UuidUtils.getUuid());
			couponsRecord.setCollectType(ActivityCouponsType.invite_regist);
			couponsRecord.setCouponsId(coupons.getId());
			couponsRecord.setCouponsCollectId(activityId);
			couponsRecord.setCollectTime(new Date());
			couponsRecord.setCollectUserId(inviteesId);
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0,
					0, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.add(Calendar.DAY_OF_YEAR, coupons.getValidDay());
			couponsRecord.setValidTime(calendar.getTime());
			couponsRecord.setStatus(ActivityCouponsRecordStatusEnum.UNUSED);
			couponsRecordList.add(couponsRecord);
		}
		
		// 保存邀请记录
		registeRecordMapper.saveRecord(registRecord);
		// 保存用户领取代金券记录
		activityCouponsRecordMapper.insertSelectiveBatch(couponsRecordList);
	}
	// End Bug:14408 added by maojj 2016-10-17
}
