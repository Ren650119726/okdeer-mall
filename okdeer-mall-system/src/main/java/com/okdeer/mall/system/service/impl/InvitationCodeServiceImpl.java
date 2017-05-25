
package com.okdeer.mall.system.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.archive.system.entity.SysUser;
import com.okdeer.archive.system.service.ISysUserServiceApi;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.common.consts.PointConstants;
import com.okdeer.mall.member.points.dto.AddPointsParamDto;
import com.okdeer.mall.member.points.enums.PointsRuleCode;
import com.okdeer.mall.system.entity.SysUserInvitationCode;
import com.okdeer.mall.system.entity.SysUserInvitationCodeVo;
import com.okdeer.mall.system.entity.SysUserInvitationLoginNameVO;
import com.okdeer.mall.system.entity.SysUserInvitationRecord;
import com.okdeer.mall.system.entity.SysUserInvitationRecordVo;
import com.okdeer.mall.system.enums.InvitationUserType;
import com.okdeer.mall.system.mapper.SysUserInvitationCodeMapper;
import com.okdeer.mall.system.mapper.SysUserInvitationRecordMapper;
import com.okdeer.mall.system.service.InvitationCodeService;
import com.okdeer.mall.system.service.InvitationCodeServiceApi;
import com.okdeer.mall.system.service.SysBuyerUserServiceApi;

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
 *      V1.1.0           2016年9月28日                                zhaoqc        添加根据用户Id查询邀请码信息  
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.system.service.InvitationCodeServiceApi")
public class InvitationCodeServiceImpl implements InvitationCodeServiceApi, InvitationCodeService {

	private static final Logger logger = LoggerFactory.getLogger(InvitationCodeServiceImpl.class);
	
	/**
	 * 系统用户
	 */
	@Reference(version = "1.0.0", check = false)
	private ISysUserServiceApi sysUserService;
	
	/**
	 * 买家用户
	 */
	@Reference(version = "1.0.0", check = false)
	private SysBuyerUserServiceApi sysBuyerUserService;
	
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
	
