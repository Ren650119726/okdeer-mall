package com.okdeer.mall.ele.entity;

import java.math.BigDecimal;

/**
 * ClassName: ExpressReceiverInfo
 *
 * @author wangf01
 * @Description: 配送订单对象-收货人信息
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public class ExpressReceiverInfo {

    /**
     * 收货人姓名
     *
     * @desc 必填
     */
    private String receiver_name;

    /**
     * 收货人联系电话, 只支持手机号, 只支持手机号
     *
     * @desc 必填
     */
    private String receiver_primary_phone;

    /**
     * 收货人备用联系电话
     */
    private String receiver_second_phone;

    /**
     * 收货人地址
     *
     * @desc 必填
     */
    private String receiver_address;

    /**
     * 收货人经度，取值范围0～180
     *
     * @desc 必填
     */
    private BigDecimal receiver_longitude;

    /**
     * 收货人纬度，取值范围0～90
     *
     * @desc 必填
     */
    private BigDecimal receiver_latitude;

    /**
     * 收货人经纬度来源, 1:腾讯地图, 2:百度地图, 3:高德地图
     *
     * @desc 必填
     */
    private int position_source = 2;

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public String getReceiver_primary_phone() {
        return receiver_primary_phone;
    }

    public void setReceiver_primary_phone(String receiver_primary_phone) {
        this.receiver_primary_phone = receiver_primary_phone;
    }

    public String getReceiver_second_phone() {
        return receiver_second_phone;
    }

    public void setReceiver_second_phone(String receiver_second_phone) {
        this.receiver_second_phone = receiver_second_phone;
    }

    public String getReceiver_address() {
        return receiver_address;
    }

    public void setReceiver_address(String receiver_address) {
        this.receiver_address = receiver_address;
    }

    public BigDecimal getReceiver_longitude() {
        return receiver_longitude;
    }

    public void setReceiver_longitude(BigDecimal receiver_longitude) {
        this.receiver_longitude = receiver_longitude;
    }

    public BigDecimal getReceiver_latitude() {
        return receiver_latitude;
    }

    public void setReceiver_latitude(BigDecimal receiver_latitude) {
        this.receiver_latitude = receiver_latitude;
    }

    public int getPosition_source() {
        return position_source;
    }

    public void setPosition_source(int position_source) {
        this.position_source = position_source;
    }
}
