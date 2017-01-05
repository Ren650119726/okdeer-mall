package com.okdeer.mall.order.handler.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuStock;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuStockServiceApi;
import com.okdeer.archive.store.entity.StoreInfoServiceExt;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.utils.TradeOrderStock;
import com.okdeer.mall.order.vo.ServiceOrderReq;
import com.okdeer.mall.order.vo.ServiceOrderResp;
import com.okdeer.mall.order.vo.TradeOrderGoodsItem;

/**
 * ClassName: ServerCheckServiceImpl 
 * @Description: 库存校验
 * @author wushp
 * @date 2016年9月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.1.0			2016年9月28日				wushp		库存校验
 */
@Service("servStockCheckService")
public class ServStockCheckServiceImpl implements RequestHandler<ServiceOrderReq,ServiceOrderResp> {

	/**
	 * 店铺商品库存Service
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuStockServiceApi goodsStoreSkuStockService;
	
	@Override
	public void process(Request<ServiceOrderReq> req, Response<ServiceOrderResp> resp) throws Exception {
		ServiceOrderReq reqData = req.getData();
		ServiceOrderResp respData = resp.getData();
		List<TradeOrderGoodsItem> list = reqData.getList();
		
		Map<String, Object> context = req.getContext();
		if (!context.containsKey("storeSkuIdList")) {
			resp.setResult(ResultCodeEnum.SERV_GOODS_NOT_EXSITS);
			req.setComplete(true);
			return;
		}
		List<String> storeSkuIdList = (List<String>)context.get("storeSkuIdList");
		// 校验正常商品库存
		//GoodsStoreSkuStock goodsStoreSkuStock = goodsStoreSkuStockService.getBySkuId(orderReq.getSkuId());
		List<GoodsStoreSkuStock> stockList = goodsStoreSkuStockService
				.selectSingleSkuStockBySkuIdList(storeSkuIdList);
		
		// 存储返回的sku对应的库存信息列表
		List<TradeOrderStock> detail = new ArrayList<TradeOrderStock>();
		TradeOrderStock tradeOrderStock = null;
		//可销售库存是否充足标识，0：充足， 1：不足
		int stockEnoughFlag = 0;
		// 可销售库存
		int stockNum = 0;
		// 判断库存是否满足销售
		for (GoodsStoreSkuStock skuStock : stockList) {
			for (TradeOrderGoodsItem item : list) {
				if (skuStock.getStoreSkuId().equals(item.getSkuId())) {
					tradeOrderStock = new TradeOrderStock();
					tradeOrderStock.setSkuId(skuStock.getStoreSkuId());
					tradeOrderStock.setSellableStock(skuStock.getSellable());
					detail.add(tradeOrderStock);
 					if (skuStock.getSellable() < item.getSkuNum()) {
						// 可销售库存小雨售卖的商品数量
						stockEnoughFlag = 1;
						stockNum = skuStock.getSellable();
					}
 					break;
				}
			}
		}
		// 库存不足
		if (stockEnoughFlag == 1) {
			respData.setDetail(detail);
			if (req.getData().getOrderType() != null 
					&& req.getData().getOrderType().ordinal() == OrderTypeEnum.STORE_CONSUME_ORDER.ordinal()) {
				// 到店消费订单
				if (stockNum == 0) {
					resp.setResult(ResultCodeEnum.SERV_GOODS_NOT_ENOUGH);
				} else {
					resp.setCode(299);
					resp.setMessage("抱歉，该商品剩余不足，仅能购买" + stockNum + "件");
				}
			} else {
				// 上门服务订单  bug14079
				// 服务店铺扩展信息
				StoreInfoServiceExt storeserviceExt = respData.getStoreInfoServiceExt();
				if (storeserviceExt != null && storeserviceExt.getIsShoppingCart() == 0) {
					// 不支持购物车
					if (stockNum == 0) {
						// 剩余库存为0
						resp.setCode(233);
						resp.setMessage("抱歉，该商品预约已满");
					} else {
						// 库存不足，但是还有剩余库存
						resp.setCode(299);
						resp.setMessage("抱歉，服务预约已满仅能预约" + stockNum + "件");
					}
				} else {
					// 支持购物车，与便利店提示一致
					resp.setResult(ResultCodeEnum.GOODS_STOCK_NOT_ENOUGH);
				}
				
			}
			req.setComplete(true);
			return;
		}
	}
	
}
