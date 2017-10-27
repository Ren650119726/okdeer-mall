package com.okdeer.mall.order.pay.callback;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderGroupRelation;
import com.okdeer.mall.order.mapper.TradeOrderGroupRelationMapper;
import com.okdeer.mall.order.service.TradeOrderGroupService;
import com.okdeer.mall.util.RedisLock;

/**
 * ClassName: GroupOrderPayHandler 
 * @Description: 团购订单支付回调
 * @author maojj
 * @date 2017年10月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.6.3 		2017年10月11日				maojj
 */
@Service("groupOrderPayHandler")
public class GroupOrderPayHandler extends AbstractPayResultHandler {

	@Resource
	private TradeOrderGroupRelationMapper tradeOrderGroupRelationMapper;
	
	@Resource
	private TradeOrderGroupService tradeOrderGroupService;
	
	@Resource
	private RedisLock redisLock;

	@Override
	public void postProcessOrder(TradeOrder tradeOrder) throws Exception {
		super.postProcessOrder(tradeOrder);
		// 查询订单团单关联关系
		TradeOrderGroupRelation orderGroupRel = tradeOrderGroupRelationMapper.findByOrderId(tradeOrder.getId());
		if (orderGroupRel == null) {
			// 如果团单关系不存在，标识为开团订单，去开团
			tradeOrderGroupService.openGroup(tradeOrder, orderGroupRel);
		} else {
			// 如果存在，标识为参团订单，走入团流程。对团购订单加锁执行
			String lockKey = String.format("GROUP:%s", orderGroupRel.getGroupOrderId());
			try {
				if (redisLock.tryLock(lockKey, 10)) {
					tradeOrderGroupService.joinGroup(tradeOrder, orderGroupRel);
				}
			} finally {
				redisLock.unLock(lockKey);
			}

		}
	}

	@Override
	public void sendNotifyMessage(TradeOrder tradeOrder) throws Exception {

	}
}
