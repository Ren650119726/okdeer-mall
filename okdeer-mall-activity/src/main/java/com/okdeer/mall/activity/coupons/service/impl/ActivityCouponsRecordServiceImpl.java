
package com.okdeer.mall.activity.coupons.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.okdeer.api.pay.account.dto.PayUpdateAmountDto;
import com.okdeer.api.pay.service.IPayTradeServiceApi;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.coupons.bo.ActivityCouponsRecordParamBo;
import com.okdeer.mall.activity.coupons.bo.ActivityRecordBo;
import com.okdeer.mall.activity.coupons.bo.ActivityRecordParamBo;
import com.okdeer.mall.activity.coupons.bo.UserCouponsBo;
import com.okdeer.mall.activity.coupons.bo.UserCouponsFilterContext;
import com.okdeer.mall.activity.coupons.bo.UserCouponsLoader;
import com.okdeer.mall.activity.coupons.dto.ActivityCouponsRecordDto;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsVo;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecordBefore;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecordQueryVo;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecordVo;
import com.okdeer.mall.activity.coupons.entity.CouponsFindVo;
import com.okdeer.mall.activity.coupons.entity.CouponsStatusCountVo;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsType;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.enums.CouponsType;
import com.okdeer.mall.activity.coupons.enums.RecordCountRuleEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivityCollectCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordBeforeMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsReceiveStrategy;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordService;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordServiceApi;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsServiceApi;
import com.okdeer.mall.activity.coupons.service.receive.AbstractCouponsReceive;
import com.okdeer.mall.activity.coupons.service.receive.ConponsReceiveFactory;
import com.okdeer.mall.activity.coupons.service.receive.bo.CouponsReceiveBo;
import com.okdeer.mall.activity.dto.ActivityCouponsDto;
import com.okdeer.mall.activity.dto.ActivityCouponsQueryParamDto;
import com.okdeer.mall.activity.dto.ActivityCouponsRecordQueryParamDto;
import com.okdeer.mall.activity.prize.service.ActivityPrizeRecordService;
import com.okdeer.mall.activity.service.CouponsFilterStrategy;
import com.okdeer.mall.activity.service.MaxFavourStrategy;
import com.okdeer.mall.activity.service.impl.CouponsFilterStrategyFactory;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.common.entity.ResultMsg;
import com.okdeer.mall.common.enums.GetUserType;
import com.okdeer.mall.common.enums.UseUserType;
import com.okdeer.mall.member.service.MemberService;
import com.okdeer.mall.order.constant.OrderMsgConstant;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.service.TradeOrderServiceApi;
import com.okdeer.mall.order.vo.Coupons;
import com.okdeer.mall.order.vo.PushMsgVo;
import com.okdeer.mall.order.vo.PushUserVo;
import com.okdeer.mall.order.vo.RechargeCouponVo;
import com.okdeer.mall.system.entity.PushUser;
import com.okdeer.mall.system.entity.SysUserInvitationCode;
import com.okdeer.mall.system.mapper.SysUserInvitationCodeMapper;
import com.okdeer.mall.system.service.InvitationCodeService;
import com.okdeer.mall.system.utils.ConvertUtil;
import com.okdeer.mcm.constant.MsgConstant;
import com.okdeer.mcm.service.ISmsService;

import net.sf.json.JSONObject;

