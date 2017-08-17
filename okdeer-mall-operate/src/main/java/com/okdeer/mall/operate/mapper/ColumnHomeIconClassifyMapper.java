/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ColumnHomeIconClassifyMapper.java
 * @Date 2017-08-15 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.operate.entity.ColumnHomeIconClassify;

public interface ColumnHomeIconClassifyMapper extends IBaseMapper {

	/**
	 * @Description: 通过iconid查询关联的导航分类列表
	 * @param homeIconId
	 * @return   
	 * @author xuzq01
	 * @date 2017年8月15日
	 */
	List<ColumnHomeIconClassify> findListByHomeIconId(String homeIconId);

	/**
	 * @Description: 通过homeIconId删除关联的分类信息
	 * @param homeIconId   
	 * @author xuzq01
	 * @date 2017年8月16日
	 */
	void deleteByHomeIconId(String homeIconId);

}