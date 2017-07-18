package com.okdeer.mall.order.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Lists;
import com.okdeer.archive.goods.spu.enums.SpuTypeEnum;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.jxc.common.constant.TradeOrderMQMessage;
import com.okdeer.jxc.onlineorder.entity.OnlineOrder;
import com.okdeer.jxc.onlineorder.entity.OnlineOrderItem;
import com.okdeer.jxc.onlineorder.vo.OnlineOrderVo;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.order.bo.ComboSnapshotAdapter;
import com.okdeer.mall.order.bo.TradeOrderContext;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderComboSnapshot;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderLogistics;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.PaymentStatusEnum;
import com.okdeer.mall.order.mapper.TradeOrderComboSnapshotMapper;
import com.okdeer.mall.order.service.TradeOrderItemService;
import com.okdeer.mall.order.service.TradeOrderLogisticsService;
import com.okdeer.mall.order.service.TradeOrderPayService;
import com.okdeer.mall.order.service.TradeorderProcessLister;

@Service("jxcSynTradeorderProcessLister")
public class JxcSynTradeorderProcessLister implements TradeorderProcessLister {

	@Autowired
	private RocketMQProducer rocketMQProducer;
	@Autowired
	private TradeOrderPayService tradeOrderPayService;
	@Autowired
	private TradeOrderItemService tradeOrderItemService;
	@Autowired
	private TradeOrderLogisticsService tradeOrderLogisticsService;
	@Reference(version="1.0.0")
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;
	@Resource
	private TradeOrderComboSnapshotMapper tradeOrderComboSnapshotMapper;
	@Resource
	private ComboSnapshotAdapter comboSnapshotAdapter;
	
	private static final Logger log = LoggerFactory.getLogger(ServiceOrderProcessServiceImpl.class);

	@Override
	public void tradeOrderStatusChange(TradeOrderContext tradeOrderContext) {
		try{
			log.info("进入同步erp消息 订单号:" + tradeOrderContext.getTradeOrder().getOrderNo());
			//只同步实物订单
			if(tradeOrderContext.getTradeOrder().getType() != OrderTypeEnum.PHYSICAL_ORDER){
				return;
			}
			
			if(tradeOrderContext.getTradeOrder().getStatus() == OrderStatusEnum.DROPSHIPPING ){
				sendMQMessage(TradeOrderMQMessage.TOPIC_ORDER_SYNC,buildOnlineOrder(tradeOrderContext),
					tradeOrderContext.getTradeOrder().getId(),tradeOrderContext.getTradeOrder().getStatus().ordinal());
			}else if(tradeOrderContext.getTradeOrder().getStatus() == OrderStatusEnum.TO_BE_SIGNED ||
					tradeOrderContext.getTradeOrder().getStatus() == OrderStatusEnum.REFUSED || 
					tradeOrderContext.getTradeOrder().getStatus() == OrderStatusEnum.CANCELED || 
							tradeOrderContext.getTradeOrder().getStatus() == OrderStatusEnum.HAS_BEEN_SIGNED){
				sendMQMessage(TradeOrderMQMessage.TOPIC_ORDER_UPDATE_SYNC,buildOnlineOrderVo(tradeOrderContext),
						tradeOrderContext.getTradeOrder().getId(),tradeOrderContext.getTradeOrder().getStatus().ordinal());
			}
		} catch(Exception e){
			log.error("",e);
		}
	}
	
	
	private <T> void sendMQMessage(String topic,T obj,String orderId,Integer orderStatus) throws Exception{
		MQMessage message = new MQMessage(topic, (Serializable) obj);
		//加一个key 订单id+状态,没有实际意义,方便查询定位错误
		message.setKey(orderId + "" + orderStatus);
		rocketMQProducer.sendMessage(message);
	}
	
