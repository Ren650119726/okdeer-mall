/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2016年11月14日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.service;

import java.util.List;
import java.util.Map;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.risk.entity.RiskSetting;
import com.okdeer.mall.risk.enums.IsPreferential;
import com.okdeer.mall.risk.po.RiskLimitInfo;

/**
 * ClassName: IRiskSettingService 
 * @Description: TODO
 * @author xuzq01
 * @date 2016年11月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface RiskSettingService extends IBaseService{
	
	/**
	 * @Description: 风控设置列表
	 * @param params
	 * @return
	 * @throws Exception
	 * @author zhangkeneng
	 * @date 2016年11月16日
	 */
	List<RiskSetting> list(Map<String,Object> params) throws Exception;
	
	/**
	 * @Description: 插入风控设置
	 * @param settingList
	 * @param isCoupon 1用券 0不用券
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年11月16日
	 */
	void addBatch(List<RiskSetting> settingList,Integer isCoupon) throws Exception;
	
	
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
