package com.okdeer.mall.order.handler.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuPicture;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuService;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuStock;
import com.okdeer.archive.goods.store.enums.BSSC;
import com.okdeer.archive.goods.store.enums.GoodsStoreSkuPayTypeEnum;
import com.okdeer.archive.goods.store.enums.IsShopNum;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuPictureServiceApi;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceServiceApi;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuStockServiceApi;
import com.okdeer.archive.store.entity.StoreInfoServiceExt;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.mall.common.vo.Request;
import com.okdeer.mall.common.vo.Response;
import com.okdeer.mall.order.enums.OrderOptTypeEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.utils.TradeOrderStock;
import com.okdeer.mall.order.vo.ServiceOrderReq;
import com.okdeer.mall.order.vo.ServiceOrderResp;
import com.okdeer.mall.order.vo.TradeOrderGoodsItem;
import com.okdeer.mall.order.vo.TradeOrderServiceGoodsItem;
import com.okdeer.mall.system.utils.ConvertUtil;

/**
 * ClassName: ServiceGoodsCheckServiceImpl 
 * @Description: 服务商品校验（上门服务，到店消费订单）
 * @author wushp
 * @date 2016年9月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构1.1			2016年9月29日				wushp			服务商品校验
 */
@Service("serviceGoodsCheckService")
public class ServiceGoodsCheckServiceImpl implements RequestHandler<ServiceOrderReq,ServiceOrderResp> {

