package com.okdeer.mall.ele.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.okdeer.mall.ele.util.JsonUtils;
import com.okdeer.mall.ele.util.URLUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Arrays;

/**
 * 请求订单数据封装
 */
public class ElemeCreateOrderRequest extends AbstractRequest {

    private ElemeCreateRequestData data;

    /**
     * urlencode json String
     *
     * @return
     * @throws UnsupportedEncodingException
     * @throws JsonProcessingException
     * @throws IOException
     */
    public String getData() throws IOException {
        return URLUtils.getInstance().urlEncode(JsonUtils.getInstance().objectToJson(data));
    }

    public void setData(ElemeCreateRequestData data) {
        this.data = data;
    }

    public static class ElemeCreateRequestData {
        /**
         * 第三方平台备注
         */
        private String partner_remark;
        /**
         * 第三方订单号 需唯一
         */
        private String partner_order_code;
        /**
         * 回调url
         */
        private String notify_url;
        /**
         * 订单类型
         */
        private int order_type;
        /**
         * 配送点信息
         */
        private TransportInfo transport_info;
        /**
         * 订单总金额（不包含商家的任何活动以及折扣的金额）
         */
        private BigDecimal order_total_amount;
        /**
         * 客户需要支付的金额
         */
        private BigDecimal order_actual_amount;
        /**
         * 用户备注
         */
        private String order_remark;
        /**
         * 是否需要发票
         */
        private Integer is_invoiced;
        /**
         * 发票抬头, 如果需要发票 此项必填
         */
        private String invoice;
        /**
         * 订单支付状态 0：未支付 1：已支付
         */
        private Integer order_payment_status;
        /**
         * 订单支付方式 1：在线支付
         */
        private Integer order_payment_method;
        /**
         * 是否需要ele代收 0：否 1：是
         */
        private Integer is_agent_payment;
        /**
         * 需要代收时客户应付金额, 如需代收款 此项必填
         */
        private BigDecimal require_payment_pay = new BigDecimal(0.0);    //传个默认0.0
        /**
         * 订单货物件数
         */
        private Integer goods_count;
        /**
         * 需要送达时间
         */
        private long require_receive_time;
        /**
         * 下单时间
         */
        private long order_add_time;
        /**
         * 订单重量
         */
        private BigDecimal order_weight;

        /**
         * 收货人信息
         */
        private ReceiverInfo receiver_info;
        /**
         *
         */
        private ItemsJson[] items_json;

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

        public TransportInfo getTransport_info() {
            return transport_info;
        }

