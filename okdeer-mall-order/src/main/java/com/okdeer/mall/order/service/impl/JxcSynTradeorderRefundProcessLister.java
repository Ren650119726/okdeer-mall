
package com.okdeer.mall.order.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.assemble.dto.GoodsStoreSkuAssembleDto;
import com.okdeer.archive.goods.spu.enums.SpuTypeEnum;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.jxc.common.constant.TradeOrderMQMessage;
import com.okdeer.jxc.common.utils.JsonMapper;
import com.okdeer.jxc.onlineorder.entity.OnlineOrder;
import com.okdeer.jxc.onlineorder.entity.OnlineOrderItem;
import com.okdeer.jxc.onlineorder.vo.OnlineOrderVo;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.order.bo.TradeOrderContext;
import com.okdeer.mall.order.builder.StockAdjustVoBuilder;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.entity.TradeOrderRefundsLogistics;
import com.okdeer.mall.order.enums.ActivityBelongType;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.service.TradeOrderActivityService;
import com.okdeer.mall.order.service.TradeOrderItemService;
import com.okdeer.mall.order.service.TradeOrderRefundsCertificateService;
import com.okdeer.mall.order.service.TradeOrderRefundsItemService;
import com.okdeer.mall.order.service.TradeOrderRefundsLogisticsService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.service.TradeorderRefundProcessLister;

@Service("jxcSynTradeorderRefundProcessLister")
public class JxcSynTradeorderRefundProcessLister implements TradeorderRefundProcessLister {

	@Autowired
	private RocketMQProducer rocketMQProducer;

	@Autowired
	private TradeOrderService tradeOrderService;

	@Autowired
	private TradeOrderRefundsItemService tradeOrderRefundsItemService;

	@Autowired
	private TradeOrderRefundsLogisticsService tradeOrderRefundsLogisticsService;

	@Reference(version = "1.0.0")
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;

	@Autowired
	private TradeOrderActivityService tradeOrderActivityService;
	
	@Autowired
	private TradeOrderRefundsCertificateService tradeOrderRefundsCertificateService;
	@Autowired
	private TradeOrderItemService tradeOrderItemService;
	
	@Resource
	private StockAdjustVoBuilder stockAdjustVoBuilder;

	private static final Logger log = LoggerFactory.getLogger(ServiceOrderProcessServiceImpl.class);

	@Override
	public void tradeOrderStatusChange(TradeOrderContext tradeOrderContext) throws Exception {
		try {
			log.info("进入同步erp消息 退款单号:" + tradeOrderContext.getTradeOrderRefunds().getRefundNo());
			//只同步实物订单
			getTradeOrder(tradeOrderContext);
			if(tradeOrderContext.getTradeOrder().getType() != OrderTypeEnum.PHYSICAL_ORDER){
				return;
			}
			
			if (tradeOrderContext.getTradeOrderRefunds().getRefundsStatus() == RefundsStatusEnum.WAIT_SELLER_VERIFY) {
				sendMQMessage(TradeOrderMQMessage.TOPIC_ORDER_SYNC, buildOnlineOrder(tradeOrderContext));
			} else if (tradeOrderContext.getTradeOrderRefunds().getRefundsStatus() == RefundsStatusEnum.REFUND_SUCCESS ||
					tradeOrderContext.getTradeOrderRefunds().getRefundsStatus() == RefundsStatusEnum.WAIT_BUYER_RETURN_GOODS || 
					tradeOrderContext.getTradeOrderRefunds().getRefundsStatus() == RefundsStatusEnum.WAIT_SELLER_TAKE_GOODS ||
					tradeOrderContext.getTradeOrderRefunds().getRefundsStatus() == RefundsStatusEnum.WAIT_SELLER_REFUND ||
					tradeOrderContext.getTradeOrderRefunds().getRefundsStatus() == RefundsStatusEnum.SELLER_REJECT_REFUND ||
					tradeOrderContext.getTradeOrderRefunds().getRefundsStatus() == RefundsStatusEnum.SELLER_REJECT_APPLY ||
					tradeOrderContext.getTradeOrderRefunds().getRefundsStatus() == RefundsStatusEnum.YSC_REFUND_SUCCESS ||
					tradeOrderContext.getTradeOrderRefunds().getRefundsStatus() == RefundsStatusEnum.YSC_REFUND ||
					tradeOrderContext.getTradeOrderRefunds().getRefundsStatus() == RefundsStatusEnum.BUYER_REPEAL_REFUND ||
					tradeOrderContext.getTradeOrderRefunds().getRefundsStatus() == RefundsStatusEnum.CUSTOMER_SERVICE_CANCEL_INTERVENE ||
					tradeOrderContext.getTradeOrderRefunds().getRefundsStatus() == RefundsStatusEnum.FORCE_SELLER_REFUND_SUCCESS) {
				sendMQMessage(TradeOrderMQMessage.TOPIC_ORDER_UPDATE_SYNC, buildOnlineOrderVo(tradeOrderContext));
			}
		} catch (Exception e) {
			log.error("", e);
			throw e;
		}
	}

