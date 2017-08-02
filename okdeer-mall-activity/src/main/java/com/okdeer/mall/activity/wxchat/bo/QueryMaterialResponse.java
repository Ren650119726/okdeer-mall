
package com.okdeer.mall.activity.wxchat.bo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QueryMaterialResponse extends WechatBaseResult {

	/**
	 * 该类型的素材的总数
	 */
	@JsonProperty("total_count")
	private Integer totalCount;

	/**
	 * 本次调用获取的素材的数量
	 */
	@JsonProperty("item_count")
	private Integer itemCount;

	private List<Material> item;

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getItemCount() {
		return itemCount;
	}

	public void setItemCount(Integer itemCount) {
		this.itemCount = itemCount;
	}

	public List<Material> getItem() {
		return item;
	}

	public void setItem(List<Material> item) {
		this.item = item;
	}

}
