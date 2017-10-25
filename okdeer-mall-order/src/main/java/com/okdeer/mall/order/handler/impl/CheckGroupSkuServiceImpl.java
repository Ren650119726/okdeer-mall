package com.okdeer.mall.order.handler.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderItemDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.handler.RequestHandler;

/**
 * ClassName: CheckGroupSkuServiceImpl 
 * @Description: 团购商品检查
 * @author maojj
 * @date 2017年10月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.6.3 		2017年10月10日				maojj
 */
@Service("checkGroupSkuService")
public class CheckGroupSkuServiceImpl implements RequestHandler<PlaceOrderParamDto, PlaceOrderDto> {

	private static final Logger logger = LoggerFactory.getLogger(CheckGroupSkuServiceImpl.class);
	/**
	 * 店铺商品Api
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;

	@Override
	public void process(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		// 请求参数
		PlaceOrderParamDto paramDto = req.getData();
		// 请求商品列表
		List<PlaceOrderItemDto> skuList = paramDto.getSkuList();
		// 团购一次性只能购买一个商品
		if (CollectionUtils.isEmpty(skuList) || skuList.size() > 1) {
			logger.warn("团购订单只能购买一个商品");
			resp.setResult(ResultCodeEnum.ILLEGAL_PARAM);
			return;
		}
		// 获取商品id，虽然只有一个商品，为了公用接口，用集合存放
		List<String> skuIdList = skuList.stream().map(e -> e.getStoreSkuId()).collect(Collectors.toList());
		// 查询商品信息
		List<GoodsStoreSku> currentSkuList = goodsStoreSkuServiceApi.selectSkuByIds(skuIdList);
		// 检查商品信息是否存在
		if (CollectionUtils.isEmpty(currentSkuList)) {
			// 数据库中未查找到商品信息
			logger.warn("团购订单商品不存在");
			resp.setResult(ResultCodeEnum.GOODS_IS_CHANGE);
			return;
		}
		if(!currentSkuList.get(0).getStoreId().equals(paramDto.getStoreId())){
			// 如果商品和店铺信息不一致
			logger.warn("商品所属店铺{}与请求店铺{}不一致",currentSkuList.get(0).getStoreId(),paramDto.getStoreId());
			resp.setResult(ResultCodeEnum.STORE_SKU_INCONSISTENT);
			return;
		}
		StoreSkuParserBo parserBo = new StoreSkuParserBo(currentSkuList);
		parserBo.setSkuIdList(skuIdList);
		// 缓存商品解析结果
		paramDto.put("parserBo", parserBo);
		parserBo.parseCurrentSku();
		// 检查商品信息是否发生变化
		ResultCodeEnum checkResult = isChange(skuList, parserBo);
		resp.setResult(checkResult);
	}

	public ResultCodeEnum isChange(List<PlaceOrderItemDto> skuList, StoreSkuParserBo parserBo) {
		// 发生改变的商品列表
		List<PlaceOrderItemDto> changeSkuList = skuList.stream().filter(
				e -> !parserBo.getCurrentStoreSkuBo(e.getStoreSkuId()).getUpdateTime().equals(e.getUpdateTime()))
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(changeSkuList)) {
			// 发生改变的商品列表为空，则标识正常
			return ResultCodeEnum.SUCCESS;
		} else {
			return ResultCodeEnum.GOODS_IS_CHANGE;
		}
	}

}
