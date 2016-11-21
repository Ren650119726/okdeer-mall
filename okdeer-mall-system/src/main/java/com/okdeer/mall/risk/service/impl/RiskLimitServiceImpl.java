/** 
 *@Project: okdeer-mall-system 
 *@Author: guocp
 *@Date: 2016年11月18日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.risk.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.mall.risk.entity.RiskSettingDetail;
import com.okdeer.mall.risk.enums.IsPreferential;
import com.okdeer.mall.risk.enums.LimitDetailType;
import com.okdeer.mall.risk.enums.TriggerWay;
import com.okdeer.mall.risk.po.RiskLimitInfo;
import com.okdeer.mall.risk.po.RiskLimitInfoDetail;
import com.okdeer.mall.risk.service.RiskLimitService;
import com.okdeer.mall.risk.service.RiskSettingService;

/**
 * ClassName: RiskLimit 
 * @Description: 风控限制设置服务
 * @author guocp
 * @date 2016年11月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class RiskLimitServiceImpl implements RiskLimitService {

	private final static Logger logger = LoggerFactory.getLogger(RiskLimitServiceImpl.class);
	
	private String sync = "sync";
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
	private RiskSettingService riskSettingService;

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
		synchronized (sync) {
			isInitialize = false;
		}
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
		riskSettingService.list(null).forEach(riskSetting -> {
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
