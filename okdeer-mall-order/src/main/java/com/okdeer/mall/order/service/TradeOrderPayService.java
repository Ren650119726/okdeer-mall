package com.okdeer.mall.order.service;


import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.base.common.exception.ServiceException;

/**
 * 
 * ClassName: TradeOrderPayService 
 * @Description: TODO
 * @author zengjizu
 * @date 2016年11月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     v1.2.0            2016-11-18         zengjz           增加服务订单确认调用云钱包方法
 */
public interface TradeOrderPayService {

	void insertSelective(TradeOrderPay tradeOrderPay) throws ServiceException;

	TradeOrderPay selectByOrderId(String orderId) throws ServiceException;

	/**
	 * 订单支付
	 * @param tradeOrderPay
	 * @throws ServiceException
	 */
	void insertOrderPay(TradeOrderPay tradeOrderPay) throws ServiceException;

	/**
	 * 取消订单支付
	 * @return 
	 * @throws Exception 
	 */
	boolean cancelOrderPay(TradeOrder tradeOrder) throws Exception;

	/**
	 * 确认收货打款
	 * @throws MQClientException 
	 */
	boolean confirmOrderPay(TradeOrder tradeOrder) throws Exception;

	

	/**
	 * 验证云钱包用户名是否正确</p>
	 * 
	 * @author yangq
	 * @param orderMoney
	 * @return
	 * @throws Exception
	 */
	public boolean wlletPay(String orderMoney, TradeOrder order) throws Exception;

	/**
	 * 查询订单是否产生交易记录
	 * 
	 * @author yangq
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	int selectTradeOrderPayByOrderId(String orderId) throws Exception;

	/**
	 * 判断订单是否有售后服务
	 */
	boolean isServiceAssurance(TradeOrder order);

	/**
	 * 订单完成更新余额（售后期满）
	 */
	boolean updateBalanceByFinish(TradeOrder tradeOrder) throws Exception;
	
	//bigein add by zengjz  2016-11-18 增加服务订单确认调用云钱包方法
	/**
	 * @Description: 服务订单确认服务完成是资金流变动
	 * @param tradeOrder 订单信息
	 * @author zengjizu
	 * @date 2016年11月18日
	 */
	void confirmStoreServiceOrderPay(TradeOrder tradeOrder) throws Exception ;
	
	//end add by zengjz  2016-11-18 增加服务订单确认调用云钱包方法

}