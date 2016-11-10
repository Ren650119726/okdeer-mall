package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.mall.common.utils.DateUtils;
import com.okdeer.mall.common.vo.Response;
import com.okdeer.mall.order.constant.OrderTraceConstant;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderTrace;
import com.okdeer.mall.order.enums.OrderCancelType;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTraceEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.mapper.TradeOrderMapper;
import com.okdeer.mall.order.mapper.TradeOrderTraceMapper;
import com.okdeer.mall.order.service.TradeOrderTraceService;
import com.okdeer.mall.order.vo.RefundsTraceResp;
import com.okdeer.mall.order.vo.RefundsTraceVo;

/**
 * ClassName: TradeOrderTraceServiceImpl 
 * @Description: 订单轨迹服务实现类
 * @author maojj
 * @date 2016年11月4日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿1.2			2016年11月4日				maojj		      订单轨迹服务实现类
 */
@Service
public class TradeOrderTraceServiceImpl implements TradeOrderTraceService {

	private static final BigDecimal ZERO = BigDecimal.valueOf(0.0);

	@Resource
	private TradeOrderTraceMapper tradeOrderTraceMapper;

	@Resource
	private TradeOrderMapper tradeOrderMapper;

	@Override
	public void saveOrderTrace(TradeOrder tradeOrder) {
		// 只有上门服务的订单需要保存订单轨迹。
		if (tradeOrder.getType() != OrderTypeEnum.SERVICE_STORE_ORDER) {
			return;
		}
		// 如果订单状态为待付款且订单实付金额为0，不记录轨迹
		if (tradeOrder.getStatus() == OrderStatusEnum.UNPAID && ZERO.compareTo(tradeOrder.getActualAmount()) == 0) {
			return;
		}
		// 创建订单轨迹
		List<TradeOrderTrace> traceList = buildTraceList(tradeOrder);
		// 保存轨迹
		if (CollectionUtils.isEmpty(traceList)) {
			return;
		}
		for (TradeOrderTrace trace : traceList) {
			tradeOrderTraceMapper.add(trace);
		}
	}

	/**
	 * @Description: 构建订单轨迹列表
	 * @param tradeOrder
	 * @return   
	 * @author maojj
	 * @date 2016年11月7日
	 */
	private List<TradeOrderTrace> buildTraceList(TradeOrder tradeOrder) {
		List<TradeOrderTrace> traceList = new ArrayList<TradeOrderTrace>();
		// 构建操作轨迹
		TradeOrderTrace optTrace = buildOptTrace(tradeOrder);
		if (optTrace != null) {
			traceList.add(optTrace);
		}
		if (tradeOrder.getStatus() == OrderStatusEnum.UNPAID) {
			// 订单状态为待付款时，需要多记录一条轨迹
			TradeOrderTrace trace = new TradeOrderTrace(tradeOrder.getId());
			trace.setTraceStatus(OrderTraceEnum.WAIT_PAID);
			trace.setRemark(OrderTraceConstant.WAIT_PAID_REMARK);
			traceList.add(trace);
		}
		return traceList;
	}

	/**
	 * @Description: 构建操作轨迹
	 * @param tradeOrder 交易订单
	 * @return   
	 * @author maojj
	 * @date 2016年11月7日
	 */
	private TradeOrderTrace buildOptTrace(TradeOrder tradeOrder) {
		TradeOrderTrace trace = new TradeOrderTrace(tradeOrder.getId());
		// 是否需要记录轨迹
		boolean isNeedTrace = true;
		switch (tradeOrder.getStatus()) {
			case UNPAID:
				trace.setTraceStatus(OrderTraceEnum.SUBMIT_ORDER);
				trace.setRemark(String.format(OrderTraceConstant.SUBMIT_ORDER_REMARK, tradeOrder.getOrderNo()));
				break;
			case WAIT_RECEIVE_ORDER:
				trace.setTraceStatus(OrderTraceEnum.WAIT_RECEIVE);
				trace.setRemark(OrderTraceConstant.WAIT_RECEIVE_REMARK);
				break;
			case DROPSHIPPING:
				trace.setTraceStatus(OrderTraceEnum.WAIT_DISPATCHED);
				trace.setRemark(OrderTraceConstant.WAIT_DISPATCHED_REMARK);
				break;
			case TO_BE_SIGNED:
				trace.setTraceStatus(OrderTraceEnum.SET_OUT);
				trace.setRemark(OrderTraceConstant.SET_OUT_REMARK);
				break;
			case CANCELED:
				trace.setTraceStatus(OrderTraceEnum.CANCELED);
				trace.setRemark(getCancelRemark(tradeOrder));
				break;
			case HAS_BEEN_SIGNED:
				trace.setTraceStatus(OrderTraceEnum.COMPLETED);
				trace.setRemark(OrderTraceConstant.COMPLETED_REMARK);
				break;
			default:
				// 如果订单状态不匹配上述任意状态，则不需要记录轨迹
				isNeedTrace = false;
				break;
		}
		if (!isNeedTrace) {
			return null;
		}
		return trace;
	}

