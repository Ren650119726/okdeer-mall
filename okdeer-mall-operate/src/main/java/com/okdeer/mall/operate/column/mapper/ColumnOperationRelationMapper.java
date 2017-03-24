package com.okdeer.mall.operate.column.mapper;

import com.okdeer.mall.operate.dto.ColumnOperationRelationParamDto;
import com.okdeer.mall.operate.entity.ColumnOperationRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ClassName: ColumnOperationRelationMapper
 *
 * @author wangf01
 * @Description: 运营栏目关系-mapper
 * @date 2017年3月9日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public interface ColumnOperationRelationMapper {

    /**
     * 查询符合条件的数据
     *
     * @param paramDto ColumnOperationRelationParamDto
     * @return
     */
    List<ColumnOperationRelation> findByParam(ColumnOperationRelationParamDto paramDto);

    /**
     * 批量新增数据
     *
     * @param relationList List<ColumnOperationRelation>
     */
    void adds(@Param(value = "relationList") List<ColumnOperationRelation> relationList);

    /**
     * 根据条件删除数据
     *
     * @param paramDto ColumnOperationRelationParamDto
     */
    void deleteByParam(ColumnOperationRelationParamDto paramDto);
}
