package com.okdeer.mall.order.pay.entity;

import java.io.Serializable;

/**
 * 返回消息
 * 
 * @pr yschome-mall
 * @author guocp
 * @date 2016年3月24日 下午4:47:00
 */
public class ResponseResult implements Serializable {

    /** 序列号 */
    private static final long serialVersionUID = 2958131996177917611L;

    /** 成功状态码 */
    public static final String SUCCESS_CODE = "0";

    /**
     * 交易号，原路返回，多条记录，只返回一个
     */
    private String tradeNum;
    /**
     * 返回结果码 0成功
     */
    private String code;
    /**
     * 返回结果描述
     */
    private String msg;

    /**
     * 构造方法
     */
    public ResponseResult() {

    }

    public String getTradeNum() {
        return tradeNum;
    }

    public void setTradeNum(String tradeNum) {
        this.tradeNum = tradeNum;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "BaseResultVo{" + "tradeNum='" + tradeNum + '\'' + ", code='" + code + '\'' + ", msg='" + msg + '\''
                + '}';
    }
}
