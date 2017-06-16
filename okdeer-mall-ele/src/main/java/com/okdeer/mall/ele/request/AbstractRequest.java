package com.okdeer.mall.ele.request;

/**
 * 抽象request类
 */
public abstract class AbstractRequest {
    protected String app_id;
    protected int salt;
    protected String signature;

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
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
