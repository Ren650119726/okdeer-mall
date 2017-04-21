/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ColumnNativeSubjectGoodsMapper.java
 * @Date 2017-04-13 Created
 * ע�⣺�����ݽ���������¹��˾�ڲ����ģ���ֹ��й�Լ�������������ҵĿ��
 */
package com.okdeer.mall.operate.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.operate.entity.ColumnNativeSubjectGoods;

public interface ColumnNativeSubjectGoodsMapper extends IBaseMapper {

	/**
	 * @Description: 通过主键删除
	 * @param columnNativeSubjectId
	 * @author zhangkn
	 * @date 2017年4月14日
	 */
	void deleteByColumnNativeSubjectId (String columnNativeSubjectId);
	
	/**
	 * @Description: 批量添加
	 * @param list
	 * @author zhangkn
	 * @date 2017年4月14日
	 */
	void addBatch(List<ColumnNativeSubjectGoods> list);
	
	/**
	 * @Description: 通过专题id加载列表
	 * @param columnNativeSubjectId
	 * @return
	 * @author zhangkn
	 * @date 2017年4月14日
	 */
	List<ColumnNativeSubjectGoods> findByColumnNativeSubjectId (String columnNativeSubjectId);
}