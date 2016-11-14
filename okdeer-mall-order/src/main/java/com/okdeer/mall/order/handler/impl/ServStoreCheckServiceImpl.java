package com.okdeer.mall.order.handler.impl;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.entity.StoreInfoExt;
import com.okdeer.archive.store.entity.StoreInfoServiceExt;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.archive.store.enums.StoreStatusEnum;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.mall.common.vo.Request;
import com.okdeer.mall.common.vo.Response;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.vo.ServStoreInfo;
import com.okdeer.mall.order.vo.ServiceOrderReq;
import com.okdeer.mall.order.vo.ServiceOrderResp;

/**
 * 
 * ClassName: ServStoreCheckServiceImpl 
 * @Description: 秒杀时服务店铺校验服务
 * @author maojj
 * @date 2016年9月22日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构1.1			2016年9月22日				maojj			秒杀时服务店铺校验服务
 */
@Service("servStoreCheckService")
public class ServStoreCheckServiceImpl implements RequestHandler<ServiceOrderReq,ServiceOrderResp> {
	
	/**
	 * 店铺调用接口
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoService;

	@Override
	public void process(Request<ServiceOrderReq> req, Response<ServiceOrderResp> resp) throws Exception {
		ServiceOrderReq reqData = req.getData();
		ServiceOrderResp data = resp.getData();
		// 服务店铺不存在
		StoreInfo storeInfo = storeInfoService.getStoreInfoById(reqData.getStoreId());
		if (storeInfo == null || storeInfo.getStoreInfoExt() == null) {
			resp.setResult(ResultCodeEnum.SERVER_STORE_NOT_EXISTS);
			req.setComplete(true);
			return;
		}
		// 店铺已关闭
		if (StoreStatusEnum.OPENING != storeInfo.getStoreInfoExt().getIsClosed()) {
			resp.setResult(ResultCodeEnum.SERVER_STORE_IS_CLOSED);
			req.setComplete(true);
			return;
		}
		
		req.getContext().put("storeName", storeInfo.getStoreName());
		req.getContext().put("storeAreaType", storeInfo.getAreaType());
		// begin add by wushp 20160927 
		// 上门服务订单，如果商家中心设置起送价，不满起送价不可下单，提示
		// 提示‘抱歉，订单不满起送价，请重新结算’且页面跳回至购物车页面，购物车页面刷新，获取最新的起送价、配送费信息 
		OrderTypeEnum orderType = reqData.getOrderType();
		if (orderType != null && orderType.ordinal() == OrderTypeEnum.SERVICE_STORE_ORDER.ordinal()) {
			// 上门服务订单
			StoreInfoServiceExt serviceExt = storeInfo.getStoreInfoServiceExt();
			// 返回服务店铺扩展信息
			data.setStoreInfoServiceExt(serviceExt);
			
		}
		// end add by wushp 20160927 
		// 返回服务店铺信息 到店消费需要拿店铺信息发短信用
		// if (req.getOrderOptType() == OrderOptTypeEnum.ORDER_SETTLEMENT) {
		data.setStoreInfo(buildServStoreInfo(storeInfo));
		// }
	}

	/**
	 * @Description: 构建服务店铺返回信息对象
	 * @param storeInfo
	 * @return   
	 * @author maojj
	 * @date 2016年9月23日
	 */
	private ServStoreInfo buildServStoreInfo(StoreInfo storeInfo){
		ServStoreInfo servStoreInfo = new ServStoreInfo();
		// 获取店铺扩展信息
		StoreInfoExt storeExt = storeInfo.getStoreInfoExt();
		// 设置店铺ID
		servStoreInfo.setStoreId(storeInfo.getId());
		// 店铺类型
		servStoreInfo.setStoreType(storeInfo.getType().ordinal());
		// 设置店铺服务时间
		servStoreInfo.setStartTime(storeExt.getServiceStartTime());
		servStoreInfo.setEndTime(storeExt.getServiceEndTime());
		// 设置是否有发票
		servStoreInfo.setIsInvoice(extractIsInvoice(storeExt));
		// 设置预约期限天数
		servStoreInfo.setDeadline(storeExt.getSubscribeTime());
		// 设置店铺区域类型
		servStoreInfo.setStoreAreaType(storeInfo.getAreaType());
		// 根据店铺设置的信息设置提前天数预约和提前预约小时数
		// 店铺是否设置提前预约
		if(isAdvance(storeExt)){
			// 预约类型（0：提前多少小时下单 1：只能下当前日期多少天后的订单）
			if(Integer.valueOf(0).equals(storeExt.getAdvanceType())){
				// 如果预约类型是提前多少小时下单
				servStoreInfo.setAheadTimeHours(String.valueOf(storeExt.getAdvanceTime()));
			}else if(Integer.valueOf(1).equals(storeExt.getAdvanceType())){
				// 如果预约类型是只能下当前日期多少天后的订单
				servStoreInfo.setAheadTimeDay(String.valueOf(storeExt.getAdvanceTime()));
			}
		}
		// 设置店铺客服电话
		servStoreInfo.setServicePhone(storeInfo.getStoreInfoExt().getServicePhone());
		return servStoreInfo;
	}
	
	/**
	 * 提取店铺发票信息
	 */
	private Integer extractIsInvoice(StoreInfoExt storeExt){
		return storeExt.getIsInvoice() == null ? Integer.valueOf(0) : storeExt.getIsInvoice().ordinal();
	}
	
	/**
	 * 是否提前预约
	 */
	private boolean isAdvance(StoreInfoExt storeExt){
		return Integer.valueOf(1).equals(storeExt.getIsAdvanceType());
	}
}
