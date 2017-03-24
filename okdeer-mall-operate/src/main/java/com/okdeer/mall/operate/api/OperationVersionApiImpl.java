package com.okdeer.mall.operate.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.operate.column.service.OperationVersionService;
import com.okdeer.mall.operate.dto.ColumnOperationVersionDto;
import com.okdeer.mall.operate.dto.ColumnOperationVersionParamDto;
import com.okdeer.mall.operate.entity.ColumnOperationVersion;
import com.okdeer.mall.operate.service.OperationVersionApi;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * ClassName: OperationVersionApiImpl
 *
 * @author wangf01
 * @Description: 运营栏目-版本-api-impl
 * @date 2017年3月13日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.operate.service.OperationVersionApi")
public class OperationVersionApiImpl implements OperationVersionApi {

    /**
     * 注入service
     */
    @Autowired
    private OperationVersionService operationVersionService;

    @Override
    public List<ColumnOperationVersionDto> findByParam(ColumnOperationVersionParamDto paramDto) throws Exception {
        List<ColumnOperationVersion> versionList = operationVersionService.findByParam(paramDto);
        List<ColumnOperationVersionDto> versionDtoList = BeanMapper.mapList(versionList, ColumnOperationVersionDto.class);
        return versionDtoList;
    }
}
