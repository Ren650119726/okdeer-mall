package com.okdeer.mall.order.service.impl;

import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.order.dto.TradeOrderExtSnapshotParamDto;
import com.okdeer.mall.order.entity.TradeOrderExtSnapshot;
import com.okdeer.mall.order.mapper.TradeOrderExtSnapshotMapper;
import com.okdeer.mall.order.service.TradeOrderExtSnapshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ClassName: TradeOrderExtSnapshotServiceImpl
 *
 * @author wangf01
 * @Description: 订单扩展信息快照-service-impl
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
@Service
public class TradeOrderExtSnapshotServiceImpl implements TradeOrderExtSnapshotService {

    /**
     * 注入-mapper
     */
    @Autowired
    private TradeOrderExtSnapshotMapper tradeOrderExtSnapshotMapper;

    @Override
    public TradeOrderExtSnapshot selectExtSnapshotById(String id) throws Exception {
        TradeOrderExtSnapshotParamDto paramDto = new TradeOrderExtSnapshotParamDto();
        paramDto.setId(id);
        return tradeOrderExtSnapshotMapper.selectExtSnapshotByParam(paramDto);
    }

    @Override
    public TradeOrderExtSnapshot selectExtSnapshotByParam(TradeOrderExtSnapshotParamDto paramDto) throws Exception {
        return tradeOrderExtSnapshotMapper.selectExtSnapshotByParam(paramDto);
    }

    @Override
    public List<TradeOrderExtSnapshot> selectExtSnapshotListByParam(TradeOrderExtSnapshotParamDto paramDto) throws Exception {
        return tradeOrderExtSnapshotMapper.selectExtSnapshotListByParam(paramDto);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insert(TradeOrderExtSnapshotParamDto paramDto) throws Exception {
        paramDto.setId(UuidUtils.getUuid());
        return tradeOrderExtSnapshotMapper.insert(paramDto);
    }
}
