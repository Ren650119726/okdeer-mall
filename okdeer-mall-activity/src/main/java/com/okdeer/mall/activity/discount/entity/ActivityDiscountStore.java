/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年8月23日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.discount.entity;  


/**
 * ClassName: ActivityDiscountStore 
 * @Description: 折扣活动关联店铺
 * @author xuzq01
 * @date 2017年8月23日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public class ActivityDiscountStore {
	/**
	 * ID
	 */
	private String id;
	/**
	 * 折扣（满减）活动ID
	 */
	private String discountId;
	/**
	 * 店铺ID
	 */
	private String storeId;

	public String getId() {
		return id;
	}

	
	public void setId(String id) {
		this.id = id;
	}

	public String getDiscountId() {
		return discountId;
	}

	public void setDiscountId(String discountId) {
		this.discountId = discountId;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	
	

}
