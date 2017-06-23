package com.okdeer.mall.ele.config;

/**
 * 请求常量
 */
public class RequestConstant {
    /**
     * 获取token
     */
    public static final String obtainToken = "/get_access_token";

    /**
     * 创建订单
     */
    public static final String orderCreate = "/v2/order";

    /**
     * 取消 订单
     */
    public static final String orderCancel = "/v2/order/cancel";

    /**
     * 订单查询
     */
    public static final String orderQuery = "/v2/order/query";

    /**
     * 订单查询
     */
    public static final String orderComplaint = "/v2/order/complaint";

    /**
     * 骑手座标
     */
    public static final String orderCarrier = "/v2/order/carrier";
}
