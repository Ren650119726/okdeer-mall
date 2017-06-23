package com.okdeer.mall.ele.entity;

import java.math.BigDecimal;

/**
 * ClassName: ExpressOrderItem
 *
 * @author wangf01
 * @Description: 配送订单对象-订单项数据
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public class ExpressOrderItem {

    /**
     * 商品编号
     */
    private String item_id;

    /**
     * 商品名称
     *
     * @desc 必填
     */
    private String item_name;

    /**
     * 商品数量
     *
     * @desc 必填
     */
    private Integer item_quantity;

    /**
     * 商品原价
     *
     * @desc 必填
     */
    private BigDecimal item_price;

    /**
     * 商品实际支付金额
     *
     * @desc 必填
     */
    private BigDecimal item_actual_price;

    /**
     * 商品尺寸
     */
    private Integer item_size;

    /**
     * 商品备注
     */
    private String item_remark;

    /**
     * 是否需要ele打包 0:否 1:是
     *
     * @desc 必填
     */
    private int is_need_package = 0;

    /**
     * 是否代购 0:否
     *
     * @desc 必填
     */
    private int is_agent_purchase = 0;

    /**
     * 代购进价, 如果需要代购 此项必填
     */
    private BigDecimal agent_purchase_price;

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public Integer getItem_quantity() {
        return item_quantity;
    }

    public void setItem_quantity(Integer item_quantity) {
        this.item_quantity = item_quantity;
    }

    public BigDecimal getItem_price() {
        return item_price;
    }

    public void setItem_price(BigDecimal item_price) {
        this.item_price = item_price;
    }

    public BigDecimal getItem_actual_price() {
        return item_actual_price;
    }

    public void setItem_actual_price(BigDecimal item_actual_price) {
        this.item_actual_price = item_actual_price;
    }

    public Integer getItem_size() {
        return item_size;
    }

    public void setItem_size(Integer item_size) {
        this.item_size = item_size;
    }

    public String getItem_remark() {
        return item_remark;
    }

    public void setItem_remark(String item_remark) {
        this.item_remark = item_remark;
    }

    public int getIs_need_package() {
        return is_need_package;
    }

    public void setIs_need_package(int is_need_package) {
        this.is_need_package = is_need_package;
    }

    public int getIs_agent_purchase() {
        return is_agent_purchase;
    }

    public void setIs_agent_purchase(int is_agent_purchase) {
        this.is_agent_purchase = is_agent_purchase;
    }

    public BigDecimal getAgent_purchase_price() {
        return agent_purchase_price;
    }

    public void setAgent_purchase_price(BigDecimal agent_purchase_price) {
        this.agent_purchase_price = agent_purchase_price;
    }
}
