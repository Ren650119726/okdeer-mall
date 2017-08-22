/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: ITradeOrderServiceApiImpl.java 
 * @Date: 2016年3月3日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */

package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.store.entity.StoreMemberRelation;
import com.okdeer.archive.store.service.IStoreMemberRelationServiceApi;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.archive.system.entity.SysUser;
import com.okdeer.archive.system.service.ISysUserServiceApi;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.bdp.address.service.IAddressService;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsServiceApi;
import com.okdeer.mall.activity.coupons.service.ActivitySaleServiceApi;
import com.okdeer.mall.activity.discount.service.ActivityDiscountApi;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillServiceApi;
import com.okdeer.mall.order.dto.TradeOrderDto;
import com.okdeer.mall.order.dto.TradeOrderInvoiceDto;
import com.okdeer.mall.order.dto.TradeOrderItemDetailDto;
import com.okdeer.mall.order.dto.TradeOrderItemDto;
import com.okdeer.mall.order.dto.TradeOrderLogisticsDto;
import com.okdeer.mall.order.dto.TradeOrderPayDto;
import com.okdeer.mall.order.dto.TradeOrderQueryDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderInvoice;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.entity.TradeOrderLogistics;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.enums.ActivityBelongType;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.WithInvoiceEnum;
import com.okdeer.mall.order.service.CancelOrderService;
import com.okdeer.mall.order.service.ITradeOrderServiceApi;
import com.okdeer.mall.order.service.TradeOrderActivityService;
import com.okdeer.mall.order.service.TradeOrderItemService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.vo.TradeOrderOperateParamVo;
import com.okdeer.mall.order.vo.TradeOrderPayQueryVo;

