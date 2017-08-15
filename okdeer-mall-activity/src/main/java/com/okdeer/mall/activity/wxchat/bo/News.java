
package com.okdeer.mall.activity.wxchat.bo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class News implements Serializable{

	/**
	 * 图文消息的标题
	 */
	private String title;

	/**
	 * 图文消息的封面图片素材id
	 */
	@JsonProperty("thumb_media_id")
	private String thumbMediaId;

	/**
	 * 是否显示封面
	 */
	@JsonProperty("show_cover_pic")
	private String showCoverPic;

	/**
	 * 作者
	 */
	private String author;

	/**
	 * 图文消息的摘要
	 */
	private String digest;

	/**
	 * 图文消息的具体内容
	 */
	private String content;

	/**
	 * 图文页的URL，或者，当获取的列表是图片素材列表时，该字段是图片的URL
	 */
	private String url;

	/**
	 * 文件名称
	 */
	@JsonProperty("content_source_url")
	private String contentSourceUrl;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getThumbMediaId() {
		return thumbMediaId;
	}

	public void setThumbMediaId(String thumbMediaId) {
		this.thumbMediaId = thumbMediaId;
	}

	public String getShowCoverPic() {
		return showCoverPic;
	}

	public void setShowCoverPic(String showCoverPic) {
		this.showCoverPic = showCoverPic;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContentSourceUrl() {
		return contentSourceUrl;
	}

	public void setContentSourceUrl(String contentSourceUrl) {
		this.contentSourceUrl = contentSourceUrl;
	}

}
