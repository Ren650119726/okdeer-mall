package com.okdeer.mall.ele.entity;

import java.util.Date;

/**
 * ClassName: ExpressPushLog
 *
 * @author wangf01
 * @Description: 订单快递配送信息推送日志
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public class ExpressPushLog {

    /**
     * 主键id
     */
    private String id;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 推送json数据
     */
    private String pushJson;

    /**
     * 结果json数据
     */
    private String resultJson;

    /**
     * 创建时间
     */
    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPushJson() {
        return pushJson;
    }

    public void setPushJson(String pushJson) {
        this.pushJson = pushJson;
    }

    public String getResultJson() {
        return resultJson;
    }

    public void setResultJson(String resultJson) {
        this.resultJson = resultJson;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
