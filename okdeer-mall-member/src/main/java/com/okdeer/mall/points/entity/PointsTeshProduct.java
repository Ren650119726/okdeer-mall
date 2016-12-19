/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * PointsTeshProduct.java
 * @Date 2016-12-19 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */

package com.okdeer.mall.points.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.okdeer.base.common.enums.Disabled;

/**
 * ClassName: PointsTeshProduct 
 * @Description: 特奢汇商品实体类
 * @author zengjizu
 * @date 2016年12月19日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class PointsTeshProduct {

	/**
	 * 主键id
	 */
	private String id;

	/**
	 * sku编号
	 */
	private String skuCode;

	/**
	 * 主图地址
	 */
	private String mainPicUrl;

	/**
	 * 商品编号
	 */
	private String productCode;

	/**
	 * 商品名称
	 */
	private String productName;

	/**
	 * 1:上架0：下架
	 */
	private Integer productShelves;

	/**
	 * 商品id
	 */
	private String productId;

	/**
	 * 商品描述
	 */
	private String productDesc;

	/**
	 * 品牌编号
	 */
	private String brandCode;

	/**
	 * 品牌名称
	 */
	private String brandName;

	/**
	 * 一级分类类目编号
	 */
	private String firstCatCode;

	/**
	 * 一级分类类目名称
	 */
	private String firstCatBame;

	/**
	 * 二级分类类目编号
	 */
	private String secondCatCode;

	/**
	 * 二级分类类目名称
	 */
	private String secondCatName;

	/**
	 * 三级分类类目编号
	 */
	private String categoryCode;

	/**
	 * 三级分类类目名称
	 */
	private String categoryName;

	/**
	 * 市场价
	 */
	private BigDecimal markerPrice;

	/**
	 * 合作方进货价
	 */
	private BigDecimal distPrice;

	/**
	 * 产品密度
	 */
	private String productDensity;

	/**
	 * 产品高度
	 */
	private String productHeight;

	/**
	 * 产品长度
	 */
	private String productLong;

	/**
	 * 产品产地
	 */
	private String productProducer;

	/**
	 * 产品重量
	 */
	private String productWeight;

	/**
	 * 产品宽度
	 */
	private String productWidth;

	/**
	 * 第一属性名称
	 */
	private String colorName;

	/**
	 * 第一属性值
	 */
	private String colorValue;

	/**
	 * 第二属性名称
	 */
	private String sizeName;

	/**
	 * 第二属性值
	 */
	private String sizeValue;

	/**
	 * 商品状态：0:待上架 1：已经上架
	 */
	private Integer status;

	/**
	 * 兑换积分
	 */
	private Integer scores;

	/**
	 * 添加时间
	 */
	private Date createTime;

	/**
	 * 创建用户id
	 */
	private String createUserId;

	/**
	 * 修改时间
	 */
	private Date updateTime;

	/**
	 * 最后修改人
	 */
	private String updateUserId;

	/**
	 * 0:正常 1:删除
	 */
	private Disabled disabled;

	/**
	 * 同步时间
	 */
	private Date syncTime;

	/**
	 * 发布时间
	 */
	private Date publishTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public String getMainPicUrl() {
		return mainPicUrl;
	}

	public void setMainPicUrl(String mainPicUrl) {
		this.mainPicUrl = mainPicUrl;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getProductShelves() {
		return productShelves;
	}

	public void setProductShelves(Integer productShelves) {
		this.productShelves = productShelves;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getBrandCode() {
		return brandCode;
	}

	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getFirstCatCode() {
		return firstCatCode;
	}

	public void setFirstCatCode(String firstCatCode) {
		this.firstCatCode = firstCatCode;
	}

	public String getFirstCatBame() {
		return firstCatBame;
	}

	public void setFirstCatBame(String firstCatBame) {
		this.firstCatBame = firstCatBame;
	}

	public String getSecondCatCode() {
		return secondCatCode;
	}

	public void setSecondCatCode(String secondCatCode) {
		this.secondCatCode = secondCatCode;
	}

	public String getSecondCatName() {
		return secondCatName;
	}

	public void setSecondCatName(String secondCatName) {
		this.secondCatName = secondCatName;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public BigDecimal getMarkerPrice() {
		return markerPrice;
	}

	public void setMarkerPrice(BigDecimal markerPrice) {
		this.markerPrice = markerPrice;
	}

	public BigDecimal getDistPrice() {
		return distPrice;
	}

	public void setDistPrice(BigDecimal distPrice) {
		this.distPrice = distPrice;
	}

	public String getProductDensity() {
		return productDensity;
	}

	public void setProductDensity(String productDensity) {
		this.productDensity = productDensity;
	}

	public String getProductHeight() {
		return productHeight;
	}

	public void setProductHeight(String productHeight) {
		this.productHeight = productHeight;
	}

	public String getProductLong() {
		return productLong;
	}

	public void setProductLong(String productLong) {
		this.productLong = productLong;
	}

	public String getProductProducer() {
		return productProducer;
	}

	public void setProductProducer(String productProducer) {
		this.productProducer = productProducer;
	}

	public String getProductWeight() {
		return productWeight;
	}

	public void setProductWeight(String productWeight) {
		this.productWeight = productWeight;
	}

	public String getProductWidth() {
		return productWidth;
	}

	public void setProductWidth(String productWidth) {
		this.productWidth = productWidth;
	}

	public String getColorName() {
		return colorName;
	}

	public void setColorName(String colorName) {
		this.colorName = colorName;
	}

	public String getColorValue() {
		return colorValue;
	}

	public void setColorValue(String colorValue) {
		this.colorValue = colorValue;
	}

	public String getSizeName() {
		return sizeName;
	}

	public void setSizeName(String sizeName) {
		this.sizeName = sizeName;
	}

	public String getSizeValue() {
		return sizeValue;
	}

	public void setSizeValue(String sizeValue) {
		this.sizeValue = sizeValue;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getScores() {
		return scores;
	}

	public void setScores(Integer scores) {
		this.scores = scores;
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

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}

	public Disabled getDisabled() {
		return disabled;
	}

	public void setDisabled(Disabled disabled) {
		this.disabled = disabled;
	}

	public Date getSyncTime() {
		return syncTime;
	}

	public void setSyncTime(Date syncTime) {
		this.syncTime = syncTime;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

}