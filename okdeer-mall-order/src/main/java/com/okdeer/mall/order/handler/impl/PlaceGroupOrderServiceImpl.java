package com.okdeer.mall.order.handler.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.jxc.common.utils.UuidUtils;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.mapper.ActivityJoinRecordMapper;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.member.bo.UserAddressFilterCondition;
import com.okdeer.mall.member.mapper.MemberConsigneeAddressMapper;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.member.service.impl.GroupUserAddrFilterStrategy;
import com.okdeer.mall.order.bo.TradeOrderContext;
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
import com.okdeer.mall.order.service.TradeOrderChangeListeners;
import com.okdeer.mall.order.service.TradeOrderLogService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.timer.TradeOrderTimer;
import com.okdeer.mall.system.utils.ConvertUtil;

@Service("placeGroupOrderService")
public class PlaceGroupOrderServiceImpl implements RequestHandler<PlaceOrderParamDto, PlaceOrderDto> {
	
	private static final Logger logger = LoggerFactory.getLogger(PlaceGroupOrderServiceImpl.class);
	
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
	
	@Resource
	private MemberConsigneeAddressMapper memberConsigneeAddressMapper;
	
	@Resource
	private GroupUserAddrFilterStrategy groupUserAddrFilterStrategy;

	@Autowired
	private TradeOrderChangeListeners tradeOrderChangeListeners;
	
	@Override
	@Transactional
	public void process(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		PlaceOrderParamDto paramDto = req.getData();
		// 根据请求构建订单
		TradeOrder tradeOrder = tradeOrderBoBuilder.build(paramDto);
		MemberConsigneeAddress userUseAddr = (MemberConsigneeAddress) paramDto.get("userUseAddr");
		// 检查用户地址
		if(!checkUserAddr(userUseAddr, paramDto, resp)){
			return;
		}
		// 保存订单
		tradeOrderSerive.insertTradeOrder(tradeOrder);
		// 保存订单日志
		tradeOrderLogService.insertSelective(new TradeOrderLog(tradeOrder.getId(), tradeOrder.getUserId(),
				tradeOrder.getStatus().getName(), tradeOrder.getStatus().getValue()));
		// 保存活动参与记录
		saveActivityJoinRec(paramDto, tradeOrder.getId());
		// 保存订单团单关联关系
		saveGroupOrderRel(paramDto, tradeOrder.getId());
		// 更新用户最后使用的地址
		updateLastUseAddr(userUseAddr);
		//begin add by zengjz 2017-10-25 添加创建订单监听
		TradeOrderContext tradeOrderContext = new TradeOrderContext();
		tradeOrderContext.setTradeOrder(tradeOrder);
		tradeOrderContext.setTradeOrderLogistics(tradeOrder.getTradeOrderLogistics());
		tradeOrderContext.setItemList(tradeOrder.getTradeOrderItem());
		tradeOrderChangeListeners.tradeOrderCreated(tradeOrderContext);
		//end add by zengjz 2017-10-25
		
		// 发送订单超时未支付的消息
		tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_pay_timeout, tradeOrder.getId());
		
		resp.getData().setOrderId(tradeOrder.getId());
		resp.getData().setOrderNo(tradeOrder.getOrderNo());
		resp.getData().setOrderPrice(ConvertUtil.format(tradeOrder.getActualAmount()));
		resp.getData().setTradeNum(tradeOrder.getTradeNum());
		// 订单倒计时
		resp.getData().setLimitTime(60 * 30);
	}
	
	private boolean checkUserAddr(MemberConsigneeAddress userUseAddr,PlaceOrderParamDto paramDto,Response<PlaceOrderDto> resp){
		if (userUseAddr == null) {
			resp.setResult(ResultCodeEnum.ADDRESS_NOT_EXSITS);
			return false;
		}
		UserAddressFilterCondition filterCondition = new UserAddressFilterCondition();
		ActivityDiscount actInfo = (ActivityDiscount) paramDto.get("activityGroup");
		filterCondition.setActivityInfo(actInfo);
		filterCondition.setActivityId(actInfo.getId());
		if (groupUserAddrFilterStrategy.isOutRange(userUseAddr, filterCondition)){
			logger.warn("团购订单使用的用户地址{}超出服务范围",userUseAddr.getId());
			resp.setResult(ResultCodeEnum.ILLEGAL_PARAM);
			return false;
		}
		return true;
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
	
	/**
	 * @Description: 更新最后一次用户使用的地址
	 * @param userUseAddr   
	 * @author maojj
	 * @date 2017年3月15日
	 */
	public void updateLastUseAddr(MemberConsigneeAddress userUseAddr) {
		userUseAddr.setUseTime(DateUtils.getSysDate());
		memberConsigneeAddressMapper.updateByPrimaryKeySelective(userUseAddr);
	}
}