	/**
	 * @Description: 获取订单取消的备注
	 * @param tradeOrder 交易订单
	 * @return   
	 * @author maojj
	 * @date 2016年11月7日
	 */
	private String getCancelRemark(TradeOrder tradeOrder) {
		String remark = "";
		// 订单取消原因
		String reason = tradeOrder.getReason();
		// 判断订单取消类型：0：系统取消，1：用户取消，2：商家取消
		OrderCancelType cancelType = tradeOrder.getCancelType();
		// TODO 是否有违约金 0:否，1:是
		WhetherEnum isBreach = tradeOrder.getIsBreach();
		// 查询当前订单
		TradeOrder currentOrder = tradeOrderMapper.selectByPrimaryKey(tradeOrder.getId());
		switch (currentOrder.getStatus()) {
			case UNPAID:
				if (cancelType == OrderCancelType.CANCEL_BY_BUYER) {
					remark = String.format(OrderTraceConstant.CANCEL_ON_UNPAID, reason);
				} else {
					remark = OrderTraceConstant.CANCEL_ON_TIMEOUT;
				}
				break;
			case WAIT_RECEIVE_ORDER:
				if (cancelType == OrderCancelType.CANCEL_BY_BUYER) {
					remark = String.format(OrderTraceConstant.CANCEL_BY_USER, reason);
				} else if (cancelType == OrderCancelType.CANCEL_BY_SELLER) {
					remark = String.format(OrderTraceConstant.CANCEL_BY_SELLER, reason);
				} else if (cancelType == OrderCancelType.CANCEL_BY_SYSTEM) {
					remark = OrderTraceConstant.CANCEL_BY_SELLER_TIMEOUT;
				}
				break;
			case DROPSHIPPING:
				if (cancelType == OrderCancelType.CANCEL_BY_BUYER) {
					if (isBreach == WhetherEnum.whether) {
						// TODO 第二个参数时违约金的百分比
						remark = String.format(OrderTraceConstant.CANCEL_BY_USER_BREAK_CONTRACT, reason, tradeOrder.getBreachPercent());
					} else {
						remark = String.format(OrderTraceConstant.CANCEL_BY_USER, reason);
					}
				} else if (cancelType == OrderCancelType.CANCEL_BY_SELLER) {
					remark = String.format(OrderTraceConstant.CANCEL_BY_SELLER, reason);
				} else if (cancelType == OrderCancelType.CANCEL_BY_SYSTEM) {
					remark = OrderTraceConstant.CANCEL_BY_SELLER_TIMEOUT;
				}
				break;
			default:
				break;
		}
		return remark;
	}

	@Override
	public Response<RefundsTraceResp> findOrderTrace(String orderId) {
		Response<RefundsTraceResp> resp = new Response<RefundsTraceResp>();
		RefundsTraceResp respData = new RefundsTraceResp();
		// 根据订单Id查询订单
		TradeOrder tradeOrder = tradeOrderMapper.selectByPrimaryKey(orderId);
		// 根据订单ID查询轨迹列表
		List<TradeOrderTrace> traceList = tradeOrderTraceMapper.findTraceList(orderId);
		// 定义返回给App的退款轨迹列表
		List<RefundsTraceVo> traceVoList = new ArrayList<RefundsTraceVo>();
		RefundsTraceVo traceVo = null;
		// 判断是否为历史退款单。如果是，则直接返回
		if (isHistory(traceList)) {
			// 如果是历史定单，直接响应。
			resp.setCode(ResultCodeEnum.SUCCESS.getCode());
			// 是否为历史订单，0：否，1：是
			respData.setIsHistory(WhetherEnum.whether.ordinal());
			resp.setData(respData);
			return resp;
		}
		for (TradeOrderTrace trace : traceList) {
			traceVo = new RefundsTraceVo();
			traceVo.setTitle(trace.getTraceStatus().getDesc());
			traceVo.setContent(trace.getRemark());
			traceVo.setTime(DateUtils.formatDate(trace.getOptTime(), "MM-dd HH:mm"));
			// 是否已完成 0：否，1：是
			traceVo.setIsDone(WhetherEnum.whether.ordinal());
			traceVoList.add(traceVo);
		}
		// 填充未完成的轨迹节点
		fillUncompletedTrace(traceVoList, tradeOrder.getStatus());
		respData.setTraceList(traceVoList);
		resp.setData(respData);
		resp.setCode(ResultCodeEnum.SUCCESS.getCode());
		return resp;
	}

