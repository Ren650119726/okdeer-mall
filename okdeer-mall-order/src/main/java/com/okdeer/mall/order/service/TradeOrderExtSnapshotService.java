package com.okdeer.mall.order.service;

import com.okdeer.mall.order.dto.TradeOrderExtSnapshotParamDto;
import com.okdeer.mall.order.entity.TradeOrderExtSnapshot;

import java.util.List;

/**
 * ClassName: TradeOrderExtSnapshotService
 *
 * @author wangf01
 * @Description: 订单扩展信息快照-service
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public interface TradeOrderExtSnapshotService {

    /**
     * 根据id查询符合的数据
     *
     * @param id String
     * @return TradeOrderExtSnapshot
     */
    TradeOrderExtSnapshot selectExtSnapshotById(String id) throws Exception;

    /**
     * 根据条件查询符合的数据
     *
     * @param paramDto TradeOrderExtSnapshotParamDto 查询数据的条件
     * @return TradeOrderExtSnapshot
     */
    TradeOrderExtSnapshot selectExtSnapshotByParam(TradeOrderExtSnapshotParamDto paramDto) throws Exception;

    /**
     * 根据条件查询符合的数据
     *
     * @param paramDto TradeOrderExtSnapshotParamDto 查询数据的条件
     * @return TradeOrderExtSnapshot
     */
    List<TradeOrderExtSnapshot> selectExtSnapshotListByParam(TradeOrderExtSnapshotParamDto paramDto) throws Exception;

    /**
     * 保存数据
     *
     * @param paramDto TradeOrderExtSnapshotParamDto
     * @return int
     */
    int insert(TradeOrderExtSnapshotParamDto paramDto) throws Exception;
}
