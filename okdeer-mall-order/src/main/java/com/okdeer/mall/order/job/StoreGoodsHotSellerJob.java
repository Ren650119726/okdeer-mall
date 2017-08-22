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

import com.alibaba.dubbo.config.annotation.Reference;
import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.okdeer.archive.goods.dto.StoreGoodsHotSellerDto;
import com.okdeer.archive.goods.service.StoreGoodsHotSellerApi;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.order.bo.StoreGoodsHotSellerBo;
import com.okdeer.mall.order.service.TradeOrderItemService;

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

public class StoreGoodsHotSellerJob extends AbstractSimpleElasticJob  {
	/**
	 * 日志管理器
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(StoreGoodsHotSellerJob.class);


	@Autowired
	private TradeOrderItemService tradeOrderItemService;
	
	@Reference(version = "1.0.0", check = false)
	private StoreGoodsHotSellerApi storeGoodsHotSellerApi;

	@Override
	public void process(JobExecutionMultipleShardingContext shardingContext) {

		// 获取昨天的所有已销售商品列表
		List<StoreGoodsHotSellerBo> sellerList = tradeOrderItemService.findSellerList();

		if (CollectionUtils.isNotEmpty(sellerList)) {

			for (StoreGoodsHotSellerBo storeGoodsHotSellerBo : sellerList) {
				try {
					StoreGoodsHotSellerDto dto = BeanMapper.map(storeGoodsHotSellerBo, StoreGoodsHotSellerDto.class);
					
					storeGoodsHotSellerApi.addHotSeller(dto);
				} catch (Exception e) {
					LOGGER.error("店铺统计商品销售出现异常，订单id{}", storeGoodsHotSellerBo.getStoreSkuId());
				}
			}

		}

	}
}
