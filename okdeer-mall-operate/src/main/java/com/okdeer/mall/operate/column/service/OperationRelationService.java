package com.okdeer.mall.operate.column.service;

import com.okdeer.mall.operate.dto.ColumnOperationRelationParamDto;
import com.okdeer.mall.operate.entity.ColumnOperationRelation;

import java.util.List;

/**
 * ClassName: OperationRelationService
 *
 * @author wangf01
 * @Description: 运营栏目-关联-serivce
 * @date 2017年3月13日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public interface OperationRelationService {

    /**
     * 查询符合条件的数据
     *
     * @param paramDto ColumnOperationRelationParamDto
     * @return
     */
    List<ColumnOperationRelation> findByParam(ColumnOperationRelationParamDto paramDto) throws Exception;
}
