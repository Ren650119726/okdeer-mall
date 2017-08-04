
package com.okdeer.mall.activity.wxchat.bo;

public class PosterAddWechatUserRequest {

	/**
	 * 关注用户id
	 */
	private String openid;

	/**
	 * 分享人用户id
	 */
	private String shareOpenid;

	public PosterAddWechatUserRequest() {

	}

	public PosterAddWechatUserRequest(String openid, String shareOpenid) {
		this.openid = openid;
		this.shareOpenid = shareOpenid;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getShareOpenid() {
		return shareOpenid;
	}

	public void setShareOpenid(String shareOpenid) {
		this.shareOpenid = shareOpenid;
	}

}
