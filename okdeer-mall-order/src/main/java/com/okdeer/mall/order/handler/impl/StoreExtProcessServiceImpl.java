package com.okdeer.mall.order.handler.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.entity.StoreInfoExt;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.mall.order.utils.CodeStatistical;
import com.okdeer.mall.order.vo.TradeOrderContext;
import com.okdeer.mall.order.vo.TradeOrderReqDto;
import com.okdeer.mall.order.vo.TradeOrderResp;
import com.okdeer.mall.order.vo.TradeOrderRespDto;
import com.okdeer.mall.order.constant.text.OrderTipMsgConstant;
import com.okdeer.mall.order.handler.StoreExtProcessService;

/**
 * ClassName: StoreExtProcessServiceImpl 
 * @Description: 店铺运费和起订金额处理
 * @author maojj
 * @date 2016年7月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构V4.1			2016-07-14			maojj			店铺运费和起订金额处理
 *		Bug:12808		2016-08-16		 	maojj			结算失败时返回店铺的相关信息
 */
@Service
public class StoreExtProcessServiceImpl implements StoreExtProcessService {

	private static final Logger logger = LoggerFactory.getLogger(StoreExtProcessServiceImpl.class);

	/**
	 * 店铺信息查询Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoServiceApi;

	@Override
	public void process(TradeOrderReqDto reqDto, TradeOrderRespDto respDto) throws Exception {
		TradeOrderResp resp = respDto.getResp();
		StoreInfo store = reqDto.getContext().getStoreInfo();
		StoreInfoExt storeExt = store.getStoreInfoExt();
		// 店铺名称
		resp.setConsigneeName(store.getStoreName());
		// 店铺营业开始时间
		resp.setStartTime(storeExt.getServiceStartTime());
		// 店铺营业结束时间
		resp.setEndTime(storeExt.getServiceEndTime());
		// 店铺默认地址查询
		resp.setAddress(getStoreAddress(reqDto.getData().getStoreId()));
		// 设置店铺起送金额和运费
		setStartMoneyAndFare(reqDto, resp);
		resp.setCurrenTime(new Date().getTime());
		resp.setIsInvoice(storeExt.getIsInvoice().ordinal());
		
		// Begin modified by maojj 2016-08-16 Bug:12808
		if (respDto.isFlag()) {
			// isOrder:是否可以接单，0：否，1：是
			resp.setIsOrder(1);
			respDto.setMessage(OrderTipMsgConstant.SHOPPING_SUCCESS);
		}
		// End modified by maojj 2016-08-16
	}

	/**
	 * @Description: 店铺默认地址查询
	 * @param storeId 店铺ID
	 * @return String  
	 * @throws Exception 异常
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private String getStoreAddress(String storeId) throws Exception {
		StoreInfo storeInfo = storeInfoServiceApi.selectDefaultAddressById(storeId);
		if (storeInfo == null) {
			logger.error("根据店铺ID{}查询店铺默认地址", "store为空-------->{}", storeId, CodeStatistical.getLineInfo());
			throw new Exception("查询店铺默认地址异常：store为空-------->" + CodeStatistical.getLineInfo());
		}

		String area = storeInfo.getArea() == null ? "" : storeInfo.getArea();
		String address = storeInfo.getAddress() == null ? "" : storeInfo.getAddress();
		// 店铺详细地址
		return area + address;
	}

	/**
	 * @Description: 设置起送金额和运费
	 * @param reqDto 响应Dto
	 * @param resp 响应对象
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void setStartMoneyAndFare(TradeOrderReqDto reqDto, TradeOrderResp resp) {
		TradeOrderContext context = reqDto.getContext();
		StoreInfoExt storeExt = context.getStoreInfo().getStoreInfoExt();
		// 店铺起送价
		BigDecimal startPrice = storeExt.getStartPrice() == null ? new BigDecimal(0.0) : storeExt.getStartPrice();
		// 店铺运费
		BigDecimal fare = storeExt.getFreight() == null ? new BigDecimal(0.0) : storeExt.getFreight();
		// 获取订单总金额
		BigDecimal totalAmount = context.getTotalAmount();
		// Begin added by maojj 2016-08-16 上下文中总金额为空时，重新计算订单请求的总金额
		if (totalAmount == null) {
			totalAmount = reqDto.getData().getTotalAmount();
		}
		// End added by maojj 2016-08-16

		resp.setStartMoney(startPrice);
		if (totalAmount.compareTo(startPrice) == -1) {
			resp.setFare(fare);
		} else {
			resp.setFare(new BigDecimal(0.0));
		}
	}
}