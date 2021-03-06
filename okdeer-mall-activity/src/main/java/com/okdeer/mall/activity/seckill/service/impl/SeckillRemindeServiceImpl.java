/** 
 *@Project: okdeer-mall-activity 
 *@Author: yangq
 *@Date: 2016年9月28日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */

package com.okdeer.mall.activity.seckill.service.impl;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.mall.activity.seckill.entity.SeckillReminde;
import com.okdeer.mall.activity.seckill.mapper.SeckillRemindeMapper;
import com.okdeer.mall.activity.seckill.service.SeckillRemindeServiceApi;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.order.constant.OrderMsgConstant;
import com.okdeer.mall.order.vo.PushMsgVo;
import com.okdeer.mall.order.vo.PushUserVo;
import com.okdeer.mall.order.vo.SendMsgParamVo;
import com.okdeer.mall.system.mapper.SysBuyerUserMapper;
import com.okdeer.mcm.constant.MsgConstant;

/**
 * ClassName: SeckillRemindeServiceImpl 
 * @Dcom.okdeer.base.common.utils.mapper.JsonMapper016年9月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.seckill.service.SeckillRemindeServiceApi",timeout=60000)
public class SeckillRemindeServiceImpl implements SeckillRemindeServiceApi {

	
	
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
	
	private static final String TOPIC = "topic_mcm_msg";
    
	@Autowired
	private RocketMQProducer rocketMQProducer;

	/**取消订单短信1*/
	@Value("${sms.cancalOrder.style1}")
	private String smsCancalOrderStyle1;

	/**取消订单短信2*/
	@Value("${sms.cancalOrder.style2}")
	private String smsCancalOrderStyle2;

	/**取消订单短信3*/
	@Value("${sms.cancalOrder.style3}")
	private String smsCancalOrderStyle3;

	/**商家同意退款短信1*/
	@Value("${sms.agreeRefundPay.style1}")
	private String smsAgreeRefundPayStyle1;

	/**商家同意退款短信2*/
	@Value("${sms.agreeRefundPay.style2}")
	private String smsAgreeRefundPayStyle2;

	/**商家同意退款短信3*/
	@Value("${sms.agreeRefundPay.style3}")
	private String smsAgreeRefundPayStyle3;

	/**用户拒收短信-第三方支付*/
	@Value("${sms.userRefuse.thirdPay}")
	private String smsUserRefuseThirdPay;

	/**用户拒收短信-云钱包支付*/
	@Value("${sms.userRefuse.walletPay}")
	private String smsUserRefuseWalletPay;

	/**用户拒收短信-货到付款*/
	@Value("${sms.userRefuse.unPay}")
	private String smsUserRefuseUnPay;

	/**商家点击发货短信*/
	@Value("${sms.shipments.style1}")
	private String smsShipmentsStyle1;

	/**到店自提订单下单短信*/
	@Value("${sms.order.store.pickup.style1}")
	private String smsOrderStorePickupStyle1;

	/**团购服务型商品下单短信*/
	@Value("${sms.server.order.style1}")
	private String smsServerOrderStyle1;

	/**商家版APP订单详情链接*/
	@Value("${orderDetailLink}")
	private String orderDetailLink;

	/**商家版APP退款单详情链接*/
	@Value("${orderRefundsDetailLink}")
	private String orderRefundsDetailLink;

	/**
	 * Android客户端自定义通知样式，如果没有，可以默认为0
	 */
	@Value("${notification.builder.id}")
	private String notificationBuilderId;

	/**
	 * 只有notificationBuilderId为0时有效
	 *  响铃：4;振动：2;可清除：1;
	 *  可组合相加
	 *  响铃
	 */
	@Value("${notification.basic.style1}")
	private String notificationBasicStyle1;

	/**
	 * 只有notificationBuilderId为0时有效
	 *  响铃：4;振动：2;可清除：1;
	 *  可组合相加
	 *  震动
	 */
	@Value("${notification.basic.style2}")
	private String notificationBasicStyle2;

	// Begin 重构4.1 add by wusw
	/**
	 * 商家点击派单发送短信（服务店）
	 */
	@Value("${sms.service.store.shipments.style1}")
	private String smsServiceStoreShipmentsStyle1;

	/**
	 * 商家点击拒绝服务发送短信（服务店，在线支付--微信支付、支付宝支付）
	 */
	@Value("${sms.service.store.refuse.style1}")
	private String smsServiceStoreRefuseStyle1;

	/**
	 * 商家点击拒绝服务发送短信（服务店，在线支付--余额支付、京东支付）
	 */
	@Value("${sms.service.store.refuse.style2}")
	private String smsServiceStoreRefuseStyle2;

	/**
	 * 商家点击拒绝服务发送短信（服务店，线下支付并当面确认价格）
	 */
	@Value("${sms.service.store.refuse.style3}")
	private String smsServiceStoreRefuseStyle3;

	/**
	 * 商家点击取消订单发送短信（服务店，在线支付--微信支付、支付宝支付）
	 */
	@Value("${sms.service.store.cancel.style1}")
	private String smsServiceStoreCancelStyle1;

	/**
	 * 商家点击取消订单发送短信（服务店，在线支付--余额支付、京东支付）
	 */
	@Value("${sms.service.store.cancel.style2}")
	private String smsServiceStoreCancelStyle2;

	/**
	 * 商家点击取消订单发送短信（服务店，线下支付并当面确认价格）
	 */
	@Value("${sms.service.store.cancel.style3}")
	private String smsServiceStoreCancelStyle3;
	// End 重构4.1 add by wusw

	@Resource
	private SeckillRemindeMapper seckillRemindeMapper;
	
	@Resource
	private SysBuyerUserMapper sysBuyerUserMapper;

	public static final int time = 60000 * 15;

	@Override
	public void insertSeckillReminde(SeckillReminde seckillReminde) throws Exception {
		seckillRemindeMapper.insertSeckillReminde(seckillReminde);
	}

	@Override
	public void sendPosMessage(SendMsgParamVo sendMsgParamVo, String content, Date startTime) throws Exception {
		PushMsgVo pushMsgVo = new PushMsgVo();
		pushMsgVo.setSysCode(msgSysCode);
		pushMsgVo.setToken(msgToken);
		pushMsgVo.setSendUserId(sendMsgParamVo.getUserId());
		pushMsgVo.setServiceFkId(sendMsgParamVo.getOrderId());
		pushMsgVo.setServiceTypes(new Integer[] { MsgConstant.ServiceTypes.MALL_OTHER });
		// 0:用户APP,2:商家APP,3POS机
		pushMsgVo.setAppType(Constant.ZERO);
		pushMsgVo.setIsUseTemplate(Constant.ZERO);
		pushMsgVo.setMsgType(Constant.ONE);
		pushMsgVo.setId(sendMsgParamVo.getStoreId());
		pushMsgVo.setMsgTypeCustom(OrderMsgConstant.SECKILL_MESSAGE);
		
		// 不使用模板
		pushMsgVo.setMsgNotifyContent("友门鹿");
		pushMsgVo.setMsgDetailType(Constant.ONE);
		pushMsgVo.setMsgDetailContent(content);
		// 设置是否定时发送
		pushMsgVo.setIsTiming(Constant.ONE);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		pushMsgVo.setSendTime(format.format(new Date(startTime.getTime() - time)));
		
		// 发送用户
		List<PushUserVo> userList = new ArrayList<PushUserVo>();
		// 查询的用户信息
		SysBuyerUser sysBuyerUser = sysBuyerUserMapper.selectByPrimaryKey(sendMsgParamVo.getUserId());
		PushUserVo pushUser = new PushUserVo();
		pushUser.setUserId(sysBuyerUser.getId());
		pushUser.setMobile(sysBuyerUser.getPhone());
		pushUser.setMsgType(Constant.ONE);

		userList.add(pushUser);
		pushMsgVo.setUserList(userList);
		sendMessage(pushMsgVo);

	}
	
    
	private void sendMessage(Object entity) throws Exception {
		MQMessage anMessage = new MQMessage(TOPIC, (Serializable)JsonMapper.nonDefaultMapper().toJson(entity));
		rocketMQProducer.sendMessage(anMessage);
	}

	@Override
	public SeckillReminde selectSeckillRemindeByActivityId(String userId, String activityId) throws Exception {
		SeckillReminde seckillReminde = seckillRemindeMapper.selectSeckillRemindeByActivityId(userId, activityId);
		return seckillReminde;
	}

	@Override
	public void updateSeckillReminde(SeckillReminde reminde) throws Exception {
		seckillRemindeMapper.updateSeckillReminde(reminde);
	}

}