	private OnlineOrder buildOnlineOrder(TradeOrderContext tradeOrderContext) throws Exception{
		OnlineOrder vo = new OnlineOrder();
		TradeOrder order = tradeOrderContext.getTradeOrder();
		
		//除了订单主对象,其他信息传过来的时候可能为空,就再查一遍
		//订单支付对象
		TradeOrderPay tradeOrderPay = tradeOrderContext.getTradeOrderPay();
		if(tradeOrderPay == null){
			tradeOrderPay = tradeOrderPayService.selectByOrderId(order.getId());
		}
		//订单物流对象
		TradeOrderLogistics tradeOrderLogistics = tradeOrderContext.getTradeOrderLogistics();
		if(tradeOrderLogistics == null){
			tradeOrderLogistics = tradeOrderLogisticsService.findByOrderId(order.getId());
		}
		//订单项列表
		List<TradeOrderItem> itemList = tradeOrderContext.getItemList();
		if(CollectionUtils.isEmpty(itemList)){
			
			List<String> orderIds = new ArrayList<String>();
			orderIds.add(order.getId());
			itemList = tradeOrderItemService.findOrderItems(orderIds);
			
			//拆分订单项
			splitItemList(itemList,order.getId());
			
			List<String> goodsStoreSkuIdList = new ArrayList<String>();
			for(TradeOrderItem item : itemList){
				goodsStoreSkuIdList.add(item.getStoreSkuId());
			}
			//需要得到skuId,把idlist一次拉出来,然后用in的方式
			List<GoodsStoreSku> goodsStoreSkuList = goodsStoreSkuServiceApi.findByIds(goodsStoreSkuIdList);
			for(TradeOrderItem item : itemList){
				for(GoodsStoreSku sku : goodsStoreSkuList){
					if(item.getStoreSkuId().equals(sku.getId())){
						item.setGoodsSkuId(sku.getSkuId());
						break;
					}
				}
			}
		}
		
		//按照文档上的属性一一设置
		vo.setId(order.getId());
		vo.setStoreId(order.getStoreId());
		vo.setOrderNo(order.getOrderNo());
		vo.setSaleType("A");//A销售单 B退货单
		vo.setOrderResource(order.getOrderResource().ordinal());
		vo.setTotalAmount(order.getTotalAmount());
		vo.setActualAmount(order.getActualAmount());
		// 店铺优惠
		vo.setDiscountAmount(order.getStorePreferential());
		// 平台优惠 = 优惠总金额-店铺优惠-运费优惠
		BigDecimal platFavoutAmount = order.getPreferentialPrice().subtract(order.getStorePreferential()).subtract(order.getRealFarePreferential());
		vo.setPlatDiscountAmount(platFavoutAmount);
		vo.setFare(order.getFare());
		vo.setUserId(order.getUserId());
		vo.setPickUpCode(order.getPickUpCode());
		vo.setRemark(order.getRemark());
		vo.setCreateTime(order.getCreateTime());
		vo.setPayWay(order.getPayWay().ordinal());
		vo.setPayType((tradeOrderPay == null || tradeOrderPay.getPayType() == null) ? 4 : tradeOrderPay.getPayType().ordinal());//如果为空,就是4现金
		vo.setTradeNum(order.getTradeNum());
		
		// 进销存那边的优惠类型0:无活动 ;1：代金券；2：其他
		int activityType = 0;
		// 活动类型为代金券活动
		if (order.getActivityType() == ActivityTypeEnum.VONCHER) {
			activityType = 1;
		} else if (platFavoutAmount.compareTo(BigDecimal.valueOf(0.0)) == 1) {
			// 如果平台有优惠，返回2.平台优惠不包括运费补贴。运费补贴留给另外的字段存储
			activityType = 2;
		}
		vo.setActivityType(activityType);
		// Begin V2.5 added by maojj 2017-07-12
		vo.setDeliveryType(order.getDeliveryType());
		vo.setFarePreferential(order.getRealFarePreferential());
		vo.setCommisionRatio(order.getCommisionRatio());
		// End V2.5 added by maojj 2017-07-12
		
		if(tradeOrderLogistics != null){
			//收货人姓名要从物流表取
			vo.setUserName(tradeOrderLogistics.getConsigneeName());
			vo.setPhone(tradeOrderLogistics.getMobile());
			//省市区+详细地址
			vo.setAddress(
				(StringUtils.isEmpty(tradeOrderLogistics.getArea()) ? "" : tradeOrderLogistics.getArea() )
					+
				( StringUtils.isEmpty(tradeOrderLogistics.getAddress()) ? "" : tradeOrderLogistics.getAddress())
			);
			vo.setLogisticsCompanyName(tradeOrderLogistics.getLogisticsCompanyName());
			vo.setLogisticsNo(tradeOrderLogistics.getLogisticsNo());
			vo.setLogisticsType(tradeOrderLogistics.getType() == null ? null : tradeOrderLogistics.getType().ordinal());
		}
		vo.setPickUpType(order.getPickUpType().ordinal());
		vo.setDeliveryTime(order.getDeliveryTime());
		
		//订单项list部分
		List<OnlineOrderItem> ooiList = new ArrayList<OnlineOrderItem>();
		if(CollectionUtils.isNotEmpty(itemList)){
			int i = 1;
			for(TradeOrderItem item : itemList){
				OnlineOrderItem ooi = new OnlineOrderItem();
				ooi.setOriginalPrice(item.getUnitPrice());
				ooi.setRowNo(i);
				ooi.setSaleNum(new BigDecimal(item.getQuantity()));
				
				// 店铺优惠金额
				BigDecimal storePreferentialPrice = item.getStorePreferential();
				// 实际单价 = 原单价 - 店铺优惠/购买数量
				BigDecimal actualPrice = item.getUnitPrice().subtract(storePreferentialPrice);
				if (item.getWeight() != null) {
					actualPrice = item.getUnitPrice().subtract(
							storePreferentialPrice.divide(item.getWeight(), 4, BigDecimal.ROUND_HALF_UP));
				} else {
					actualPrice = item.getUnitPrice().subtract(
							storePreferentialPrice.divide(BigDecimal.valueOf(item.getQuantity()), 4, BigDecimal.ROUND_HALF_UP));
				}
				ooi.setSalePrice(actualPrice);
				ooi.setSkuId(item.getGoodsSkuId() );
				i++;
				ooiList.add(ooi);
			}
		}
		vo.setItemList(ooiList);
		return vo;
	}
	