	private <T> void sendMQMessage(String topic, T obj) throws Exception {
		log.info("同步erp退款消息，topic：{}，消息内容:{}",topic,JsonMapper.nonDefaultMapper().toJson(obj));
		MQMessage message = new MQMessage(topic, (Serializable) obj);
		rocketMQProducer.sendMessage(message);
	}

	private OnlineOrder buildOnlineOrder(TradeOrderContext tradeOrderContext) throws Exception {
		OnlineOrder vo = new OnlineOrder();
		TradeOrderRefunds tradeOrderRefunds = tradeOrderContext.getTradeOrderRefunds();
		vo.setId(tradeOrderRefunds.getId());
		vo.setStoreId(tradeOrderRefunds.getStoreId());
		vo.setOrderNo(tradeOrderRefunds.getRefundNo());
		vo.setSaleType("B");// A销售单 B退货单
		vo.setOrderResource(tradeOrderRefunds.getOrderResource().ordinal());
		if (tradeOrderRefunds.getTotalPreferentialPrice() != null) {
			vo.setTotalAmount(tradeOrderRefunds.getTotalAmount().add(tradeOrderRefunds.getTotalPreferentialPrice()));
		} else {
			vo.setTotalAmount(tradeOrderRefunds.getTotalAmount());
		}

		vo.setActualAmount(tradeOrderRefunds.getTotalAmount());

		Boolean isPlatformPreferential = isPlatformPreferential(tradeOrderContext);
		if (isPlatformPreferential) {
			vo.setPlatDiscountAmount(tradeOrderRefunds.getTotalPreferentialPrice());
			vo.setDiscountAmount(BigDecimal.ZERO);
		} else {
			vo.setPlatDiscountAmount(BigDecimal.ZERO);
			vo.setDiscountAmount(tradeOrderRefunds.getTotalPreferentialPrice());
		}
		
		vo.setFare(BigDecimal.ZERO);
		vo.setUserId(tradeOrderRefunds.getUserId());
		vo.setCreateTime(tradeOrderRefunds.getCreateTime());
		vo.setPayType(tradeOrderRefunds.getPaymentMethod().ordinal());
		vo.setReferenceNo(tradeOrderRefunds.getOrderNo());
		vo.setRefundsType(tradeOrderRefunds.getStatus().ordinal());
		vo.setRefundsReason(tradeOrderRefunds.getRefundsReason());
		vo.setRefuseReason(tradeOrderRefunds.getRefuseReson());
		vo.setMemo(tradeOrderRefunds.getMemo());

		List<TradeOrderRefundsItem> tradeOrderRefundsItems = getTradeOrderRefundsItem(tradeOrderContext);
		//拆分退款单item
		splitItemList(tradeOrderRefundsItems);

		// 订单项list部分
		List<OnlineOrderItem> ooiList = new ArrayList<OnlineOrderItem>();
		if (CollectionUtils.isNotEmpty(tradeOrderRefundsItems)) {
			int i = 1;
			for (TradeOrderRefundsItem item : tradeOrderRefundsItems) {
				OnlineOrderItem ooi = new OnlineOrderItem();
				ooi.setOriginalPrice(item.getUnitPrice());
				ooi.setRowNo(i);
				ooi.setSaleNum(new BigDecimal(item.getQuantity() == null ? 0 : item.getQuantity()));
				
				// 店铺优惠金额
				BigDecimal storePreferentialPrice = BigDecimal.ZERO;
				// 订单金额如果不等于店家收入金额，说明是店铺有优惠
				//Begin 排除平台优惠 update by tangy  2016-10-28
				
				if (tradeOrderContext.getTradeOrder().getActualAmount().compareTo(tradeOrderContext.getTradeOrder().getIncome()) == 0 ) {
					storePreferentialPrice = item.getPreferentialPrice();
				} 
				// 实际单价=原单价减去店铺优惠
				BigDecimal actualPrice = item.getUnitPrice().subtract(storePreferentialPrice);
				if (item.getQuantity() != null && item.getQuantity().intValue() > 0) {
					actualPrice = item.getUnitPrice().subtract(
							storePreferentialPrice.divide(new BigDecimal(item.getQuantity()), 4, BigDecimal.ROUND_HALF_UP));
				} else if (item.getWeight() != null 
						&& storePreferentialPrice.compareTo(BigDecimal.ZERO) == 1) {
					actualPrice = item.getUnitPrice().subtract(
							storePreferentialPrice.divide(item.getWeight(), 4, BigDecimal.ROUND_HALF_UP));
				} 
				ooi.setSalePrice(actualPrice);
				ooi.setSkuId(item.getGoodsSkuId());
				i++;
				ooiList.add(ooi);
			}
		}
		vo.setItemList(ooiList);
		
		//退单图片部分
		List<String> imageList = tradeOrderRefundsCertificateService.findImageByRefundsId(tradeOrderRefunds.getId());
		StringBuffer sb = new StringBuffer();
		if(CollectionUtils.isNotEmpty(imageList)){
			for(String image : imageList){
				if(StringUtils.isNotBlank(image)){
					if(!"".equals(sb.toString())){
						sb.append(",");
					}
					sb.append(image);
				}
			}
		}
		vo.setRefundPicUrl(sb.toString());
		return vo;
	}

