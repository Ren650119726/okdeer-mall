package com.okdeer.mall.points.vo;

import java.io.Serializable;

/**
 * 
 * ClassName: TeshProductPicVo 
 * @Description: 特奢汇商品图片
 * @author tangy
 * @date 2016年12月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     1.3.0          2016年12月15日                               tangy
 */
public class TeshProductPicVo implements Serializable {

	/**
	 * @Fields serialVersionUID : 
	 */
	private static final long serialVersionUID = -3291535539470357215L;
	
	/**
	 * 图片名称
	 */
	private String imageName;
	
	/**
	 * 图片格式
	 */
	private String imageType;
	
	/**
	 * 图片路径
	 */
	private String imagePath;
	
	public String getImageName() {
		return imageName;
	}
	
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	
	public String getImageType() {
		return imageType;
	}
	
	public void setImageType(String imageType) {
		this.imageType = imageType;
	}
	
	public String getImagePath() {
		return imagePath;
	}
	
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
}
