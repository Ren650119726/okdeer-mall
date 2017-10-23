/** 
 *@Project: okdeer-mall-order 
 *@Author: guocp
 *@Date: 2017年10月13日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.order.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuPicture;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuPictureServiceApi;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.base.common.model.RequestParams;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.redis.IRedisTemplateWrapper;
import com.okdeer.common.utils.ImageCutUtils;
import com.okdeer.common.utils.ImageTypeContants;
import com.okdeer.common.utils.JsonDateUtil;
import com.okdeer.jxc.branch.entity.Branches;
import com.okdeer.jxc.branch.service.BranchesServiceApi;
import com.okdeer.jxc.common.result.RespSelfJson;
import com.okdeer.jxc.pos.service.SelfPayOrderServiceApi;
import com.okdeer.jxc.pos.vo.SelfOrderVo;
import com.okdeer.jxc.pos.vo.SelfPayTradeInfoVo;
import com.okdeer.jxc.pos.vo.SelfPrepayVo;
import com.okdeer.jxc.sale.goods.entity.PosGoods;
import com.okdeer.jxc.sale.goods.qo.GoodsInfoQo;
import com.okdeer.jxc.sale.goods.service.PosGoodsService;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.common.utils.TradeNumUtil;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderItemDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.dto.ScanOrderDetailDto;
import com.okdeer.mall.order.dto.ScanOrderDto;
import com.okdeer.mall.order.dto.ScanOrderItemDto;
import com.okdeer.mall.order.dto.ScanOrderParamDto;
import com.okdeer.mall.order.dto.ScanPosStoreDto;
import com.okdeer.mall.order.dto.ScanPosStoreSkuDto;
import com.okdeer.mall.order.dto.ScanSkuParamDto;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.service.ScanOrderApi;
import com.okdeer.mall.order.service.ScanOrderFavourService;
import com.okdeer.mall.order.service.ScanOrderService;
import com.site.lookup.util.StringUtils;

/**
 * ClassName: ScanOrderApiImpl 
 * @Description: APP扫码购接口
 * @author guocp
 * @date 2017年10月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version="1.0.0")
public class ScanOrderApiImpl implements ScanOrderApi {
	
	private static final Logger logger = LoggerFactory.getLogger(ScanOrderApiImpl.class);

	/**
	* 店铺商品图片域名
	*/
	@Value("${goodsImagePrefix}")
	private String goodsImagePrefix;
	
	@Resource
	private IRedisTemplateWrapper<String, Object> redisTemplateWrapper;
	
	/**
	 * 订单支付超时时间（分钟）
	 */
	private static final int  TIME_OUT_MINUTES = 30;
	
	private static final String SCAN_ORDER_PRDFIX = "scanOrder:";
	
	@Autowired
	private ScanOrderFavourService scanOrderFavourService;

	/**
	 * 商品图片
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuPictureServiceApi goodsStoreSkuPictureService;

    @Reference(version = "1.0.0", check = false)
    private SelfPayOrderServiceApi selfPayOrderServiceApi;
    
    @Reference(version = "1.0.0", check = false)
    private PosGoodsService posGoodsService;
    
    @Reference(version = "1.0.0", check = false)
    private BranchesServiceApi branchesServiceApi;
    
    @Autowired
    private ScanOrderService scanOrderService;
    
    /**
 	 * 优惠信息校验
 	 */
 	@Resource
 	RequestHandler<PlaceOrderParamDto, PlaceOrderDto>  checkFavourService;
 	/**
 	 * 优惠信息校验
 	 */
 	@Resource
 	RequestHandler<PlaceOrderParamDto, PlaceOrderDto>  checkPinMoneyService;
    
    
    /**
     * 确认订单接口
     * @throws Exception 
     */
    @Override
    public ScanOrderDto confirmOrder(ScanOrderParamDto scanOrderDto, RequestParams requestParams) throws Exception {
    	SelfPrepayVo prepayDto = BeanMapper.map(scanOrderDto, SelfPrepayVo.class);
    	prepayDto.setSerialNum(TradeNumUtil.getTradeNum());
    	//调用零售确认订单接口
    	RespSelfJson resp = selfPayOrderServiceApi.settlementOrder(prepayDto);
    	//验证返回结果
    	ScanOrderDto orderDetail = new ScanOrderDto();
    	orderDetail.setOrderResource(scanOrderDto.getOrderResource());
		if(Integer.valueOf(resp.get(RespSelfJson.KEY_CODE).toString()) != 0){
			orderDetail.setCode(Integer.valueOf(resp.get(RespSelfJson.KEY_CODE).toString()));
			logger.error("扫码购确认订单失败，code:{},原因：{}",resp.get(RespSelfJson.KEY_CODE),resp.get(RespSelfJson.KEY_MESSAGE));
			return orderDetail;
		}
		SelfPayTradeInfoVo tradeInfoVo = (SelfPayTradeInfoVo) resp.get(RespSelfJson.DATA);
		BeanMapper.copy(tradeInfoVo, orderDetail);
		logger.info("订单详情：{}",JsonMapper.nonDefaultMapper().toJson(orderDetail));
    	scanOrderFavourService.appendFavour(orderDetail, requestParams);
    	for (ScanOrderItemDto item : orderDetail.getList()) {
    		item.setSkuPic(findSkuPic(item.getSkuId(), requestParams.getScreen()));
    	}
    	redisTemplateWrapper.set(SCAN_ORDER_PRDFIX+orderDetail.getOrderId(), orderDetail, TIME_OUT_MINUTES, TimeUnit.MINUTES);
    	return orderDetail;
    }
    
    
	/**
	 * 提交订单接口
	 * @throws Exception 
	 */
	@Override
	public Response<PlaceOrderDto> submitOrder(Request<PlaceOrderParamDto> reqDto, Response<PlaceOrderDto> resp,
			RequestParams requestParams) throws Exception {
		PlaceOrderParamDto orderParamDto = reqDto.getData();
    	//共用实物订单代金券校验
		StoreSkuParserBo bo = new StoreSkuParserBo(Lists.newArrayList());
		orderParamDto.getCacheMap().put("parserBo", bo);
		orderParamDto.setChannel(String.valueOf(OrderResourceEnum.WECHAT_MIN.ordinal()));
		if(orderParamDto.getActivityType() == null){
			orderParamDto.setActivityType(String.valueOf(ActivityTypeEnum.NO_ACTIVITY.ordinal()));
		}else{
			ScanOrderDto orderDetail = (ScanOrderDto) redisTemplateWrapper.get(SCAN_ORDER_PRDFIX+orderParamDto.getOrderId());
			PlaceOrderItemDto item = new PlaceOrderItemDto();
			//设置可优惠金额 用于代金券校验，设置可以优惠金额到订单项中，由于优惠校验
			item.setTotalAmount(orderDetail.getTotalAmount());
			item.setSkuPrice(orderDetail.getTotalAmount());
			item.setQuantity(1);
			item.setSkuActType(ActivityTypeEnum.NO_ACTIVITY.ordinal());
			item.setSkuActType(0);
			List<PlaceOrderItemDto> list= Lists.newArrayList();
			list.add(item);
			reqDto.getData().setSkuList(list);
			bo.setTotalAmountHaveFavour(orderDetail.getAllowDiscountsAmount());
		}
		//检查代金券
    	checkFavourService.process(reqDto, resp);
    	if(!resp.isSuccess()){
    		return resp;
    	}
    	//检查零花钱
    	checkPinMoneyService.process(reqDto, resp);
    	if(!resp.isSuccess()){
    		return resp;
    	}
    	
		//构建请求参数
		SelfOrderVo orderParam = builderParams(reqDto.getData(),bo.getPlatformPreferential());
		// 调用零售提交订单接口 
		RespSelfJson jxcResp = selfPayOrderServiceApi.getOrderInfo(orderParam );
    	SelfPayTradeInfoVo tradeInfoVo = (SelfPayTradeInfoVo) jxcResp.get(RespSelfJson.DATA);
    	ScanOrderDto orderDetail = BeanMapper.map(tradeInfoVo, ScanOrderDto.class);
    	orderDetail.setPinMoneyAmount(tradeInfoVo.getPinAmount());
    	orderDetail.setActivityType(orderParamDto.getActivityType());
    	orderDetail.setCouponsId(orderParamDto.getActivityItemId());
		//保存扫描购订单信息
		orderDetail.setRecordId(reqDto.getData().getRecordId());
		scanOrderService.saveScanOrder(orderDetail, orderDetail.getBranchId(),requestParams);
		//填充返回信息
		fillResponse(resp,orderDetail);
		return resp;
	}
	
	/**
	 * @Description: 构建零售提交订单接口参数
	 * @param reqDto   
	 * @author guocp
	 * @date 2017年10月17日
	 */
	private SelfOrderVo builderParams(PlaceOrderParamDto orderParam,BigDecimal platDiscountsAmount) {
		SelfOrderVo scanOrderParam = new SelfOrderVo();
		scanOrderParam.setOrderId(orderParam.getOrderId());		
		BigDecimal pinAmount = orderParam.getPinMoney() == null ? BigDecimal.ZERO
				: new BigDecimal(orderParam.getPinMoney());
		scanOrderParam.setPinAmount(pinAmount);
		scanOrderParam.setPlatDiscountAmount(platDiscountsAmount);
		return scanOrderParam;
	}


	/**
	 * @Description: 填充提交订单返回结果
	 * @param orderDetail   
	 * @author guocp
	 * @date 2017年10月16日
	 */
	private void fillResponse(Response<PlaceOrderDto> resp,ScanOrderDto orderDetail) {
		PlaceOrderDto placeOrderDto = new PlaceOrderDto();
		placeOrderDto.setOrderId(orderDetail.getOrderId());
		placeOrderDto.setOrderNo(orderDetail.getOrderNo());

		placeOrderDto.setTradeNum(orderDetail.getTradeNum());
		placeOrderDto.setOrderPrice(JsonDateUtil.priceConvertToString(orderDetail.getPaymentAmount(), 2, 3));
		placeOrderDto.setLimitTime(TIME_OUT_MINUTES * 60);
		placeOrderDto.setCurrentTime(System.currentTimeMillis());
		placeOrderDto.setPaymentMode(0);
		placeOrderDto.setIsReachPrice(1);
		// 平台优惠
		placeOrderDto.setFavour(JsonDateUtil.priceConvertToString(orderDetail.getPlatDiscountAmount(), 2, 3));
		placeOrderDto.setUsablePinMoney(JsonDateUtil.priceConvertToString(orderDetail.getPinMoneyAmount(), 2, 3));
		// 店铺优惠金额
		placeOrderDto.setStoreFavour(JsonDateUtil.priceConvertToString(orderDetail.getDiscountAmount(), 2, 3));
		placeOrderDto.setOrderResource(OrderResourceEnum.SWEEP.ordinal());
		placeOrderDto.setOrderPrice(JsonDateUtil.priceConvertToString(orderDetail.getSaleAmount(),2, 3));
		placeOrderDto.setCurrentTime(System.currentTimeMillis());
		resp.setData(placeOrderDto);
		resp.setResult(ResultCodeEnum.SUCCESS);
	}


	/**
	 * 查找订单详情
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.ScanOrderApi#findOrder(com.okdeer.base.common.model.RequestParams)
	 */
	@Override
	public ScanOrderDetailDto findOrder(String orderNo,RequestParams requestParams) {
		// 调用零售接口
		RespSelfJson resp = selfPayOrderServiceApi.getOrderInfoByOrderNo(orderNo);
		com.okdeer.jxc.bill.entity.TradeOrder order = (com.okdeer.jxc.bill.entity.TradeOrder) resp
				.get(RespSelfJson.DATA);
		ScanOrderDetailDto scanOrderDetail = BeanMapper.map(order, ScanOrderDetailDto.class);
		for(ScanOrderItemDto orderItem : scanOrderDetail.getList()){
			Optional<String> skuPic = Optional.ofNullable(findSkuPic(orderItem.getId(), requestParams.getScreen()));
			orderItem.setSkuPic(skuPic.orElse(null));
		}
		return scanOrderDetail;
	}

	/**
	 * 扫描商品
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.ScanOrderApi#scanGoods(com.okdeer.base.common.model.RequestParams)
	 */
	@Override
	public ScanPosStoreDto scanGoods(ScanSkuParamDto scanSkuParam ,RequestParams requestParams) {
		if (StringUtils.isNotEmpty(scanSkuParam.getBranchId()) && StringUtils.isEmpty(scanSkuParam.getBarCode())) {
			return getJxcStoreId(scanSkuParam.getBranchId());
		}
		GoodsInfoQo ginfoQo = BeanMapper.map(scanSkuParam, GoodsInfoQo.class);
		List<PosGoods> posGoods = posGoodsService.getGoodsInfo(ginfoQo);
		List<ScanPosStoreSkuDto> scanPosSkus = Lists.newArrayList(); 
		posGoods.forEach((PosGoods e)->{
			ScanPosStoreSkuDto sku = BeanMapper.map(e, ScanPosStoreSkuDto.class);
			Optional<String> skuPic = Optional.ofNullable(findSkuPic(sku.getId(), requestParams.getScreen()));
			sku.setSkuPic(skuPic.orElse(null));
			scanPosSkus.add(sku);
		});
		ScanPosStoreDto dto = new ScanPosStoreDto();
		dto.setList(scanPosSkus);
		return dto;
	}
	
	/**
	 * 扫描店铺二维码
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.ScanOrderApi#getJxcStoreId(java.lang.String)
	 */
	public ScanPosStoreDto getJxcStoreId(String branchId){
		Branches branches = branchesServiceApi.getBranchInfoById(branchId);
		return BeanMapper.map(branches, ScanPosStoreDto.class);
	}
	
	
	/**
	 * 查询商品线上图片
	 * @Description: 
	 * @param goodsSkuId 店铺商品 skuId
	 * @param screen 手机分辨率
	 */
	private String findSkuPic(String goodsSkuId, String screen) {
		GoodsStoreSkuPicture pic = goodsStoreSkuPictureService.findMainPicByStoreSkuId(goodsSkuId);
		if (pic == null) {
			return null;
		}
		String goodsPic = ImageCutUtils.changeType(ImageTypeContants.SPXQYSPTP, goodsImagePrefix + pic.getUrl(),
				screen);
		return goodsPic;
	}

}
