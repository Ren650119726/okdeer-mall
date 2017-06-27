package com.okdeer.mall.ele.entity;

/**
 * ClassName: ExpressOrderEventLog
 *
 * @author wangf01
 * @Description: 第三方订单事件日志信息
 * @date 2017年6月23日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public class ExpressOrderEventLog {

    /**
     * 订单状态（配送阶段）
     */
    private int order_status;

    /**
     * 事件发生时间
     */
    private long occur_time;

    /**
     * 配送员姓名（配送阶段）
     */
    private String carrier_driver_name;

    /**
     * 配送员电话（配送阶段）
     */
    private String carrier_driver_phone;

    public int getOrder_status() {
        return order_status;
    }

    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }

    public long getOccur_time() {
        return occur_time;
    }

    public void setOccur_time(long occur_time) {
        this.occur_time = occur_time;
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
}
