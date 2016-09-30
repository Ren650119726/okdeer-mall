
package com.okdeer.mall.system.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.archive.system.entity.SysBuyerUserThirdparty;
import com.okdeer.archive.system.entity.SysSmsVerifyCode;
import com.okdeer.archive.system.entity.SysUserLoginLog;
import com.okdeer.archive.system.service.SysSmsVerifyCodeServiceApi;
import com.okdeer.archive.system.service.SysUserLoginLogServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.EncryptionUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.dal.IBaseCrudMapper;
import com.okdeer.base.service.BaseCrudServiceImpl;
import com.okdeer.ca.api.buyeruser.entity.SysBuyerUserConditionDto;
import com.okdeer.ca.api.buyeruser.entity.SysBuyerUserDto;
import com.okdeer.ca.api.buyeruser.entity.SysBuyerUserItemDto;
import com.okdeer.ca.api.buyeruser.service.ISysBuyerUserApi;
import com.okdeer.ca.api.common.ApiException;
import com.okdeer.ca.api.sysuser.entity.SysUserDto;
import com.okdeer.ca.api.sysuser.service.ISysUserApi;
import com.okdeer.common.consts.RedisKeyConstants;
import com.okdeer.mall.common.utils.security.DESUtils;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.member.member.entity.SysBuyerExt;
import com.okdeer.mall.member.member.service.MemberConsigneeAddressServiceApi;
import com.okdeer.mall.member.member.service.SysBuyerExtServiceApi;
import com.okdeer.mall.member.points.entity.PointsRecord;
import com.okdeer.mall.member.points.entity.PointsRule;
import com.okdeer.mall.member.points.enums.PointsRuleCode;
import com.okdeer.mall.member.points.service.PointsBuriedServiceApi;
import com.okdeer.mall.member.points.service.PointsRecordServiceApi;
import com.okdeer.mall.member.points.service.PointsRuleServiceApi;
import com.okdeer.mall.system.entity.BuyerUserVo;
import com.okdeer.mall.system.entity.SysUserInvitationCode;
import com.okdeer.mall.system.enums.InvitationUserType;
import com.okdeer.mall.system.enums.VerifyCodeBussinessTypeEnum;
import com.okdeer.mall.system.mapper.SysBuyerUserMapper;
import com.okdeer.mall.system.mapper.SysBuyerUserThirdpartyMapper;
import com.okdeer.mall.system.mapper.SysSmsVerifyCodeMapper;
import com.okdeer.mall.system.service.InvitationCodeService;
import com.okdeer.mall.system.service.SysBuyerUserService;
import com.okdeer.mall.system.service.SysBuyerUserServiceApi;

