package com.okdeer.mall.operate.operatefields.mq.dto;

import java.io.Serializable;

public class GoodsChangedMsgDto implements Serializable {

    /**
     * 序列化器
     */
    private static final long serialVersionUID = -8412137251108715057L;

    /**
     * 城市Id
     */
    private String cityId;
    
    /**
     * 店铺ID
     */
    private String storeId;
    
    /**
     * 店铺商品Id
     */
    private String storeSkuId;
    
    /**
     * 栏位Id
     */
    private String fieldId;

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

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }
    
}
