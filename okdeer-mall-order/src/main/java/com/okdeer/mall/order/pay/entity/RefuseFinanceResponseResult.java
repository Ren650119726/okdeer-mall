/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: RefuseFinanceResponseResult.java 
 * @Date: 2016年4月12日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
package com.okdeer.mall.order.pay.entity;

import java.io.Serializable;

/**
 * 返回消息
 * 
 * @project yschome-mall
 * @author wusw
 * @date 2016年4月12日 下午3:02:59
 */
public class RefuseFinanceResponseResult implements Serializable {
    /** 序列化ID */
    private static final long serialVersionUID = -1049173982987448991L;

    /**
     * 成功状态码
     */
    public static final Integer SUCCESS_CODE = 0;

    /**
     * 失败状态码
     */
    public static final Integer FAILED_CODE = 1;

    /**
     * 订单ID，原路返回，多条记录，只返回一个
     */
    private String orderId;
    /**
     * 返回结果码 0成功
     */
    private Integer status;

    /**
     * 构造方法
     */
    public RefuseFinanceResponseResult() {

    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