	/**
	 * 商品信息Service
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuService;
	
	/**
	 * 商品图片信息Service
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuPictureServiceApi goodsStoreSkuPictureService;
	
	/**
	 * 到店消费服务商品service
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceServiceApi goodsStoreSkuServiceServiceApi;
	
	/**
	 * 店铺商品库存Service
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuStockServiceApi goodsStoreSkuStockService;
	
	@Override
	public void process(Request<ServiceOrderReq> req, Response<ServiceOrderResp> resp) throws Exception {
		// 上门服务或者到店消费订单商品校验
		this.checkGoodsDoorService(req, resp);
	}
	
	private int getPaymentMode(GoodsStoreSkuPayTypeEnum payType){
		int paymentMode = 0;
		switch (payType) {
			case onlinePay:
				paymentMode = PayWayEnum.PAY_ONLINE.ordinal();
				break;
			case offlinePay:
				paymentMode = PayWayEnum.OFFLINE_CONFIRM_AND_PAY.ordinal();
				break;
			default:
				paymentMode = PayWayEnum.PAY_ONLINE.ordinal();
				break;
		}
		return paymentMode;
	}
	
	// begin add by wushp V1.1.0
	/**
	 * 
	 * @Description: 上门服务订单商品校验
	 * @param req 请求参数
	 * @param resp 响应参数
	 * @author wushp
	 * @date 2016年9月27日
	 */
	private void checkGoodsDoorService(Request<ServiceOrderReq> req, Response<ServiceOrderResp> resp) 
			throws Exception {
		ServiceOrderReq reqData = req.getData();
		ServiceOrderResp respData = resp.getData();
		// 店铺商品skuId集合
		List<String> storeSkuIdList = new ArrayList<String>();
		List<TradeOrderGoodsItem> listTemp = reqData.getList();
		for (TradeOrderGoodsItem item : listTemp) {
			storeSkuIdList.add(item.getSkuId());
		}
		
		// 店铺skuId集合
		req.getContext().put("storeSkuIdList", storeSkuIdList);
		
		// 数据库中对应的商品信息list
		//List<GoodsStoreSku> goodsStoreSkuList = goodsStoreSkuService.findByIds(storeSkuIdList);
		List<GoodsStoreSku> goodsStoreSkuList = goodsStoreSkuService.selectSkuByIds(storeSkuIdList);
		if (CollectionUtils.isEmpty(goodsStoreSkuList)) {
			resp.setResult(ResultCodeEnum.SERV_GOODS_NOT_EXSITS);
			req.setComplete(true);
			return;
		}
		
		List<TradeOrderServiceGoodsItem> list = new ArrayList<TradeOrderServiceGoodsItem>();
		TradeOrderServiceGoodsItem goodsItem = null;
		// 组装商品list
		for (GoodsStoreSku goodsStoreSku : goodsStoreSkuList) {
			goodsItem = new TradeOrderServiceGoodsItem();
			goodsItem.setSkuId(goodsStoreSku.getId());
			goodsItem.setSkuName(goodsStoreSku.getName());
			goodsItem.setUnitPrice(goodsStoreSku.getOnlinePrice());
			goodsItem.setLimitNum(goodsStoreSku.getTradeMax());
			goodsItem.setUpdateTime(DateUtils.formatDateTime(goodsStoreSku.getUpdateTime()));
			goodsItem.setSkuType(goodsStoreSku.getSpuTypeEnum().ordinal());
			// 是否上架，0:下架、1:上架
			goodsItem.setOnline(goodsStoreSku.getOnline().ordinal());
			// 设置商品的支付方式
			goodsItem.setPaymentMode(getPaymentMode(goodsStoreSku.getPayType()));
			respData.setPaymentMode(goodsItem.getPaymentMode());
			
			GoodsStoreSkuService skuServiceEntity = goodsStoreSku.getGoodsStoreSkuService();
			if (skuServiceEntity != null) {
				// 是否有起购量 0：无 1：有
				IsShopNum isShopNum = skuServiceEntity.getIsShopNum();
				if (isShopNum != null && isShopNum.ordinal() == IsShopNum.YES.ordinal()) {
					// 有起购量
					goodsItem.setShopNum(skuServiceEntity.getShopNum());
				} else {
					// 无起购量
					goodsItem.setShopNum(1);
				}
			}
			// 商品主图信息列表
			List<GoodsStoreSkuPicture> storeSkuPicList = goodsStoreSkuPictureService
					.findMainByStoreSkuIds(storeSkuIdList);
			for (GoodsStoreSkuPicture goodsStoreSkuPicture : storeSkuPicList) {
				if (goodsStoreSku.getId().equals(goodsStoreSkuPicture.getStoreSkuId())) {
					// 设置商品主图
					goodsItem.setSkuIcon(goodsStoreSkuPicture.getUrl());
				} 
			}
			for (TradeOrderGoodsItem item : listTemp) {
				if (goodsStoreSku.getId().equals(item.getSkuId())) {
					// 设置商品数量
					goodsItem.setSkuNum(item.getSkuNum());
				}
			}
			list.add(goodsItem);
		}
		if (goodsStoreSkuList.size() > 1) {
			// 商品列表大于1，说明支持购物车，商品支付方式设为线上支付，因为线下支付只能单款商品购买
			respData.setPaymentMode(PayWayEnum.PAY_ONLINE.ordinal());
		}
		// 将商品信息返回
		respData.setList(list);
		// 组装库存信息并返回
		this.buildRespGoodsStock(req, resp);
		
		// 商品类目id
		List<String> spuCategoryIds = new ArrayList<String>();
		// 订单总价
		BigDecimal totalAmout = BigDecimal.ZERO;
		Date goodsUpdateTime = null;
		// 检测商品信息是否有变化
		for (GoodsStoreSku goodsStoreSku : goodsStoreSkuList) {
			if (goodsStoreSku == null || goodsStoreSku.getOnline() != BSSC.PUTAWAY) {
				// bug 14150
				if (reqData.getOrderType().ordinal() == OrderTypeEnum.SERVICE_STORE_ORDER.ordinal()) {
					// 上门服务
					resp.setResult(ResultCodeEnum.SERV_GOODS_NOT_EXSITS);
				} else {
					// 到店消费
					resp.setResult(ResultCodeEnum.SERV_GOODS_NOT_BUY);
				}
				
				
				req.setComplete(true);
				return;
			}
			
			// 判断到店消费商品是否已过有效期
			if (reqData.getOrderType().ordinal() == OrderTypeEnum.STORE_CONSUME_ORDER.ordinal()) {
				GoodsStoreSkuService skuService = goodsStoreSkuServiceServiceApi.selectBySkuId(goodsStoreSku.getId());
				if (skuService == null) {
					resp.setResult(ResultCodeEnum.SERV_GOODS_NOT_EXSITS_1);
					req.setComplete(true);
					return;
				}
				
				Date endTime = skuService.getEndTime();
				if (new Date().compareTo(endTime) == 0 || new Date().compareTo(endTime) == 1) {
					// 服务商品已过期，不能预约
					resp.setResult(ResultCodeEnum.SERV_GOODS_EXP);
					req.setComplete(true);
					return;
				} 
			}
			
			
			// 商品类目id
			spuCategoryIds.add(goodsStoreSku.getSpuCategoryId());
			
			// 计算订单总价
			for (TradeOrderGoodsItem item : listTemp) {
				if (goodsStoreSku.getId().equals(item.getSkuId())) {
					goodsUpdateTime = DateUtils.parseDate(item.getUpdateTime());
					// 商品信息有更新
					if (goodsStoreSku.getUpdateTime().getTime() != goodsUpdateTime.getTime()) {
						resp.setResult(ResultCodeEnum.SERV_GOODS_IS_UPDATE);
						req.setComplete(true);
						return;
					}
					
					BigDecimal onlinePrice = goodsStoreSku.getOnlinePrice();
					totalAmout = totalAmout.add(onlinePrice.multiply(BigDecimal.valueOf(item.getSkuNum())));
				}
			}
			
		}
		// 上门服务订单，如果商家中心设置起送价，不满起送价不可下单，提示
		// 提示‘抱歉，订单不满起送价，请重新结算’且页面跳回至购物车页面，购物车页面刷新，获取最新的起送价、配送费信息
		if (reqData.getOrderType().ordinal() == OrderTypeEnum.SERVICE_STORE_ORDER.ordinal()) {
			// 服务店扩展信息
			StoreInfoServiceExt serviceExt = respData.getStoreInfoServiceExt();
			if (serviceExt != null && serviceExt.getIsShoppingCart() == 1 
					&& serviceExt.getIsStartingPrice() == 1 
					&& serviceExt.getIsSupportPurchase() == 0) {
				BigDecimal startingPrice = serviceExt.getStartingPrice();
				if (totalAmout.compareTo(startingPrice) == -1) {
					// 订单总价小与起送价
					if (req.getOrderOptType().ordinal() == OrderOptTypeEnum.ORDER_SUBMIT.ordinal()) {
						// 提交订单
						resp.setResult(ResultCodeEnum.SERV_ORDER_AMOUT_NOT_ENOUGH);
					} else {
						// 确认订单
						// bug14207
						resp.setCode(227);
						resp.setMessage("抱歉，商品金额不满起送价");
					}
					req.setComplete(true);
					return;
				}
			}
			
			if (serviceExt != null && serviceExt.getIsShoppingCart() == 1
					&& serviceExt.getIsDistributionFee() == 1) {
				// 支持购物车并且有配送费
				if (serviceExt.getIsStartingPrice() == 1) {
					// 有起送价
					if (serviceExt.getIsCollect() == 1) {
						// 已满起送价收取配送费
						respData.setFare(BigDecimal.valueOf(serviceExt.getDistributionFee()));
					} else {
						// 已满起送价不收取配送费
						BigDecimal startingPrice = serviceExt.getStartingPrice();
						if (totalAmout.compareTo(startingPrice) == -1) {
							// 设置运费
							respData.setFare(BigDecimal.valueOf(serviceExt.getDistributionFee()));
						}
					}
					
				} else {
					// 没有起送价
					if (serviceExt.getIsCollect() == 1) {
						// 已满起送价收取配送费，0：否，1：是
						// 设置运费
						respData.setFare(BigDecimal.valueOf(serviceExt.getDistributionFee()));
					}
					
				}
			}
		}
		
		for (TradeOrderGoodsItem goodsStoreSku : reqData.getList()) {
			// 商品主图信息列表
			List<GoodsStoreSkuPicture> storeSkuPicList = goodsStoreSkuPictureService
					.findMainByStoreSkuIds(storeSkuIdList);
			for (GoodsStoreSkuPicture goodsStoreSkuPicture : storeSkuPicList) {
				if (goodsStoreSku.getSkuId().equals(goodsStoreSkuPicture.getStoreSkuId())) {
					// 设置商品主图
					goodsStoreSku.setSkuIcon(goodsStoreSkuPicture.getUrl());
				} 
			}
			
			for (GoodsStoreSku goodsItems : goodsStoreSkuList) {
				if (goodsStoreSku.getSkuId().equals(goodsItems.getId())) {
					goodsStoreSku.setSkuPrice(goodsItems.getOnlinePrice());
				}
			}
		}
		
		// 类目id
		req.getContext().put("spuCategoryIds", spuCategoryIds);
		// 商品sku信息
		req.getContext().put("storeSkuList", goodsStoreSkuList);
		
		reqData.setTotalAmount(totalAmout);
	}
	
