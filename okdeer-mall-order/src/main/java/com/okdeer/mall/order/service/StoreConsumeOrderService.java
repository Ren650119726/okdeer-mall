
package com.okdeer.mall.order.service;

import java.util.List;
import java.util.Map;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.vo.ExpireStoreConsumerOrderVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsCertificateVo;

import net.sf.json.JSONObject;

/**
 * ClassName: StoreConsumeOrderService 
 * @Description: 到店消费订单service
 * @author zengjizu
 * @date 2016年9月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * v1.2.0            2016-11-28          zengjz             根据退款单项id查询订单明细列表
 */
public interface StoreConsumeOrderService {

	/**
	 * @Description: 查询过期订单列表
	 * @return
	 * @author zengjizu
	 * @date 2016年9月29日
	 */
	List<ExpireStoreConsumerOrderVo> findExpireOrder();

	/**
	 * @Description: 处理过期订单
	 * @param order
	 * @throws Exception
	 * @author zengjizu
	 * @date 2016年11月15日
	 */
	void handleExpireOrder(TradeOrder order) throws Exception;
	
	/**
	 * @Description: 消费码退款
	 * @param order 订单信息
	 * @param orderRefunds 退款单信息
	 * @param certificate 退款凭证
	 * @param waitRefundDetailList 退款消费码
	 * @author zengjizu 
	 * @throws Exception 
	 * @date 2016年10月11日
	 */
	void refundConsumeCode(TradeOrder order, TradeOrderRefunds orderRefunds, TradeOrderRefundsCertificateVo certificate,
			List<TradeOrderItemDetail> waitRefundDetailList) throws Exception;
	
	/**
	 * @Description: 查询消费码订单列表
	 * @param map 查询条件
	 * @param pageNo 当前页
	 * @param pageSize 每页大小
	 * @return PageUtils<TradeOrder>  订单列表分页信息
	 * @author zengjizu
	 * @date 2016年9月20日
	 */
	PageUtils<TradeOrder> findStoreConsumeOrderList(Map<String, Object> map, Integer pageNo, Integer pageSize);
	
	/**
	 * @Description: 查询到店消费订单详情
	 * @param orderId 订单id
	 * @return JSONObject 订单详情数据
	 * @author zengjizu
	 * @date 2016年9月23日
	 */
	JSONObject findStoreConsumeOrderDetail(String orderId)  throws Exception ;
	
	/**
	 * @Description: 获取退款单列表
	 * @param map 查询条件
	 * @param pageNumber 当前叶
	 * @param pageSize 每页大小
	 * @return 退款单列表信息
	 * @author zengjizu
	 * @date 2016年9月24日
	 */
	PageUtils<TradeOrderRefunds> findUserRefundOrderList(Map<String, Object> map, Integer pageNumber, Integer pageSize);
	
	/**
	 * @Description: 根据退款id查询退款单详情
	 * @param refundId 退款单id
	 * @return 退款单详情
	 * @author zengjizu
	 * @date 2016年9月24日
	 */
	TradeOrderRefunds getRefundOrderDetail(String refundId);
	
	/**
	 * @Description: 查询订单明细列表
	 * @param orderId 订单id
	 * @param status 消费码状态
	 * @return 消费码列表
	 * @author zengjizu
	 * @date 2016年9月24日
	 */
	List<TradeOrderItemDetail> getStoreConsumeOrderDetailList(String orderId,int status);
	

	//begin V1.2.0 add by zengjz 20161128
	/**
	 * @Description: 根据退款单项id查询订单明细列表
	 * @param refundItemId 退款单项id
	 * @return
	 * @author zengjizu
	 * @date 2016年11月28日
	 */
	List<TradeOrderItemDetail> findRefundTradeOrderItemDetailList(String refundItemId);
	
	//end V1.2.0 add by zengjz 20161128
}
