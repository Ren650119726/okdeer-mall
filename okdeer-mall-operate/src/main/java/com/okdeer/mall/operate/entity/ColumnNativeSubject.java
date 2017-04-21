/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ColumnNativeSubject.java
 * @Date 2017-04-13 Created
 * ע�⣺�����ݽ���������¹��˾�ڲ����ģ���ֹ��й�Լ�������������ҵĿ��
 */
package com.okdeer.mall.operate.entity;

import java.util.Date;

import com.okdeer.base.common.enums.Disabled;

/**
 * 原生专题
 * 
 * @author zhangkn
 * @version 1.0 2017-04-13
 */
public class ColumnNativeSubject implements java.io.Serializable{

    /**
     * 主键
     */
    private String id;
    /**
     * 专题名字
     */
    private String name;
    /**
     * 对应模版
     */
    private String template;
    /**
     * 顶部图片
     */
    private String topImg;
    /**
     * 创建人
     */
    private String createUserId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改人
     */
    private String updateUserId;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 是否启动
     */
    private Disabled disabled;


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

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getTopImg() {
        return topImg;
    }

    public void setTopImg(String topImg) {
        this.topImg = topImg;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public Disabled getDisabled() {
        return disabled;
    }

    public void setDisabled(Disabled disabled) {
        this.disabled = disabled;
    }
}