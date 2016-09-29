package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuPicture;
import com.okdeer.archive.goods.store.enums.BSSC;
import com.okdeer.archive.goods.store.enums.GoodsStoreSkuPayTypeEnum;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuPictureServiceApi;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.store.entity.StoreInfoServiceExt;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.mall.common.vo.Request;
import com.okdeer.mall.common.vo.Response;
import com.okdeer.mall.order.enums.OrderOptTypeEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.vo.ServiceOrderReq;
import com.okdeer.mall.order.vo.ServiceOrderResp;
import com.okdeer.mall.order.vo.TradeOrderGoodsItem;
import com.okdeer.mall.order.vo.TradeOrderServiceGoodsItem;
import com.okdeer.mall.system.utils.ConvertUtil;

/**
 * ClassName: ServGoodsCheckServiceImpl 
 * @Description: 服务商品校验
 * @author maojj
 * @date 2016年9月21日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构1.1			2016年9月21日				maojj			服务商品校验
 */
@Service("servGoodsCheckService")
public class ServGoodsCheckServiceImpl implements RequestHandler<ServiceOrderReq,ServiceOrderResp> {

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
	
	@Override
	public void process(Request<ServiceOrderReq> req, Response<ServiceOrderResp> resp) throws Exception {
		ServiceOrderReq reqData = req.getData();
		ServiceOrderResp respData = resp.getData();
		
		// 服务店上门服务或者到店消费订单，订单项为list
		if (reqData.getOrderType() != null 
				&& (reqData.getOrderType().ordinal() == OrderTypeEnum.SERVICE_STORE_ORDER.ordinal()
				|| reqData.getOrderType().ordinal() == OrderTypeEnum.STORE_CONSUME_ORDER.ordinal()
						)) {
			// 上门服务或者到店消费订单商品校验
			this.checkGoodsDoorService(req, resp);
			return;
		}
		// 判断商品信息是否有更新
		GoodsStoreSku goodsStoreSku = goodsStoreSkuService.getById(reqData.getSkuId());
		if (goodsStoreSku == null || goodsStoreSku.getOnline() != BSSC.PUTAWAY) {
			resp.setResult(ResultCodeEnum.SERV_GOODS_NOT_EXSITS);
			req.setComplete(true);
			return;
		}
		Date goodsUpdateTime = DateUtils.parseDate(reqData.getGoodsUpdateTime());
		// 商品信息有更新
		if (goodsStoreSku.getUpdateTime().getTime() != goodsUpdateTime.getTime()) {
			resp.setResult(ResultCodeEnum.SERV_GOODS_IS_UPDATE);
			req.setComplete(true);
			return;
		}
		// 查询商品主图信息
		GoodsStoreSkuPicture goodsStoreSkuPicture = goodsStoreSkuPictureService
				.findMainPicByStoreSkuId(reqData.getSkuId());
		req.getContext().put("skuPrice", goodsStoreSku.getOnlinePrice());
		req.getContext().put("storeSku", goodsStoreSku);
		req.getContext().put("mainPic", goodsStoreSkuPicture.getUrl());

		// 设置响应信息
		if (req.getOrderOptType() == OrderOptTypeEnum.ORDER_SETTLEMENT) {
			respData.setSkuId(goodsStoreSku.getId());
			respData.setSkuName(goodsStoreSku.getName());
			respData.setSkuIcon(goodsStoreSkuPicture.getUrl());
			respData.setSkuNum(reqData.getSkuNum());
			respData.setUnitPrice(ConvertUtil.format(goodsStoreSku.getOnlinePrice()));
			respData.setLimitNum(goodsStoreSku.getTradeMax());
			// 设置商品的支付方式
			respData.setPaymentMode(getPaymentMode(goodsStoreSku.getPayType()));
		}
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
		
		// 数据库中对应的商品信息list
		List<GoodsStoreSku> goodsStoreSkuList = goodsStoreSkuService.findByIds(storeSkuIdList);
		
		if (CollectionUtils.isEmpty(goodsStoreSkuList)) {
			resp.setResult(ResultCodeEnum.SERV_GOODS_NOT_EXSITS);
			req.setComplete(true);
			return;
		}
		
		// 商品类目id
		List<String> spuCategoryIds = new ArrayList<String>();
		// 订单总价
		BigDecimal totalAmout = BigDecimal.ZERO;
		// 检测商品信息是否有变化
		for (GoodsStoreSku goodsStoreSku : goodsStoreSkuList) {
			if (goodsStoreSku == null || goodsStoreSku.getOnline() != BSSC.PUTAWAY) {
				resp.setResult(ResultCodeEnum.SERV_GOODS_NOT_EXSITS);
				req.setComplete(true);
				return;
			}
			Date goodsUpdateTime = DateUtils.parseDate(reqData.getGoodsUpdateTime());
			// 商品信息有更新
			if (goodsStoreSku.getUpdateTime().getTime() != goodsUpdateTime.getTime()) {
				resp.setResult(ResultCodeEnum.SERV_GOODS_IS_UPDATE);
				req.setComplete(true);
				return;
			}
			
			// 商品类目id
			spuCategoryIds.add(goodsStoreSku.getSpuCategoryId());
			
			// 计算订单总价
			for (TradeOrderGoodsItem item : listTemp) {
				if (goodsStoreSku.getId().equals(item.getSkuId())) {
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
					resp.setResult(ResultCodeEnum.SERV_ORDER_AMOUT_NOT_ENOUGH);
					req.setComplete(true);
					return;
				}
			}
		}
		
		List<TradeOrderServiceGoodsItem> list = new ArrayList<TradeOrderServiceGoodsItem>();
		TradeOrderServiceGoodsItem goodsItem = null;
		// 组装商品list
		for (GoodsStoreSku goodsStoreSku : goodsStoreSkuList) {
			goodsItem = new TradeOrderServiceGoodsItem();
			goodsItem.setSkuId(goodsStoreSku.getId());
			goodsItem.setSkuName(goodsStoreSku.getName());
			goodsItem.setUnitPrice(ConvertUtil.format(goodsStoreSku.getOnlinePrice()));
			goodsItem.setLimitNum(goodsStoreSku.getTradeMax());
			// 设置商品的支付方式
			goodsItem.setPaymentMode(getPaymentMode(goodsStoreSku.getPayType()));
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
		// 将商品信息返回
		respData.setList(list);
		// 店铺skuId集合
		req.getContext().put("storeSkuIdList", storeSkuIdList);
		// 类目id
		req.getContext().put("spuCategoryIds", spuCategoryIds);
		// 商品sku信息
		req.getContext().put("storeSkuList", goodsStoreSkuList);
		
		reqData.setTotalAmount(totalAmout);
	}
	// end add by wushp V1.1.0
}
