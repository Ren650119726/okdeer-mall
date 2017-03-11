/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * SysOrganization.java
 * @Date 2017-03-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */

package com.okdeer.mall.system.entity;

import java.util.Date;

import com.okdeer.base.common.enums.Disabled;

/**
 * 系统组织表
 * 
 * @author null
 * @version 1.0 2017-03-10
 */
public class SysOrganization {

	/**
	 * 主键ID
	 */
	private String id;

	/**
	 * 组织编码
	 */
	private Integer code;

	/**
	 * 组织名称
	 */
	private String name;

	/**
	 * 父id
	 */
	private String parentId;

	/**
	 * 联系人
	 */
	private String linkman;

	/**
	 * 联系电话
	 */
	private String tel;

	/**
	 * 联系地址
	 */
	private String address;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 创建人ID
	 */
	private String createUserId;

	/**
	 * 创建人名称
	 */
	private String createUserName;

	/**
	 * 修改时间
	 */
	private Date updateTime;

	/**
	 * 修改人ID
	 */
	private String updateUserId;

	/**
	 * 修改人名称
	 */
	private String updateUserName;

	/**
	 * 删除标识 0未删除，1已删除
	 */
	private Disabled disabled;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getLinkman() {
		return linkman;
	}

	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}

	public Disabled getDisabled() {
		return disabled;
	}

	public void setDisabled(Disabled disabled) {
		this.disabled = disabled;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getUpdateUserName() {
		return updateUserName;
	}

	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}

}