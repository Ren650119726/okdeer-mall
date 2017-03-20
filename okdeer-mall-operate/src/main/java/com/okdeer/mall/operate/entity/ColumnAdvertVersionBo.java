/** 
 *@Project: okdeer-mall-api 
 *@Author: tangzj02
 *@Date: 2017年3月14日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.entity;

import java.io.Serializable;

/**
 * ClassName: ColumnAdvertVersionDto 
 * @Description: 广告与APP版本关联信息DTO
 * @author tangzj02
 * @date 2017年3月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.2       2017-03-14        tangzj02                        添加
 */

public class ColumnAdvertVersionBo implements Serializable {

	/**
	 * @Fields serialVersionUID 
	 */
	private static final long serialVersionUID = 663318025408791150L;

	/**
	 * APP类型  0:管家版 3:便利店版
	 */
	private Integer type;

	/**
	 * APP版本
	 */
	private String version;

	/**
	 * 省或者 地区ID
	 */
	private String areaId;

	/**
	 * 地区ID APP类型 APP版本
	 */
	private String idKey;

	/**
	 * 统计发布数量
	 */
	private Integer count;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}
	
	public String getIdKey() {
		return idKey;
	}

	
	public void setIdKey(String idKey) {
		this.idKey = idKey;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}
