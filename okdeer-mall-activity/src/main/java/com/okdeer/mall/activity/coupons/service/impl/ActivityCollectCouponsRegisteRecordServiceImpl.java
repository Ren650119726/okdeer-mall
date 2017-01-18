package com.okdeer.mall.activity.coupons.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.esotericsoftware.minlog.Log;
import com.github.pagehelper.PageHelper;
import com.okdeer.archive.system.entity.SysSmsVerifyCode;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.ca.api.buyeruser.entity.SysBuyerUserDto;
import com.okdeer.common.consts.LogConstants;
import com.okdeer.common.consts.PointConstants;
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
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordService;
import com.okdeer.mall.activity.coupons.vo.InvitationRegisterRecordVo;
import com.okdeer.mall.activity.coupons.vo.InvitationRegisterVo;
import com.okdeer.mall.member.points.dto.AddPointsParamDto;
import com.okdeer.mall.member.points.enums.PointsRuleCode;
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
	
	/**
	 * 代金券领取记录Mapper
	 */
	@Autowired
	private ActivityCouponsRecordMapper activityCouponsRecordMapper;
	
	/**
	 * 代金券领取记录service
	 */
	@Autowired
	private ActivityCouponsRecordService activityCouponsRecordService;
	
	/**
	 * mq
	 */
	@Autowired
	private RocketMQProducer rocketMQProducer;
	
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
		// 完善买家头像图片地址信息
		fillPicUrl(invitationRecordList);
		// 邀请成功的总人数
		Integer totalInvitation = invitationRecordList.size();
		inviteRegisterVo.setTotalFavour(totalFavour);
		// 邀请注册获取的总奖励
		inviteRegisterVo.setRewardAmount(getRewardAmount(userId, activityId));
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
	private void fillPicUrl(List<InvitationRegisterRecordVo> invitationRecordList){
		String picUrl = null;
		// 图片格式：图片域名+图片名称
		String picUrlFormat = "%s%s";
		for(InvitationRegisterRecordVo record : invitationRecordList){
			// 如果买家用户没有图片，则取默认图片值
			picUrl = StringUtils.isEmpty(record.getPicUrl()) ? StaticConstants.USER_PIC_DEFAULT : record.getPicUrl();
			record.setPicUrl(String.format(picUrlFormat, userInfoPicServerUrl,picUrl));
		}
	}
	
	/**
	 * @Description: 获取邀请人领取的总奖励金额
	 * @param userId
	 * @param activityId
	 * @return   
	 * @author maojj
	 * @date 2016年10月18日
	 */
	private int getRewardAmount(String userId,String activityId){
		// 查询用户作为被邀请人参与代金券活动邀请注册记录
		ActivityCollectCouponsRegisteRecord registerRecord = registeRecordMapper.selectByInviteId(userId);
		// 用户作为被邀请人注册之后领取的活动代金券作为总奖励 
		Date limitDate = registerRecord == null ? null : registerRecord.getCreateTime();
		// 构建查询总奖励的条件
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userId", userId);
		params.put("activityId", activityId);
		params.put("limitDate", limitDate);
		// 查询邀请人领取的总奖励金额
		Integer totalReward = activityCouponsRecordMapper.findTotalRewardAmount(params);
		return totalReward == null ? 0 : totalReward.intValue();
	}
	
	@Transactional(rollbackFor = Exception.class)
	@Override
	public String receiveInviteRegistFavour(Map<String, Object> requestParam) throws Exception {
		// 邀请人Id
		String userId = String.valueOf(requestParam.get("userId"));
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
		String inviteesId = sysBuyerUserService.addSysBuyerSync410(sysBuyerUserDto, sysSmsVerifyCodeUpdate, null);
		// 创建代金券活动邀请注册记录
		Date currentDate = new Date();
		ActivityCollectCouponsRegisteRecord registRecord = new ActivityCollectCouponsRegisteRecord();
		registRecord.setId(UuidUtils.getUuid());
		registRecord.setActivityId(activityId);
		registRecord.setUserId(userId);
		registRecord.setInviteId(inviteesId);
		registRecord.setCreateTime(currentDate);
		
		// 查询注册活动所赠送的代金券信息
		List<ActivityCoupons> couponslist = activityCouponsMapper.selectByActivityId(activityId);
		// 创建给用户赠送的代金券
		List<ActivityCouponsRecord> couponsRecordList = new ArrayList<ActivityCouponsRecord>();
		ActivityCouponsRecord couponsRecord = null;
		for(ActivityCoupons coupons : couponslist){
			if (coupons.getRemainNum() <= 0) {
				// 如果代金券已经被领完，则直接跳过
				continue;
			}
			
			couponsRecord = new ActivityCouponsRecord();
			couponsRecord.setId(UuidUtils.getUuid());
			couponsRecord.setCollectType(ActivityCouponsType.invite_regist);
			couponsRecord.setCouponsId(coupons.getId());
			couponsRecord.setCouponsCollectId(activityId);
			couponsRecord.setCollectTime(currentDate);
			couponsRecord.setCollectUserId(inviteesId);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(currentDate);
			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0,
					0, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.add(Calendar.DAY_OF_YEAR, coupons.getValidDay());
			couponsRecord.setValidTime(calendar.getTime());
			couponsRecord.setStatus(ActivityCouponsRecordStatusEnum.UNUSED);
			try {
				// 用户成功领取代金券，需要修改代金券剩余数量
				activityCouponsMapper.updateRemainNum(coupons.getId());
				// 剩余数量更改成功，则给用户送代金券。否则不送
				couponsRecordList.add(couponsRecord);
			} catch (Exception e) {
				Log.error(LogConstants.ERROR_EXCEPTION,e.getMessage());
			}
		}
		
		// 保存邀请记录
		registeRecordMapper.saveRecord(registRecord);
		// 保存用户领取代金券记录
		if (CollectionUtils.isNotEmpty(couponsRecordList)) {
			activityCouponsRecordMapper.insertSelectiveBatch(couponsRecordList);
		}
		// 用户参与注册送代金券活动
		getRegisterCoupons(inviteesId);
		
		// 邀请人添加成功邀请好友积分
		AddPointsParamDto addPointsParamDto = new AddPointsParamDto();
		addPointsParamDto.setPointsRuleCode(PointsRuleCode.INVITE_REGISTER);
		addPointsParamDto.setUserId(userId);
		addPointsParamDto.setBusinessId(UuidUtils.getUuid());
		MQMessage anMessage = new MQMessage(PointConstants.TOPIC_POINT_ADD, (Serializable) addPointsParamDto);
		rocketMQProducer.sendMessage(anMessage);
		
		// 被邀请人添加注册积分
		AddPointsParamDto addPointsParamDtoNew = new AddPointsParamDto();
		addPointsParamDtoNew.setPointsRuleCode(PointsRuleCode.REGISTER);
		addPointsParamDtoNew.setUserId(inviteesId);
		addPointsParamDtoNew.setBusinessId(UuidUtils.getUuid());
		MQMessage anMessageNew = new MQMessage(PointConstants.TOPIC_POINT_ADD, (Serializable) addPointsParamDtoNew);
		rocketMQProducer.sendMessage(anMessageNew);
		
		return inviteesId;
	}
	
	private void getRegisterCoupons(String userId) throws ServiceException{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", 1);// 注册活动
		List<ActivityCoupons> lstActivityCoupons = null;
		List<ActivityCoupons> lstActivityCouponFilter = null;
		lstActivityCoupons = activityCouponsMapper.listCouponsByType(map);
		if (CollectionUtils.isEmpty(lstActivityCoupons)) {
			// 没有注册送代金券的活动
			return;
		}

		lstActivityCouponFilter = new ArrayList<ActivityCoupons>();
		for (ActivityCoupons activityCoupons : lstActivityCoupons) {
			int remainNum = activityCoupons.getRemainNum();// 剩余总数量
			if (remainNum > 0) {
				lstActivityCouponFilter.add(activityCoupons);
			}
		}

		if (CollectionUtils.isEmpty(lstActivityCouponFilter)) {
			// 活动代金券已经全部被领完
			return;
		}
		activityCouponsRecordService.drawCouponsRecord(lstActivityCouponFilter, ActivityCouponsType.sign, userId);
	}
	// End Bug:14408 added by maojj 2016-10-17
}
