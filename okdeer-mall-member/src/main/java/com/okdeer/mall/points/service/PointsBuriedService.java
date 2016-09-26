/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: PointsBuriedService.java 
 * @Date: 2016年1月30日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */ 

package com.okdeer.mall.points.service;

import java.math.BigDecimal;

import com.okdeer.mall.member.points.enums.PointsRuleCode;
import com.okdeer.base.common.exception.ServiceException;

/**
 * 积分埋点接口
 * @project yschome-mall
 * @author zhongy
 * @date 2016年1月30日 下午2:53:36
 */
public interface PointsBuriedService {

	/**
	 * 
	 * @param userId 买家用户id
	 * @param code app消费规则编码
	 * @param totalAmount 订单总金额
	 */
	String addConsumerPoints(String userId,BigDecimal totalAmount) throws Exception;
	
	
	/**
	 * 注册/登录/授权/邀请/查看通知获得积分
	 * @param userId 买家用户id
	 * @param ruleCode 积分规则类型
	 */
	//begin add by luosm 2016-08-05
	public Integer doProcessPoints(String userId,PointsRuleCode ruleCode) throws ServiceException;
	//end add by luosm 2016-08-05
	
	/**
	 * app消费获得积分（现用）
	 * @author luosm
	 * @param userId 买家用户id
	 * @param totalMoney 消费总金额
	 */
	public boolean doConsumePoints(String userId,BigDecimal totalMoney)throws ServiceException;
	
	
}
