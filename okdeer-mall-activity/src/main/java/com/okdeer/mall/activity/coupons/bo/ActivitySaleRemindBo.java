/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivitySaleRemind.java
 * @Date 2017-02-15 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.coupons.bo;

import java.io.Serializable;
import java.util.Date;

/**
 * 活动商品安全库存提醒人
 * 
 * @author tangy
 * @version 1.0 2017-02-15
 */
public class ActivitySaleRemindBo implements Serializable {

    /**
	 * @Fields serialVersionUID : 
	 */
	private static final long serialVersionUID = -1564706057852093970L;
	
	/**
     * 主键id
     */
    private String id;
    /**
     * 特惠活动id
     */
    private String saleId;
    /**
     * 用户id
     */
    private String sysUserId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建人
     */
    private String createUserId;

    /**
     * 手机号码
     */
    private String phone;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSaleId() {
        return saleId;
    }

    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }

    public String getSysUserId() {
        return sysUserId;
    }

    public void setSysUserId(String sysUserId) {
        this.sysUserId = sysUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
}