	@Autowired
	private RocketMQProducer rocketMQProducer;

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
			// 三个月前
			cal.add(Calendar.MONTH, -3);
			beginTime = cal.getTime();
		}
		if (beginTime != null) {
			// 页面搜索时间框传来的时间
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
			// 三个月前
			cal.add(Calendar.MONTH, -3);
			beginTime = cal.getTime();
		}
		if (beginTime != null) {
			// 页面搜索时间框传来的时间
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
			// 三个月前
			cal.add(Calendar.MONTH, -3);
			beginTimeRecord = cal.getTime();
		}
		if (beginTimeRecord != null) {
			// 页面搜索时间框传来的时间
			Date sta = DateUtils.getDateStart(beginTimeRecord);
			sysUserInvitationRecordVo.setBeginTimeRecord(sta);
		}
		if (endTimeRecord != null) {
			Date end = DateUtils.getDateEnd(endTimeRecord);
			sysUserInvitationRecordVo.setEndTimeRecord(end);
		}
		List<SysUserInvitationRecordVo> list = sysUserInvitationRecordMapper
				.findByQueryRecordVo(sysUserInvitationRecordVo);
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
			// 三个月前
			cal.add(Calendar.MONTH, -3);
			beginTimeRecord = cal.getTime();
		}
		if (beginTimeRecord != null) {
			// 页面搜索时间框传来的时间
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

	/**
	 * 保存邀请码记录
	 * @param code
	 * @throws ServiceException
	 * 涂志定 start 2016-10-4 修改进行数据校验
	 */
	@Override
	public void saveCode(SysUserInvitationCode code) throws ServiceException {
		// 保存邀请码数据，需要校验改
		if (code != null && code.getInvitationCode() != null) {
			String userid = code.getSysBuyerUserId();
			// 手机用户id为空去取后台系统用户
			if (StringUtils.isBlank(userid)) {
				// 并且如果后台系统用户id都为空 返回
				if (StringUtils.isBlank(code.getSysUserId())) {
					return;
				}
				userid = code.getSysUserId();
			}
			// 如果该验证码或用户id 不存在记录，才进行保存
			List<SysUserInvitationCode> ls = sysUserInvitationCodeMapper
					.findInvitationByIdCode(code.getInvitationCode(), userid);
			if (CollectionUtils.isEmpty(ls)) {
				sysUserInvitationCodeMapper.saveCode(code);
			}
		}
	}

	// 涂志定 end 2016-10-4
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
		SysUserInvitationCode sysUser = sysUserInvitationCodeMapper.selectInvitationByCode(invitationCode);

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

	@Override
	public SysUserInvitationCode findInvitationCodeByUserId(String userId, InvitationUserType userType) {
		return this.sysUserInvitationCodeMapper.findInvitationCodeByUserId(userId, userType.ordinal());
	}

	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int fillInvitationCode(String userId, String invitationCode, String machineCode) throws Exception{
		// 用户自己不能邀请自己
		SysUserInvitationCode invatitationInfo = this.sysUserInvitationCodeMapper.findInvitationCodeByUserId(userId,
				InvitationUserType.phoneUser.ordinal());
		if (invatitationInfo != null) {
			if (invitationCode.equals(invatitationInfo.getInvitationCode())) {
				return 3;
			}
		}

		// 验证邀请码是否有效
		invatitationInfo = this.sysUserInvitationCodeMapper.findInvitationCodeByCode(invitationCode);
		if (invatitationInfo == null) {
			// 邀请码不存在
			return 1;
		}
		return  saveInvatationRecord(invatitationInfo, userId, machineCode);
	}
	
	/**
	 * 保存邀请码记录 
	 * @param invatitationInfo 邀请人邀请码记录
	 * @param userId 被邀请人id
	 * @param machineCode 机器编码
	 * @tuzhd
	 * @return
	 * @throws Exception 
	 */
	@Transactional(rollbackFor = Exception.class)
	public int saveInvatationRecord(SysUserInvitationCode invatitationInfo, String userId, String machineCode) throws Exception{
		SysUserInvitationRecord records = this.sysUserInvitationRecordMapper.findInvitationRecordByUserId(userId);
		if (records != null) {
			// 已经被邀请过，不能再次填写邀请记录
			return 2;
		}
		// 创建邀请记录表
		SysUserInvitationRecord invitationRecord = new SysUserInvitationRecord();
		invitationRecord.setId(UuidUtils.getUuid());
		invitationRecord.setInvitationCodeId(invatitationInfo.getId());
		invitationRecord.setSysBuyerUserId(userId);
		invitationRecord.setIsFirstOrder(WhetherEnum.not);
		invitationRecord.setMachineCode(machineCode);
		invitationRecord.setCreateTime(new Date());
		invitationRecord.setUpdateTime(new Date());
		this.sysUserInvitationRecordMapper.saveCodeRecord(invitationRecord);

		// 更新邀请码信息表
		invatitationInfo.setInvitationUserNum(invatitationInfo.getInvitationUserNum() + 1);
		invatitationInfo.setUpdateTime(new Date());
		this.sysUserInvitationCodeMapper.updateCode(invatitationInfo);
		
		if(invatitationInfo.getUserType() == InvitationUserType.phoneUser){
			//用户类型的邀请，給用户添加积分
			addPoint(invatitationInfo.getSysBuyerUserId(),invitationRecord.getId());
		}
		
		return 0;
	}

	@Override
	public SysUserInvitationCode findById(String id) throws Exception {
		return sysUserInvitationCodeMapper.selectById(id);
	}

	/**
	 * @Description: 邀请注册添加积分，发送消息
	 * @param userId 用户id
	 * @param orderId 订单id
	 * @param amount 金额
	 * @author zengjizu
	 * @throws Exception 
	 * @date 2016年12月31日
	 */
	private void addPoint(String userId, String businessId) throws Exception {
		AddPointsParamDto addPointsParamDto = new AddPointsParamDto();
		addPointsParamDto.setPointsRuleCode(PointsRuleCode.INVITE_REGISTER);
		addPointsParamDto.setUserId(userId);
		addPointsParamDto.setBusinessId(businessId);
		MQMessage anMessage = new MQMessage(PointConstants.TOPIC_POINT_ADD, (Serializable) addPointsParamDto);
		SendResult sendResult = rocketMQProducer.sendMessage(anMessage);
		if (sendResult.getSendStatus() == SendStatus.SEND_OK) {
			logger.info("发送添加积分消息成功，发送数据：{},topic:{}", JsonMapper.nonDefaultMapper().toJson(addPointsParamDto),
					PointConstants.TOPIC_POINT_ADD);
		} else {
			logger.error("发送添加积分消息失败,topic:{}", PointConstants.TOPIC_POINT_ADD);
		}
	}

	// Begin V2.1.0 added by luosm 20170215
	/***
	 * 
	 * @Description: 根据用户id获取邀请人账户名
	 * @param userId
	 * @return
	 * @throws ServiceException
	 * @author luosm
	 * @date 2017年2月15日
	 */
	@Override
	public String findInvitationNameByUserId(String userId) throws ServiceException {
		String loginName = null;
		SysUserInvitationRecord sysUserInvitationRecord =sysUserInvitationRecordMapper.findInvitationRecordByUserId(userId);
		if(sysUserInvitationRecord !=null&&StringUtils.isNotEmpty(sysUserInvitationRecord.getInvitationCodeId())){
		SysUserInvitationCode sysUserInvitationCode =sysUserInvitationCodeMapper.selectByPrimaryKey(sysUserInvitationRecord.getInvitationCodeId());
		
		if(sysUserInvitationCode!=null && sysUserInvitationCode.getUserType()!=null){
		if(sysUserInvitationCode.getUserType().ordinal() == 0){
			SysUser sysUser = sysUserService.findSysUserById(sysUserInvitationCode.getSysUserId());
			if(sysUser != null && StringUtils.isNotEmpty(sysUser.getLoginName())){
			loginName = sysUser.getLoginName();
			}
		}else if(sysUserInvitationCode.getUserType().ordinal() == 1){
			SysBuyerUser sysBuyerUser = sysBuyerUserService.findByPrimaryKey(sysUserInvitationCode.getSysBuyerUserId());
			if(sysBuyerUser !=null && StringUtils.isNotEmpty(sysBuyerUser.getLoginName())){
			loginName = sysBuyerUser.getLoginName();
			}
		  }
		 }
		}
		return loginName;
	}
	
	@Override
	public List<SysUserInvitationLoginNameVO> selectLoginNameByUserId(List<String> userIds) {
		return pageQueryByIds(userIds,new PageCallBack<SysUserInvitationLoginNameVO>() {

			@Override
			public List<SysUserInvitationLoginNameVO> callBackHandle(List<String> idList) {
				return sysUserInvitationCodeMapper.selectLoginNameByUserId(idList);
			}
		}, 100);
	}
	// End V2.1.0 added by luosm 20170215
	
	public static <T> List<T> pageQueryByIds(List<String> ids, PageCallBack<T> pageCallBack,final int pageSize) {
		List<T> resultList = Lists.newArrayList();
		if (ids.size() > pageSize) {
			// 如果list太大，分批查询
			int page = ids.size() % pageSize == 0 ? ids.size() / pageSize : ids.size() / pageSize + 1;
			for (int i = 0; i < page; i++) {
				int fromIndex = i * pageSize;
				int toIndex = fromIndex + pageSize - 1;
				if (toIndex > ids.size()) {
					toIndex = ids.size();
				}
				List<String> indexList = ids.subList(fromIndex, toIndex);
				List<T> tempList = pageCallBack.callBackHandle(indexList);
				
				resultList.addAll(tempList);
			}
		}else{
			List<T> tempList = pageCallBack.callBackHandle(ids);
			resultList.addAll(tempList);
		}
		return resultList;
	}
	public interface PageCallBack<T> {
		List<T> callBackHandle(List<String> idList);
	}
}
