
package com.okdeer.mall.activity.wxchat.bo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WechatUserInfo implements Serializable {

	/**
	 * 微信用户唯一标识
	 */
	private String openid;

	/**
	 * 用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息。
	 */
	private String subscribe;

	/**
	 * 用户昵称
	 */
	@JsonProperty("nickname")
	private String nickName;

	/**
	 * 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
	 */
	private Integer sex;

	/**
	 * 城市
	 */
	private String city;

	/**
	 * 国家
	 */
	private String country;

	private String province;

	private String language;

	/**
	 * 用户头像地址
	 */
	@JsonProperty("headimgurl")
	private String headImgUrl;

	/**
	 * 用户最后关注时间
	 */
	@JsonProperty("subscribe_time")
	private Long subscribeTime;

	/**
	 * 关联id
	 */
	private String unionid;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 分组
	 */
	@JsonProperty("groupid")
	private String groupId;

	/**
	 * 标签ID列表
	 */
	@JsonProperty("tagid_list")
	private List<String> tagids;

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	public Long getSubscribeTime() {
		return subscribeTime;
	}

	public void setSubscribeTime(Long subscribeTime) {
		this.subscribeTime = subscribeTime;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public List<String> getTagids() {
		return tagids;
	}

	public void setTagids(List<String> tagids) {
		this.tagids = tagids;
	}

	public String getSubscribe() {
		return subscribe;
	}

	public void setSubscribe(String subscribe) {
		this.subscribe = subscribe;
	}

}
