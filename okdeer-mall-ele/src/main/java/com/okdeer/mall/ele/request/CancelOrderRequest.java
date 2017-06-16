package com.okdeer.mall.ele.request;


import com.okdeer.mall.ele.util.JsonUtils;
import com.okdeer.mall.ele.util.URLUtils;

import java.io.IOException;

/**
 * 取消订单对应的 字段
 */
public class CancelOrderRequest extends AbstractRequest {
    private CancelOrderRequstData data ;

    public static class CancelOrderRequstData {
        private String partner_order_code;
        private int order_cancel_reason_code;
        private Integer order_cancel_code;
        private String order_cancel_description;
        private long order_cancel_time;
        private String order_cancel_notify_url;

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
        
		public Integer getOrder_cancel_code() {
			return order_cancel_code;
		}

		public void setOrder_cancel_code(Integer order_cancel_code) {
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

        public String getOrder_cancel_notify_url() {
            return order_cancel_notify_url;
        }

        public void setOrder_cancel_notify_url(String order_cancel_notify_url) {
            this.order_cancel_notify_url = order_cancel_notify_url;
        }

		@Override
		public String toString() {
			return "CancelOrderRequstData [partner_order_code=" + partner_order_code + ", order_cancel_reason_code="
					+ order_cancel_reason_code + ", order_cancel_code=" + order_cancel_code
					+ ", order_cancel_description=" + order_cancel_description + ", order_cancel_time="
					+ order_cancel_time + ", order_cancel_notify_url=" + order_cancel_notify_url + "]";
		}

    }

    public String getData() throws IOException {
        return URLUtils.getInstance().urlEncode(JsonUtils.getInstance().objectToJson(data));
    }

    public void setData(CancelOrderRequstData data) {
        this.data = data;
    }
}
