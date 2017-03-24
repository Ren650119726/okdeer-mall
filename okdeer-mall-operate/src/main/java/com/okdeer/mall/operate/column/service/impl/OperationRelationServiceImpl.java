package com.okdeer.mall.operate.column.service.impl;

import com.okdeer.mall.operate.column.mapper.ColumnOperationRelationMapper;
import com.okdeer.mall.operate.column.service.OperationRelationService;
import com.okdeer.mall.operate.dto.ColumnOperationRelationParamDto;
import com.okdeer.mall.operate.entity.ColumnOperationRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ClassName: OperationRelationServiceImpl
 *
 * @author wangf01
 * @Description: 运营栏目-关联-serivce-impl
 * @date 2017年3月13日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
@Service
public class OperationRelationServiceImpl implements OperationRelationService {

    /**
     * 注入mapper
     */
    @Autowired
    private ColumnOperationRelationMapper columnOperationRelationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ColumnOperationRelation> findByParam(ColumnOperationRelationParamDto paramDto) throws Exception {
        return columnOperationRelationMapper.findByParam(paramDto);
    }
}
