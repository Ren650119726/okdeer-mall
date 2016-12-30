
package com.okdeer.mall.points.entity;

/**
 * ClassName: PointsTeshProductQuery 
 * @Description: 积分商品查询参数实体类
 * @author zengjizu
 * @date 2016年12月19日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class PointsTeshProductQuery {

	/**
	 * 商品编号
	 */
	private String productCode;

	/**
	 * sku编号
	 */
	private String skuCode;

	/**
	 * 商品名称
	 */
	private String productName;

	/**
	 * 三级分类类目名称
	 */
	private String categoryName;

	/**
	 * 品牌名称
	 */
	private String brandName;

	/**
	 * 是否支持 1:支持0：不支持
	 */
	private Integer productShelves;

	/**
	 * 发布开始时间(yyyy-MM-dd HH:mm:ss)
	 */
	private String publishStartTime;

	/**
	 * 发布结束时间(yyyy-MM-dd HH:mm:ss)
	 */
	private String publishEndTime;

	/**
	 * 状态 0:待上架 1：已经上架
	 */
	private Integer status;

	/**
	 * 查询数据上限(列表导出使用)
	 */
	private Integer limitNum;

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public Integer getProductShelves() {
		return productShelves;
	}

	public void setProductShelves(Integer productShelves) {
		this.productShelves = productShelves;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getLimitNum() {
		return limitNum;
	}

	public void setLimitNum(Integer limitNum) {
		this.limitNum = limitNum;
	}

	public String getPublishStartTime() {
		return publishStartTime;
	}

	public void setPublishStartTime(String publishStartTime) {
		this.publishStartTime = publishStartTime;
	}

	public String getPublishEndTime() {
		return publishEndTime;
	}

	public void setPublishEndTime(String publishEndTime) {
		this.publishEndTime = publishEndTime;
	}

}
