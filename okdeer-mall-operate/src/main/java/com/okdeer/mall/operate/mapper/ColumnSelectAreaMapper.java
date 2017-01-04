/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ColumnSelectAreaMapper.java
 * @Date 2016-12-30 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.operate.entity.ColumnSelectArea;

public interface ColumnSelectAreaMapper extends IBaseMapper {

	/**
	 * @Description: 根据栏目ID查询数据
	 * @param columnId 栏目ID
	 * @return List<ColumnSelectArea>  
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	List<ColumnSelectArea> findListByColumnId(String columnId);

	/**
	 * @Description: 根据栏目ID集合查询数据
	 * @param columnIds  栏目ID集合
	 * @return List<ColumnSelectArea>  
	 * @throws Exception
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	List<ColumnSelectArea> findListByColumnIds(@Param("columnIds")List<String> columnIds) throws Exception;

	/**
	 * @Description: 根据栏目ID删除数据
	 * @param columnId 栏目ID   
	 * @return int 删除记录  
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	int deleteByColumnId(String columnId);

	/**
	 * @Description: 批量插入数据
	 * @param list   
	 * @return 成功插入记录数  
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	int insertMore(@Param("list") List<ColumnSelectArea> list);

	/**
	 * @Description: 根据城市查询对应栏位的数据
	 * @param provinceId  省ID
	 * @param cityId  城市ID
	 * @param columnType   栏位类型 1:首页ICON 2:APP端服务商品推荐
	 * @return int 成功插入记录数  
	 * @throws Exception
	 * @author tangzj02
	 * @date 2016年12月30日
	 */
	List<String> findColumnIdsByCity(@Param("provinceId") String provinceId, @Param("cityId") String cityId,
			@Param("columnType") Integer columnType);
}