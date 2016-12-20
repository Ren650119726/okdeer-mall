package com.okdeer.mall.points.vo;

import java.io.Serializable;

/**
 * 
 * ClassName: TeshProductSkuVo 
 * @Description: 特奢汇商品sku
 * @author tangy
 * @date 2016年12月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     1.3.0          2016年12月15日                               tangy
 */
public class TeshProductSkuVo implements Serializable {

	/**
	 * @Fields serialVersionUID : 
	 */
	private static final long serialVersionUID = 5025021118138077915L;
	
	/**
	 * sku编号
	 */
	private String skuCode;
	
	/**
	 * 第一属性值
	 */
	private String colorValue;
	
	/**
	 * 第二属性值
	 */
	private String sizeValue;
	
	public String getSkuCode() {
		return skuCode;
	}
	
	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}
	
	public String getColorValue() {
		return colorValue;
	}
	
	public void setColorValue(String colorValue) {
		this.colorValue = colorValue;
	}
	
	public String getSizeValue() {
		return sizeValue;
	}
	
	public void setSizeValue(String sizeValue) {
		this.sizeValue = sizeValue;
	}
	
}
