package com.okdeer.mall.ele.entity;

/**
 * ClassName: ExpressRequestJson
 *
 * @author wangf01
 * @Description: 配送订单Json
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public class ExpressRequestJson {

    /**
     * 商户App Id
     *
     * @desc 必填
     */
    private String app_id;

    /**
     * 推送订单结构
     *
     * @desc 必填
     */
    private ExpressOrderData data;

    /**
     * 1000-9999随机数
     *
     * @desc 必填
     */
    private int salt;

    /**
     * 签名
     *
     * @desc 必填
     */
    private String signature;

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public ExpressOrderData getData() {
        return data;
    }

    public void setData(ExpressOrderData data) {
        this.data = data;
    }

    public int getSalt() {
        return salt;
    }

    public void setSalt(int salt) {
        this.salt = salt;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
