/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年8月23日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.discount.entity;  


/**
 * ClassName: ActivityDiscountArea 
 * @Description: 折扣活动关联区域
 * @author xuzq01
 * @date 2017年8月23日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public class ActivityDiscountArea {

	/**
	 * ID
	 */
	private String id;
	/**
	 * 折扣活动ID
	 */
	private String discountId;
	/**
	 * 区域ID
	 */
	private String areaId;
	/**
	 * 区域类型：0市，1省
	 */
	private Integer type;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return the discountId
	 */
	public String getDiscountId() {
		return discountId;
	}
	
	/**
	 * @param discountId the discountId to set
	 */
	public void setDiscountId(String discountId) {
		this.discountId = discountId;
	}
	
	/**
	 * @return the areaId
	 */
	public String getAreaId() {
		return areaId;
	}
	
	/**
	 * @param areaId the areaId to set
	 */
	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}
	
	/**
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	
}
