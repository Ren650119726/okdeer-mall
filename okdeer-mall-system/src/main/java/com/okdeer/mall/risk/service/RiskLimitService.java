/** 
 *@Project: okdeer-mall-system 
 *@Author: guocp
 *@Date: 2016年11月18日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.risk.service;

import com.okdeer.mall.risk.enums.IsPreferential;
import com.okdeer.mall.risk.po.RiskLimitInfo;

/**
 * ClassName: RiskLimit 
 * @Description: 限制对象
 * @author guocp
 * @date 2016年11月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface RiskLimitService {

	/**
	 * 获取提醒限制设置
	 * @param isPreferential
	 * @return   
	 * @author guocp
	 * @date 2016年11月19日
	 */
	RiskLimitInfo getWarnLimit(IsPreferential isPreferential);

	/**
	 * 获取禁止下单限制设置
	 * @param isPreferential
	 * @return   
	 * @author guocp
	 * @date 2016年11月19日
	 */
	RiskLimitInfo getForbidLimit(IsPreferential isPreferential);

	/**
	 * 重置设置
	 * @author guocp
	 * @date 2016年11月19日
	 */
	void retrySetting();
}
