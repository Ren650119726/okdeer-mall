package com.okdeer.mall.operate.column.service.impl;

import com.okdeer.mall.operate.column.mapper.ColumnOperationVersionMapper;
import com.okdeer.mall.operate.column.service.OperationVersionService;
import com.okdeer.mall.operate.dto.ColumnOperationVersionParamDto;
import com.okdeer.mall.operate.entity.ColumnOperationVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ClassName: OperationVersionServiceImpl
 *
 * @author wangf01
 * @Description: 运营栏目-版本-service-impl
 * @date 2017年3月13日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
@Service
public class OperationVersionServiceImpl implements OperationVersionService {

    /**
     * 注入mapper
     */
    @Autowired
    private ColumnOperationVersionMapper columnOperationVersionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ColumnOperationVersion> findByParam(ColumnOperationVersionParamDto paramDto) throws Exception {
        return columnOperationVersionMapper.findByParam(paramDto);
    }
}
