/** 
 *@Project: okdeer-mall-system 
 *@Author: guocp
 *@Date: 2016年11月18日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.po;  


/**
 * ClassName: RiskLimit 
 * @Description: 风控设置明细
 * @author guocp
 * @date 2016年11月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public final class RiskLimitInfoDetail {
	
	
	/**
	 * 最大充值额度
	 */
	private Integer maxRecharge;
	/**
	 * 最大充值笔数
	 */
	private Integer maxRechargeTime;
	/**
	 * 最大充值对象号码
	 */
	private Integer maxRechargeNumber;
	/**
	 * 最大充值id数
	 */
	private Integer maxLoginTime;
	
	
	public Integer getMaxRecharge() {
		return maxRecharge;
	}
	
	public void setMaxRecharge(Integer maxRecharge) {
		this.maxRecharge = maxRecharge;
	}
	
	public Integer getMaxRechargeTime() {
		return maxRechargeTime;
	}
	
	public void setMaxRechargeTime(Integer maxRechargeTime) {
		this.maxRechargeTime = maxRechargeTime;
	}
	
	public Integer getMaxRechargeNumber() {
		return maxRechargeNumber;
	}
	
	public void setMaxRechargeNumber(Integer maxRechargeNumber) {
		this.maxRechargeNumber = maxRechargeNumber;
	}
	
	public Integer getMaxLoginTime() {
		return maxLoginTime;
	}
	
	public void setMaxLoginTime(Integer maxLoginTime) {
		this.maxLoginTime = maxLoginTime;
	}
	
}
