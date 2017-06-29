package com.okdeer.mall.ele.entity;

import com.okdeer.mall.common.utils.DateUtils;

/**
 * ClassName: ExpressCancelData
 *
 * @author wangf01
 * @Description: 配送订单取消-data数据
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public class ExpressCancelData {

    /**
     * 商户订单号
     */
    private String partner_order_code;

    /**
     * 订单取消原因代码(2:商家取消)
     */
    private int order_cancel_reason_code = 2;

    /**
     * 订单取消编码（0:其他, 1:联系不上商户, 2:商品已经售完, 3:用户申请取消, 4:运力告知不配送 让取消订单, 5:订单长时间未分配, 6:接单后骑手未取件）
     */
    private int order_cancel_code = 0;

    /**
     * 订单取消描述（order_cancel_code为0时必填）
     */
    private String order_cancel_description = "其他";

    /**
     * 订单取消时间（毫秒）
     */
    private long order_cancel_time = DateUtils.getSysDate().getTime();

    public String getPartner_order_code() {
        return partner_order_code;
    }

    public void setPartner_order_code(String partner_order_code) {
        this.partner_order_code = partner_order_code;
    }

    public int getOrder_cancel_reason_code() {
        return order_cancel_reason_code;
    }

    public void setOrder_cancel_reason_code(int order_cancel_reason_code) {
        this.order_cancel_reason_code = order_cancel_reason_code;
    }

    public int getOrder_cancel_code() {
        return order_cancel_code;
    }

    public void setOrder_cancel_code(int order_cancel_code) {
        this.order_cancel_code = order_cancel_code;
    }

    public String getOrder_cancel_description() {
        return order_cancel_description;
    }

    public void setOrder_cancel_description(String order_cancel_description) {
        this.order_cancel_description = order_cancel_description;
    }

    public long getOrder_cancel_time() {
        return order_cancel_time;
    }

    public void setOrder_cancel_time(long order_cancel_time) {
        this.order_cancel_time = order_cancel_time;
    }
}
