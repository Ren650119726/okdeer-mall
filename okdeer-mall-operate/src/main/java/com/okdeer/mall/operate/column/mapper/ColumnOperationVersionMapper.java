package com.okdeer.mall.operate.column.mapper;

import com.okdeer.mall.operate.dto.ColumnOperationVersionParamDto;
import com.okdeer.mall.operate.entity.ColumnOperationVersion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ClassName: ColumnOperationVersionMapper
 *
 * @author wangf01
 * @Description: 运营栏目-版本-mapper
 * @date 2017年3月13日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public interface ColumnOperationVersionMapper {

    /**
     * 查询符合条件的数据
     *
     * @param paramDto ColumnOperationVersionParamDto
     * @return
     */
    List<ColumnOperationVersion> findByParam(ColumnOperationVersionParamDto paramDto);

    /**
     * 批量新增数据
     *
     * @param versionList List<ColumnOperationVersion>
     */
    void adds(@Param(value = "versionList") List<ColumnOperationVersion> versionList);

    /**
     * 根据条件删除数据
     *
     * @param paramDto ColumnOperationVersionParamDto
     */
    void deleteByParam(ColumnOperationVersionParamDto paramDto);
}
