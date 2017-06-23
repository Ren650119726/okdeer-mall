package com.okdeer.mall.ele.entity;

import java.util.Date;

/**
 * ClassName: ExpressCallback
 *
 * @author wangf01
 * @Description: 订单快递配送信息回调
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public class ExpressCallback {

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
     * 状态码
     */
    private Integer orderStatus;

    /**
     * 状态推送时间(毫秒)
     */
    private Date pushTime;

    /**
     * 蜂鸟配送员姓名
     */
    private String carrierDriverName;

    /**
     * 蜂鸟配送员电话
     */
    private String carrierDriverPhone;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 定点次日达服务独有的字段: 微仓地址
     */
    private String address;

    /**
     * 定点次日达服务独有的字段: 微仓纬度
     */
    private String latitude;

    /**
     * 定点次日达服务独有的字段: 微仓经度
     */
    private String longitude;

    /**
     * 订单取消原因. 1:用户取消, 2:商家取消
     */
    private Integer cancelReason;

    /**
     * 错误编码
     */
    private String errorCode;

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

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Date getPushTime() {
        return pushTime;
    }

    public void setPushTime(Date pushTime) {
        this.pushTime = pushTime;
    }

    public String getCarrierDriverName() {
        return carrierDriverName;
    }

    public void setCarrierDriverName(String carrierDriverName) {
        this.carrierDriverName = carrierDriverName;
    }

    public String getCarrierDriverPhone() {
        return carrierDriverPhone;
    }

    public void setCarrierDriverPhone(String carrierDriverPhone) {
        this.carrierDriverPhone = carrierDriverPhone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Integer getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(Integer cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
