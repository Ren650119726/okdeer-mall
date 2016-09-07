/** 
 *@Project: yschome-mall-api 
 *@Author: zengj
 *@Date: 2016年9月5日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */

package com.okdeer.mall.order.utils;

/**
 * ClassName: OrderNoUtils 
 * @Description: 订单编号生成工具类
 * @author zengj
 * @date 2016年9月5日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构4.1          2016年9月5日                               zengj
 */

public class OrderNoUtils {

	/** * 流水号位数 */
	public static final Integer SEQ_SIZE = 4;

	/**
	 *  销售单号生成规则调整：
		调整线上销售订单及POS销售订单的订单号规则，为XS+5位店铺编码+2位POS机ID+6位日期+4位流水号，其中：
		1)	5位店铺编码为店铺资料中新增的进销存字段（见本文档一部分）；
		2)	POS机ID取POS上发货时的POS的ID，对于线上订单默认为00；
		3)	流水号为店铺唯一，当日依次递增，每天零点重置为0000；
	 */

	/** * 默认POS机ID */
	private static final String DEFAULT_POS_ID = "00";

	/** * 实物订单前缀 */
	public static final String PHYSICAL_ORDER_PREFIX = "XS";

	/** * 线上POS机ID */
	public static final String ONLINE_POS_ID = DEFAULT_POS_ID;

	/** * 线下POS机ID */
	public static final String OFFLINE_POS_ID = "01";
}
