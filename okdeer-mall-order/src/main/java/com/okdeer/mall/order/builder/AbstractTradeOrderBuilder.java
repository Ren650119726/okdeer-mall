package com.okdeer.mall.order.builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.springframework.util.StringUtils;

import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.common.utils.TradeNumUtil;
import com.okdeer.mall.order.bo.CurrentStoreSkuBo;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.PlaceOrderItemDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderInvoice;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.enums.AppraiseEnum;
import com.okdeer.mall.order.enums.CompainStatusEnum;
import com.okdeer.mall.order.enums.OrderIsShowEnum;
import com.okdeer.mall.order.enums.OrderItemStatusEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.PaymentStatusEnum;
import com.okdeer.mall.order.enums.WithInvoiceEnum;
import com.okdeer.mall.order.service.GenerateNumericalService;

/**
 * ClassName: AbstractTradeOrderBoBuilder 
 * @Description: 订单Bo构建者
 * @author maojj
 * @date 2016年12月22日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿1.2.5		2016年12月22日				maojj			 订单Bo构建者
 */
public abstract class AbstractTradeOrderBuilder {
	
	/**
	 * 生成编号的service
	 */
	@Resource
	protected GenerateNumericalService generateNumericalService;

	/**
	 * @Description: 构建TradeOrderBo
	 * @param placeOrderDto
	 * @return   
	 * @author maojj
	 * @date 2016年12月22日
	 */
	public TradeOrder build(PlaceOrderParamDto paramDto) throws Exception{
		// 构建交易订单
		TradeOrder tradeOrder = buildTradeOrder(paramDto);
		// 构建交易订单项
		List<TradeOrderItem> orderItemList = buildOrderItemList(tradeOrder, paramDto);
		// 构建订单发票信息
		TradeOrderInvoice tradeOrderInvoice = buildTradeOrderInvoice(tradeOrder, paramDto);
		
		tradeOrder.setTradeOrderItem(orderItemList);
		tradeOrder.setTradeOrderInvoice(tradeOrderInvoice);
		// 构建订单物流
		return tradeOrder;
	}
	
	/**
	 * @Description: 构建TradeOrder
	 * @param placeOrderDto   
	 * @author maojj
	 * @date 2016年12月22日
	 */
	public TradeOrder buildTradeOrder(PlaceOrderParamDto paramDto) throws Exception{
		TradeOrder tradeOrder  = new TradeOrder();
		tradeOrder.setId(UuidUtils.getUuid());
		tradeOrder.setUserId(paramDto.getUserId());
		tradeOrder.setUserPhone(paramDto.getUserPhone());
		tradeOrder.setStoreId(paramDto.getStoreId());
		tradeOrder.setStoreName(((StoreInfo)paramDto.get("storeInfo")).getStoreName());
		tradeOrder.setSellerId(paramDto.getStoreId());
		tradeOrder.setType(paramDto.getSkuType());
		tradeOrder.setPid("0");
		tradeOrder.setActivityType(paramDto.getActivityType());
		tradeOrder.setActivityId(paramDto.getActivityId());
		tradeOrder.setActivityItemId(paramDto.getActivityItemId());
		tradeOrder.setRemark(paramDto.getRemark());
		tradeOrder.setInvoice(paramDto.getIsInvoice());
		tradeOrder.setOrderResource(paramDto.getChannel());
		tradeOrder.setIsShow(OrderIsShowEnum.yes);
		tradeOrder.setPaymentStatus(PaymentStatusEnum.STAY_BACK);
		tradeOrder.setCompainStatus(CompainStatusEnum.NOT_COMPAIN);
		tradeOrder.setTradeNum(TradeNumUtil.getTradeNum());
		tradeOrder.setDisabled(Disabled.valid);
		tradeOrder.setCreateTime(new Date());
		tradeOrder.setUpdateTime(new Date());
		// 设置订单编号
		setOrderNo(tradeOrder);
		// 设置订单项列表.统计商品的总数量、总种类、总金额
		parseOrderItemList(tradeOrder,paramDto.getSkuList());
		// 设置订单状态
		setOrderStatus(tradeOrder,paramDto.getPayType());
		// 设置支付方式
		setPayWay(tradeOrder,paramDto.getPayType());
		// 设置提货类型和提货时间
		setPickUpTypeAndTime(tradeOrder,paramDto);
		// 设置送货地址Id
		setPickUpId(tradeOrder,paramDto);
		// 解析优惠活动
		parseFavour(tradeOrder, paramDto);
		
		return tradeOrder;
	}
	