	private TradeOrder getTradeOrder(TradeOrderContext tradeOrderContext) throws ServiceException {
		TradeOrder tradeOrder = tradeOrderContext.getTradeOrder();
		if (tradeOrder == null) {
			tradeOrder  = tradeOrderService.selectById(tradeOrderContext.getTradeOrderRefunds().getOrderId());
		}
		tradeOrderContext.setTradeOrder(tradeOrder);
		return tradeOrder;
	}
	

	private Boolean isPlatformPreferential(TradeOrderContext tradeOrderContext) throws Exception {
		Boolean isPlatformPreferential = tradeOrderContext.getPlatformPreferential();
		if (isPlatformPreferential == null) {
			
			isPlatformPreferential = false;
			TradeOrder tradeOrder = getTradeOrder(tradeOrderContext);
			// 是否平台优惠
			// 优惠额退款 判断是否有优惠劵
			if (tradeOrder.getActivityType() == ActivityTypeEnum.FULL_DISCOUNT_ACTIVITIES
					|| tradeOrder.getActivityType() == ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES
					|| tradeOrder.getActivityType() == ActivityTypeEnum.VONCHER) {
				ActivityBelongType activityBelong = tradeOrderActivityService.findActivityType(tradeOrder);
				if (ActivityBelongType.OPERATOR == activityBelong || ActivityBelongType.AGENT == activityBelong) {
					isPlatformPreferential = true;
				}
			}
			tradeOrderContext.setPlatformPreferential(isPlatformPreferential);
		}
		tradeOrderContext.setPlatformPreferential(isPlatformPreferential);
		return isPlatformPreferential;
	}

	private List<TradeOrderRefundsItem> getTradeOrderRefundsItem(TradeOrderContext tradeOrderContext) throws Exception{
		List<TradeOrderRefundsItem> tradeOrderRefundsItemList = tradeOrderContext.getOrderRefundsItemList();
		if (tradeOrderRefundsItemList == null) {
			tradeOrderRefundsItemList = tradeOrderContext.getTradeOrderRefunds().getTradeOrderRefundsItem();
		}

		if (tradeOrderRefundsItemList == null) {
			tradeOrderRefundsItemList = tradeOrderRefundsItemService
					.getTradeOrderRefundsItemByRefundsId(tradeOrderContext.getTradeOrderRefunds().getId());
		}
		
		//需要标准库商品id
		List<String> goodsStoreSkuIdList = new ArrayList<String>();
		for(TradeOrderRefundsItem item : tradeOrderRefundsItemList){
			goodsStoreSkuIdList.add(item.getStoreSkuId());
		}
		//需要得到skuId,把idlist一次拉出来,然后用in的方式
		List<GoodsStoreSku> goodsStoreSkuList = goodsStoreSkuServiceApi.findByIds(goodsStoreSkuIdList);
		for(TradeOrderRefundsItem item : tradeOrderRefundsItemList){
			for(GoodsStoreSku sku : goodsStoreSkuList){
				if(item.getStoreSkuId().equals(sku.getId())){
					item.setGoodsSkuId(sku.getSkuId());
					break;
				}
			}
		}
		
		tradeOrderContext.setOrderRefundsItemList(tradeOrderRefundsItemList);
		return tradeOrderRefundsItemList;
	}
	

