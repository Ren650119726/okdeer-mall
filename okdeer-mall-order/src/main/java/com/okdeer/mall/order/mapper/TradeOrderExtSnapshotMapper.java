package com.okdeer.mall.order.mapper;

import com.okdeer.mall.order.dto.TradeOrderExtSnapshotParamDto;
import com.okdeer.mall.order.entity.TradeOrderExtSnapshot;

import java.util.List;

/**
 * ClassName: TradeOrderExtSnapshotMapper
 *
 * @author wangf01
 * @Description: 订单扩展信息快照
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public interface TradeOrderExtSnapshotMapper {

    TradeOrderExtSnapshot selectExtSnapshotByParam(TradeOrderExtSnapshotParamDto paramDto);

    List<TradeOrderExtSnapshot> selectExtSnapshotListByParam(TradeOrderExtSnapshotParamDto paramDto);

    int insert(TradeOrderExtSnapshotParamDto paramDto);

    int update(TradeOrderExtSnapshotParamDto paramDto);
}
