
package com.okdeer.mall.activity.wxchat.bo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddMediaResult extends WechatBaseResult implements Serializable {

	private String type;

	@JsonProperty("media_id")
	private String mediaId;

	@JsonProperty("created_at")
	private Long createdAt;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public Long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}

}
