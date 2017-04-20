/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年4月20日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.staticFile.bo;

import java.io.Serializable;
import java.util.Date;

import com.okdeer.base.common.enums.Disabled;

/**
 * ClassName: ActivityStaticFileBo 
 * @Description: 界面资源管理表
 * @author xuzq01
 * @date 2017年4月20日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *	 v2.2.0				2017年4月20日 			xuzq01				界面资源管理表
 */

public class ActivityStaticFileBo implements Serializable {

    /**
	 * @Fields serialVersionUID : 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * ID
     */
    private String id;
    /**
     * 页面标题
     */
    private String title;
    /**
     * H5活动id
     */
    private String activityAdvertId;
    /**
     * 广告活动名称
     */
    private String activityAdvertName;
    /**
     * 分享主标题
     */
    private String shareTitleMain;
    /**
     * 分享副标题
     */
    private String shareTitleSub;
    /**
     * 资源文件html名
     */
    private String staticFileName;
    /**
     * 静态资源存储包名
     */
    private String staticPackage;
    /**
     * 生成的访问路径
     */
    private String visitUrl;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

	public String getActivityAdvertName() {
		return activityAdvertName;
	}

	public void setActivityAdvertName(String activityAdvertName) {
		this.activityAdvertName = activityAdvertName;
	}

	public String getShareTitleMain() {
        return shareTitleMain;
    }

    public void setShareTitleMain(String shareTitleMain) {
        this.shareTitleMain = shareTitleMain;
    }

    public String getShareTitleSub() {
        return shareTitleSub;
    }

    public void setShareTitleSub(String shareTitleSub) {
        this.shareTitleSub = shareTitleSub;
    }

    public String getStaticFileName() {
        return staticFileName;
    }

    public void setStaticFileName(String staticFileName) {
        this.staticFileName = staticFileName;
    }

    public String getStaticPackage() {
        return staticPackage;
    }

    public void setStaticPackage(String staticPackage) {
        this.staticPackage = staticPackage;
    }

    public String getVisitUrl() {
        return visitUrl;
    }

    public void setVisitUrl(String visitUrl) {
        this.visitUrl = visitUrl;
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

	public String getActivityAdvertId() {
		return activityAdvertId;
	}

	public void setActivityAdvertId(String activityAdvertId) {
		this.activityAdvertId = activityAdvertId;
	}

}
