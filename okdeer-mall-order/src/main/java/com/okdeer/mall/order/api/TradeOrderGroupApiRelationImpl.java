package com.okdeer.mall.order.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.order.dto.TradeOrderGroupRelationDto;
import com.okdeer.mall.order.service.TradeOrderGroupRelationApi;
import com.okdeer.mall.order.service.TradeOrderGroupRelationService;

/**
 * ClassName: TradeOrderCarrierApiImpl
 *
 * @author zhangkn
 * @Description: 团购订单api
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderGroupRelationApi")
public class TradeOrderGroupApiRelationImpl implements TradeOrderGroupRelationApi {

    /**
     * 注入-service
     */
    @Autowired
    private TradeOrderGroupRelationService tradeOrderGroupRelationService;

	@Override
	public List<TradeOrderGroupRelationDto> findByGroupOrderId(String groupOrderId) {
		return BeanMapper.mapList(tradeOrderGroupRelationService.findByGroupOrderId(groupOrderId), 
				TradeOrderGroupRelationDto.class);
	}

	@Override
	public int countSuccessJoinNum(String groupOrderId) {
		return tradeOrderGroupRelationService.countSuccessJoinNum(groupOrderId);
	}
}
