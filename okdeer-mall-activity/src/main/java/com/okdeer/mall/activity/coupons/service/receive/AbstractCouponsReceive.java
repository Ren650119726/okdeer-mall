package com.okdeer.mall.activity.coupons.service.receive;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Lists;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecordBefore;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsType;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordBeforeMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsReceiveStrategy;
import com.okdeer.mall.activity.coupons.service.receive.bo.CouponsReceiveBo;
import com.okdeer.mall.activity.dto.ActivityCouponsRecordBeforeParamDto;
import com.okdeer.mall.activity.dto.ActivityCouponsRecordQueryParamDto;
import com.okdeer.mall.common.entity.ResultMsg;
import com.okdeer.mall.common.enums.GetUserType;
import com.okdeer.mall.order.dto.TradeOrderParamDto;
import com.okdeer.mall.order.service.TradeOrderServiceApi;

/**
 * ClassName: AbstractCheckCouponsReceive 
 * @Description: 公共校验代金券领取
 * @author tuzhd
 * @date 2017年11月23日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		2.7				2017-11-23			tuzhd			公共校验代金券领取
 */
public abstract class AbstractCouponsReceive implements CouponsReceiveHandler {
	
	/**
	 * 代金券记录
	 */
	@Autowired
	private ActivityCouponsRecordMapper activityCouponsRecordMapper;
	
	@Reference(version = "1.0.0", check = false)
	private TradeOrderServiceApi tradeOrderService;
	
	@Resource
	private ActivityCouponsReceiveStrategy activityCouponsReceiveStrategy;
	/**
	 * 代金券管理mapper
	 */
	@Autowired
	private ActivityCouponsMapper activityCouponsMapper;
	

	@Autowired
	private ActivityCouponsRecordBeforeMapper activityCouponsRecordBeforeMapper;
	
	
	
	/**
	 * 抽象执行检查入口
	 * @throws ServiceException 
	 */
	@Override
	public ResultMsg excute(CouponsReceiveBo bo) throws ServiceException{
		ResultMsg result = new ResultMsg();
		//1、检查公共代金券活动 校验
		if(!checkCouponsCollect(bo,result)){
			return result;
		}
		
		//2、检查代金券公共日领取校验 一个活动多张券检验一个代金券即可知道是否超过日发行量
		List<ActivityCoupons> activityCoupons = activityCouponsMapper.selectByActivityId(bo.getCollId());
		if(CollectionUtils.isEmpty(activityCoupons)){
			result.setMsg("非法参数！");
			result.setCode(105);
			return result;
		}
		//3、校验代金卷的日领取量
		if (!checkDaliyRecord(activityCoupons.get(0).getId(),bo,result)) {
			return result;
		}
		//4、判断是否是成功，成功则进行批量保存代金劵
		Map<String, Object> reMap = new HashMap<>();
		if (!checkCouponsList(activityCoupons, reMap, bo, result)) {
			return result;
		}
		//5、循环进行代金劵插入
		updateCouponsRecode(reMap, activityCoupons, bo);

		//6、处理其他操作    如广告  存在邀请人用户id需要确认是否记录邀请人
		updateOtherRecode(bo);
		result.setMsg("恭喜你，领取成功！");
		result.setCode(100);
		return result;
	}
	
