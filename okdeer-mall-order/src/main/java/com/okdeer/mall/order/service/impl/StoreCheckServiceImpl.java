package com.okdeer.mall.order.service.impl;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.entity.StoreInfoExt;
import com.okdeer.archive.store.enums.StoreStatusEnum;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.mall.common.utils.DateUtils;
import com.okdeer.mall.order.enums.OrderOptTypeEnum;
import com.okdeer.mall.order.utils.CodeStatistical;
import com.okdeer.mall.order.vo.TradeOrderReq;
import com.okdeer.mall.order.vo.TradeOrderReqDto;
import com.okdeer.mall.order.vo.TradeOrderResp;
import com.okdeer.mall.order.vo.TradeOrderRespDto;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.order.constant.OrderTipMsgConstant;
import com.okdeer.mall.order.service.StoreCheckService;

/**
 * ClassName: StoreCheckServiceImpl 
 * @Description: 检查库存
 * @author maojj
 * @date 2016年7月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构V4.1			2016-07-14			maojj			检查库存
 *		Bug:12572	    2016-08-10		 	maojj			添加结算校验失败的提示语
 *		Bug:12816   	2016-08-16		 	maojj			店铺非营业时，修改返回状态
 */
@Service
public class StoreCheckServiceImpl implements StoreCheckService {

	private static final Logger logger = LoggerFactory.getLogger(StoreCheckServiceImpl.class);

	/**
	 * 店铺Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoServiceApi;

	/**
	 * 检查商品信息是否发生变化并获取商品最新信息返回给app
	 */
	@Override
	public void process(TradeOrderReqDto reqDto, TradeOrderRespDto respDto) throws Exception {
		TradeOrderResp resp = respDto.getResp();
		TradeOrderReq req = reqDto.getData();
		// 获取订单请求中的店铺ID
		String storeId = req.getStoreId();
		// 查询店铺信息
		StoreInfo storeInfo = storeInfoServiceApi.selectStoreBaseInfoById(storeId);
		if (storeInfo == null) {
			logger.error("根据店铺Id{}查询店铺信息为空-------->{}", storeId, CodeStatistical.getLineInfo());
			throw new ServiceException("查询店铺信息异常：storeInfo 为空-------->" + CodeStatistical.getLineInfo());
		}
		StoreInfoExt storeExt = storeInfo.getStoreInfoExt();

		// 店铺信息存入上下文
		reqDto.getContext().setStoreInfo(storeInfo);
		// 店铺关闭，则返回错误提示信息
		if (storeExt.getIsClosed() == StoreStatusEnum.CLOSED) {
			respDto.setFlag(false);
			// Begin modified by maojj 2016-08-10 Bug:12572
			if (reqDto.getOrderOptType() == OrderOptTypeEnum.ORDER_SETTLEMENT) {
				respDto.setMessage(OrderTipMsgConstant.STORE_IS_CLOSED_SETTLEMENT);
			} else {
				respDto.setMessage(OrderTipMsgConstant.STORE_IS_CLOSED);
			}
			// End modified by maojj 2016-08-10
			resp.setIsClosed(StoreStatusEnum.CLOSED.ordinal());
			return;
		}

		// 店铺暂停营业
		if (storeExt.getIsBusiness() == WhetherEnum.not) {
			respDto.setFlag(false);
			// Begin modified by maojj 2016-08-10 Bug:12572
			if (reqDto.getOrderOptType() == OrderOptTypeEnum.ORDER_SETTLEMENT) {
				respDto.setMessage(OrderTipMsgConstant.STORE_IS_PAUSE_SETTLEMENT);
			} else {
				respDto.setMessage(OrderTipMsgConstant.STORE_IS_PAUSE);
			}
			// End modified by maojj 2016-08-10
			resp.setIsBusiness(WhetherEnum.not.ordinal());
			return;
		}

		// Begin modified by maojj 2016-08-10 Bug:12572
		switch (reqDto.getOrderOptType()) {
			case ORDER_SETTLEMENT:
				checkTimeWhenSettlement(respDto, storeExt);
				break;
			case ORDER_SUBMIT:
				checkTimeWhenSubmit(respDto, storeExt, req.isCheckTime());
				break;
			default:
				break;
		}
		// End modified by maojj 2016-08-10
	}

	// Begin added by maojj 2016-08-10 Bug:12572
	/**
	 * @Description: 结算时校验店铺营业时间。如果店铺非营业时间接单，则校验通过，否则检查店铺是否在营业时间范围内
	 * @param respDto 响应对象
	 * @param storeExt 店铺扩展信息
	 * @author maojj
	 * @date 2016年8月11日
	 */
	private void checkTimeWhenSettlement(TradeOrderRespDto respDto, StoreInfoExt storeExt) {
		// 店铺非营业时间接单，结算时不用校验时间，直接校验通过
		if (storeExt.getIsAcceptOrder() == WhetherEnum.whether) {
			return;
		}
		TradeOrderResp resp = respDto.getResp();
		resp.setIsAcceptOrder(storeExt.getIsAcceptOrder().ordinal());
		// 判定当前时间是否在营业时间范围内
		if (!isBusiness(storeExt.getServiceStartTime(), storeExt.getServiceEndTime())) {
			// 1:营业中,0:休息中
			resp.setIsRest(0);
			respDto.setFlag(false);
			respDto.setMessage(OrderTipMsgConstant.STORE_IS_SHUT_SETTLEMENT);
		}
	}

	/**
	 * @Description: 提交订单时检查店铺营业时间。店铺非营业时间接单和非营业时间不接单的校验
	 * @param respDto 响应对象
	 * @param storeExt 店铺扩展信息
	 * @param isCheckTime 是否需要校验时间
	 * @author maojj
	 * @date 2016年8月11日
	 */
	private void checkTimeWhenSubmit(TradeOrderRespDto respDto, StoreInfoExt storeExt, boolean isCheckTime) {
		// 是否校验营业时间
		if (!isCheckTime) {
			return;
		}
		TradeOrderResp resp = respDto.getResp();
		// 判定当前时间是否在营业时间范围内
		if (!isBusiness(storeExt.getServiceStartTime(), storeExt.getServiceEndTime())) {
			resp.setIsAcceptOrder(storeExt.getIsAcceptOrder().ordinal());
			resp.setIsRest(0);
			respDto.setFlag(false);
			// 不在营业时间范围内
			if (storeExt.getIsAcceptOrder() == WhetherEnum.whether) {
				// 店铺非营业时间接单
				respDto.setMessage(OrderTipMsgConstant.STORE_DELIVERY_TOMORROW);
				return;
			} else {
				respDto.setMessage(OrderTipMsgConstant.STORE_IS_SHUT);
				return;
			}
		}
	}
	// End modified by maojj 2016-08-10

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
