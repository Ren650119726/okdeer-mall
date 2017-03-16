package com.okdeer.mall.order.handler.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuStock;
import com.okdeer.archive.goods.store.enums.BSSC;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.stock.service.GoodsStoreSkuStockApi;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.entity.StoreInfoServiceExt;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.bo.CurrentStoreSkuBo;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderItemDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PlaceOrderTypeEnum;
import com.okdeer.mall.order.handler.RequestHandler;

/**
 * ClassName: CheckServSkuServiceImpl 
 * @Description: 检查服务商品信息
 * @author maojj
 * @date 2017年1月5日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.0 			2017年1月5日				maojj
 */
@Service("checkServSkuService")
public class CheckServSkuServiceImpl implements RequestHandler<PlaceOrderParamDto, PlaceOrderDto> {

	/**
	 * 商品信息Service
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;
	
	/**
	 * 店铺商品库存Service
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuStockApi goodsStoreSkuStockApi;

	@Override
	public void process(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		PlaceOrderParamDto paramDto = req.getData();
		// 提取下单商品ID清单
		List<String> skuIdList = extractSkuId(paramDto.getSkuList());
		// 查询当前商品信息
		List<GoodsStoreSku> currentSkuList = findCurrentSkuList(skuIdList);
		// 判断商品列表与请求清单是否一致
		if (currentSkuList.size() != skuIdList.size()) {
			if (paramDto.getSkuList().size() > 1) {
				resp.setResult(ResultCodeEnum.PART_GOODS_IS_CHANGE);
			} else {
				resp.setResult(ResultCodeEnum.GOODS_IS_CHANGE);
			}
			return;
		}
		StoreSkuParserBo parserBo = new StoreSkuParserBo(currentSkuList);
		parserBo.setSkuIdList(skuIdList);
		// 缓存商品解析结果
		paramDto.put("parserBo", parserBo);
		parserBo.parseCurrentSku();
		if(paramDto.getOrderType() == PlaceOrderTypeEnum.SECKILL_ORDER){
			parserBo.processSeckill();
		}
		parserBo.loadBuySkuList(paramDto.getSkuList());
		List<GoodsStoreSkuStock> stockList = goodsStoreSkuStockApi.findByStoreSkuIdList(skuIdList);
		parserBo.loadStockList(stockList);
		// 检查请求的商品类型
		checkSkuType(paramDto,parserBo);
		// 检查商品信息是否发生变化
		ResultCodeEnum checkResult = isChange(paramDto, parserBo);
		// 检查商品信息是否发生变化
		if (checkResult != ResultCodeEnum.SUCCESS) {
			resp.setResult(checkResult);
			return;
		}
		// 检查店铺配送费信息
		checkResult = checkFare(paramDto, parserBo);
		if (checkResult != ResultCodeEnum.SUCCESS) {
			resp.setResult(checkResult);
			return;
		}
	}

	private List<String> extractSkuId(List<PlaceOrderItemDto> itemList) {
		List<String> skuIdList = new ArrayList<String>();
		for (PlaceOrderItemDto item : itemList) {
			skuIdList.add(item.getStoreSkuId());
		}
		return skuIdList;
	}

	public List<GoodsStoreSku> findCurrentSkuList(List<String> skuIdList) throws Exception {
		return goodsStoreSkuServiceApi.selectSkuByIds(skuIdList);
	}

	/**
	 * @Description: 检查服务商品类型
	 * @param paramDto
	 * @param parserBo   
	 * @author maojj
	 * @date 2017年1月17日
	 */
	public void checkSkuType(PlaceOrderParamDto paramDto,StoreSkuParserBo parserBo){
		// 多个商品，商品类型均是一种类型，获取第一个即可判定。仅仅只对服务店商品做检查更新
		CurrentStoreSkuBo skuBo = parserBo.getCurrentStoreSkuBo(paramDto.getSkuList().get(0).getStoreSkuId());
		switch (skuBo.getSpuType()) {
			case fwdSpu:
				paramDto.updateSkuType(OrderTypeEnum.SERVICE_STORE_ORDER);
				break;
			case fwdDdxfSpu:
				paramDto.updateSkuType(OrderTypeEnum.STORE_CONSUME_ORDER);
				break;
			default:
				break;
		}
	}
	
