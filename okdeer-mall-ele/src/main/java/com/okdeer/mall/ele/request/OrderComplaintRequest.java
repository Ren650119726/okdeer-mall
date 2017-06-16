package com.okdeer.mall.ele.request;

import com.okdeer.mall.ele.util.JsonUtils;
import com.okdeer.mall.ele.util.URLUtils;

import java.io.IOException;


/**
 * 取消订单对应的 字段
 */
public class OrderComplaintRequest extends AbstractRequest {
    private OrderComplaintRequstData data;

    public static class OrderComplaintRequstData {
        private String partner_order_code;
        private Integer order_complaint_code;
        private String order_complaint_desc;
        private Long order_complaint_time;

        public String getPartner_order_code() {
            return partner_order_code;
        }

        public void setPartner_order_code(String partner_order_code) {
            this.partner_order_code = partner_order_code;
        }

        public Integer getOrder_complaint_code() {
            return order_complaint_code;
        }

        public void setOrder_complaint_code(Integer order_complaint_code) {
            this.order_complaint_code = order_complaint_code;
        }

        public String getOrder_complaint_desc() {
            return order_complaint_desc;
        }

        public void setOrder_complaint_desc(String order_complaint_desc) {
            this.order_complaint_desc = order_complaint_desc;
        }

        public Long getOrder_complaint_time() {
            return order_complaint_time;
        }

        public void setOrder_complaint_time(Long order_complaint_time) {
            this.order_complaint_time = order_complaint_time;
        }

        @Override
        public String toString() {
            return "CancelOrderRequstData [partner_order_code=" + partner_order_code + ", order_complaint_code="
                    + order_complaint_code + ", order_complaint_desc=" + order_complaint_desc
                    + ", order_complaint_time=" + order_complaint_time + "]";
        }

    }

    public String getData() throws IOException {
        return URLUtils.getInstance().urlEncode(JsonUtils.getInstance().objectToJson(data));
    }

    public void setData(OrderComplaintRequstData data) {
        this.data = data;
    }
}
