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
    
    // Begin V2.4 added by maojj 2017-05-23
    /**
     * 订单Id
     */
    private String orderId;
    
    /**
     * 订单编号
     */
    private String orderNo;
    
    /**
     * 退款金额
     */
    private String refundAmount ;
    
    /**
     * 用户Id
     */
    private String userId;
    
    /**
     * 支付方式：ALIPAY 支付宝  WXPAY微信
     */
    private String payType;
    // End V2.4 added by maojj 2017-05-23

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

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(String refundAmount) {
		this.refundAmount = refundAmount;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}
    
}
