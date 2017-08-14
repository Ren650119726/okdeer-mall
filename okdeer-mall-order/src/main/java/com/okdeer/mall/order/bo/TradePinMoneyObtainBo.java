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
 * ClassName: TradePinMoneyObtainBo 
 * @Description: 零花钱领取封装bo
 * @author xuzq01
 * @date 2017年8月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public class TradePinMoneyObtainBo implements Serializable {

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
     * 状态：0未使用，1已使用
     */
    private Integer status;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 剩余金额
     */
    private BigDecimal remainAmount;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 有效时间
     */
    private Date validTime;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getRemainAmount() {
        return remainAmount;
    }

    public void setRemainAmount(BigDecimal remainAmount) {
        this.remainAmount = remainAmount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getValidTime() {
        return validTime;
    }

    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getActivityName() {
		return activityName;
	}

	public String getActivityType() {
		return activityType;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}
    
}
