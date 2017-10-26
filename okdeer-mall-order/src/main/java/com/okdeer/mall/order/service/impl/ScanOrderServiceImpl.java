/** 
 *@Project: okdeer-mall-order 
 *@Author: guocp
 *@Date: 2017年10月16日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.order.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.archive.system.entity.SysBuyerUser;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.model.RequestParams;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.mall.activity.coupons.bo.ActivityCouponsBo;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.mq.constants.ActivityCouponsTopic;
import com.okdeer.mall.order.dto.ScanOrderDto;
import com.okdeer.mall.order.dto.ScanOrderItemDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.enums.OrderIsShowEnum;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.service.ScanOrderService;
import com.okdeer.mall.order.service.TradeOrderPayServiceApi;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.service.TradePinMoneyUseService;
import com.okdeer.mall.system.service.SysBuyerUserServiceApi;


/**
 * ClassName: ScanOrderServiceImpl 
 * @Description: 扫码购订单服务
 * @author guocp
 * @date 2017年10月16日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class ScanOrderServiceImpl implements ScanOrderService {
	
	private static final Logger logger = LoggerFactory.getLogger(ScanOrderServiceImpl.class);
	
	@Autowired
	private RocketMQProducer rocketMQProducer;
	
	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoService;
	
    @Reference(version = "1.0.0", check = false)
    private SysBuyerUserServiceApi buyserUserService;
    
	/**
	 * goodsStoreSkuApi服务接口
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuApi;

	/**
	 * tradeOrder服务接口
	 */
	@Autowired
	private TradeOrderService tradeOrderService;
	
	@Autowired
	private TradePinMoneyUseService tradePinMoneyUseService;
	
	/**
	 * 支付接口
	 */
	@Autowired
	private TradeOrderPayServiceApi tradeOrderPayApi;
	
    @Resource
    private ActivityCouponsRecordMapper activityCouponsRecordMapper;
   
	/**
	 * 保存扫码购订单
	 * @Description: 
	 * @param vo 从返回参数中转化来的vo对象
	 * @param branchId 店铺id
	 * @throws Exception void
	 * @author mengsj
	 * @date 2017年3月18日
	 */
    @Override
    @Transactional(rollbackFor=Exception.class)
	public void saveScanOrder(ScanOrderDto vo,String branchId,RequestParams requestParams) throws Exception{
		//转化结果集
		TradeOrder persity = BeanMapper.map(vo, TradeOrder.class);
		//设置id值
		persity.setId(vo.getOrderId());
		//设置店铺id
		persity.setStoreId(vo.getBranchId());
		//实付金额
		persity.setActualAmount(vo.getSaleAmount());
		//优惠金额
		BigDecimal prefer = vo.getPlatDiscountAmount() != null ? vo.getPlatDiscountAmount():BigDecimal.ZERO;
		BigDecimal discount = vo.getDiscountAmount() != null ? vo.getDiscountAmount():BigDecimal.ZERO;
		BigDecimal pinMoney = vo.getPinMoneyAmount()!=null?vo.getPinMoneyAmount():BigDecimal.ZERO;
		//店铺优惠
		persity.setStorePreferential(discount);
		//总优惠
		persity.setPreferentialPrice(discount.add(prefer).add(pinMoney));
		//零花钱优惠
		persity.setPinMoney(pinMoney);
		//平台优惠字段
		persity.setPlatformPreferential(prefer.add(pinMoney));
		//店铺收入字段
		persity.setIncome(persity.getActualAmount().add(persity.getPlatformPreferential()));
		persity.setCreateTime(new Date());
		persity.setUpdateTime(persity.getCreateTime());
		//设置显示
		persity.setIsShow(OrderIsShowEnum.yes);
		//设置店铺信息
		StoreInfo storeInfo = storeInfoService.findById(branchId);
		logger.info("店铺信息:{}",JsonMapper.nonEmptyMapper().toJson(storeInfo));
		persity.setStoreName(storeInfo.getStoreName());
		//设置用户信息
		SysBuyerUser user = buyserUserService.findByPrimaryKey(vo.getUserId());
		logger.info("下单用户:{}",JsonMapper.nonEmptyMapper().toJson(user));
		
		persity.setUserPhone(user.getPhone());
		if(CollectionUtils.isEmpty(vo.getList())){
			return;
		}
		List<TradeOrderItem> items = BeanMapper.mapList(vo.getList(), TradeOrderItem.class);
		Map<String, ScanOrderItemDto> orderIts = vo.getList().stream()
				.collect(Collectors.toMap(e -> e.getId(), e -> e));
		for(int i=0;i<items.size();i++){
			TradeOrderItem item = items.get(i);
			ScanOrderItemDto map = orderIts.get(item.getId());
				//在线上查找是否有对应商品，如果有，将对应信息设置进去
				GoodsStoreSku goodsStoreSku = goodsStoreSkuApi.selectByStoreIdAndSkuId(branchId, map.getSkuId());
				if(goodsStoreSku != null && StringUtils.isNotBlank(goodsStoreSku.getId())){
					item.setStoreSkuId(goodsStoreSku.getId());
					item.setMainPicPrl(goodsStoreSku.getContent());
					item.setBarCode(goodsStoreSku.getBarCode());
				}
				//跟jxc约定barCode放在skuCode字段里面返回
				item.setBarCode(map.getSkuCode());
				//实付金额
				item.setActualAmount(map.getSaleAmount());
				//优惠金额
				item.setPreferentialPrice(map.getDiscountAmount());
				item.setUnitPrice(map.getSalePrice());
				item.setIncome(map.getSaleAmount());
				//商品数量
				item.setQuantity((int)map.getSaleNum().doubleValue());
				//不支持售后
				item.setServiceAssurance(0);
		}
		persity.setTradeOrderItem(items);
		//将订单状态标记为：等待买家付款
		persity.setStatus(OrderStatusEnum.UNPAID);
		//将订单来源标记为：自助买单
		persity.setOrderResource(vo.getOrderResource());
		//保存订单
		tradeOrderService.insertTradeOrder(persity);
		
		//更新优惠券信息
		if(vo.getActivityType() == ActivityTypeEnum.VONCHER){
			this.updateActivityCoupons(vo.getOrderId(), vo.getRecordId(),vo.getCouponsId(), requestParams.getMachineCode());
		}
		// 保存零花钱记录
		if (persity.getPinMoney().compareTo(BigDecimal.ZERO) > 0) {
			tradePinMoneyUseService.orderOccupy(vo.getUserId(), vo.getOrderId(), persity.getTotalAmount(),
					persity.getPinMoney());
		}
		//支付0元直接改为支付完成
		if(persity.getActualAmount().compareTo(BigDecimal.ZERO) == 0){
			tradeOrderPayApi.wlletPay(BigDecimal.ZERO.toString(), persity);
		}
	}
    
    /**
	 * @Description: 更新代金券
	 * @param tradeOrder 交易订单
	 * @param req 请求对象
	 * @return void  
	 */
    @Override
	public void updateActivityCoupons(String orderId,String recordId,String couponsId,String deviceId) throws Exception {
		Map<String, Object> params = new HashMap<>();
		params.put("orderId", orderId);
		params.put("id", recordId);
		params.put("deviceId", deviceId);
		params.put("recDate", DateUtils.getDate());
		// 更新代金券状态
		int updateResult = activityCouponsRecordMapper.updateActivityCouponsStatus(params);
		if (updateResult == 0) {
			throw new ServiceException("代金券已使用或者已过期");
		}
		// 发送消息修改代金券使用数量
		ActivityCouponsBo couponsBo = new ActivityCouponsBo(couponsId, Integer.valueOf(1));
		MQMessage<?> anMessage = new MQMessage<>(ActivityCouponsTopic.TOPIC_COUPONS_COUNT, (Serializable) couponsBo);
		// key:订单id：代金券id
		anMessage.setKey(String.format("%s:%s", orderId,couponsId));
		try {
			rocketMQProducer.sendMessage(anMessage);
		} catch (Exception e) {
			logger.error("发送代金券使用消息时发生异常，{}",e);
		}
	}
}
