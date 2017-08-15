
package com.okdeer.mall.activity.wxchat.bo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Material {

	/**
	 * 素材id
	 */
	@JsonProperty("media_id")
	private String mediaId;

	/**
	 * 名称
	 */
	@JsonProperty("name")
	private String name;

	/**
	 * 最后更新时间
	 */
	@JsonProperty("update_time")
	private String updateTime;

	/**
	 * 链接地址
	 */
	@JsonProperty("url")
	private String url;
	/**
	 * 图片信息内容
	 */
	private NewsContent content;

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public NewsContent getContent() {
		return content;
	}

	public void setContent(NewsContent content) {
		this.content = content;
	}

}
