package com.okdeer.mall.operate.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.operate.column.service.OperationRelationService;
import com.okdeer.mall.operate.dto.ColumnOperationRelationDto;
import com.okdeer.mall.operate.dto.ColumnOperationRelationParamDto;
import com.okdeer.mall.operate.entity.ColumnOperationRelation;
import com.okdeer.mall.operate.service.OperationRelationApi;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * ClassName: OperationRelationApiImpl
 *
 * @author wangf01
 * @Description: 运营栏目-关联-api-impl
 * @date 2017年3月13日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.operate.service.OperationRelationApi")
public class OperationRelationApiImpl implements OperationRelationApi {

    /**
     * 注入service
     */
    @Autowired
    private OperationRelationService operationRelationService;

    @Override
    public List<ColumnOperationRelationDto> findByParam(ColumnOperationRelationParamDto paramDto) throws Exception {
        List<ColumnOperationRelation> relationList = operationRelationService.findByParam(paramDto);
        List<ColumnOperationRelationDto> relationDtoList = BeanMapper.mapList(relationList, ColumnOperationRelationDto.class);
        return relationDtoList;
    }
}
