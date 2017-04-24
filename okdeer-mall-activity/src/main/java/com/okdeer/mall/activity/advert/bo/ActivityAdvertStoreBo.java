/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityAdvertStore.java
 * @Date 2017-04-12 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.advert.bo;

import java.io.Serializable;

/**
 * H5活动店铺关联表
 * 
 * @author xuzq01
 * @version 1.0 2017-04-12
 */
public class ActivityAdvertStoreBo implements Serializable {

    /**
	 * @Fields serialVersionUID : 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * ID
     */
    private String id;
    /**
     * 店铺ID
     */
    private String storeId;
    
    /**
     * 店铺名称
     */
    private String storeName;
    
	/**
     * H5活动id
     */
    private String activityAdvertId;


	public String getStoreName() {
		return storeName;
	}

	
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getActivityAdvertId() {
        return activityAdvertId;
    }

    public void setActivityAdvertId(String activityAdvertId) {
        this.activityAdvertId = activityAdvertId;
    }
}