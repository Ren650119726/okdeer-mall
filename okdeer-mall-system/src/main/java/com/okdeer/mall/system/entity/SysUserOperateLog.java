/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * SysUserOperateLog.java
 * @Date 2017-11-08 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.system.entity;

import java.util.Date;

/**
 * 系统用户操作日志表（V2.6.4）
 * 
 * @author xuzq01
 * @version 1.0 2017-11-08
 */
public class SysUserOperateLog {

    /**
     * 主键id
     */
    private String id;
    /**
     * 操作类型 0修改密码
     */
    private Integer type;
    /**
     * 修改内容
     */
    private String content;
    /**
     * 修改人
     */
    private String updateUserId;
    /**
     * 修改时间
     */
    private Date updateTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}