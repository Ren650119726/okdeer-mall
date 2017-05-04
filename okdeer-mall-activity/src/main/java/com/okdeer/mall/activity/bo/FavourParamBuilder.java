package com.okdeer.mall.activity.bo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Lists;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.common.utils.EnumAdapter;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.member.member.service.MemberConsigneeAddressServiceApi;
import com.okdeer.mall.member.member.vo.UserAddressVo;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderItemDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.vo.ServiceOrderReq;
import com.okdeer.mall.order.vo.ServiceOrderResp;
import com.okdeer.mall.order.vo.TradeOrderGoodsItem;
import com.okdeer.mall.order.vo.TradeOrderReq;
import com.okdeer.mall.order.vo.TradeOrderReqDto;

/**
 * ClassName: FavourParamBuilder 
 * @Description: 优惠参数构建者
 * @author maojj
 * @date 2017年2月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.1 			2017年2月15日			maojj		                   优惠参数构建者
 */
@Component
public class FavourParamBuilder {
	
	/**
	 * 地址
	 */
	@Reference(version = "1.0.0", check = false)
	private MemberConsigneeAddressServiceApi memberConsigneeAddressService;

	/**
	 * @Description: 订单结算时，根据订单请求构建查询优惠信息的参数
	 * @param paramDto
	 * @param orderDto
	 * @param postProcessor
	 * @return   
	 * @author maojj
	 * @date 2017年2月15日
	 */
	public FavourParamBO build(PlaceOrderParamDto paramDto,PlaceOrderDto orderDto,Set<String> spuCategoryIds,List<PlaceOrderItemDto> goodsList){
		FavourParamBO paramBO = new FavourParamBO();
		// 获取店铺类型
		StoreTypeEnum storeType = ((StoreInfo)paramDto.get("storeInfo")).getType();
		
		paramBO.setUserId(paramDto.getUserId());
		paramBO.setStoreId(paramDto.getStoreId());
		paramBO.setStoreType(storeType);
		paramBO.setCouponsType(EnumAdapter.convert(storeType));
		paramBO.setTotalAmount(paramDto.getTotalAmount());
		if(storeType == StoreTypeEnum.SERVICE_STORE){
			// 服务店代金券，需要根据用户收货地址来确认是否可用。
			UserAddressVo addr = orderDto.getUserAddrInfo();
			if(addr != null){
				// 如果能获取用户默认地址，根据用户默认地址查询用户有效的代金券。
				paramBO.setAddressId(addr.getAddressId());
			}
		}
		paramBO.setClientType(EnumAdapter.convert(paramDto.getChannel()));
		paramBO.setSpuCategoryIds(spuCategoryIds);
		paramBO.setChannel(paramDto.getChannel());
		paramBO.setGoodsList(goodsList);
		return paramBO;
	}
	
	/**
	 * @Description: V2.0之前的版本订单结算时，根据订单请求构建查询优惠信息的参数
	 * @param reqDto
	 * @return   ServiceOrderReq reqDto, ServiceOrderResp respData
	 * @author maojj
	 * @date 2017年2月17日
	 */
	public FavourParamBO build(TradeOrderReqDto reqDto){
		FavourParamBO paramBO = new FavourParamBO();
		TradeOrderReq req = reqDto.getData();
		// 获取店铺类型
		StoreTypeEnum storeType = reqDto.getContext().getStoreInfo().getType();
		
		paramBO.setUserId(req.getUserId());
		paramBO.setStoreId(req.getStoreId());
		paramBO.setStoreType(storeType);
		paramBO.setCouponsType(EnumAdapter.convert(storeType));
		paramBO.setTotalAmount(req.getTotalAmount());
		if(storeType == StoreTypeEnum.SERVICE_STORE){
			paramBO.setAddressId(req.getAddressId());
		}
		paramBO.setClientType(EnumAdapter.convert(reqDto.getData().getOrderResource()));
		paramBO.setSpuCategoryIds(reqDto.getContext().getSpuCategoryIds());
		
		// Begin V2.3 added by maojj 2017-04-21
		paramBO.setChannel(reqDto.getData().getOrderResource());
		List<PlaceOrderItemDto> goodsList = Lists.newArrayList();
		PlaceOrderItemDto goods = null;
		for(TradeOrderGoodsItem item : req.getList()){
			goods = new PlaceOrderItemDto();
			goods.setStoreSkuId(item.getSkuId());
			goods.setSpuCategoryId(item.getSpuCategoryId());
			goods.setSkuPrice(item.getSkuPrice());
			goods.setQuantity(item.getSkuNum());
			goodsList.add(goods);
		}
		paramBO.setGoodsList(goodsList);
		// End V2.3 added by maojj 2017-04-21
		return paramBO;
	}
	