	/**
	 * @Description: 检查代金券活动
	 * @param collId 代金券活动id
	 * @author tuzhd
	 * @date 2017年11月23日
	 */
	private boolean checkCouponsCollect(CouponsReceiveBo bo,ResultMsg result){
		if (bo.getColl() == null) {
			result.setMsg("非法参数！");
			result.setCode(105);
			return false;
		}
		//检验代金券活动 是否是新人专项
		if(!checkCollectPublic(bo, result)){
			return false;
		}
		
		//检查是否预领取过了
		if(bo.isLimitOne() && checkBeforeCoupons(bo)){
			result.setCode(102);
			result.setMsg("您已经领取了，快去友门鹿app注册使用吧！");
			return false;
		}
		
		// 查询该用户已领取,存在未使用的新人代金券不可再领取，limitOne为false表示可以领取多次新人代金券活动
		if (bo.isLimitOne() && StringUtils.isNotBlank(bo.getUserId()) && checkNewUserCoupons(bo)) {
			result.setMsg("您已经领取了，快去我的代金券查看使用吧！");
			result.setCode(102);
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * @DESC 校验预领取记录 1、存在未使用的新人代金券则 不能领取返回false 2、持续的活动领取过不能再领取
	 * @param phone   手机号
	 * @param collectId  代金券活动id
	 * @return
	 */
	private boolean checkNewUserCoupons(CouponsReceiveBo bo) {
		// 如果领取为限新人使用 则校验是否领取过新人代金券
		if (bo.getColl() != null && bo.getColl().getGetUserType() == GetUserType.ONlY_NEW_USER) {
			// 查询该用户已领取， 新人限制， 未使用，的代金劵活动的代金劵数量
			int countCoupons = activityCouponsRecordMapper.findcountByNewType(GetUserType.ONlY_NEW_USER, bo.getUserId(),
					ActivityCouponsRecordStatusEnum.UNUSED);
			// 存在未使用的新人代金券则 返回true
			return countCoupons > 0;
		}
		return false;
	}
	
	/**
	 * @Description: 进行公共代金劵活动校验
	 * @param collect 活动id
	 * @param userId 用户id
	 * @author tuzhd
	 * @date 2017年6月30日
	 */
	private boolean checkCollectPublic(CouponsReceiveBo bo,ResultMsg result) {
		ActivityCollectCoupons collect = bo.getColl();
		if (collect == null || collect.getStatus() == null || collect.getStatus().intValue() != 1) {
			// 如果不存在活动都当成活动已结束2
			int status = (collect != null && collect.getStatus() != null) ? collect.getStatus().intValue() : 2;
			result.setMsg(status == 0 ? DateUtils.dateFormat(collect.getStartTime()) : "活动已结束！");
			result.setCode(status == 0 ? 115 : 105);
			return false;
		}
		// 根据用户id查询其订单完成的订单总量 大于 0 下过单 就不算新用户
		if (collect.getGetUserType() == GetUserType.ONlY_NEW_USER) {
			TradeOrderParamDto param = new TradeOrderParamDto();
			param.setUserId(bo.getUserId());
			// 如果用户存在单没有下过单一样送券
			if (StringUtils.isNotBlank(bo.getUserId())&& tradeOrderService.selectCountByUserStatus(param) > 0) {
				result.setCode(106);
				result.setMsg("该活动为新用户专享！");
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * @DESC 校验预领取记录 1、存在未使用的新人代金券则 不能领取返回false 2、持续的活动领取过不能再领取
	 * @param phone 手机号
	 * @param collectId 代金券活动id
	 * @return
	 */
	public boolean checkBeforeCoupons(CouponsReceiveBo bo) {
		return true;
	}
	
	/**
	 * @Description: 校验代金券集合
	 * @param activityCoupons
	 * @param reMap 返回检验成功的记录
	 * @param userId
	 * @author tuzhd
	 * @date 2017年11月23日
	 */
	private boolean checkCouponsList(List<ActivityCoupons> activityCoupons,Map<String,Object> reMap,
			CouponsReceiveBo bo,ResultMsg result){
		boolean checkFlag = true;
		for (ActivityCoupons coupons : activityCoupons) {
			// 设置代金券领取记录的代金券id、代金券领取活动id、活动类型，以便后面代码中的数量判断查询
			// 进行公共代金劵领取校验
			if (!checkRecordPubilc(coupons, bo.getUserId(),result)) {
				checkFlag = false;
				break;
			}
			//创建代金券记录用于保存
			createRecord(coupons, reMap, bo);
			checkFlag = true;
		}
		return checkFlag;
	}
	
	//创建代金券记录用于保存
	private void createRecord(ActivityCoupons coupons,Map<String,Object> reMap,CouponsReceiveBo bo){
		//用户id存在保存到领取记录表
		if(StringUtils.isNotBlank(bo.getUserId())){
			ActivityCouponsRecord record = new ActivityCouponsRecord();
			record.setCollectType(ActivityCouponsType.enumValueOf(bo.getColl().getType()));
			record.setCouponsId(coupons.getId());
			record.setCouponsCollectId(bo.getColl().getId());
			record.setCollectUserId(bo.getUserId());
			record.setCollectTime(new Date());
			record.setCollectUserId(bo.getUserId());
			reMap.put(coupons.getId(), record);
		
		//如果手机号存在保存到预领取记录中
		}else if(StringUtils.isNotBlank(bo.getPhone())){
			ActivityCouponsRecordBefore record = new ActivityCouponsRecordBefore();
			record.setCollectType(ActivityCouponsType.enumValueOf(bo.getColl().getType()));
			// 预领取记录中 手机号码 、 邀请人id 、广告活动id
			record.setCollectUser(bo.getPhone());
			record.setCollectTime(new Date());
			record.setInviteUserId(bo.getInvitaUserId());
			record.setActivityId(bo.getActivityId());
			record.setIsComplete(WhetherEnum.not);
			record.setCouponsCollectId(bo.getCollId());
			record.setCouponsId(coupons.getId());
			reMap.put(coupons.getId(), record);
		}
	}
	
	/**
	 * @Description: 进行公共代金劵领取校验
	 * @param activityCoupons 代金券信息
	 * @param userId 用户id
	 * @param coll 活动信息
	 * @author tuzhd
	 * @date 2017年11月23日
	 */
	public boolean checkRecordPubilc(ActivityCoupons coupons,String userId,ResultMsg result) {
		if (coupons.getRemainNum() <= 0) {
			// 剩余数量小于0 显示已领完
			result.setMsg("该代金券已经领完了！");
			result.setCode(101);
			return false;
		}

		ActivityCouponsRecordQueryParamDto redParamDto = new ActivityCouponsRecordQueryParamDto();
		redParamDto.setCollectUserId(userId);
		redParamDto.setCouponsId(coupons.getId());
		redParamDto.setCouponsCollectId(coupons.getActivityId());
		redParamDto.setCollectStartTime(DateUtils.getDateStart(new Date()));
		redParamDto.setCollectEndTime(DateUtils.getDateEnd(new Date()));
		if (StringUtils.isNotBlank(userId) && 
				activityCouponsRecordMapper.selectCountByParams(redParamDto) >= coupons.getEveryLimit().intValue()) {
			// 已领取
			result.setMsg("每人限领" + coupons.getEveryLimit() + "张，不要贪心哦！");
			result.setCode(102);
			return false;
		}
		return true;

	}
	
	/**
	 * @Description: 校验代金卷的日领取量 一个代金券活动领取多张代金券，取其中一个校验即可
	 * @param activityCouponsRecordQueryParamDto
	 * @param collect
	 * @author tuzhd
	 * @date 2017年11月23日
	 */
	private boolean checkDaliyRecord(String couponsId,CouponsReceiveBo bo,ResultMsg result) {
		ActivityCouponsRecordQueryParamDto redParamDto = new ActivityCouponsRecordQueryParamDto();
		redParamDto.setCouponsId(couponsId);
		redParamDto.setCouponsCollectId(bo.getColl().getId());
		redParamDto.setCollectStartTime(new Date());
		redParamDto.setCollectEndTime(new Date());
		redParamDto.setCollectType(bo.getColl().getType());
		
		// 当前日期已经领取的数量
		int dailyCirculation = activityCouponsRecordMapper.selectCountByParams(redParamDto);
		ActivityCouponsRecordBeforeParamDto beforeDto = new ActivityCouponsRecordBeforeParamDto();
		BeanMapper.copy(redParamDto, beforeDto);
		// 当前代金劵日已经预领取领取的数量
		int dailyBefore = activityCouponsRecordBeforeMapper.getCountByDayParams(beforeDto);
		dailyCirculation = dailyCirculation + dailyBefore;
		
		String collDaily = bo.getColl().getDailyCirculation();
		// 获取当前登陆用户已领取的指定代金券数量
		if (StringUtils.isNotBlank(collDaily) && dailyCirculation >= Integer.parseInt(collDaily)) {
			result.setMsg("来迟啦！券已抢完，明天早点哦");
			result.setCode(104);
			return false;
		}
		return true;
	}
	
	/**
	 * @Description: 更新保存领取代金劵记录
	 * @param record 代金券记录
	 * @param coupons   
	 * @author tuzhd
	 * @throws ServiceException 
	 * @date 2017年11月23日
	 */
	private void updateCouponsRecode(Map<String,Object> reMap, List<ActivityCoupons> activityCoupons,
			CouponsReceiveBo bo) throws ServiceException {
		insertRecordInfo(reMap, activityCoupons, bo);
	}
	
	//插入代金券
	private void insertRecordInfo(Map<String,Object> reMap, List<ActivityCoupons> activityCoupons,
			CouponsReceiveBo bo) throws ServiceException{
		List<String> ids  = Lists.newArrayList();
		//用户id存在保存到领取记录表
		if(StringUtils.isNotBlank(bo.getUserId())){
			List<ActivityCouponsRecord> list = Lists.newArrayList();
			for (ActivityCoupons coupons : activityCoupons) {
				ActivityCouponsRecord record= (ActivityCouponsRecord) reMap.get(coupons.getId());
				//存在设置属性
				if(record == null){
					continue;
				}
				record.setId(UuidUtils.getUuid());
				activityCouponsReceiveStrategy.process(record, coupons);
				list.add(record);
				ids.add(coupons.getId());
			}
			if(CollectionUtils.isNotEmpty(list)){
				//批量将代金卷兑换码写入记录表
				activityCouponsRecordMapper.insertSelectiveBatch(list);
			}
		
		//如果手机号存在保存到预领取记录中
		}else if(StringUtils.isNotBlank(bo.getPhone())){
			List<ActivityCouponsRecordBefore> list = Lists.newArrayList();
			for (ActivityCoupons coupons : activityCoupons) {
				ActivityCouponsRecordBefore record= (ActivityCouponsRecordBefore) reMap.get(coupons.getId());
				//存在设置属性
				if(record == null){
					continue;
				}
				// 立即领取
				record.setId(UuidUtils.getUuid());
				record.setCollectTime(new Date());
				record.setEffectTime(activityCouponsReceiveStrategy.getEffectTime(coupons));
				record.setStatus(ActivityCouponsRecordStatusEnum.UNUSED);
				record.setValidTime(activityCouponsReceiveStrategy.getExpireTime(coupons));
				list.add(record);
				ids.add(coupons.getId());
			}
			if(CollectionUtils.isNotEmpty(list)){
				activityCouponsRecordBeforeMapper.insertSelectiveBatch(list);
			}
			
		}
			
		//批量更新 代金卷领取数量
		if(CollectionUtils.isNotEmpty(ids)){
			int count = activityCouponsMapper.updateAllRemainNum(ids);
			if (count < ids.size()) {
				throw new ServiceException("添加代金卷记录失败!id:"+ids);
			}
		}
	}
	
	/**
	 * @Description: 插入要求人信息
	 * @param bo 为处理需要的参数
	 * @author tuzhd
	 * @date 2017年11月23日
	 */
	public void updateOtherRecode(CouponsReceiveBo bo){
		//需要的子类进行重写
	}
	
}
