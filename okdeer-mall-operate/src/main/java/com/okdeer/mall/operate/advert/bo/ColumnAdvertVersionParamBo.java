
package com.okdeer.mall.operate.advert.bo;

import java.util.List;

public class ColumnAdvertVersionParamBo {

	/**
	 * 广告id
	 */
	private String advertId;

	/**
	 * 广告id列表
	 */
	private List<String> advertIdList;

	/**
	 * 类型 APP类型  0:管家版 3:便利店版
	 */
	private Integer type;

	/**
	 * 版本
	 */
	private String version;

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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
