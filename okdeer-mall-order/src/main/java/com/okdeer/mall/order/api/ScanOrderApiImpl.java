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
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.common.utils.TradeNumUtil;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.PlaceOrderDto;
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
	
	/**
	 * 订单支付超时时间（分钟）
	 */
	private static final int  TIME_OUT_MINUTES = 30;
	
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
		if(Integer.valueOf(resp.get(RespSelfJson.KEY_CODE).toString()) == 127){
			orderDetail.setCode(Integer.valueOf(resp.get(RespSelfJson.KEY_CODE).toString()));
			return orderDetail;
		}
		SelfPayTradeInfoVo tradeInfoVo = (SelfPayTradeInfoVo) resp.get(RespSelfJson.DATA);
		BeanMapper.copy(tradeInfoVo, orderDetail);
    	scanOrderFavourService.appendFavour(orderDetail, requestParams);
    	for (ScanOrderItemDto item : orderDetail.getList()) {
    		item.setSkuPic(findSkuPic(item.getSkuId(), requestParams.getScreen()));
    	}
    	return orderDetail;
    }
    
    
	/**
	 * 提交订单接口
	 * @throws Exception 
	 */
	@Override
	public Response<PlaceOrderDto> submitOrder(Request<PlaceOrderParamDto> reqDto, Response<PlaceOrderDto> resp,
			RequestParams requestParams) throws Exception {
	
    	//共用实物订单代金券校验
		StoreSkuParserBo bo = new StoreSkuParserBo(Lists.newArrayList());
		reqDto.getData().getCacheMap().put("parserBo", bo);
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

		//保存扫描购订单信息
		orderDetail.setRecordId(reqDto.getData().getRecordId());
		String branchId = reqDto.getData().getStoreId();
		scanOrderService.saveScanOrder(orderDetail, branchId,requestParams);
		//填充返回信息
		Response<PlaceOrderDto> respones = new Response<PlaceOrderDto>();
		fillResponse(respones,orderDetail);
		return respones;
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
		scanOrderParam.setPinAmount(new BigDecimal(orderParam.getPinMoney()));
		scanOrderParam.setPlatDiscountsAmount(platDiscountsAmount);
		return scanOrderParam;
	}


	/**
	 * @Description: 填充提交订单返回结果
	 * @param orderDetail   
	 * @author guocp
	 * @date 2017年10月16日
	 */
	private void fillResponse(Response<PlaceOrderDto> resp,ScanOrderDto orderDetail) {
		resp.getData().setOrderId(orderDetail.getOrderId());
		resp.getData().setOrderNo(orderDetail.getOrderNo());

		resp.getData().setTradeNum(orderDetail.getTradeNum());
		resp.getData().setOrderPrice(JsonDateUtil.priceConvertToString(orderDetail.getPaymentAmount(), 2, 3));
		resp.getData().setLimitTime(TIME_OUT_MINUTES * 60);
		resp.getData().setCurrentTime(System.currentTimeMillis());
		resp.getData().setPaymentMode(0);
		resp.getData().setIsReachPrice(1);
		// 平台优惠
		resp.getData().setFavour(JsonDateUtil.priceConvertToString(orderDetail.getPlatPreferentialAmount(), 2, 3));
		resp.getData().setUsablePinMoney(JsonDateUtil.priceConvertToString(orderDetail.getPinMoneyAmount(), 2, 3));
		// 店铺优惠金额
		resp.getData().setStoreFavour(JsonDateUtil.priceConvertToString(orderDetail.getDiscountAmount(), 2, 3));
		resp.getData().setOrderResource(OrderResourceEnum.SWEEP.ordinal());
		resp.getData().setOrderPrice(JsonDateUtil.priceConvertToString(orderDetail.getSaleAmount(),2, 3));
		resp.getData().setCurrentTime(System.currentTimeMillis());
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