	/**
	 * @Description: 设置订单编号
	 * @param tradeOrder   
	 * @author maojj
	 * @date 2016年12月22日
	 */
	public abstract void setOrderNo(TradeOrder tradeOrder) throws Exception;
	
	/**
	 * @Description: 解析下单购买的商品列表，统计商品的总数量、总种类、总金额
	 * @param tradeOrder
	 * @param orderItemList   
	 * @author maojj
	 * @date 2016年12月22日
	 */
	public void parseOrderItemList(TradeOrder tradeOrder, List<PlaceOrderItemDto> orderItemList)  {
		// 商品总数量
		Integer totalQuantity = Integer.valueOf(0);
		// 商品总金额
		BigDecimal totalAmount = BigDecimal.valueOf(0);
		for (PlaceOrderItemDto itemDto : orderItemList) {
			totalQuantity += itemDto.getQuantity() + itemDto.getSkuActQuantity();
			totalAmount =  totalAmount.add(itemDto.getTotalAmount());
		}
		// 设置订单总品项
		tradeOrder.setTotalKind(orderItemList.size());
		// 设置订单总数量
		tradeOrder.setTotalQuantity(totalQuantity);
		// 设置订单总金额
		tradeOrder.setTotalAmount(totalAmount);
	}
	
	/**
	 * @Description: 设置订单状态
	 * @param tradeOrder
	 * @param payType   
	 * @author maojj
	 * @date 2016年12月22日
	 */
	public void setOrderStatus(TradeOrder tradeOrder,int payType){
		tradeOrder.setStatus(OrderStatusEnum.UNPAID);
	}
	
	/**
	 * @Description: 设置支付方式
	 * @param tradeOrder
	 * @param payType   
	 * @author maojj
	 * @date 2016年12月22日
	 */
	public void setPayWay(TradeOrder tradeOrder,int payType){
		tradeOrder.setPayWay(PayWayEnum.PAY_ONLINE);
	}
	
	/**
	 * @Description: 设置提货类型和提货时间
	 * @param tradeOrder
	 * @param placeOrderDto   
	 * @author maojj
	 * @date 2016年12月22日
	 */
	public abstract void setPickUpTypeAndTime(TradeOrder tradeOrder,PlaceOrderParamDto paramDto) throws Exception;
	
	/**
	 * @Description: 设置提货人Id
	 * @param tradeOrder
	 * @param placeOrderDto   
	 * @author maojj
	 * @date 2016年12月22日
	 */
	public abstract void setPickUpId(TradeOrder tradeOrder,PlaceOrderParamDto paramDto);
	
	/**
	 * @Description: 解析优惠设置优惠金额和收入
	 * @param tradeOrder
	 * @param placeOrderDto   
	 * @author maojj
	 * @date 2016年12月22日
	 */
	public abstract void parseFavour(TradeOrder tradeOrder,PlaceOrderParamDto paramDto) throws ServiceException;
	
