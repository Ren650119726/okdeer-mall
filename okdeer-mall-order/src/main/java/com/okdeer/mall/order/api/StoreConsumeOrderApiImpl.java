package com.okdeer.mall.order.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.service.StoreConsumeOrderService;
import com.okdeer.mall.order.service.StoreConsumeOrderServiceApi;

import net.sf.json.JSONObject;

/**
 * ClassName: StoreConsumeOrderApiImpl 
 * @Description: 到店消费服务api实现
 * @author zengjizu
 * @date 2016年11月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     v1.2.0            2016-11-15          zengjz           代码优化
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.StoreConsumeOrderServiceApi")
public class StoreConsumeOrderApiImpl implements StoreConsumeOrderServiceApi {
	
	/**
	 * 到店消费service
	 */
	@Autowired
	private StoreConsumeOrderService storeConsumeOrderService;
	
	
	/**
	 * @Description: 查询消费码订单列表
	 * @param map 查询条件
	 * @param pageNo 当前页
	 * @param pageSize 每页大小
	 * @return PageUtils<TradeOrder>  订单列表分页信息
	 * @author zengjizu
	 * @date 2016年9月20日
	 */
	@Override
	public PageUtils<TradeOrder> findStoreConsumeOrderList(Map<String, Object> map, Integer pageNo, Integer pageSize) {

		return storeConsumeOrderService.findStoreConsumeOrderList(map, pageNo, pageSize);
	}
	
	/**
	 * @Description: 查询到店消费订单详情
	 * @param orderId 订单id
	 * @return JSONObject 订单详情数据
	 * @author zengjizu
	 * @date 2016年9月23日
	 */
	@Override
	public JSONObject findStoreConsumeOrderDetail(String orderId) throws Exception {

		return storeConsumeOrderService.findStoreConsumeOrderDetail(orderId);
	}
	
	/**
	 * @Description: 获取退款单列表
	 * @param map 查询条件
	 * @param pageNumber 当前叶
	 * @param pageSize 每页大小
	 * @return 退款单列表信息
	 * @author zengjizu
	 * @date 2016年9月24日
	 */
	@Override
	public PageUtils<TradeOrderRefunds> findUserRefundOrderList(Map<String, Object> map, Integer pageNumber,
			Integer pageSize) {

		return storeConsumeOrderService.findUserRefundOrderList(map, pageNumber, pageSize);
	}
	
	/**
	 * @Description: 根据退款id查询退款单详情
	 * @param refundId 退款单id
	 * @return 退款单详情
	 * @author zengjizu
	 * @date 2016年9月24日
	 */
	@Override
	public TradeOrderRefunds getRefundOrderDetail(String refundId) {

		return storeConsumeOrderService.getRefundOrderDetail(refundId);
	}
	
	/**
	 * @Description: 查询订单明细列表
	 * @param orderId 订单id
	 * @param status 消费码状态
	 * @return 消费码列表
	 * @author zengjizu
	 * @date 2016年9月24日
	 */
	@Override
	public List<TradeOrderItemDetail> getStoreConsumeOrderDetailList(String orderId, int status) {

		return storeConsumeOrderService.getStoreConsumeOrderDetailList(orderId, status);
	}

}
