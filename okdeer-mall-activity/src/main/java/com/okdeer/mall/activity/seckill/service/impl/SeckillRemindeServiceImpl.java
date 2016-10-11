/** 
 *@Project: okdeer-mall-activity 
 *@Author: yangq
 *@Date: 2016年9月28日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */

package com.okdeer.mall.activity.seckill.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.archive.system.entity.SysUser;
import com.okdeer.base.kafka.producer.KafkaProducer;
import com.okdeer.mall.activity.seckill.entity.SeckillReminde;
import com.okdeer.mall.activity.seckill.mapper.SeckillRemindeMapper;
import com.okdeer.mall.activity.seckill.service.SeckillRemindeServiceApi;
import com.okdeer.mall.order.constant.OrderMsgConstant;
import com.okdeer.mall.order.vo.PushMsgVo;
import com.okdeer.mall.order.vo.PushUserVo;
import com.okdeer.mall.order.vo.SendMsgParamVo;
import com.okdeer.mall.system.mapper.SysBuyerUserMapper;
import com.okdeer.mall.system.mapper.SysUserMapper;
import com.okdeer.mall.system.utils.mapper.JsonMapper;

/**
 * ClassName: SeckillRemindeServiceImpl 
 * @Description: TODO
 * @author yangq
 * @date 2016年9月28日
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

	/**
	 * 默认通知样式：0
	 */
	private final Integer defaultNotificationBuilderId = 0;

	/**
	 * 默认:响铃+震动+可清除
	 */
	private final Integer defaultNotificationBasicStyle1 = 7;

	/**
	 * 默认:震动+可清除
	 */
	private final Integer defaultNotificationBasicStyle2 = 3;

	@Resource
	private SeckillRemindeMapper seckillRemindeMapper;

	@Resource
	private KafkaProducer kafkaProducer;
	
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
		pushMsgVo.setServiceTypes(new Integer[] { 0 });
		// 0:用户APP,2:商家APP,3POS机
		pushMsgVo.setAppType(0);
		pushMsgVo.setIsUseTemplate(0);
		pushMsgVo.setMsgType(1);
		pushMsgVo.setId(sendMsgParamVo.getStoreId());
		// 业务消息标识
		String msgTypeCustom = OrderMsgConstant.SECKILL_MESSAGE;

		// 推送消息标题
		String msgTitle = "友门鹿";

		pushMsgVo.setMsgTypeCustom(msgTypeCustom);
		// 不使用模板
		pushMsgVo.setMsgNotifyContent(msgTitle);
		pushMsgVo.setMsgDetailType(1);
		pushMsgVo.setMsgDetailContent(content);
		// 设置是否定时发送
		pushMsgVo.setIsTiming(1);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		pushMsgVo.setSendTime(format.format(new Date(startTime.getTime() - time)));
		// 发送用户
		List<PushUserVo> userList = new ArrayList<PushUserVo>();
		// 查询的用户信息
		SysBuyerUser sysBuyerUser = sysBuyerUserMapper.selectByPrimaryKey(sendMsgParamVo.getUserId());
		PushUserVo pushUser = new PushUserVo();
		pushUser.setUserId(sysBuyerUser.getId());
		pushUser.setMobile(sysBuyerUser.getPhone());
/*
		try {
			pushUser.setNotificationBuilderId(Integer.valueOf(notificationBuilderId));
			// 消息信息提示
			if (WhetherEnum.whether.equals(sysBuyerUser.getIsAccept())) {
				// 有声音
				pushUser.setIsexitsSound(0);
				pushUser.setNotificationBasicStyle(Integer.valueOf(notificationBasicStyle1));
			} else {
				// 无声音
				pushUser.setNotificationBasicStyle(Integer.valueOf(notificationBasicStyle2));
				pushUser.setIsexitsSound(1);
			}
		} catch (Exception e) {
			// 没有配置zookeeper，取默认的
			pushUser.setNotificationBuilderId(defaultNotificationBuilderId);
			// 消息信息提示
			if (WhetherEnum.whether.equals(sysBuyerUser.getIsAccept())) {
				// 有声音
				pushUser.setIsexitsSound(0);
				pushUser.setNotificationBasicStyle(defaultNotificationBasicStyle1);
			} else {
				// 无声音
				pushUser.setNotificationBasicStyle(defaultNotificationBasicStyle2);
				pushUser.setIsexitsSound(1);
			}
		}*/
		pushUser.setMsgType(1);

		userList.add(pushUser);
		pushMsgVo.setUserList(userList);
		kafkaProducer.send(JsonMapper.nonDefaultMapper().toJson(pushMsgVo));

	}

	@Override
	public SeckillReminde selectSeckillRemindeByUserId(String UserId) throws Exception {
		SeckillReminde seckillReminde = seckillRemindeMapper.selectSeckillRemindeByUserId(UserId);
		return seckillReminde;
	}

	@Override
	public void updateSeckillReminde(SeckillReminde reminde) throws Exception {
		seckillRemindeMapper.updateSeckillReminde(reminde);
	}

}