	/**
	 * @Description: 修改订单状态
	 * @param tradeOrderContext
	 * @return
	 * @author zhangkn
	 * @date 2017年6月6日
	 */
	private OnlineOrderVo buildOnlineOrderVo(TradeOrderContext tradeOrderContext) {
		OnlineOrderVo vo = new OnlineOrderVo();
		// 退款对象
		TradeOrderRefunds tradeOrderRefunds = tradeOrderContext.getTradeOrderRefunds();
		
		vo.setOrderId(tradeOrderRefunds.getId());
		vo.setOrderNo(tradeOrderRefunds.getRefundNo());
		vo.setLogisticsType(tradeOrderRefunds.getLogisticsType() == null ? 2 : tradeOrderRefunds.getLogisticsType().ordinal());//为空传2
		vo.setUpdateTime(tradeOrderRefunds.getUpdateTime());
		vo.setUpdateUserId(tradeOrderRefunds.getOperator());
		vo.setRefuseReason(tradeOrderRefunds.getRefundsReason());
	
		/*
		 * 0:等待卖家确认,1:买家撤销退款,2:等待买家退货,3:等待卖家收货,4:待卖家退款,5:卖家拒绝退款,
		 * 6:退款成功,7:卖家拒绝申请,8:申请客服介入,9:客户介入取消,10:友门鹿退款,
		 * 11:(纠纷单)友门鹿退款成功,12:待卖家退款(强制),13:卖家退款成功(强制),14:卖家退款中
		 */
		// 操作类型：0-成功1-待发货/等待卖家确认 2-发货/等待买家退货3-确认收货/等待卖家退款4-拒收/卖家拒绝退货5-强制卖家退款6-强制友门鹿退款 7-回款 8-取消
		/*
		我们的值								左文明的值
		6:退款成功  							0-成功
		0:等待卖家确认    							1-待发货/等待卖家确认
		2:等待买家退货     							2-发货/等待买家退货
		3:等待卖家收货,4:待卖家退款   				3-确认收货/等待卖家退款
		5:卖家拒绝退款, 7:卖家拒绝申请    				4-拒收/卖家拒绝退货
		12:待卖家退款(强制),13:卖家退款成功(强制)   	5-强制卖家退款
		10:友门鹿退款,11:(纠纷单)友门鹿退款成功   		6-强制友门鹿退款
		1:买家撤销退款 9:客户介入取消,   			8-取消
		*/
		if (tradeOrderRefunds.getRefundsStatus() == RefundsStatusEnum.REFUND_SUCCESS) {
			vo.setOptType(0);
		} else if (tradeOrderRefunds.getRefundsStatus() == RefundsStatusEnum.WAIT_SELLER_VERIFY) {
			vo.setOptType(1);
		} else if (tradeOrderRefunds.getRefundsStatus() == RefundsStatusEnum.WAIT_BUYER_RETURN_GOODS ) {
			vo.setOptType(2);
		} else if (tradeOrderRefunds.getRefundsStatus() == RefundsStatusEnum.WAIT_SELLER_TAKE_GOODS ||
				tradeOrderRefunds.getRefundsStatus() == RefundsStatusEnum.WAIT_SELLER_REFUND	) {
			vo.setOptType(3);
		} else if (tradeOrderRefunds.getRefundsStatus() == RefundsStatusEnum.SELLER_REJECT_REFUND ||
				tradeOrderRefunds.getRefundsStatus() == RefundsStatusEnum.SELLER_REJECT_APPLY	) {
			vo.setOptType(4);
		} else if (tradeOrderRefunds.getRefundsStatus() == RefundsStatusEnum.FORCE_SELLER_REFUND ||
				tradeOrderRefunds.getRefundsStatus() == RefundsStatusEnum.FORCE_SELLER_REFUND_SUCCESS) {
			vo.setOptType(5);
		} else if (tradeOrderRefunds.getRefundsStatus() == RefundsStatusEnum.YSC_REFUND || 
				tradeOrderRefunds.getRefundsStatus() == RefundsStatusEnum.YSC_REFUND_SUCCESS) {
			vo.setOptType(6);
		} else if (tradeOrderRefunds.getRefundsStatus() == RefundsStatusEnum.BUYER_REPEAL_REFUND ||
				tradeOrderRefunds.getRefundsStatus() == RefundsStatusEnum.CUSTOMER_SERVICE_CANCEL_INTERVENE) {
			vo.setOptType(8);
		} 

		// 退款物流对象
		TradeOrderRefundsLogistics tradeOrderRefundsLogistics = tradeOrderContext.getTradeOrderRefundsLogistics();
		if (tradeOrderRefundsLogistics == null) {
			tradeOrderRefundsLogistics = tradeOrderRefundsLogisticsService.findByRefundsId(tradeOrderRefunds.getId());
		}
		if(tradeOrderRefundsLogistics != null){
			vo.setLogisticsNo(tradeOrderRefundsLogistics.getLogisticsNo());
			vo.setLogisticsCompanyName(tradeOrderRefundsLogistics.getLogisticsCompanyName());
		}
		return vo;
	}
	
