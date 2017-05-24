/** 
 *@Project: yschome-mall-order 
 *@Author: maojj
 *@Date: 2016年7月14日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.order.constant.text;  


/**
 * ClassName: OrderTipMsgConstant 
 * @Description:订单提示信息常量
 * @author maojj
 * @date 2016年7月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *	  重构V4.1				2016-07-14		 maojj				订单提示信息常量
 *	  重构V4.1				2016-08-10		 maojj				修改优惠失效的提示语
 *	 Bug 12572			2016-08-10		 maojj				添加结算校验失败的提示语
 *   V1.1.0             2016-09-28       zhaoqc             添加优惠券金额判断错误
 */

public interface OrderTipMsgConstant {

	String STORE_IS_CLOSED = "店铺已关闭,不能进行下单";
	
	String STORE_IS_PAUSE = "抱歉，该商家已停止营业了哦";
	
	String STORE_IS_SHUT = "店铺已打烊，暂时不接单，给您造成的不便敬请谅解！";
	
	String STORE_DELIVERY_TOMORROW = "店铺休息中，现在下单需要营业后才能配送，给您造成的不便敬请谅解！";
	
	String GOODS_IS_CHANGE = "商品信息发生变化不能进行购买";
	
	String GOODS_IS_OFFLINE = "对不起，部分商品已下架，提交订单失败";
	
	String GOODS_PRICE_CHANGE = "对不起，部分商品价格已调整，提交订单失败";
	
	String SIZE_IS_OVER = "特惠商品超过限款数量";
	
	String BUY_IS_OVER = "您所购买的部分商品超过限购数";
	
	String STOCK_NOT_ENOUGH = "对不起，部分商品库存不足，提交订单失败";
	
	String PRIVILEGE_INVALID = "对不起，该优惠已失效，提交订单失败";
	
	// Begin Bug:14093 added by maojj 2016-10-12
	String KIND_LIMIT_OVER = "商品超出代金券的限用品类";
	// End added by maojj 2016-10-12
	
	String SHOPPING_SUCCESS = "用户App购物车列表操作成功";
	
	String ORDER_SUCESS = "用户App下单成功";
	
	String FAVOUR_FIND_SUCCESS = "用户App获取优惠活动成功";
	
	// Begin added by maojj 2016-08-10
	String STORE_IS_CLOSED_SETTLEMENT = "店铺刚刚关闭，不能结算";
	
	String STORE_IS_PAUSE_SETTLEMENT = "对不起，商家刚刚暂停营业，不能结算";
	
	String STORE_IS_SHUT_SETTLEMENT = "对不起，商家已打烊，暂时不能接单";
	
	String GOODS_IS_OFFLINE_SETTLEMENT = "对不起，部分商品已下架，请您重新结算";
	
	String GOODS_PRICE_CHANGE_SETTLEMENT = "对不起，部分商品价格已被商家调整，请您重新结算";
	
	String STOCK_NOT_ENOUGH_SETTLEMENT = "对不起，部分商品库存不足，请您重新结算";
	// End added by maojj 2016-08-10
	
	//Begin added by zhaoqc 
	String RECHARGE_COUPON_VALUE_TOOMUCH = "优惠券面额大于订单实付金额，下单失败。";
	//End added by zhaoqc
	
	// Begin　V2.1 added by maojj 2017-02-27
	String CVS_NOT_SUPPORT_TO_STORE = "抱歉，店铺不支持到店自提，请重新选择提货方式";
	// End V2.1 added by maojj 2017-02-27
}
