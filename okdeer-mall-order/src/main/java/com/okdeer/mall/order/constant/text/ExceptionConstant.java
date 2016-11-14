/** 
 *@Project: yschome-mall-order 
 *@Author: zengj
 *@Date: 2016年7月14日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */

package com.okdeer.mall.order.constant.text;

/**
 * ClassName: ExceptionConstant 
 * @Description: 异常消息常量类
 * @author zengj
 * @date 2016年7月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构4.1          2016年7月14日                               zengj            异常提示常量
 */

public interface ExceptionConstant {

	public static final String ZERO = "0";

	public static final String ONE = "1";

	public static final String TWO = "2";

	public static final String THIRD = "3";

	/*
	 * 服务栏目相关提示 begin
	 */
	public static final String SERVER_COLUMN_IS_NULL = "服务栏目不能为空";

	public static final String SERVER_COLUMN_NOT_EXISTS = "服务栏目不存在";

	public static final String SERVER_COLUMN_IS_CLOSED = "抱歉，该服务已关闭";

	/*
	 * 服务栏目相关提示 end
	 */

	// ======================================================================================================

	/*
	 * 服务店铺相关提示 begin
	 */
	public static final String SERVER_STORE_IS_NULL = "店铺不能为空";

	public static final String SERVER_STORE_NOT_EXISTS = "店铺不存在";

	public static final String SERVER_STORE_IS_CLOSED = "抱歉，店铺关闭，暂不接受预约";

	/*
	 * 服务店铺相关提示 end
	 */

	// ======================================================================================================

	/*
	 * 商品相关提示 begin
	 */
	public static final String GOODS_IS_NULL = "商品不能为空";

	// Begin 12085 add by zengj
	public static final String GOODS_NOT_EXSITS = "抱歉，服务刚刚下架，不能预约";
	// End 12085 add by zengj

	public static final String GOODS_IS_UPDATE = "抱歉，服务信息发生变化，请重新预约";

	public static final String GOODS_BUY_NUM_IS_NULL = "购买商品数量不能为空";

	public static final String GOODS_UPDATE_TIME_IS_NULL = "商品更新时间不能为空";

	public static final String GOODS_UPDATE_TIME_FORMAT_ERROR = "商品更新时间格式错误";

	public static final String GOODS_STOCK_INSUFICIENTE_BY_ZERO = "抱歉，该服务预约已满";

	public static final String GOODS_STOCK_INSUFICIENTE_BY_ACTIVITY = "你慢了一步，活动已被抢光，继续将按原价购买";

	public static final String GOODS_STOCK_INSUFICIENTE = "抱歉，服务预约已满仅能预约%s件";

	public static final String GOODS_LIMIT_NUM = "该商品限购%s件";

	// Begin add by wushp V1.1.0
	/**
	 * 部分商品已下架
	 */
	public static final String GOODS_NOT_EXSITS_PART = "部分商品已下架";
	/**
	 * 部分商品信息发生变化
	 */
	public static final String GOODS_IS_UPDATE_PART = "部分商品信息发生变化";
	/**
	 * 部分商品库存不足
	 */
	public static final String GOODS_STOCK_NOT_ENOUGH = "部分商品库存不足";
	// End add by wushp V1.1.0
	/*
	 * 商品相关提示 end
	 */

	// ======================================================================================================

	/*
	 * 服务地址相关提示 begin
	 */
	public static final String ADDRESS_IS_NULL = "服务地址不能为空";

	public static final String ADDRESS_NOT_EXSITS = "服务地址不存在";
	/*
	 * 服务地址相关提示 end
	 */

	// ======================================================================================================

	/*
	 * 服务时间相关提示 begin
	 */
	public static final String SERVER_TIME_IS_NULL = "服务时间不能为空";

	public static final String SERVER_TIME_FORMAT_ERROR = "服务时间格式错误";
	/*
	 * 服务时间相关提示 end
	 */

	// ======================================================================================================

	/*
	 * 支付方式相关提示 begin
	 */
	public static final String PAY_TYPE_IS_NULL = "支付方式不能为空";
	/*
	 * 支付方式相关提示 end
	 */

	// ======================================================================================================

	/*
	 * 活动相关提示 begin
	 */
	public static final String ACTIVITY_NOT_EXISTS = "活动不存在";

	public static final String ACTIVITY_ITEM_NOT_EXISTS = "活动项不存在";

	public static final String ACTIVITY_IS_CLOSED = "该活动已关闭，继续购买将按原价购买";

	public static final String ACTIVITY_IS_END = "你来晚啦，活动已结束，继续将按原价购买";

	public static final String ACTIVITY_NOT_REPEAT_PARTICIPATION = "限购1件，如果继续购买则以原价购买";

	public static final String ACTIVITY_LIMIT_NUM = "该活动限购1件";

	public static final String ACTIVITY_GOODS_NOT_SUPPORT = "抱歉，该商品不支持秒杀活动";

	public static final String ACTIVITY_DISCOUNT_IS_CLOSED = "该活动已关闭";

	public static final String ACTIVITY_DISCOUNT_IS_END = "该活动已结束";

	/*
	 * 活动相关提示 end
	 */

	// ======================================================================================================

	/*
	 * 订单相关提示 begin
	 */
	public static final String ORDER_PAY_FAIL = "订单支付失败,订单号{}";

	public static final String ORDER_ADD_FAIL = "订单编号生成失败";

	/*
	 * 订单相关提示 end
	 */
	
	public static final String COUPONS_REGISTE_RETURN_FAIL = "订单处理邀请注册首单送券异常，tradeNum={}，异常信息{}";
}
