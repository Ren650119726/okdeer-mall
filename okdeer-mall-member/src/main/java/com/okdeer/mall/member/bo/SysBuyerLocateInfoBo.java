/** 
 *@Project: okdeer-mall-member 
 *@Author: xuzq01
 *@Date: 2017年8月18日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.member.bo;  


/**
 * ClassName: SysBuyerLocateInfoBo 
 * @Description: 消息推送查询列表
 * @author xuzq01
 * @date 2017年8月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public class SysBuyerLocateInfoBo {
	/**
	 * 用户id
	 */
	private String userId;
	/**
	 * 城市id
	 */
	private String cityId;
	/**
	 * 用户手机号
	 */
	private String userPhone;
	
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getCityId() {
		return cityId;
	}
	
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}
	
}
