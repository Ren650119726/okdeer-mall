/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年4月19日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.advert.bo;

import java.io.Serializable;

/**
 * ClassName: ActivityAdvertStoreSkuBo 
 * @Description: 广告活动关联店铺商品信息类
 * @author xuzq01
 * @date 2017年4月19日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *   V2.2.2			 2017年4月19日			xuzq01			服务店和便利店公用
 */

public class ActivityAdvertStoreSkuBo implements Serializable {
	/**
	 * @Fields serialVersionUID : 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 服务店商品id
	 */
	private String productNo;
	/**
	 * skuid
	 */
	private String id;
	
	/**
	 * sku名称
	 */
	private String name;
	/**
	 * 店铺id
	 */
	private String storeId;
	/**
	 * 店铺名称
	 */
	private String storeName;
	/**
	 * 条形码
	 */
	private String barCode;
	/**
	 * 款号
	 */
	private String styleCode;
	/**
	 * 
	 */
	private String propertiesIndb;
	/**
	 * 排序
	 */
	private int sort;
	
	public String getProductNo() {
		return productNo;
	}

	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getStoreId() {
		return storeId;
	}
	
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	
	public String getBarCode() {
		return barCode;
	}
	
	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}
	
	public String getStyleCode() {
		return styleCode;
	}
	
	public void setStyleCode(String styleCode) {
		this.styleCode = styleCode;
	}
	
	public String getPropertiesIndb() {
		return propertiesIndb;
	}
	
	public void setPropertiesIndb(String propertiesIndb) {
		this.propertiesIndb = propertiesIndb;
	}
	
	public int getSort() {
		return sort;
	}
	
	public void setSort(int sort) {
		this.sort = sort;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	
}
