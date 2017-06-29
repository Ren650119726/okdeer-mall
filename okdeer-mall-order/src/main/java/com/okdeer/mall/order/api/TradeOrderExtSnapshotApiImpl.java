package com.okdeer.mall.order.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.order.dto.TradeOrderExtSnapshotParamDto;
import com.okdeer.mall.order.entity.TradeOrderExtSnapshot;
import com.okdeer.mall.order.service.TradeOrderExtSnapshotApi;
import com.okdeer.mall.order.service.TradeOrderExtSnapshotService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * ClassName: TradeOrderExtSnapshotApi
 *
 * @author wangf01
 * @Description: 订单扩展快照信息-api-impl
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderExtSnapshotApi")
public class TradeOrderExtSnapshotApiImpl implements TradeOrderExtSnapshotApi {

    /**
     * 注入-service
     */
    @Autowired
    private TradeOrderExtSnapshotService tradeOrderExtSnapshotService;


    @Override
    public TradeOrderExtSnapshot selectExtSnapshotById(String id) throws Exception {
        return tradeOrderExtSnapshotService.selectExtSnapshotById(id);
    }

    @Override
    public TradeOrderExtSnapshot selectExtSnapshotByParam(TradeOrderExtSnapshotParamDto paramDto) throws Exception {
        return tradeOrderExtSnapshotService.selectExtSnapshotByParam(paramDto);
    }

    @Override
    public List<TradeOrderExtSnapshot> selectExtSnapshotListByParam(TradeOrderExtSnapshotParamDto paramDto) throws Exception {
        return tradeOrderExtSnapshotService.selectExtSnapshotListByParam(paramDto);
    }
}
