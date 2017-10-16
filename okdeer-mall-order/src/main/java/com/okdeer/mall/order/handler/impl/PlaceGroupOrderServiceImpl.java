package com.okdeer.mall.order.handler.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.okdeer.base.common.enums.Disabled;
import com.okdeer.jxc.common.utils.UuidUtils;
import com.okdeer.mall.activity.discount.mapper.ActivityJoinRecordMapper;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.builder.TradeOrderBuilder;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.entity.ActivityJoinRecord;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderGroupRelation;
import com.okdeer.mall.order.entity.TradeOrderLog;
import com.okdeer.mall.order.enums.GroupJoinStatusEnum;
import com.okdeer.mall.order.enums.GroupJoinTypeEnum;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.mapper.TradeOrderGroupRelationMapper;
import com.okdeer.mall.order.service.TradeOrderLogService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.timer.TradeOrderTimer;
import com.okdeer.mall.system.utils.ConvertUtil;

@Service("placeGroupOrderService")
public class PlaceGroupOrderServiceImpl implements RequestHandler<PlaceOrderParamDto, PlaceOrderDto> {
	
	@Autowired
	private TradeOrderBuilder tradeOrderBoBuilder;
	
	@Resource
	private TradeOrderService tradeOrderSerive;
	
	@Resource
	private TradeOrderLogService tradeOrderLogService;
	
	@Resource
	private ActivityJoinRecordMapper activityJoinRecordMapper;
	
	@Resource
	private TradeOrderGroupRelationMapper tradeOrderGroupRelationMapper;
	
	@Resource
	private TradeOrderTimer tradeOrderTimer;

	@Override
	@Transactional
	public void process(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		PlaceOrderParamDto paramDto = req.getData();
		// 根据请求构建订单
		TradeOrder tradeOrder = tradeOrderBoBuilder.build(paramDto);
		// 保存订单
		tradeOrderSerive.insertTradeOrder(tradeOrder);
		// 保存订单日志
		tradeOrderLogService.insertSelective(new TradeOrderLog(tradeOrder.getId(), tradeOrder.getUserId(),
				tradeOrder.getStatus().getName(), tradeOrder.getStatus().getValue()));
		// 保存活动参与记录
		saveActivityJoinRec(paramDto, tradeOrder.getId());
		// 保存订单团单关联关系
		saveGroupOrderRel(paramDto, tradeOrder.getId());
		// 发送订单超时未支付的消息
		tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_pay_timeout, tradeOrder.getId());
		
		resp.getData().setOrderId(tradeOrder.getId());
		resp.getData().setOrderNo(tradeOrder.getOrderNo());
		resp.getData().setOrderPrice(ConvertUtil.format(tradeOrder.getActualAmount()));
		resp.getData().setTradeNum(tradeOrder.getTradeNum());
		// 订单倒计时
		resp.getData().setLimitTime(60 * 30);
	}

	/**
	 * @Description: 保存团购活动参与记录
	 * @param paramDto
	 * @param orderId   
	 * @author maojj
	 * @date 2017年10月11日
	 */
	private void saveActivityJoinRec(PlaceOrderParamDto paramDto,String orderId){
		ActivityJoinRecord activityJoinRec = new ActivityJoinRecord();
		activityJoinRec.setId(UuidUtils.getUuid());
		activityJoinRec.setActivityType(paramDto.getActivityType());
		activityJoinRec.setActivityId(paramDto.getActivityId());
		activityJoinRec.setActivityItemId(paramDto.getActivityItemId());
		activityJoinRec.setActivityNum(1);
		activityJoinRec.setUserId(paramDto.getUserId());
		activityJoinRec.setOrderId(orderId);
		activityJoinRec.setDeviceId(paramDto.getDeviceId());
		activityJoinRec.setCreateDate(new Date());
		activityJoinRec.setDisabled(Disabled.valid);
		activityJoinRecordMapper.add(activityJoinRec);
	}
	
	/**
	 * @Description: 保存订单团单关系
	 * @param paramDto
	 * @param orderId   
	 * @author maojj
	 * @date 2017年10月11日
	 */
	private void saveGroupOrderRel(PlaceOrderParamDto paramDto,String orderId){
		if(paramDto.getGroupJoinType() == GroupJoinTypeEnum.GROUP_OPEN){
			// 如果是开团，关系再支付之后才存在
			return;
		}
		TradeOrderGroupRelation orderGroupRel = new TradeOrderGroupRelation();
		orderGroupRel.setId(UuidUtils.getUuid());
		orderGroupRel.setGroupOrderId(paramDto.getGroupOrderId());
		orderGroupRel.setOrderId(orderId);
		orderGroupRel.setUserId(paramDto.getUserId());
		orderGroupRel.setType(GroupJoinTypeEnum.GROUP_JOIN);
		orderGroupRel.setStatus(GroupJoinStatusEnum.WAIT_JOIN);
		tradeOrderGroupRelationMapper.add(orderGroupRel);
	}
}
