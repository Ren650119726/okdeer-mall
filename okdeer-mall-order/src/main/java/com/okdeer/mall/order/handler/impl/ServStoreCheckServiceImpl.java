package com.okdeer.mall.order.handler.impl;

import static com.okdeer.archive.store.enums.ResultCodeEnum.CVS_IS_PAUSE;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.entity.StoreInfoExt;
import com.okdeer.archive.store.entity.StoreInfoServiceExt;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.archive.store.enums.StoreStatusEnum;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.dto.TimeInterval;
import com.okdeer.mall.order.enums.OrderOptTypeEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PlaceOrderTypeEnum;
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
	
	private static final Pattern pattern = Pattern.compile("(\\d{2}:\\d{2}-\\d{2}:\\d{2},*)*");

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
		// Begin V2.0 added by maojj 2017-01-19
		// 店铺已暂停
		if (storeInfo.getStoreInfoExt().getIsBusiness() == WhetherEnum.not) {
			resp.setResult(CVS_IS_PAUSE);
			req.setComplete(true);
			return;
		}
		// End V2.0 added by maojj 2017-01-19
		
		req.getContext().put("storeName", storeInfo.getStoreName());
		req.getContext().put("storeAreaType", storeInfo.getAreaType());
		// begin add by wushp 20160927 
		// 上门服务订单，如果商家中心设置起送价，不满起送价不可下单，提示
		// 提示‘抱歉，订单不满起送价，请重新结算’且页面跳回至购物车页面，购物车页面刷新，获取最新的起送价、配送费信息 
		if ( reqData.getOrderType() == null || reqData.getOrderType() == OrderTypeEnum.SERVICE_STORE_ORDER ) {
			// Begin V2.4 added by maojj 2017-05-31
			if(!isValid(reqData.getServiceTime(),storeInfo.getStoreInfoExt().getInvalidDate())){
				resp.setResult(ResultCodeEnum.SERV_TIME_INVALID);
				req.setComplete(true);
				return;
			}
			// End V2.4 added by maojj 2017-05-31
			// 上门服务订单
			StoreInfoServiceExt serviceExt = storeInfo.getStoreInfoServiceExt();
			serviceExt.setInvalidDate(storeInfo.getStoreInfoExt().getInvalidDate());
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
		// 解析时间店铺下单模式
		parseOrderTimeModel(servStoreInfo,storeInfo.getStoreInfoServiceExt());
		return servStoreInfo;
	}
	
	/**
	 * 提取店铺发票信息
	 */
	private Integer extractIsInvoice(StoreInfoExt storeExt){
		return storeExt.getIsInvoice() == null ? Integer.valueOf(Constant.ZERO) : storeExt.getIsInvoice().ordinal();
	}
	
	/**
	 * 是否提前预约
	 */
	private boolean isAdvance(StoreInfoExt storeExt){
		return Integer.valueOf(Constant.ONE).equals(storeExt.getIsAdvanceType());
	}
	
	private void parseOrderTimeModel(ServStoreInfo servStoreInfo,StoreInfoServiceExt storeServExt){
		Integer orderTimeModel = storeServExt.getOrderTimeModel();
		if (orderTimeModel == null){
			return;
		}
		// 下单时间模式，0：按时间点，1：按时间段
		servStoreInfo.setTimeType(orderTimeModel);
		String timePoint = storeServExt.getTimePoint();
		if (orderTimeModel.intValue() == Constant.ZERO){
			// 按时间周期
			servStoreInfo.setTimePeriod(timePoint);
		}else{
			// 按时间段
			List<TimeInterval> timeList = new ArrayList<TimeInterval>();
			TimeInterval time = null;
			if(StringUtils.isEmpty(timePoint) || !pattern.matcher(timePoint).matches()){
				return;
			}
			String[] timeIntervals = timePoint.split(",");
			for(String timeInterVal : timeIntervals){
				String[] times = timeInterVal.split("-");
				time = new TimeInterval();
				time.setStartTime(times[0]);
				time.setEndTime(times[1]);
				
				timeList.add(time);
			}
			servStoreInfo.setTimeList(timeList);
		}
	}
	
	// Begin V2.4 added by maojj 2017-05-31
	/**
	 * @Description: 判断当前日期是否被设置为不可用日期
	 * @param servTime
	 * @param invalidDate
	 * @return   
	 * @author maojj
	 * @date 2017年5月22日
	 */
	private boolean isValid(String servTime,String invalidDate){
		if(StringUtils.isEmpty(servTime) || StringUtils.isEmpty(invalidDate)){
			return true;
		}
		// 服务时间年月
		String servMonth = servTime.substring(0,4) + servTime.substring(5,7);
		// 服务时间天数
		int servDay = Integer.parseInt(servTime.substring(8,10));
		String[] invalidDateArr =  invalidDate.split(",");
		String invalidMonth = null;
		for (String invalidTime : invalidDateArr) {
			invalidMonth = invalidTime.substring(0, 6);
			if (servMonth.compareTo(invalidMonth) == -1) {
				// 如果当前月份小于不可用日期限制的月份，则当前日期一定可用
				break;
			} else if (servMonth.compareTo(invalidMonth) == 0) {
				// 如果当前月份等于不可用日期限制的月份，则判定这天是否可用
				int invalidDay = Integer.parseInt(invalidTime.substring(6));
				if (((invalidDay >> (servDay - 1)) & 1) == 1) {
					// 如果当前天数为不可用日期
					return false;
				} 
			} else if (servMonth.compareTo(invalidMonth) == 1) {
				// 如果当前月份大于不可用日期限制的月份，则循环跳入下一个月份限制进行判定
				continue;
			}
		}
		return true;
	}
	// End V2.4 added by maojj 2017-05-31
}
