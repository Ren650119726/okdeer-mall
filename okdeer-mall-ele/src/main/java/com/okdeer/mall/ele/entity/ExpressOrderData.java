package com.okdeer.mall.ele.entity;

import java.math.BigDecimal;
import java.util.List;

/**
 * ClassName: ExpressOrderData
 *
 * @author wangf01
 * @Description: 配送订单对象-data数据
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public class ExpressOrderData {

    /**
     * 商户备注信息
     *
     * @desc 必填
     */
    private String partner_remark;

    /**
     * 商户订单号,要求唯一
     */
    private String partner_order_code;

    /**
     * 回调地址,订单状态变更时会调用此接口传递状态信息
     *
     * @desc 必填
     */
    private String notify_url;

    /**
     * 订单类型
     * 1: 蜂鸟配送, 未向饿了么物流平台查询过站点的订单，支持两小时送达
     * 2: 定点次日达, 提前向饿了么物流平台查询过配送站点的订单，支持次日送达
     *
     * @desc 必填
     */
    private int order_type = 1;

    /**
     * 门店信息
     *
     * @desc 必填
     */
    private ExpressTransportInfo transport_info;

    /**
     * 下单时间(毫秒)
     */
    private Long order_add_time;

    /**
     * 订单总金额（不包含商家的任何活动以及折扣的金额）
     *
     * @desc 必填
     */
    private BigDecimal order_total_amount;

    /**
     * 客户需要支付的金额
     *
     * @desc 必填
     */
    private BigDecimal order_actual_amount;

    /**
     * 订单总重量（kg），营业类型选定为果蔬生鲜、商店超市、其他三类时必填，大于0kg并且小于等于6kg
     *
     * @desc 必填
     */
    private BigDecimal order_weight = new BigDecimal(1);

    /**
     * 用户备注
     */
    private String order_remark;

    /**
     * 是否需要发票, 0:不需要, 1:需要
     *
     * @desc 必填
     */
    private int is_invoiced = 0;

    /**
     * 发票抬头, 如果需要发票, 此项必填
     */
    private String invoice;

    /**
     * 订单支付状态 0:未支付 1:已支付
     *
     * @desc 必填
     */
    private int order_payment_status = 1;

    /**
     * 订单支付方式 1:在线支付
     *
     * @desc 必填
     */
    private int order_payment_method = 1;

    /**
     * 是否需要ele代收 0:否
     *
     * @desc 必填
     */
    private int is_agent_payment = 0;

    /**
     * 需要代收时客户应付金额, 如果需要ele代收 此项必填
     */
    private BigDecimal require_payment_pay;

    /**
     * 订单货物件数
     *
     * @desc 必填
     */
    private int goods_count = 0;

    /**
     * 需要送达时间（毫秒).
     */
    private long require_receive_time;

    /**
     * 商家订单流水号, 方便配送骑手到店取货, 支持数字,字母及#等常见字符. 如不填写, 蜂鸟将截取商家订单号后4位作为流水号.
     */
    private String serial_number;

    /**
     * 收货人信息
     *
     * @desc 必填
     */
    private ExpressReceiverInfo receiver_info;

    /**
     * 订单商品项信息
     *
     * @desc 必填
     */
    private List<ExpressOrderItem> items_json;

    public String getPartner_remark() {
        return partner_remark;
    }

    public void setPartner_remark(String partner_remark) {
        this.partner_remark = partner_remark;
    }

    public String getPartner_order_code() {
        return partner_order_code;
    }

    public void setPartner_order_code(String partner_order_code) {
        this.partner_order_code = partner_order_code;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public int getOrder_type() {
        return order_type;
    }

    public void setOrder_type(int order_type) {
        this.order_type = order_type;
    }

    public ExpressTransportInfo getTransport_info() {
        return transport_info;
    }

    public void setTransport_info(ExpressTransportInfo transport_info) {
        this.transport_info = transport_info;
    }

    public Long getOrder_add_time() {
        return order_add_time;
    }

    public void setOrder_add_time(Long order_add_time) {
        this.order_add_time = order_add_time;
    }

    public BigDecimal getOrder_total_amount() {
        return order_total_amount;
    }

    public void setOrder_total_amount(BigDecimal order_total_amount) {
        this.order_total_amount = order_total_amount;
    }

    public BigDecimal getOrder_actual_amount() {
        return order_actual_amount;
    }

    public void setOrder_actual_amount(BigDecimal order_actual_amount) {
        this.order_actual_amount = order_actual_amount;
    }

    public BigDecimal getOrder_weight() {
        return order_weight;
    }

    public void setOrder_weight(BigDecimal order_weight) {
        this.order_weight = order_weight;
    }

    public String getOrder_remark() {
        return order_remark;
    }

    public void setOrder_remark(String order_remark) {
        this.order_remark = order_remark;
    }

    public int getIs_invoiced() {
        return is_invoiced;
    }

    public void setIs_invoiced(int is_invoiced) {
        this.is_invoiced = is_invoiced;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public int getOrder_payment_status() {
        return order_payment_status;
    }

    public void setOrder_payment_status(int order_payment_status) {
        this.order_payment_status = order_payment_status;
    }

    public int getOrder_payment_method() {
        return order_payment_method;
    }

    public void setOrder_payment_method(int order_payment_method) {
        this.order_payment_method = order_payment_method;
    }

    public int getIs_agent_payment() {
        return is_agent_payment;
    }

    public void setIs_agent_payment(int is_agent_payment) {
        this.is_agent_payment = is_agent_payment;
    }

    public BigDecimal getRequire_payment_pay() {
        return require_payment_pay;
    }

    public void setRequire_payment_pay(BigDecimal require_payment_pay) {
        this.require_payment_pay = require_payment_pay;
    }

    public int getGoods_count() {
        return goods_count;
    }

    public void setGoods_count(int goods_count) {
        this.goods_count = goods_count;
    }

    public long getRequire_receive_time() {
        return require_receive_time;
    }

    public void setRequire_receive_time(long require_receive_time) {
        this.require_receive_time = require_receive_time;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    public ExpressReceiverInfo getReceiver_info() {
        return receiver_info;
    }

    public void setReceiver_info(ExpressReceiverInfo receiver_info) {
        this.receiver_info = receiver_info;
    }

    public List<ExpressOrderItem> getItems_json() {
        return items_json;
    }

    public void setItems_json(List<ExpressOrderItem> items_json) {
        this.items_json = items_json;
    }
}
