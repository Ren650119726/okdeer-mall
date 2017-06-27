package com.okdeer.mall.ele.entity;

import java.math.BigDecimal;
import java.util.List;

/**
 * ClassName: ExpressOrderInfo
 *
 * @author wangf01
 * @Description: 第三方订单信息
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public class ExpressOrderInfo {

    /**
     * 微仓ID
     */
    private int transport_station_id;

    /**
     * 微仓电话
     */
    private String transport_station_tel;

    /**
     * 配送员ID
     */
    private String carrier_driver_id;

    /**
     * 配送员姓名
     */
    private String carrier_driver_name;

    /**
     * 配送员电话
     */
    private String carrier_driver_phone;

    /**
     * 预计送达时间（毫秒）
     */
    private long estimate_arrive_time;

    /**
     * 配送费
     */
    private BigDecimal order_total_delivery_cost;

    /**
     * 配送费折扣
     */
    private BigDecimal order_total_delivery_discount;

    /**
     * 订单状态
     */
    private int order_status;

    /**
     * 运单异常原因code
     */
    private String abnormal_code;

    /**
     * 运单异常原因描述
     */
    private String abnormal_desc;

    /**
     * 事件日志详情
     */
    private List<ExpressOrderEventLog> event_log_details;

    public int getTransport_station_id() {
        return transport_station_id;
    }

    public void setTransport_station_id(int transport_station_id) {
        this.transport_station_id = transport_station_id;
    }

    public String getTransport_station_tel() {
        return transport_station_tel;
    }

    public void setTransport_station_tel(String transport_station_tel) {
        this.transport_station_tel = transport_station_tel;
    }

    public String getCarrier_driver_id() {
        return carrier_driver_id;
    }

    public void setCarrier_driver_id(String carrier_driver_id) {
        this.carrier_driver_id = carrier_driver_id;
    }

    public String getCarrier_driver_name() {
        return carrier_driver_name;
    }

    public void setCarrier_driver_name(String carrier_driver_name) {
        this.carrier_driver_name = carrier_driver_name;
    }

    public String getCarrier_driver_phone() {
        return carrier_driver_phone;
    }

    public void setCarrier_driver_phone(String carrier_driver_phone) {
        this.carrier_driver_phone = carrier_driver_phone;
    }

    public long getEstimate_arrive_time() {
        return estimate_arrive_time;
    }

    public void setEstimate_arrive_time(long estimate_arrive_time) {
        this.estimate_arrive_time = estimate_arrive_time;
    }

    public BigDecimal getOrder_total_delivery_cost() {
        return order_total_delivery_cost;
    }

    public void setOrder_total_delivery_cost(BigDecimal order_total_delivery_cost) {
        this.order_total_delivery_cost = order_total_delivery_cost;
    }

    public BigDecimal getOrder_total_delivery_discount() {
        return order_total_delivery_discount;
    }

    public void setOrder_total_delivery_discount(BigDecimal order_total_delivery_discount) {
        this.order_total_delivery_discount = order_total_delivery_discount;
    }

    public int getOrder_status() {
        return order_status;
    }

    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }

    public String getAbnormal_code() {
        return abnormal_code;
    }

    public void setAbnormal_code(String abnormal_code) {
        this.abnormal_code = abnormal_code;
    }

    public String getAbnormal_desc() {
        return abnormal_desc;
    }

    public void setAbnormal_desc(String abnormal_desc) {
        this.abnormal_desc = abnormal_desc;
    }

    public List<ExpressOrderEventLog> getEvent_log_details() {
        return event_log_details;
    }

    public void setEvent_log_details(List<ExpressOrderEventLog> event_log_details) {
        this.event_log_details = event_log_details;
    }
}
