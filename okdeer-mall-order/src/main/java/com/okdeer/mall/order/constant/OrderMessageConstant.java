/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @项目名称: yschome-mall 
 * @文件名称: OrderMessageConstant.java 
 * @Date: 2016年3月2日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
package com.okdeer.mall.order.constant;

/**
 * 消息常量
 * 
 * @pr yschome-mall
 * @author guocp
 * @date 2016年3月2日 下午3:48:25
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *    重构4.1             2016-7-15            wusw               添加服务店TOPIC
 *    重构4.1             2016-8-1             wushp              
 */
public interface OrderMessageConstant {

	/** UTF-8转码 */
	String EN_DECODE_UTF_8 = "utf-8";

	/**
	 * 快送店订单TOPIC
	 */
	String TOPIC_ORDER_FAST = "topic_order_fast";
	/**
	 * 便利店订单TOPIC
	 */
	String TOPIC_ORDER_CLOUD = "topic_order_cloud";
	/**
	 * 周边订单TOPIC
	 */
	String TOPIC_ORDER_AROUND = "topic_order_around";
	/**
	 * 活动店订单TOPIC
	 */
	String TOPIC_ORDER_ACTIVITY = "topic_order_activity";

	/**
	 * 下单TAG
	 */
	String TAG_ORDER_ADD = "tag_order_add";
	/**
	 * 取消订单TAG
	 */
	String TAG_ORDER_CANCEL = "tag_order_cancel";
	/**
	 * 支付订单TAG
	 */
	String TAG_ORDER_APPLY = "tag_order_apply";
	/**
	 * 订单自提TAG
	 */
	String TAG_ORDER_PICKUP = "tag_order_pickup";
	/**
	 * 订单提货码失效TAG
	 */
	String TAG_ORDER_PICKUP_CODE_DISABLED = "tag_order_pickup_code_disabled";
	/**
	 * 确认收货TAG
	 */
	String TAG_ORDER_CONFIRM = "tag_order_confirm";
	/**
	 * 用户拒收TAG
	 */
	String TAG_ORDER_REFUSE_RECEIVE = "tag_order_refuse_receive";
	/**
	 * 用户评价TAG
	 */
	String TAG_ORDER_EVALUATE = "tag_order_evaluate";

	/**
	 * 发货TAG
	 */
	String TAG_ORDER_SHIPMENT = "tag_order_shipment";
	
	// Begin 重构4.1  add by wusw
	/**
	 * 服务店TOPIC
	 */
	String TOPIC_ORDER_SERVICE = "topic_order_service";
	
	// End 重构4.1 add by wusw
	
	// Begin 重构4.1  add by wushp
	/**
	 * 100:未评价
	 */
	String ONE_HUNDRED = "100";
	// end 重构4.1  add by wushp
}
