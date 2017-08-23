/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityH5Advert.java
 * @Date 2017-08-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.nadvert.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * H5广告活动表
 * 
 * @author mengsj
 * @version 1.0 2017-08-10
 */
public class ActivityH5Advert implements Serializable {

    /**
	 * @Fields serialVersionUID : 序列号
	 */
	private static final long serialVersionUID = 5827467204354204030L;
	/**
     * ID
     */
    private String id;
    /**
     * 广告活动名称
     */
    private String name;
    /**
     * 0未开始，1、进行中，2、已结束 默认未开始
     */
    private Integer status;
    /**
     * 起始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 页面标题
     */
    private String pageTitle;
    /**
     * 分享主标题
     */
    private String shareMainTitle;
    /**
     * 分享副标题
     */
    private String shareSubTitle;
    /**
     * 分享链接图片
     */
    private String shareLinkPicture;
    /**
     * 页面背景颜色
     */
    private String backGroundColor;
    /**
     * 删除标识 0未删除，1已删除  默认未删除
     */
    private Integer disabled;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建人
     */
    private String createUserId;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 修改人
     */
    private String updateUserId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getShareMainTitle() {
        return shareMainTitle;
    }

    public void setShareMainTitle(String shareMainTitle) {
        this.shareMainTitle = shareMainTitle;
    }

    public String getShareSubTitle() {
        return shareSubTitle;
    }

    public void setShareSubTitle(String shareSubTitle) {
        this.shareSubTitle = shareSubTitle;
    }

    public String getShareLinkPicture() {
        return shareLinkPicture;
    }

    public void setShareLinkPicture(String shareLinkPicture) {
        this.shareLinkPicture = shareLinkPicture;
    }

    public String getBackGroundColor() {
        return backGroundColor;
    }

    public void setBackGroundColor(String backGroundColor) {
        this.backGroundColor = backGroundColor;
    }

    public Integer getDisabled() {
        return disabled;
    }

    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
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
}