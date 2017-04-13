/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * OperateFields.java
 * @Date 2017-04-13 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */

package com.okdeer.mall.operate.entity;

import java.util.Date;


import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.enums.Enabled;
import com.okdeer.mall.operate.enums.OperateFieldsPointType;
import com.okdeer.mall.operate.enums.OperateFieldsTemplate;
import com.okdeer.mall.operate.enums.OperateFieldsType;

/**
 * 运营栏位表
 * 
 * @author null
 * @version 1.0 2017-04-13
 */
public class OperateFields {

	private String id;

	/**
	 * 0:城市运营栏位1:默认运营栏位2:店铺运营栏位
	 */
	private OperateFieldsType type;

	/**
	 * 业务id( type=0时为cityId,type=1时为0，type=2时为店铺id)
	 */
	private String businessId;

	/**
	 * 栏位名称
	 */
	private String name;

	/**
	 * 模板（0：一统天下 1：二分天下 2：三足鼎立）
	 */
	private OperateFieldsTemplate template;

	/**
	 * 指向类型(0:商品类型1：入口类型)
	 */
	private OperateFieldsPointType pointType;

	/**
	 * 指向内容（0:单品选择1：店铺活动 2：指定店铺菜单 3：指定商品分类 4:h5链接5:原生专题页6：业务入口）
	 */
	private String pointContent;

	/**
	 * 头图图片地址
	 */
	private String headPic;

	/**
	 * 排序值
	 */
	private Integer sort;

	/**
	 * 是否启用（0:启用 1：禁用）
	 */
	private Enabled enabled;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 创建人
	 */
	private String createUserId;

	/**
	 * 更新人
	 */
	private String updateUserId;

	/**
	 * 是否删除（0：否  1：是）
	 */
	private Disabled disabled;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public OperateFieldsType getType() {
		return type;
	}

	public void setType(OperateFieldsType type) {
		this.type = type;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OperateFieldsTemplate getTemplate() {
		return template;
	}

	public void setTemplate(OperateFieldsTemplate template) {
		this.template = template;
	}

	public OperateFieldsPointType getPointType() {
		return pointType;
	}

	public void setPointType(OperateFieldsPointType pointType) {
		this.pointType = pointType;
	}

	public String getPointContent() {
		return pointContent;
	}

	public void setPointContent(String pointContent) {
		this.pointContent = pointContent;
	}

	public String getHeadPic() {
		return headPic;
	}

	public void setHeadPic(String headPic) {
		this.headPic = headPic;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Enabled getEnabled() {
		return enabled;
	}

	public void setEnabled(Enabled enabled) {
		this.enabled = enabled;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
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

}