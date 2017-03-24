/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @项目名称: yschome-mall 
 * @文件名称: OrderMessageConstant.java 
 * @Date: 2016年3月2日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 *  =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     v1.1.0            2016-9-24          wusw			  云钱包解冻金额修改
 */

package com.okdeer.mall.order.constant.mq;

/**
 * 消息常量
 * 
 * @pr yschome-mall
 * @author guocp
 * @date 2016年3月2日 下午3:48:25
 */
public interface PayMessageConstant {
	
	/**
	 * TAG连接符
	 */
	String JOINT = " || ";

	/**
	 * 支付topic
	 */
	String TOPIC_PAY = "topic_pay";

	/**
	 * 支付宝退款topic
	 */
	String TOPIC_ALIPAY_REFUND = "topic_alipay_refund";
	
	
	String TAG_ORDER = "tag_order";
	
	/**
	 * 充值订单余额支付结果
	 */
	String TAG_PAY_RECHARGE_ORDER_BLANCE = "tag_pay_recharge_order_blance";
	/*********************************start 发送消息 余额支付或交易记录********************************/
	/**
	 * 余额支付或交易记录 topic
	 */
	
	String TOPIC_BALANCE_PAY_TRADE = "topic_balance_pay_trade";

	/**
	 * 余额支付或交易记录 tag
	 */
	String TAG_PAY_TRADE_MALL = "tag_pay_trade_mall";
	
	/**
	 * 广告缴费余额支付tag
	 */
	String TAG_ADVERT_BALANCE_PAY = "tag_ad_balance";

	/**********************************end 发送消息 余额支付或交易记录********************************/	
	
	
	
	
	/*********************************start 接收消息 余额支付或交易记录结果********************************/
	
	/**
	 * 余额支付结果消息topic
	 */
	String TOPIC_PAY_RESULT = "topic_pay_trade_result";
	
	
	/**
	 * (支付宝、微信、京东支付)余额支付结果消息tag
	 */
	String TAG_PAY_RESULT_THIRD = "tag_pay_result_mall_third";
	/**
	 * (余额支付)余额支付结果消息tag
	 */
	String TAG_PAY_RESULT_INSERT = "tag_pay_result_mall_insert";
	/**
	 * (取消订单)余额支付结果消息tag
	 */
	String TAG_PAY_RESULT_CANCEL = "tag_pay_result_mall_cancel";
	
	/**
	 * (确认收货)余额支付结果消息tag
	 */
	String TAG_PAY_RESULT_CONFIRM = "tag_pay_result_mall_confirm";
	
	/**
	 * (订单退款)余额支付结果消息tag
	 */
	String TAG_PAY_RESULT_REFUND = "tag_pay_result_mall_refund";
	
	//start add by zengjz 2017-3-24 增加扫码购支付结果监听tag
	/**
	 * 扫码购监听tag
	 */
	String TAG_POST_ORDER =	"tag_pos_order";
	//end add by zengjz 2017-3-24 增加扫码购支付结果监听tag
	/*********************************end 接收消息 余额支付或交易记录结果********************************/
	
	
	
	
	/*********************************财务退款结果通知********************************/
	/**
	 * 财务退款通知topic
	 */
	String TOPIC_REFUND_RESULT = "topic_refund_result";
	
	/**
	 * 财务退款通知tag
	 */
	String TAG_REFUND_RESULT = "tag_refund_result_mall";
	
	/**
	 * 财务退款通知tag（取消的订单）
	 */
	String TAG_REFUSE_REFUND_RESULT = "tag_refuse_refund_result";
	
	
	
	
	/*********************************start  云钱包冻结金额修改通知********************************/
	/**
	 * 云钱包冻结金额修改topic
	 */
	String TOPIC_BALANCE_CHANGE = "topic_balance_change";
	
	/**
	 * 云钱包冻结金额修改tag
	 */
	String TAG_BALANCE_CHANGE = "tag_balance_change";
	/*********************************end  云钱包解冻金额通知********************************/

	/**
	 * 到店消费验证tipic
	 */
	String TOPIC_CONSUME_CODE_VALI = "topic_consume_code_vali";
	/**
	 * 到店消费验证tag
	 */
	String TAG_CONSUME_CODE_VALI = "tag_consume_code_vali";
	
	/**
	 * 订单状态改变
	 */
	String TOPIC_ORDER_STATUS_CHANGE = "top_order_status_change";
	/**
	 * 订单取消tag
	 */
	String TAG_ORDER_CANCELED = "tag_order_canceled";

}