/**
 * 订单接口
 * 
 * @project yschome-mall
 * @author 郭昌平
 * @date 2016年3月3日 下午7:57:22
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *    重构4.1            2016-7-19            wusw                添加财务系统的订单接口（包含服务店订单情况）
 *    重构4.1            2016-7-19            wusw                添加财务系统的订单接口关于订单来源的判断转换
 *    重构4.1            2016-7-28            wusw                修改财务系统的订单导出接口
 *    12170            2016-8-6              wusw                修改财务系统的订单交易接口关于货到付款订单的支付方式判断处理
 *    12051            2016-8-11             wusw                修改商品金额总计
 *    v1.1.0            2016-9-17            zengjz              增加财务系统统计交易订单数量、金额接口
 *    v1.2.0           2016-11-16            zengjz              取消订单接口更换
 *    V2.0.0            2017-01-09           wusw                修改订单查询和导出的线上订单包括订单来源为友门鹿便利店(CVS)的订单
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.ITradeOrderServiceApi")
public class TradeOrderApiImpl implements ITradeOrderServiceApi {

	private static final Logger logger = LoggerFactory.getLogger(TradeOrderApiImpl.class);

	@Resource
	private TradeOrderService tradeOrderService;

	@Resource
	private TradeOrderItemService tradeOrderItemService;

	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoService;

	@Autowired
	private TradeOrderActivityService tradeOrderActivityService;

	@Reference(version = "1.0.0", check = false)
	private ActivityCouponsServiceApi activityCouponsService;

	/**
	 * 用户Service
	 */
	@Reference(version = "1.0.0", check = false)
	private ISysUserServiceApi sysUserService;

	/**
	 * 用户店铺关联Service
	 */
	@Reference(version = "1.0.0", check = false)
	private IStoreMemberRelationServiceApi storeMemberRelationService;

	// Begin V2.1.0 added by luosm 2017-02-16
	@Reference(version = "1.0.0", check = false)
	private IAddressService addressService;


	@Reference(version = "1.0.0", check = false)
	private ActivityDiscountApi activityDiscountService;

	@Reference(version = "1.0.0", check = false)
	private ActivitySaleServiceApi activitySaleService;

	@Reference(version = "1.0.0", check = false)
	private ActivitySeckillServiceApi activitySeckillService;

	// End V2.1.0 added by luosm 2017-02-16

	/**
	 * 订单取消service
	 */
	@Resource
	private CancelOrderService cancelOrderService;

	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;

	/**
	 * @desc 订单详情（快送同步）（订单、支付、物流、订单项等信息）
	 */
	@Override
	public TradeOrderDto findOrderDetail(String orderId) throws Exception {
		TradeOrder tradeOrder = tradeOrderService.findOrderDetail(orderId);

		TradeOrderDto orderDto = new TradeOrderDto();
		if (tradeOrder != null) {
			orderDto.setId(tradeOrder.getId());
			orderDto.setUserPhone(tradeOrder.getUserPhone());
			orderDto.setUserId(tradeOrder.getUserId());
			orderDto.setStoreId(tradeOrder.getStoreId());
			orderDto.setOrderNo(tradeOrder.getOrderNo());
			orderDto.setPayWay(tradeOrder.getPayWay().ordinal());
			orderDto.setPickUpCode(tradeOrder.getPickUpCode());
			orderDto.setPickUpTime(tradeOrder.getPickUpTime());
			orderDto.setTotalAmount(tradeOrder.getTotalAmount());
			orderDto.setActualAmount(tradeOrder.getActualAmount());
			orderDto.setPreferentialPrice(tradeOrder.getPreferentialPrice());
			orderDto.setFare(tradeOrder.getFare());
			orderDto.setRemark(tradeOrder.getRemark());
			orderDto.setDeliveryTime(tradeOrder.getDeliveryTime());
			orderDto.setReceivedTime(tradeOrder.getReceivedTime());
			orderDto.setCreateTime(tradeOrder.getCreateTime());
			orderDto.setUpdateTime(tradeOrder.getUpdateTime());
			orderDto.setStatus(tradeOrder.getStatus().ordinal());
			orderDto.setStatusName(tradeOrder.getStatus().getValue());
		}

		List<TradeOrderItemDto> itemDtoList = new ArrayList<TradeOrderItemDto>();
		if (tradeOrder != null && tradeOrder.getTradeOrderItem() != null) {
			for (TradeOrderItem item : tradeOrder.getTradeOrderItem()) {
				TradeOrderItemDto itemDto = new TradeOrderItemDto();
				BeanUtils.copyProperties(itemDto, item);
				itemDtoList.add(itemDto);
			}
		}
		orderDto.setTradeOrderItem(itemDtoList);
		return orderDto;
	}

	/**
	 * @desc 同步订单状态
	 *
	 * @param orderId
	 *            订单ID
	 * @param status
	 *            订单状态
	 * @param reason
	 *            原因
	 * @return 是否成功
	 */
	@Override
	public boolean updateOrderStatus(String orderId, String status, String reason) throws Exception {
		logger.info("订单同步状态" + "，orderId:" + orderId + "，status:" + status + "，reason:" + reason);
		TradeOrder tradeOrder = tradeOrderService.selectById(orderId);
		// OrderStatusEnum orderStatus = OrderStatusEnum.enumNameOf(status);
		// if (orderStatus == null) {
		// throw new Exception("订单状态status为空或名字错误异常");
		// }
		// tradeOrder.setCurrentStatus(tradeOrder.getStatus());
		// tradeOrder.setStatus(orderStatus);
		if (StringUtils.isNotEmpty(reason)) {
			tradeOrder.setReason(reason);
		}
		if (tradeOrder.getStatus() == OrderStatusEnum.CANCELED) {
			cancelOrderService.cancelOrder(tradeOrder, false);
		} else if (tradeOrder.getStatus() == OrderStatusEnum.HAS_BEEN_SIGNED) {
			this.tradeOrderService.updateWithConfirm(tradeOrder);
		} else if (tradeOrder.getStatus() == OrderStatusEnum.REFUSED) {
			cancelOrderService.updateWithUserRefuse(tradeOrder);
		}
		return true;
	}

	/**
	 * @desc 发货 （快送同步）
	 * @param orderId 订单ID
	 * @param status 订单状态
	 * @param reason 原因
	 * @return 是否成功
	 */
	@Override
	public boolean updateOrderStatus(String orderId, String status, String reason, String userId) throws Exception {
		logger.info(
				"确认收货订单同步状态" + "，orderId:" + orderId + "，status:" + status + "，reason:" + reason + "userId" + userId);
		TradeOrder tradeOrder = tradeOrderService.selectById(orderId);
		// OrderStatusEnum orderStatus = OrderStatusEnum.enumNameOf(status);
		// if (orderStatus == null) {
		// throw new Exception("订单状态status为空或名字错误异常");
		// }
		// tradeOrder.setCurrentStatus(tradeOrder.getStatus());
		// tradeOrder.setStatus(orderStatus);
		if (StringUtils.isNotEmpty(reason)) {
			tradeOrder.setReason(reason);
		}
		if (StringUtils.isNotEmpty(userId)) {
			tradeOrder.setUpdateUserId(userId);
		}
		tradeOrder.setUpdateTime(new Date());

		if (tradeOrder.getStatus() == OrderStatusEnum.CANCELED) {
			// modify by zengjz 将取消订单的接口换成 cancelOrderService
			cancelOrderService.cancelOrder(tradeOrder, false);
		} else if (tradeOrder.getStatus() == OrderStatusEnum.HAS_BEEN_SIGNED) {
			this.tradeOrderService.updateWithConfirm(tradeOrder);
		} else if (tradeOrder.getStatus() == OrderStatusEnum.REFUSED) {
			// modify by zengjz 将取消订单的接口换成 cancelOrderService
			cancelOrderService.updateWithUserRefuse(tradeOrder);
		} else if (tradeOrder.getStatus() == OrderStatusEnum.TO_BE_SIGNED) {
			TradeOrderOperateParamVo param = new TradeOrderOperateParamVo();
			param.setOrderId(orderId);
			param.setReason(reason);
			param.setUserId(userId);
			SysUser sysUser = sysUserService.getUserById(userId);
			if (sysUser != null) {
				List<StoreMemberRelation> relationList = storeMemberRelationService.findBySysUserId(userId);
				if (relationList != null && !relationList.isEmpty()) {
					param.setStoreId(relationList.get(0).getStoreId());
				}
			}
			this.tradeOrderService.updateOrderShipment(param);
		}
		return true;
	}

	/**
	 * 更新订单状态
	 *
	 * @param orderId
	 *            订单ID
	 * @param status
	 *            订单状态
	 * @param reason
	 *            原因
	 * @return
	 */
	@Override
	public List<TradeOrderItemDto> findOrderItems(String orderId) throws Exception {
		List<TradeOrderItemDto> orderDtos = Lists.newArrayList();
		List<TradeOrderItem> tradeOrders = tradeOrderItemService.selectOrderItemByOrderId(orderId);
		for (TradeOrderItem tradeOrder : tradeOrders) {
			TradeOrderItemDto itemDto = new TradeOrderItemDto();
			BeanUtils.copyProperties(itemDto, tradeOrder);
			orderDtos.add(itemDto);
		}
		return orderDtos;
	}

	

	
	
	/***
	 * 实物订单详情（订单状态，店铺，收货，送货，发票，优惠，订单，商品信息）
	 */
	@Override
	public TradeOrderDto selectByOrderId(String orderId) throws Exception {
		TradeOrder order = tradeOrderService.erpSelectByOrderId(orderId);
		TradeOrderDto tradeOrderDto = new TradeOrderDto();

		// 订单状态
		tradeOrderDto.setId(order.getId());
		tradeOrderDto.setStatus(order.getStatus().ordinal());
		if (order.getReason() != null) {
			tradeOrderDto.setReason(order.getReason());
		}
		tradeOrderDto.setUpdateTime(order.getUpdateTime());

		// 店铺信息
		tradeOrderDto.setStoreName(order.getStoreName());
		if (order.getStoreType() != null) {
			tradeOrderDto.setStoreType(order.getStoreType().ordinal());
		}
		tradeOrderDto.setStoreMobile(order.getStoreMobile());

		// 收货信息
		TradeOrderLogisticsDto tradeOrderLogisticsDto = new TradeOrderLogisticsDto();
		if (order.getTradeOrderLogistics() != null) {
			TradeOrderLogistics tradeOrderLogistics = order.getTradeOrderLogistics();
			tradeOrderLogisticsDto.setConsigneeName(tradeOrderLogistics.getConsigneeName());
			tradeOrderLogisticsDto.setMobile(tradeOrderLogistics.getMobile());
			tradeOrderLogisticsDto.setArea(tradeOrderLogistics.getArea());
			tradeOrderLogisticsDto.setAddress(tradeOrderLogistics.getAddress());

			tradeOrderDto.setTradeOrderLogistics(tradeOrderLogisticsDto);
		}
		if (order.getRemark() != null) {
			tradeOrderDto.setRemark(order.getRemark());
		}
		// 送货信息
		tradeOrderDto.setPickUpType(order.getPickUpType().ordinal());
		if (StringUtils.isNotEmpty(order.getPickUpTime())) {
			tradeOrderDto.setPickUpTime(order.getPickUpTime());
		}
		// 发票信息
		if (order.getInvoice() == WithInvoiceEnum.HAS) {
			TradeOrderInvoiceDto tradeOrderInvoiceDto = new TradeOrderInvoiceDto();
			TradeOrderInvoice tradeOrderInvoice = order.getTradeOrderInvoice();
			tradeOrderInvoiceDto.setHead(tradeOrderInvoice.getHead());
			tradeOrderInvoiceDto.setContext(tradeOrderInvoice.getContext());

			tradeOrderDto.setTradeOrderInvoice(tradeOrderInvoiceDto);
		}
		// 优惠信息
		if (order.getActivityName() != null) {
			tradeOrderDto.setActivityName(order.getActivityName());
			tradeOrderDto.setPreferentialPrice(order.getPreferentialPrice());
		}
		// 订单信息
		tradeOrderDto.setOrderNo(order.getOrderNo());
		tradeOrderDto.setPayWay(order.getPayWay().ordinal());
		tradeOrderDto.setCreateTime(order.getCreateTime());// 下单时间

		TradeOrderPayDto tradeOrderPayDto = new TradeOrderPayDto();
		if (order.getTradeOrderPay() != null) {
			TradeOrderPay tradeOrderPay = order.getTradeOrderPay();
			tradeOrderPayDto.setPayTime(tradeOrderPay.getPayTime());
			tradeOrderPayDto.setPayType(tradeOrderPay.getPayType().ordinal());

			tradeOrderDto.setTradeOrderPay(tradeOrderPayDto);// 付款时间
		}

		if (order.getDeliveryTime() != null) {
			tradeOrderDto.setDeliveryTime(order.getDeliveryTime());// 发货时间
		}
		// 商品信息
		/*
		 * TradeOrderItemDto tradeOrderItemDto = new TradeOrderItemDto(); TradeOrderItem tradeOrderItem =
		 * order.getTradeOrderItem().get(0); tradeOrderItemDto.setMainPicPrl(tradeOrderItem.getMainPicPrl());
		 * tradeOrderItemDto.setSkuName(tradeOrderItem.getSkuName());
		 * tradeOrderItemDto.setUnitPrice(tradeOrderItem.getUnitPrice());
		 * tradeOrderItemDto.setQuantity(tradeOrderItem.getQuantity());
		 * tradeOrderItemDto.setTotalAmount(tradeOrderItem.getTotalAmount());
		 */

		List<String> storeSkuIds = new ArrayList<String>();
		if (order.getTradeOrderItem() != null) {
			for (TradeOrderItem item : order.getTradeOrderItem()) {
				storeSkuIds.add(item.getStoreSkuId());
			}
		}

		// Begin 12051 add by wusw 20160811
		BigDecimal totalAmount = new BigDecimal(0.00);
		// End 12051 add by wusw 20160811
		List<TradeOrderItemDto> itemDtoList = new ArrayList<TradeOrderItemDto>();
		if (order.getTradeOrderItem() != null) {
			List<GoodsStoreSku> storeSkuList = goodsStoreSkuServiceApi.findByIds(storeSkuIds);
			for (TradeOrderItem item : order.getTradeOrderItem()) {
				TradeOrderItemDto tradeOrderItemDto = new TradeOrderItemDto();
				tradeOrderItemDto.setMainPicPrl(item.getMainPicPrl());
				tradeOrderItemDto.setSkuName(item.getSkuName());
				tradeOrderItemDto.setPropertiesIndb(item.getPropertiesIndb());
				tradeOrderItemDto.setUnitPrice(item.getUnitPrice());
				tradeOrderItemDto.setQuantity(item.getQuantity());
				tradeOrderItemDto.setTotalAmount(item.getTotalAmount());
				if (null != storeSkuList) {
					for (GoodsStoreSku goodsStoreSku : storeSkuList) {
						if (item.getStoreSkuId().equals(goodsStoreSku.getId())) {
							tradeOrderItemDto.setUnit(goodsStoreSku.getUnit());
							break;
						}
					}
				}
				itemDtoList.add(tradeOrderItemDto);
				tradeOrderDto.setTradeOrderItem(itemDtoList);
				// Begin 12051 add by wusw 20160811
				BigDecimal quantity = new BigDecimal(item.getQuantity());
				BigDecimal itemAmount = item.getUnitPrice().multiply(quantity);
				totalAmount = totalAmount.add(itemAmount);
				// End 12051 add by wusw 20160811
			}
		}
		// Begin 12051 update by wusw 20160811
		tradeOrderDto.setTotalAmount(totalAmount);
		// End 12051 update by wusw 20160811
		tradeOrderDto.setFare(order.getFare());
		tradeOrderDto.setActualAmount(order.getActualAmount());
		return tradeOrderDto;
	}

	/**
	 * 服务订单详情(状态，店铺，优惠，订单，商品信息)
	 */
	@Override
	public TradeOrderDto selectByServiceOrderId(String orderId) throws Exception {
		TradeOrderDto tradeOrderDto = new TradeOrderDto();
		TradeOrder order = tradeOrderService.erpSelectByServiceOrderId(orderId);
		tradeOrderDto.setType(order.getType().ordinal());
		// 订单状态
		tradeOrderDto.setId(order.getId());
		tradeOrderDto.setStatus(order.getStatus().ordinal());

		// 店铺信息
		tradeOrderDto.setStoreName(order.getStoreName());
		tradeOrderDto.setStoreMobile(order.getStoreMobile());

		// 优惠信息
		if (order.getActivityName() != null) {
			tradeOrderDto.setActivityName(order.getActivityName());
			tradeOrderDto.setPreferentialPrice(order.getPreferentialPrice());
		}
		// 订单信息
		tradeOrderDto.setOrderNo(order.getOrderNo());
		tradeOrderDto.setPayWay(order.getPayWay().ordinal());
		tradeOrderDto.setCreateTime(order.getCreateTime());// 下单时间

		TradeOrderPayDto tradeOrderPayDto = new TradeOrderPayDto();
		if (order.getTradeOrderPay() != null) {
			TradeOrderPay tradeOrderPay = order.getTradeOrderPay();
			tradeOrderPayDto.setPayTime(tradeOrderPay.getPayTime());
			tradeOrderPayDto.setPayType(tradeOrderPay.getPayType().ordinal());

			tradeOrderDto.setTradeOrderPay(tradeOrderPayDto);// 付款时间
		}
		// add by mengsj begin 收货信息
		TradeOrderLogisticsDto tradeOrderLogisticsDto = new TradeOrderLogisticsDto();
		if (order.getTradeOrderLogistics() != null) {
			TradeOrderLogistics tradeOrderLogistics = order.getTradeOrderLogistics();
			tradeOrderLogisticsDto.setConsigneeName(tradeOrderLogistics.getConsigneeName());
			tradeOrderLogisticsDto.setMobile(tradeOrderLogistics.getMobile());
			tradeOrderLogisticsDto.setArea(tradeOrderLogistics.getArea());
			tradeOrderLogisticsDto.setAddress(tradeOrderLogistics.getAddress());

			tradeOrderDto.setTradeOrderLogistics(tradeOrderLogisticsDto);
		}
		if (order.getRemark() != null) {
			tradeOrderDto.setRemark(order.getRemark());
		}
		// 送货信息
		tradeOrderDto.setPickUpType(order.getPickUpType().ordinal());
		if (StringUtils.isNotEmpty(order.getPickUpTime())) {
			tradeOrderDto.setPickUpTime(order.getPickUpTime());
		}
		// 发票信息
		if (order.getInvoice() == WithInvoiceEnum.HAS) {
			TradeOrderInvoiceDto tradeOrderInvoiceDto = new TradeOrderInvoiceDto();
			TradeOrderInvoice tradeOrderInvoice = order.getTradeOrderInvoice();
			tradeOrderInvoiceDto.setHead(tradeOrderInvoice.getHead());
			tradeOrderInvoiceDto.setContext(tradeOrderInvoice.getContext());

			tradeOrderDto.setTradeOrderInvoice(tradeOrderInvoiceDto);
		}
		// add by mengsj end

		List<String> storeSkuIds = new ArrayList<String>();
		if (order.getTradeOrderItem() != null) {
			for (TradeOrderItem item : order.getTradeOrderItem()) {
				storeSkuIds.add(item.getStoreSkuId());
			}
		}

		// Begin 12051 add by wusw 20160811
		BigDecimal totalAmount = new BigDecimal(0.00);
		// End 12051 add by wusw 20160811
		// 商品信息
		List<TradeOrderItemDto> itemDtoList = new ArrayList<TradeOrderItemDto>();
		if (order.getTradeOrderItem() != null) {
			List<GoodsStoreSku> storeSkuList = goodsStoreSkuServiceApi.findByIds(storeSkuIds);
			for (TradeOrderItem item : order.getTradeOrderItem()) {
				TradeOrderItemDto tradeOrderItemDto = new TradeOrderItemDto();
				tradeOrderItemDto.setMainPicPrl(item.getMainPicPrl());
				tradeOrderItemDto.setSkuName(item.getSkuName());
				tradeOrderItemDto.setPropertiesIndb(item.getPropertiesIndb());
				tradeOrderItemDto.setUnitPrice(item.getUnitPrice());
				tradeOrderItemDto.setQuantity(item.getQuantity());
				tradeOrderItemDto.setTotalAmount(item.getTotalAmount());
				if (null != storeSkuList) {
					for (GoodsStoreSku goodsStoreSku : storeSkuList) {
						if (item.getStoreSkuId().equals(goodsStoreSku.getId())) {
							tradeOrderItemDto.setUnit(goodsStoreSku.getUnit());
							break;
						}
					}
				}
				itemDtoList.add(tradeOrderItemDto);
				tradeOrderDto.setTradeOrderItem(itemDtoList);
				// Begin 12051 add by wusw 20160811
				BigDecimal quantity = new BigDecimal(item.getQuantity());
				BigDecimal itemAmount = item.getUnitPrice().multiply(quantity);
				totalAmount = totalAmount.add(itemAmount);
				// End 12051 add by wusw 20160811
			}
		}

		// 服务型订单项详情
		List<TradeOrderItemDetailDto> itemDetailDtoList = new ArrayList<TradeOrderItemDetailDto>();
		if (order.getTradeOrderItemDetail() != null) {
			for (TradeOrderItemDetail tradeOrderItemDetail : order.getTradeOrderItemDetail()) {
				TradeOrderItemDetailDto tradeOrderItemDetailDto = new TradeOrderItemDetailDto();
				tradeOrderItemDetailDto.setConsumeCode(tradeOrderItemDetail.getConsumeCode());// 消费码
				tradeOrderItemDetailDto.setStatus(tradeOrderItemDetail.getStatus().ordinal());// 状态
				tradeOrderItemDetailDto.setStartTime(tradeOrderItemDetail.getStartTime());// 有效开始时间
				tradeOrderItemDetailDto.setEndTime(tradeOrderItemDetail.getEndTime());// 有效结束时间
				tradeOrderItemDetailDto.setUpdateTime(tradeOrderItemDetail.getUpdateTime());
				tradeOrderItemDetailDto.setUseTime(tradeOrderItemDetail.getUseTime());
				itemDetailDtoList.add(tradeOrderItemDetailDto);
				tradeOrderDto.setTradeOrderItemDetailDto(itemDetailDtoList);
			}
		}
		// Begin 12051 update by wusw 20160811
		tradeOrderDto.setTotalAmount(totalAmount);
		// End 12051 update by wusw 20160811
		tradeOrderDto.setFare(order.getFare());
		tradeOrderDto.setActualAmount(order.getActualAmount());

		return tradeOrderDto;
	}

