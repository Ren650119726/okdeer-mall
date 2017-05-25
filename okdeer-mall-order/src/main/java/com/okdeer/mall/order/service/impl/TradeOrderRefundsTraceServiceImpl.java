package com.okdeer.mall.order.service.impl;

import static com.okdeer.mall.order.constant.RefundsTraceConstant.REFUND_APPLY_REMARK;
import static com.okdeer.mall.order.constant.RefundsTraceConstant.SELLER_WAIT_RETURN;
import static com.okdeer.mall.order.constant.RefundsTraceConstant.WAIT_BUYER_REMARK;
import static com.okdeer.mall.order.constant.RefundsTraceConstant.WAIT_SELLER_REMARK;
import static com.okdeer.mall.order.constant.RefundsTraceConstant.DEFAULT_NULL_REMARK;
import static com.okdeer.mall.order.constant.RefundsTraceConstant.CUSTOMER_CANCEL_REMARK;
import static com.okdeer.mall.order.constant.RefundsTraceConstant.OKDEER_REFUND_REMARK;
import static com.okdeer.mall.order.constant.RefundsTraceConstant.FORCE_SELLER_REFUND_REMARK;
import static com.okdeer.mall.order.constant.RefundsTraceConstant.APPLY_CUSTOMER_SERV_REAMRK;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.common.utils.DateUtils;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsTrace;
import com.okdeer.mall.order.enums.OrderAppStatusAdaptor;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.RefundsLogisticsEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.enums.RefundsTraceEnum;
import com.okdeer.mall.order.mapper.TradeOrderRefundsMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsTraceMapper;
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
 *		Bug:13658		2016年10月10日				maojj		查询退款轨迹时返回参数增加isDone。默认均为1	
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
		// 友门鹿退款成功和待友门鹿退款合成一种轨迹，所以友门鹿退款成功不做任何处理
		if(refundsOrder.getRefundsStatus() == null){
			return;
		}
		List<TradeOrderRefundsTrace> traceList = createTraceList(refundsOrder);
		if(!CollectionUtils.isEmpty(traceList)){
			tradeOrderRefundsTraceMapper.batchInsert(traceList);
		}
	}
	
	private List<TradeOrderRefundsTrace> createTraceList(TradeOrderRefunds refundsOrder){
		// 轨迹列表
		List<TradeOrderRefundsTrace> traceList = new ArrayList<TradeOrderRefundsTrace>();
		// 构建操作轨迹
		TradeOrderRefundsTrace optTrace = buildOptTrace(refundsOrder);
		// 构建等待处理轨迹
		TradeOrderRefundsTrace waitDealTrace = buildWaitDealTrace(refundsOrder);
		if(optTrace != null && optTrace.getTraceStatus() != null){
			traceList.add(optTrace);
		}
		if(waitDealTrace != null){
			traceList.add(waitDealTrace);
		}
		return traceList;
	}
	
	/**
	 * @Description: 构建操作轨迹
	 * @param refundsOrder 退款单对象
	 * @return   
	 * @author maojj
	 * @date 2016年10月12日
	 */
	private TradeOrderRefundsTrace buildOptTrace(TradeOrderRefunds refundsOrder){
		TradeOrderRefundsTrace optTrace = initRefundsTrace(refundsOrder.getId());
		// 填充轨迹状态和备注
		fillTraceStatusAndRemark(optTrace,refundsOrder);
		return optTrace;
	}
	
	/**
	 * @Description: 构建等待处理轨迹
	 * @param refundsOrder
	 * @return   
	 * @author maojj
	 * @date 2016年10月12日
	 */
	private TradeOrderRefundsTrace buildWaitDealTrace(TradeOrderRefunds refundsOrder){
		if(!isNeedWaitDeal(refundsOrder.getRefundsStatus())){
			// 不需要等待处理流程。
			return null;
		}
		TradeOrderRefundsTrace waitDealTrace = initRefundsTrace(refundsOrder.getId());
		switch (refundsOrder.getRefundsStatus()) {
			case WAIT_SELLER_VERIFY:
				if(refundsOrder.getType() == OrderTypeEnum.STORE_CONSUME_ORDER){
					// 到店消费的订单，发起退款，则直接退款成功
					waitDealTrace.setTraceStatus(RefundsTraceEnum.REFUND_SUCCESS);
					waitDealTrace.setRemark(DEFAULT_NULL_REMARK);
				}else{
					waitDealTrace.setTraceStatus(RefundsTraceEnum.WAIT_SELLER_DEAL);
					waitDealTrace.setRemark(WAIT_SELLER_REMARK);
				}
				break;
			case WAIT_BUYER_RETURN_GOODS:
			case SELLER_REJECT_APPLY:
				waitDealTrace.setTraceStatus(RefundsTraceEnum.WAIT_BUYER_DEAL);
				waitDealTrace.setRemark(WAIT_BUYER_REMARK);
				break;
			case SELLER_REJECT_REFUND:
				waitDealTrace.setTraceStatus(RefundsTraceEnum.WAIT_BUYER_RESP);
				waitDealTrace.setRemark(WAIT_BUYER_REMARK);
				break;
			case WAIT_SELLER_REFUND:
				waitDealTrace.setTraceStatus(getWaitDealTraceStatus(refundsOrder.getLogisticsType()));
				waitDealTrace.setRemark(SELLER_WAIT_RETURN);
				break;
			default:
				break;
		}
		return waitDealTrace;
	} 
	
	/**
	 * @Description: 是否需要等待处理
	 * @param refundsStatus
	 * @return   
	 * @author maojj
	 * @date 2016年10月12日
	 */
	private boolean isNeedWaitDeal(RefundsStatusEnum refundsStatus){
		boolean isNeedWaitDeal = true;
		switch (refundsStatus) {
			case APPLY_CUSTOMER_SERVICE_INTERVENE:
			case CUSTOMER_SERVICE_CANCEL_INTERVENE:
			case BUYER_REPEAL_REFUND:
			case YSC_REFUND:
			case FORCE_SELLER_REFUND:
			case FORCE_SELLER_REFUND_SUCCESS:
			case SELLER_REFUNDING:
			case REFUND_SUCCESS:
			case YSC_REFUND_SUCCESS:
				isNeedWaitDeal = false;
				break;
			default:
				break;
		}
		return isNeedWaitDeal;
	}
	
	/**
	 * @Description: 初始化退款轨迹对象
	 * @param refundId
	 * @return   
	 * @author maojj
	 * @date 2016年10月12日
	 */
	private TradeOrderRefundsTrace initRefundsTrace(String refundId){
		TradeOrderRefundsTrace trace = new TradeOrderRefundsTrace();
		trace.setId(UuidUtils.getUuid());
		trace.setRefundsId(refundId);
		trace.setOptTime(DateUtils.getSysDate());
		return trace;
	}
	
	/**
	 * @Description: 设置轨迹状态和备注文案
	 * @param trace
	 * @param refundsOrder   
	 * @author maojj
	 * @date 2016年10月12日
	 */
	private void fillTraceStatusAndRemark(TradeOrderRefundsTrace trace,TradeOrderRefunds refundsOrder){
		// 轨迹状态
		RefundsTraceEnum traceStatus = null;
		// 轨迹备注信息
		String remark = DEFAULT_NULL_REMARK;
		switch (refundsOrder.getRefundsStatus()) {
			case WAIT_SELLER_VERIFY:
				// 用户发起退款申请，等待商家确认
				traceStatus = getTraceStatus(refundsOrder.getType());
				remark = String.format(REFUND_APPLY_REMARK, refundsOrder.getRefundNo());
				break;
			case WAIT_BUYER_RETURN_GOODS:
				// 商家同意退货退款申请
				traceStatus = RefundsTraceEnum.SELLER_AGREE_REFUND;
				break;
			case SELLER_REJECT_APPLY:
				// 商家拒绝退款退货申请
				traceStatus = RefundsTraceEnum.SELLER_REFUSE_REFUND;
				break;
			case WAIT_SELLER_REFUND:
				// 等待卖家退款
				traceStatus = getTraceStatus(refundsOrder.getLogisticsType());
				break;
			case SELLER_REJECT_REFUND:
				// 商家拒绝退款
				traceStatus = RefundsTraceEnum.BUSINESS_REFUSE_REFUND;
				break;
			case APPLY_CUSTOMER_SERVICE_INTERVENE:
				// 申请客服介入
				traceStatus = RefundsTraceEnum.BUYER_APPLY_CUSTOMER_SERVICE;
				remark = APPLY_CUSTOMER_SERV_REAMRK;
				break;
			case CUSTOMER_SERVICE_CANCEL_INTERVENE:
				// 客服介入取消
				traceStatus = RefundsTraceEnum.CANCEL_CUSTOMER_SERVICE;
				remark = CUSTOMER_CANCEL_REMARK;
				break;
			case YSC_REFUND:
				// 待友门鹿退款
				traceStatus = RefundsTraceEnum.CUSTOMER_SERVICE_DEAL;
				remark = OKDEER_REFUND_REMARK;
				break;
			case FORCE_SELLER_REFUND:
				// 卖家退款成功(强制)
				traceStatus = RefundsTraceEnum.CUSTOMER_SERVICE_DEAL;
				remark = FORCE_SELLER_REFUND_REMARK;
				break;
			case SELLER_REFUNDING:
				// 卖家退款中
				traceStatus = RefundsTraceEnum.BUSINESS_AGREE_REFUND;
				break;
			case REFUND_SUCCESS : 
			case YSC_REFUND_SUCCESS:
			case FORCE_SELLER_REFUND_SUCCESS:
				traceStatus = RefundsTraceEnum.REFUND_SUCCESS;
				break;
			case BUYER_REPEAL_REFUND:
				traceStatus = RefundsTraceEnum.BUYER_CANCEL_REFUND;
				break;
			default:
				break;
		}
		
		trace.setTraceStatus(traceStatus);
		trace.setRemark(remark);
	}
	
	/**
	 * @Description: 根据订单类型获取退款轨迹状态
	 * @param orderType
	 * @return   
	 * @author maojj
	 * @date 2016年10月12日
	 */
	private RefundsTraceEnum getTraceStatus(OrderTypeEnum orderType) {
		if (orderType == OrderTypeEnum.STORE_CONSUME_ORDER) {
			// 到店消费订单，只需要进行退款
			return RefundsTraceEnum.REFUND_APPLY;
		} else {
			return RefundsTraceEnum.RETURN_REFUND_APPLY;
		}
	}
	
	/**
	 * @Description: 根据退货方式获取退款轨迹状态
	 * @param logisticsType
	 * @return   
	 * @author maojj
	 * @date 2016年10月12日
	 */
	private RefundsTraceEnum getTraceStatus(RefundsLogisticsEnum logisticsType){
		RefundsTraceEnum traceStatus = null;
		switch (logisticsType) {
			case LOGISTICS:
				traceStatus = RefundsTraceEnum.BUYER_CHOOSE_LOGISTICS;
				break;
			case DOOR_PICK_UP:
				traceStatus = RefundsTraceEnum.BUYER_CHOOSE_PICKUP;
				break;
			case TO_STORE_REFUND:
				traceStatus = RefundsTraceEnum.BUYER_CHOOSE_RETURN;
				break;
			default:
				break;
		}
		return traceStatus;
	}
	
	/**
	 * @Description: 根据退货方式获取等待处理的退款轨迹状态
	 * @param logisticsType
	 * @return   
	 * @author maojj
	 * @date 2016年10月12日
	 */
	private RefundsTraceEnum getWaitDealTraceStatus(RefundsLogisticsEnum logisticsType){
		RefundsTraceEnum traceStatus = null;
		switch (logisticsType) {
			case LOGISTICS:
				traceStatus = RefundsTraceEnum.WAIT_SELLER_DELIVERY;
				break;
			case DOOR_PICK_UP:
				traceStatus = RefundsTraceEnum.WAIT_SELLER_PICKUP;
				break;
			case TO_STORE_REFUND:
				traceStatus = RefundsTraceEnum.WAIT_BUYER_RETURN;
				break;
			default:
				break;
		}
		return traceStatus;
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
			resp.setCode(ResultCodeEnum.FAIL.getCode());
			return resp;
		}
		// Begin added by maojj 2016-10-19
		// 设置当前退款单所处的状态
		if(refundsOrder.getRefundsStatus() == RefundsStatusEnum.SELLER_REFUNDING){
			// 退款中的状态返回app为等待买家退货。这块的处理主要是为了显示“退款成功”的虚节点。不具备业务意义。
			refundsOrder.setRefundsStatus(RefundsStatusEnum.WAIT_BUYER_RETURN_GOODS);
		}
		// End added by maojj 2016-10-19
		respData.setRefundStatus(OrderAppStatusAdaptor.convertAppRefundStatus(refundsOrder.getRefundsStatus()));
		// 根据退款单ID查询退款轨迹列表
		List<TradeOrderRefundsTrace> traceList = tradeOrderRefundsTraceMapper.findRefundsTrace(refundsId);
		// 定义返回给App的退款轨迹列表
		List<RefundsTraceVo> traceVoList = new ArrayList<RefundsTraceVo>();
		RefundsTraceVo traceVo = null;
		int index = 0;
		int size = traceList.size();
		// Begin added by maojj 2016-10-13 增加对历史退款单的处理。因为历史退款单没有轨迹记录。
		// 判断是否为历史退款单。如果是，则直接返回
		if(isHistory(traceList)){
			// 如果是历史退款单，直接响应。
			resp.setCode(ResultCodeEnum.SUCCESS.getCode());
			// 是否为历史退款单，0：否，1：是
			respData.setIsHistory(Constant.ONE);
			resp.setData(respData);
			return resp;
		}
		// End added by maojj 2016-10-13 
		for (TradeOrderRefundsTrace trace : traceList) {
			traceVo = new RefundsTraceVo();
			traceVo.setTitle(trace.getTraceStatus().getDesc());
			// 如果退款轨迹状态为：等待您的处理或者是等待卖家的处理，当所处状态不是最后一个节点时，无需显示备注信息，如果是最后一个节点，需要显示备注的提示信息
			String remark = trace.getRemark();
			if ((WAIT_SELLER_REMARK.equals(remark) || WAIT_BUYER_REMARK.equals(remark) || SELLER_WAIT_RETURN.equals(remark)) && index < size - 1) {
				traceVo.setContent(DEFAULT_NULL_REMARK);
			} else {
				traceVo.setContent(remark);
			}
			traceVo.setTime(DateUtils.formatDate(trace.getOptTime(),"MM-dd HH:mm"));
			traceVo.setIsDone(1);
			traceVoList.add(traceVo);
			index++;
		}
		respData.setTraceList(traceVoList);
		resp.setData(respData);
		resp.setCode(ResultCodeEnum.SUCCESS.getCode());
		return resp;
	}
	
	/**
	 * @Description: 是否为历史退款单
	 * @param traceList 退款轨迹列表
	 * @return   
	 * @author maojj
	 * @date 2016年10月13日
	 */
	private boolean isHistory(List<TradeOrderRefundsTrace> traceList){
		if(CollectionUtils.isEmpty(traceList)){
			// 没有任何轨迹，则认为是历史退款单
			return true;
		}
		TradeOrderRefundsTrace firstNode = traceList.get(0);
		if(firstNode.getTraceStatus() != RefundsTraceEnum.REFUND_APPLY && firstNode.getTraceStatus() != RefundsTraceEnum.RETURN_REFUND_APPLY  ){
			// 如果第一个轨迹节点不是提交退款申请状态，也认为是历史退款单
			return true;
		}else{
			return false;
		}
	}
}
