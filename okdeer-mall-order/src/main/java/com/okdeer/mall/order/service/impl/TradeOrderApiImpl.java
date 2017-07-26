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
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.store.entity.StoreMemberRelation;
import com.okdeer.archive.store.service.IStoreMemberRelationServiceApi;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.archive.system.entity.SysUser;
import com.okdeer.archive.system.service.ISysUserServiceApi;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.bdp.address.entity.Address;
import com.okdeer.bdp.address.service.IAddressService;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsServiceApi;
import com.okdeer.mall.activity.coupons.service.ActivitySaleServiceApi;
import com.okdeer.mall.activity.discount.service.ActivityDiscountApi;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillServiceApi;
import com.okdeer.mall.common.vo.PageResultVo;
import com.okdeer.mall.member.member.vo.UserAddressVo;
import com.okdeer.mall.member.service.MemberConsigneeAddressService;
import com.okdeer.mall.order.dto.ERPTradeOrderVoDto;
import com.okdeer.mall.order.dto.TradeOrderDto;
import com.okdeer.mall.order.dto.TradeOrderInvoiceDto;
import com.okdeer.mall.order.dto.TradeOrderItemDetailDto;
import com.okdeer.mall.order.dto.TradeOrderItemDto;
import com.okdeer.mall.order.dto.TradeOrderLogisticsDto;
import com.okdeer.mall.order.dto.TradeOrderPayDto;
import com.okdeer.mall.order.dto.TradeOrderPayQueryDto;
import com.okdeer.mall.order.dto.TradeOrderQueryDto;
import com.okdeer.mall.order.dto.TradeOrderQueryParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderInvoice;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.entity.TradeOrderLogistics;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.enums.ActivityBelongType;
import com.okdeer.mall.order.enums.OrderActivityType;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.PickUpTypeEnum;
import com.okdeer.mall.order.enums.PreferentialType;
import com.okdeer.mall.order.enums.WithInvoiceEnum;
import com.okdeer.mall.order.exception.ExceedRangeException;
import com.okdeer.mall.order.service.CancelOrderService;
import com.okdeer.mall.order.service.ITradeOrderServiceApi;
import com.okdeer.mall.order.service.TradeOrderActivityService;
import com.okdeer.mall.order.service.TradeOrderItemService;
import com.okdeer.mall.order.service.TradeOrderLogisticsService;
import com.okdeer.mall.order.service.TradeOrderRefundsService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.vo.ActivityInfoVO;
import com.okdeer.mall.order.vo.ERPTradeOrderVo;
import com.okdeer.mall.order.vo.TradeOrderOperateParamVo;
import com.okdeer.mall.order.vo.TradeOrderPayQueryVo;
import com.okdeer.mall.system.entity.SysUserInvitationLoginNameVO;
import com.okdeer.mall.system.service.InvitationCodeService;

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

	/** 记录数 */
	private static final Integer RECORD_NUM = 10000;

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

	/**
	 * 物流service方法
	 */
	@Autowired
	private TradeOrderLogisticsService tradeOrderLogisticsService;

	/**
	 * 地址service
	 */
	@Autowired
	private MemberConsigneeAddressService memberConsigneeAddressService;

	/***
	 * 邀请信息
	 */
	@Autowired
	private InvitationCodeService invitationCodeService;

	@Reference(version = "1.0.0", check = false)
	private ActivityDiscountApi activityDiscountService;

	@Reference(version = "1.0.0", check = false)
	private ActivitySaleServiceApi activitySaleService;

	@Reference(version = "1.0.0", check = false)
	private ActivitySeckillServiceApi activitySeckillService;

	@Autowired
	private TradeOrderRefundsService tradeOrderRefundsService;
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

	/**
	 * 订单列表-分页
	 */
	@Override
	public PageResultVo<ERPTradeOrderVoDto> selectByParams(Map<String, Object> params) throws Exception {

		Assert.notNull(params, "type is not null");
		int pageSize = Integer.valueOf(params.get("pageSize").toString());
		int pageNum = Integer.valueOf(params.get("pageNum").toString());
		convertERPParams(params);

		PageUtils<ERPTradeOrderVo> page = tradeOrderService.erpSelectByParams(params, pageSize, pageNum);
		List<ERPTradeOrderVoDto> erpTradeOrderVoDto = Lists.newArrayList();
		for (ERPTradeOrderVo erpTradeOrderVo : page.getList()) {
			ERPTradeOrderVoDto dto = new ERPTradeOrderVoDto();
			dto.setId(erpTradeOrderVo.getId());
			dto.setOrderNo(erpTradeOrderVo.getOrderNo());
			dto.setType(erpTradeOrderVo.getType().ordinal());
			dto.setStoreName(erpTradeOrderVo.getStoreName());
			dto.setUserPhone(erpTradeOrderVo.getUserPhone());
			dto.setTotalAmount(erpTradeOrderVo.getTotalAmount());
			dto.setActualAmount(erpTradeOrderVo.getActualAmount());
			dto.setPreferentialPrice(erpTradeOrderVo.getPreferentialPrice());
			dto.setCreateTime(erpTradeOrderVo.getCreateTime());
			dto.setPayWay(erpTradeOrderVo.getPayWay().ordinal());
			if (erpTradeOrderVo.getStatus() != null) {
				dto.setStatus(erpTradeOrderVo.getStatus().ordinal());
			}

			erpTradeOrderVoDto.add(dto);
		}
		PageResultVo<ERPTradeOrderVoDto> result = new PageResultVo<ERPTradeOrderVoDto>(page.getPageNum(),
				page.getPageSize(), page.getTotal(), erpTradeOrderVoDto);
		return result;
	}

	private void convertERPParams(Map<String, Object> params) {
		Assert.notNull(params.get("type"), "type is not null");
		String type = params.get("type").toString();
		List<OrderTypeEnum> typeList = Lists.newArrayList();
		if ("0".equals(type)) {
			typeList.add(OrderTypeEnum.PHYSICAL_ORDER);
		} else if ("1".equals(type)) {
			typeList.add(OrderTypeEnum.SERVICE_ORDER);
		} else if ("2".equals(type)) {
			typeList.add(OrderTypeEnum.STORE_CONSUME_ORDER);
		} else if ("3".equals(type)) {
			typeList.add(OrderTypeEnum.PHONE_PAY_ORDER);
			typeList.add(OrderTypeEnum.TRAFFIC_PAY_ORDER);
		}
		params.put("typeList", typeList);
	}

	/**
	 * 订单列表
	 */
	@Override
	public List<ERPTradeOrderVoDto> selectByParam(Map<String, Object> params) throws Exception {

		convertERPParams(params);
		List<ERPTradeOrderVo> list = tradeOrderService.erpSelectByParam(params);
		List<ERPTradeOrderVoDto> erpTradeOrderVoDtoList = new ArrayList<ERPTradeOrderVoDto>();
		for (ERPTradeOrderVo erpTradeOrderVo : list) {
			ERPTradeOrderVoDto dto = new ERPTradeOrderVoDto();
			dto.setId(erpTradeOrderVo.getId());
			dto.setOrderNo(erpTradeOrderVo.getOrderNo());
			dto.setType(erpTradeOrderVo.getType().ordinal());
			dto.setStoreName(erpTradeOrderVo.getStoreName());
			dto.setUserPhone(erpTradeOrderVo.getUserPhone());
			dto.setTotalAmount(erpTradeOrderVo.getTotalAmount());
			dto.setActualAmount(erpTradeOrderVo.getActualAmount());
			dto.setPreferentialPrice(erpTradeOrderVo.getPreferentialPrice());
			dto.setCreateTime(erpTradeOrderVo.getCreateTime());
			dto.setPayWay(erpTradeOrderVo.getPayWay().ordinal());
			dto.setStatus(erpTradeOrderVo.getStatus().ordinal());
			erpTradeOrderVoDtoList.add(dto);
		}
		return erpTradeOrderVoDtoList;
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

	/******************************* 财务系统接口 **************************************************************************************/
	/**
	 * 查询微信或支付宝支付的，订单状态处于已取消、已拒收、取消中、拒收中的订单 （主要用于财务系统接口,分页）
	 * 
	 * @author wusw
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Override
	public PageResultVo<TradeOrderPayQueryDto> findByStatusPayType(Map<String, Object> params) throws Exception {
		int pageNum = Integer.valueOf(params.get("page").toString());
		int pageSize = Integer.valueOf(params.get("rows").toString());
		List<TradeOrderPayQueryDto> dtoList = new ArrayList<TradeOrderPayQueryDto>();
		PageUtils<TradeOrderPayQueryVo> voPage = tradeOrderService.findByStatusPayType(params, pageNum, pageSize);
		if (voPage.getList() != null) {
			for (TradeOrderPayQueryVo vo : voPage.getList()) {
				TradeOrderPayQueryDto dto = new TradeOrderPayQueryDto();
				dto.setOrderId(vo.getOrderId());
				dto.setOrderNo(vo.getOrderNo());
				dto.setShopName(vo.getShopName());
				dto.setBuyer(vo.getBuyer());
				dto.setAgentName(vo.getAgentName());
				dto.setTransMoney(vo.getTransMoney());
				dto.setRealMoney(vo.getRealMoney());
				dto.setPreferMoney(vo.getPreferMoney());
				if (vo.getApplyTime() != null) {
					dto.setApplyTime(vo.getApplyTime());
				}
				if (vo.getDealTime() != null) {
					dto.setDealTime(vo.getDealTime());
				}
				if (vo.getHandTime() != null) {
					dto.setHandTime(vo.getHandTime());
				}
				dto.setStatus(vo.getStatus());

				// begin add by zengjz 增加违约金的处理
				if (WhetherEnum.whether == vo.getIsBreach() && vo.getBreachMoney() != null) {
					dto.setRefundAmount(vo.getRealMoney().subtract(vo.getBreachMoney()));
				} else {
					dto.setRefundAmount(vo.getRealMoney());
				}
				// end add by zengjz 增加违约金的处理

				dto.setThirdTransNo(vo.getThirdTransNo());
				dto.setPayType(vo.getPayType());

				// BeanUtils.copyProperties(dto, vo);
				dtoList.add(dto);
			}
		}
		PageResultVo<TradeOrderPayQueryDto> dtoPage = new PageResultVo<TradeOrderPayQueryDto>(voPage.getPageNum(),
				voPage.getPageSize(), voPage.getTotal(), dtoList);
		return dtoPage;
	}

	/**
	 * 查询微信或支付宝支付的，订单状态处于已取消、已拒收、取消中、拒收中的订单 （主要用于财务系统接口,不分页）
	 * 
	 * @author wusw
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<TradeOrderPayQueryDto> findListByStatusPayType(Map<String, Object> params)
			throws ExceedRangeException, Exception {
		if (tradeOrderService.selectCountByStatusPayType(params) > RECORD_NUM) {
			throw new ExceedRangeException("查询导出取消、拒收订单异常", new Throwable());
		}

		List<TradeOrderPayQueryDto> dtoList = new ArrayList<TradeOrderPayQueryDto>();
		List<TradeOrderPayQueryVo> voList = tradeOrderService.findListByStatusPayType(params);
		if (voList != null) {
			for (TradeOrderPayQueryVo vo : voList) {
				TradeOrderPayQueryDto dto = new TradeOrderPayQueryDto();
				dto.setOrderId(vo.getOrderId());
				dto.setOrderNo(vo.getOrderNo());
				dto.setShopName(vo.getShopName());
				dto.setBuyer(vo.getBuyer());
				dto.setTransMoney(vo.getTransMoney());
				dto.setRealMoney(vo.getRealMoney());
				dto.setPreferMoney(vo.getPreferMoney());
				if (vo.getApplyTime() != null) {
					dto.setApplyTime(vo.getApplyTime());
				}
				if (vo.getDealTime() != null) {
					dto.setDealTime(vo.getDealTime());
				}
				if (vo.getHandTime() != null) {
					dto.setHandTime(vo.getHandTime());
				}
				dto.setStatus(vo.getStatus());

				// begin add by zengjz 增加违约金的处理
				if (WhetherEnum.whether == vo.getIsBreach() && vo.getBreachMoney() != null) {
					dto.setRefundAmount(vo.getRealMoney().subtract(vo.getBreachMoney()));
				} else {
					dto.setRefundAmount(vo.getRealMoney());
				}
				// end add by zengjz 增加违约金的处理
				dto.setThirdTransNo(vo.getThirdTransNo());
				dto.setPayType(vo.getPayType());
				dto.setOrderResource(vo.getOrderResource());

				// BeanUtils.copyProperties(dto, vo);
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

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

	// Begin 重构4.1 add by wusw 20160719
	@Override
	public PageUtils<ERPTradeOrderVoDto> findOrderByParams(TradeOrderQueryParamDto tradeOrderQueryParamDto)
			throws Exception {
		PageUtils<ERPTradeOrderVo> page = tradeOrderService.findOrderForFinanceByParams(tradeOrderQueryParamDto,
				tradeOrderQueryParamDto.getPageNumber(), tradeOrderQueryParamDto.getPageSize());
		List<ERPTradeOrderVoDto> dtoList = buildERPTradeOrderVoDto(page);
		PageUtils<ERPTradeOrderVoDto> result = new PageUtils<ERPTradeOrderVoDto>(dtoList);
		result.setPageNum(page.getPageNum());
		result.setPageSize(page.getPageSize());
		result.setTotal(page.getTotal());
		return result;
	}

	// End 重构4.1 add by wusw 20160719

	// Begin V2.1.0 added by luosm 20170218
	// 构建数据
	private List<ERPTradeOrderVoDto> buildERPTradeOrderVoDto(PageUtils<ERPTradeOrderVo> page) {
		List<ERPTradeOrderVoDto> dtoList = new ArrayList<ERPTradeOrderVoDto>();
		// 订单ID集合
		List<String> orderIds = new ArrayList<String>();
		// 订单关联的店铺ids
		List<String> storeIds = new ArrayList<String>();
		for (ERPTradeOrderVo vo : page.getList()) {
			if (StringUtils.isNotEmpty(vo.getId())) {
				orderIds.add(vo.getId());
			}
			if (StringUtils.isNotEmpty(vo.getStoreId()) && !storeIds.contains(vo.getStoreId())) {
				storeIds.add(vo.getStoreId());
			}
		}

		// 订单的物流信息
		List<TradeOrderLogistics> logisticsList = null;
		if (CollectionUtils.isNotEmpty(orderIds)) {
			try {
				logisticsList = this.tradeOrderLogisticsService.selectByOrderIds(orderIds);
			} catch (ServiceException e) {
				logger.error("查询物流信息异常", e);
			}
		}
		if (logisticsList == null) {
			logisticsList = Lists.newArrayList();
		}
		// 店铺地址（到店自提和到店消费订单店铺地址）
		List<UserAddressVo> memberAddressList = null;
		// V2.1.0 end add by zhulq 收货地址取物流表信息 之前是取店铺地址
		if (CollectionUtils.isNotEmpty(storeIds)) {
			memberAddressList = this.memberConsigneeAddressService.findByStoreIds(storeIds);
		}
		if (memberAddressList == null) {
			memberAddressList = Lists.newArrayList();
		}
		// V2.1.0 end add by zhulq 获取服务店上门服务的收货地址（物流表的地址）

		Map<String, UserAddressVo> memberAddressMap = memberAddressList.stream()
				.collect(Collectors.toMap(m -> m.getUserId(), e -> e));
		Map<String, TradeOrderLogistics> logisticsMap = logisticsList.stream()
				.collect(Collectors.toMap(m -> m.getOrderId(), e -> e));

		Map<String, Address> addressMap = Maps.newHashMap();

		for (ERPTradeOrderVo vo : page.getList()) {
			ERPTradeOrderVoDto dto = new ERPTradeOrderVoDto();
			dto.setId(vo.getId());
			dto.setOrderNo(vo.getOrderNo());
			dto.setType(vo.getType().ordinal());
			dto.setStoreId(vo.getStoreId());
			dto.setStoreName(vo.getStoreName());
			dto.setUserPhone(vo.getUserPhone());
			dto.setTotalAmount(vo.getTotalAmount());
			dto.setActualAmount(vo.getActualAmount());
			dto.setPreferentialPrice(vo.getPreferentialPrice());
			dto.setCreateTime(vo.getCreateTime());
			dto.setOrderResource(vo.getOrderResource().ordinal());
			dto.setFare(vo.getFare());
			dto.setRealFarePreferential(vo.getRealFarePreferential());
			dto.setPlatformPreferential(vo.getPlatformPreferential());
			dto.setStorePreferential(vo.getStorePreferential());
			dto.setDeliveryType(vo.getDeliveryType());
			dto.setPickUpType(vo.getPickUpType());
			if(vo.getStatus()!=null){
				dto.setStatus(vo.getStatus().ordinal());
			}
			if (vo.getPayWay() == PayWayEnum.OFFLINE_CONFIRM_AND_PAY) {
				dto.setPayType(3);
			} else {
				// Begin 重构4.1 add by wusw 20160726
				if (vo.getPayType() != null) {
					dto.setPayType(vo.getPayType().ordinal());
				} else {// Begin 12170 add by wusw 20160806
					// Begin add by zhulq 2017-03-01 充值未付款 如果不重新赋值dto的支付类型直接默认是0
					dto.setPayType(8);
					// Begin add by zhulq 2017-03-01 充值未付款
					if (vo.getType() == OrderTypeEnum.PHYSICAL_ORDER && vo.getPayWay() == PayWayEnum.CASH_DELIERY) {
						dto.setPayType(4);
					}
				} // End 12170 add by wusw 20160806
					// End 重构4.1 add by wusw 20160726
			}
			// begin add by zhulq 充值订单所属城市和完成时间设置
			dto.setLocateCityName(vo.getCityName());
			dto.setCompleteTime(vo.getUpdateTime());
			// begin add by zhulq 充值订单所属城市和完成时间设置

			setAddressInfo(vo, dto, memberAddressMap, logisticsMap, addressMap);

			dtoList.add(dto);
		}

		return dtoList;
	}
	// End V2.1.0 added by luosm 20170218

	private void setAddressInfo(ERPTradeOrderVo vo, ERPTradeOrderVoDto dto, Map<String, UserAddressVo> memberAddressMap,
			Map<String, TradeOrderLogistics> logisticsMap, Map<String, Address> addressMap) {
		// 实物的送货上门订单收货地址取物流表信息 到店自提取的是店铺地址
		if (vo.getType() == OrderTypeEnum.PHYSICAL_ORDER && vo.getPickUpType() == PickUpTypeEnum.DELIVERY_DOOR) {
			// 如果是实物订单 而且是送货上门
			TradeOrderLogistics logistics = logisticsMap.get(vo.getId());
			if (logistics != null && !StringUtils.isBlank(logistics.getCityId())) {
				Address address = getAddressByCityId(logistics.getCityId(), addressMap);
				// 所属城市 实物订单的送货上门取物流表的地址
				dto.setCityName(address.getName() == null ? "" : address.getName());
				String area = logistics.getArea() == null ? "" : logistics.getArea();
				String addressExt = logistics.getAddress() == null ? "" : logistics.getAddress();
				// 收货地址
				dto.setAddress(area + addressExt);
			}
		} else if (vo.getType() == OrderTypeEnum.PHYSICAL_ORDER
				&& vo.getPickUpType() == PickUpTypeEnum.TO_STORE_PICKUP) {

			UserAddressVo userAddressVo = memberAddressMap.get(vo.getStoreId());
			if (userAddressVo != null) {
				String proviceName = userAddressVo.getProvinceName() == null ? "" : userAddressVo.getProvinceName();
				String cityName = userAddressVo.getCityName() == null ? "" : userAddressVo.getCityName();
				String areaName = userAddressVo.getAreaName() == null ? "" : userAddressVo.getAreaName();
				String ext = userAddressVo.getAreaExt() == null ? "" : userAddressVo.getAreaExt();
				String address = userAddressVo.getAddress() == null ? "" : userAddressVo.getAddress();
				// 所属城市
				dto.setCityName(cityName);
				// 收货地址
				dto.setAddress(proviceName + cityName + areaName + ext + address);
			}
		} else if (vo.getType() == OrderTypeEnum.SERVICE_STORE_ORDER) {
			TradeOrderLogistics logistics = logisticsMap.get(vo.getId());
			// 如果是实物订单 而且是送货上门
			if (logistics != null && !StringUtils.isBlank(logistics.getCityId())) {
				Address address = getAddressByCityId(logistics.getCityId(), addressMap);
				// 所属城市 实物订单的送货上门取物流表的地址
				dto.setCityName(address.getName() == null ? "" : address.getName());
				String area = logistics.getArea() == null ? "" : logistics.getArea();
				String addressExt = logistics.getAddress() == null ? "" : logistics.getAddress();
				// 收货地址
				dto.setAddress(area + addressExt);
			}
		} else if (vo.getType() == OrderTypeEnum.STORE_CONSUME_ORDER) {
			UserAddressVo userAddressVo = memberAddressMap.get(vo.getStoreId());
			if (userAddressVo != null) {
				// 所属城市
				String proviceName = userAddressVo.getProvinceName() == null ? "" : userAddressVo.getProvinceName();
				String cityName = userAddressVo.getCityName() == null ? "" : userAddressVo.getCityName();
				String areaName = userAddressVo.getAreaName() == null ? "" : userAddressVo.getAreaName();
				String ext = userAddressVo.getAreaExt() == null ? "" : userAddressVo.getAreaExt();
				String address = userAddressVo.getAddress() == null ? "" : userAddressVo.getAddress();
				// 所属城市
				dto.setCityName(cityName);
				// 收货地址
				dto.setAddress(proviceName + cityName + areaName + ext + address);
			}
		}
	}

	/**
	 * @Description: 获得城市信息
	 * @param cityId 城市id
	 * @param cityMap 缓村map
	 * @return
	 * @author zengjizu
	 * @date 2017年4月26日
	 */
	private Address getAddressByCityId(String cityId, Map<String, Address> cityMap) {
		if (cityMap.get(cityId) != null) {
			return cityMap.get(cityId);
		}
		Address address = addressService.getAddressById(Long.parseLong(cityId));
		cityMap.put(cityId, address);
		return address;
	}

	/**
	 * (non-Javadoc)
	 * @see com.yschome.api.mall.order.service.ITradeOrderServiceApi#findOrderListByParams(java.util.Map)
	 */
	@Override
	public List<ERPTradeOrderVoDto> findOrderListByParams(TradeOrderQueryParamDto tradeOrderQueryParamDto)
			throws Exception {
		
		List<ERPTradeOrderVo> list = tradeOrderService.findOrderListForFinanceByParams(tradeOrderQueryParamDto);
		
		List<ERPTradeOrderVoDto> result = new ArrayList<ERPTradeOrderVoDto>();

		// 如果有订单信息
		if (CollectionUtils.isNotEmpty(list)) {
			// 订单ID集合
			List<String> orderIds = new ArrayList<String>();
			// 用户ID集合
			List<String> userIds = new ArrayList<String>();
			// 订单关联的店铺ids
			List<String> storeIds = new ArrayList<String>();
			// 活动id集合
			for (ERPTradeOrderVo order : list) {
				if (StringUtils.isNotEmpty(order.getId()) && !orderIds.contains(order.getId())) {
					orderIds.add(order.getId());
				}
				if (StringUtils.isNotEmpty(order.getUserId()) && !userIds.contains(order.getUserId())) {
					userIds.add(order.getUserId());
				}
				if (StringUtils.isNotEmpty(order.getStoreId()) && !storeIds.contains(order.getStoreId())) {
					storeIds.add(order.getStoreId());
				}
			}

			// V2.1.0 begin add by zhulq 获取服务店上门服务的收货地址（物流表的地址）
			
			// 订单的物流信息
			List<TradeOrderLogistics> logisticsList = null;
			if (CollectionUtils.isNotEmpty(orderIds)) {
				try {
					logisticsList = this.tradeOrderLogisticsService.selectByOrderIds(orderIds);
				} catch (ServiceException e) {
					logger.error("查询物流信息异常", e);
				}
			}
			if (logisticsList == null) {
				logisticsList = Lists.newArrayList();
			}
			
			// 店铺地址（到店自提和到店消费订单店铺地址）
			List<UserAddressVo> memberAddressList = null;
			// V2.1.0 end add by zhulq 收货地址取物流表信息 之前是取店铺地址
			if (CollectionUtils.isNotEmpty(storeIds)) {
				memberAddressList = this.memberConsigneeAddressService.findByStoreIds(storeIds);
			}
			if (memberAddressList == null) {
				memberAddressList = Lists.newArrayList();
			}

			// V2.1.0 end add by zhulq 获取服务店上门服务的收货地址（物流表的地址）

			List<SysUserInvitationLoginNameVO> inviteNameLists = new ArrayList<SysUserInvitationLoginNameVO>();
			if (CollectionUtils.isNotEmpty(userIds)) {
				inviteNameLists = invitationCodeService.selectLoginNameByUserId(userIds);
			}
			if (inviteNameLists == null) {
				inviteNameLists = Lists.newArrayList();
			}
			
			
			List<TradeOrderRefunds> tradeOrderRefundsList = new ArrayList<TradeOrderRefunds>();
			List<ActivityInfoVO> activityList = null;
			if (CollectionUtils.isNotEmpty(orderIds)) {
				tradeOrderRefundsList = tradeOrderRefundsService.selectByOrderIds(orderIds);
				activityList = tradeOrderService.findActivityInfo(orderIds);
			}
			if (activityList == null) {
				activityList = Lists.newArrayList();
			}

			
			
			Map<String, UserAddressVo> memberAddressMap = memberAddressList.stream()
					.collect(Collectors.toMap(m -> m.getUserId(), e -> e));

			Map<String, TradeOrderLogistics> logisticsMap = logisticsList.stream()
					.collect(Collectors.toMap(m -> m.getOrderId(), e -> e));

			Map<String, ActivityInfoVO> activityMap = activityList.stream()
					.collect(Collectors.toMap(m -> m.getOrderId(), e -> e));

			Map<String, Address> addressMap = Maps.newHashMap();

			for (ERPTradeOrderVo vo : list) {
				ERPTradeOrderVoDto dto = new ERPTradeOrderVoDto();
				dto.setId(vo.getId());
				dto.setOrderNo(vo.getOrderNo());
				dto.setType(vo.getType().ordinal());
				dto.setStoreId(vo.getStoreId());
				dto.setStoreName(vo.getStoreName());
				dto.setUserPhone(vo.getUserPhone());
				dto.setTotalAmount(vo.getTotalAmount());
				dto.setActualAmount(vo.getActualAmount());
				dto.setPreferentialPrice(vo.getPreferentialPrice());
				dto.setCreateTime(vo.getCreateTime());
				dto.setFare(vo.getFare());
				dto.setRealFarePreferential(vo.getRealFarePreferential());
				dto.setPlatformPreferential(vo.getPlatformPreferential());
				dto.setStorePreferential(vo.getStorePreferential());
				dto.setDeliveryType(vo.getDeliveryType());
				if(vo.getStatus()!=null){
					dto.setStatus(vo.getStatus().ordinal());
				}
				if (vo.getPayWay() == PayWayEnum.OFFLINE_CONFIRM_AND_PAY) {
					dto.setPayType(3);
				} else {
					// Begin 重构4.1 add by wusw 20160726
					if (vo.getPayType() != null) {
						dto.setPayType(vo.getPayType().ordinal());
					} else {// Begin 12170 add by wusw 20160806
						// Begin add by zhulq 2017-03-01 充值未付款 如果不重新赋值dto的支付类型直接默认是0
						dto.setPayType(8);
						// Begin add by zhulq 2017-03-01
						if (vo.getType() == OrderTypeEnum.PHYSICAL_ORDER && vo.getPayWay() == PayWayEnum.CASH_DELIERY) {
							dto.setPayType(4);
						}
					} // End 12170 add by wusw 20160806
						// End 重构4.1 add by wusw 20160726
				}

				// 获取邀请人姓名
				if (CollectionUtils.isNotEmpty(inviteNameLists)) {
					for (SysUserInvitationLoginNameVO loginNameVO : inviteNameLists) {
						if (StringUtils.isNotEmpty(loginNameVO.getsLoginName())
								&& StringUtils.isNotEmpty(loginNameVO.getUserId())
								&& StringUtils.isNotEmpty(vo.getUserId())
								&& vo.getUserId().equals(loginNameVO.getUserId())) {
							dto.setInviteName(loginNameVO.getsLoginName());
						}

						if (StringUtils.isNotEmpty(loginNameVO.getbLoginName())
								&& StringUtils.isNotEmpty(loginNameVO.getUserId())
								&& StringUtils.isNotEmpty(vo.getUserId())
								&& vo.getUserId().equals(loginNameVO.getUserId())) {
							dto.setInviteName(loginNameVO.getbLoginName());
						}
					}
				}

				// 接单时间
				if (vo.getAcceptTime() != null) {
					dto.setAcceptTime(vo.getAcceptTime());
				}

				// 发货时间
				if (vo.getDeliveryTime() != null) {
					dto.setDeliveryTime(vo.getDeliveryTime());
				}

				// 收货时间
				if (vo.getReceivedTime() != null) {
					dto.setReceivedTime(vo.getReceivedTime());
				}

				// 收入
				dto.setIncome(vo.getIncome());

				// 优惠类型
				if (vo.getActivityType().ordinal() != 0) {
					if (vo.getIncome() != null && vo.getActualAmount().compareTo(vo.getIncome()) == -1) {
						dto.setPreferentialType(PreferentialType.PLATFORM.getValue());// 0为平台优惠
					} else if (vo.getIncome() != null && vo.getActualAmount().compareTo(vo.getIncome()) == 0) {
						dto.setPreferentialType(PreferentialType.STORE.getValue());// 1为店铺优惠
					}
				}

				// 活动类型
				ActivityInfoVO activityInfoVO = activityMap.get(vo.getId());
				if (activityInfoVO != null) {
					if (activityInfoVO.getActivityType() != null) {
						dto.setActivityType(activityInfoVO.getActivityType().getValue());
					}
					if (StringUtils.isNotEmpty(activityInfoVO.getActivityName())) {
						dto.setActivityName(activityInfoVO.getActivityName());
					}
				}
				// begin add by zhulq 充值订单 只要平台优惠 只能用代金卷
				if (vo.getType() == OrderTypeEnum.PHONE_PAY_ORDER || vo.getType() == OrderTypeEnum.TRAFFIC_PAY_ORDER) {
					String activityId = vo.getActivityId();
					if (StringUtils.isNotEmpty(activityId) && !"0".equals(activityId)) {
						dto.setPreferentialType(PreferentialType.PLATFORM.getValue());
						dto.setActivityType(OrderActivityType.coupons.getValue());
					}
				}

				if (StringUtils.isNotEmpty(vo.getId()) && (vo.getType() == OrderTypeEnum.PHYSICAL_ORDER
						|| vo.getType() == OrderTypeEnum.STORE_CONSUME_ORDER)) {
					if (CollectionUtils.isNotEmpty(tradeOrderRefundsList)) {
						BigDecimal refundPrice = new BigDecimal("0");
						BigDecimal refundPreferentialPrice = new BigDecimal("0");
						for (TradeOrderRefunds tradeOrderRefunds : tradeOrderRefundsList) {
							if (StringUtils.isNotEmpty(tradeOrderRefunds.getOrderId())
									&& StringUtils.isNotEmpty(vo.getId())
									&& vo.getId().equals(tradeOrderRefunds.getOrderId())) {
								if (tradeOrderRefunds.getTotalAmount() != null) {
									refundPrice = refundPrice.add(tradeOrderRefunds.getTotalAmount());
									// 退款总金额
									dto.setRefundPrice(refundPrice);
								}
								if (tradeOrderRefunds.getTotalPreferentialPrice() != null) {
									refundPreferentialPrice = refundPreferentialPrice
											.add(tradeOrderRefunds.getTotalPreferentialPrice());
									// 退款优惠金额
									dto.setRefundPreferentialPrice(refundPreferentialPrice);
								}
							}
						}
					}
					if (dto.getRefundPrice() != null && dto.getRefundPrice().compareTo(new BigDecimal(0)) == 1) {
						dto.setIsRefundsType(WhetherEnum.whether.getValue());
					} else {
						dto.setIsRefundsType(WhetherEnum.not.getValue());
					}
				}

				String lProviceName = vo.getlProviceName() == null ? "" : vo.getlProviceName();
				String lCityName = vo.getlCityName() == null ? "" : vo.getlCityName();
				String lAreaName = vo.getlAreaName() == null ? "" : vo.getlAreaName();
				String areaExt = vo.getlAreaExt() == null ? "" : vo.getlAreaExt();

				// 定位基点
				dto.setLocation(lProviceName + lCityName + lAreaName + areaExt);

				// begin V2.1.0 added by zhulq 2017-03-22 订单的收货地址
				setAddressInfo(vo, dto, memberAddressMap, logisticsMap, addressMap);
				// End V2.1.0 added by zhulq 2017-03-22

				// 订单来源
				dto.setOrderResource(vo.getOrderResource().ordinal());

				// End V2.1.0 added by luosm 2017-02-18

				// begin add by zhulq 充值订单所属城市和完成时间设置
				dto.setLocateCityName(vo.getCityName());
				dto.setCompleteTime(vo.getUpdateTime());
				// begin add by zhulq 充值订单所属城市和完成时间设置
				result.add(dto);
			}
		}
		return result;
	}
	// Begin v1.1.0 add by zengjz 20160912

	@Override
	public Map<String, Object> statisOrderByParams(TradeOrderQueryParamDto tradeOrderQueryParamDto) {
		// 参数转换处理（例如订单状态）
		Map<String, Object> result = tradeOrderService.statisOrderForFinanceByParams(tradeOrderQueryParamDto);
		return result;
	}

	@Override
	public Map<String, Object> statisOrderCannelRefundByParams(Map<String, Object> params) {

		return tradeOrderService.statisOrderCannelRefundByParams(params);
	}
	// End v1.1.0 add by zengjz 20160912


	@Override
	public int queryOrderCountByParams(TradeOrderQueryParamDto tradeOrderQueryParamDto) throws Exception {
		return tradeOrderService.findOrderCountForFinanceByParams(tradeOrderQueryParamDto);
	}
	
}
