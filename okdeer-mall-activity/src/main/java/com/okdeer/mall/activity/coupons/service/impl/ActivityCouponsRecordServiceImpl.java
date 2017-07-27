package com.okdeer.mall.activity.coupons.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.okdeer.api.pay.account.dto.PayUpdateAmountDto;
import com.okdeer.api.pay.service.IPayTradeServiceApi;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.coupons.bo.ActivityRecordBo;
import com.okdeer.mall.activity.coupons.bo.ActivityRecordParamBo;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsVo;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsCategory;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRandCode;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecordBefore;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecordQueryVo;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecordVo;
import com.okdeer.mall.activity.coupons.entity.CouponsFindVo;
import com.okdeer.mall.activity.coupons.entity.CouponsStatusCountVo;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsType;
import com.okdeer.mall.activity.coupons.enums.RecordCountRuleEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivityCollectCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRandCodeMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordBeforeMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsThirdCodeMapper;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordService;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordServiceApi;
import com.okdeer.mall.activity.prize.service.ActivityPrizeRecordService;
import com.okdeer.mall.activity.service.CouponsFilterStrategy;
import com.okdeer.mall.activity.service.MaxFavourStrategy;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.common.enums.GetUserType;
import com.okdeer.mall.common.enums.UseUserType;
import com.okdeer.mall.member.service.MemberService;
import com.okdeer.mall.member.service.SysBuyerExtService;
import com.okdeer.mall.order.constant.OrderMsgConstant;
import com.okdeer.mall.order.dto.TradeOrderParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.service.TradeOrderServiceApi;
import com.okdeer.mall.order.vo.Coupons;
import com.okdeer.mall.order.vo.PushMsgVo;
import com.okdeer.mall.order.vo.PushUserVo;
import com.okdeer.mall.order.vo.RechargeCouponVo;
import com.okdeer.mall.system.entity.SysUserInvitationCode;
import com.okdeer.mall.system.mapper.SysUserInvitationCodeMapper;
import com.okdeer.mall.system.service.InvitationCodeService;
import com.okdeer.mcm.constant.MsgConstant;
import com.okdeer.mcm.entity.SmsVO;
import com.okdeer.mcm.service.ISmsService;

import net.sf.json.JSONObject;

