/** 
 *@Project: okdeer-mall-system 
 *@Author: guocp
 *@Date: 2016年11月17日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.risk.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.mall.risk.entity.RiskOrderRecord;
import com.okdeer.mall.risk.entity.RiskTriggerRecord;
import com.okdeer.mall.risk.enums.TriggerType;
import com.okdeer.mall.risk.enums.TriggerWay;
import com.okdeer.mall.risk.mq.constants.RiskTriggerTopic;
import com.okdeer.mall.risk.po.RiskLimitInfo;
import com.okdeer.mall.risk.po.RiskLimitInfoDetail;
import com.okdeer.mall.risk.po.RiskOrderRecordPo;
import com.okdeer.mall.risk.service.RiskBlackService;
import com.okdeer.mall.risk.service.RiskOrderRecordService;
import com.okdeer.mall.risk.service.RiskSettingService;
import com.okdeer.mall.risk.service.RiskTriggerConditions;
import com.okdeer.mall.risk.service.RiskWhiteService;

/**
 * ClassName: RiskConditionsTriggerImpl 
 * @Description: 风控条件触发过滤
 * @author guocp
 * @date 2016年11月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class RiskTriggerConditionsImpl implements RiskTriggerConditions {

	private static final Logger log = LoggerFactory.getLogger(RiskTriggerConditionsImpl.class);

	@Autowired
	private RiskOrderRecordService riskOrderRecordService;

	@Autowired
	private RiskSettingService riskSettingService;

	@Autowired
	private RiskBlackService riskBlackService;

	@Autowired
	private RiskWhiteService riskWhiteService;

	@Autowired
	private RocketMQProducer rocketMQProducer;

	@Override
	public boolean isTrigger(RiskOrderRecord riskOrder) throws Exception {

		log.debug("进入风控条件检测中,订单信息：{}",JsonMapper.nonDefaultMapper().toJson(riskOrder));
		//解决临时问题，屏蔽云钱包未传支付ID问题，后期可以去掉
		if (riskOrder.getPayAccount() == null) {
			return true;
		}
		// 新增订单记录
		riskOrderRecordService.add(riskOrder);

		Set<String> blackLoginAccount = riskBlackService.findAllBlackLoginAccount();
		Set<String> blackMobiles = riskBlackService.findAllBlackMobile();
		Set<String> blackDevices = riskBlackService.findAllBlackDevice();
		Set<String> blackPayAccounts = riskBlackService.findAllBlackPayAccount();
		// 判断黑名单
		if (blackLoginAccount.contains(riskOrder.getLoginName()) || blackMobiles.contains(riskOrder.getTel())
				|| blackDevices.contains(riskOrder.getDeviceId())
				|| blackPayAccounts.contains(riskOrder.getPayAccount())) {
			log.info("============登录账号================"+blackLoginAccount.contains(riskOrder.getLoginName()));
			log.info("=============充值号码==============="+blackMobiles.contains(riskOrder.getTel()));
			log.info("===============设备id============="+blackDevices.contains(riskOrder.getDeviceId()));
			log.info("=============支付账号==============="+blackPayAccounts.contains(riskOrder.getPayAccount()));
			log.info("充值订单人员在黑名单中，跳过风控，用户登入名：{}",riskOrder.getLoginName());
			return true;
		}
		// 判断白名单
		Set<String> whites = riskWhiteService.findAllWhite();
		if (whites.contains(riskOrder.getLoginName())) {
			log.info("充值订单人员在白名单中，跳过风控，用户登入名：{}",riskOrder.getLoginName());
			return false;
		}

		return filter(riskOrder);
	}

	/**
	 * 风控过滤
	 * @param riskOrder
	 * @return   
	 * @author guocp
	 * @date 2016年11月19日
	 */
	public boolean filter(RiskOrderRecord riskOrder) {
		// 判断是否触发次数限制--使用优惠
		RiskOrderRecordPo loginNameRecord = riskOrderRecordService.findByLoginName(riskOrder.getLoginName(),
				riskOrder.getIsPreferential());
		RiskOrderRecordPo deviceRecord = riskOrderRecordService.findByDeviceId(riskOrder.getDeviceId(),
				riskOrder.getIsPreferential());
		RiskOrderRecordPo payAccountRecord = riskOrderRecordService.findByPayAccount(riskOrder.getPayAccount(),
				riskOrder.getIsPreferential());

		TriggerType triggerType = null;
		TriggerWay triggerWay = TriggerWay.FORBID;
		// 禁止下单操作
		RiskLimitInfo forbidLimitInfo = riskSettingService.getForbidLimit(riskOrder.getIsPreferential());

		// 用户登入账号验证
		if (triggerType==null) {
			triggerType = filter(loginNameRecord, forbidLimitInfo.getUserLimitInfoDetail());
		}
		// 用户设备号验证
		if (triggerType==null) {
			triggerType = filter(deviceRecord, forbidLimitInfo.getDeviceLimitInfoDetail());
		}
		// 用户支付账号验证
		if (triggerType==null) {
			triggerType = filter(payAccountRecord, forbidLimitInfo.getPayAccountLimitInfoDetail());
		}

		// ******* 提醒操作 **********
		if (triggerType==null) {
			triggerWay = TriggerWay.NOTICE;
			RiskLimitInfo warnLimitInfo = riskSettingService.getWarnLimit(riskOrder.getIsPreferential());
			// 用户登入账号验证
			if (triggerType==null) {
				triggerType = filter(loginNameRecord, warnLimitInfo.getUserLimitInfoDetail());
			}
			// 用户设备号验证
			if (triggerType==null) {
				triggerType = filter(deviceRecord, warnLimitInfo.getDeviceLimitInfoDetail());
			}
			// 用户支付账号验证
			if (triggerType==null) {
				triggerType = filter(payAccountRecord, warnLimitInfo.getPayAccountLimitInfoDetail());
			}
		}

		// 是否触发风控
		if (triggerType != null) {
			log.info("用户：{}充值触发风控，触发类型：{}",riskOrder.getLoginName(),triggerType.getDesc());
			sendTriggerMessage(riskOrder, triggerType, triggerWay);
		}
		return triggerType!=null && triggerWay == TriggerWay.FORBID;
	}

	/**
	 * 非代金券禁止下单限制
	 * @param riskOrders
	 * @return   
	 * @author guocp
	 * @date 2016年11月18日
	 */
	public TriggerType filter(RiskOrderRecordPo riskOrders, RiskLimitInfoDetail detail) {
		// 开启禁止下次操作限制
		TriggerType triggerType = null;
		if (detail != null) {
			// 判断用户下单次数上限
			if (triggerType==null && detail.getMaxRechargeTime() != null) {
				if (riskOrders.getCount() > detail.getMaxRechargeTime()) {
					triggerType = TriggerType.COUNT_LIMIT;
				}
			}
			// 判断用户下单额度上限
			if (triggerType==null && detail.getMaxRecharge() != null) {
				if (riskOrders.getFacePriceTotal()
						.compareTo(BigDecimal.valueOf(detail.getMaxRecharge().longValue())) > 0) {
					triggerType = TriggerType.TOTAL_LIMIT;
				}
			}
			// 判断用户充值手机号上限
			if (triggerType==null && detail.getMaxRechargeNumber() != null) {
				if (riskOrders.getTels().size() > detail.getMaxRechargeNumber()) {
					triggerType = TriggerType.TEL_LIMIT;
				}
			}
			// 判断设备登入用户上限
			if (triggerType==null && detail.getMaxLoginTime() != null) {
				if (riskOrders.getLoginNames().size() > detail.getMaxLoginTime()) {
					triggerType = TriggerType.DEVICE_LIMIT;
				}
			}
		}
		return triggerType;
	}

	/**
	 * 触发风控发送MQ消息
	 * @param record   
	 * @author guocp
	 * @date 2016年11月19日
	 */
	public void sendTriggerMessage(RiskOrderRecord riskOrder, TriggerType triggerType, TriggerWay triggerWay) {

		RiskTriggerRecord record = new RiskTriggerRecord();
		record = BeanMapper.map(riskOrder, RiskTriggerRecord.class);
		record.setId(UuidUtils.getUuid());
		record.setTriggerType(triggerType);
		record.setTriggerWay(triggerWay);
		MQMessage anMessage = new MQMessage(RiskTriggerTopic.TOPIC_RISK_TRIGGER, (Serializable) record);
		try {
			rocketMQProducer.sendMessage(anMessage);
		} catch (Exception e) {
			log.error("订单触发风控发送消息异常:{}", JsonMapper.nonEmptyMapper().toJson(record), e);
		}
	}

}
