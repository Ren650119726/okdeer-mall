package com.okdeer.mall.order.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsRegisteRecord;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsType;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsRegisteRecordServiceApi;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsServiceApi;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordServiceApi;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsServiceApi;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.mapper.TradeOrderMapper;
import com.okdeer.mall.order.service.OrderReturnCouponsService;

/**
 * ClassName: OrderReturnCouponsService 
 * @Description: 订单返券service实现类
 * @author wushp
 * @date 2016年10月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.1.0			2016-10-18		wushp				订单返券
 */
@Service
public class OrderReturnCouponsServiceImpl implements OrderReturnCouponsService {
	
	/**
	 * 邀请注册记录service
	 */
	@Reference(version = "1.0.0", check = false)
	private ActivityCollectCouponsRegisteRecordServiceApi couponsRegisteRecordServiceApi;
	
	/**
	 * 代金券活动service
	 */
	@Reference(version = "1.0.0", check = false)
	private ActivityCollectCouponsServiceApi activityCollectCouponsServiceApi;
	
	/**
	 * 代金券service
	 */
	@Reference(version = "1.0.0", check = false)
	private ActivityCouponsServiceApi activityCouponsServiceApi;
	
	/**
	 * 代金券领取记录service
	 */
	@Reference(version = "1.0.0", check = false)
	private ActivityCouponsRecordServiceApi  activityCouponsRecordServiceApi;
	
	/**
	 * 代金券管理mapper
	 */
	@Autowired
	private ActivityCouponsMapper activityCouponsMapper;
	
	/**
	 * 代金券领取记录mapper
	 */
	@Autowired
	private ActivityCouponsRecordMapper activityCouponsRecordMapper;
	
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void firstOrderReturnCoupons(TradeOrder tradeOrder) throws Exception {
		if (tradeOrder == null || StringUtils.isBlank(tradeOrder.getUserId())) {
			return;
		}
		// 不管支付结果如何，程序能走到这一步都走邀请注册首单送券流程
		ActivityCollectCouponsRegisteRecord registeRecord = couponsRegisteRecordServiceApi
				.selectByInviteId(tradeOrder.getUserId());
		if (registeRecord == null) {
			// 邀请注册记录为空
			return;
		}
		
		if (registeRecord.getFinishOrderTime() != null) {
			// 非首单，不送券
			return;
		}
		
		// 代金券活动
		ActivityCollectCoupons collectCoupons = activityCollectCouponsServiceApi.get(registeRecord.getActivityId());
		if (collectCoupons == null || collectCoupons.getType() != 3 || collectCoupons.getStatus() != 1) {
			// 1.代金券活动为空，2.非邀请注册送代金券活动，3.代金券活动非进行中
			return;
		}
		
		// 代金券
		List<ActivityCoupons> couponsList = activityCouponsServiceApi.selectByActivityId(collectCoupons.getId());
		if (CollectionUtils.isEmpty(couponsList)) {
			// 关联不到代金券
			return;
		}
		
		// 待插入的代金券领取记录
		List<ActivityCouponsRecord> lstCouponsRecords = new ArrayList<ActivityCouponsRecord>();
				
		for (ActivityCoupons coupons : couponsList) {
			// 剩余数量
			Integer remainNum = coupons.getRemainNum();
			if (remainNum <= 0) {
				// 剩余代金券数量不足
				return;
			}
			
			ActivityCouponsRecord recordTemp = new ActivityCouponsRecord();
			recordTemp.setCollectUserId(registeRecord.getUserId());
			recordTemp.setCouponsId(coupons.getId());
			recordTemp.setCouponsCollectId(collectCoupons.getId());
			recordTemp.setCollectType(ActivityCouponsType.invite_regist);
			// 领取总量
			int countRecord = activityCouponsRecordServiceApi.selectCountByParams(recordTemp);
			if (coupons.getEveryLimit() != 0 && countRecord >= coupons.getEveryLimit()) {
				// 已经达到限领数量
				return;
			}
			
			// 首单完成时间
			registeRecord.setFinishOrderTime(new Date());
			// 代金券活动邀请注册记录表更新首单完成时间
			couponsRegisteRecordServiceApi.updateByPrimaryKeySelective(registeRecord);
			
			// 设置代金券领取记录的代金券id、代金券领取活动id、活动类型，以便后面代码中的数量判断查询
			ActivityCouponsRecord activityCouponsRecord = new ActivityCouponsRecord();
			activityCouponsRecord.setId(UuidUtils.getUuid());
			activityCouponsRecord.setCollectType(ActivityCouponsType.invite_regist);
			activityCouponsRecord.setCouponsId(coupons.getId());
			activityCouponsRecord.setCouponsCollectId(collectCoupons.getId());
			activityCouponsRecord.setCollectTime(new Date());
			activityCouponsRecord.setCollectUserId(registeRecord.getUserId());
			activityCouponsRecord.setValidTime(DateUtils.addDays(new Date(), coupons.getValidDay()));
			activityCouponsRecord.setStatus(ActivityCouponsRecordStatusEnum.UNUSED);
			// 更新可使用的
			activityCouponsMapper.updateRemainNum(coupons.getId());
			lstCouponsRecords.add(activityCouponsRecord);
		}
		// 批量插入代金券
		if (!CollectionUtils.isEmpty(lstCouponsRecords)) {
			activityCouponsRecordMapper.insertSelectiveBatch(lstCouponsRecords);
		}
	}

}
