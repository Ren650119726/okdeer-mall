
package com.okdeer.mall.order.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuService;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceServiceApi;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.service.IStoreInfoExtServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.common.utils.DateUtils;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.member.member.enums.AddressDefault;
import com.okdeer.mall.member.member.service.MemberConsigneeAddressServiceApi;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.enums.ConsumerCodeStatusEnum;
import com.okdeer.mall.order.enums.OrderAppStatusAdaptor;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.mapper.TradeOrderItemDetailMapper;
import com.okdeer.mall.order.mapper.TradeOrderItemMapper;
import com.okdeer.mall.order.mapper.TradeOrderMapper;
import com.okdeer.mall.order.service.StoreConsumeOrderServiceApi;
import com.okdeer.mall.order.vo.UserTradeOrderDetailVo;

/**
 * ClassName: ConsumerCodeOrderServiceImpl 
 * @Description: 到店消费接口实现类
 * @author zengjizu
 * @date 2016年9月20日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     v1.1.0            2016-9-20          zengjz			  增加查询消费码订单列表
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.StoreConsumeOrderServiceApi")
public class StoreConsumeOrderServiceImpl implements StoreConsumeOrderServiceApi {

	@Autowired
	private TradeOrderMapper tradeOrderMapper;

	@Autowired
	private TradeOrderItemMapper tradeOrderItemMapper;

	@Autowired
	private TradeOrderItemDetailMapper tradeOrderItemDetailMapper;

	@Reference(version = "1.0.0", check = false)
	private IStoreInfoExtServiceApi storeInfoExtService;

	@Reference(version = "1.0.0", check = false)
	private MemberConsigneeAddressServiceApi memberConsigneeAddressService;

	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceServiceApi goodsStoreSkuServiceServiceApi;

	@Override
	public PageUtils<TradeOrder> findStoreConsumeOrderList(Map<String, Object> map, Integer pageNo, Integer pageSize) {
		PageHelper.startPage(pageNo, pageSize, true, false);

		String status = (String) map.get("status");
		List<TradeOrder> list = new ArrayList<TradeOrder>();

		if (status != null) {
			// 订单状态
			List<String> orderStatus = new ArrayList<String>();

			if (status.equals(String.valueOf(Constant.ONE))) {
				// 查询未支付的消费码订单
				orderStatus.add(String.valueOf(OrderStatusEnum.UNPAID.ordinal()));
				orderStatus.add(String.valueOf(OrderStatusEnum.BUYER_PAYING.ordinal()));
				map.put("orderStatus", orderStatus);
			} else if (status.equals(String.valueOf(Constant.TWO))) {
				// 查询已经取消的消费码订单
				orderStatus.add(String.valueOf(OrderStatusEnum.CANCELED.ordinal()));
				orderStatus.add(String.valueOf(OrderStatusEnum.CANCELING.ordinal()));
				map.put("orderStatus", orderStatus);
			} else if (status.equals(String.valueOf(Constant.THIRTH))) {
				// 查询未消费的消费码订单
				map.put("orderStatus", OrderStatusEnum.HAS_BEEN_SIGNED.ordinal());
				map.put("consumerCodeStatus", ConsumerCodeStatusEnum.WAIT_CONSUME.ordinal());
			} else if (status.equals(String.valueOf(Constant.FOUR))) {
				// 查询已过期的消费码订单
				map.put("orderStatus", OrderStatusEnum.HAS_BEEN_SIGNED.ordinal());
				map.put("consumerCodeStatus", ConsumerCodeStatusEnum.EXPIRED.ordinal());
			} else if (status.equals(String.valueOf(Constant.FIVE))) {
				// 查询已消费的消费码订单
				map.put("orderStatus", OrderStatusEnum.HAS_BEEN_SIGNED.ordinal());
				map.put("consumerCodeStatus", ConsumerCodeStatusEnum.WAIT_EVALUATE.ordinal());
			} else if (status.equals(String.valueOf(Constant.SIX))) {
				// 查询已退款的消费码订单
				map.put("orderStatus", OrderStatusEnum.HAS_BEEN_SIGNED.ordinal());
				map.put("consumerCodeStatus", ConsumerCodeStatusEnum.REFUNDED.ordinal());
			} else if (status.equals(String.valueOf(Constant.SEVEN))) {
				// 查询已完成的消费码订单
				map.put("orderStatus", OrderStatusEnum.HAS_BEEN_SIGNED.ordinal());
				map.put("consumerCodeStatus", ConsumerCodeStatusEnum.COMPLETED.ordinal());
			}

			list = tradeOrderMapper.selectStoreConsumeOrderList(map);
		}

		for (TradeOrder vo : list) {
			List<TradeOrderItem> items = tradeOrderItemMapper.selectTradeOrderItem(vo.getId());
			vo.setTradeOrderItem(items);
		}
		return new PageUtils<TradeOrder>(list);
	}

	@Override
	public JSONObject findStoreConsumeOrderDetail(String orderId) throws Exception {

		UserTradeOrderDetailVo userTradeOrderDetailVo = tradeOrderMapper.findStoreConsumeOrderById(orderId);

		List<TradeOrderItem> tradeOrderItems = tradeOrderItemMapper.selectTradeOrderItemOrRefund(orderId);
		// 判断订单是否评价appraise大于0，已评价
		Integer appraise = tradeOrderItemMapper.selectTradeOrderItemIsAppraise(orderId);
		// 查询店铺扩展信息
		JSONObject json = new JSONObject();
		try {
			// StoreInfoExt storeInfoExt = storeInfoExtService.getByStoreId(storeId);

			// 1 订单信息
			json.put("orderId", userTradeOrderDetailVo.getId() == null ? "" : userTradeOrderDetailVo.getId());
			json.put("orderStatus", OrderAppStatusAdaptor.convertAppStoreConsumeOrderStatus(
					userTradeOrderDetailVo.getStatus(), userTradeOrderDetailVo.getConsumerCodeStatus()));

			// 2 订单支付倒计时计算
			Integer remainingTime = userTradeOrderDetailVo.getRemainingTime();
			if (remainingTime != null) {
				remainingTime = remainingTime + 1800;
				json.put("remainingTime", remainingTime <= 0 ? "0" : remainingTime);
			} else {
				json.put("remainingTime", "0");
			}

			// 支付信息
			TradeOrderPay payInfo = userTradeOrderDetailVo.getTradeOrderPay();

			if (payInfo != null) {
				// 0:余额支付 1:支付宝 2:微信支付
				json.put("payMethod", payInfo.getPayType());
				json.put("orderPayTime", DateUtils.formatDate(payInfo.getPayTime(), "yyyy-MM-dd HH:mm:ss"));
				// 是否支持投诉 0：支持 1:不支持
				json.put("isSupportComplain", 0);
			} else {
				json.put("isSupportComplain", 1);
			}

			// 交易号
			json.put("tradeNum",
					userTradeOrderDetailVo.getTradeNum() == null ? "" : userTradeOrderDetailVo.getTradeNum());
			json.put("remark", userTradeOrderDetailVo.getRemark() == null ? "" : userTradeOrderDetailVo.getRemark());
			json.put("orderAmount",
					userTradeOrderDetailVo.getTotalAmount() == null ? "0" : userTradeOrderDetailVo.getTotalAmount());
			json.put("actualAmount",
					userTradeOrderDetailVo.getActualAmount() == null ? "0" : userTradeOrderDetailVo.getActualAmount());
			json.put("orderNo", userTradeOrderDetailVo.getOrderNo() == null ? "" : userTradeOrderDetailVo.getOrderNo());
			json.put("cancelReason",
					userTradeOrderDetailVo.getReason() == null ? "" : userTradeOrderDetailVo.getReason());
			json.put(
					"orderSubmitOrderTime",
					userTradeOrderDetailVo.getCreateTime() != null ? DateUtils.formatDate(
							userTradeOrderDetailVo.getCreateTime(), "yyyy-MM-dd HH:mm:ss") : "");

			json.put("activityType", userTradeOrderDetailVo.getActivityType() == null ? "" : userTradeOrderDetailVo
					.getActivityType().ordinal());
			json.put("preferentialPrice", userTradeOrderDetailVo.getPreferentialPrice() == null ? ""
					: userTradeOrderDetailVo.getPreferentialPrice());
			// 订单评价类型0：未评价，1：已评价
			json.put("orderIsComment", appraise > 0 ? Constant.ONE : Constant.ZERO);
			// 订单投诉状态
			json.put("compainStatus", userTradeOrderDetailVo.getCompainStatus() == null ? "" : userTradeOrderDetailVo
					.getCompainStatus().ordinal());

			json.put("leaveMessage", userTradeOrderDetailVo.getRemark());

			// 店铺信息
			getStoreInfo(json, userTradeOrderDetailVo);
			// 商品信息
			getTradeItemInfo(json, tradeOrderItems);
			//订单明细信息
			getTradeOrderItemDetail(json, userTradeOrderDetailVo, tradeOrderItems.get(0), orderId);
		} catch (ServiceException e) {
			throw new RuntimeException("查询店铺信息出错");
		}
		return json;
	}
	
	/**
	 * @Description: 获取店铺信息
	 * @param json 返回的json对象
	 * @param userTradeOrderDetailVo 订单信息
	 * @throws ServiceException 抛出异常
	 * @author zengjizu
	 * @date 2016年9月24日
	 */
	private void getStoreInfo(JSONObject json,UserTradeOrderDetailVo userTradeOrderDetailVo) throws ServiceException{
		StoreInfo storeInfo = userTradeOrderDetailVo.getStoreInfo();
		String storeName = "";
		String storeMobile = "";
		String address = "";
		String storeId ="";
		if (storeInfo != null) {
			storeId = storeInfo.getId();
			storeName = storeInfo.getStoreName();
			storeMobile = storeInfo.getMobile();

			// 确认订单时，没有将地址保存到trade_order_logistics订单物流表，暂时取收货地址表的默认地址
			MemberConsigneeAddress memberConsigneeAddress = new MemberConsigneeAddress();
			memberConsigneeAddress.setUserId(storeId);
			memberConsigneeAddress.setIsDefault(AddressDefault.YES);

			List<MemberConsigneeAddress> memberAddressList = memberConsigneeAddressService
					.getList(memberConsigneeAddress);
			if (memberAddressList != null && memberAddressList.size() > 0) {
				MemberConsigneeAddress memberAddress = memberAddressList.get(0);
				address = memberAddress.getArea() + memberAddress.getAddress();
			}
		}

		json.put("orderShopid", storeId);
		json.put("orderShopName", storeName);
		json.put("orderShopMobile", storeMobile);
		json.put("orderExtractShopName", storeName);
		json.put("orderShopAddress", address);
		json.put("storeLogo", storeInfo.getLogoUrl());
		
		
	}
	
	/**
	 * @Description: 获取订单项信息
	 * @param json 返回json对象
	 * @param tradeOrderItems 订单项
	 * @author zengjizu
	 * @date 2016年9月24日
	 */
	private void getTradeItemInfo(JSONObject json,List<TradeOrderItem> tradeOrderItems){
		TradeOrderItem tradeOrderItem = tradeOrderItems.get(0);

		if (tradeOrderItem != null) {

			json.put("productId", tradeOrderItem.getStoreSkuId() == null ? "" : tradeOrderItem.getStoreSkuId());
			json.put("mainPicPrl", tradeOrderItem.getMainPicPrl() == null ? "" : tradeOrderItem.getMainPicPrl());
			json.put("skuName", tradeOrderItem.getSkuName() == null ? "" : tradeOrderItem.getSkuName());
			json.put("unitPrice", tradeOrderItem.getUnitPrice() == null ? "0" : tradeOrderItem.getUnitPrice());
			json.put("quantity", tradeOrderItem.getQuantity() == null ? "" : tradeOrderItem.getQuantity());
			json.put("skuTotalAmount",
					tradeOrderItem.getTotalAmount() == null ? "" : tradeOrderItem.getTotalAmount());
			json.put("skuActualAmount",
					tradeOrderItem.getActualAmount() == null ? "" : tradeOrderItem.getActualAmount());
			json.put("preferentialPrice",
					tradeOrderItem.getPreferentialPrice() == null ? "0" : tradeOrderItem.getPreferentialPrice());

			GoodsStoreSkuService goodsStoreSkuService = goodsStoreSkuServiceServiceApi
					.selectByStoreSkuId(tradeOrderItem.getStoreSkuId());

			// 是否需要预约0：不需要，1：需要
			json.put("isPrecontract", goodsStoreSkuService.getIsAppointment());
			json.put("appointmentHour", goodsStoreSkuService.getAppointmentHour());
			// 是否支持退订0：不支持，1：支持
			json.put("isUnsubscribe", goodsStoreSkuService.getIsUnsubscribe());

			String startDate = DateUtils.formatDate(goodsStoreSkuService.getStartTime(), "yyyy-MM-dd");
			String endDate = DateUtils.formatDate(goodsStoreSkuService.getEndTime(), "yyyy-MM-dd");
			json.put("orderInDate", startDate + "-" + endDate);
			json.put("notAvailableDate", goodsStoreSkuService.getInvalidDate());

		}
	}
	
	/**
	 * @Description: 获取订单明细列表信息
	 * @param json 返回的json对象
	 * @param userTradeOrderDetailVo 订单信息
	 * @param tradeOrderItem 订单项信息
	 * @param orderId 订单id
	 * @author zengjizu
	 * @date 2016年9月24日
	 */
	private void getTradeOrderItemDetail(JSONObject json,UserTradeOrderDetailVo userTradeOrderDetailVo,TradeOrderItem tradeOrderItem, String orderId) {

		// 消费码列表
		List<TradeOrderItemDetail> detailList = tradeOrderItemDetailMapper.selectByOrderItemDetailByOrderId(orderId);

		JSONArray consumeCodeList = new JSONArray();

		if (CollectionUtils.isNotEmpty(detailList)) {
			JSONObject detail = null;
			for (TradeOrderItemDetail tradeOrderItemDetail : detailList) {
				detail = new JSONObject();

				detail.put("consumeId", tradeOrderItemDetail.getId());
				detail.put("consumeCode", tradeOrderItemDetail.getConsumeCode());
				// 0：未消费，1：已消费，2：已退款，3：已过期
				detail.put("consumeStatus", tradeOrderItemDetail.getStatus());
				if (tradeOrderItemDetail.getUseTime() != null) {
					detail.put("consumeTime",
							DateUtils.formatDate(tradeOrderItemDetail.getUseTime(), "yyyy-MM-dd HH:mm:ss"));
				} else {
					detail.put("consumeTime", "");
				}

				if (tradeOrderItem != null) {
					detail.put("consumePrice", tradeOrderItem.getUnitPrice());
				} else {
					detail.put("consumePrice", "0");
				}
				consumeCodeList.add(detail);
				detail = null;
			}
		}

		if (userTradeOrderDetailVo.getStatus() == OrderStatusEnum.CANCELED) {
			json.put("cancelTime", DateUtils.formatDate(userTradeOrderDetailVo.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
		}

		json.put("consumeCodeList", consumeCodeList);
	}

}
