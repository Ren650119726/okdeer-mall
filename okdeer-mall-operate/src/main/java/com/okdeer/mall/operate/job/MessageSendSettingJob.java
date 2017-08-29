/** 
 *@Project: yschome-mall-operate 
 *@Author: xuzq
 *@Date: 2017年8月17日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */    

package com.okdeer.mall.operate.job;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.google.common.collect.Lists;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.member.member.dto.LocateInfoQueryDto;
import com.okdeer.mall.member.member.dto.SysBuyerLocateInfoDto;
import com.okdeer.mall.member.member.service.SysBuyerLocateInfoServiceApi;
import com.okdeer.mall.operate.entity.MessageSendSelectArea;
import com.okdeer.mall.operate.entity.MessageSendSetting;
import com.okdeer.mall.operate.service.MessageSendSelectAreaService;
import com.okdeer.mall.operate.service.MessageSendSettingService;
import com.okdeer.mall.order.constant.OrderMsgConstant;
import com.okdeer.mall.order.vo.PushMsgVo;
import com.okdeer.mall.order.vo.PushUserVo;
import com.okdeer.mcm.constant.MsgConstant;

/**
 * ClassName: MessageSendSettingJob 
 * @Description: app消息推送Job
 * @author xzq
 * @date 2017年8月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *      V2.6.0         2017年8月17日                                 xzq               新增
 * 
 */
@Service
public class MessageSendSettingJob extends AbstractSimpleElasticJob {
	
	/**
	 * 日志输出
	 */
	private static final Logger logger = LoggerFactory.getLogger(MessageSendSettingJob.class);

	/**
	 * 消息推送设置service
	 */
	@Autowired
	MessageSendSettingService messageSendSettingService;
	/**
	 * 消息关联城市区域service
	 */
	@Autowired
	private MessageSendSelectAreaService messageSendSelectAreaService;
	/**
	 * 用户定位信息api
	 */
	@Reference(version = "1.0.0", check = false)
	private SysBuyerLocateInfoServiceApi sysBuyerLocateInfoApi;
	
	private static final String TOPIC = "topic_mcm_msg";
	
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
	
	@Autowired
	private RocketMQProducer rocketMQProducer;
	
	@Override
	public void process(JobExecutionMultipleShardingContext shardingContext) {
		try {
			logger.info("app消息推送定时器开始-----" + DateUtils.getDateTime());
			
			// 1 查询所有推送状态为未推送的消息列表
			List<MessageSendSetting> messageSendList = messageSendSettingService.findMessageListByStatus(0);
			if (CollectionUtils.isNotEmpty(messageSendList)) {
				for (MessageSendSetting messageSend : messageSendList) {
					//2 获取要发送的用户对象列表 
					//根据城市筛选用户
					List<String> cityIdsList =Lists.newArrayList();
					if(messageSend.getRangeType() == 1){
						List<MessageSendSelectArea> areaList = messageSendSelectAreaService.findListByMessageId(messageSend.getId());
						areaList.forEach(selectArea -> cityIdsList.add(selectArea.getCityId()));
					}
					
					// 根据用户关联发送对象 新用户 老用户等 
					LocateInfoQueryDto dto = new LocateInfoQueryDto();
					if(messageSend.getType()==3){
						SimpleDateFormat df =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date beginDate = new Date();
						Calendar date = Calendar.getInstance();
						date.setTime(beginDate);
						date.add(Calendar.DATE, - (messageSend.getSendObject()));
						
						dto.setEndTime(df.parse(df.format(date.getTime())));
					}
					dto.setType(messageSend.getType());
					dto.setCityIdList(cityIdsList);
					
					List<SysBuyerLocateInfoDto> infoList = sysBuyerLocateInfoApi.findUserList(dto);
					//3 发送消息
					sendAppMessage(messageSend,infoList);
					//4 更新发送状态
					messageSend.setStatus(1);
					messageSend.setUpdateTime(new Date());
					messageSendSettingService.update(messageSend);
				}
				
			}
			// 未开始活动，时间开始之后变更状态为已开始 end
			
		} catch (Exception e) {
			logger.error("app消息推送异常", e);
		} finally {
			logger.info("app消息推送定时器job結束-----" + DateUtils.getDateTime());
		}
	}
	
	/**
	 * @Description: 构建消息类并发送推送消息
	 * @return   
	 * @author xuzq01
	 * @param messageSend 
	 * @param infoList 
	 * @throws Exception 
	 * @date 2017年8月18日
	 */
	private void sendAppMessage(MessageSendSetting messageSend, List<SysBuyerLocateInfoDto> infoList) throws Exception {
		PushMsgVo pushMsgVo = new PushMsgVo();
		pushMsgVo.setSysCode(msgSysCode);
		pushMsgVo.setToken(msgToken);
		pushMsgVo.setServiceTypes(new Integer[] { MsgConstant.ServiceTypes.MALL_OTHER });
		// 0:用户APP,2:商家APP,3POS机
		pushMsgVo.setAppType(Constant.ZERO);
		pushMsgVo.setIsUseTemplate(Constant.ZERO);
		pushMsgVo.setMsgType(Constant.ONE);
		pushMsgVo.setMsgTypeCustom(OrderMsgConstant.APP_MESSAGE_SEND);

		// 不使用模板 设置消息名称
		pushMsgVo.setMsgNotifyContent(messageSend.getMessageName());
		//消息详情类型： 0 链接详情，1内容详情
		pushMsgVo.setMsgDetailType(Constant.ONE);
		pushMsgVo.setMsgDetailContent(messageSend.getContext());
		// 设置是否定时发送 定时发送
		pushMsgVo.setIsTiming(Constant.ZERO);
		//发送时间无需设置 会立即发送
		//SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//pushMsgVo.setSendTime(format.format(new Date(messageSend.getSendTime().getTime() - time)));

		// 发送用户
		List<PushUserVo> userList = new ArrayList<PushUserVo>();
		infoList.forEach(user -> {
			if(user.getUserPhone() != null){
				PushUserVo pushUser = new PushUserVo();
				pushUser.setUserId(user.getUserId());
				pushUser.setMsgType(Constant.ONE);
				//设置手机号
				pushUser.setMobile(user.getUserPhone());
				
				userList.add(pushUser);
			}
		});
		// 查询的用户信息
		pushMsgVo.setUserList(userList);
		sendMessage(pushMsgVo);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void sendMessage(Object entity) throws Exception {
		MQMessage anMessage = new MQMessage(TOPIC, (Serializable)JsonMapper.nonDefaultMapper().toJson(entity));
		rocketMQProducer.sendMessage(anMessage);
	}
}
