package com.okdeer.mall.operate.column.service;

import com.okdeer.mall.operate.dto.ColumnOperationVersionParamDto;
import com.okdeer.mall.operate.entity.ColumnOperationVersion;

import java.util.List;

/**
 * ClassName: OperationVersionService
 *
 * @author wangf01
 * @Description: 运营栏目-版本-service
 * @date 2017年3月13日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public interface OperationVersionService {
    /**
     * 查询符合条件的数据
     *
     * @param paramDto ColumnOperationVersionParamDto
     * @return
     */
    List<ColumnOperationVersion> findByParam(ColumnOperationVersionParamDto paramDto) throws Exception;

}
