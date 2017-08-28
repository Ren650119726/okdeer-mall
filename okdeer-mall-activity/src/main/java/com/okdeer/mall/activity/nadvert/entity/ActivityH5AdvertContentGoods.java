/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityH5AdvertContentGoods.java
 * @Date 2017-08-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.nadvert.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * h5_广告活动-广告商品列表
 * 
 * @author mengsj
 * @version 1.0 2017-08-10
 */
public class ActivityH5AdvertContentGoods implements Serializable {

    /**
	 * @Fields serialVersionUID : 序列号
	 */
	private static final long serialVersionUID = 6598777012746594384L;
	private String id;
    /**
     * 活动id
     */
    private String activityId;
    /**
     * 活动内容id
     */
    private String contentId;
    
    /**
     * 商品id
     */
    private String storeSkuId;
    
    /**
     * @Fields skuPrice : 商品价格
     */
    private BigDecimal skuPrice;
    
    /**
     * @Fields skuName : 商品名称
     */
    private String skuName;
    
    /**
     * 商品图片
     */
    private String goodsSkuPic;
    /**
     *  图片设置
     */
    private String picUrl;
    
    /**
     * @Fields sellableStock : 可销售库存
     */
    private Integer sellableStock;
    
    /**
     * @Fields lockedStock : 锁定库存
     */
    private Integer lockedStock;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建人
     */
    private String createUserId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getStoreSkuId() {
        return storeSkuId;
    }

    public void setStoreSkuId(String storeSkuId) {
        this.storeSkuId = storeSkuId;
    }

    public String getGoodsSkuPic() {
        return goodsSkuPic;
    }

    public void setGoodsSkuPic(String goodsSkuPic) {
        this.goodsSkuPic = goodsSkuPic;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

	public BigDecimal getSkuPrice() {
		return skuPrice;
	}

	public void setSkuPrice(BigDecimal skuPrice) {
		this.skuPrice = skuPrice;
	}

	public String getSkuName() {
		return skuName;
	}

	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}

	public Integer getSellableStock() {
		return sellableStock;
	}

	public void setSellableStock(Integer sellableStock) {
		this.sellableStock = sellableStock;
	}

	public Integer getLockedStock() {
		return lockedStock;
	}

	public void setLockedStock(Integer lockedStock) {
		this.lockedStock = lockedStock;
	}
}