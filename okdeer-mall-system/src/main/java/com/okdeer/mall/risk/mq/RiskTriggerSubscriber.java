/** 
 *@Project: okdeer-mall-system 
 *@Author: guocp
 *@Date: 2016年11月19日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.risk.mq;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.framework.mq.annotation.RocketMQListener;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.base.redis.IRedisTemplateWrapper;
import com.okdeer.mall.risk.entity.RiskTriggerRecord;
import com.okdeer.mall.risk.entity.RiskUserManager;
import com.okdeer.mall.risk.enums.IsPreferential;
import com.okdeer.mall.risk.mq.constants.RiskTriggerTopic;
import com.okdeer.mall.risk.service.RiskTriggerRecordService;
import com.okdeer.mall.risk.service.RiskUserManagerService;
import com.okdeer.mcm.entity.EmailVO;
import com.okdeer.mcm.entity.SmsVO;
import com.okdeer.mcm.service.IEmailService;
import com.okdeer.mcm.service.ISmsService;

/**
 * ClassName: RiskTriggerSubscriber 
 * @Description: 订阅风控触发消息
 * @author guocp
 * @date 2016年11月19日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class RiskTriggerSubscriber {

	private static final Logger log = LoggerFactory.getLogger(RiskTriggerSubscriber.class);

	// 是否启用
	private static final int YES = 1;

	// 发送短信间隔时间（分钟）
	private static final long INTERCEPT_TIME = 12 * 60;

	// 记录发送短信间隔redis key
	private static final String RISK_RISK_TRIGGER_MSG = "MALL:RISK:TRIGGER:MSG";

	@Value(value = "${sms.risk.notice}")
	private String risk_msg_template;

	@Value(value = "${email.risk.notice.title}")
	private String risk_email_template_title;

	@Value(value = "${email.risk.notice.content}")
	private String risk_email_template_content;

	@Value("${mcm.sys.code}")
	private String mcmSysCode;

	@Value("${mcm.sys.token}")
	private String mcmSysToken;

	@Reference(version = "1.0.0", check = false)
	private ISmsService smsService;

	@Reference(version = "1.0.0", check = false)
	private IEmailService emailService;

	@Resource
	private IRedisTemplateWrapper<String, Date> redisTemplateWrapper;

	/**
	 * 风控触发记录服务
	 */
	@Autowired
	private RiskTriggerRecordService riskTriggerRecordService;

	/**
	 * 风控管理人员列表服务
	 */
	@Autowired
	private RiskUserManagerService riskUserManagerService;

	@RocketMQListener(topic = RiskTriggerTopic.TOPIC_RISK_TRIGGER, tag = "*")
	public ConsumeConcurrentlyStatus trigger(MQMessage enMessage) {

		RiskTriggerRecord triggerRecord = (RiskTriggerRecord) enMessage.getContent();
		log.debug("风控触发信息：{}", JsonMapper.nonEmptyMapper().toJson(triggerRecord));
		try {
			// 保存风控触发记录
			riskTriggerRecordService.add(triggerRecord);

			// 发送短信和邮件
			sendMessage(triggerRecord);
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		} catch (Exception e) {
			log.error("风控触发信息保存失败：{}", JsonMapper.nonEmptyMapper().toJson(triggerRecord), e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
	}

	/**
	 * 发送消息
	 * @param triggerRecord   
	 * @author guocp
	 * @date 2016年11月19日
	 */
	private void sendMessage(RiskTriggerRecord triggerRecord) {

		// 判断今天是否已经发过短信提醒
		boolean isSendMsg = false;
		if (!redisTemplateWrapper.exists(RISK_RISK_TRIGGER_MSG)) {
			isSendMsg = true;
			redisTemplateWrapper.set(RISK_RISK_TRIGGER_MSG, triggerRecord.getCreateTime(), INTERCEPT_TIME);
		}

		// 发送提醒
		List<RiskUserManager> users = riskUserManagerService.findUserList(null);
		for (RiskUserManager riskUser : users) {
			// 发送短信
			if (isSendMsg && riskUser.getIsAcceptMail() == YES) {
				sendMsg(riskUser.getTelephone(), triggerRecord.getCreateTime());
				sendMsg(riskUser.getTelephone(), triggerRecord.getCreateTime());
			}
			// 发送邮件
			if (riskUser.getIsAcceptMail() == YES) {
				sendEmail(riskUser.getEmail(), triggerRecord);
			}
		}
	}

	private void sendEmail(final String email, RiskTriggerRecord triggerRecord) {
		String content = risk_email_template_content
				.replace("#1", DateUtils.formatDateTime(triggerRecord.getCreateTime()))
				.replace("#2", triggerRecord.getIsPreferential() == IsPreferential.NO ? "没有" : "").replace("#3", "")
				.replace("#4", triggerRecord.getTriggerType().getDesc());
		EmailVO emailVo = new EmailVO();
		emailVo.setContent(content);
		emailVo.setEmail(email);
		emailVo.setId(UuidUtils.getUuid());
		emailVo.setToken(mcmSysToken);
		emailVo.setSysCode(mcmSysCode);
		emailVo.setIsTiming(0);
		emailVo.setTitle(risk_email_template_title);
		emailService.sendEmail(emailVo);
	}

	private void sendMsg(final String mobile, Date date) {
		SmsVO smsVo = new SmsVO();
		smsVo.setId(UuidUtils.getUuid());
		// smsVo.setUserId(user.getId());
		smsVo.setIsTiming(0);
		smsVo.setToken(mcmSysToken);
		smsVo.setSysCode(mcmSysCode);
		smsVo.setMobile(mobile);
		smsVo.setContent(risk_msg_template.replace("#1", DateUtils.formatDateTime(date)));
		smsVo.setSmsChannelType(3);
		smsVo.setSendTime(DateUtils.formatDateTime(new Date()));
		smsService.sendSms(smsVo);
	}

}
