/** 
 *@Project: okdeer-mall-order 
 *@Author: xuzq01
 *@Date: 2017年8月21日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.order.job;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.google.common.collect.Lists;
import com.okdeer.archive.goods.dto.StoreGoodsHotSellerDto;
import com.okdeer.archive.goods.service.StoreGoodsHotSellerApi;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.order.bo.StoreGoodsHotSellerBo;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.service.TradeOrderItemService;
import com.okdeer.mall.order.service.TradeOrderService;

/**
 * ClassName: StoreGoodsHotSellerJob 
 * @Description: 店铺热销商品统计job
 * @author xuzq01
 * @date 2017年8月21日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *  V2.6.0            2017年8月21日                       xuzq01             每日2点定时统计店铺商品销售数量
 */
@Service
public class StoreGoodsHotSellerJob extends AbstractSimpleElasticJob  {
	/**
	 * 日志管理器
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(StoreGoodsHotSellerJob.class);
	
	/**
	 * 订单项service
	 */
	@Autowired
	private TradeOrderItemService tradeOrderItemService;
	/**
	 * 订单service
	 */
	@Autowired
	private TradeOrderService tradeOrderService;
	/**
	 * 店铺商品sku表service
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuApi;
	/**
	 * 店铺热销商品api
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreGoodsHotSellerApi storeGoodsHotSellerApi;

	@Override
	public void process(JobExecutionMultipleShardingContext shardingContext) {
		LOGGER.info("店铺统计每日热销商品定时器开始-----" + DateUtils.getDateTime());
		try {
			// 1获取所有昨天有销售订单完成的订单
			List<TradeOrder> orderList = tradeOrderService.findOrderListForJob();
			
			if(CollectionUtils.isNotEmpty(orderList)){
				List<String> orderIds = Lists.newArrayList();
				orderList.forEach(order -> orderIds.add(order.getId()));
				
				// 2根据订单id统计所有订单项销售商品
				List<StoreGoodsHotSellerBo> sellerList = tradeOrderItemService.findSellerList(orderIds);
				
				// 3根据skuids获取店铺商品信息
				List<String> skuIds = Lists.newArrayList();
				sellerList.forEach(seller -> skuIds.add(seller.getStoreSkuId()));
				
				List<GoodsStoreSku>	skuList = goodsStoreSkuApi.findByIds(skuIds);
				// 4 遍历获取SpuCategoryId
				if (CollectionUtils.isNotEmpty(sellerList)) {
					for(StoreGoodsHotSellerBo sellerBo : sellerList ){
						for(GoodsStoreSku storeSku : skuList){
							if(sellerBo.getStoreSkuId().equals(storeSku.getId())){
								sellerBo.setSpuCategoryId(storeSku.getSpuCategoryId());
								StoreGoodsHotSellerDto dto = BeanMapper.map(sellerBo, StoreGoodsHotSellerDto.class);
								storeGoodsHotSellerApi.addHotSeller(dto);
							}
						}
					}
				}
			}
			LOGGER.info("店铺统计每日热销商品定时器结束-----" + DateUtils.getDateTime());
		} catch (Exception e) {
			LOGGER.error("店铺统计商品销售出现异常", e);
		}

	}

}
