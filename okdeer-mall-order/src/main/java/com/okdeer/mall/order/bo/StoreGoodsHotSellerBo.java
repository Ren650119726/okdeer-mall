/** 
 *@Project: okdeer-mall-order 
 *@Author: xuzq01
 *@Date: 2017年8月21日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.order.bo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * ClassName: StoreGoodsHotSellerBo 
 * @Description: 用于统计店铺销售商品
 * @author xuzq01
 * @date 2017年8月21日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public class StoreGoodsHotSellerBo {

    /**
     * 主键id
     */
    private String id;
    /**
     * 店铺id
     */
    private String storeId;
    /**
     * 商品id
     */
    private String storeSkuId;
    /**
     * spu商品分类id(三级)
     */
    private String spuCategoryId;
    /**
     * 销售量
     */
    private Integer quantity;
    /**
     * 销售额
     */
    private BigDecimal sale;
    /**
     * 统计日期
     */
    private Date collectDate;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreSkuId() {
        return storeSkuId;
    }

    public void setStoreSkuId(String storeSkuId) {
        this.storeSkuId = storeSkuId;
    }

    public String getSpuCategoryId() {
        return spuCategoryId;
    }

    public void setSpuCategoryId(String spuCategoryId) {
        this.spuCategoryId = spuCategoryId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSale() {
        return sale;
    }

    public void setSale(BigDecimal sale) {
        this.sale = sale;
    }

    public Date getCollectDate() {
        return collectDate;
    }

    public void setCollectDate(Date collectDate) {
        this.collectDate = collectDate;
    }


}
