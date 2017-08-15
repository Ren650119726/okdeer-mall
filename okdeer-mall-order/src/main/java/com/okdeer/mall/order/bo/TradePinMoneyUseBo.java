/** 
 *@Project: okdeer-mall-web 
 *@Author: xuzq01
 *@Date: 2017年8月11日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.order.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * ClassName: TradePinMoneyUseBo 
 * @Description: 零花钱使用记录bo
 * @author xuzq01
 * @date 2017年8月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public class TradePinMoneyUseBo implements Serializable {

    /**
	 * @Fields serialVersionUID : 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * 主键ID
     */
    private String id;
	/**
     * 用户手机号
     */
    private String userPhone;
    /**
     * 使用金额
     */
    private BigDecimal useAmount;
    /**
     * 创建时间
     */
    private Date createTime;
	/**
	 * 活动名称 跟产品确认不动态关联活动 直接写死
	 */
	private String activityName = "零花钱活动";
	/**
	 * 活动类型  跟产品确认不动态关联活动 直接写死
	 */
	private String activityType = "零花钱";
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserPhone() {
		return userPhone;
	}
	
	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}
	
	public BigDecimal getUseAmount() {
		return useAmount;
	}
	
	public void setUseAmount(BigDecimal useAmount) {
		this.useAmount = useAmount;
	}
	
	public Date getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public String getActivityName() {
		return activityName;
	}
	
	public String getActivityType() {
		return activityType;
	}
	
}