	/**
	 * 
	 * @Description: 组装商品库存信息
	 * @param req 请求
	 * @param resp 响应
	 * @throws Exception 异常
	 * @author wushp
	 * @date 2016年10月10日
	 */
	public void buildRespGoodsStock(Request<ServiceOrderReq> req, Response<ServiceOrderResp> resp) throws Exception {
		List<String> storeSkuIdList = (List<String>)req.getContext().get("storeSkuIdList");
		// 商品库存
		List<GoodsStoreSkuStock> stockList = goodsStoreSkuStockService
				.selectSingleSkuStockBySkuIdList(storeSkuIdList);
		
		List<TradeOrderGoodsItem> list = req.getData().getList();
		// 存储返回的sku对应的库存信息列表
		List<TradeOrderStock> detail = new ArrayList<TradeOrderStock>();
		TradeOrderStock tradeOrderStock = null;
		// 组装商品库存信息
		for (GoodsStoreSkuStock skuStock : stockList) {
			for (TradeOrderGoodsItem item : list) {
				if (skuStock.getStoreSkuId().equals(item.getSkuId())) {
					tradeOrderStock = new TradeOrderStock();
					tradeOrderStock.setSkuId(skuStock.getStoreSkuId());
					tradeOrderStock.setSellableStock(skuStock.getSellable());
					detail.add(tradeOrderStock);
 					break;
				}
			}
		}
		// 返回商品库存信息
		resp.getData().setDetail(detail);
	}
	// end add by wushp V1.1.0
}
