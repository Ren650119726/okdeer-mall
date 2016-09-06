package com.okdeer.mall.order.pay.entity;

import java.io.Serializable;

/**
 * 返回消息
 * 
 * @pr yschome-mall
 * @author guocp
 * @date 2016年3月24日 下午4:47:00
 */
public class FinanceResponseResult implements Serializable {

    /** 序列号 */
    private static final long serialVersionUID = -5150933732980893154L;

    /**
     * 成功状态码
     */
    public static final Integer SUCCESS_CODE = 0;

    /**
     * 失败状态码
     */
    public static final Integer FAILED_CODE = 1;

    /**
     * 交易号，原路返回，多条记录，只返回一个
     */
    private String refundNo;
    /**
     * 返回结果码 0成功
     */
    private Integer status;

    /**
     * 构造方法
     */
    public FinanceResponseResult() {

    }

    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