	/**
	 * @Description: V2.0之前的版本服务订单结算时，根据订单请求构建查询优惠信息的参数
	 * @param req
	 * @param respData
	 * @return
	 * @throws Exception   
	 * @author maojj
	 * @date 2017年2月17日
	 */
	public FavourParamBO build(Request<ServiceOrderReq> req,ServiceOrderResp respData) throws Exception{
		FavourParamBO paramBO = new FavourParamBO();
		ServiceOrderReq reqDto = req.getData();
		// 获取店铺类型
		StoreTypeEnum storeType = StoreTypeEnum.valuesOf(respData.getStoreInfo().getStoreType());
		
		paramBO.setUserId(reqDto.getUserId());
		paramBO.setStoreId(reqDto.getStoreId());
		paramBO.setStoreType((storeType));
		paramBO.setCouponsType(EnumAdapter.convert(storeType));
		paramBO.setTotalAmount(reqDto.getTotalAmount());
		if(storeType == StoreTypeEnum.SERVICE_STORE){
			//到店消费根据店铺地址查询代金券
			if (OrderTypeEnum.STORE_CONSUME_ORDER == reqDto.getOrderType()) {
				MemberConsigneeAddress mAddress = memberConsigneeAddressService.findByStoreId(reqDto.getStoreId());
			    if (mAddress != null) {
			    	paramBO.setAddressId(mAddress.getId());
				}
			}else {
				if(respData.getDefaultAddress() != null){
					paramBO.setAddressId(respData.getDefaultAddress().getAddressId());
				}else{
					paramBO.setAddressId("");
				}
			}
		}
		paramBO.setClientType(EnumAdapter.convert(req.getOrderResource()));
		paramBO.setSpuCategoryIds((Set<String>)req.getContext().get("spuCategoryIds"));
		List<PlaceOrderItemDto> goodsList = Lists.newArrayList();
		paramBO.setChannel(req.getOrderResource());
		return paramBO;
	}
	
	public FavourParamBO build(StoreInfo storeInfo, ServiceOrderReq orderReq, BigDecimal totalAmount) throws Exception{
		FavourParamBO paramBO = new FavourParamBO();
		// 获取店铺类型
		StoreTypeEnum storeType = storeInfo.getType();
		
		paramBO.setUserId(orderReq.getUserId());
		paramBO.setStoreId(orderReq.getStoreId());
		paramBO.setStoreType((storeType));
		paramBO.setCouponsType(EnumAdapter.convert(storeType));
		paramBO.setTotalAmount(totalAmount);
		if(storeType == StoreTypeEnum.SERVICE_STORE){
			//到店消费根据店铺地址查询代金券
			if (OrderTypeEnum.STORE_CONSUME_ORDER == orderReq.getOrderType()) {
				MemberConsigneeAddress mAddress = memberConsigneeAddressService.findByStoreId(orderReq.getStoreId());
			    if (mAddress != null) {
			    	paramBO.setAddressId(mAddress.getId());
				}
			}else {
				paramBO.setAddressId(orderReq.getAddressId());
			}
		}
		List<String> skuIdList = new ArrayList<String>();
		skuIdList.add(orderReq.getSkuId());
		paramBO.setSkuIdList(skuIdList);
		return paramBO;
	}
}
