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

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.okdeer.archive.store.entity.StoreMemberRelation;
import com.okdeer.archive.store.service.IStoreMemberRelationServiceApi;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.archive.system.entity.SysUser;
import com.okdeer.archive.system.service.ISysUserServiceApi;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderInvoice;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.entity.TradeOrderLogistics;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.enums.ActivityBelongType;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.WithInvoiceEnum;
import com.okdeer.mall.order.service.TradeOrderActivityService;
import com.okdeer.mall.order.service.TradeOrderItemService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.vo.ERPTradeOrderVo;
import com.okdeer.mall.order.vo.TradeOrderOperateParamVo;
import com.okdeer.mall.order.vo.TradeOrderPayQueryVo;
import com.okdeer.mall.order.dto.ERPTradeOrderVoDto;
import com.okdeer.mall.order.dto.TradeOrderDto;
import com.okdeer.mall.order.dto.TradeOrderInvoiceDto;
import com.okdeer.mall.order.dto.TradeOrderItemDetailDto;
import com.okdeer.mall.order.dto.TradeOrderItemDto;
import com.okdeer.mall.order.dto.TradeOrderLogisticsDto;
import com.okdeer.mall.order.dto.TradeOrderPayDto;
import com.okdeer.mall.order.dto.TradeOrderPayQueryDto;
import com.okdeer.mall.order.dto.TradeOrderQueryDto;
import com.okdeer.mall.order.exception.ExceedRangeException;
import com.okdeer.mall.order.service.ITradeOrderServiceApi;
import com.okdeer.mall.common.vo.PageResultVo;

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
		if (tradeOrder.getTradeOrderItem() != null) {
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
		OrderStatusEnum orderStatus = OrderStatusEnum.enumNameOf(status);
		if (orderStatus == null) {
			throw new Exception("订单状态status为空或名字错误异常");
		}
		tradeOrder.setCurrentStatus(tradeOrder.getStatus());
		tradeOrder.setStatus(orderStatus);
		if (StringUtils.isNotEmpty(reason)) {
			tradeOrder.setReason(reason);
		}
		if (tradeOrder.getStatus() == OrderStatusEnum.CANCELED) {
			this.tradeOrderService.updateCancelOrder(tradeOrder, false);
		} else if (tradeOrder.getStatus() == OrderStatusEnum.HAS_BEEN_SIGNED) {
			this.tradeOrderService.updateWithConfirm(tradeOrder);
		} else if (tradeOrder.getStatus() == OrderStatusEnum.REFUSED) {
			this.tradeOrderService.updateWithUserRefuse(tradeOrder);
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
		logger.info("确认收货订单同步状态" + "，orderId:" + orderId + "，status:" + status + "，reason:" + reason + "userId"
				+ userId);
		TradeOrder tradeOrder = tradeOrderService.selectById(orderId);
		OrderStatusEnum orderStatus = OrderStatusEnum.enumNameOf(status);
		if (orderStatus == null) {
			throw new Exception("订单状态status为空或名字错误异常");
		}
		tradeOrder.setCurrentStatus(tradeOrder.getStatus());
		tradeOrder.setStatus(orderStatus);
		if (StringUtils.isNotEmpty(reason)) {
			tradeOrder.setReason(reason);
		}
		if (StringUtils.isNotEmpty(userId)) {
			tradeOrder.setUpdateUserId(userId);
		}
		tradeOrder.setUpdateTime(new Date());

		if (tradeOrder.getStatus() == OrderStatusEnum.CANCELED) {
			this.tradeOrderService.updateCancelOrder(tradeOrder, false);
		} else if (tradeOrder.getStatus() == OrderStatusEnum.HAS_BEEN_SIGNED) {
			this.tradeOrderService.updateWithConfirm(tradeOrder);
		} else if (tradeOrder.getStatus() == OrderStatusEnum.REFUSED) {
			this.tradeOrderService.updateWithUserRefuse(tradeOrder);
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
		if("0".equals(type)){
			typeList.add(OrderTypeEnum.PHYSICAL_ORDER);
		}else if("1".equals(type)){
			typeList.add(OrderTypeEnum.SERVICE_ORDER);
		}else if("2".equals(type)){
			typeList.add(OrderTypeEnum.STORE_CONSUME_ORDER);
		}else if("3".equals(type)){
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
		// TODO Auto-generated method stub
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
		tradeOrderDto.setStoreType(order.getStoreType().ordinal());
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
		 * TradeOrderItemDto tradeOrderItemDto = new TradeOrderItemDto();
		 * TradeOrderItem tradeOrderItem = order.getTradeOrderItem().get(0);
		 * tradeOrderItemDto.setMainPicPrl(tradeOrderItem.getMainPicPrl());
		 * tradeOrderItemDto.setSkuName(tradeOrderItem.getSkuName());
		 * tradeOrderItemDto.setUnitPrice(tradeOrderItem.getUnitPrice());
		 * tradeOrderItemDto.setQuantity(tradeOrderItem.getQuantity());
		 * tradeOrderItemDto.setTotalAmount(tradeOrderItem.getTotalAmount());
		 */
		// Begin 12051 add by wusw 20160811
		BigDecimal totalAmount = new BigDecimal(0.00);
		// End 12051 add by wusw 20160811
		List<TradeOrderItemDto> itemDtoList = new ArrayList<TradeOrderItemDto>();
		if (order.getTradeOrderItem() != null) {
			for (TradeOrderItem item : order.getTradeOrderItem()) {
				TradeOrderItemDto tradeOrderItemDto = new TradeOrderItemDto();
				tradeOrderItemDto.setMainPicPrl(item.getMainPicPrl());
				tradeOrderItemDto.setSkuName(item.getSkuName());
				tradeOrderItemDto.setPropertiesIndb(item.getPropertiesIndb());
				tradeOrderItemDto.setUnitPrice(item.getUnitPrice());
				tradeOrderItemDto.setQuantity(item.getQuantity());
				tradeOrderItemDto.setTotalAmount(item.getTotalAmount());
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
		// TODO Auto-generated method stub
		TradeOrderDto tradeOrderDto = new TradeOrderDto();
		TradeOrder order = tradeOrderService.erpSelectByServiceOrderId(orderId);

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
		// Begin 12051 add by wusw 20160811
		BigDecimal totalAmount = new BigDecimal(0.00);
		// End 12051 add by wusw 20160811
		// 商品信息
		List<TradeOrderItemDto> itemDtoList = new ArrayList<TradeOrderItemDto>();
		if (order.getTradeOrderItem() != null) {
			for (TradeOrderItem item : order.getTradeOrderItem()) {
				TradeOrderItemDto tradeOrderItemDto = new TradeOrderItemDto();
				tradeOrderItemDto.setMainPicPrl(item.getMainPicPrl());
				tradeOrderItemDto.setSkuName(item.getSkuName());
				tradeOrderItemDto.setPropertiesIndb(item.getPropertiesIndb());
				tradeOrderItemDto.setUnitPrice(item.getUnitPrice());
				tradeOrderItemDto.setQuantity(item.getQuantity());
				tradeOrderItemDto.setTotalAmount(item.getTotalAmount());
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
				dto.setRefundAmount(vo.getRefundAmount());
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
	public List<TradeOrderPayQueryDto> findListByStatusPayType(Map<String, Object> params) throws ExceedRangeException,
			Exception {
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
				dto.setRefundAmount(vo.getRefundAmount());
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
				dto.setTotalFee(vo.getRealMoney().multiply(new BigDecimal("100")).intValue());
				dto.setRefundFee(vo.getRefundAmount().multiply(new BigDecimal("100")).intValue());

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
	public PageResultVo<ERPTradeOrderVoDto> findOrderByParams(Map<String, Object> params) throws Exception {
		int pageSize = Integer.valueOf(params.get("pageSize").toString());
		int pageNumber = Integer.valueOf(params.get("pageNumber").toString());
		PageUtils<ERPTradeOrderVo> page = tradeOrderService.findOrderForFinanceByParams(params, pageNumber, pageSize);
		List<ERPTradeOrderVoDto> dtoList = new ArrayList<ERPTradeOrderVoDto>();
		for (ERPTradeOrderVo vo : page.getList()) {
			ERPTradeOrderVoDto dto = new ERPTradeOrderVoDto();
			dto.setId(vo.getId());
			dto.setOrderNo(vo.getOrderNo());
			dto.setType(vo.getType().ordinal());
			dto.setStoreName(vo.getStoreName());
			dto.setUserPhone(vo.getUserPhone());
			dto.setTotalAmount(vo.getTotalAmount());
			dto.setActualAmount(vo.getActualAmount());
			dto.setPreferentialPrice(vo.getPreferentialPrice());
			dto.setCreateTime(vo.getCreateTime());
			if (vo.getOrderResource() != null) {
				if (vo.getOrderResource() == OrderResourceEnum.YSCAPP
						|| vo.getOrderResource() == OrderResourceEnum.WECHAT) {
					dto.setOrderResource(0);
				} else {
					dto.setOrderResource(1);
				}
			}
			dto.setStatus(OrderStatusEnum.convertStatusView(vo.getType(), vo.getStatus()));
			if (vo.getPayWay() == PayWayEnum.OFFLINE_CONFIRM_AND_PAY) {
				dto.setPayType(3);
			} else {
				// Begin 重构4.1 add by wusw 20160726
				if (vo.getPayType() != null) {
					dto.setPayType(vo.getPayType().ordinal());
				} else {// Begin 12170 add by wusw 20160806
					if (vo.getType() == OrderTypeEnum.PHYSICAL_ORDER && vo.getPayWay() == PayWayEnum.CASH_DELIERY) {
						dto.setPayType(4);
					}
				}// End 12170 add by wusw 20160806
					// End 重构4.1 add by wusw 20160726
			}
			dtoList.add(dto);
		}
		PageResultVo<ERPTradeOrderVoDto> result = new PageResultVo<ERPTradeOrderVoDto>(page.getPageNum(),
				page.getPageSize(), page.getTotal(), dtoList);
		return result;
	}

	// End 重构4.1 add by wusw 20160719

	/**
	 * (non-Javadoc)
	 * @see com.yschome.api.mall.order.service.ITradeOrderServiceApi#findOrderListByParams(java.util.Map)
	 */
	@Override
	public List<ERPTradeOrderVoDto> findOrderListByParams(Map<String, Object> params) throws Exception {
		List<ERPTradeOrderVo> list = tradeOrderService.findOrderListForFinanceByParams(params);
		if (list != null && list.size() > RECORD_NUM) {
			throw new ExceedRangeException("查询导出订单列表超过一万条", new Throwable());
		}
		List<ERPTradeOrderVoDto> result = new ArrayList<ERPTradeOrderVoDto>();
		for (ERPTradeOrderVo vo : list) {
			ERPTradeOrderVoDto dto = new ERPTradeOrderVoDto();
			dto.setId(vo.getId());
			dto.setOrderNo(vo.getOrderNo());
			// Begin 重构4.1 add by wusw 20160728
			dto.setType(vo.getType().ordinal());
			// Begin 重构4.1 add by wusw 20160728
			dto.setStoreName(vo.getStoreName());
			dto.setUserPhone(vo.getUserPhone());
			dto.setTotalAmount(vo.getTotalAmount());
			dto.setActualAmount(vo.getActualAmount());
			dto.setPreferentialPrice(vo.getPreferentialPrice());
			// Begin 重构4.1 add by wusw 20160725
			dto.setCreateTime(vo.getCreateTime());
			if (vo.getOrderResource() != null) {
				if (vo.getOrderResource() == OrderResourceEnum.YSCAPP
						|| vo.getOrderResource() == OrderResourceEnum.WECHAT) {
					dto.setOrderResource(0);
				} else {
					dto.setOrderResource(1);
				}
			}
			// End 重构4.1 add by wusw 20160725
			dto.setStatus(OrderStatusEnum.convertStatusView(vo.getType(), vo.getStatus()));
			if (vo.getPayWay() == PayWayEnum.OFFLINE_CONFIRM_AND_PAY) {
				dto.setPayType(3);
			} else {
				// Begin 重构4.1 add by wusw 20160726
				if (vo.getPayType() != null) {
					dto.setPayType(vo.getPayType().ordinal());
				} else {// Begin 12170 add by wusw 20160806
					if (vo.getType() == OrderTypeEnum.PHYSICAL_ORDER && vo.getPayWay() == PayWayEnum.CASH_DELIERY) {
						dto.setPayType(4);
					}
				}// End 12170 add by wusw 20160806
					// End 重构4.1 add by wusw 20160726
			}
			result.add(dto);
		}
		return result;
	}

	// Begin v1.1.0 add by zengjz 20160912

	@Override
	public Map<String, Object> statisOrderByParams(Map<String, Object> params) {
		// 参数转换处理（例如订单状态）
		Map<String, Object> result = tradeOrderService.statisOrderForFinanceByParams(params);
		return result;
	}

	@Override
	public Map<String, Object> statisOrderCannelRefundByParams(Map<String, Object> params) {

		return tradeOrderService.statisOrderCannelRefundByParams(params);
	}
	// End v1.1.0 add by zengjz 20160912
}
