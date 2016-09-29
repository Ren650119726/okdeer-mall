package com.okdeer.mall.order.service.impl;

import static com.okdeer.mall.order.constant.RefundsTraceConstant.REFUND_APPLY_REMARK;
import static com.okdeer.mall.order.constant.RefundsTraceConstant.SELLER_WAIT_RETURN;
import static com.okdeer.mall.order.constant.RefundsTraceConstant.WAIT_BUYER_REMARK;
import static com.okdeer.mall.order.constant.RefundsTraceConstant.WAIT_SELLER_REMARK;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.common.utils.DateUtils;
import com.okdeer.mall.common.vo.Response;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsTrace;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.RefundsLogisticsEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.enums.RefundsTraceEnum;
import com.okdeer.mall.order.mapper.TradeOrderRefundsMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsTraceMapper;
import com.okdeer.mall.order.service.TradeOrderRefundsService;
import com.okdeer.mall.order.service.TradeOrderRefundsTraceService;
import com.okdeer.mall.order.service.TradeOrderRefundsTraceServiceApi;
import com.okdeer.mall.order.vo.RefundsTraceResp;
import com.okdeer.mall.order.vo.RefundsTraceVo;

/**
 * ClassName: TradeOrderRefundsTraceServiceImpl 
 * @Description: 交易订单退款轨迹Service
 * @author maojj
 * @date 2016年9月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构1.1			2016年9月28日				maojj		 交易订单退款轨迹Service
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderRefundsTraceServiceApi")
public class TradeOrderRefundsTraceServiceImpl implements TradeOrderRefundsTraceService,TradeOrderRefundsTraceServiceApi{

	/**
	 * TradeOrderRefundsTrace 数据访问接口
	 */
	@Autowired
	private TradeOrderRefundsTraceMapper tradeOrderRefundsTraceMapper;
	
	@Autowired
	private TradeOrderRefundsMapper tradeOrderRefundsMapper;
	
	@Override
	public void saveRefundTrace(TradeOrderRefunds refundsOrder){
		saveRefundTrace(refundsOrder,null);
	}
	
	@Override
	public void saveRefundTrace(TradeOrderRefunds refundsOrder,RefundsLogisticsEnum logisticsType){
		if(refundsOrder.getType() != null && refundsOrder.getType() != OrderTypeEnum.PHYSICAL_ORDER){
			return;
		}
		switch (refundsOrder.getRefundsStatus()) {
			case WAIT_SELLER_VERIFY:
				saveRefundApplyTrace(refundsOrder);
				break;
			case BUYER_REPEAL_REFUND:
				saveCancelRefundTrace(refundsOrder.getId());
				break;
			case WAIT_BUYER_RETURN_GOODS:
			case SELLER_REJECT_APPLY:
				saveSellerDealTrace(refundsOrder);
				break;
			case WAIT_SELLER_REFUND:
				saveBuyerReturnTrace(refundsOrder.getId(),logisticsType);
				break;
			case SELLER_REJECT_REFUND:
			case REFUND_SUCCESS:	
				saveSellerRefundTrace(refundsOrder);
				break;
			case APPLY_CUSTOMER_SERVICE_INTERVENE:
				saveApplyCustomerServTrace(refundsOrder);
				break;
			default:
				break;
		}
	}
	
	/**
	 * @Description: 保存退款申请轨迹(用户申请退款时走该操作)
	 * @param refundsOrder   
	 * @author maojj
	 * @date 2016年9月28日
	 */
	public void saveRefundApplyTrace(TradeOrderRefunds refundsOrder) {
		List<TradeOrderRefundsTrace> traceList = new ArrayList<TradeOrderRefundsTrace>();
		TradeOrderRefundsTrace trace = new TradeOrderRefundsTrace();
		trace.setId(UuidUtils.getUuid());
		trace.setRefundsId(refundsOrder.getId());
		trace.setOptTime(DateUtils.getSysDate());
		//到店消费的订单提示是退款
		if(refundsOrder.getType() == OrderTypeEnum.STORE_CONSUME_ORDER){
			trace.setTraceStatus(RefundsTraceEnum.REFUND_APPLY);
		}else{
			trace.setTraceStatus(RefundsTraceEnum.RETURN_REFUND_APPLY);
		}
		trace.setRemark(String.format(REFUND_APPLY_REMARK, refundsOrder.getRefundNo()));
		traceList.add(trace);
		
		trace = new TradeOrderRefundsTrace();
		trace.setId(UuidUtils.getUuid());
		trace.setRefundsId(refundsOrder.getId());
		trace.setOptTime(DateUtils.getSysDate());
		trace.setTraceStatus(RefundsTraceEnum.WAIT_SELLER_DEAL);
		trace.setRemark(WAIT_SELLER_REMARK);
		traceList.add(trace);
		tradeOrderRefundsTraceMapper.batchInsert(traceList);
	}
	
	/**
	 * @Description: 卖家处理轨迹（卖家拒绝退款申请或接受退款申请时走此操作。这里是操作用户发起请求的操作轨迹）
	 * @param refundsOrder   
	 * @author maojj
	 * @date 2016年9月28日
	 */
	public void saveSellerDealTrace(TradeOrderRefunds refundsOrder) {
		List<TradeOrderRefundsTrace> traceList = new ArrayList<TradeOrderRefundsTrace>();
		TradeOrderRefundsTrace trace = new TradeOrderRefundsTrace();
		trace.setId(UuidUtils.getUuid());
		trace.setRefundsId(refundsOrder.getId());
		trace.setOptTime(DateUtils.getSysDate());
		if(refundsOrder.getRefundsStatus() ==  RefundsStatusEnum.WAIT_BUYER_RETURN_GOODS){
			trace.setTraceStatus(RefundsTraceEnum.SELLER_AGREE_REFUND);
		}else if(refundsOrder.getRefundsStatus() ==  RefundsStatusEnum.SELLER_REJECT_APPLY){
			trace.setTraceStatus(RefundsTraceEnum.SELLER_REFUSE_REFUND);
		}
		trace.setRemark("");
		traceList.add(trace);
		
		trace = new TradeOrderRefundsTrace();
		trace.setId(UuidUtils.getUuid());
		trace.setRefundsId(refundsOrder.getId());
		trace.setOptTime(DateUtils.getSysDate());
		trace.setTraceStatus(RefundsTraceEnum.WAIT_BUYER_DEAL);
		trace.setRemark(WAIT_BUYER_REMARK);
		traceList.add(trace);
		tradeOrderRefundsTraceMapper.batchInsert(traceList);
	}
	
	/**
	 * @Description: 保存申请客服介入轨迹
	 * @param refundsOrder   
	 * @author maojj
	 * @date 2016年9月28日
	 */
	public void saveApplyCustomerServTrace(TradeOrderRefunds refundsOrder) {
		TradeOrderRefundsTrace trace = new TradeOrderRefundsTrace();
		trace.setId(UuidUtils.getUuid());
		trace.setRefundsId(refundsOrder.getId());
		trace.setOptTime(DateUtils.getSysDate());
		trace.setTraceStatus(RefundsTraceEnum.BUYER_APPLY_CUSTOMER_SERVICE);
		trace.setRemark("");
		tradeOrderRefundsTraceMapper.insert(trace);
	}
	
	/**
	 * @Description: 保存买家退货轨迹
	 * @param refundsId
	 * @param logisticsType   
	 * @author maojj
	 * @date 2016年9月28日
	 */
	public void saveBuyerReturnTrace(String refundsId,RefundsLogisticsEnum logisticsType){
		List<TradeOrderRefundsTrace> traceList = new ArrayList<TradeOrderRefundsTrace>();
		TradeOrderRefundsTrace trace = new TradeOrderRefundsTrace();
		trace.setId(UuidUtils.getUuid());
		trace.setRefundsId(refundsId);
		trace.setOptTime(DateUtils.getSysDate());
		trace.setTraceStatus(RefundsTraceEnum.BUYER_APPLY_CUSTOMER_SERVICE);
		switch (logisticsType) {
			case LOGISTICS:
				trace.setTraceStatus(RefundsTraceEnum.BUYER_CHOOSE_LOGISTICS);
				break;
			case DOOR_PICK_UP:
				trace.setTraceStatus(RefundsTraceEnum.BUYER_CHOOSE_PICKUP);
				break;
			case TO_STORE_REFUND:
				trace.setTraceStatus(RefundsTraceEnum.BUYER_CHOOSE_RETURN);
				break;
			default:
				break;
		}
		trace.setRemark("");
		traceList.add(trace);
		
		trace = new TradeOrderRefundsTrace();
		trace.setId(UuidUtils.getUuid());
		trace.setRefundsId(refundsId);
		trace.setOptTime(DateUtils.getSysDate());
		switch (logisticsType) {
			case LOGISTICS:
				trace.setTraceStatus(RefundsTraceEnum.WAIT_SELLER_DELIVERY);
				break;
			case DOOR_PICK_UP:
				trace.setTraceStatus(RefundsTraceEnum.WAIT_SELLER_PICKUP);
				break;
			case TO_STORE_REFUND:
				trace.setTraceStatus(RefundsTraceEnum.WAIT_BUYER_RETURN);
				break;
			default:
				break;
		}		
		trace.setRemark(SELLER_WAIT_RETURN);
		traceList.add(trace);
		tradeOrderRefundsTraceMapper.batchInsert(traceList);
	}
	
	/**
	 * @Description: 保存取消退款轨迹
	 * @param refundsId   
	 * @author maojj
	 * @date 2016年9月28日
	 */
	public void saveCancelRefundTrace(String refundsId){
		TradeOrderRefundsTrace trace = new TradeOrderRefundsTrace();
		trace.setId(UuidUtils.getUuid());
		trace.setRefundsId(refundsId);
		trace.setOptTime(DateUtils.getSysDate());
		trace.setTraceStatus(RefundsTraceEnum.BUYER_CANCEL_REFUND);
		trace.setRemark("");
		tradeOrderRefundsTraceMapper.insert(trace);
	}
	
	/**
	 * @Description: 保存商家退款轨迹。（指商家已取到用户的退货商品之后，所做的处理，是否退款）
	 * @param refundsOrder   
	 * @author maojj
	 * @date 2016年9月28日
	 */
	public void saveSellerRefundTrace(TradeOrderRefunds refundsOrder){
		List<TradeOrderRefundsTrace> traceList = new ArrayList<TradeOrderRefundsTrace>();
		TradeOrderRefundsTrace trace = new TradeOrderRefundsTrace();
		trace.setId(UuidUtils.getUuid());
		trace.setRefundsId(refundsOrder.getId());
		trace.setOptTime(DateUtils.getSysDate());
		if(refundsOrder.getRefundsStatus() ==  RefundsStatusEnum.SELLER_REJECT_REFUND){
			trace.setTraceStatus(RefundsTraceEnum.BUSINESS_REFUSE_REFUND);
		}else if(refundsOrder.getRefundsStatus() ==  RefundsStatusEnum.REFUND_SUCCESS){
			trace.setTraceStatus(RefundsTraceEnum.REFUND_SUCCESS);
		}
		trace.setRemark("");
		traceList.add(trace);
		
		if(refundsOrder.getRefundsStatus() ==  RefundsStatusEnum.SELLER_REJECT_REFUND){
			trace = new TradeOrderRefundsTrace();
			trace.setId(UuidUtils.getUuid());
			trace.setRefundsId(refundsOrder.getId());
			trace.setOptTime(DateUtils.getSysDate());
			trace.setTraceStatus(RefundsTraceEnum.WAIT_BUYER_DEAL);
			trace.setRemark(WAIT_BUYER_REMARK);
			traceList.add(trace);
		}
		tradeOrderRefundsTraceMapper.batchInsert(traceList);
	}
	
	/**
	 * @Description: 查找用户轨迹列表
	 * @param refundsId 退款单ID
	 * @return   
	 * @author maojj
	 * @date 2016年9月28日
	 */
	@Override
	public Response<RefundsTraceResp> findRefundsTrace(String refundsId) {
		Response<RefundsTraceResp> resp = new Response<RefundsTraceResp>();
		RefundsTraceResp respData = new RefundsTraceResp();
		// 根据退款单id查询退款单
		TradeOrderRefunds refundsOrder =tradeOrderRefundsMapper.selectByPrimaryKey(refundsId);
		if(refundsOrder == null){
			// 如果未找到退款单ID，表示该请求数据不对，返回错误代码。
			resp.setCode(ResultCodeEnum.FAIL.ordinal());
			return resp;
		}
		// 设置当前退款单所处的状态
		respData.setRefundStatus(refundsOrder.getRefundsStatus().ordinal());
		// 根据退款单ID查询退款轨迹列表
		List<TradeOrderRefundsTrace> traceList = tradeOrderRefundsTraceMapper.findRefundsTrace(refundsId);
		// 定义返回给App的退款轨迹列表
		List<RefundsTraceVo> traceVoList = new ArrayList<RefundsTraceVo>();
		RefundsTraceVo traceVo = null;
		int index = 0;
		int size = traceList.size();
		for (TradeOrderRefundsTrace trace : traceList) {
			traceVo = new RefundsTraceVo();
			traceVo.setTitle(trace.getTraceStatus().getDesc());
			// 如果退款轨迹状态为：等待您的处理或者是等待卖家的处理，当所处状态不是最后一个节点时，无需显示备注信息，如果是最后一个节点，需要显示备注的提示信息
			if ((trace.getTraceStatus() == RefundsTraceEnum.WAIT_BUYER_DEAL
					|| trace.getTraceStatus() == RefundsTraceEnum.WAIT_SELLER_DEAL) && index < size - 1) {
				traceVo.setContent("");
			} else {
				traceVo.setContent(trace.getRemark());
			}
			traceVo.setTime(DateUtils.formatDateTime(trace.getOptTime()));
			traceVoList.add(traceVo);
			index++;
		}
		respData.setTraceList(traceVoList);
		resp.setData(respData);
		resp.setCode(ResultCodeEnum.SUCCESS.getCode());
		return resp;
	}
}
