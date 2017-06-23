package com.okdeer.mall.ele.entity;

import java.util.Date;

/**
 * ClassName: ExpressCallbackLog
 *
 * @author wangf01
 * @Description: 订单快递配送信息回调日志
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public class ExpressCallbackLog {

    /**
     * 主键id
     */
    private String id;

    /**
     * 蜂鸟配送开放平台返回的订单号
     */
    private String openOrderCode;

    /**
     * 商户自己的订单号
     */
    private String partnerOrderCode;

    /**
     * 回调json数据
     */
    private String callbackJson;

    /**
     * 日志创建时间
     */
    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpenOrderCode() {
        return openOrderCode;
    }

    public void setOpenOrderCode(String openOrderCode) {
        this.openOrderCode = openOrderCode;
    }

    public String getPartnerOrderCode() {
        return partnerOrderCode;
    }

    public void setPartnerOrderCode(String partnerOrderCode) {
        this.partnerOrderCode = partnerOrderCode;
    }

    public String getCallbackJson() {
        return callbackJson;
    }

    public void setCallbackJson(String callbackJson) {
        this.callbackJson = callbackJson;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
