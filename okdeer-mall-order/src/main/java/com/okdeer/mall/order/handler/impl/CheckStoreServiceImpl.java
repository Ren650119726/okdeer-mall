package com.okdeer.mall.order.handler.impl;

import static com.okdeer.archive.store.enums.ResultCodeEnum.CVS_DELIVERY_TOMORROW;
import static com.okdeer.archive.store.enums.ResultCodeEnum.CVS_IS_PAUSE;
import static com.okdeer.archive.store.enums.ResultCodeEnum.STORE_IS_CLOSED;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.entity.StoreInfoExt;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.archive.store.enums.StoreStatusEnum;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.common.utils.DateUtils;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.enums.PlaceOrderTypeEnum;
import com.okdeer.mall.order.handler.RequestHandler;
/**
 * ClassName: CheckStoreServiceImpl 
 * @Description: 校验店铺服务
 * @author maojj
 * @date 2016年12月31日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿1.2.5		2016年12月31日				maojj
 */
@Service("checkStoreService")
public class CheckStoreServiceImpl implements RequestHandler<PlaceOrderParamDto, PlaceOrderDto>{
	
	/**
	 * 店铺Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoServiceApi;
	

	@Override
	public void process(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		PlaceOrderParamDto paramDto = req.getData();
		// 查询当前店铺信息
		StoreInfo storeInfo = storeInfoServiceApi.getStoreInfoById(paramDto.getStoreId());
		// 检查店铺信息是否存在
		checkNonNull(resp,storeInfo);
		// 检查店铺是否关闭
		checkIsClose(resp,paramDto,storeInfo);
		// 检查店铺是否营业
		checkIsBusiness(resp,paramDto,storeInfo);
		// 检查店铺营业时间
		checkBusinessTime(resp,paramDto,storeInfo);
		// 缓存店铺信息
		paramDto.put("storeInfo", storeInfo);
	}

	/**
	 * @Description: 店铺信息非空校验
	 * @param resp
	 * @param storeInfo   
	 * @author maojj
	 * @date 2016年12月31日
	 */
	private void checkNonNull(Response<PlaceOrderDto> resp, StoreInfo storeInfo){
		if(!resp.isSuccess()){
			return;
		}
		
		if(storeInfo == null || storeInfo.getStoreInfoExt() == null){
			resp.setResult(ResultCodeEnum.SERVER_STORE_NOT_EXISTS);
		}
	}
	
	/**
	 * @Description: 校验店铺是否关闭
	 * @param resp
	 * @param storeInfo   
	 * @author maojj
	 * @date 2016年12月31日
	 */
	private void checkIsClose(Response<PlaceOrderDto> resp, PlaceOrderParamDto paramDto, StoreInfo storeInfo){
		if(!resp.isSuccess()){
			return;
		}
		
		if (storeInfo.getStoreInfoExt().getIsClosed() ==  StoreStatusEnum.CLOSED) {
			resp.setResult(STORE_IS_CLOSED);
		}
	}
	
	/**
	 * @Description: 校验店铺是否营业。只针对便利店
	 * @param resp
	 * @param storeInfo
	 * @param orderType   
	 * @author maojj
	 * @date 2016年12月31日
	 */
	private void checkIsBusiness(Response<PlaceOrderDto> resp,PlaceOrderParamDto paramDto, StoreInfo storeInfo){
		if(!resp.isSuccess()){
			return;
		}
		
		if (storeInfo.getStoreInfoExt().getIsBusiness() == WhetherEnum.not) {
			resp.setResult(CVS_IS_PAUSE);
		}
	}
	
	/**
	 * @Description: 校验店铺营业时间。只针对便利店
	 * @param resp
	 * @param req
	 * @param storeInfo   
	 * @author maojj
	 * @date 2016年12月31日
	 */
	private void checkBusinessTime(Response<PlaceOrderDto> resp, PlaceOrderParamDto paramDto, StoreInfo storeInfo){
		if(!resp.isSuccess() || paramDto.getOrderType() != PlaceOrderTypeEnum.CVS_ORDER){
			return;
		}
		switch (paramDto.getOrderOptType()) {
			case ORDER_SETTLEMENT:
				checkTimeWhenSettlement(resp, storeInfo.getStoreInfoExt());
				break;
			case ORDER_SUBMIT:
				checkTimeWhenSubmit(resp, storeInfo.getStoreInfoExt(), paramDto.isCheckTime());
				break;
			default:
				break;
		}
	}
	
	/**
	 * @Description: 结算时校验店铺营业时间。如果店铺非营业时间接单，则校验通过，否则检查店铺是否在营业时间范围内
	 * @param resp 响应对象
	 * @param storeExt 店铺扩展信息  
	 * @author maojj
	 * @date 2016年12月31日
	 */
	private void checkTimeWhenSettlement(Response<PlaceOrderDto> resp, StoreInfoExt storeExt) {
		// 店铺非营业时间接单，结算时不用校验时间，直接校验通过
		if (storeExt.getIsAcceptOrder() == WhetherEnum.whether) {
			return;
		}
		// 判定当前时间是否在营业时间范围内
		if (!isBusiness(storeExt.getServiceStartTime(), storeExt.getServiceEndTime())) {
			resp.setResult(CVS_IS_PAUSE);
		}
	}

	/**
	 * @Description: 提交订单时检查店铺营业时间。店铺非营业时间接单和非营业时间不接单的校验
	 * @param resp 响应对象
	 * @param storeExt  店铺扩展信息
	 * @param isCheckTime 是否需要校验时间  
	 * @author maojj
	 * @date 2016年12月31日
	 */
	private void checkTimeWhenSubmit(Response<PlaceOrderDto> resp, StoreInfoExt storeExt, boolean isCheckTime) {
		// 是否校验营业时间
		if (!isCheckTime) {
			return;
		}
		// 判定当前时间是否在营业时间范围内
		if (!isBusiness(storeExt.getServiceStartTime(), storeExt.getServiceEndTime())) {
			// 不在营业时间范围内
			if (storeExt.getIsAcceptOrder() == WhetherEnum.whether) {
				// 店铺非营业时间接单
				resp.setResult(CVS_DELIVERY_TOMORROW);
			} else {
				resp.setResult(CVS_IS_PAUSE);
			}
		}
	}

	/**
	 * @Description: 判断当前时间是否再营业时间范围内
	 * @param servStartTime 店铺营业开始时间
	 * @param servEndTime  店铺营业结束时间
	 * @return boolean  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private boolean isBusiness(String servStartTime, String servEndTime) {
		// 当前时间
		Date currentDate = getCurrentDate();
		// 服务开始时间
		Date startTime = DateUtils.parseDate(servStartTime);
		Date endTime = DateUtils.parseDate(servEndTime);
		if (startTime.before(endTime)) {
			// 不跨天营业
			if ((currentDate.after(startTime)) && (endTime.after(currentDate))) {
				return true;
			} else {
				return false;
			}
		} else {
			// 跨天营业
			if (currentDate.after(startTime) || endTime.after(currentDate)) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * @Description: 获取当前时间小时分钟数
	 * @return Date  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private Date getCurrentDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		String hourMinute = hour + ":" + minute;
		return DateUtils.parseDate(hourMinute);
	}

}
