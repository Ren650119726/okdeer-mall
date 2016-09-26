package com.okdeer.mall.operate.column.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.okdeer.mall.operate.entity.ColumnOperationArea;
import com.okdeer.base.dal.IBaseCrudMapper;

/**
 * @DESC: 运营栏目区域关联mapper
 * @author wusw
 * @date  2016-01-13 19:13:03
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
@Repository
public interface ColumnOperationAreaMapper extends IBaseCrudMapper {
	
	/**
	 * 
	 * 批量新增运营栏目与区域关联信息
	 *
	 * @param list
	 */
	void insertAreaBatch(@Param("list")List<ColumnOperationArea> list);
	
	/**
	 * 
	 * 根据运营栏目id，删除运营栏目与区域关联信息（物理删除） 
	 *
	 * @param operationId 运营栏目id
	 */
	void deleteByOperationId(@Param("operationId")String operationId);
}