//	/******************************* 财务系统接口 **************************************************************************************/
//	/**
//	 * 查询微信或支付宝支付的，订单状态处于已取消、已拒收、取消中、拒收中的订单 （主要用于财务系统接口,分页）
//	 * 
//	 * @author wusw
//	 * @param params
//	 * @return
//	 * @throws Exception
//	 */
//	@Override
//	public PageResultVo<TradeOrderPayQueryDto> findByStatusPayType(Map<String, Object> params) throws Exception {
//		int pageNum = Integer.valueOf(params.get("page").toString());
//		int pageSize = Integer.valueOf(params.get("rows").toString());
//		List<TradeOrderPayQueryDto> dtoList = new ArrayList<TradeOrderPayQueryDto>();
//		PageUtils<TradeOrderPayQueryVo> voPage = tradeOrderService.findByStatusPayType(params, pageNum, pageSize);
//		if (voPage.getList() != null) {
//			for (TradeOrderPayQueryVo vo : voPage.getList()) {
//				TradeOrderPayQueryDto dto = new TradeOrderPayQueryDto();
//				dto.setOrderId(vo.getOrderId());
//				dto.setOrderNo(vo.getOrderNo());
//				dto.setShopName(vo.getShopName());
//				dto.setBuyer(vo.getBuyer());
//				dto.setAgentName(vo.getAgentName());
//				dto.setTransMoney(vo.getTransMoney());
//				dto.setRealMoney(vo.getRealMoney());
//				dto.setPreferMoney(vo.getPreferMoney());
//				if (vo.getApplyTime() != null) {
//					dto.setApplyTime(vo.getApplyTime());
//				}
//				if (vo.getDealTime() != null) {
//					dto.setDealTime(vo.getDealTime());
//				}
//				if (vo.getHandTime() != null) {
//					dto.setHandTime(vo.getHandTime());
//				}
//				dto.setStatus(vo.getStatus());
//
//				// begin add by zengjz 增加违约金的处理
//				if (WhetherEnum.whether == vo.getIsBreach() && vo.getBreachMoney() != null) {
//					dto.setRefundAmount(vo.getRealMoney().subtract(vo.getBreachMoney()));
//				} else {
//					dto.setRefundAmount(vo.getRealMoney());
//				}
//				// end add by zengjz 增加违约金的处理
//
//				dto.setThirdTransNo(vo.getThirdTransNo());
//				dto.setPayType(vo.getPayType());
//
//				// BeanUtils.copyProperties(dto, vo);
//				dtoList.add(dto);
//			}
//		}
//		PageResultVo<TradeOrderPayQueryDto> dtoPage = new PageResultVo<TradeOrderPayQueryDto>(voPage.getPageNum(),
//				voPage.getPageSize(), voPage.getTotal(), dtoList);
//		return dtoPage;
//	}
//
//	/**
//	 * 查询微信或支付宝支付的，订单状态处于已取消、已拒收、取消中、拒收中的订单 （主要用于财务系统接口,不分页）
//	 * 
//	 * @author wusw
//	 * @param params
//	 * @return
//	 * @throws Exception
//	 */
//	@Override
//	public List<TradeOrderPayQueryDto> findListByStatusPayType(Map<String, Object> params)
//			throws ExceedRangeException, Exception {
//		if (tradeOrderService.selectCountByStatusPayType(params) > RECORD_NUM) {
//			throw new ExceedRangeException("查询导出取消、拒收订单异常", new Throwable());
//		}
//
//		List<TradeOrderPayQueryDto> dtoList = new ArrayList<TradeOrderPayQueryDto>();
//		List<TradeOrderPayQueryVo> voList = tradeOrderService.findListByStatusPayType(params);
//		if (voList != null) {
//			for (TradeOrderPayQueryVo vo : voList) {
//				TradeOrderPayQueryDto dto = new TradeOrderPayQueryDto();
//				dto.setOrderId(vo.getOrderId());
//				dto.setOrderNo(vo.getOrderNo());
//				dto.setShopName(vo.getShopName());
//				dto.setBuyer(vo.getBuyer());
//				dto.setTransMoney(vo.getTransMoney());
//				dto.setRealMoney(vo.getRealMoney());
//				dto.setPreferMoney(vo.getPreferMoney());
//				if (vo.getApplyTime() != null) {
//					dto.setApplyTime(vo.getApplyTime());
//				}
//				if (vo.getDealTime() != null) {
//					dto.setDealTime(vo.getDealTime());
//				}
//				if (vo.getHandTime() != null) {
//					dto.setHandTime(vo.getHandTime());
//				}
//				dto.setStatus(vo.getStatus());
//
//				// begin add by zengjz 增加违约金的处理
//				if (WhetherEnum.whether == vo.getIsBreach() && vo.getBreachMoney() != null) {
//					dto.setRefundAmount(vo.getRealMoney().subtract(vo.getBreachMoney()));
//				} else {
//					dto.setRefundAmount(vo.getRealMoney());
//				}
//				// end add by zengjz 增加违约金的处理
//				dto.setThirdTransNo(vo.getThirdTransNo());
//				dto.setPayType(vo.getPayType());
//				dto.setOrderResource(vo.getOrderResource());
//
//				// BeanUtils.copyProperties(dto, vo);
//				dtoList.add(dto);
//			}
//		}
//		return dtoList;
//	}

	/**
	 * @desc 根据订单id集合，查询订单信息 （主要用于财务系统接口）
	 * 
	 * @author wusw
	 * @param orderIds
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<TradeOrderQueryDto> findByOrderIdForRefunds(List<String> orderIds) throws Exception {

		List<TradeOrderQueryDto> dtoList = new ArrayList<TradeOrderQueryDto>();
		List<TradeOrderPayQueryVo> voList = tradeOrderService.findByOrderIdList(orderIds);
		if (voList != null) {
			for (TradeOrderPayQueryVo vo : voList) {
				TradeOrderQueryDto dto = new TradeOrderQueryDto();
				dto.setOrderId(vo.getOrderId());
				dto.setOrderNo(vo.getOrderNo());
				dto.setTradeNo(vo.getTradeNum());

				// begin add by zengjz 增加违约金的处理
				dto.setTotalFee(vo.getRealMoney());
				if (WhetherEnum.whether == vo.getIsBreach() && vo.getBreachMoney() != null) {
					dto.setRefundFee(vo.getRealMoney().subtract(vo.getBreachMoney()));
				} else {
					dto.setRefundFee(vo.getRealMoney());
				}
				// end add by zengjz 增加违约金的处理
				// 用于活动的相关查询
				TradeOrder order = new TradeOrder();
				order.setId(vo.getOrderId());
				order.setActivityId(vo.getActivityId());
				order.setActivityType(vo.getActivityType());
				// 如果不是代理商发起的活动，优惠金额为0
				if (tradeOrderActivityService.findActivityType(order) == ActivityBelongType.AGENT) {
					dto.setPreferAmount(vo.getPreferMoney().multiply(new BigDecimal("100")).intValue());
				} else {
					dto.setPreferAmount(0);
				}
				dto.setStatus(vo.getStatus());
				dto.setReason(vo.getReason());

				dto.setStoreUserId(storeInfoService.getBossIdByStoreId(vo.getStoreId()));
				dto.setBuyerUserId(vo.getUserId());
				// 活动发起用户ID
				if (vo.getActivityId() != null) {
					dto.setActivityUserId(tradeOrderActivityService.findActivityUserId(order));
				}

				dto.setPayType(vo.getPayType());
				dto.setTransactionId(vo.getThirdTransNo());
				dto.setOrderResource(vo.getOrderResource());

				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	/**
	 * @desc 查询微信或支付宝支付的，订单状态处于取消中、拒收中的订单 数量（主要用于财务系统接口）
	 * 
	 * @author wusw
	 * @return
	 * @throws Exception
	 */
	@Override
	public int selectCountForUnRefund() throws Exception {

		return tradeOrderService.selectCountForUnRefund();
	}
	
}
