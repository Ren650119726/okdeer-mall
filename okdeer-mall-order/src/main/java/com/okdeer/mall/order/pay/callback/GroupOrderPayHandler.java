package com.okdeer.mall.order.pay.callback;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Lists;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.stock.dto.StockUpdateDto;
import com.okdeer.archive.stock.service.GoodsStoreSkuStockApi;
import com.okdeer.archive.store.service.IStoreInfoServiceExtServiceApi;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.jxc.common.utils.UuidUtils;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountGroup;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountGroupMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.order.bo.GroupOrderRemarkConst;
import com.okdeer.mall.order.bo.TradeOrderGroupParamBo;
import com.okdeer.mall.order.builder.MallStockUpdateBuilder;
import com.okdeer.mall.order.dto.CancelOrderParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderGroup;
import com.okdeer.mall.order.entity.TradeOrderGroupRelation;
import com.okdeer.mall.order.enums.GroupJoinStatusEnum;
import com.okdeer.mall.order.enums.GroupJoinTypeEnum;
import com.okdeer.mall.order.enums.GroupOrderStatusEnum;
import com.okdeer.mall.order.enums.OrderCancelType;
import com.okdeer.mall.order.enums.SendMsgType;
import com.okdeer.mall.order.mapper.TradeOrderGroupMapper;
import com.okdeer.mall.order.mapper.TradeOrderGroupRelationMapper;
import com.okdeer.mall.order.service.CancelOrderApi;
import com.okdeer.mall.order.service.GenerateNumericalService;
import com.okdeer.mall.order.timer.TradeOrderTimer;
import com.okdeer.mall.order.utils.OrderNoUtils;
import com.okdeer.mall.order.vo.SendMsgParamVo;
import com.okdeer.mall.system.utils.ConvertUtil;

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
	private GenerateNumericalService generateNumericalService;

	@Resource
	private ActivityDiscountMapper activityDiscountMapper;
	
	@Resource
	private ActivityDiscountGroupMapper activityDiscountGroupMapper;

	@Resource
	private TradeOrderGroupMapper tradeOrderGroupMapper;

	@Resource
	private MallStockUpdateBuilder mallStockUpdateBuilder;

	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuStockApi goodsStoreSkuStockApi;
	
	@Reference(version = "1.0.0", check = false)
	private IStoreInfoServiceExtServiceApi storeInfoServiceExtServiceApi;

	@Resource
	private CancelOrderApi cancelOrderApi;
	
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;
	
	@Override
	public void postProcessOrder(TradeOrder tradeOrder) throws Exception {
		super.postProcessOrder(tradeOrder);
		// 查询订单团单关联关系
		TradeOrderGroupRelation orderGroupRel = tradeOrderGroupRelationMapper.findByOrderId(tradeOrder.getId());
		if (orderGroupRel == null) {
			// 如果团单关系不存在，标识为开团订单，去开团
			openGroup(tradeOrder, orderGroupRel);
		} else {
			joinGroup(tradeOrder, orderGroupRel);
		}
	}

	@Override
	public void sendNotifyMessage(TradeOrder tradeOrder) throws Exception {

	}

	public void sendNotifyMessage(List<String> orderIdList) {
		try {
			List<TradeOrder> orderList = tradeOrderMapper.findByOrderIds(orderIdList);
			for (TradeOrder tradeOrder : orderList) {
				// 发送消息
				tradeMessageService.sendSmsByCreateOrder(tradeOrder);
				SendMsgParamVo sendMsgParamVo = new SendMsgParamVo(tradeOrder);
				tradeMessageService.sendSellerAppMessage(sendMsgParamVo, SendMsgType.createOrder);
			}
		} catch (Exception e) {
			logger.error("发送通知消息发生异常", e);
		}
	}

	/**
	 * @Description: 开团流程
	 * @param tradeOrder
	 * @param orderGroupRel   
	 * @author maojj
	 * @throws Exception 
	 * @date 2017年10月12日
	 */
	private void openGroup(TradeOrder tradeOrder, TradeOrderGroupRelation orderGroupRel) throws Exception {
		// 查询团购活动信息
		ActivityDiscount activityGroup = activityDiscountMapper.findById(tradeOrder.getActivityId());
		// 查询团购商品信息
		ActivityDiscountGroup groupSku = activityDiscountGroupMapper
				.findByActivityIdAndSkuId(tradeOrder.getActivityId(), tradeOrder.getActivityItemId());
		// 保存团购订单
		TradeOrderGroup orderGroup = saveGroupOrder(tradeOrder, activityGroup, groupSku);
		if (orderGroupRel == null) {
			// 开团订单，生成团单关系
			saveGroupOrderRel(tradeOrder, orderGroup.getId(), GroupJoinTypeEnum.GROUP_OPEN);
		} else {
			// 参团订单开团，修改团单关系
			updateGroupOrderRel(orderGroupRel, orderGroup.getId());
		}
		// 开团成功发送团单过期消息
		sendGroupOutTimeMessage(orderGroup);
	}

	/**
	 * @Description: 保存团购订单
	 * @param tradeOrder
	 * @param activityGroup
	 * @param groupSku
	 * @return   
	 * @author maojj
	 * @date 2017年10月12日
	 */
	private TradeOrderGroup saveGroupOrder(TradeOrder tradeOrder, ActivityDiscount activityGroup,
			ActivityDiscountGroup groupSku) {
		TradeOrderGroup orderGroup = new TradeOrderGroup();
		orderGroup.setId(UuidUtils.getUuid());
		orderGroup.setGroupNo(generateNumericalService.generateOrderNo(OrderNoUtils.GROUP_ORDER_PREFIX));
		orderGroup.setGroupUserId(tradeOrder.getUserId());
		orderGroup.setActivityId(tradeOrder.getActivityId());
		orderGroup.setActivityName(activityGroup.getName());
		orderGroup.setGroupCount(groupSku.getGroupCount());
		orderGroup.setStoreSkuId(tradeOrder.getActivityItemId());
		orderGroup.setGroupPrice(groupSku.getGroupPrice());
		orderGroup.setStoreId(tradeOrder.getStoreId());
		orderGroup.setStatus(GroupOrderStatusEnum.UN_GROUP);
		orderGroup.setCreateTime(new Date());
		// 成团结束时间
		orderGroup.setExpireTime(getExpireTime(activityGroup.getEndTime(), groupSku.getGroupValid()));
		tradeOrderGroupMapper.add(orderGroup);
		return orderGroup;
	}

	/**
	 * @Description: 获取团单过期时间
	 * @param actEndTime
	 * @param groupValid
	 * @return   
	 * @author maojj
	 * @date 2017年10月12日
	 */
	public Date getExpireTime(Date actEndTime, Integer groupValid) {
		// 结束时间
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_YEAR, groupValid);
		return calendar.getTime().before(actEndTime) ? calendar.getTime() : actEndTime;
	}

	/**
	 * @Description: 保存订单团单关联关系
	 * @param tradeOrder
	 * @param groupOrderId
	 * @param joinType   
	 * @author maojj
	 * @date 2017年10月12日
	 */
	private void saveGroupOrderRel(TradeOrder tradeOrder, String groupOrderId, GroupJoinTypeEnum joinType) {
		TradeOrderGroupRelation orderGroupRel = new TradeOrderGroupRelation();
		orderGroupRel.setId(UuidUtils.getUuid());
		orderGroupRel.setGroupOrderId(groupOrderId);
		orderGroupRel.setOrderId(tradeOrder.getId());
		orderGroupRel.setUserId(tradeOrder.getUserId());
		orderGroupRel.setType(joinType);
		orderGroupRel.setStatus(GroupJoinStatusEnum.JOIN_SUCCESS);
		tradeOrderGroupRelationMapper.add(orderGroupRel);
	}

	/**
	 * @Description: 更新团单订单关联关系
	 * @param orderGroupRel
	 * @param groupOrderId   
	 * @author maojj
	 * @date 2017年10月12日
	 */
	private void updateGroupOrderRel(TradeOrderGroupRelation orderGroupRel, String groupOrderId) {
		orderGroupRel.setGroupOrderIdHis(orderGroupRel.getGroupOrderId());
		orderGroupRel.setGroupOrderId(groupOrderId);
		// 订单身份由拼团转为开团
		orderGroupRel.setType(GroupJoinTypeEnum.GROUP_OPEN);
		// 关系状态由待入团改为已入团
		orderGroupRel.setStatus(GroupJoinStatusEnum.JOIN_SUCCESS);
		tradeOrderGroupRelationMapper.update(orderGroupRel);
	}

	/**
	 * @Description: 修改团单订单关联状态
	 * @param orderGroupRel
	 * @param status   
	 * @author maojj
	 * @date 2017年10月12日
	 */
	private void updateGroupOrderRel(TradeOrderGroupRelation orderGroupRel, GroupJoinStatusEnum status) {
		orderGroupRel.setStatus(status);
		tradeOrderGroupRelationMapper.update(orderGroupRel);
	}
	
	/**
	 * @Description: 发送团购订单拼团超时消息
	 * @param orderGroup
	 * @throws Exception   
	 * @author maojj
	 * @date 2017年10月17日
	 */
	private void sendGroupOutTimeMessage(TradeOrderGroup orderGroup) throws Exception {
		Date expireTime = orderGroup.getExpireTime();
		long delayTimeMillis = (expireTime.getTime() - System.currentTimeMillis())/1000;
		delayTimeMillis = delayTimeMillis > 0L ? delayTimeMillis : 0L;
		tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_group_timeout, orderGroup.getId(), delayTimeMillis);
	}

	/**
	 * @Description: 入团
	 * @param tradeOrder
	 * @param orderGroupRel   
	 * @author maojj
	 * @throws Exception 
	 * @date 2017年10月12日
	 */
	private void joinGroup(TradeOrder tradeOrder, TradeOrderGroupRelation orderGroupRel) throws Exception {
		// 查询关联的团购订单
		TradeOrderGroup orderGroup = tradeOrderGroupMapper.findById(orderGroupRel.getGroupOrderId());
		if (orderGroup.getStatus() != GroupOrderStatusEnum.UN_GROUP) {
			// 如果团购订单不是未成团状态，则不可入团，给用户重新开团
			openGroup(tradeOrder, orderGroupRel);
			return;
		}
		// 如果可入团，修改订单团单关系为已入团
		updateGroupOrderRel(orderGroupRel, GroupJoinStatusEnum.JOIN_SUCCESS);
		// 查询团单总数
		int successJoinNum = tradeOrderGroupRelationMapper.countSuccessJoinNum(orderGroupRel.getGroupOrderId());
		// 查询团购商品活动信息
		ActivityDiscountGroup groupSku = activityDiscountGroupMapper
				.findByActivityIdAndSkuId(tradeOrder.getActivityId(), tradeOrder.getActivityItemId());
		// 判断是否满足成团数量
		if (successJoinNum == groupSku.getGroupCount()) {
			// 判断是否可成团
			if (isGroupSuccess(orderGroup, groupSku, successJoinNum)) {
				groupSuccess(orderGroup,groupSku);
			} else {
				List<TradeOrderGroupRelation> orderGroupRelList = tradeOrderGroupRelationMapper
						.findByGroupOrderId(orderGroup.getId());
				List<String> orderIdList = orderGroupRelList.stream().map(e -> e.getOrderId())
						.collect(Collectors.toList());
				// 如果不可成团，对所有团单走取消流程
				cancelGroupOrder(orderGroup, orderIdList);
			}

		}
	}

	/**
	 * @Description: 是否可以成团
	 * @param orderGroup
	 * @param groupSku
	 * @param successJoinNum
	 * @return   
	 * @author maojj
	 * @date 2017年10月12日
	 */
	private boolean isGroupSuccess(TradeOrderGroup orderGroup, ActivityDiscountGroup groupSku, int successJoinNum) {
		Date currentDate = new Date();
		// 满足成团数量，检查成团商品限制
		if (groupSku.getGoodsDayCountLimit() > 0) {
			// 商品每日限售数为0标识不限制，>0有限制。
			// 查询当天活动成团总数
			TradeOrderGroupParamBo paramBo = new TradeOrderGroupParamBo();
			paramBo.setActivityId(orderGroup.getActivityId());
			paramBo.setStoreSkuId(paramBo.getStoreSkuId());
			paramBo.setStatus(GroupOrderStatusEnum.GROUP_SUCCESS);
			paramBo.setGroupTimeStart(DateUtils.getDateStart(currentDate));
			paramBo.setGroupTimeEnd(DateUtils.getDateEnd(currentDate));

			int soldDayNum = tradeOrderGroupMapper.countGroupSkuNum(paramBo);
			if (soldDayNum + orderGroup.getGroupCount() > groupSku.getGoodsDayCountLimit().intValue()) {
				orderGroup.setRemark(GroupOrderRemarkConst.GROUP_FAIL_OUT_DAY_LIMIT);
				return false;
			}
		}
		// 检查总的出售数量
		if (groupSku.getGoodsCountLimit() > 0) {
			TradeOrderGroupParamBo paramBo = new TradeOrderGroupParamBo();
			paramBo.setActivityId(orderGroup.getActivityId());
			paramBo.setStoreSkuId(paramBo.getStoreSkuId());
			paramBo.setStatus(GroupOrderStatusEnum.GROUP_SUCCESS);

			int soldTotal = tradeOrderGroupMapper.countGroupSkuNum(paramBo);
			if (soldTotal + orderGroup.getGroupCount() > groupSku.getGoodsCountLimit().intValue()) {
				orderGroup.setRemark(GroupOrderRemarkConst.GROUP_FAIL_OUT_TOTAL_LIMIT);
				return false;
			}
		}
		return true;
	}

	/**
	 * @Description: 成团处理流程
	 * @param orderGroup   
	 * @author maojj
	 * @date 2017年10月12日
	 */
	private void groupSuccess(TradeOrderGroup orderGroup, ActivityDiscountGroup groupSku) {
		Date currentDate = new Date();
		// 修改团单状态为已成团
		orderGroup.setStatus(GroupOrderStatusEnum.GROUP_SUCCESS);
		orderGroup.setEndTime(currentDate);
		tradeOrderGroupMapper.update(orderGroup);
		// 修改所有关联的订单类型为已寄送订单
		// 查询已入团的团单关联关系
		List<TradeOrderGroupRelation> orderGroupRelList = tradeOrderGroupRelationMapper
				.findByGroupOrderId(orderGroup.getId());
		List<String> orderIdList = orderGroupRelList.stream().map(e -> e.getOrderId()).collect(Collectors.toList());
		// 更新库存
		try {
			StockUpdateDto mallStockUpdate = mallStockUpdateBuilder.buildForGroupOrder(orderGroup,
					groupSku.getGoodsCountLimit().compareTo(Integer.valueOf(0)) > 0);
			goodsStoreSkuStockApi.updateStock(mallStockUpdate);
			// 增加商品销量
			GoodsStoreSku goodsStoreSku = goodsStoreSkuServiceApi.getById(groupSku.getStoreSkuId());
			if (goodsStoreSku != null) {
				goodsStoreSku.setSaleNum(ConvertUtil.format(goodsStoreSku.getSaleNum()) + orderGroup.getGroupCount());
				goodsStoreSkuServiceApi.updateByPrimaryKeySelective(goodsStoreSku);
			}
		} catch (Exception e) {
			logger.error("团购订单更新库存失败", e);
			// 更新库存失败，走取消流程
			orderGroup.setRemark(GroupOrderRemarkConst.GROUP_FAIL_STOCK_NOT_ENOUGH);
			cancelGroupOrder(orderGroup, orderIdList);
			return;
		}
		// 修改所有订单类型为寄送服务订单
		tradeOrderMapper.updateOrderType(orderIdList,currentDate);
		// 成团成功，发送通知消息
		sendNotifyMessage(orderIdList);
		// 成团成功，发送订单待发货超时消息
		sendTimerMessage(orderIdList);
	}

	/**
	 * @Description: 取消团单
	 * @param orderGroup
	 * @param orderIdList   
	 * @author maojj
	 * @date 2017年10月12日
	 */
	private void cancelGroupOrder(TradeOrderGroup orderGroup, List<String> orderIdList) {
		// 修改团单状态为成团失败
		orderGroup.setStatus(GroupOrderStatusEnum.GROUP_FAIL);
		orderGroup.setEndTime(new Date());
		if(StringUtils.isEmpty(orderGroup.getRemark())){
			orderGroup.setRemark(GroupOrderRemarkConst.GROUP_FAIL);
		}
		tradeOrderGroupMapper.update(orderGroup);
		// 对所有入团订单走订单取消流程
		List<CancelOrderParamDto> cancelOrderList = Lists.newArrayList();
		orderIdList.forEach(orderId -> {
			CancelOrderParamDto cancelParamDto = new CancelOrderParamDto();
			cancelParamDto.setOrderId(orderId);
			cancelParamDto.setReason("成团失败");
			cancelParamDto.setCancelType(OrderCancelType.CANCEL_BY_SYSTEM);
			cancelOrderList.add(cancelParamDto);
		});
		cancelOrderList.forEach(cancelOrder -> cancelOrderApi.cancelOrder(cancelOrder));
	}

	public void sendTimerMessage(List<String> orderIdList) {
		try {
			for (String orderId : orderIdList) {
				tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_delivery_group_timeout, orderId);
			}
		} catch (Exception e) {
			logger.error("发送团购订单待发货超时消息异常", e);
		}

	}
}
