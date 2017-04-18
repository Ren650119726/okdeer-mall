package com.okdeer.mall.operate.operatefields.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OperateFieldContentDto implements Serializable {

    /**
     * 序列化器
     */
    private static final long serialVersionUID = -8433546043945849786L;

    /**
     * 业务id
     */
    private String pointContent;
    
    /**
     * 图片地址
     */
    private String imageUrl;
    
    /**
     * 店铺Id
     */
    private String storeId;
    
    /**
     * 店铺logoUrl
     */
    private String logoUrl;
    
    /**
     * 店铺名称
     */
    private String storeName;
    
    /**
     * 店铺机构类型
     */
    private Integer branchesType;
    
    /**
     * 店铺类型描述
     */
    private String branchesName;
    
    /**
     * 运费
     */
    private BigDecimal deliveryPrice;
    
    /**
     * 物流方式
     */
    private String shipment;
    
    /**
     * 商品Id
     */
    private String goodsId;
    
    /**
     * 名称
     */
    private String goodsName;
    
    /**
     * 商品主图
     */
    private String newUrl;
    
    /**
     * 0:单品、1:服务商品、2:无条码商品、3：上门服务店服务商品、4：到店消费服务商品、5 组合商品
     */
    private Integer skuType;
    
    /**
     * 别名
     */
    private String alias;
    
    /**
     * 是否低价商品
     */
    private Integer isLowPrice;
    
    /**
     * 活动价
     */
    private BigDecimal lowPrice;
    
    /**
     * 线上价
     */
    private BigDecimal onlinePrice;
    
    /**
     * 市场价
     */
    private BigDecimal marketPrice;
    
    /**
     * 属性
     */
    private String propertiesIndb;
    
    /**
     * 可销售库存
     */
    private Integer sellableStock;
    
    /**
     * 锁定库存
     */
    private Integer lockedStock;
    
    /**
     * 单次购买上限
     */
    private Integer tradeMax;
    
    /**
     * 低价限购数
     */
    private Integer lowPriceUpper;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 限购款数（主要是针对参加特惠活动的商品，-1表示不限制）
     */
    private Integer limitNum;
    
    /**
     * 是否参加特惠活动（0：否，1：是）
     */
    private Integer isPrivliege;
    
    /**
     * 是否支持退换货
     */
    private Integer isReturnGoods;
    
    /**
     * 是否新品的标识 1-是新品
     */
    private Integer tagType;

    public String getPointContent() {
        return pointContent;
    }

    public void setPointContent(String pointContent) {
        this.pointContent = pointContent;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Integer getBranchesType() {
        return branchesType;
    }

    public void setBranchesType(Integer branchesType) {
        this.branchesType = branchesType;
    }

    public String getBranchesName() {
        return branchesName;
    }

    public void setBranchesName(String branchesName) {
        this.branchesName = branchesName;
    }

    public BigDecimal getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(BigDecimal deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public String getShipment() {
        return shipment;
    }

    public void setShipment(String shipment) {
        this.shipment = shipment;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getNewUrl() {
        return newUrl;
    }

    public void setNewUrl(String newUrl) {
        this.newUrl = newUrl;
    }

    public Integer getSkuType() {
        return skuType;
    }

    public void setSkuType(Integer skuType) {
        this.skuType = skuType;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getIsLowPrice() {
        return isLowPrice;
    }

    public void setIsLowPrice(Integer isLowPrice) {
        this.isLowPrice = isLowPrice;
    }

    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(BigDecimal lowPrice) {
        this.lowPrice = lowPrice;
    }

    public BigDecimal getOnlinePrice() {
        return onlinePrice;
    }

    public void setOnlinePrice(BigDecimal onlinePrice) {
        this.onlinePrice = onlinePrice;
    }

    public BigDecimal getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(BigDecimal marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getPropertiesIndb() {
        return propertiesIndb;
    }

    public void setPropertiesIndb(String propertiesIndb) {
        this.propertiesIndb = propertiesIndb;
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

    public Integer getTradeMax() {
        return tradeMax;
    }

    public void setTradeMax(Integer tradeMax) {
        this.tradeMax = tradeMax;
    }

    public Integer getLowPriceUpper() {
        return lowPriceUpper;
    }

    public void setLowPriceUpper(Integer lowPriceUpper) {
        this.lowPriceUpper = lowPriceUpper;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(Integer limitNum) {
        this.limitNum = limitNum;
    }

    public Integer getIsPrivliege() {
        return isPrivliege;
    }

    public void setIsPrivliege(Integer isPrivliege) {
        this.isPrivliege = isPrivliege;
    }

    public Integer getIsReturnGoods() {
        return isReturnGoods;
    }

    public void setIsReturnGoods(Integer isReturnGoods) {
        this.isReturnGoods = isReturnGoods;
    }

    public Integer getTagType() {
        return tagType;
    }

    public void setTagType(Integer tagType) {
        this.tagType = tagType;
    }
    
}
