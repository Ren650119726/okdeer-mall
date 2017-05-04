/** 
 *@Project: okdeer-mall-order 
 *@Author: guocp
 *@Date: 2017年5月2日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.order.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 消息配置类
 * ClassName: TradeMessageProperties 
 * @author guocp
 * @date 2017年5月2日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class TradeMessageProperties {
	
	/**取消订单短信1*/
	@Value("${sms.cancalOrder.style1}")
	public String smsCancalOrderStyle1;

	/**取消订单短信2*/
	@Value("${sms.cancalOrder.style2}")
	public String smsCancalOrderStyle2;

	/**取消订单短信3*/
	@Value("${sms.cancalOrder.style3}")
	public String smsCancalOrderStyle3;

	/**商家同意退款短信1*/
	@Value("${sms.agreeRefundPay.style1}")
	public String smsAgreeRefundPayStyle1;

	/**商家同意退款短信2*/
	@Value("${sms.agreeRefundPay.style2}")
	public String smsAgreeRefundPayStyle2;

	/**商家同意退款短信3*/
	@Value("${sms.agreeRefundPay.style3}")
	public String smsAgreeRefundPayStyle3;

	/**用户拒收短信-第三方支付*/
	@Value("${sms.userRefuse.thirdPay}")
	public String smsUserRefuseThirdPay;

	/**用户拒收短信-云钱包支付*/
	@Value("${sms.userRefuse.walletPay}")
	public String smsUserRefuseWalletPay;

	/**用户拒收短信-货到付款*/
	@Value("${sms.userRefuse.unPay}")
	public String smsUserRefuseUnPay;

	/**商家点击发货短信*/
	@Value("${sms.shipments.style1}")
	public String smsShipmentsStyle1;

	/**到店自提订单下单短信*/
	@Value("${sms.order.store.pickup.style1}")
	public String smsOrderStorePickupStyle1;

	/**团购服务型商品下单短信*/
	@Value("${sms.server.order.style1}")
	public String smsServerOrderStyle1;

	/**商家版APP订单详情链接*/
	@Value("${orderDetailLink}")
	public String orderDetailLink;

	/**商家版APP退款单详情链接*/
	@Value("${orderRefundsDetailLink}")
	public String orderRefundsDetailLink;

	/**
	 * Android客户端自定义通知样式，如果没有，可以默认为0
	 */
	@Value("${notification.builder.id}")
	public String notificationBuilderId;

	/**
	 * 只有notificationBuilderId为0时有效
	 *  响铃：4;振动：2;可清除：1;
	 *  可组合相加
	 *  响铃
	 */
	@Value("${notification.basic.style1}")
	public String notificationBasicStyle1;

	/**
	 * 只有notificationBuilderId为0时有效
	 *  响铃：4;振动：2;可清除：1;
	 *  可组合相加
	 *  震动
	 */
	@Value("${notification.basic.style2}")
	public String notificationBasicStyle2;
	
	// Begin V1.2 added by maojj 2016-12-02
	/**
	 * 商家点击派单发送短信（服务店）
	 */
	@Value("${sms.acceptOrder.style}")
	public String smsAcceptOrderStyle;
	// End V1.2 added by maojj 2016-12-02

	// Begin 重构4.1 add by wusw
	/**
	 * 商家点击派单发送短信（服务店）
	 */
	@Value("${sms.service.store.shipments.style1}")
	public String smsServiceStoreShipmentsStyle1;

	/**
	 * 商家点击拒绝服务发送短信（服务店，在线支付--微信支付、支付宝支付）
	 */
	@Value("${sms.service.store.refuse.style1}")
	public String smsServiceStoreRefuseStyle1;

	/**
	 * 商家点击拒绝服务发送短信（服务店，在线支付--余额支付、京东支付）
	 */
	@Value("${sms.service.store.refuse.style2}")
	public String smsServiceStoreRefuseStyle2;

	/**
	 * 商家点击拒绝服务发送短信（服务店，线下支付并当面确认价格）
	 */
	@Value("${sms.service.store.refuse.style3}")
	public String smsServiceStoreRefuseStyle3;

	/**
	 * 商家点击取消订单发送短信（服务店，在线支付--微信支付、支付宝支付）
	 */
	@Value("${sms.service.store.cancel.style1}")
	public String smsServiceStoreCancelStyle1;

	/**
	 * 商家点击取消订单发送短信（服务店，在线支付--余额支付、京东支付）
	 */
	@Value("${sms.service.store.cancel.style2}")
	public String smsServiceStoreCancelStyle2;

	/**
	 * 商家点击取消订单发送短信（服务店，线下支付并当面确认价格）
	 */
	@Value("${sms.service.store.cancel.style3}")
	public String smsServiceStoreCancelStyle3;

	@Value("${sms.service.store.cancel.style4}")
	public String smsServiceStoreCancelStyle4;

	@Value("${sms.service.store.cancel.style5}")
	public String smsServiceStoreCancelStyle5;
	// End 重构4.1 add by wusw
}
