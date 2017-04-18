package com.okdeer.mall.operate.operatefields.mq.dto;

import java.io.Serializable;

public class GoodsChangedMsgDto implements Serializable {

    /**
     * 序列化器
     */
    private static final long serialVersionUID = -8412137251108715057L;

    /**
     * 店铺ID
     */
    private String storeId;
    
    /**
     * 店铺商品Id
     */
    private String storeSkuId;

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
    
}