/**
 * @DESC: 活动代金券记录
 * @author YSCGD
 * @date 2016-04-08 19:39:19
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V4.1			2016-07-04			maojj			事务控制使用注解
 *		V1.1.0			2016-9-19		wushp				各种状态代金券数量统计
 *		V1.2			 2016-11-21			tuzhd			  代金劵提醒定时任务
 *		V1.3			2016-12-19          zhulq				异业代金卷领取成功后操作
 *		V2.1			2017-02-15			maojj			增加查询用户有效的代金券记录
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordServiceApi")
class ActivityCouponsRecordServiceImpl implements ActivityCouponsRecordServiceApi, ActivityCouponsRecordService {

	private static final Logger log = LoggerFactory.getLogger(ActivityCouponsRecordServiceImpl.class);

	private static final String TOPIC = "topic_mcm_msg";
    
	@Autowired
	private RocketMQProducer rocketMQProducer;
	
	@Autowired
	private ActivityCouponsRecordMapper activityCouponsRecordMapper;
	
	@Autowired
	private ActivityCouponsRecordBeforeMapper activityCouponsRecordBeforeMapper;

	/**
	 * 代金券管理mapper
	 */
	@Autowired
	private ActivityCouponsMapper activityCouponsMapper;

	/**
	 * 代金券领取活动
	 */
	@Autowired
	private ActivityCollectCouponsMapper activityCollectCouponsMapper;

	/**
	 * 代金券随机码
	 */
	@Autowired
	private ActivityCouponsRandCodeMapper activityCouponsRandCodeMapper;
	
	/**
	 * 异业代金券兑换码
	 */
	@Autowired
	ActivityCouponsThirdCodeMapper activityCouponsThirdCodeMapper;
	
	@Autowired
	InvitationCodeService invitationCodeService;
	
	/**
	 * 邀请码mapper
	 */
	@Autowired
	private SysUserInvitationCodeMapper sysUserInvitationCodeMapper;
	
	@Reference(version = "1.0.0", check = false)
	private IPayTradeServiceApi payTradeServiceApi;
	
	/**
	 * 消息系统CODE
	 */
	@Value("${mcm.sys.code}")
	private String msgSysCode;

	/**
	 * 消息token
	 */
	@Value("${mcm.sys.token}")
	private String msgToken;

	/**取消订单短信1*/
	@Value("${sms.coupons.notice}")
	private String smsIsNoticeCouponsRecordStyle;
	
	/**
	 * 短信  service
	 */
	@Reference(version = "1.0.0")
	private ISmsService smsService;
	
	@Autowired
	private SysBuyerExtService sysBuyerExtService;
	@Autowired
	private MemberService memberService;
	//中奖记录表Service
	@Autowired
	ActivityPrizeRecordService activityPrizeRecordService;
	
	// Begin added by maojj 2017-02-15
	@Resource
	private MaxFavourStrategy genericMaxFavourStrategy;
	
	@Autowired
	private  RedisTemplate<String,Boolean> redisTemplate;
	
	/**
	 * 店铺商品Api
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;
	// End added by maojj 2017-02-15
	
	@Reference(version = "1.0.0", check = false)
	private TradeOrderServiceApi tradeOrderService;

	@Override
	@Transactional(readOnly = true)
	public PageUtils<ActivityCouponsRecordVo> getAllRecords(ActivityCouponsRecordVo activityCouponsRecordVo,
			int pageNum, int pageSize) throws ServiceException {
		PageHelper.startPage(pageNum, pageSize, true);
		//begin 重构4.1 added by zhangkn
		if(activityCouponsRecordVo.getStartTime() != null) {
			String str = DateUtils.formatDate(activityCouponsRecordVo.getStartTime(), "yyyy-MM-dd") + " 00:00:00";
			try {
				activityCouponsRecordVo.setStartTime(DateUtils.parseDate(str, "yyyy-MM-dd HH:mm:ss"));
			} catch (ParseException e) {
				log.error("getAllRecords执行异常,",e);
			} 
		}
		if(activityCouponsRecordVo.getEndTime() != null) {
			String str = DateUtils.formatDate(activityCouponsRecordVo.getEndTime(), "yyyy-MM-dd") + " 23:59:59";
			try {
				activityCouponsRecordVo.setEndTime(DateUtils.parseDate(str, "yyyy-MM-dd HH:mm:ss"));
			} catch (ParseException e) {
				log.error("getAllRecords执行异常,",e);
			} 
		}
		//end 重构4.1 added by zhangkn
		List<ActivityCouponsRecordVo> recordInfos = activityCouponsRecordMapper
				.selectAllRecords(activityCouponsRecordVo);
		if (recordInfos == null) {
			recordInfos = new ArrayList<ActivityCouponsRecordVo>();
		} else {
			List<String> recordIds = new ArrayList<String>();
			for (ActivityCouponsRecordVo vo : recordInfos) {
				recordIds.add(vo.getId());
				Calendar cal = Calendar.getInstance();
				cal.setTime(vo.getValidTime());
				cal.add(Calendar.DATE, -1); // 减1天
				vo.setValidTime(cal.getTime());
			}
/*			if (CollectionUtils.isNotEmpty(recordIds)) {
				List<ActivityCouponsRecordVo> list = activityCouponsRecordMapper.findOrderByRecordId(recordIds);
				for (ActivityCouponsRecordVo recordVo : recordInfos) {
					for (ActivityCouponsRecordVo record : list) {
						recordVo.setOrderNo(record.getOrderNo());
					}
				}
			}*/
		}
		PageUtils<ActivityCouponsRecordVo> pageUtils = new PageUtils<ActivityCouponsRecordVo>(recordInfos);
		return pageUtils;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityCouponsRecordVo> getRecordExportData(Map<String, Object> paraMap) {
		return activityCouponsRecordMapper.selectExportRecords(paraMap);
	}

	@Override
	@Transactional(readOnly = true)
	public int selectCountByParams(ActivityCouponsRecord activityCouponsRecord) throws ServiceException {
		Integer count = activityCouponsRecordMapper.selectCountByParams(activityCouponsRecord);
		if (null == count) {
			return 0;
		} else {
			return count;
		}
	}

	// begin update by　zhulq  2016-10-17  新增方法提供给之前的版本
	@Override
	@Transactional(readOnly = true)
	public List<ActivityCouponsRecordQueryVo> findMyCouponsDetailByParams(ActivityCouponsRecordStatusEnum status,
			String currentOperateUserId,Boolean flag) throws ServiceException {
		ActivityCouponsRecord activityCouponsRecord = new ActivityCouponsRecord();
		activityCouponsRecord.setStatus(status);
		activityCouponsRecord.setCollectUserId(currentOperateUserId);
		List<ActivityCouponsRecordQueryVo> voList = new ArrayList<>();
		// true  新的版本的请求
		if (flag) {
			voList = activityCouponsRecordMapper.selectMyCouponsDetailByParams(activityCouponsRecord);
		} else {	
			voList = activityCouponsRecordMapper.selectMyCouponsDetailByParamsOld(activityCouponsRecord);
		}
		if (!CollectionUtils.isEmpty(voList)) {
			for (ActivityCouponsRecordQueryVo vo : voList) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(vo.getValidTime());
				// 减1天
				cal.add(Calendar.DATE, -1); 
				vo.setValidTime(cal.getTime());
				ActivityCoupons activityCoupons = vo.getActivityCoupons();
				Integer couponsType = activityCoupons.getType();
				if (couponsType != null) {
					if (activityCoupons.getType() == 1) {
						if (activityCoupons.getIsCategory() == 1) {
							String categoryNames = "";
							List<ActivityCouponsCategory> cates = activityCoupons.getActivityCouponsCategory();
							if (cates != null) {
						    	for (ActivityCouponsCategory category : cates) {
						    		if (StringUtils.isNotBlank(categoryNames)) {
							    		categoryNames =  categoryNames + "、" ;	
						    		}	
						    		categoryNames = categoryNames + category.getCategoryName();
						    	}
						    }
							activityCoupons.setCategoryNames(categoryNames);
						}
					} else if (activityCoupons.getType() == 2) {
						if (activityCoupons.getIsCategory() == 1) {
							String categoryNames = "";
							List<ActivityCouponsCategory> cates = activityCoupons.getActivityCouponsCategory();
							if (cates != null) {
						    	for (ActivityCouponsCategory category : cates) {
						    		if (StringUtils.isNotBlank(categoryNames)) {
							    		categoryNames =  categoryNames + "、" ;	
						    		}	
						    		categoryNames = categoryNames + category.getCategoryName();						    		
						    	}
						    }
							activityCoupons.setCategoryNames(categoryNames);
						}
						
					}
				}
			}
		}
		return voList;

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject addRecordForRecevie(String couponsId, String currentOperatUserId,
			ActivityCouponsType activityCouponsType) throws ServiceException {
		Map<String, Object> map = new HashMap<String, Object>();
		//校验成功标识 //如果不存在缓存数据进行加入到缓存中
		String key =currentOperatUserId+couponsId;
		boolean checkFlag = checkUserStatusByRedis(key, 6);
		if(!checkFlag){
			map.put("code", 104);
			map.put("msg", "用户调用次数超限");
			return JSONObject.fromObject(map);
		}
		try{
			ActivityCoupons activityCoupons = activityCouponsMapper.selectByPrimaryKey(couponsId);
			// 根据数量的判断，插入代金券领取记录
			map = this.insertRecordByJudgeNum(activityCoupons, currentOperatUserId, "恭喜你，领取成功！", activityCouponsType);
		}catch(Exception e){
			throw e;
		}finally {
			//移除redis缓存的key
			removeRedisUserStatus(key);
		}

		return JSONObject.fromObject(map);

	}
	
	/**
	 * 根据活动ID领取代金劵 存在邀请人用户id需要确认是否记录邀请人
	 * @param collectId 活动id集合
	 * @param userId 用户id
	 * @param activityCouponsType 活动类型
	 * @return tuzhiding 
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public JSONObject addRecordsByCollectId(String collectId, String userId) throws Exception{
		return addRecordsByCollectId(collectId, userId, null);
	}
	/**
	 * 根据活动ID领取代金劵
	 * @param collectId 活动id集合
	 * @param userId 用户id
	 * @param activityCouponsType 活动类型
	 * @param invitaUserId 邀请人用户id
	 * @return tuzhiding 
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject addRecordsByCollectId(String collectId, String userId,String invitaUserId) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		//校验成功标识 //如果不存在缓存数据进行加入到缓存中
		String key = userId+collectId;
		boolean checkFlag = checkUserStatusByRedis(key, 6);
		if(!checkFlag){
			map.put("code", 104);
			map.put("msg", "用户调用次数超限");
			return JSONObject.fromObject(map);
		}
		
		try{
			ActivityCollectCoupons coll = activityCollectCouponsMapper.get(collectId);
			if(coll == null){
				map.put("msg", "非法参数！");
				map.put("code", 105);
				return JSONObject.fromObject(map);
			}
			//查询该用户已领取， 新人限制， 未使用，的代金劵活动的代金劵数量 
			if(checkNewUserCoupons(userId, coll)){
				map.put("msg", "您已经领取了，快去我的代金券查看使用吧！");
				map.put("code", 102);
				return JSONObject.fromObject(map);
			}
			
			//校验活动信息
			if(!checkCollectPublic(map, coll, userId)){
				return JSONObject.fromObject(map);
			}
			
			//循环代金劵id进行送劵
			List<ActivityCoupons> activityCoupons = activityCouponsMapper.selectByActivityId(collectId);
			Map<String,ActivityCouponsRecord> reMap = new HashMap<String,ActivityCouponsRecord>();
			
			//根据代金劵列表逐个领取，当出现一个代金劵领取异常即反馈错误给前端
			for(ActivityCoupons coupons : activityCoupons){
				// 设置代金券领取记录的代金券id、代金券领取活动id、活动类型，以便后面代码中的数量判断查询
				ActivityCouponsRecord record = new ActivityCouponsRecord();
				record.setCollectType(ActivityCouponsType.enumValueOf(coll.getType()));
				//进行公共代金劵领取校验
				if (!checkRecordPubilc(map, coupons,userId,record,coll)) {
					checkFlag = false;
					break;
				}
				//检验优惠码的领取
				if (!checkExchangeCode(map, userId, record, coupons)) {
					checkFlag = false;
					break;
				}
				record.setCollectUserId(userId);
				reMap.put(coupons.getId(), record);
				checkFlag = true;
			}
			//判断是否是成功，成功则进行批量保存代金劵
			if(checkFlag){
				//循环进行代金劵插入
				for(ActivityCoupons coupons : activityCoupons){
					updateCouponsRecode(reMap.get(coupons.getId()), coupons);
				}
				
				//存在邀请人用户id需要确认是否记录邀请人
				if(StringUtils.isNotBlank(invitaUserId)){
					//根据用户id或用户邀请码，所以InviteUserId 可以存储用户id或邀请码
					List<SysUserInvitationCode> listCode= sysUserInvitationCodeMapper
							.findInvitationByIdCode(invitaUserId, invitaUserId);
					//存在邀请码及添加第一个进去，防止数据库中存在多个
					if(listCode != null && listCode.size() > 0){
						//内部会判断 不存在邀请记录 则将给该用户userId记录 邀请记录
						invitationCodeService.saveInvatationRecord(listCode.get(0), userId, "");
					}
				}
				map.put("code", 100);
				map.put("msg", "恭喜你，领取成功！");
			}
		}catch(Exception e){
			throw e;
		}finally {
			//移除redis缓存的key
			removeRedisUserStatus(key);
		}
		return JSONObject.fromObject(map);

	}
	/**
	 * @DESC 校验预领取记录
	 * 1、存在未使用的新人代金券则 不能领取返回false
	 * 2、持续的活动领取过不能再领取
	 * @param phone 手机号
	 * @param collectId 代金券活动id
	 * @return
	 */
	private boolean checkNewUserCoupons(String userId,ActivityCollectCoupons coll){
		//如果领取为限新人使用 则校验是否领取过新人代金券
		if(coll.getGetUserType() == GetUserType.ONlY_NEW_USER){
			//查询该用户已领取， 新人限制， 未使用，的代金劵活动的代金劵数量 
			int countCoupons = activityCouponsRecordMapper.findcountByNewType(GetUserType.ONlY_NEW_USER, userId, 
																		ActivityCouponsRecordStatusEnum.UNUSED);
			//存在未使用的新人代金券则 返回true
			return countCoupons > 0;
		}
		return false;
	}
	
	/**
	 * 根据活动ID及用户手机号码领取代金劵
	 * @param collectId 活动id集合
	 * @param phone 用户手机号码
	 * @param activityCouponsType 活动类型
	 * @param userId 	邀请的用户id 没有null
	 * @param advertId 	H5活动id
	 * @return tuzhiding 
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject addBeforeRecords(String collectId, String phone,String userId,String advertId) throws ServiceException {
		Map<String, Object> map = new HashMap<String, Object>();
		
		//校验成功标识 //如果不存在缓存数据进行加入到缓存中
		String key = userId+collectId;
		boolean checkFlag = checkUserStatusByRedis(key, 6);
		if(!checkFlag){
			map.put("code", 104);
			map.put("msg", "用户调用次数超限");
			return JSONObject.fromObject(map);
		}
		//校验成功标识
		try{
			ActivityCollectCoupons coll = activityCollectCouponsMapper.get(collectId);
			if(coll == null){
				map.put("msg", "非法参数！");
				map.put("code", 105);
				return JSONObject.fromObject(map);
			}
			
			//校验活动信息
			if(!checkCollectPublic(map, coll, userId)){
				return JSONObject.fromObject(map);
			}
			
			//循环代金劵id进行送劵
			List<ActivityCoupons> activityCoupons = activityCouponsMapper.selectByActivityId(collectId);
			Map<String,ActivityCouponsRecordBefore> reMap = new HashMap<String,ActivityCouponsRecordBefore>();
			//根据用户手机号码及活动id查询该号码是否领取过 
			if (checkBeforeCoupons(phone, coll)) {
				map.put("code", 102);
				map.put("msg", "您已经领取了，快去友门鹿app注册使用吧！");
				checkFlag = false;
			}else{
				//根据代金劵列表逐个领取，当出现一个代金劵领取异常即反馈错误给前端
				for(ActivityCoupons coupons : activityCoupons){
					// 设置代金券领取记录的代金券id、代金券领取活动id、活动类型，以便后面代码中的数量判断查询
					ActivityCouponsRecordBefore record = new ActivityCouponsRecordBefore();
					record.setCollectType(ActivityCouponsType.enumValueOf(coll.getType()));
					//进行公共代金劵领取校验
					if (!checkRecordPubilc(map, coupons,null,record,coll)) {
						checkFlag = false;
						break;
					}
					//预领取记录中 手机号码 、 邀请人id 、广告活动id
					record.setCollectUser(phone);
					record.setInviteUserId(userId);
					record.setActivityId(advertId);
					record.setIsComplete(WhetherEnum.not);
					reMap.put(coupons.getId(), record);
					checkFlag = true;
				}
			}
			//判断是否是成功，成功则进行批量保存代金劵
			if(checkFlag){
				//循环进行代金劵插入
				for(ActivityCoupons coupons : activityCoupons){
					insertRecodeBefore(reMap.get(coupons.getId()), coupons);
				}
				map.put("code", 100);
				map.put("msg", "恭喜你，领取成功！");
			}
		}catch(Exception e){
			throw e;
		}finally {
			//移除redis缓存的key
			removeRedisUserStatus(key);
		}
		return JSONObject.fromObject(map);

	}
	
	/**
	 * @DESC 校验预领取记录
	 * 1、存在未使用的新人代金券则 不能领取返回false
	 * 2、持续的活动领取过不能再领取
	 * @param phone 手机号
	 * @param collectId 代金券活动id
	 * @return
	 */
	private boolean checkBeforeCoupons(String phone,ActivityCollectCoupons coll){
		//如果领取为限新人使用 则校验是否领取过新人代金券
		if(coll.getGetUserType() == GetUserType.ONlY_NEW_USER){
			//查询该用户已领取， 新人限制， 未使用，的代金劵活动的代金劵数量 
			int hadNewCount = activityCouponsRecordBeforeMapper.countCouponsByType(GetUserType.ONlY_NEW_USER,phone,new Date());
			//存在未使用的新人代金券则 返回true
			if(hadNewCount > 0){
				return true;
			}
		}
		//根据代金劵活动id代金劵预领取统计 持续的活动领取过不能再领取
		return activityCouponsRecordBeforeMapper.countCouponsAllId(phone, coll.getId()) > 0;
	}
	
	/**
	 * 注册送完代金劵后将预代金劵送到用户的账户中
	 *  @param userId 用户id
	 */
	public void insertCopyRecords(String userId,String phone,String machineCode){
		try{
			List<ActivityCouponsRecordBefore> list = activityCouponsRecordBeforeMapper.getCopyRecords(userId,new Date(),phone);
			if(list != null && list.size() > 0 ){
				activityCouponsRecordMapper.insertBatchRecordByBefore(list);
				
				//循环代金券记录，找出最后一次有效的邀请人领取记录
				ActivityCouponsRecordBefore  lastR = null;
				for(ActivityCouponsRecordBefore record : list){
					if(StringUtils.isBlank(record.getInviteUserId())){
						continue;
					}
					//第一个有邀请人 的预领取记录记录到 临时对象中 //当后面的预领取记录记录 领取时候大 已后面的领取邀请人为准
					if(lastR == null || (record.getCollectTime().getTime() > lastR.getCollectTime().getTime())){
						lastR = record;
					}
				}
				//存在记录 说明该用户存在 邀请人，添加邀请人记录
				if(lastR != null){
					//根据用户id或用户邀请码，所以InviteUserId 可以存储用户id或邀请码
					List<SysUserInvitationCode> listCode= sysUserInvitationCodeMapper
							.findInvitationByIdCode(lastR.getInviteUserId(), lastR.getInviteUserId());
					//存在邀请码及添加第一个进去，防止数据库中存在多个
					if(listCode != null && listCode.size() > 0){
						invitationCodeService.saveInvatationRecord(listCode.get(0), userId, machineCode);
					}
				}
				
			}
		}catch (Exception e) {
			//捕获异常不影响注册流程
			log.error("将预代金劵送到用户的账户中出现异常！",e);
		}
	}
	 
	/**
	 * DESC: 领取活动优惠券
	 * 
	 * @author LIU.W
	 * @param lstActivityCoupons
	 * @param activityCouponsType
	 * @param userId
	 * @throws ServiceException
	 */
	public void drawCouponsRecord(List<ActivityCoupons> lstActivityCoupons, ActivityCouponsType activityCouponsType,
			String userId) throws ServiceException {
		if (CollectionUtils.isEmpty(lstActivityCoupons)) {
			return;
		}
		//校验成功标识 //如果不存在缓存数据进行加入到缓存中
		String key = "drawCoupons"+userId;
		boolean checkFlag = checkUserStatusByRedis(key, 6);
		if(!checkFlag){
			return;
		}
		try{

			List<ActivityCouponsRecord> lstCouponsRecords = new ArrayList<ActivityCouponsRecord>();
			List<String> lstActivityCouponsIds = new ArrayList<String>();
			for (ActivityCoupons activityCoupons : lstActivityCoupons) {
				int remainNum = activityCoupons.getRemainNum();// 剩余总数量
				if (remainNum <= 0) {
					continue;
				}
				// 设置代金券领取记录的代金券id、代金券领取活动id、活动类型，以便后面代码中的数量判断查询
				ActivityCouponsRecord activityCouponsRecord = new ActivityCouponsRecord();
				activityCouponsRecord.setId(UuidUtils.getUuid());
				activityCouponsRecord.setCollectType(activityCouponsType);
				activityCouponsRecord.setCouponsId(activityCoupons.getId());
				activityCouponsRecord.setCouponsCollectId(activityCoupons.getActivityId());
				activityCouponsRecord.setCollectTime(new Date());
				activityCouponsRecord.setCollectUserId(userId);
				activityCouponsRecord.setValidTime(DateUtils.addDays(new Date(), activityCoupons.getValidDay()));
				activityCouponsRecord.setStatus(ActivityCouponsRecordStatusEnum.UNUSED);
	
				lstCouponsRecords.add(activityCouponsRecord);
				// 更新代金券已使用数量和剩余数量
				lstActivityCouponsIds.add(activityCoupons.getId());
			}
	
			addActivityCouponsRecord(lstCouponsRecords, lstActivityCouponsIds);
		}catch(Exception e){
			throw e;
		}finally {
			//移除redis缓存的key
			removeRedisUserStatus(key);
		}
	}

	/**
	 * DESC: 添加代金券
	 * 
	 * @author LIU.W
	 * @throws ServiceException
	 */
	@Transactional(rollbackFor = Exception.class)
	public void addActivityCouponsRecord(List<ActivityCouponsRecord> lstCouponsRecords,
			List<String> lstActivityCouponsIds) throws ServiceException {

		try {
			// 批量插入代金券
			if (!CollectionUtils.isEmpty(lstCouponsRecords)) {
				activityCouponsRecordMapper.insertSelectiveBatch(lstCouponsRecords);
			}
			// 更新可使用的
			if (!CollectionUtils.isEmpty(lstActivityCouponsIds)) {
				for (String activityCouponId : lstActivityCouponsIds) {
					int count = activityCouponsMapper.updateRemainNum(activityCouponId);
					if (count == 0) {
						throw new Exception("添加代金卷记录失败!");
					}
				}
			}

		} catch (Exception e) {
			throw new ServiceException("", e);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject addRecordForExchangeCode(Map<String, Object> params, String exchangeCode,
			String currentOperatUserId, ActivityCouponsType activityCouponsType) throws ServiceException {
		Map<String, Object> map = new HashMap<String, Object>();
		//校验成功标识 //如果不存在缓存数据进行加入到缓存中
		String key = "exchangeCode"+currentOperatUserId+exchangeCode;
		boolean checkFlag = checkUserStatusByRedis(key, 6);
		if(!checkFlag){
			map.put("code", 104);
			map.put("msg", "用户调用次数超限");
			return JSONObject.fromObject(map);
		}
		try{
			List<ActivityCollectCouponsVo> result = activityCollectCouponsMapper.selectByStoreAndLimitType(params);
			// 判断输入的优惠码是否正确
			if (result != null && result.size() > 0) {
				ActivityCoupons activityCoupons = result.get(0).getActivityCoupons().get(0);
				// 根据数量的判断，插入代金券领取记录
				map = this.insertRecordByJudgeNum(activityCoupons, currentOperatUserId, "恭喜你，优惠券兑换成功！",
						activityCouponsType);
			} else {
				map.put("msg", "您输入的抵扣券优惠码错误！");
				map.put("code", 103);
			}
		}catch(Exception e){
			throw e;
		}finally {
			//移除redis缓存的key
			removeRedisUserStatus(key);
		}
		return JSONObject.fromObject(map);
	}
	
	/**
	 * 根据随机码进行领取代金劵
	 * @param activityCoupons 活动信息
	 * @param userId 用户id
	 * @param successMsg 成功提示语
	 * @param activityCouponsType  活动类型
	 * @author zhulq
	 * @date 2016年10月26日
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> insertRecordByRandCode(ActivityCoupons activityCoupons, String userId,
			String successMsg, ActivityCouponsType activityCouponsType) {
		Map<String, Object> map = new HashMap<String, Object>();
		//校验成功标识 //如果不存在缓存数据进行加入到缓存中
		String key = userId+activityCoupons.getActivityId();
		boolean checkFlag = checkUserStatusByRedis(key, 6);
		if(!checkFlag){
			map.put("code", 104);
			map.put("msg", "用户调用次数超限");
			return map;
		}
		try{
			// 设置代金券领取记录的代金券id、代金券领取活动id、活动类型，以便后面代码中的数量判断查询
			ActivityCouponsRecord record = new ActivityCouponsRecord();
			record.setCollectType(activityCouponsType);
			ActivityCollectCoupons collect = activityCollectCouponsMapper
					.get(activityCoupons.getActivityId());
			//校验活动信息
			if(!checkCollectPublic(map, collect, userId)){
				return map;
			}
			//进行公共代金劵领取校验
			if (!checkRecordPubilc(map, activityCoupons,userId,record,collect)) {
				return map;
			}
			//检验随机码是否可以领取
			if (!checkRandCode(map, userId, record, activityCoupons)) {
				return map;
			}
			//更新保存领取代金劵记录
			updateCouponsRecode(record, activityCoupons);
			//更新随机码表记录
			updateRandCode(activityCoupons.getRandCode(), userId);
			
			map.put("code", 100);
			map.put("msg", successMsg);
		}catch(Exception e){
			throw e;
		}finally {
			//移除redis缓存的key
			removeRedisUserStatus(key);
		}
		return map;
	}
	
	/**
	 * 判断当前登陆用户领取的指定代金券数量是否已经超过限领数量，所有用户领取的指定代金券总数量是否已经超过代金券的总发行数量，否则，插入代金券领取记录
	 * @param activityCoupons 活动信息
	 * @param userId 用户id
	 * @param successMsg 成功提示语
	 * @param activityCouponsType  活动类型
	 * @author zhulq
	 * @date 2016年10月26日
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> insertRecordByJudgeNum(ActivityCoupons activityCoupons, String userId,
			String successMsg, ActivityCouponsType activityCouponsType) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 设置代金券领取记录的代金券id、代金券领取活动id、活动类型，以便后面代码中的数量判断查询
		ActivityCouponsRecord record = new ActivityCouponsRecord();
		record.setCollectType(activityCouponsType);
		ActivityCollectCoupons collect = activityCollectCouponsMapper
				.get(activityCoupons.getActivityId());
		
		//校验活动信息
		if(!checkCollectPublic(map, collect, userId)){
			return map;
		}
		//进行公共代金劵领取校验
		if (!checkRecordPubilc(map, activityCoupons,userId,record,collect)) {
			return map;
		}
		//检验优惠码的领取
		if (!checkExchangeCode(map, userId, record, activityCoupons)) {
			return map;
		}
		record.setCollectUserId(userId);
		//更新保存领取代金劵记录
		updateCouponsRecode(record, activityCoupons);
		//map.put("data", null);
		map.put("msg", successMsg);
		map.put("code", 0);
		return map;
	}
	
	/**
	 * @Description: 进行公共代金劵活动校验
	 * @param map 返回值
	 * @param collect 活动对象
	 * @param userId 用户id
	 * @return boolean  
	 * @author tuzhd
	 * @date 2017年6月30日
	 */
	private boolean checkCollectPublic(Map<String, Object> map,ActivityCollectCoupons collect,String userId){
		if (collect == null || collect.getStatus().intValue() != 1) {
			map.put("msg", "活动已结束！");
			map.put("code", 105);
			return false;
		}
		//根据用户id查询其订单完成的订单总量 大于 0 下过单 就不算新用户
    	if(collect.getGetUserType() == GetUserType.ONlY_NEW_USER){
    		TradeOrderParamDto param =new TradeOrderParamDto();
    		param.setUserId(userId);
	    	//如果用户存在单没有下过单一样送券
    		if(userId != null && tradeOrderService.selectCountByUserStatus(param) > 0){
    	    	map.put("code", 106);
    			map.put("msg", "该活动为新用户专享！");
    			return false;
    		}
    	}
    	
    	return true;
	}
	
	/**
	 * @Description: 进行公共代金劵领取校验
	 * @param map 返回结果
	 * @param activityCoupons 活动信息
	 * @param userId 用户id
	 * @param activityCouponsType 活动类型
	 * @param record 代金劵记录对象
	 * @author zhulq
	 * @date 2016年10月26日
	 */
	private boolean checkRecordPubilc(Map<String, Object> map,ActivityCoupons activityCoupons,
			 String  userId,ActivityCouponsRecord record,ActivityCollectCoupons collect) {
		if (activityCoupons.getRemainNum() <= 0) {
			// 剩余数量小于0 显示已领完
			map.put("msg", "该代金券已经领完了！");
			map.put("code", 101);
			return false;
		}
		Date collectTime = DateUtils.getDateStart(new Date());
		record.setCouponsId(activityCoupons.getId());
		record.setCouponsCollectId(activityCoupons.getActivityId());
		record.setCollectUserId(userId);
		record.setCollectTime(collectTime);	
		//校验代金卷的日领取量
		if (!checkDaliyRecord(map, record, activityCoupons,userId,collect)) {
			return false;
		}
		return true;
	}
	
	/**
	 * @Description: 更新随机码表记录
	 * @param radeCode 随机码 
	 * @param userId 用户id
	 * @author zhulq
	 * @date 2016年10月26日
	 */
	private void updateRandCode(String radeCode,String userId) {
		//如果领取成功更新随机码表
		if (StringUtils.isNotBlank(radeCode)) {
			ActivityCouponsRandCode couponsRandCode	= activityCouponsRandCodeMapper.selectByRandCode(radeCode);
			if (couponsRandCode != null) {
				couponsRandCode.setIsExchange(1);
				couponsRandCode.setUpdateTime(new Date());
				couponsRandCode.setUpdateUserId(userId);
				activityCouponsRandCodeMapper.updateByPrimaryKey(couponsRandCode);
			}
		}
	}
	
	/**
	 * @Description: 更新保存领取代金劵记录
	 * @param record 代金劵信息
	 * @param coupons 活动信息
	 * @author zhulq
	 * @date 2016年10月26日
	 */
	private void updateCouponsRecode(ActivityCouponsRecord record,ActivityCoupons coupons) {
		// 立即领取
		record.setId(UuidUtils.getUuid());
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 
				calendar.get(Calendar.DATE), 0,	0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		record.setCollectTime(calendar.getTime());
		record.setStatus(ActivityCouponsRecordStatusEnum.UNUSED);
		calendar.add(Calendar.DAY_OF_YEAR, coupons.getValidDay());
		record.setValidTime(calendar.getTime());
		// begin v1.3 领取异业代金券时候操作
/*		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("couponsId", coupons.getId());
		map.put("status", 0);
		map.put("length", 1);
		List<ActivityCouponsThirdCode> thirdCodeList = activityCouponsThirdCodeMapper.listByParam(map);
		if (thirdCodeList != null && thirdCodeList.size() > 0) {
			ActivityCouponsThirdCode activityCouponsThirdCode = thirdCodeList.get(0);
			if (activityCouponsThirdCode != null) {
				String code = activityCouponsThirdCode.getCode();
				record.setThridCode(code);
				activityCouponsThirdCode.setStatus(1);
				//更新代金卷兑换码状态
				activityCouponsThirdCodeMapper.update(activityCouponsThirdCode);
			}
		}*/
		//将代金卷兑换码写入记录表  
		activityCouponsRecordMapper.insertSelective(record);
		activityCouponsMapper.updateRemainNum(coupons.getId());
		//begin add by zhulq 2016-12-19 异业代金卷领取成功操作
		
	}
	
	/**
	 * @Description: 更新保存预领取代金劵记录
	 * @param record 代金劵信息
	 * @param coupons 活动信息
	 * @author tuzhiding
	 * @date 2016年10月26日
	 */
	private void insertRecodeBefore(ActivityCouponsRecordBefore record,ActivityCoupons coupons) {
		// 立即领取
		record.setId(UuidUtils.getUuid());
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 
				calendar.get(Calendar.DATE), 0,	0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		record.setCollectTime(calendar.getTime());
		record.setStatus(ActivityCouponsRecordStatusEnum.UNUSED);
		calendar.add(Calendar.DAY_OF_YEAR, coupons.getValidDay());
		record.setValidTime(calendar.getTime());

		activityCouponsRecordBeforeMapper.insertSelective(record);
		activityCouponsMapper.updateRemainNum(coupons.getId());
	}
	
	/**
	 * @Description:  校验代金卷的日领取量
	 * @param map 返回结果
	 * @param record 代金卷记录对象
	 * @param collect 活动信息
	 * @author zhulq
	 * @date 2016年10月26日
	 */
	private boolean checkDaliyRecord(Map<String, Object> map,ActivityCouponsRecord record,
								ActivityCoupons coupons,String userId,ActivityCollectCoupons collect) {
		
		// 当前日期已经领取的数量
		int dailyCirculation = activityCouponsRecordMapper.selectCountByParams(record);
		// 当前代金劵日已经预领取领取的数量
		int dailyBefore = activityCouponsRecordBeforeMapper.getCountByDayParams(record);
		dailyCirculation = dailyCirculation + dailyBefore;
		if(collect.getDailyCirculation() != null){
			// 获取当前登陆用户已领取的指定代金券数量
			if (dailyCirculation >= Integer.parseInt(collect.getDailyCirculation())) {
				//map.put("data", null);
				map.put("msg", "来迟啦！券已抢完，明天早点哦");
				map.put("code", 104);
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @Description: 检验优惠码的领取
	 * @param map 返回结果
	 * @param userId 用户id
	 * @param record 代金卷记录对象
	 * @param coupons 代金卷信息
	 * @author zhulq
	 * @date 2016年10月26日
	 */
	private boolean checkExchangeCode(Map<String, Object> map,String userId,
			ActivityCouponsRecord record,ActivityCoupons coupons) {
		if (!StringUtils.isEmpty(userId)) {
			record.setCollectUserId(userId);
			record.setCollectTime(null);
			int currentRecordCount = activityCouponsRecordMapper.selectCountByParams(record);
			if (currentRecordCount >= coupons.getEveryLimit().intValue()) {
				// 已领取
				//map.put("data", null);
				map.put("msg", "每人限领" + coupons.getEveryLimit() + "张，不要贪心哦！");
				map.put("code", 102);
				return false;
			}
		} else {
			//map.put("data", null);
			map.put("msg", "请登录后再领取");
			map.put("code", 4);
			return false;
		}
		return true;
	}
	
	/**
	 * @Description: 检验优惠码的领取
	 * @param map 返回结果
	 * @param userId 用户id
	 * @param record 代金卷记录对象
	 * @param coupons 代金卷信息
	 * @author zhulq
	 * @date 2016年10月26日
	 */
	private boolean checkRandCode(Map<String, Object> map,String userId,
			ActivityCouponsRecord record,ActivityCoupons coupons) {
		if (!StringUtils.isEmpty(userId)) {
			//判断该随机码的代金卷是否已经被领取了
			int count = activityCouponsRecordMapper.selectCountByRandCode(coupons.getRandCode());
			if (count >= 1) {
				// 已领取
				map.put("code", 106);
				map.put("msg", "相同随机码的代金券已经被领取了!");
				return false;
			}
			//判断 该用户是否还能领卷该类代金卷
			record.setCollectUserId(userId);
			record.setCollectTime(null);
			int currentRecordCount = activityCouponsRecordMapper.selectCountByParams(record);
			if (currentRecordCount >= coupons.getEveryLimit().intValue()) {
				// 已领取
				map.put("code", 102);
				map.put("msg", "每人限领" + coupons.getEveryLimit() + "张，不要贪心哦！!");
				return false;
			}
		} else {
			map.put("code", 104);
			map.put("msg", "请登录后再领取");
			return false;
		}
		return true;
	}

	@Override
	public List<ActivityCouponsRecordQueryVo> selectCouponsDetailByStoreId(ActivityCouponsRecord activityCouponsRecord)
			throws ServiceException {

		List<ActivityCouponsRecordQueryVo> couponsList = new ArrayList<ActivityCouponsRecordQueryVo>();
		List<ActivityCouponsRecordQueryVo> couponsAllList = new ArrayList<ActivityCouponsRecordQueryVo>();
		couponsAllList = activityCouponsRecordMapper.selectCouponsAllId(activityCouponsRecord);
		couponsList = activityCouponsRecordMapper.selectCouponsDetailByStoreId(activityCouponsRecord);
		List<ActivityCouponsRecordQueryVo> couponsAllLists = new ArrayList<ActivityCouponsRecordQueryVo>();

		couponsAllLists.addAll(couponsAllList);
		couponsAllLists.addAll(couponsList);

		return couponsAllLists;
	}

	@Override
	public ActivityCoupons selectCouponsItem(CouponsFindVo couponsFindVo) throws Exception {
		ActivityCoupons coupons = activityCouponsRecordMapper.selectCouponsItem(couponsFindVo);
		return coupons;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateStatusByJob() throws Exception {
		List<ActivityCouponsRecord> list = activityCouponsRecordMapper.selectAllForJob();
		List<String> ids = new ArrayList<>();
		Date date = new Date(); /* date.compareTo(anotherDate) */
		if (list != null && list.size() > 0) {
			for (ActivityCouponsRecord record : list) {
				Date validTime = record.getValidTime();
				int res = date.compareTo(validTime);
				if ((res == 0) || (res == 1)) {
					ids.add(record.getId());
				}
			}
		}

		if (ids != null && ids.size() > 0) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ids", ids);
			params.put("status", ActivityCouponsRecordStatusEnum.EXPIRES);
			activityCouponsRecordMapper.updateAllByBatch(params);
		}
	}

	// 活动已经关闭 、 已经结束（未被使用的）
	@Transactional(rollbackFor = Exception.class)
	public void updateRefundStatus(List<ActivityCouponsRecordVo> couponsRecordVoList, String id) throws Exception {
		BigDecimal faceValueTotal = new BigDecimal("0");
		PayUpdateAmountDto freeDto = new PayUpdateAmountDto();
		for (ActivityCouponsRecordVo couponsRecordVo : couponsRecordVoList) {
			Integer value = couponsRecordVo.getFaceValue();
			BigDecimal faceValue = new BigDecimal(value);
			faceValueTotal = faceValueTotal.add(faceValue);
			freeDto.setUserId(couponsRecordVo.getCreateUserId());
			// 算出该代金卷 所有的金额
		}
		freeDto.setAmount(faceValueTotal);
		// 将活动改为 领取未使用已经退款 状态为2
		activityCollectCouponsMapper.updateRefundTypeByVo(id);
//		BaseResultDto result = payTradeServiceApi.unfreezeAmount(freeDto);
//		if (result != null && !"0".equals(result.getCode())) {
//			throw new Exception(result.getMsg());
//		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateUseStatus(String orderId) {

		Map<String, Object> params = Maps.newHashMap();
		params.put("orderId", orderId);
		List<ActivityCouponsRecord> records = activityCouponsRecordMapper.selectByParams(params);
		if (records != null && records.size() == 1) {
			if (records.get(0).getValidTime().compareTo(DateUtils.getSysDate()) > 0) {
				activityCouponsRecordMapper.updateUseStatus(orderId);
			} else {
				activityCouponsRecordMapper.updateUseStatusAndExpire(orderId);
			}
			activityCouponsMapper.updateReduceUseNum(records.get(0).getCouponsId());
		}

	}

	@Override
	public ActivityCouponsRecord selectByPrimaryKey(String id) {
		return activityCouponsMapper.selectByPrimaryKey(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateActivityCouponsStatus(Map<String, Object> params) {
		activityCouponsRecordMapper.updateActivityCouponsStatus(params);

	}
	
	//begin add by wushp 20160919 V1.1.0
	@Transactional(readOnly = true)
	@Override
	public List<CouponsStatusCountVo> findStatusCountByUserId(String userId) {
		return activityCouponsRecordMapper.findStatusCountByUserId(userId);
	}
	//end add by wushp 20160919 V1.1.0

    @Override
    public List<RechargeCouponVo> findValidRechargeCoupons(Map<String, Object> params) {
        List<RechargeCouponVo> couponVos = activityCouponsRecordMapper.findValidRechargeCoupons(params);
        
        //限制设备使用次数的代金券Id
        List<String> deviceLimitCouponIds = Lists.newArrayList();
        //限制账号使用次数的代金券Id
        List<String> accountLimitCouponIds = Lists.newArrayList();
        for(RechargeCouponVo couponVo : couponVos) {
            if(couponVo.getDeviceDayLimit() != null && couponVo.getDeviceDayLimit() > 0) {
                deviceLimitCouponIds.add(couponVo.getCouponId());
            }
            if(couponVo.getAccountDayLimit() != null && couponVo.getAccountDayLimit() > 0) {
                accountLimitCouponIds.add(couponVo.getCouponId());
            }
        }
        
        ActivityRecordParamBo recParamBo = null;
        List<ActivityRecordBo> recordBoList = null;
        FavourParamBO paramBo = new FavourParamBO();
        String machineCode = params.get("machineCode").toString();
        String userId = params.get("userId").toString();
        if(CollectionUtils.isNotEmpty(deviceLimitCouponIds) && StringUtils.isNotEmpty(machineCode)) {
            //根据设备统计使用次数
            recParamBo = new ActivityRecordParamBo();
            recParamBo.setPkIdList(deviceLimitCouponIds);
            recParamBo.setRecDate(DateUtils.getDate());
            recParamBo.setDeviceId(machineCode);
            recordBoList =  activityCouponsRecordMapper.countActivityRecord(recParamBo);
            paramBo.putActivityCounter(RecordCountRuleEnum.COUPONS_BY_DEVICE, recordBoList);
        }
        
        if(CollectionUtils.isNotEmpty(accountLimitCouponIds) && StringUtils.isNotEmpty(machineCode)) {
            //根据账号统计使用次数
            recParamBo = new ActivityRecordParamBo();
            recParamBo.setPkIdList(accountLimitCouponIds);
            recParamBo.setRecDate(DateUtils.getDate());
            recParamBo.setUserId(userId);
            recordBoList = activityCouponsRecordMapper.countActivityRecord(recParamBo);
            paramBo.putActivityCounter(RecordCountRuleEnum.COUPONS_BY_USER, recordBoList);
        }
        
        //检验优惠券的设备和账号限制
        Iterator<RechargeCouponVo> it = couponVos.iterator();
        while(it.hasNext()) {
            RechargeCouponVo couponVo = it.next();
            boolean result = checkCouponUseLimit(paramBo, RecordCountRuleEnum.COUPONS_BY_DEVICE, couponVo);
            if(result) {
                result = checkCouponUseLimit(paramBo, RecordCountRuleEnum.COUPONS_BY_USER, couponVo);
                if(!result) {
                    it.remove();
                }
            } else {
                it.remove();
            }
        }
        return couponVos;
    }

    /**
     * 验证优惠券的使用限制
     * @param paramBo
     * @param rule
     * @param couponVo
     * @author zhaoqc
     * @return
     */
    private boolean checkCouponUseLimit(FavourParamBO paramBo, RecordCountRuleEnum rule, 
            RechargeCouponVo couponVo) {
        if(rule == RecordCountRuleEnum.COUPONS_BY_DEVICE) {
            if(couponVo.getDeviceDayLimit() != null && couponVo.getDeviceDayLimit() > 0) {
                if(couponVo.getDeviceDayLimit().compareTo(paramBo.findCountNum(rule, couponVo.getCouponId())) < 1) {
                   return false; 
                }
            }
        } else if(rule == RecordCountRuleEnum.COUPONS_BY_USER) {
            if(couponVo.getAccountDayLimit() != null && couponVo.getAccountDayLimit() > 0) {
                if(couponVo.getAccountDayLimit().compareTo(paramBo.findCountNum(rule, couponVo.getCouponId())) < 1) {
                    return false;
                }
            }
        }
        return true;
    }
    
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void insertSelective(ActivityCouponsRecord couponsRecord) throws Exception {
		activityCouponsRecordMapper.insertSelective(couponsRecord);
	}

	@Override
	public List<ActivityCouponsRecord> selectActivityCouponsRecord(ActivityCouponsRecord couponsRecord) throws Exception {
		List<ActivityCouponsRecord> record = activityCouponsRecordMapper.selectAllRecordsByUserId(couponsRecord);
		return record;
	}

	@Override
	public JSONObject addRecordForRandCode(Map<String, Object> params, String randCode, String currentOperatUserId,
			ActivityCouponsType activityCouponsType) throws ServiceException {
		Map<String, Object> map = new HashMap<String, Object>();
		//如果随机码为空即不进行检验
		String radeCode = (String)params.get("randCode");
		if (StringUtils.isNotBlank(radeCode)) {
			List<ActivityCollectCouponsVo> result = activityCollectCouponsMapper.selectRandCodeVoucher(params);
			// 判断输入的随机码是否正确
			if (result != null && result.size() > 0) {
				ActivityCoupons activityCoupons = result.get(0).getActivityCoupons().get(0);
				activityCoupons.setRandCode(radeCode);
				// 根据数量的判断，插入代金券领取记录
				map = this.insertRecordByRandCode(activityCoupons, currentOperatUserId, "恭喜你，优惠券兑换成功！",
						activityCouponsType);
			} else {
				map.put("code", 103);
				map.put("msg", "您输入的抵扣券优惠码错误！");
			}
		} else {
			map.put("code", 103);
			map.put("msg", "您输入的抵扣券优惠码错误！");
		}
		
		return JSONObject.fromObject(map);
	}

	/**
	 * @Description: * 1、当代金券设置的领取后的有效期大于3天，则在代金券结束前第三天发送；2、当代金券设置的领取后的有效期大于1天小于或等于3天，
	 * 则在代金券的有效期最后一天发送；当代金券设置的领取后的有效期等于1天，则不会发送推送和短线
	 * @return List<String>  
	 * @author tuzhd
	 * @date 2016年11月21日
	 */
	private List<Map<String,Object>> getIsNoticeUser(){
		return activityCouponsRecordMapper.getIsNoticeUser();
	}
	
	/**
	 * @Description: 执行代金劵提醒JOB   
	 * @return void  
	 * @throws
	 * @author tuzhd
	 * @date 2016年11月21日
	 */
	public void procesRecordNoticeJob(){
		List<Map<String,Object>> list = getIsNoticeUser();
		//存在需要提醒的对象
		List<PushUser> pushUsers = Lists.newArrayList();
		if(CollectionUtils.isNotEmpty(list)){
			list.forEach(user->pushUsers.add(new PushUser((String)user.get("id"), (String)user.get("phone"))));
			//按产品及运营的要求屏蔽代金券到期提醒短信功能
			/*pushUsers.forEach(user->{
				sendMsg(user.phone);
			});*/
			sendPosMessage(pushUsers);
			log.info("代金卷提醒发送列表：{}",JsonMapper.nonEmptyMapper().toJson(pushUsers));
		}
	}
	
	public class PushUser{
		PushUser(String userId,String phone){
			this.userId = userId;
			this.phone = phone;
		}
		String userId;
		String phone;
		
		public String getUserId() {
			return userId;
		}
		
		public void setUserId(String userId) {
			this.userId = userId;
		}
		
		public String getPhone() {
			return phone;
		}
		
		public void setPhone(String phone) {
			this.phone = phone;
		}
	}
	
	/**
	 * @Description: 根据手机号发送短信
	 * @param mobile   手机号码
	 * @author tuzhd
	 * @date 2016年11月21日
	 */
	private void sendMsg(String phone) {
		try{
			SmsVO sms = new SmsVO();
			sms.setContent(smsIsNoticeCouponsRecordStyle);
			sms.setSysCode(msgSysCode);
			sms.setId(UuidUtils.getUuid());
			sms.setMobile(phone);
			sms.setToken(msgToken);
			sms.setIsTiming(0);
			sms.setSmsChannelType(3);
			sms.setSendTime(DateUtils.formatDateTime(new Date()));
			smsService.sendSms(sms);
		}catch(Exception e){
			//捕获异常不影响发送流程
			log.error("代金劵到期提醒发送短信异常！"+phone,e);
		}
	}
	
	/**
	 * @Description: 根据用户id及手机号码进行消息发送 代金劵到期提醒功能
	 * @param userId
	 * @param phone
	 * @throws Exception   
	 * @return void  
	 * @throws
	 * @author tuzhd
	 * @date 2016年11月21日
	 */
	private void sendPosMessage(List<PushUser> pushUsers) {
		try{
			PushMsgVo pushMsgVo = new PushMsgVo();
			pushMsgVo.setSysCode(msgSysCode);
			pushMsgVo.setToken(msgToken);
//			pushMsgVo.setSendUserId(userId);
//			pushMsgVo.setServiceFkId(userId);
			pushMsgVo.setServiceTypes(new Integer[] { MsgConstant.ServiceTypes.MALL_OTHER });
			// 0:用户APP,2:商家APP,3POS机
			pushMsgVo.setAppType(Constant.ZERO);
			pushMsgVo.setIsUseTemplate(Constant.ZERO);
			pushMsgVo.setMsgType(Constant.ONE);
			pushMsgVo.setMsgTypeCustom(OrderMsgConstant.COUPONS_MESSAGE);
			pushMsgVo.setMsgDetailType(Constant.ZERO);
			pushMsgVo.setMsgDetailLinkUrl("/voucher/V1.2.0/goMyVoucher");
			
			// 不使用模板
			pushMsgVo.setMsgNotifyContent(smsIsNoticeCouponsRecordStyle);
			pushMsgVo.setMsgDetailType(Constant.ONE);
			pushMsgVo.setMsgDetailContent(smsIsNoticeCouponsRecordStyle);
			// 设置是否定时发送
			pushMsgVo.setIsTiming(Constant.ZERO);
			
			// 发送用户
			List<PushUserVo> userList = new ArrayList<PushUserVo>();
			pushUsers.forEach(user->{
				PushUserVo pushUser = new PushUserVo();
				pushUser.setUserId(user.userId);
				pushUser.setMobile(user.phone);
				pushUser.setMsgType(Constant.ONE);
				userList.add(pushUser);
			});
			// 查询的用户信息
			pushMsgVo.setUserList(userList);
			sendMessage(pushMsgVo);
		}catch(Exception e){
			//捕获异常不影响发送流程
			log.error("代金劵到期提醒发送消息异常！",e);
		}
	}
	
    
	private void sendMessage(Object entity) throws Exception {
		MQMessage anMessage = new MQMessage(TOPIC, (Serializable)JsonMapper.nonDefaultMapper().toJson(entity));
		rocketMQProducer.sendMessage(anMessage);
	}

	@Override
	public int findCouponsRemain(String userId,String couponsId) {
		ActivityCoupons activityCoupons = activityCouponsMapper.selectById(couponsId);
		ActivityCouponsRecord activityCouponsRecord = new ActivityCouponsRecord();
		activityCouponsRecord.setCouponsId(couponsId);
		activityCouponsRecord.setCollectType(ActivityCouponsType.coupons);
		activityCouponsRecord.setCollectUserId(userId);
		int	currentRecordCount = activityCouponsRecordMapper.selectCountByParams(activityCouponsRecord);
		if (activityCoupons.getRemainNum() <= 0) {
			// 剩余数量小于0 显示已领完
			return 0;
		} else {
			if (currentRecordCount >= activityCoupons.getEveryLimit().intValue()) {
				// 已领取
				return 1;
			} else {
				// 立即领取
				return 2;
			}
		}
	}

	/**
	 * 根据邀请人id查询邀请记录信息
	 * (non-Javadoc)
	 * @see findInviteInfoByInviteUserId(java.lang.String)
	 */
	@Override
	public List<ActivityCouponsRecordBefore> findInviteInfoByInviteUserId(String inviteUserId) {
		return activityCouponsRecordBeforeMapper.findInviteInfoByInviteUserId(inviteUserId);
	}
	

	/**
	 * @Description: 邀新活动 被邀用户下单完成后给 邀请人送代金劵及抽奖次数 
	 * 1、是否完成首单
	 * 2、活动是否未结束
	 * @param userId 被邀请人id
	 * @param collectCouponsIds 邀请人获得的代金劵奖励id
	 * @author tuzhd
	 * @date 2016年12月13日
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addInviteUserHandler(String userId,String[] collectCouponsIds) throws Exception{
		//根据邀新活动，代金劵预领取表中的邀请人及被邀手机号码，确定该订单是否属于活动时间
		SysBuyerUser user = memberService.selectByPrimaryKey(userId);
		if(user != null){
			List<ActivityCouponsRecordBefore> list  = activityCouponsRecordBeforeMapper.findRecordVaildByUserId(user.getPhone());
			if(list == null){
				return;
			}
			//查询用户的手机邀请预领取记录
			for(ActivityCouponsRecordBefore record : list){
				/*ColumnAdvert columnAdvert = columnAdvertService.findAdvertById(record.getActivityId());
				//判断活动是否结束 结束时间大于当前时间未结束
				if(columnAdvert.getEndTime().getTime() <= new Date().getTime()){
					return;
				}*/
				//执行 给邀请人送代金劵及抽奖次数 1
				sysBuyerExtService.updateAddPrizeCount(record.getInviteUserId(), 1);
				
				//用户的邀请次数
				int inviteCount = activityCouponsRecordBeforeMapper.findInviteUserCount(record.getInviteUserId(),
						record.getActivityId());
				log.info("已成功邀请到"+inviteCount+"个用户");
				//修改预领取记录 为已完成邀请
				record.setIsComplete(WhetherEnum.whether);
				activityCouponsRecordBeforeMapper.updateByPrimaryKey(record);
				
				//超过4位不送代金劵 根据 邀请次数奖项给邀请人发放奖项代金劵
				if(inviteCount < collectCouponsIds.length){
					String collectCouponsId  = collectCouponsIds[inviteCount];
					
					//一次可以送多张代金劵
					String[] collectIds = collectCouponsId.split(",");
					for(String collectId : collectIds){
						//根据代金劵活动ID领取代金劵
						addRecordsByCollectId(collectId,record.getInviteUserId());
						//领取后并插入一次奖励 中奖纪录
						activityPrizeRecordService.addPrizeRecord(collectId, record.getInviteUserId(), 
												record.getActivityId(),null,WhetherEnum.whether.ordinal());
					}
				}
			
			}
			
		}
					
	}
	
	/**
	 * @Description:  tuzhd根据用户id查询其是否存在已使用的新用户专享代金劵 用于首单条件判断
	 * @param useUserType 使用用户类型
	 * @param userId 用户id
	 * @return int  统计结果
	 * @author tuzhd
	 * @date 2016年12月31日
	 */
	public int findCouponsCountByUser(UseUserType useUserType,String userId){
		return activityCouponsRecordMapper.findCouponsCountByUser(useUserType, userId);
	}

	@Override
	public List<Coupons> findValidCoupons(FavourParamBO paramBo, CouponsFilterStrategy favourFilter) throws Exception {
		List<Coupons> couponsList = activityCouponsRecordMapper.findValidCoupons(paramBo);
		if(CollectionUtils.isEmpty(couponsList)){
			return couponsList;
		}
		// 解析出代金券统一设备、同一账户的使用次数
		countUseRecord(paramBo,couponsList);
		// 对集合进行数据迭代
		Iterator<Coupons> it = couponsList.iterator();
		Coupons coupons = null;
		while(it.hasNext()){
			coupons = it.next();
			if(coupons.getIsCategory() == Constant.ONE && CollectionUtils.isEmpty(paramBo.getSpuCategoryIds())){
				// 如果代金券指定分类，且paramBo的分类为空，则重新查询
				// 当前店铺商品信息
				List<GoodsStoreSku> currentStoreSkuList = goodsStoreSkuServiceApi.findStoreSkuForOrder(paramBo.getSkuIdList());
		        //商品类型ids
				Set<String> spuCategoryIds = new HashSet<String>();
				if (CollectionUtils.isNotEmpty(currentStoreSkuList)) {
					for (GoodsStoreSku goodsStoreSku : currentStoreSkuList) {
						spuCategoryIds.add(goodsStoreSku.getSpuCategoryId());
					}
				}
				paramBo.setSpuCategoryIds(spuCategoryIds);
			}
			if(!favourFilter.accept(coupons)){
				// 如果过滤器不接受该优惠，则将该优惠从列表中移除
				it.remove();
			}else{
				coupons.setFirstUserLimit(coupons.getUseUserType().ordinal());
				coupons.setMaxFavourStrategy(genericMaxFavourStrategy.calMaxFavourRule(coupons, paramBo.getTotalAmount()));
			}
		}
		return couponsList;
	}
	
	private void countUseRecord(FavourParamBO paramBo,List<Coupons> couponsList){
		// 有设备限制的代金券Id列表
		List<String> deviceLimitCouponsIds = Lists.newArrayList();
		// 有账户限制的代金券Id列表
		List<String> userLimitCouponsIds = Lists.newArrayList();
		for(Coupons coupons : couponsList){
			if(coupons.getDeviceDayLimit() != null && coupons.getDeviceDayLimit() > 0){
				deviceLimitCouponsIds.add(coupons.getCouponId());
			}
			if(coupons.getAccountDayLimit() != null && coupons.getAccountDayLimit() > 0){
				userLimitCouponsIds.add(coupons.getCouponId());
			}
		}
		ActivityRecordParamBo recParamBo = null;
		List<ActivityRecordBo> recordBoList = null;
		if(CollectionUtils.isNotEmpty(deviceLimitCouponsIds) && StringUtils.isNotEmpty(paramBo.getDeviceId())){
			// 根据设备统计使用次数
			recParamBo = new ActivityRecordParamBo();
			recParamBo.setPkIdList(deviceLimitCouponsIds);
			recParamBo.setRecDate(DateUtils.getDate());
			recParamBo.setDeviceId(paramBo.getDeviceId());
			recordBoList = activityCouponsRecordMapper.countActivityRecord(recParamBo);
			paramBo.putActivityCounter(RecordCountRuleEnum.COUPONS_BY_DEVICE, recordBoList);
		}
		if(CollectionUtils.isNotEmpty(userLimitCouponsIds) && StringUtils.isNotEmpty(paramBo.getUserId())){
			// 根据设备统计使用次数
			recParamBo = new ActivityRecordParamBo();
			recParamBo.setPkIdList(userLimitCouponsIds);
			recParamBo.setRecDate(DateUtils.getDate());
			recParamBo.setUserId(paramBo.getUserId());
			recordBoList = activityCouponsRecordMapper.countActivityRecord(recParamBo);
			paramBo.putActivityCounter(RecordCountRuleEnum.COUPONS_BY_USER, recordBoList);
		}
	}
	
	/**
	 * 校验代金券领取的用户状态，避免短时间内并发操作
	 * @param key 存储的redis key
	 * @param times 超时时间
	 * @return
	 */
	private boolean checkUserStatusByRedis(String key,int times){
		//如果不存在缓存数据  返回true 存在false
		boolean flag = redisTemplate.boundValueOps(key).setIfAbsent(true);
		if(flag){
			redisTemplate.expire(key, times, TimeUnit.SECONDS);
		}else{
			return false;
		}
		return true;
	}
	/**
	 * 移除校验代金券领取的用户状	 
	 * @param key 存储的redis key
	 * @return
	 */
	private void removeRedisUserStatus(String key){
		redisTemplate.delete(key);
	}

	@Override
	public void releaseConpons(TradeOrder tradeOrder) {
		Map<String, Object> params = Maps.newHashMap();
		String orderId = tradeOrder.getId();
		params.put("orderId", orderId);
		List<ActivityCouponsRecord> records = activityCouponsRecordMapper.selectByParams(params);
		if(CollectionUtils.isEmpty(records)){
			return;
		}
		OrderStatusEnum orderState = tradeOrder.getStatus();
		for(ActivityCouponsRecord record : records){
			if ((orderState == OrderStatusEnum.REFUSED || orderState == OrderStatusEnum.REFUSING)
					&& record.getCouponsCollectId().equals(tradeOrder.getFareActivityId())) {
				// 如果订单为拒收，则不返还运费券
				continue;
			}
			if (record.getValidTime().compareTo(DateUtils.getSysDate()) > 0) {
				record.setStatus(ActivityCouponsRecordStatusEnum.UNUSED);
				activityCouponsRecordMapper.updateByPrimaryKeySelective(record);
			} else {
				record.setStatus(ActivityCouponsRecordStatusEnum.EXPIRES);
				activityCouponsRecordMapper.updateByPrimaryKeySelective(record);
			}
			activityCouponsMapper.updateReduceUseNum(record.getCouponsId());
		}
	}
}