	/**
	 * @Description: 修改订单状态
	 * @param tradeOrderContext
	 * @return
	 * @author zhangkn
	 * @date 2017年6月6日
	 */
	private OnlineOrderVo  buildOnlineOrderVo(TradeOrderContext tradeOrderContext) throws Exception{
		OnlineOrderVo vo = new OnlineOrderVo();
		TradeOrder order = tradeOrderContext.getTradeOrder();
		
		//订单物流对象
		TradeOrderLogistics tradeOrderLogistics = tradeOrderContext.getTradeOrderLogistics();
		if(tradeOrderLogistics == null){
			tradeOrderLogistics = tradeOrderLogisticsService.findByOrderId(order.getId());
		}
		
		vo.setOrderId(order.getId());
		vo.setOrderNo(order.getOrderNo());
		
		//操作类型：0-成功1-待发货/等待卖家确认 2-发货/等待买家退货3-确认收货/等待卖家退款4-拒收/卖家拒绝退货5-强制卖家退款6-强制友门鹿退款 7-回款 8-取消
		if(order.getStatus() == OrderStatusEnum.HAS_BEEN_SIGNED){
			vo.setOptType(0);
		} else if (order.getStatus() == OrderStatusEnum.DROPSHIPPING){
			vo.setOptType(1);
		} else if (order.getStatus() == OrderStatusEnum.TO_BE_SIGNED){
			vo.setOptType(2);
		} else if (order.getStatus() == OrderStatusEnum.REFUSED){
			vo.setOptType(4);
		} else if (order.getStatus() == OrderStatusEnum.DROPSHIPPING){
			vo.setOptType(5);
		} else if (order.getStatus() == OrderStatusEnum.CANCELED){
			vo.setOptType(8);
		} 
		
		//货到付款的订单 才有回款
		if(order.getPayWay() != null && order.getPayWay() == PayWayEnum.CASH_DELIERY){
			if(PaymentStatusEnum.BACK_SECTION == order.getPaymentStatus()){
				vo.setOptType(7);
			}
		}
		vo.setUpdateTime(order.getUpdateTime());
		vo.setUpdateUserId(order.getUpdateUserId());
		return vo;
	}
	
	//拆分订单项
	private void splitItemList(List<TradeOrderItem> itemList,String orderId) throws Exception{
		// 组合商品快照列表
		List<TradeOrderComboSnapshot> comboSkuList = tradeOrderComboSnapshotMapper.findByOrderId(orderId);
		if(CollectionUtils.isEmpty(comboSkuList)){
			// 如果快照表中没有找到明细，则直接从组合成分表中获取明细
			comboSkuList = comboSnapshotAdapter.findByTradeOrderItemList(itemList);
		}
		Iterator<TradeOrderItem> itemIt = itemList.iterator();
		TradeOrderItem item = null;
		List<TradeOrderItem> splitItemList = new ArrayList<TradeOrderItem>();
		TradeOrderItem splitItem = null;
		BigDecimal favourItem = null;
		while(itemIt.hasNext()){
			item = itemIt.next();
			if(item.getSpuType() == SpuTypeEnum.assembleSpu){
				// 如果是组合商品，对订单项进行拆分
				List<TradeOrderComboSnapshot> comboDetailList = findComboDetailList(comboSkuList, item.getStoreSkuId());
				for(TradeOrderComboSnapshot comboDetail : comboDetailList){
					splitItem = new TradeOrderItem();
					splitItem.setId(UuidUtils.getUuid());
					splitItem.setOrderId(item.getOrderId());
					splitItem.setActivityType(item.getActivityType());
					// 单价即线上价格
					splitItem.setUnitPrice(comboDetail.getOnlinePrice());
					splitItem.setQuantity(comboDetail.getQuantity()*item.getQuantity());
					// 组合商品优惠=（线上价格-组合价格）*组合成分数量
					favourItem = comboDetail.getOnlinePrice().subtract(comboDetail.getUnitPrice()).multiply(BigDecimal.valueOf(splitItem.getQuantity()));
					splitItem.setPreferentialPrice(favourItem);
					splitItem.setStorePreferential(favourItem);
					splitItem.setStoreSkuId(comboDetail.getStoreSkuId());
					splitItem.setCreateTime(item.getCreateTime());
					splitItemList.add(splitItem);
				}
				itemIt.remove();
			}
		}
		itemList.addAll(splitItemList);
	}
	
	private List<TradeOrderComboSnapshot> findComboDetailList(List<TradeOrderComboSnapshot> comboSkuList,String comboSkuId){
		List<TradeOrderComboSnapshot> comboDetailList = Lists.newArrayList();
		for(TradeOrderComboSnapshot comboDetail : comboSkuList){
			if(comboSkuId.equals(comboDetail.getComboSkuId())){
				comboDetailList.add(comboDetail);
			}
		}
		return comboDetailList;
	}
}