	/**
	 * @Description: 构建交易订单项
	 * @param tradeOrder
	 * @param reqDto
	 * @return
	 * @throws ServiceException   
	 * @author maojj
	 * @date 2017年1月5日
	 */
	private List<TradeOrderItem> buildOrderItemList(TradeOrder tradeOrder, PlaceOrderParamDto paramDto)
			throws ServiceException {
		List<TradeOrderItem> orderItemList = new ArrayList<TradeOrderItem>();
		StoreSkuParserBo parserBo = (StoreSkuParserBo)paramDto.get("parserBo");
		
		String orderId = tradeOrder.getId();
		// 订单项总金额
		BigDecimal totalAmount = parserBo.getTotalItemAmount();
		BigDecimal totalFavour = tradeOrder.getPreferentialPrice();
		BigDecimal favourSum = new BigDecimal("0.00");
		int index = 0;
		int itemSize = paramDto.getSkuList().size();
		TradeOrderItem tradeOrderItem = null;

		Collection<CurrentStoreSkuBo> skuBoSet = parserBo.getCurrentSkuMap().values();
		List<CurrentStoreSkuBo> goodsItemList = new ArrayList<CurrentStoreSkuBo>();
		goodsItemList.addAll(skuBoSet);
		ComparatorChain chain = new ComparatorChain();
		chain.addComparator(new BeanComparator("skuPrice"), false);
		Collections.sort(goodsItemList, chain);

		for (CurrentStoreSkuBo skuBo : goodsItemList) {
			tradeOrderItem = new TradeOrderItem();

			tradeOrderItem.setId(UuidUtils.getUuid());
			tradeOrderItem.setOrderId(orderId);
			tradeOrderItem.setStoreSpuId(skuBo.getStoreSpuId());
			tradeOrderItem.setStoreSkuId(skuBo.getId());
			tradeOrderItem.setSkuName(skuBo.getName());
			tradeOrderItem.setPropertiesIndb(skuBo.getPropertiesIndb());
			tradeOrderItem.setMainPicPrl(skuBo.getMainPicUrl());
			tradeOrderItem.setSpuType(skuBo.getSpuType());
			tradeOrderItem.setUnitPrice(skuBo.getOnlinePrice());
			tradeOrderItem.setQuantity(skuBo.getQuantity());
			tradeOrderItem.setActivityPrice(skuBo.getActPrice());
			tradeOrderItem.setActivityQuantity(skuBo.getSkuActQuantity());
			tradeOrderItem.setStatus(OrderItemStatusEnum.NO_REFUND);
			tradeOrderItem.setCompainStatus(CompainStatusEnum.NOT_COMPAIN);
			tradeOrderItem.setAppraise(AppraiseEnum.NOT_APPROPRIATE);
			tradeOrderItem.setCreateTime(new Date());
			tradeOrderItem.setServiceAssurance(
					StringUtils.isEmpty(skuBo.getGuaranteed()) ? 0 : Integer.valueOf(skuBo.getGuaranteed()));
			tradeOrderItem.setActivityType(paramDto.getActivityType().ordinal());
			tradeOrderItem.setActivityId(paramDto.getActivityId());
			tradeOrderItem.setBarCode(skuBo.getBarCode());
			tradeOrderItem.setStyleCode(skuBo.getStyleCode());

			// 订单项总金额
			BigDecimal totalAmountOfItem = skuBo.getTotalAmount();
			tradeOrderItem.setTotalAmount(totalAmountOfItem);
			// 计算订单项优惠金额
			BigDecimal favourItem = BigDecimal.valueOf(0.0);
			if (paramDto.getActivityType() != ActivityTypeEnum.NO_ACTIVITY) {
				if (index++ < itemSize - 1) {
					favourItem = totalAmountOfItem.multiply(totalFavour).divide(totalAmount, 2, BigDecimal.ROUND_FLOOR);
					if (favourItem.compareTo(totalAmountOfItem) == 1) {
						favourItem = totalAmountOfItem;
					}
					favourSum = favourSum.add(favourItem);
				} else {
					favourItem = totalFavour.subtract(favourSum);
					if (favourItem.compareTo(totalAmountOfItem) == 1) {
						favourItem = totalAmountOfItem;
					}
				}
			}
			// 设置优惠金额
			tradeOrderItem.setPreferentialPrice(favourItem);
			// 设置实付金额
			tradeOrderItem.setActualAmount(totalAmountOfItem.subtract(favourItem));
			// 设置订单项收入
			setOrderItemIncome(tradeOrderItem, tradeOrder);
			orderItemList.add(tradeOrderItem);
		}

		return orderItemList;
	}
	
	public abstract void setOrderItemIncome(TradeOrderItem tradeOrderItem, TradeOrder tradeOrder);
	
	public TradeOrderInvoice buildTradeOrderInvoice(TradeOrder tradeOrder, PlaceOrderParamDto paramDto){
		TradeOrderInvoice orderInvoice = null;
		if (paramDto.getIsInvoice() == WithInvoiceEnum.HAS) {
			orderInvoice = new TradeOrderInvoice();
			// 有发票
			orderInvoice.setId(UuidUtils.getUuid());
			orderInvoice.setOrderId(tradeOrder.getId());
			orderInvoice.setHead(paramDto.getInvoiceHead());
			orderInvoice.setContext(paramDto.getInvoiceContent());
			tradeOrder.setTradeOrderInvoice(orderInvoice);
		}
		return orderInvoice;
	}
}