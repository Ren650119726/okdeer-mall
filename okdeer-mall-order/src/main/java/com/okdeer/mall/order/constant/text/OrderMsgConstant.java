/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @项目名称: yschome-mall 
 * @文件名称: OrderMsgConstant.java 
 * @Date: 2016年4月23日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */ 
package com.okdeer.mall.order.constant.text;

/**
 * 消息表示 
 * @pr yschome-mall
 * @author guocp
 * @date 2016年4月23日 下午4:26:38
 */
public interface OrderMsgConstant {
	
	/****************** POS *******************/
	/**
	 * 下单消息标识
	 */
	String MESSAGE_BUY = "201";
	
	
	
	/**
	 * 申请退款消息标识
	 */
	String MESSAGE_RETURN = "205";
	
	/**
	 * 用户填写物流信息
	 */
	String MESSAGE_LOGISTICS = "206";
	
	
	
	
	/****************** 商家版 *******************/
	/**
	 * 下单消息标识
	 */
	String SELLER_MESSAGE_BUY = "211";
	
	
	/**
	 * 申请退款消息标识
	 */
	String SELLER_MESSAGE_RETURN = "215";
	
	/**
	 * 用户填写物流信息
	 */
	String SELLER_MESSAGE_LOGISTICS = "216";
	
	/**
	 * 投诉单消息标识
	 */
	String SELLER_MESSAGE_COMPLAIN = "220";
	
	/**
	 * 鹿掌柜收款标识
	 */
	String SELLER_MESSAGE_LZGGATHERING = "230";
	
	/********************用户表************************/


}
