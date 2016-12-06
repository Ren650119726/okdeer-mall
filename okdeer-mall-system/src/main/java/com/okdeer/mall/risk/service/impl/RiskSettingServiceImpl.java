/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2016年11月4日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.risk.service.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.annotation.RocketMQListener;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.risk.entity.RiskSetting;
import com.okdeer.mall.risk.entity.RiskSettingDetail;
import com.okdeer.mall.risk.enums.IsPreferential;
import com.okdeer.mall.risk.enums.LimitDetailType;
import com.okdeer.mall.risk.enums.TriggerWay;
import com.okdeer.mall.risk.mapper.RiskSettingDetailMapper;
import com.okdeer.mall.risk.mapper.RiskSettingMapper;
import com.okdeer.mall.risk.po.RiskLimitInfo;
import com.okdeer.mall.risk.po.RiskLimitInfoDetail;
import com.okdeer.mall.risk.service.RiskSettingService;

/**
 * ClassName: RiskSettingServiceImpl 
 * @Description: 风控设置
 * @author zhangkn
 * @date 2016年11月16日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class RiskSettingServiceImpl extends BaseServiceImpl implements RiskSettingService {

	private final static Logger logger = LoggerFactory.getLogger(RiskSettingServiceImpl.class);

	private String sync = "sync";

	private final static String TOPIC = "topic_risk_setting_notity";

	/**
	 * 未使用优惠提醒限制明细
	 */
	private RiskLimitInfo warnLimitInfo = null;

	/**
	 * 未使用优惠禁止下单限制明细
	 */
	private RiskLimitInfo forbidLimitInfo = null;

	/**
	 * 使用优惠提醒限制明细
	 */
	private RiskLimitInfo preferentialWarnLimitInfo = null;

	/**
	 * 使用优惠禁止下单限制明细
	 */
	private RiskLimitInfo preferentialForbidLimitInfo = null;

	/**
	 * 是否初始化
	 */
	private boolean isInitialize = false;

	@Autowired
	private RocketMQProducer producer;

	@Autowired
	private RiskSettingMapper settingMapper;

	@Autowired
	private RiskSettingDetailMapper detailMapper;

	@Override
	public IBaseMapper getBaseMapper() {
		return settingMapper;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addBatch(List<RiskSetting> settingList, Integer isCoupon) throws Exception {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("isCoupon", isCoupon);
		List<RiskSetting> oldList = settingMapper.list(params);
		if (CollectionUtils.isNotEmpty(oldList)) {
			// 先删除detail老数据
			for (RiskSetting setting : oldList) {
				detailMapper.deleteBySettingId(setting.getId());
			}
		}
		// 再删除主表setting数据
		settingMapper.deleteByIsCoupon(isCoupon);

		// 删除完了再批量添加
		if (CollectionUtils.isNotEmpty(settingList)) {
			// detail表删除记录
			for (RiskSetting setting : settingList) {
				detailMapper.deleteBySettingId(setting.getId());
			}

			// 批量插入新纪录,(后台功能,使用频率非常少,数据量也不大,循环插入性能也不会有多大问题)
			for (RiskSetting setting : settingList) {
				settingMapper.add(setting);

				List<RiskSettingDetail> detailList = setting.getDetailList();
				if (CollectionUtils.isNotEmpty(detailList)) {
					for (RiskSettingDetail detail : detailList) {
						detailMapper.add(detail);
					}
				}
			}
		}

		// 重置本地设置项 gcp
		retrySetting();
	}

	@Override
	public List<RiskSetting> list(Map<String, Object> params) throws Exception {
		// 风控setting列表以及每个setting的明细
		List<RiskSetting> settingList = settingMapper.list(params);
		if (CollectionUtils.isNotEmpty(settingList)) {
			for (RiskSetting setting : settingList) {
				setting.setDetailList(detailMapper.listBySettingId(setting.getId()));
			}
		}
		return settingList;
	}

	/**
	 * 检查初始数据或初始化
	 * @author guocp
	 * @date 2016年11月18日
	 */
	private void initialize() {
		if (!isInitialize) {
			synchronized (sync) {
				if (!isInitialize) {
					try {
						doInitialize();
						isInitialize = true;
					} catch (Exception e) {
						logger.error("执行风控设置初始异常", e);
					}
				}
			}
		}
	}

	@Override
	public void retrySetting() {
		MQMessage anMessage = new MQMessage(TOPIC, (Serializable) "refresh");
		try {
			producer.sendMessage(anMessage);
		} catch (Exception e) {
			logger.error("更新风控设置发送消息异常", e);
		}
	}

	@Override
	@RocketMQListener(tag = "*", topic = TOPIC, consumer = "broadcastRocketMQConsumer")
	public ConsumeConcurrentlyStatus onReceive(MQMessage message) {
		logger.info("接收到风控设置更新消息");
		synchronized (sync) {
			isInitialize = false;
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

	/**
	 * 初始数据
	 * @throws Exception   
	 * @author guocp
	 * @date 2016年11月18日
	 */
	private void doInitialize() throws Exception {

		// 初始化设置对象
		this.warnLimitInfo = new RiskLimitInfo();
		this.forbidLimitInfo = new RiskLimitInfo();
		this.preferentialWarnLimitInfo = new RiskLimitInfo();
		this.preferentialForbidLimitInfo = new RiskLimitInfo();

		// 查询设置集合进行遍历
		this.list(null).forEach(riskSetting -> {
			// 判断是否使用优惠
			if (IsPreferential.YES.getCode() == riskSetting.getIsCoupon()) {
				// 判断风控类型：提醒Or禁止下单
				if (TriggerWay.NOTICE.getCode() == riskSetting.getLimitType()) {
					riskSetting.getDetailList().forEach(settingDetail -> {
						setParameters(preferentialWarnLimitInfo, settingDetail);
					});
				} else if (TriggerWay.FORBID.getCode() == riskSetting.getLimitType()) {
					riskSetting.getDetailList().forEach(settingDetail -> {
						setParameters(preferentialForbidLimitInfo, settingDetail);
					});
				}
			} else if (riskSetting.getIsCoupon() == IsPreferential.NO.getCode()) {
				// 判断风控类型：提醒Or禁止下单
				if (TriggerWay.NOTICE.getCode() == riskSetting.getLimitType()) {
					riskSetting.getDetailList().forEach(settingDetail -> {
						setParameters(warnLimitInfo, settingDetail);
					});
				} else if (TriggerWay.FORBID.getCode() == riskSetting.getLimitType()) {
					riskSetting.getDetailList().forEach(settingDetail -> {
						setParameters(forbidLimitInfo, settingDetail);
					});
				}
			}
		});
	}

	/**
	 * 设置限制信息
	 * @param detail
	 * @param riskSetting   
	 * @author guocp
	 * @date 2016年11月18日
	 */
	private void setParameters(RiskLimitInfo info, RiskSettingDetail riskSetting) {

		if (LimitDetailType.USER.getCodeStr().equals(riskSetting.getUserType())) {
			setParameters(info.getUserLimitInfoDetail(), riskSetting);
		} else if (LimitDetailType.DEVICE.getCodeStr().equals(riskSetting.getUserType())) {
			setParameters(info.getDeviceLimitInfoDetail(), riskSetting);
		} else if (LimitDetailType.PAY_ACCOUNT.getCodeStr().equals(riskSetting.getUserType())) {
			setParameters(info.getPayAccountLimitInfoDetail(), riskSetting);
		}
	}

	/**
	 * 设置参数
	 * @param detail
	 * @param riskSetting   
	 * @author guocp
	 * @date 2016年11月18日
	 */
	private void setParameters(RiskLimitInfoDetail detail, RiskSettingDetail riskSetting) {
		detail.setMaxRecharge(riskSetting.getMaxRecharge());
		detail.setMaxRechargeNumber(riskSetting.getMaxRechargeNumber());
		detail.setMaxRechargeTime(riskSetting.getMaxRechargeTime());
		detail.setMaxLoginTime(riskSetting.getMaxLoginTime());
	}

	public boolean isInitialize() {
		return isInitialize;
	}

	@Override
	public RiskLimitInfo getWarnLimit(IsPreferential isPreferential) {
		if (!isInitialize) {
			initialize();
		}
		if (isPreferential == IsPreferential.YES) {
			return preferentialWarnLimitInfo;
		}
		return warnLimitInfo;
	}

	@Override
	public RiskLimitInfo getForbidLimit(IsPreferential isPreferential) {
		if (!isInitialize) {
			initialize();
		}
		if (isPreferential == IsPreferential.YES) {
			return preferentialForbidLimitInfo;
		}
		return forbidLimitInfo;
	}

}
