/** 
 *@Project: okdeer-mall-system 
 *@Author: guocp
 *@Date: 2016年11月18日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.po;  


/**
 * ClassName: RiskLimit 
 * @Description: 风控设置信息
 * @author guocp
 * @date 2016年11月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public final class RiskLimitInfo {
	
	
	/**
	 * 用户限制
	 */
	private RiskLimitInfoDetail userLimitInfoDetail = new RiskLimitInfoDetail();
	
	/**
	 * 设备号限制
	 */
	private RiskLimitInfoDetail deviceLimitInfoDetail = new RiskLimitInfoDetail();
	
	/**
	 * 支付账号限制
	 */
	private RiskLimitInfoDetail payAccountLimitInfoDetail = new RiskLimitInfoDetail();

	
	public RiskLimitInfoDetail getUserLimitInfoDetail() {
		return userLimitInfoDetail;
	}

	
	public void setUserLimitInfoDetail(RiskLimitInfoDetail userLimitInfoDetail) {
		this.userLimitInfoDetail = userLimitInfoDetail;
	}

	
	public RiskLimitInfoDetail getDeviceLimitInfoDetail() {
		return deviceLimitInfoDetail;
	}

	
	public void setDeviceLimitInfoDetail(RiskLimitInfoDetail deviceLimitInfoDetail) {
		this.deviceLimitInfoDetail = deviceLimitInfoDetail;
	}

	
	public RiskLimitInfoDetail getPayAccountLimitInfoDetail() {
		return payAccountLimitInfoDetail;
	}

	
	public void setPayAccountLimitInfoDetail(RiskLimitInfoDetail payAccountLimitInfoDetail) {
		this.payAccountLimitInfoDetail = payAccountLimitInfoDetail;
	}


}