/**
 * ClassName: ActivityCouponsRecordServiceImpl 
 * @Description: 代金卷记录
 * @author zengjizu
 * @date 2017年11月23日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
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

	@Autowired
	InvitationCodeService invitationCodeService;

	/**
	 * 邀请码mapper
	 */
	@Autowired
	private SysUserInvitationCodeMapper sysUserInvitationCodeMapper;

	@Reference(version = "1.0.0", check = false)
	private IPayTradeServiceApi payTradeServiceApi;
	
	@Resource
	private ConponsReceiveFactory conponsReceiveFactory;

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

	/** 取消订单短信1 */
	@Value("${sms.coupons.notice}")
	private String smsIsNoticeCouponsRecordStyle;

	/**
	 * 短信 service
	 */
	@Reference(version = "1.0.0")
	private ISmsService smsService;

	@Autowired
	private MemberService memberService;

	// 中奖记录表Service
	@Autowired
	ActivityPrizeRecordService activityPrizeRecordService;

	// Begin added by maojj 2017-02-15
	@Resource
	private MaxFavourStrategy genericMaxFavourStrategy;

	@Autowired
	private RedisTemplate<String, Boolean> redisTemplate;

	/**
	 * 店铺商品Api
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;
	// End added by maojj 2017-02-15

	@Reference(version = "1.0.0", check = false)
	private TradeOrderServiceApi tradeOrderService;

	@Autowired
	private ActivityCouponsServiceApi activityCouponsApi;

	@Resource
	private ActivityCouponsReceiveStrategy activityCouponsReceiveStrategy;

	@Resource
	private CouponsFilterStrategyFactory couponsFilterStrategyFactory;

	@Override
	@Transactional(readOnly = true)
	public PageUtils<ActivityCouponsRecordVo> getAllRecords(ActivityCouponsRecordVo recordVo,
			int pageNum, int pageSize) throws ServiceException {
		PageHelper.startPage(pageNum, pageSize, true);
		// begin 重构4.1 added by zhangkn
		if (recordVo.getStartTime() != null) {
			recordVo.setStartTime(DateUtils.getDateStart(recordVo.getStartTime()));
		}
		if (recordVo.getEndTime() != null) {
			recordVo.setEndTime(DateUtils.getDateEnd(recordVo.getEndTime()));
		}
		// end 重构4.1 added by zhangkn
		List<ActivityCouponsRecordVo> recordInfos = activityCouponsRecordMapper.selectAllRecords(recordVo);
		if (recordInfos == null) {
			recordInfos = new ArrayList<>();
		} else {
			List<String> recordIds = new ArrayList<>();
			for (ActivityCouponsRecordVo vo : recordInfos) {
				recordIds.add(vo.getId());
				Calendar cal = Calendar.getInstance();
				cal.setTime(vo.getValidTime());
				cal.add(Calendar.DATE, -1); // 减1天
				vo.setValidTime(cal.getTime());

				// 折扣券的时候，要显示***折
				if (vo.getCouponsType() == CouponsType.tyzkq.getValue().intValue()) {
					BigDecimal tempFaveValue = new BigDecimal(vo.getFaceValue()).divide(new BigDecimal(10));
					vo.setFaceValueStr(tempFaveValue.toPlainString() + "折");
				} else {
					vo.setFaceValueStr(vo.getFaceValue() + "元");
				}
				
				//v2.6.4有效期要改为区间段的显示方式，
				//0：设置领取后多少天生效，生效后多少天失效。1：设置设置有效时间范围'
				if(vo.getTermType() == 1){
					if(vo.getCouponsEndTime() != null && vo.getCouponsStartTime() != null){
						vo.setValidDayStr(
								DateUtils.formatDate(vo.getCouponsStartTime(),"yyyy-MM-dd") + " - "+
								DateUtils.formatDate(vo.getCouponsEndTime(),"yyyy-MM-dd")
							);
					}
				} else {
					//当前时间11-9 折扣券A:生效时间领取后2天有效，失效时间生效后3天失效 折扣券B: 有效期 11-9-11.20 用户13723472527 在11-10 领取折扣券A B
					//1. 有效期显示2017-11-11-2017-11-13
					//2. 有效期显示2017-11-09-2017-11-20
					Calendar calValidTime = Calendar.getInstance();
					calValidTime.setTime(vo.getCollectTime());
					calValidTime.add(Calendar.DATE, vo.getEffectDay());
					
					Calendar calLoseTime = Calendar.getInstance();
					calLoseTime.setTime(calValidTime.getTime());
					calLoseTime.add(Calendar.DATE, vo.getValidDay() - 1);
					
					vo.setValidDayStr(
							DateUtils.formatDate(calValidTime.getTime(),"yyyy-MM-dd") + " - "+
							DateUtils.formatDate(calLoseTime.getTime(),"yyyy-MM-dd")
						);
				}
			}
		}
		return new PageUtils<>(recordInfos);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityCouponsRecordVo> getRecordExportData(Map<String, Object> paraMap) {
		return activityCouponsRecordMapper.selectExportRecords(paraMap);
	}

	@Override
	@Transactional(readOnly = true)
	public int selectCountByParams(ActivityCouponsRecordQueryParamDto activityCouponsRecordQueryParam)
			throws ServiceException {
		return activityCouponsRecordMapper.selectCountByParams(activityCouponsRecordQueryParam);
	}

	// begin update by zhulq 2016-10-17 新增方法提供给之前的版本
	@Override
	@Transactional(readOnly = true)
	public List<ActivityCouponsRecordDto> findMyCouponsDetailByParams(List<ActivityCouponsRecordStatusEnum> statusList,
			String currentOperateUserId) throws ServiceException {
		ActivityCouponsRecordParamBo paramBo = new ActivityCouponsRecordParamBo();
		paramBo.setIncludeStatusList(statusList);
		paramBo.setCollectUserId(currentOperateUserId);
		List<ActivityCouponsRecord> list = activityCouponsRecordMapper.findCollectRecordList(paramBo);
		if (CollectionUtils.isEmpty(list)) {
			return Lists.newArrayList();
		}
		// 按时间排序
		list.sort((obj1, obj2) -> {
			if (obj1.getCollectTime().getTime() == obj2.getCollectTime().getTime()) {
				return 0;
			}
			if (obj1.getCollectTime().getTime() > obj2.getCollectTime().getTime()) {
				return -1;
			}
			return 1;
		});

		List<ActivityCouponsRecordDto> dtoList = BeanMapper.mapList(list, ActivityCouponsRecordDto.class);
		ActivityCouponsQueryParamDto activityCouponsQueryParamDto = new ActivityCouponsQueryParamDto();
		activityCouponsQueryParamDto.setQueryArea(false);
		activityCouponsQueryParamDto.setQueryCategory(true);
		for (ActivityCouponsRecordDto activityCouponsRecordDto : dtoList) {
			ActivityCouponsDto activityCouponsDto = activityCouponsApi
					.findDetailById(activityCouponsRecordDto.getCouponsId(), activityCouponsQueryParamDto);
			activityCouponsRecordDto.setActivityCouponsDto(activityCouponsDto);
		}
		return dtoList;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject addRecordForRecevie(String couponsId, String currentOperatUserId) throws ServiceException {
		ActivityCoupons activityCoupons = activityCouponsMapper.selectByPrimaryKey(couponsId);
		// 根据数量的判断，插入代金券领取记录
		return JSONObject.fromObject(insertRecordByJudgeNum(activityCoupons, currentOperatUserId));

	}

	/**
	 * 根据活动ID领取代金劵 存在邀请人用户id需要确认是否记录邀请人
	 * @param collectId 活动id集合
	 * @param userId 用户id
	 * @param invitaUserId  邀请人用户i
	 * @return tuzhd
	 * @throws ServiceException
	 */
	@Transactional(rollbackFor = Exception.class)
	public JSONObject addRecordsByCollectId(String collectId, String userId) throws ServiceException {
		return addRecordsByCollectId(collectId, userId, null);
	}

	/**
	 * 根据活动ID领取代金劵
	 * @param collectId 活动id集合
	 * @param userId 用户id
	 * @param invitaUserId  邀请人用户id
	 * @return tuzhd
	 * @throws ServiceException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject addRecordsByCollectId(String collectId, String userId, String invitaUserId) throws ServiceException {
		return addRecordsByCollectId(collectId, userId, invitaUserId, true);

	}


	/**
	 * 代金券预领取
	 *@see addBeforeRecords(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject addBeforeRecords(String collectId, String phone, String invitaUserId, String advertId)
			throws ServiceException {
		return addBeforeRecords(collectId, phone, invitaUserId, advertId, true);
		
	}
	
	/**
	 * 根据活动ID及用户手机号码领取代金劵
	 * @param collectId 活动id集合
	 * @param phone  用户手机号码
	 * @param activityCouponsType 活动类型
	 * @param invitaUserId 邀请的用户id 没有null
	 * @param advertId H5活动id
	 * @param limitOne 是否只允许领取一次
	 * @throws ServiceException   
	 * @author tuzhd
	 * @date 2017年11月24日
	 */
	@Transactional(rollbackFor = Exception.class)
	private JSONObject addBeforeRecords(String collectId, String phone, String invitaUserId, String advertId,boolean limitOne)
			throws ServiceException {
		// 校验成功标识 //如果不存在缓存数据进行加入到缓存中
		String key = phone + collectId;
		ResultMsg msg  = new ResultMsg();
		// 校验成功标识 //如果不存在缓存数据进行加入到缓存中
		if (!checkUserStatusByRedis(key, msg)) {
			return JSONObject.fromObject(msg);
		}
		try {
			ActivityCollectCoupons coll = activityCollectCouponsMapper.get(collectId);
			//获得领取实际操作 对象
			AbstractCouponsReceive receive=conponsReceiveFactory.produce(coll.getType());
			CouponsReceiveBo bo = setCouponsReceiveBo(coll, null, invitaUserId, limitOne);
			bo.setActivityId(advertId);
			//执行领券操作
			msg = receive.excute(bo);
			
		} catch (ServiceException e) {
			log.error("预领取代金券失败，活动id:"+collectId,e);
			throw e;
		} finally {
			// 移除redis缓存的key
			removeRedisUserStatus(key);
		}
		return JSONObject.fromObject(msg);
		
	}

	/**
	 * 微信活动预领取
	 * @see addBeforeRecords
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject addBeforeRecordsForWechatActivity(String collectId, String phone, String advertId)
			throws ServiceException {
		return addBeforeRecords(collectId, phone, null, advertId, false);

	}

	/**
	 * @DESC 校验预领取记录 1、存在未使用的新人代金券则 不能领取返回false 2、持续的活动领取过不能再领取
	 * @param phone
	 *            手机号
	 * @param collectId
	 *            代金券活动id
	 * @return
	 */
	private boolean checkBeforeCoupons(String phone, ActivityCollectCoupons coll) {
		// 如果领取为限新人使用 则校验是否领取过新人代金券
		if (coll.getGetUserType() == GetUserType.ONlY_NEW_USER) {
			// 查询该用户已领取， 新人限制， 未使用，的代金劵活动的代金劵数量
			int hadNewCount = activityCouponsRecordBeforeMapper.countCouponsByType(GetUserType.ONlY_NEW_USER, phone,
					new Date());
			// 存在未使用的新人代金券则 返回true
			if (hadNewCount > 0) {
				return true;
			}
		}
		// 根据代金劵活动id代金劵预领取统计 持续的活动领取过不能再领取
		return activityCouponsRecordBeforeMapper.countCouponsAllId(phone, coll.getId()) > 0;
	}

	/**
	 * 注册送完代金劵后将预代金劵送到用户的账户中
	 * @param userId 用户id
	 * @author tuzhd
	 */
	public void insertCopyRecords(String userId, String phone, String machineCode) {
		try {
			List<ActivityCouponsRecordBefore> list = activityCouponsRecordBeforeMapper.getCopyRecords(userId,
					new Date(), phone);
			if (CollectionUtils.isNotEmpty(list)) {
				list.forEach(e -> {
					if (e.getCollectTime().after(new Date())) {
						e.setStatus(ActivityCouponsRecordStatusEnum.UNEFFECTIVE);
					}
				});
				activityCouponsRecordMapper.insertBatchRecordByBefore(list);

				// 循环代金券记录，找出最后一次有效的邀请人领取记录
				ActivityCouponsRecordBefore lastR = null;
				for (ActivityCouponsRecordBefore record : list) {
					if (StringUtils.isBlank(record.getInviteUserId())) {
						continue;
					}
					// 第一个有邀请人 的预领取记录记录到 临时对象中 //当后面的预领取记录记录 领取时候大 已后面的领取邀请人为准
					if (lastR == null || (record.getCollectTime().getTime() > lastR.getCollectTime().getTime())) {
						lastR = record;
					}
				}
				// 存在记录 说明该用户存在 邀请人，添加邀请人记录
				if (lastR != null) {
					// 根据用户id或用户邀请码，所以InviteUserId 可以存储用户id或邀请码
					List<SysUserInvitationCode> listCode = sysUserInvitationCodeMapper
							.findInvitationByIdCode(lastR.getInviteUserId(), lastR.getInviteUserId());
					// 存在邀请码及添加第一个进去，防止数据库中存在多个
					if (CollectionUtils.isNotEmpty(listCode)) {
						invitationCodeService.saveInvatationRecord(listCode.get(0), userId, machineCode);
					}
				}

			}
		} catch (Exception e) {
			// 捕获异常不影响注册流程
			log.error("将预代金劵送到用户的账户中出现异常！", e);
		}
	}

	/**
	 * 领取活动优惠券
	 * (non-Javadoc)
	 */
	public void drawCouponsRecord(ActivityCollectCoupons coll,String userId) throws ServiceException {
		// 校验成功标识 //如果不存在缓存数据进行加入到缓存中
		addRecordsByCollectId(coll.getId(), userId,null, true);
		
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject addRecordForExchangeCode(Map<String, Object> params, String exchangeCode,
			String currentOperatUserId) throws ServiceException {
		ResultMsg msg = new ResultMsg();
		List<ActivityCollectCouponsVo> result = activityCollectCouponsMapper.selectByStoreAndLimitType(params);
		// 判断输入的优惠码是否正确
		if (CollectionUtils.isNotEmpty(result)) {
			ActivityCoupons activityCoupons = result.get(0).getActivityCoupons().get(0);
			// 根据数量的判断，插入代金券领取记录
			msg = this.insertRecordByJudgeNum(activityCoupons, currentOperatUserId);
			if(msg.getCode() == 100) msg.setMsg("恭喜你，优惠券兑换成功！");
		} else {
			msg.setMsg("您输入的抵扣券优惠码错误！");
			msg.setCode(103);
		}
		return JSONObject.fromObject(msg);
	}

	/**
	 * 根据随机码进行领取代金劵
	 * @param activityCoupons 活动信息
	 * @param userId 用户id
	 * @param activityCouponsType 活动类型
	 * @author zhulq
	 * @throws ServiceException 
	 * @date 2016年10月26日
	 */
	@Transactional(rollbackFor = Exception.class)
	public ResultMsg insertRecordByRandCode(ActivityCoupons activityCoupons, String userId) throws ServiceException {
		return insertCouponsRecord(activityCoupons, userId, true);
	}

	/**
	 * @Description: 公共领取方法
	 * @param activityCoupons 代金券
	 * @param userId 用户id
	 * @param isRandCode 是否是随机吗
	 * @author tuzhd
	 * @throws ServiceException 
	 * @date 2017年11月24日
	 */
	private ResultMsg insertCouponsRecord(ActivityCoupons activityCoupons, String userId,boolean isRandCode) 
			throws ServiceException{
		ResultMsg msg = new ResultMsg();
		// 校验成功标识 //如果不存在缓存数据进行加入到缓存中
		String key = userId + activityCoupons.getActivityId();
		boolean checkFlag = checkUserStatusByRedis(key, msg);
		if (!checkFlag) {
			return msg;
		}
		try {
			// 设置代金券领取记录的代金券id、代金券领取活动id、活动类型，以便后面代码中的数量判断查询
			ActivityCollectCoupons coll = activityCollectCouponsMapper.get(activityCoupons.getActivityId());
			//获得领取实际操作 对象
			AbstractCouponsReceive receive=conponsReceiveFactory.produce(coll.getType());
			CouponsReceiveBo  bo = setCouponsReceiveBo(coll, userId, null, false);
			//随机码领取加入随机码检验
			if(isRandCode){
				bo.setRandCode(activityCoupons.getRandCode());
			}
			//执行领券操作
			msg = receive.excute(bo);
			
		} catch (ServiceException e) {
			log.error("领取代金券失败，代金券id:"+activityCoupons.getId(),e);
			throw e;
		} finally {
			// 移除redis缓存的key
			removeRedisUserStatus(key);
		}
		return msg;
	}
	
	/**
	 * 判断当前登陆用户领取的指定代金券数量是否已经超过限领数量，
	 * 所有用户领取的指定代金券总数量是否已经超过代金券的总发行数量，否则，插入代金券领取记录
	 * @param activityCoupons 活动信息
	 * @param userId  用户id
	 * @author zhulq
	 * @throws ServiceException 
	 * @date 2016年10月26日
	 */
	@Transactional(rollbackFor = Exception.class)
	public ResultMsg insertRecordByJudgeNum(ActivityCoupons activityCoupons, String userId) throws ServiceException {
		return insertCouponsRecord(activityCoupons, userId,false);
	}

	@Override
	public List<ActivityCouponsRecordQueryVo> selectCouponsDetailByStoreId(ActivityCouponsRecord activityCouponsRecord)
			throws ServiceException {

		List<ActivityCouponsRecordQueryVo> couponsList = null;
		List<ActivityCouponsRecordQueryVo> couponsAllList = null;
		couponsAllList = activityCouponsRecordMapper.selectCouponsAllId(activityCouponsRecord);
		couponsList = activityCouponsRecordMapper.selectCouponsDetailByStoreId(activityCouponsRecord);
		List<ActivityCouponsRecordQueryVo> couponsAllLists = new ArrayList<>();

		couponsAllLists.addAll(couponsAllList);
		couponsAllLists.addAll(couponsList);

		return couponsAllLists;
	}

	@Override
	public ActivityCoupons selectCouponsItem(CouponsFindVo couponsFindVo) throws Exception {
		return activityCouponsRecordMapper.selectCouponsItem(couponsFindVo);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateStatusByJob() throws Exception {
		List<ActivityCouponsRecord> updateRecList = activityCouponsRecordMapper.findForJob(new Date());
		if (CollectionUtils.isEmpty(updateRecList)) {
			return;
		}
		Date currentDate = new Date();
		// 生效的id列表
		List<String> effectIdList = updateRecList.stream()
				.filter(e -> e.getEffectTime().before(currentDate) && e.getValidTime().after(currentDate))
				.map(e -> e.getId()).collect(Collectors.toList());
		// 过期的id列表
		List<String> expireIdList = updateRecList.stream().filter(e -> e.getValidTime().before(currentDate))
				.map(e -> e.getId()).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(effectIdList)) {
			Map<String, Object> params = new HashMap<>();
			params.put("ids", effectIdList);
			params.put("status", ActivityCouponsRecordStatusEnum.UNUSED);
			activityCouponsRecordMapper.updateAllByBatch(params);
		}

		if (CollectionUtils.isNotEmpty(expireIdList)) {
			Map<String, Object> params = new HashMap<>();
			params.put("ids", expireIdList);
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
		return activityCouponsRecordMapper.selectByPrimaryKey(id);
	}

	/**
	 * 添加根据条件查询代金券信息
	 * 
	 * @param params
	 * @return
	 */
	@Override
	public List<ActivityCouponsRecord> selectByParams(Map<String, Object> params) {
		return activityCouponsRecordMapper.selectByParams(params);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateActivityCouponsStatus(Map<String, Object> params) {
		activityCouponsRecordMapper.updateActivityCouponsStatus(params);

	}

	// begin add by wushp 20160919 V1.1.0
	@Transactional(readOnly = true)
	@Override
	public List<CouponsStatusCountVo> findStatusCountByUserId(String userId) {
		return activityCouponsRecordMapper.findStatusCountByUserId(userId);
	}
	// end add by wushp 20160919 V1.1.0

	@Override
	public List<RechargeCouponVo> findValidRechargeCoupons(Map<String, Object> params) {
		List<RechargeCouponVo> couponVos = activityCouponsRecordMapper.findValidRechargeCoupons(params);

		// 限制设备使用次数的代金券Id
		List<String> deviceLimitCouponsIds = Lists.newArrayList();
		// 限制账号使用次数的代金券Id
		List<String> userLimitCouponsIds = Lists.newArrayList();
		// 用户所有代金券ID
		List<String> userCouponsIds = Lists.newArrayList();
		for (RechargeCouponVo couponVo : couponVos) {
			if (couponVo.getDeviceDayLimit() != null && couponVo.getDeviceDayLimit() > 0) {
				deviceLimitCouponsIds.add(couponVo.getCouponId());
			}
			if (couponVo.getAccountDayLimit() != null && couponVo.getAccountDayLimit() > 0) {
				userLimitCouponsIds.add(couponVo.getCouponId());
			}
			userCouponsIds.add(couponVo.getCouponId());
		}

		FavourParamBO paramBo = new FavourParamBO();
		String machineCode = params.get("machineCode").toString();
		String userId = params.get("userId").toString();
		if (CollectionUtils.isNotEmpty(deviceLimitCouponsIds) && StringUtils.isNotEmpty(machineCode)) {
			// 根据设备统计使用次数
			List<ActivityRecordBo> recordBoList = findActivityRecordCount(deviceLimitCouponsIds, null, machineCode,
					false);
			paramBo.putActivityCounter(RecordCountRuleEnum.COUPONS_BY_DEVICE, recordBoList);
		}
		if (CollectionUtils.isNotEmpty(userLimitCouponsIds) && StringUtils.isNotEmpty(userId)) {
			// 根据设备统计使用次数
			List<ActivityRecordBo> recordBoList = findActivityRecordCount(userLimitCouponsIds, userId, null, false);
			paramBo.putActivityCounter(RecordCountRuleEnum.COUPONS_BY_USER, recordBoList);
		}
		// 代金券活动统计
		if (CollectionUtils.isNotEmpty(userCouponsIds) && StringUtils.isNotEmpty(machineCode)) {
			// 根据设备统计使用次数
			List<ActivityRecordBo> recordBoList = findActivityRecordCount(userCouponsIds, null, machineCode, true);
			paramBo.putActivityCounter(RecordCountRuleEnum.COUPONS_COLLECT_BY_DEVICE, recordBoList);
		}
		if (CollectionUtils.isNotEmpty(userCouponsIds) && StringUtils.isNotEmpty(userId)) {
			// 根据用户统计使用次数
			List<ActivityRecordBo> recordBoList = findActivityRecordCount(userCouponsIds, userId, null, true);
			paramBo.putActivityCounter(RecordCountRuleEnum.COUPONS_COLLECT_BY_USER, recordBoList);
		}

		// 检验优惠券的设备和账号限制
		Iterator<RechargeCouponVo> it = couponVos.iterator();
		while (it.hasNext()) {
			RechargeCouponVo couponVo = it.next();
			if (!checkCouponLimit(paramBo, couponVo)) {
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
	private boolean checkCouponLimit(FavourParamBO paramBo, RechargeCouponVo couponVo) {
		if (couponVo.getDeviceDayLimit() != null && couponVo.getDeviceDayLimit() > 0) {
			if (couponVo.getDeviceDayLimit().compareTo(
					paramBo.findCountNum(RecordCountRuleEnum.COUPONS_BY_DEVICE, couponVo.getCouponId())) < 1) {
				return false;
			}
		}
		if (couponVo.getAccountDayLimit() != null && couponVo.getAccountDayLimit() > 0) {
			if (couponVo.getAccountDayLimit()
					.compareTo(paramBo.findCountNum(RecordCountRuleEnum.COUPONS_BY_USER, couponVo.getCouponId())) < 1) {
				return false;
			}
		}
		// 代金券活动
		ActivityCollectCoupons collectCoupons = activityCollectCouponsMapper.get(couponVo.getActivityId());
		// 代金券活动设备限制
		if (collectCoupons != null && collectCoupons.getDeviceDayLimit() > 0) {
			if (collectCoupons.getDeviceDayLimit().compareTo(paramBo
					.findCountNum(RecordCountRuleEnum.COUPONS_COLLECT_BY_DEVICE, couponVo.getActivityId())) < 1) {
				return false;
			}
		}
		// 代金券活动账号现在
		if (collectCoupons != null && collectCoupons.getAccountDayLimit() > 0) {
			if (collectCoupons.getAccountDayLimit().compareTo(
					paramBo.findCountNum(RecordCountRuleEnum.COUPONS_COLLECT_BY_USER, couponVo.getActivityId())) < 1) {
				return false;
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
	public List<ActivityCouponsRecord> selectActivityCouponsRecord(ActivityCouponsRecord couponsRecord)
			throws Exception {
		return  activityCouponsRecordMapper.selectAllRecordsByUserId(couponsRecord);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject addRecordForRandCode(Map<String, Object> params, String randCode, String currentOperatUserId) 
			throws ServiceException {
		ResultMsg msg = new ResultMsg();
		// 如果随机码为空即不进行检验
		String radeCode = (String) params.get("randCode");
		if (StringUtils.isNotBlank(radeCode)) {
			List<ActivityCollectCouponsVo> result = activityCollectCouponsMapper.selectRandCodeVoucher(params);
			// 判断输入的随机码是否正确
			if (CollectionUtils.isNotEmpty(result)) {
				ActivityCoupons activityCoupons = result.get(0).getActivityCoupons().get(0);
				activityCoupons.setRandCode(radeCode);
				// 根据数量的判断，插入代金券领取记录
				msg = this.insertRecordByRandCode(activityCoupons, currentOperatUserId);
				if(msg.getCode() == 100) msg.setMsg("恭喜你，优惠券兑换成功！");
			} else {
				msg.setCode(103);
				msg.setMsg("您输入的抵扣券优惠码错误！");
			}
		} else {
			msg.setCode(103);
			msg.setMsg("您输入的抵扣券优惠码错误！");
		}

		return JSONObject.fromObject(msg);
	}

	/**
	 * @Description: 
	 * 1、当代金券设置的领取后的有效期大于3天，则在代金券结束前第三天发送；
	 * 2、当代金券设置的领取后的有效期大于1天小于或等于3天，
	 * 则在代金券的有效期最后一天发送；当代金券设置的领取后的有效期等于1天，则不会发送推送和短线
	 * @author tuzhd
	 * @date 2016年11月21日
	 */
	private List<Map<String, Object>> getIsNoticeUser() {
		return activityCouponsRecordMapper.getIsNoticeUser();
	}

	/**
	 * @Description: 执行代金劵提醒JOB
	 * @throws @author tuzhd
	 * @date 2016年11月21日
	 */
	public void procesRecordNoticeJob() {
		List<Map<String, Object>> list = getIsNoticeUser();
		// 存在需要提醒的对象
		List<PushUser> pushUsers = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(list)) {
			list.forEach(user -> pushUsers.add(new PushUser((String) user.get("id"), (String) user.get("phone"))));
			// 按产品及运营的要求屏蔽代金券到期提醒短信功能
			sendPosMessage(pushUsers);
			log.info("代金卷提醒发送列表：{}", JsonMapper.nonEmptyMapper().toJson(pushUsers));
		}
	}




	/**
	 * @Description: 根据用户id及手机号码进行消息发送 代金劵到期提醒功能
	 * @param userId
	 * @param phone
	 * @return void tuzhd
	 * @date 2016年11月21日
	 */
	private void sendPosMessage(List<PushUser> pushUsers) {
		try {
			PushMsgVo pushMsgVo = new PushMsgVo();
			pushMsgVo.setSysCode(msgSysCode);
			pushMsgVo.setToken(msgToken);
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
			List<PushUserVo> userList = new ArrayList<>();
			pushUsers.forEach(user -> {
				PushUserVo pushUser = new PushUserVo();
				pushUser.setUserId(user.getUserId());
				pushUser.setMobile(user.getPhone());
				pushUser.setMsgType(Constant.ONE);
				userList.add(pushUser);
			});
			// 查询的用户信息
			pushMsgVo.setUserList(userList);
			sendMessage(pushMsgVo);
		} catch (Exception e) {
			// 捕获异常不影响发送流程
			log.error("代金劵到期提醒发送消息异常！", e);
		}
	}

	private void sendMessage(Object entity) throws Exception {
		MQMessage anMessage = new MQMessage(TOPIC, (Serializable) JsonMapper.nonDefaultMapper().toJson(entity));
		rocketMQProducer.sendMessage(anMessage);
	}

	@Override
	public int findCouponsRemain(String userId, String couponsId) {
		ActivityCoupons activityCoupons = activityCouponsMapper.selectById(couponsId);
		ActivityCouponsRecord activityCouponsRecord = new ActivityCouponsRecord();
		activityCouponsRecord.setCouponsId(couponsId);
		activityCouponsRecord.setCollectType(ActivityCouponsType.coupons);
		activityCouponsRecord.setCollectUserId(userId);
		ActivityCouponsRecordQueryParamDto activityCouponsRecordQueryParamDto = new ActivityCouponsRecordQueryParamDto();
		BeanMapper.copy(activityCouponsRecord, activityCouponsRecordQueryParamDto);
		activityCouponsRecordQueryParamDto.setCollectType(ActivityCouponsType.coupons.ordinal());
		int currentRecordCount = activityCouponsRecordMapper.selectCountByParams(activityCouponsRecordQueryParamDto);
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
	 * 根据邀请人id查询邀请记录信息 (non-Javadoc)
	 * 
	 * @see findInviteInfoByInviteUserId(java.lang.String)
	 */
	@Override
	public List<ActivityCouponsRecordBefore> findInviteInfoByInviteUserId(String inviteUserId) {
		return activityCouponsRecordBeforeMapper.findInviteInfoByInviteUserId(inviteUserId);
	}

	/**
	 * @Description: 邀新活动 被邀用户下单完成后给 邀请人送代金劵及抽奖次数 1、是否完成首单 2、活动是否未结束
	 * @param userId 被邀请人id
	 * @param collectCouponsIds  邀请人获得的代金劵奖励id
	 * @author tuzhd
	 * @date 2016年12月13日
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addInviteUserHandler(String userId, String[] collectCouponsIds) throws Exception {
		// 根据邀新活动，代金劵预领取表中的邀请人及被邀手机号码，确定该订单是否属于活动时间
		SysBuyerUser user = memberService.selectByPrimaryKey(userId);
		if (user != null) {
			List<ActivityCouponsRecordBefore> list = activityCouponsRecordBeforeMapper
					.findRecordVaildByUserId(user.getPhone());
			if(CollectionUtils.isNotEmpty(list)){
				// 查询用户的手机邀请预领取记录
				for (ActivityCouponsRecordBefore record : list) {
					// 修改预领取记录 为已完成邀请
					record.setIsComplete(WhetherEnum.whether);
					activityCouponsRecordBeforeMapper.updateByPrimaryKey(record);
				}
			}
		}

	}

	/**
	 * @Description: tuzhd根据用户id查询其是否存在已使用的新用户专享代金劵 用于首单条件判断
	 * @param useUserType
	 *            使用用户类型
	 * @param userId
	 *            用户id
	 * @return int 统计结果
	 * @author tuzhd
	 * @date 2016年12月31日
	 */
	public int findCouponsCountByUser(UseUserType useUserType, String userId) {
		return activityCouponsRecordMapper.findCouponsCountByUser(useUserType, userId);
	}

	@Override
	public List<Coupons> findValidCoupons(FavourParamBO paramBo) throws Exception {
		// 查询用户未使用的代金券列表
		ActivityCouponsRecordParamBo queryParamBo = new ActivityCouponsRecordParamBo();
		queryParamBo.setCollectUserId(paramBo.getUserId());
		List<ActivityCouponsRecordStatusEnum> enumList=Lists.newArrayList();
		enumList.add(ActivityCouponsRecordStatusEnum.UNUSED);
		queryParamBo.setIncludeStatusList(enumList);
		List<UserCouponsBo> userCouponsList = findUserCouponsList(queryParamBo);
		if (CollectionUtils.isEmpty(userCouponsList)) {
			return Lists.newArrayList();
		}
		// 解析出代金券统一设备、同一账户的使用次数
		countUseRecord(paramBo, userCouponsList);
		List<Coupons> couponsList = Lists.newArrayList();
		UserCouponsFilterContext filterContext = new UserCouponsFilterContext();
		// 过滤可用的用户代金券信息
		userCouponsList.forEach(userCoupons -> {
			CouponsFilterStrategy filterStrategy = couponsFilterStrategyFactory
					.get(userCoupons.getCouponsInfo().getType());
			// 代金券信息
			ActivityCoupons couponsInfo = userCoupons.getCouponsInfo();
			if (filterStrategy.accept(userCoupons, paramBo, filterContext)) {
				// 领取记录信息
				ActivityCouponsRecord collectRec = userCoupons.getCollectRecord();
				// 如果代金券可用，转换成可用对象
				Coupons coupons = new Coupons();
				coupons.setCouponTitle(buildCouponsTitle(couponsInfo));
				coupons.setType(couponsInfo.getType());
				coupons.setUsableRange("0");
				coupons.setId(couponsInfo.getActivityId());
				coupons.setCouponId(couponsInfo.getId());
				// 面额.如果是折扣券，优惠面额为折扣的金额。（保留两位小数）
				if (couponsInfo.getType() == CouponsType.tyzkq.getValue()) {
					coupons.setCouponPrice(ConvertUtil.formatNumber(
							BigDecimal.valueOf(couponsInfo.getFaceValue()).divide(BigDecimal.valueOf(10))));
				} else {
					coupons.setCouponPrice(ConvertUtil.formatNumber(couponsInfo.getFaceValue()));
				}
				setDiscountAmount(coupons, couponsInfo, filterContext.getEnjoyFavourAmount());
				coupons.setRecordId(collectRec.getId());
				coupons.setActivityItemId(couponsInfo.getId());
				coupons.setCouponsType(collectRec.getCollectType().ordinal());
				coupons.setArrive(ConvertUtil.formatNumber(couponsInfo.getArriveLimit()));
				coupons.setFirstUserLimit(couponsInfo.getUseUserType().ordinal());
				coupons.setActivityType(ActivityTypeEnum.VONCHER.ordinal());
				// 代金券到期日期（数据库中存储的到期日期为：yyyy-MM-dd 00：00：00 给用户展示时间时减一天）
				coupons.setIndate(DateUtils.formatDate(collectRec.getValidTime(), "yyyy-MM-dd"));
				coupons.setMaxFavourStrategy(
						genericMaxFavourStrategy.calMaxFavourRule(coupons, paramBo.getTotalAmount()));

				couponsList.add(coupons);
				filterContext.addEnabledCouponsId(couponsInfo.getId());
			} else {
				// 如果不可用，将代金券id缓存到不可用列表中
				filterContext.addExcludeCouponsId(couponsInfo.getId());
			}
		});
		return couponsList;
	}

	/**
	 * 统计用户代金券使用记录
	 */
	public void countUseRecord(FavourParamBO paramBo, List<UserCouponsBo> userCouponsList) {
		// 有设备限制的代金券Id列表
		List<String> deviceLimitCouponsIds = Lists.newArrayList();
		// 有账户限制的代金券Id列表
		List<String> userLimitCouponsIds = Lists.newArrayList();
		// 用户所有代金券ID
		List<String> userCouponsIds = Lists.newArrayList();
		userCouponsList.forEach(userCoupons -> {
			ActivityCoupons couponsInfo = userCoupons.getCouponsInfo();
			if (couponsInfo.getDeviceDayLimit() != null && couponsInfo.getDeviceDayLimit() > 0) {
				deviceLimitCouponsIds.add(couponsInfo.getId());
			}
			if (couponsInfo.getAccountDayLimit() != null && couponsInfo.getAccountDayLimit() > 0) {
				userLimitCouponsIds.add(couponsInfo.getId());
			}
			userCouponsIds.add(couponsInfo.getId());
		});

		if (CollectionUtils.isNotEmpty(deviceLimitCouponsIds) && StringUtils.isNotEmpty(paramBo.getDeviceId())) {
			// 根据设备统计使用次数
			List<ActivityRecordBo> recordBoList = findActivityRecordCount(deviceLimitCouponsIds, null,
					paramBo.getDeviceId(), false);
			paramBo.putActivityCounter(RecordCountRuleEnum.COUPONS_BY_DEVICE, recordBoList);
		}
		if (CollectionUtils.isNotEmpty(userLimitCouponsIds) && StringUtils.isNotEmpty(paramBo.getUserId())) {
			// 根据设备统计使用次数
			List<ActivityRecordBo> recordBoList = findActivityRecordCount(userLimitCouponsIds, paramBo.getUserId(),
					null, false);
			paramBo.putActivityCounter(RecordCountRuleEnum.COUPONS_BY_USER, recordBoList);
		}
		// 代金券活动统计
		if (CollectionUtils.isNotEmpty(userCouponsIds) && StringUtils.isNotEmpty(paramBo.getDeviceId())) {
			// 根据设备统计使用次数
			List<ActivityRecordBo> recordBoList = findActivityRecordCount(userCouponsIds, null, paramBo.getDeviceId(),
					true);
			paramBo.putActivityCounter(RecordCountRuleEnum.COUPONS_COLLECT_BY_DEVICE, recordBoList);
		}
		if (CollectionUtils.isNotEmpty(userCouponsIds) && StringUtils.isNotEmpty(paramBo.getUserId())) {
			// 根据用户统计使用次数
			List<ActivityRecordBo> recordBoList = findActivityRecordCount(userCouponsIds, paramBo.getUserId(), null,
					true);
			paramBo.putActivityCounter(RecordCountRuleEnum.COUPONS_COLLECT_BY_USER, recordBoList);
		}
	}

	/**
	 * @Description: 查找代金券及代金券活动已使用统计
	 * @param couponsIds
	 * @param userId
	 * @param deviceId
	 * @param isFindCollectActivity
	 * @return
	 * @author guocp
	 * @date 2017年8月4日
	 */
	private List<ActivityRecordBo> findActivityRecordCount(List<String> couponsIds, String userId, String deviceId,
			boolean isFindCollectActivity) {
		ActivityRecordParamBo recParamBo = new ActivityRecordParamBo();
		recParamBo.setPkIdList(couponsIds);
		recParamBo.setUserId(userId);
		recParamBo.setDeviceId(deviceId);
		recParamBo.setRecDate(DateUtils.getDate());
		if (isFindCollectActivity) {
			return activityCouponsRecordMapper.countCollectActivityRecord(recParamBo);
		} else {
			return activityCouponsRecordMapper.countActivityRecord(recParamBo);
		}
	}

	private String buildCouponsTitle(ActivityCoupons couponsInfo) {
		StringBuilder sb = new StringBuilder();
		if (couponsInfo.getArriveLimit().compareTo(Integer.valueOf(0)) > 0) {
			sb.append("满¥").append(couponsInfo.getArriveLimit()).append("可用");
		} else {
			sb.append("无条件使用");
		}
		if (couponsInfo.getOrderDiscountMax().compareTo(Integer.valueOf(0)) > 0) {
			// 如果有最大上限
			sb.append("，最高抵扣¥").append(couponsInfo.getOrderDiscountMax());
		}
		return sb.toString();
	}

	private void setDiscountAmount(Coupons coupons, ActivityCoupons couponsInfo, BigDecimal enjoyFavoutAmount) {
		BigDecimal discountAmount = null;
		if (couponsInfo.getType() == CouponsType.tyzkq.getValue()) {
			// 如果是折扣券，优惠金额等于享受优惠的总金额*(100-折扣比例)/100.且不得超过折扣上限
			BigDecimal orderDiscountMax = BigDecimal.valueOf(couponsInfo.getOrderDiscountMax());
			discountAmount = enjoyFavoutAmount.multiply(BigDecimal.valueOf(100 - couponsInfo.getFaceValue()))
					.divide(BigDecimal.valueOf(100), BigDecimal.ROUND_DOWN);
			if (orderDiscountMax.compareTo(BigDecimal.ZERO) > 0) {
				// 如果设置了上线，需要取最小值
				if (discountAmount.compareTo(orderDiscountMax) >= 0) {
					coupons.setIsArriveMaxLimit(1);
					discountAmount = orderDiscountMax;
				}
			}
		} else {
			discountAmount = BigDecimal.valueOf(couponsInfo.getFaceValue());
		}
		coupons.setDiscountAmount(ConvertUtil.format(discountAmount));
	}

	/**
	 * 校验代金券领取的用户状态，避免短时间内并发操作
	 * @param key 存储的redis key
	 * @param times 超时时间
	 * @return
	 */
	private boolean checkUserStatusByRedis(String key,ResultMsg msg) {
		// 如果不存在缓存数据 返回true 存在false
		boolean flag = redisTemplate.boundValueOps(key).setIfAbsent(true);
		if (flag) {
			redisTemplate.expire(key, 30, TimeUnit.SECONDS);
		} else {
			msg.setCode(104);
			msg.setMsg("用户调用次数超限");
			return false;
		}
		return true;
	}

	/**
	 * 移除校验代金券领取的用户状
	 * 
	 * @param key
	 *            存储的redis key
	 * @return
	 */
	private void removeRedisUserStatus(String key) {
		redisTemplate.delete(key);
	}

	@Override
	public void releaseConpons(TradeOrder tradeOrder) {
		Map<String, Object> params = Maps.newHashMap();
		String orderId = tradeOrder.getId();
		params.put("orderId", orderId);
		List<ActivityCouponsRecord> records = activityCouponsRecordMapper.selectByParams(params);
		if (CollectionUtils.isEmpty(records)) {
			return;
		}
		OrderStatusEnum orderState = tradeOrder.getStatus();
		for (ActivityCouponsRecord record : records) {
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

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject addRecordsByCollectId(String collectId, String userId, String invitaUserId, boolean limitOne)
			throws ServiceException {
		ResultMsg msg  = new ResultMsg();
		// 校验成功标识 //如果不存在缓存数据进行加入到缓存中
		String key = userId + collectId;
		if (!checkUserStatusByRedis(key, msg)) {
			return JSONObject.fromObject(msg);
		}
		try {
			ActivityCollectCoupons coll = activityCollectCouponsMapper.get(collectId);
			//获得领取实际操作 对象
			AbstractCouponsReceive receive=conponsReceiveFactory.produce(coll.getType());
			//执行领券操作
			msg = receive.excute(setCouponsReceiveBo(coll, userId, invitaUserId, limitOne));
			
		} catch (ServiceException e) {
			log.error("领取代金券失败，活动id:"+collectId,e);
			throw e;
		} finally {
			// 移除redis缓存的key
			removeRedisUserStatus(key);
		}
		return JSONObject.fromObject(msg);
	}

	/**
	 * @Description: 设置领券对象
	 * @param coll
	 * @param userId
	 * @param invitaUserId
	 * @param limitOne
	 * @author tuzhd
	 * @date 2017年11月23日
	 */
	private CouponsReceiveBo setCouponsReceiveBo(ActivityCollectCoupons coll,String userId,
			String invitaUserId,boolean limitOne){
		CouponsReceiveBo bo = new CouponsReceiveBo();
		bo.setColl(coll);
		bo.setCollId(coll.getId());
		bo.setInvitaUserId(invitaUserId);
		bo.setLimitOne(limitOne);
		bo.setUserId(userId);
		return bo;
	}
	
	@Override
	public void add(ActivityCouponsRecord activityCouponsRecord) {
		activityCouponsRecordMapper.insertSelective(activityCouponsRecord);
	}

	@Override
	public List<UserCouponsBo> findUserCouponsList(ActivityCouponsRecordParamBo paramBo) {
		Assert.notNull(paramBo, "查询参数对象不能为空");
		Assert.notNull(paramBo.getCollectUserId(), "领取用户id不能为空");
		// 查询用户代金券领取记录
		List<ActivityCouponsRecord> collectRecList = activityCouponsRecordMapper.findCollectRecordList(paramBo);
		if (CollectionUtils.isEmpty(collectRecList)) {
			return Lists.newArrayList();
		}
		// 构建用户代金券装载器
		UserCouponsLoader userCouponsLoader = new UserCouponsLoader();
		// 加载领取记录列表
		userCouponsLoader.loadCollectRecList(collectRecList);
		// 查询代金券列表
		List<ActivityCoupons> couponsList = activityCouponsMapper.findByIds(userCouponsLoader.extraCouponsIdList());
		userCouponsLoader.loadCouponsList(couponsList);
		// 查询领取活动信息
		List<String> couponsActIds = Lists.newArrayList();
		couponsActIds.addAll(userCouponsLoader.extraCouponsActIdList());
		List<ActivityCollectCoupons> couponsActList = activityCollectCouponsMapper.findByIds(couponsActIds);
		userCouponsLoader.loadCouponsActList(couponsActList);
		return userCouponsLoader.retrieveResult();
	}

}