	/**
	 * @Description: 判断是否为历史记录
	 * @param traceList
	 * @return   
	 * @author maojj
	 * @date 2016年11月7日
	 */
	private boolean isHistory(List<TradeOrderTrace> traceList) {
		if (CollectionUtils.isEmpty(traceList)) {
			// 没有任何轨迹，则认为是历史退款单
			return true;
		}
		TradeOrderTrace firstNode = traceList.get(0);
		if (firstNode.getTraceStatus() != OrderTraceEnum.SUBMIT_ORDER
				&& firstNode.getTraceStatus() != OrderTraceEnum.WAIT_RECEIVE) {
			// 如果第一个轨迹节点不是提交订单或者是待接单，也认为是历史订单
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @Description: 填充未完成的轨迹节点
	 * @param traceVoList
	 * @param orderStatus   
	 * @author maojj
	 * @date 2016年11月7日
	 */
	private void fillUncompletedTrace(List<RefundsTraceVo> traceVoList, OrderStatusEnum orderStatus) {
		switch (orderStatus) {
			case UNPAID:
			case BUYER_PAYING:
				traceVoList.add(buildUncompletedTrace(OrderTraceEnum.WAIT_RECEIVE.getDesc(),
						OrderTraceConstant.WAIT_RECEIVE_REMARK));
				traceVoList.add(buildUncompletedTrace(OrderTraceEnum.WAIT_DISPATCHED.getDesc(),
						OrderTraceConstant.WAIT_DISPATCHED_REMARK));
				traceVoList.add(
						buildUncompletedTrace(OrderTraceEnum.SET_OUT.getDesc(), OrderTraceConstant.SET_OUT_REMARK));
				traceVoList.add(
						buildUncompletedTrace(OrderTraceEnum.COMPLETED.getDesc(), OrderTraceConstant.COMPLETED_REMARK));
				break;
			case WAIT_RECEIVE_ORDER:
				traceVoList.add(buildUncompletedTrace(OrderTraceEnum.WAIT_DISPATCHED.getDesc(),
						OrderTraceConstant.WAIT_DISPATCHED_REMARK));
				traceVoList.add(
						buildUncompletedTrace(OrderTraceEnum.SET_OUT.getDesc(), OrderTraceConstant.SET_OUT_REMARK));
				traceVoList.add(
						buildUncompletedTrace(OrderTraceEnum.COMPLETED.getDesc(), OrderTraceConstant.COMPLETED_REMARK));
				break;
			case DROPSHIPPING:
				traceVoList.add(
						buildUncompletedTrace(OrderTraceEnum.SET_OUT.getDesc(), OrderTraceConstant.SET_OUT_REMARK));
				traceVoList.add(
						buildUncompletedTrace(OrderTraceEnum.COMPLETED.getDesc(), OrderTraceConstant.COMPLETED_REMARK));
				break;
			case TO_BE_SIGNED:
				traceVoList.add(
						buildUncompletedTrace(OrderTraceEnum.COMPLETED.getDesc(), OrderTraceConstant.COMPLETED_REMARK));
				break;
			default:
				break;
		}
	}

	/**
	 * @Description: 构建未完成的轨迹节点
	 * @param title
	 * @param content
	 * @return   
	 * @author maojj
	 * @date 2016年11月7日
	 */
	private RefundsTraceVo buildUncompletedTrace(String title, String content) {
		RefundsTraceVo traceVo = new RefundsTraceVo();
		traceVo.setTitle(title);
		traceVo.setContent(content);
		traceVo.setIsDone(WhetherEnum.not.ordinal());
		return traceVo;
	}
}
