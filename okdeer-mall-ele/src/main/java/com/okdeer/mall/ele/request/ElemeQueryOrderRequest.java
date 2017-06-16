package com.okdeer.mall.ele.request;


import com.okdeer.mall.ele.util.JsonUtils;
import com.okdeer.mall.ele.util.URLUtils;

import java.io.IOException;

/**
 * 查询订单对应字段
 */
public class ElemeQueryOrderRequest extends AbstractRequest {

    private ElemeQueryRequestData data;

    public String getData() throws IOException {
        return URLUtils.getInstance().urlEncode(JsonUtils.getInstance().objectToJson(data));
    }

    public void setData(ElemeQueryRequestData data) {
        this.data = data;
    }

    public static class ElemeQueryRequestData {
        private String partner_order_code;

        public String getPartner_order_code() {
            return partner_order_code;
        }

        public void setPartner_order_code(String partner_order_code) {
            this.partner_order_code = partner_order_code;
        }
    }
}
