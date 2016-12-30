package com.okdeer.mall.points.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * ClassName: TeshProductVo 
 * @Description: 特奢汇商品
 * @author tangy
 * @date 2016年12月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     1.3.0          2016年12月15日                               tangy
 */
public class TeshProductVo implements Serializable {

	/**
	 * @Fields serialVersionUID : 
	 */
	private static final long serialVersionUID = 5519133109738713095L;
	
	/**
	 * 商品id
	 */
	private String productId;

	/**
	 * 商品名称
	 */
	private String productName;

	/**
	 * 商品编号
	 */
	private String productCode;

	/**
	 * 市场价
	 */
	private BigDecimal marketPrice;

	/**
	 * 合作方进货价
	 */
	private BigDecimal distPrice;

	/**
	 * 品牌编号
	 */
	private String brandCode;

	/**
	 * 品牌名称
	 */
	private String brandName;

	/**
	 * 分类编号
	 */
	private String catCode;

	/**
	 * 分类名称
	 */
	private String catName;

	/**
	 * 图片路径
	 */
	private String mainPic;

	/**
	 * sku编号
	 */
	private String skuCode;
	
	public String getProductId() {
		return productId;
	}
	
	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}
	
	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public BigDecimal getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(BigDecimal marketPrice) {
		this.marketPrice = marketPrice;
	}
	
	public BigDecimal getDistPrice() {
		return distPrice;
	}
	
	public void setDistPrice(BigDecimal distPrice) {
		this.distPrice = distPrice;
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
	
	public String getCatCode() {
		return catCode;
	}
	
	public void setCatCode(String catCode) {
		this.catCode = catCode;
	}
	
	public String getCatName() {
		return catName;
	}

	public void setCatName(String catName) {
		this.catName = catName;
	}

	public String getMainPic() {
		return mainPic;
	}
	
	public void setMainPic(String mainPic) {
		this.mainPic = mainPic;
	}
	
	public String getSkuCode() {
		return skuCode;
	}
	
	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}
 
}