        public void setTransport_info(TransportInfo transport_info) {
            this.transport_info = transport_info;
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

        public String getOrder_remark() {
            return order_remark;
        }

        public void setOrder_remark(String order_remark) {
            this.order_remark = order_remark;
        }

        public Integer getIs_invoiced() {
            return is_invoiced;
        }

        public void setIs_invoiced(Integer is_invoiced) {
            this.is_invoiced = is_invoiced;
        }

        public String getInvoice() {
            return invoice;
        }

        public void setInvoice(String invoice) {
            this.invoice = invoice;
        }

        public Integer getOrder_payment_status() {
            return order_payment_status;
        }

        public void setOrder_payment_status(Integer order_payment_status) {
            this.order_payment_status = order_payment_status;
        }

        public Integer getOrder_payment_method() {
            return order_payment_method;
        }

        public void setOrder_payment_method(Integer order_payment_method) {
            this.order_payment_method = order_payment_method;
        }

        public Integer getIs_agent_payment() {
            return is_agent_payment;
        }

        public void setIs_agent_payment(Integer is_agent_payment) {
            this.is_agent_payment = is_agent_payment;
        }

        public BigDecimal getRequire_payment_pay() {
            return require_payment_pay;
        }

        public void setRequire_payment_pay(BigDecimal require_payment_pay) {
            this.require_payment_pay = require_payment_pay;
        }

        public Integer getGoods_count() {
            return goods_count;
        }

        public void setGoods_count(Integer goods_count) {
            this.goods_count = goods_count;
        }

        public long getRequire_receive_time() {
            return require_receive_time;
        }

        public void setRequire_receive_time(long require_receive_time) {
            this.require_receive_time = require_receive_time;
        }

        public ReceiverInfo getReceiver_info() {
            return receiver_info;
        }

        public void setReceiver_info(ReceiverInfo receiver_info) {
            this.receiver_info = receiver_info;
        }

        public ItemsJson[] getItems_json() {
            return items_json;
        }

        public void setItems_json(ItemsJson[] items_json) {
            this.items_json = items_json;
        }

        public long getOrder_add_time() {
            return order_add_time;
        }

        public void setOrder_add_time(long order_add_time) {
            this.order_add_time = order_add_time;
        }

        public BigDecimal getOrder_weight() {
            return order_weight;
        }

        public void setOrder_weight(BigDecimal order_weight) {
            this.order_weight = order_weight;
        }

        public String getPartner_remark() {
            return partner_remark;
        }

        public void setPartner_remark(String partner_remark) {
            this.partner_remark = partner_remark;
        }

        @Override
        public String toString() {
            return "ElemeCreateRequestData{" +
                    "partner_remark='" + partner_remark + '\'' +
                    ", partner_order_code='" + partner_order_code + '\'' +
                    ", notify_url='" + notify_url + '\'' +
                    ", order_type=" + order_type +
                    ", transport_info=" + transport_info +
                    ", order_total_amount=" + order_total_amount +
                    ", order_actual_amount=" + order_actual_amount +
                    ", order_remark='" + order_remark + '\'' +
                    ", is_invoiced=" + is_invoiced +
                    ", invoice='" + invoice + '\'' +
                    ", order_payment_status=" + order_payment_status +
                    ", order_payment_method=" + order_payment_method +
                    ", is_agent_payment=" + is_agent_payment +
                    ", require_payment_pay=" + require_payment_pay +
                    ", goods_count=" + goods_count +
                    ", require_receive_time=" + require_receive_time +
                    ", order_add_time=" + order_add_time +
                    ", order_weight=" + order_weight +
                    ", receiver_info=" + receiver_info +
                    ", items_json=" + Arrays.toString(items_json) +
                    '}';
        }
    }

    /**
     * 配送点信息
     */
    public static class TransportInfo {
        /**
         * 配送点名称
         */
        private String transport_name;
        /**
         * 配送点地址
         */
        private String transport_address;
        /**
         * 配送点联系方式
         */
        private String transport_tel;
        /**
         * 经度
         */
        private BigDecimal transport_longitude;
        /**
         * 纬度
         */
        private BigDecimal transport_latitude;
        /**
         * 备注
         */
        private String transport_remark;

        /**
         * 经纬度来源
         */
        private int position_source;

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

        public String getTransport_tel() {
            return transport_tel;
        }

        public void setTransport_tel(String transport_tel) {
            this.transport_tel = transport_tel;
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

        public String getTransport_remark() {
            return transport_remark;
        }

        public void setTransport_remark(String transport_remark) {
            this.transport_remark = transport_remark;
        }

        public int getPosition_source() {
            return position_source;
        }

        public void setPosition_source(int position_source) {
            this.position_source = position_source;
        }

        @Override
        public String toString() {
            return "TransportInfo{" +
                    ", transport_name='" + transport_name + '\'' +
                    ", transport_address='" + transport_address + '\'' +
                    ", transport_tel='" + transport_tel + '\'' +
                    ", transport_longitude=" + transport_longitude +
                    ", transport_latitude=" + transport_latitude +
                    ", transport_remark='" + transport_remark + '\'' +
                    ", position_source=" + position_source +
                    '}';
        }
    }

