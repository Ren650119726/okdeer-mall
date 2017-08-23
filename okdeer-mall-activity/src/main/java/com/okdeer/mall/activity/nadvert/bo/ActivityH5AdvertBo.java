/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityH5Advert.java
 * @Date 2017-08-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.nadvert.bo;

import java.io.Serializable;
import java.util.List;

import com.okdeer.mall.activity.nadvert.entity.ActivityH5Advert;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertRole;

/**
 * H5广告活动表
 * 
 * @author mengsj
 * @version 1.0 2017-08-10
 */
public class ActivityH5AdvertBo implements Serializable {

    /**
	 * @Fields serialVersionUID : 序列号
	 */
	private static final long serialVersionUID = 5323296733181794027L;
	
	/**
	 * @Fields advert : h5活动基本信息
	 */
	private ActivityH5Advert advert;
	
	/**
	 * @Fields roles : h5活动规则
	 */
	private List<ActivityH5AdvertRole> roles;
	
	/**
	 * @Fields contents : h5活动内容
	 */
	private List<ActivityH5AdvertContentBo> contents;

	public ActivityH5Advert getAdvert() {
		return advert;
	}

	public void setAdvert(ActivityH5Advert advert) {
		this.advert = advert;
	}

	public List<ActivityH5AdvertRole> getRoles() {
		return roles;
	}

	public void setRoles(List<ActivityH5AdvertRole> roles) {
		this.roles = roles;
	}

	public List<ActivityH5AdvertContentBo> getContents() {
		return contents;
	}

	public void setContents(List<ActivityH5AdvertContentBo> contents) {
		this.contents = contents;
	}
}