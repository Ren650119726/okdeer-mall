package com.okdeer.mall.points.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 
 * ClassName: TeshProductDetailVo 
 * @Description: 特奢汇商品详情
 * @author tangy
 * @date 2016年12月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     1.3.0          2016年12月15日                               tangy
 */
public class TeshProductDetailVo implements Serializable {

	/**
	 * @Fields serialVersionUID : 
	 */
	private static final long serialVersionUID = 6359640022967321850L;
	
	/** 商品编号 */
	private String productCode;
	/** 商品名称 */
	private String productName;
	/** 商品上下架状态 1：上架  0：下架 */
	private String productShelves;
	/** 商品id */
	private String productId;
	/** 商品描述 */
	private String productDesc;
	/** 品牌编号 */
	private String brandCode;
	/** 品牌名称 */
	private String brandName;
	/** 一级分类类目编号 */
	private String firstCatCode;
	/** 一级分类类目名称 */
	private String firstCatName;
	/** 二级分类类目编号 */
	private String secondCatCode;
	/** 二级分类类目名称 */
	private String secondCatName;
	/** 三级分类类目编号 */
	private String categoryCode;
	/** 三级分类类目名称 */
	private String categoryName;
	/** 市场价 */
	private BigDecimal markerPrice;
	/** 合作方进货价 */
	private BigDecimal distPrice;
	/** 产品密度 */
	private String productDensity;
	/** 产品高度 */
	private String productHeight;
	/** 产品长度 */
	private String productLong;
	/** 产品产地 */
	private String productProducer;
	/** 产品重量 */
	private String productWeight;
	/** 产品宽度 */
	private String productWidth;
	/** 第一属性名称 */
	private String colorName;
	/** 第二属性名称 */
	private String sizeName;
	/** sku编号集合 */
	private List<TeshProductSkuVo> skuList;
	/** 主图集合 */
	private List<TeshProductPicVo> mainImageList;
	/** 图片信息集合 */
	private List<TeshProductPicVo> imageList;
	
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
	
	public String getProductShelves() {
		return productShelves;
	}
	
	public void setProductShelves(String productShelves) {
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
	
	public String getFirstCatName() {
		return firstCatName;
	}
	
	public void setFirstCatName(String firstCatName) {
		this.firstCatName = firstCatName;
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
	
	public String getSizeName() {
		return sizeName;
	}
	
	public void setSizeName(String sizeName) {
		this.sizeName = sizeName;
	}
	
	public List<TeshProductSkuVo> getSkuList() {
		return skuList;
	}
	
	public void setSkuList(List<TeshProductSkuVo> skuList) {
		this.skuList = skuList;
	}
	
	public List<TeshProductPicVo> getImageList() {
		return imageList;
	}
	
	public void setImageList(List<TeshProductPicVo> imageList) {
		this.imageList = imageList;
	}
	
	public List<TeshProductPicVo> getMainImageList() {
		return mainImageList;
	}
	
	public void setMainImageList(List<TeshProductPicVo> mainImageList) {
		this.mainImageList = mainImageList;
	}

}
