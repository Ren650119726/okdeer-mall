/** 
 *@Project: okdeer-mall-system 
 *@Author: guocp
 *@Date: 2016年11月17日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.risk.service;


import java.util.Date;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.risk.enums.IsPreferential;
import com.okdeer.mall.risk.po.RiskOrderRecordPo;

/**
 * ClassName: RiskOrderRecordService 
 * @Description: 风控记录接口
 * @author guocp
 * @date 2016年11月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface RiskOrderRecordService extends IBaseService {

	/**
	 * 根据登入名查询订单记录
	 * @param string   
	 * @author guocp
	 * @date 2016年11月17日
	 */
	RiskOrderRecordPo findByLoginName(String loginName, IsPreferential isPreferential);

	/**
	 * 根据设备号查询订单记录
	 * @param string   
	 * @author guocp
	 * @date 2016年11月17日
	 */
	RiskOrderRecordPo findByDeviceId(String deviceId, IsPreferential isPreferential);

	/**
	 * 根据支付帐号查询订单记录
	 * @param string   
	 * @author guocp
	 * @date 2016年11月17日
	 */
	RiskOrderRecordPo findByPayAccount(String payAccount, IsPreferential isPreferential);

	/**
	 * 
	 * @Description: 根据时间直接删除动作记录
	 * @throws Exception   
	 * @author xuzq01
	 * @param date 
	 * @date 2016年11月25日
	 */
	void deleteByTime(Date createTime) throws Exception;
}
