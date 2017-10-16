package com.okdeer.mall.order.service;

import java.util.List;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.order.entity.TradeOrderGroupRelation;

public interface TradeOrderGroupRelationService extends IBaseService {
	
	/**
	 * @Description: 查询已成功入团的团单关联关系
	 * @param groupOrderId
	 * @return   
	 * @author maojj
	 * @date 2017年10月12日
	 */
	List<TradeOrderGroupRelation> findByGroupOrderId(String groupOrderId);
	
	/**
	 * @Description:根据团单id统计成功入团的总数 
	 * @param groupOrderId
	 * @return   
	 * @author maojj
	 * @date 2017年10月12日
	 */
	int countSuccessJoinNum(String groupOrderId);

}
