/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * IntegralTeshProductPic.java
 * @Date 2016-12-19 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.integral.entity;

/**
 * ClassName: IntegralTeshProductPic 
 * @Description: 特奢汇商品图片实体类
 * @author zengjizu
 * @date 2016年12月19日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class IntegralTeshProductPic {

    /**
     * 主键
     */
    private String id;
    /**
     * 商品id
     */
    private String productId;
    private String imageName;
    /**
     * 七牛存储图片
     */
    private String newImagePath;
    /**
     * 特奢汇图片地址
     */
    private String imagePath;
    /**
     * 是否主图0:是1:否
     */
    private Boolean isMain;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getNewImagePath() {
        return newImagePath;
    }

    public void setNewImagePath(String newImagePath) {
        this.newImagePath = newImagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Boolean getIsMain() {
        return isMain;
    }

    public void setIsMain(Boolean isMain) {
        this.isMain = isMain;
    }
}