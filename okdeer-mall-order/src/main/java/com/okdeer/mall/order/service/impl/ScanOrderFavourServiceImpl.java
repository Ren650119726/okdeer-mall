/** 
 *@Project: okdeer-mall-order 
 *@Author: guocp
 *@Date: 2017年10月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.base.common.model.RequestParams;
import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.coupons.enums.CouponsType;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.discount.entity.PreferentialVo;
import com.okdeer.mall.common.enums.UseClientType;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.dto.ScanOrderDto;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.service.GetPreferentialService;
import com.okdeer.mall.order.service.ScanOrderFavourService;
import com.okdeer.mall.order.service.TradePinMoneyObtainService;
import com.okdeer.mall.order.vo.Coupons;

/**
 * ClassName: ScanOrderFavourApiImpl 
 * @Description: 扫码购优惠
 * @author guocp
 * @date 2017年10月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class ScanOrderFavourServiceImpl implements ScanOrderFavourService{
	
	@Autowired
	private TradePinMoneyObtainService tradePinMoneyObtainService;

	@Resource
	GetPreferentialService getPreferentialService;
	
    @Resource
    private ActivityCouponsRecordMapper activityCouponsRecordMapper;
	
    /**
 	 * 优惠信息校验
 	 */
 	@Resource
 	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto>  checkFavourService;
 	
	/**
	 * 订单补充优惠信息
	 * @throws Exception 
	 * @see com.okdeer.mall.order.service.ScanOrderFavourApi#appendFavour(com.okdeer.mall.order.dto.ScanOrderDto)
	 */
	@Override
	public void appendFavour(ScanOrderDto orderDetail, RequestParams requestParams) throws Exception {

		if(OrderResourceEnum.WECHAT_MIN == orderDetail.getOrderResource()){
			return;
		}
		//设置为使用才进行查询优惠信息  并设置优惠信息
		//可以使用的优惠金额  即是 排除掉  不可使用优惠的   商品金额
		FavourParamBO parambo = createFavourParamBo(orderDetail,requestParams);
		PreferentialVo preferentialVo = getPreferentialService.findPreferByCard(parambo);
		Coupons coupons = (Coupons) preferentialVo.getMaxFavourOnline();
		if(coupons != null){
			orderDetail.setMaxFavourOnline(coupons);
		}
		orderDetail.setCouponList(preferentialVo.getCouponList());
		//可以使用零花钱
		BigDecimal myUsable = tradePinMoneyObtainService.findMyUsableTotal(orderDetail.getUserId(),new Date());
		orderDetail.setPinMoneyAmount(myUsable);
	}
	
	/**
	 * @Description: 组装查询优惠券条件
	 * @param orderDetail
	 * @return FavourParamBO  
	 * @author tuzhd
	 * @date 2017年8月8日
	 */
	private FavourParamBO createFavourParamBo(ScanOrderDto orderDetail, RequestParams requestParams){
		FavourParamBO parambo =new FavourParamBO();
		parambo.setUserId(orderDetail.getUserId());
		parambo.setChannel(OrderResourceEnum.SWEEP);
		parambo.setClientType(UseClientType.ONlY_APP_USE);
		//便利店通用代金券
		parambo.setOrderType(OrderTypeEnum.PHYSICAL_ORDER);
		parambo.setStoreType(StoreTypeEnum.CLOUD_STORE);
		parambo.setStoreId(orderDetail.getBranchId());
		List<String> goods = Lists.newArrayList();
		orderDetail.getList().forEach(e -> goods.add(e.getSkuId()));
		parambo.setSkuIdList(goods);
		parambo.setDeviceId(requestParams.getMachineCode());
		//设置可优惠金额
		parambo.setTotalAmount(orderDetail.getAllowDiscountsAmount());
		return parambo;
	}

}
