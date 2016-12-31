/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * SysBuyerRank.java
 * @Date 2016-12-30 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.member.entity;

import java.math.BigDecimal;

import com.okdeer.base.common.enums.WhetherEnum;

/**
 * 会员等级表
 * 
 * @author null
 * @version 1.0 2016-12-30
 */
public class SysBuyerRank {

    /**
     * 主键id
     */
    private String id;
    /**
     * 等级名称
     */
    private String name;
    /**
     * 等级code(FE(铁鹿),CU(铜鹿),(AG)银鹿，金鹿(AU))
     */
    private String rankCode;
    /**
     * 积分范围-开始
     */
    private Integer fromVal;
    /**
     * 积分范围-结束
     */
    private Integer toVal;
    /**
     * 是否享有消费积分获取福利
     */
    private WhetherEnum pointBenefit;
    /**
     * 积分福利倍数
     */
    private BigDecimal pointBenefitVal;
    /**
     * 是否享有平台服务体验机会
     */
    private WhetherEnum tasteChance;
    /**
     * 图标地址
     */
    private String icoUrl;


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

    public String getRankCode() {
        return rankCode;
    }

    public void setRankCode(String rankCode) {
        this.rankCode = rankCode;
    }

    public Integer getFromVal() {
        return fromVal;
    }

    public void setFromVal(Integer fromVal) {
        this.fromVal = fromVal;
    }

    public Integer getToVal() {
        return toVal;
    }

    public void setToVal(Integer toVal) {
        this.toVal = toVal;
    }

    public WhetherEnum getPointBenefit() {
        return pointBenefit;
    }

    public void setPointBenefit(WhetherEnum pointBenefit) {
        this.pointBenefit = pointBenefit;
    }

    public BigDecimal getPointBenefitVal() {
        return pointBenefitVal;
    }

    public void setPointBenefitVal(BigDecimal pointBenefitVal) {
        this.pointBenefitVal = pointBenefitVal;
    }

    public WhetherEnum getTasteChance() {
        return tasteChance;
    }

    public void setTasteChance(WhetherEnum tasteChance) {
        this.tasteChance = tasteChance;
    }

    public String getIcoUrl() {
        return icoUrl;
    }

    public void setIcoUrl(String icoUrl) {
        this.icoUrl = icoUrl;
    }
}