
package com.okdeer.mall.operate.advert.bo;

import java.io.Serializable;
import java.util.List;

public class ColumnAdvertAreaParamBo implements Serializable {

	/**
	 * 广告id
	 */
	private String advertId;

	/**
	 * 广告id列表
	 */
	private List<String> advertIdList;

	/**
	 * 类型
	 */
	private Integer type;

	/**
	 * 区域id
	 */
	private String areaId;

	public String getAdvertId() {
		return advertId;
	}

	public void setAdvertId(String advertId) {
		this.advertId = advertId;
	}

	public List<String> getAdvertIdList() {
		return advertIdList;
	}

	public void setAdvertIdList(List<String> advertIdList) {
		this.advertIdList = advertIdList;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

}