	public ResultCodeEnum isChange(PlaceOrderParamDto paramDto, StoreSkuParserBo parserBo) {
		ResultCodeEnum checkResult = ResultCodeEnum.SUCCESS;
		int buyKindSize = paramDto.getSkuList().size();
		// 检查商品信息是否发生变化
		for (PlaceOrderItemDto item : paramDto.getSkuList()) {
			CurrentStoreSkuBo currentSku = parserBo.getCurrentStoreSkuBo(item.getStoreSkuId());
			// 检查是否下架
			if (currentSku.getOnline() == BSSC.UNSHELVE) {
				// 商品下架
				if (paramDto.getSkuType() == OrderTypeEnum.SERVICE_STORE_ORDER) {
					if(buyKindSize > 1){
						checkResult = ResultCodeEnum.PART_SERV_GOODS_IS_OFFLINE;
					}else{
						checkResult = ResultCodeEnum.SERV_GOODS_IS_OFFLINE;
					}
				} else {
					if(buyKindSize > 1){
						checkResult = ResultCodeEnum.PART_SERV_GOODS_EXP;
					}else{
						checkResult = ResultCodeEnum.SERV_GOODS_EXP;
					}
				}
			} else if (paramDto.getSkuType() == OrderTypeEnum.STORE_CONSUME_ORDER) {
				// 判断到店消费商品是否已过有效期
				Date endTime = currentSku.getEndTime();
				if (endTime == null || new Date().after(endTime)) {
					// 服务商品已过期，不能预约
					if(buyKindSize > 1){
						checkResult = ResultCodeEnum.PART_SERV_GOODS_EXP;
					}else{
						checkResult = ResultCodeEnum.SERV_GOODS_EXP;
					}
				}
			} 
 			if(checkResult == ResultCodeEnum.SUCCESS && !currentSku.getUpdateTime().equals(item.getUpdateTime())){
 				if(buyKindSize > 1){
					checkResult = ResultCodeEnum.PART_GOODS_IS_CHANGE;
				}else{
					checkResult = ResultCodeEnum.GOODS_IS_CHANGE;
				}
			}

			if (checkResult != ResultCodeEnum.SUCCESS) {
				break;
			}
		}
		return checkResult;
	}

	public ResultCodeEnum checkFare(PlaceOrderParamDto paramDto, StoreSkuParserBo parserBo) {
		ResultCodeEnum checkResult = ResultCodeEnum.SUCCESS;
		// 秒杀订单不收取运费
		if(paramDto.getOrderType() == PlaceOrderTypeEnum.SECKILL_ORDER){
			return checkResult;
		}
		
		if (paramDto.getSkuType() == OrderTypeEnum.SERVICE_STORE_ORDER) {
			// 商品总金额
			BigDecimal totalAmount = parserBo.getTotalItemAmount();
			// 服务店扩展信息
			StoreInfoServiceExt serviceExt = ((StoreInfo) paramDto.get("storeInfo")).getStoreInfoServiceExt();
			if (serviceExt != null && serviceExt.getIsShoppingCart() == 1 && serviceExt.getIsStartingPrice() == 1
					&& serviceExt.getIsSupportPurchase() == 0) {
				BigDecimal startingPrice = serviceExt.getStartingPrice();
				if (totalAmount.compareTo(startingPrice) == -1) {
					checkResult = ResultCodeEnum.SERV_ORDER_AMOUT_NOT_ENOUGH;
				}
			}
			if (serviceExt != null && serviceExt.getIsShoppingCart() == 1 && serviceExt.getIsDistributionFee() == 1) {
				// 支持购物车并且有配送费
				if (serviceExt.getIsStartingPrice() == 1) {
					// 有起送价
					if (serviceExt.getIsCollect() == 1) {
						// 已满起送价收取配送费
						parserBo.setFare(BigDecimal.valueOf(serviceExt.getDistributionFee()));
					} else {
						if (totalAmount.compareTo(serviceExt.getStartingPrice()) == -1) {
							// 设置运费
							parserBo.setFare(BigDecimal.valueOf(serviceExt.getDistributionFee()));
						}
					}

				} else {
					// 没有起送价
					if (serviceExt.getIsCollect() == 1) {
						// 已满起送价收取配送费，0：否，1：是
						// 设置运费
						parserBo.setFare(BigDecimal.valueOf(serviceExt.getDistributionFee()));
					}

				}
			}

		}
		return checkResult;
	}
}
