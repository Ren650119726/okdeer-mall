
package com.okdeer.mall.order.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Lists;
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
import com.okdeer.mall.order.bo.ComboSnapshotAdapter;
import com.okdeer.mall.order.bo.TradeOrderContext;
import com.okdeer.mall.order.builder.StockAdjustVoBuilder;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderComboSnapshot;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.entity.TradeOrderRefundsLogistics;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.mapper.TradeOrderComboSnapshotMapper;
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

	@Resource
	private TradeOrderComboSnapshotMapper tradeOrderComboSnapshotMapper;
	
	@Autowired
	private TradeOrderRefundsCertificateService tradeOrderRefundsCertificateService;
	
	@Resource
	private StockAdjustVoBuilder stockAdjustVoBuilder;
	
	@Resource
	private ComboSnapshotAdapter comboSnapshotAdapter;

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
		TradeOrder order = tradeOrderContext.getTradeOrder();
		
		// Begin V2.5 modified by maojj 2017-07-03
		// 店铺优惠
		BigDecimal storeFavour = tradeOrderRefunds.getStorePreferential();
		// 平台优惠
		BigDecimal platformFavour = tradeOrderRefunds.getTotalPreferentialPrice().subtract(storeFavour);
		// Begin V2.5 modified by maojj 2017-07-03
		
		vo.setId(tradeOrderRefunds.getId());
		vo.setStoreId(tradeOrderRefunds.getStoreId());
		vo.setOrderNo(tradeOrderRefunds.getRefundNo());
		vo.setSaleType("B");// A销售单 B退货单
		vo.setOrderResource(tradeOrderRefunds.getOrderResource().ordinal());
		vo.setTotalAmount(tradeOrderRefunds.getTotalAmount().add(platformFavour));
		vo.setActualAmount(tradeOrderRefunds.getTotalAmount());
		// 平台优惠
		vo.setPlatDiscountAmount(platformFavour);
		// 店铺优惠
		vo.setDiscountAmount(storeFavour);
		
		// 进销存那边的优惠类型0:无活动 ;1：代金券；2：其他
		int activityType = 0;
		// 活动类型为代金券活动
		if (order.getActivityType() == ActivityTypeEnum.VONCHER) {
			activityType = 1;
		} else if (order.getActivityType() == ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES) {
			// 活动类型为满减活动且店家收入不等于用户实付，说明里面有平台的补贴
			activityType = 2;
		}
		vo.setActivityType(activityType);
		
		vo.setFare(BigDecimal.ZERO);
		vo.setUserId(tradeOrderRefunds.getUserId());
		vo.setCreateTime(tradeOrderRefunds.getCreateTime());
		vo.setPayType(tradeOrderRefunds.getPaymentMethod().ordinal());
		vo.setReferenceNo(tradeOrderRefunds.getOrderNo());
		vo.setRefundsType(tradeOrderRefunds.getStatus().ordinal());
		vo.setRefundsReason(tradeOrderRefunds.getRefundsReason());
		//如果是退货单  并且 状态是拒绝  就给值   要不然 就置空
		if(tradeOrderRefunds.getRefundsStatus() == RefundsStatusEnum.SELLER_REJECT_APPLY || 
		   tradeOrderRefunds.getRefundsStatus()	== RefundsStatusEnum.SELLER_REJECT_REFUND
				){
			vo.setRefuseReason(tradeOrderRefunds.getRefuseReson());
		} else {
			vo.setRefuseReason(null);
		}
		
		vo.setMemo(tradeOrderRefunds.getMemo());

		List<TradeOrderRefundsItem> tradeOrderRefundsItems = getTradeOrderRefundsItem(tradeOrderContext);
		//拆分退款单item
		splitItemList(tradeOrderRefundsItems,order.getId());

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
				BigDecimal storePreferentialPrice = item.getStorePreferential();
				// 实际单价=原单价减去-店铺优惠/商品数量
				BigDecimal actualPrice = BigDecimal.valueOf(0.00);
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
	private void splitItemList(List<TradeOrderRefundsItem> itemList,String orderId) throws Exception{
		// 组合商品快照列表
		List<TradeOrderComboSnapshot> comboSkuList = tradeOrderComboSnapshotMapper.findByOrderId(orderId);
		if(CollectionUtils.isEmpty(comboSkuList)){
			// 如果快照表中没有找到明细，则直接从组合成分表中获取明细
			comboSkuList = comboSnapshotAdapter.findByRefundsItemList(itemList);
		}
		Iterator<TradeOrderRefundsItem> itemIt = itemList.iterator();
		TradeOrderRefundsItem item = null;
		List<TradeOrderRefundsItem> splitItemList = new ArrayList<TradeOrderRefundsItem>();
		TradeOrderRefundsItem splitItem = null;
//		BigDecimal favourItem = null;
		while(itemIt.hasNext()){
			item = itemIt.next();
			if(item.getSpuType() == SpuTypeEnum.assembleSpu){
				// 如果是组合商品，对订单项进行拆分
				List<TradeOrderComboSnapshot> comboDetailList = findComboDetailList(comboSkuList, item.getStoreSkuId());
				for(TradeOrderComboSnapshot comboDetail : comboDetailList){
					splitItem = new TradeOrderRefundsItem();
					splitItem.setId(UuidUtils.getUuid());
					splitItem.setRefundsId(item.getRefundsId());
					splitItem.setQuantity(comboDetail.getQuantity()*item.getQuantity());
					splitItem.setUnitPrice(comboDetail.getUnitPrice());
					splitItem.setPreferentialPrice(BigDecimal.ZERO);
					splitItem.setStorePreferential(BigDecimal.ZERO);
					/*// 单价即线上价格
					splitItem.setUnitPrice(comboDetail.getOnlinePrice());
					splitItem.setQuantity(comboDetail.getQuantity()*item.getQuantity());
					// 组合商品优惠=（线上价格-组合价格）*组合成分数量
					favourItem = comboDetail.getOnlinePrice().subtract(comboDetail.getUnitPrice()).multiply(BigDecimal.valueOf(splitItem.getQuantity()));
					splitItem.setPreferentialPrice(favourItem);
					splitItem.setStorePreferential(favourItem);*/
					splitItem.setStoreSkuId(comboDetail.getStoreSkuId());
					splitItem.setGoodsSkuId(comboDetail.getSkuId());
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