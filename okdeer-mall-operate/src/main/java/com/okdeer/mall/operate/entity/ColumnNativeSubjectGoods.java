/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ColumnNativeSubjectGoods.java
 * @Date 2017-04-13 Created
 * ע�⣺�����ݽ���������¹��˾�ڲ����ģ���ֹ��й�Լ�������������ҵĿ��
 */
package com.okdeer.mall.operate.entity;

/**
 * 原生专题关联商品
 * @author zhangkn
 * @version 1.0 2017-04-13
 */
public class ColumnNativeSubjectGoods implements java.io.Serializable{

    /**
     * 主键
     */
    private String id;
    /**
     * 原生专题主键id
     */
    private String columnNativeSubjectId;
    /**
     * 关联商品库id
     */
    private String goodsSkuId;
    /**
     * 排序(从大到小)
     */
    private Integer sort;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColumnNativeSubjectId() {
        return columnNativeSubjectId;
    }

    public void setColumnNativeSubjectId(String columnNativeSubjectId) {
        this.columnNativeSubjectId = columnNativeSubjectId;
    }
    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

	
	public String getGoodsSkuId() {
		return goodsSkuId;
	}

	
	public void setGoodsSkuId(String goodsSkuId) {
		this.goodsSkuId = goodsSkuId;
	}
}