package com.okdeer.mall.ele.entity;

import java.math.BigDecimal;

/**
 * ClassName: ExpressTransportInfo
 *
 * @author wangf01
 * @Description: 配送订单对象-门店信息
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public class ExpressTransportInfo {

    /**
     * 门店名称
     *
     * @desc 必填
     */
    private String transport_name;

    /**
     * 取货点地址
     *
     * @desc 必填
     */
    private String transport_address;

    /**
     * 取货点经度，取值范围0～180
     *
     * @desc 必填
     */
    private BigDecimal transport_longitude;

    /**
     * 取货点纬度，取值范围0～90
     *
     * @desc 必填
     */
    private BigDecimal transport_latitude;

    /**
     * 取货点经纬度来源, 1:腾讯地图, 2:百度地图, 3:高德地图
     *
     * @desc 必填
     */
    private int position_source = 2;

    /**
     * 取货点联系方式, 只支持手机号,400开头电话以及座机号码
     *
     * @desc 必填
     */
    private String transport_tel;

    /**
     * 取货点备注
     */
    private String transport_remark;

    public String getTransport_name() {
        return transport_name;
    }

    public void setTransport_name(String transport_name) {
        this.transport_name = transport_name;
    }

    public String getTransport_address() {
        return transport_address;
    }

    public void setTransport_address(String transport_address) {
        this.transport_address = transport_address;
    }

    public BigDecimal getTransport_longitude() {
        return transport_longitude;
    }

    public void setTransport_longitude(BigDecimal transport_longitude) {
        this.transport_longitude = transport_longitude;
    }

    public BigDecimal getTransport_latitude() {
        return transport_latitude;
    }

    public void setTransport_latitude(BigDecimal transport_latitude) {
        this.transport_latitude = transport_latitude;
    }

    public int getPosition_source() {
        return position_source;
    }

    public void setPosition_source(int position_source) {
        this.position_source = position_source;
    }

    public String getTransport_tel() {
        return transport_tel;
    }

    public void setTransport_tel(String transport_tel) {
        this.transport_tel = transport_tel;
    }

    public String getTransport_remark() {
        return transport_remark;
    }

    public void setTransport_remark(String transport_remark) {
        this.transport_remark = transport_remark;
    }
}
