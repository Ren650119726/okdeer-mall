package com.okdeer.mall.order.service.impl;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuPicture;
import com.okdeer.archive.goods.store.enums.BSSC;
import com.okdeer.archive.goods.store.enums.GoodsStoreSkuPayTypeEnum;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuPictureServiceApi;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.mall.common.vo.Request;
import com.okdeer.mall.common.vo.Response;
import com.okdeer.mall.order.enums.OrderOptTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.vo.ServiceOrderReq;
import com.okdeer.mall.order.vo.ServiceOrderResp;
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
}
