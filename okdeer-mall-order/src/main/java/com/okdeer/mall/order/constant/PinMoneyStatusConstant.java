/** 
 *@Project: okdeer-mall-order 
 *@Author: guocp
 *@Date: 2017年8月10日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.order.constant;  


/**
 * ClassName: PinMoneyStatus 
 * @Description: 零花钱状态
 * @author guocp
 * @date 2017年8月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface PinMoneyStatusConstant {
	
	/**
	 * 未使用
	 */
	int UNUSED = 0;
	
	/**
	 * 已使用
	 */
	int USED = 1;
	
	/**
	 * 已过期
	 */
	int EXPIRED = 2;
}