    /**
     * 收货人信息
     */
    public static class ReceiverInfo {
        /**
         * 收货人姓名
         */
        private String receiver_name;
        /**
         * 收货人联系电话
         */
        private String receiver_primary_phone;
        /**
         * 收货人备用电话
         */
        private String receiver_second_phone;
        /**
         * 收货人地址
         */
        private String receiver_address;
        /**
         * 收货人城市编码
         */
        private String receiver_city_code;
        /**
         * 收货人城市
         */
        private String receiver_city_name;
        /**
         * 收货人经度
         */
        private BigDecimal receiver_longitude;
        /**
         * 收货人纬度
         */
        private BigDecimal receiver_latitude;

        /**
         * 经纬度来源
         */
        private int position_source;

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

        public String getReceiver_city_code() {
            return receiver_city_code;
        }

        public void setReceiver_city_code(String receiver_city_code) {
            this.receiver_city_code = receiver_city_code;
        }

        public String getReceiver_city_name() {
            return receiver_city_name;
        }

        public void setReceiver_city_name(String receiver_city_name) {
            this.receiver_city_name = receiver_city_name;
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

        @Override
        public String toString() {
            return "ReceiverInfo{" +
                    "receiver_name='" + receiver_name + '\'' +
                    ", receiver_primary_phone='" + receiver_primary_phone + '\'' +
                    ", receiver_second_phone='" + receiver_second_phone + '\'' +
                    ", receiver_address='" + receiver_address + '\'' +
                    ", receiver_city_code='" + receiver_city_code + '\'' +
                    ", receiver_city_name='" + receiver_city_name + '\'' +
                    ", receiver_longitude=" + receiver_longitude +
                    ", receiver_latitude=" + receiver_latitude +
                    ", position_source=" + position_source +
                    '}';
        }
    }

    /**
     * 商品信息
     */
    public static class ItemsJson {
        /**
         * 商品名称
         */
        private String item_name;
        /**
         * 商品数量
         */
        private int item_quantity;
        /**
         * 商品原价
         */
        private BigDecimal item_price;
        /**
         * 商品实际支付金额
         */
        private BigDecimal item_actual_price;
        /**
         * 是否需要ele打包 0：否 1：是
         */
        private Integer is_need_package;
        /**
         * 是否代购 0：否 1：是
         */
        private Integer is_agent_purchase;

        /**
         * 代购进价, 如果需要代购 此项必填
         */
        private BigDecimal agent_purchase_price;

        public String getItem_name() {
            return item_name;
        }

        public void setItem_name(String item_name) {
            this.item_name = item_name;
        }

        public int getItem_quantity() {
            return item_quantity;
        }

        public void setItem_quantity(int item_quantity) {
            this.item_quantity = item_quantity;
        }

        public BigDecimal getItem_price() {
            return item_price;
        }

        public void setItem_price(BigDecimal item_price) {
            this.item_price = item_price;
        }

        public BigDecimal getItem_actual_price() {
            return item_actual_price;
        }

        public void setItem_actual_price(BigDecimal item_actual_price) {
            this.item_actual_price = item_actual_price;
        }

        public Integer getIs_need_package() {
            return is_need_package;
        }

        public void setIs_need_package(Integer is_need_package) {
            this.is_need_package = is_need_package;
        }

        public Integer getIs_agent_purchase() {
            return is_agent_purchase;
        }

        public void setIs_agent_purchase(Integer is_agent_purchase) {
            this.is_agent_purchase = is_agent_purchase;
        }

        public BigDecimal getAgent_purchase_price() {
            return agent_purchase_price;
        }

        public void setAgent_purchase_price(BigDecimal agent_purchase_price) {
            this.agent_purchase_price = agent_purchase_price;
        }

        @Override
        public String toString() {
            return "ItemsJson [item_name=" + item_name + ", item_quantity=" + item_quantity + ", item_price="
                    + item_price + ", item_actual_price=" + item_actual_price + ", is_need_package=" + is_need_package
                    + ", is_agent_purchase=" + is_agent_purchase + ", agent_purchase_price=" + agent_purchase_price
                    + "]";
        }
    }

    @Override
    public String toString() {
        return "ElemeCreateOrderRequest [data=" + data + ", app_id=" + app_id + ", salt=" + salt + ", signature="
                + signature + "]";
    }
}