	//拆分订单项
	private void splitItemList(List<TradeOrderRefundsItem> itemList) throws Exception{
		Map<String,List<GoodsStoreSkuAssembleDto>> comboSkuMap = stockAdjustVoBuilder.parseComboSkuForRefund(itemList);
		Iterator<TradeOrderRefundsItem> itemIt = itemList.iterator();
		TradeOrderRefundsItem item = null;
		List<TradeOrderRefundsItem> splitItemList = new ArrayList<TradeOrderRefundsItem>();
		TradeOrderRefundsItem splitItem = null;
		
		while(itemIt.hasNext()){
			item = itemIt.next();
			
			TradeOrderItem tradeOrderItem = tradeOrderItemService.selectByPrimaryKey(item.getOrderItemId() ) ;
			if(item.getSpuType() == SpuTypeEnum.assembleSpu){
				// 如果是组合商品，对订单项进行拆分
				List<GoodsStoreSkuAssembleDto> comboDetailList = comboSkuMap.get(item.getStoreSkuId());
				for(GoodsStoreSkuAssembleDto comboDto : comboDetailList){
					splitItem = new TradeOrderRefundsItem();
					splitItem.setId(UuidUtils.getUuid());
					splitItem.setRefundsId(item.getRefundsId());
					splitItem.setPreferentialPrice(BigDecimal.valueOf(0.0));
					splitItem.setUnitPrice(comboDto.getUnitPrice());
					splitItem.setQuantity(comboDto.getQuantity()*item.getQuantity());
					splitItem.setStoreSkuId(comboDto.getStoreSkuId());
					splitItem.setGoodsSkuId(comboDto.getSkuId());
					splitItemList.add(splitItem);
				}
				itemIt.remove();
			} else if(tradeOrderItem.getActivityQuantity() != null && tradeOrderItem.getActivityQuantity() > 0){
				
				//需要标准库商品id,tradeOrderItem里面没有
				GoodsStoreSku goodsStoreSku = goodsStoreSkuServiceApi.selectByPrimaryKey(tradeOrderItem.getStoreSkuId());
				// 如果是低价且购买了低价商品，对商品进行拆分
				splitItem = new TradeOrderRefundsItem();
				splitItem.setId(UuidUtils.getUuid());
				splitItem.setRefundsId(item.getRefundsId());
				splitItem.setPreferentialPrice(tradeOrderItem.getPreferentialPrice());
				splitItem.setUnitPrice(tradeOrderItem.getUnitPrice());
				splitItem.setQuantity(tradeOrderItem.getQuantity() - tradeOrderItem.getActivityQuantity());
				splitItem.setStoreSkuId(tradeOrderItem.getStoreSkuId());
				splitItem.setGoodsSkuId(goodsStoreSku == null ? "" : goodsStoreSku.getSkuId());
				splitItemList.add(splitItem);
				
				if(tradeOrderItem.getQuantity() - tradeOrderItem.getActivityQuantity() > 0){
					splitItem = new TradeOrderRefundsItem();
					splitItem.setId(UuidUtils.getUuid());
					splitItem.setRefundsId(item.getRefundsId());
					splitItem.setPreferentialPrice(BigDecimal.valueOf(0.0));
					splitItem.setUnitPrice(tradeOrderItem.getUnitPrice());
					splitItem.setQuantity(tradeOrderItem.getQuantity() - tradeOrderItem.getActivityQuantity());
					splitItem.setStoreSkuId(tradeOrderItem.getStoreSkuId());
					splitItem.setGoodsSkuId(goodsStoreSku == null ? "" : goodsStoreSku.getSkuId());
					splitItemList.add(splitItem);
				}
				itemIt.remove();
			}
		}
		itemList.addAll(splitItemList);
	}
}