/**
 * @DESC: 买家用户信息
 * @author YSCGD
 * @date  2016-03-17 17:05:52
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.system.service.SysBuyerUserServiceApi")
class SysBuyerUserServiceImpl extends BaseCrudServiceImpl implements SysBuyerUserService, SysBuyerUserServiceApi {

	private static final Logger logger = LoggerFactory.getLogger(SysBuyerUserServiceImpl.class);

	@Resource
	private SysBuyerUserMapper sysBuyerUserMapper;

	// begin by wangf01 2016.07.27
	/**
	 * 登录送积分
	 */
	@Reference(version = "1.0.0", check = false)
	private PointsBuriedServiceApi pointsBuriedService;

	/**
	 * 系统用户登陆信息日志
	 */
	@Reference(version = "1.0.0", check = false)
	private SysUserLoginLogServiceApi sysUserLoginLogService;

	/**
	 * 短信验证码
	 */
	@Reference(version = "1.0.0", check = false)
	private SysSmsVerifyCodeServiceApi sysSmsVerifyCodeService;

	/**
	 * 
	 */
	@Reference(version = "1.0.0", check = false)
	private SysBuyerUserServiceApi sysBuyerUserService;
	// end by wangf01 2016.07.27

	// @Resource
	// private SysBuyerExtMapper sysBuyerExtMapper;
	@Reference(version = "1.0.0", check = false)
	SysBuyerExtServiceApi sysBuyerExtServiceApi;

	@Resource
	private SysSmsVerifyCodeMapper sysSmsVerifyCodeMapper;

	@Resource
	private SysBuyerUserThirdpartyMapper sysBuyerUserThirdpartyMapper;

	// @Resource
	// private PointsRuleMapper pointsRuleMapper;
	@Reference(version = "1.0.0", check = false)
	PointsRuleServiceApi pointsRuleServiceApi;

	// @Resource
	// private PointsRecordMapper pointsRecordMapper;
	@Reference(version = "1.0.0", check = false)
	PointsRecordServiceApi pointsRecordServiceApi;

	@Reference(version = "1.0.0", check = false)
	private ISysBuyerUserApi sysBuyerUserApi;

	/**
	 * mapper注入
	 */
	// @Autowired
	// StoreInfoMapper storeInfoMapper;
	@Reference(version = "1.0.0", check = false)
	StoreInfoServiceApi storeInfoServiceApi;

	/**
	 * 用户中心系统用户接口注入
	 */
	@Reference(version = "1.0.0", check = false)
	ISysUserApi sysUserApi;

	/**
	 * mapper注入
	 */
	// @Autowired
	// MemberConsigneeAddressMapper memberConsigneeAddressMapper;
	@Reference(version = "1.0.0", check = false)
	private MemberConsigneeAddressServiceApi MemberConsigneeAddressServiceApi;

	/**
	 * RedisTemplate
	 */
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	/**
	 * 用户邀请码service
	 */
	@Autowired
	private InvitationCodeService invitationCodeService;
	
	
	@Override
	public IBaseCrudMapper init() {
		return sysBuyerUserMapper;
	}

	/**
	 * DESC: 添加买家用户信息、修改验证码状态、添加第三方平台账号与本平台账号映射
	 * @author LIU.W
	 * @param sysSmsVerifyCodeUpdate
	 * @param buyerUserThirdparty
	 * @throws ServiceException
	 */
	@Transactional(rollbackFor = Exception.class)
	public String addSysBuyerSync(SysBuyerUserDto sysBuyerUserDto, SysSmsVerifyCode sysSmsVerifyCodeUpdate,
			SysBuyerUserThirdparty buyerUserThirdparty) throws ApiException, ServiceException {

		try {

			String buyerId = UuidUtils.getUuid();
			/**
			 * 1. 更新验证码状态
			 */
			if (null != sysSmsVerifyCodeUpdate) {
				sysSmsVerifyCodeMapper.updateByPrimaryKeySelective(sysSmsVerifyCodeUpdate);
			}

			/**
			 * 2. 添加第三方平台与自平台账号映射关系
			 */
			if (null != buyerUserThirdparty) {
				buyerUserThirdparty.setBuyerUserId(buyerId);
				sysBuyerUserThirdpartyMapper.insertSelective(buyerUserThirdparty);
			}
			/**
			 * 3. 添加用户注册信息
			 */
			if (null != sysBuyerUserDto) {

				/**
				 * 3.1 添加买家用户扩展表信息并注册用户送积分
				 */
				PointsRule pointsRule = new PointsRule();
				pointsRule.setCode(PointsRuleCode.REGISTER.getCode());
				/**
				 * 3.2 根据积分规则编码code查询规则
				 */
				// PointsRule pointRule =
				// pointsRuleMapper.selectByCode(pointsRule);
				PointsRule pointRule = pointsRuleServiceApi.selectByCode(pointsRule);
				if (null == pointRule) {
					return buyerId;
				}
				/**
				 * 3.3 添加用户扩展信息及积分详细记录
				 */
				SysBuyerExt sysBuyerExt = new SysBuyerExt();
				sysBuyerExt.setId(UuidUtils.getUuid());
				sysBuyerExt.setUserId(buyerId);
				sysBuyerExt.setPointVal(pointRule.getPointVal());
				// sysBuyerExtMapper.insertSelective(sysBuyerExt);
				sysBuyerExtServiceApi.insertSelective(sysBuyerExt);

				PointsRecord pointsRecord = new PointsRecord();
				pointsRecord.setId(UuidUtils.getUuid());
				pointsRecord.setUserId(buyerId);
				pointsRecord.setCode(pointRule.getCode());
				pointsRecord.setPointVal(pointRule.getPointVal());
				pointsRecord.setType((byte) 0);
				pointsRecord.setDescription(pointRule.getRemark());
				pointsRecord.setCreateTime(new Date());
				// pointsRecordMapper.insert(pointsRecord);
				pointsRecordServiceApi.insert(pointsRecord);
				/**
				 * 3.4 添加用户信息
				 */
				sysBuyerUserDto.setId(buyerId);
				sysBuyerUserApi.save(sysBuyerUserDto);
			}

			return buyerId;
		} catch (ApiException e) {
			logger.error("添加用户失败!", e);
			throw e;
		} catch (Exception e) {
			throw new ServiceException("添加用户失败!", e);
		}
	}

	// begin add by wangf01 2016.08.11
	@Transactional(rollbackFor = Exception.class)
	public String addSysBuyerSync410(SysBuyerUserDto sysBuyerUserDto, SysSmsVerifyCode sysSmsVerifyCodeUpdate,
			SysBuyerUserThirdparty buyerUserThirdparty) throws Exception {

		try {
			String buyerId = UuidUtils.getUuid();
			/**
			 * 1. 更新验证码状态
			 */
			if (null != sysSmsVerifyCodeUpdate) {
				sysSmsVerifyCodeMapper.updateByPrimaryKeySelective(sysSmsVerifyCodeUpdate);
			}

			/**
			 * 2. 添加第三方平台与自平台账号映射关系
			 */
			if (null != buyerUserThirdparty) {
				buyerUserThirdparty.setBuyerUserId(buyerId);
				sysBuyerUserThirdpartyMapper.insertSelective(buyerUserThirdparty);
			}
			/**
			 * 3. 添加用户注册信息
			 */
			if (null != sysBuyerUserDto) {
				/**
				 * 3.4 添加用户信息
				 */
				sysBuyerUserDto.setId(buyerId);
				sysBuyerUserApi.save(sysBuyerUserDto);
			}

			return buyerId;
		} catch (ApiException e) {
			logger.error("添加用户失败!", e);
			throw e;
		} catch (Exception e) {
			throw new ServiceException("添加用户失败!", e);
		}
	}
	// end add by wangf01 2016.08.11

	/**
	 * DESC: 修改验证码状态、添加第三方平台账号与本平台账号映射
	 * @author LIU.W
	 * @param sysSmsVerifyCodeUpdate
	 * @param buyerUserThirdparty
	 * @throws ServiceException
	 */
	@Transactional(rollbackFor = Exception.class)
	public void addSysBuyerThirdParty(SysSmsVerifyCode sysSmsVerifyCodeUpdate,
			SysBuyerUserThirdparty buyerUserThirdparty) throws ServiceException {

		try {
			/**
			 * 2. 更新验证码状态
			 */
			if (null != sysSmsVerifyCodeUpdate) {
				sysSmsVerifyCodeMapper.updateByPrimaryKeySelective(sysSmsVerifyCodeUpdate);
			}
			/**
			 * 3. 添加第三方平台与自平台账号映射关系
			 */
			if (null != buyerUserThirdparty) {
				sysBuyerUserThirdpartyMapper.insertSelective(buyerUserThirdparty);
			}

		} catch (Exception e) {
			throw new ServiceException("", e);
		}
	}

	@Override
	public String selectMemberMobile(String userId) {
		return sysBuyerUserMapper.selectMemberMobile(userId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMobile(String phone, String smsCodeId, String userId, String storeId)
			throws ServiceException, ApiException {
		SysUserDto sysUserDto = new SysUserDto();
		sysUserDto.setId(userId);
		sysUserDto.setPhone(phone);
		sysUserDto.setUpdateUserId(userId);
		// 更新用户中心用户手机号码
		sysUserApi.edit(sysUserDto);
		StoreInfo storeInfo = new StoreInfo();
		storeInfo.setId(storeId);
		storeInfo.setMobile(phone);
		storeInfo.setUpdateUserId(userId);
		storeInfo.setUpdateTime(new Date());
		// 更新店铺表手机号码
		// storeInfoMapper.updateByPrimaryKeySelective(storeInfo);
		storeInfoServiceApi.updateByPrimaryKeySelective(storeInfo);

		// List<MemberConsigneeAddress> addressList =
		// memberConsigneeAddressMapper.selectByUserId(storeId);
		List<MemberConsigneeAddress> addressList = MemberConsigneeAddressServiceApi.findListByUserId(storeId);
		if (addressList != null && addressList.size() > 0) {
			MemberConsigneeAddress memberConsigneeAddress = addressList.get(0);
			memberConsigneeAddress.setMobile(phone);
			// 绑定手机号码时将手机号码设置到店铺初始地址的手机号码字段里
			// memberConsigneeAddressMapper.updateByPrimaryKeySelective(memberConsigneeAddress);
			MemberConsigneeAddressServiceApi.updateByPrimaryKeySelective(memberConsigneeAddress);
		}

		// 更新验证码已使用状态
		SysSmsVerifyCode sysSmsVerifyCodeUpdate = new SysSmsVerifyCode();
		sysSmsVerifyCodeUpdate.setId(smsCodeId);
		sysSmsVerifyCodeUpdate.setStatus(1);
		sysSmsVerifyCodeMapper.updateByPrimaryKeySelective(sysSmsVerifyCodeUpdate);
	}

	// begin add by wushp
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.system.service.SysBuyerUserServiceApi#findByPrimaryKey(java.lang.String)
	 */
	@Override
	public SysBuyerUser findByPrimaryKey(String userId) throws ServiceException {
		return sysBuyerUserMapper.selectByPrimaryKey(userId);
	}
	// end add by wushp

	// begin by wangf01 2016.07.26
	@Transactional(rollbackFor = Exception.class)
	@Override
	public Map<String, Object> saveBuyerUserPwdLogin(Map<String, Object> requestMap) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		String mobilePhone = requestMap.get("mobilePhone").toString();
		String loginPassword = requestMap.get("loginPassword").toString();
		String machineCode = requestMap.get("machineCode").toString();
		String token = requestMap.get("token").toString();
		SysBuyerUserItemDto sysBuyerUserItemDto = (SysBuyerUserItemDto) requestMap.get("sysBuyerUserItemDto");
		BuyerUserVo resultBuyerUserVo = (BuyerUserVo) requestMap.get("resultBuyerUserVo");

		loginPassword = EncryptionUtils.md5(DESUtils.decrypt(loginPassword));
		// 查询手机用户信息
		sysBuyerUserItemDto = sysBuyerUserApi.login(mobilePhone, null);
		// 验证密码登录
		if (null == sysBuyerUserItemDto) {
			map.put("flag", 1);
			return map;
		}
		// 验证用户是否设置密码
		if (StringUtils.isEmpty(sysBuyerUserItemDto.getLoginPassword())) {
			map.put("flag", 2);
			return map;
		}
		if (!(loginPassword.toLowerCase()).equals(sysBuyerUserItemDto.getLoginPassword().toLowerCase())) {
			map.put("flag", 3);
			return map;
		}
		
		//查看用户是否有邀请码
		SysUserInvitationCode invitationCodeEntity = this.invitationCodeService.findInvitationCodeByUserId(sysBuyerUserItemDto.getId(), InvitationUserType.phoneUser);
		resultBuyerUserVo = new BuyerUserVo();
		if(invitationCodeEntity != null) {
		    resultBuyerUserVo.setInvitationCode(invitationCodeEntity.getInvitationCode());
		}
		
		PropertyUtils.copyProperties(resultBuyerUserVo, sysBuyerUserItemDto);
		// 单点登录
		List<SysUserLoginLog> sysUserLoginLogs = sysUserLoginLogService.findAllByUserId(sysBuyerUserItemDto.getId(),
				null, null, SysUserLoginLog.CLIENT_TYPE_APP);
		singlePoint(machineCode, token, sysBuyerUserItemDto, map, sysUserLoginLogs);

		map.put("resultBuyerUserVo", resultBuyerUserVo);
		map.put("sysBuyerUserItemDto", sysBuyerUserItemDto);
		return map;
	}

	/**
	 * @Description: 单点登录
	 * @param machineCode
	 * @param token
	 * @param sysBuyerUserItemDto
	 * @param map
	 * @param sysUserLoginLogs   
	 * @return void  
	 * @throws
	 * @author wangf01
	 * @date 2016年7月26日
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void singlePoint(String machineCode, String token, SysBuyerUserItemDto sysBuyerUserItemDto, Map map,
			List<SysUserLoginLog> sysUserLoginLogs) {
		List<String> ids = new ArrayList<String>();
		boolean bool = true;
		// 设备id
		SysUserLoginLog sysLog = new SysUserLoginLog();
		for (SysUserLoginLog sysUserLoginLog : sysUserLoginLogs) {
			// 该设备是否有登陆过
			if (bool && machineCode.equals(sysUserLoginLog.getDeviceId())) {
				sysLog.setId(sysUserLoginLog.getId());
				sysLog.setDeviceId(sysUserLoginLog.getDeviceId());
				sysLog.setUserId(sysUserLoginLog.getId());
				sysLog.setCreateTime(sysUserLoginLog.getCreateTime());
				sysLog.setClientType(sysUserLoginLog.getClientType());
				bool = false;
			}
			// 设备是否在线

			if (SysUserLoginLog.IS_LOGIN_STAUE_1.equals(sysUserLoginLog.getIsLogin())) {
				ids.add(sysUserLoginLog.getId());
			} // 清除已上线设备

		}

		map.put("sysUserLoginLogs", sysUserLoginLogs);

		// 清除已上线设备
		if (ids != null && ids.size() > 0) {
			sysUserLoginLogService.updateIsLoginByIds(ids);
		}

		Date data = new Date();
		// 设置设备登陆信息
		if (!bool) {
			sysLog.setIsLogin(SysUserLoginLog.IS_LOGIN_STAUE_1);
			sysLog.setToken(token);
			sysLog.setUpdateTime(data);
			sysUserLoginLogService.updateSysUserLoginLog(sysLog);
		} else {
			sysLog = new SysUserLoginLog();
			sysLog.setId(UuidUtils.getUuid());
			sysLog.setDeviceId(machineCode);
			sysLog.setIsLogin(SysUserLoginLog.IS_LOGIN_STAUE_1);
			sysLog.setToken(token);
			sysLog.setUserId(sysBuyerUserItemDto.getId());
			sysLog.setCreateTime(data);
			sysLog.setUpdateTime(data);
			sysLog.setClientType(SysUserLoginLog.CLIENT_TYPE_APP);
			sysUserLoginLogService.insertSysUserLoginLog(sysLog);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Map<String, Object> saveBuyerUserVerifyCodeLogin(Map<String, Object> requestMap) throws Exception {
		BuyerUserVo buyerUserVo = (BuyerUserVo) requestMap.get("buyerUserVo");
		String mobilePhone = requestMap.get("mobilePhone").toString();
		SysBuyerUserDto sysBuyerUserDto = (SysBuyerUserDto) requestMap.get("sysBuyerUserDto");
		BuyerUserVo resultBuyerUserVo = (BuyerUserVo) requestMap.get("resultBuyerUserVo");
		SysBuyerUserItemDto sysBuyerUserItemDto = (SysBuyerUserItemDto) requestMap.get("sysBuyerUserItemDto");
		String machineCode = requestMap.get("machineCode").toString();
		String token = requestMap.get("token").toString();

		buyerUserVo.setVerifyCode(buyerUserVo.getVerifyCode().toLowerCase());
		String verifyCode = buyerUserVo.getVerifyCode();
		Integer verifyCodeType = buyerUserVo.getVerifyCodeType();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("phoneSearch", mobilePhone);
		params.put("typeSearch", verifyCodeType);
		params.put("bussinessTypeSearch", VerifyCodeBussinessTypeEnum.LOGIN.getCode());
		SysSmsVerifyCode sysSmsVerifyCode = sysSmsVerifyCodeService.findLatestByParams(params);
		// 如果验证码为空或者验证码不相等，则提示验证码错误，如果验证码相等，但状态为已使用，则提示验证码失效
		if (null == sysSmsVerifyCode || !verifyCode.equals(sysSmsVerifyCode.getVerifyCode())) {
			requestMap.put("flag", 12);
			return requestMap;
		} else if (sysSmsVerifyCode.getStatus() == 1) {
			requestMap.put("flag", 11);
			return requestMap;
		}
		SysBuyerUserConditionDto condition = new SysBuyerUserConditionDto();
		condition.setLoginName(mobilePhone);
		List<SysBuyerUserItemDto> lstUserItemDtos = sysBuyerUserApi.findByCondition(condition);
		if (CollectionUtils.isEmpty(lstUserItemDtos)) {
			// 更新验证码已使用状态
			SysSmsVerifyCode sysSmsVerifyCodeUpdate = new SysSmsVerifyCode();
			sysSmsVerifyCodeUpdate.setId(sysSmsVerifyCode.getId());
			sysSmsVerifyCodeUpdate.setStatus(1);

			sysBuyerUserDto = new SysBuyerUserDto();
			PropertyUtils.copyProperties(sysBuyerUserDto, buyerUserVo);
			sysBuyerUserDto.setLoginName(mobilePhone);
			sysBuyerUserDto.setPhone(mobilePhone);
			String userId = sysBuyerUserService.addSysBuyerSync410(sysBuyerUserDto, sysSmsVerifyCodeUpdate, null);

			//Begin added by zhaoqc
			//用户创建邀请码记录
			SysUserInvitationCode invitationCode = createInvitationCode(userId);
			this.invitationCodeService.saveCode(invitationCode);
			//End added by zhaoqc
			
			resultBuyerUserVo = new BuyerUserVo();
			resultBuyerUserVo.setId(userId);
			resultBuyerUserVo.setLoginName(mobilePhone);
			resultBuyerUserVo.setPhone(mobilePhone);
			resultBuyerUserVo.setInvitationCode(invitationCode.getInvitationCode());
			// 判断用户是否第一次注册登录，如果是则返回1
			resultBuyerUserVo.setIsOneLogin("1");
		} else {
			// 查询手机用户信息
			sysBuyerUserItemDto = sysBuyerUserApi.login(mobilePhone, null);

			//查询用户邀请码
			SysUserInvitationCode invitationCode = this.invitationCodeService.findInvitationCodeByUserId(sysBuyerUserItemDto.getId(), InvitationUserType.phoneUser);
			
			resultBuyerUserVo = new BuyerUserVo();
			resultBuyerUserVo.setInvitationCode(invitationCode.getInvitationCode());
			PropertyUtils.copyProperties(resultBuyerUserVo, sysBuyerUserItemDto);
		}
		// 更新验证码状态
		sysSmsVerifyCodeService.modifyUsedStatus(sysSmsVerifyCode.getId());

		// 单点登录
		List<SysUserLoginLog> sysUserLoginLogs = sysUserLoginLogService.findAllByUserId(sysBuyerUserItemDto.getId(),
				null, null, SysUserLoginLog.CLIENT_TYPE_APP);
		singlePoint(machineCode, token, sysBuyerUserItemDto, requestMap, sysUserLoginLogs);
		requestMap.put("sysBuyerUserItemDto", sysBuyerUserItemDto);
		requestMap.put("resultBuyerUserVo", resultBuyerUserVo);
		return requestMap;
	}
	// end by wangf01 2016.07.26
	
	
	private SysUserInvitationCode createInvitationCode(String userId) {
	    SysUserInvitationCode invitationCode = new SysUserInvitationCode();
        invitationCode.setId(UuidUtils.getUuid());
        invitationCode.setSysBuyerUserId(userId);
        invitationCode.setUserType(InvitationUserType.phoneUser);
        String code = redisTemplate.boundListOps(RedisKeyConstants.MALL_RANDCODE).rightPop();
        invitationCode.setInvitationCode(code);
        invitationCode.setInvitationUserNum(0);
        invitationCode.setCreateTime(new Date());
        invitationCode.setUpdateTime(new Date());
	 
        return invitationCode;
	}
	
}