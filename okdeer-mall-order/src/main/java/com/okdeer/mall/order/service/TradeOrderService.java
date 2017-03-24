
package com.okdeer.mall.order.service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.okdeer.archive.system.pos.entity.PosShiftExchange;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.order.bo.UserOrderParamBo;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.PaymentStatusEnum;
import com.okdeer.mall.order.vo.ActivityInfoVO;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.vo.ERPTradeOrderVo;
import com.okdeer.mall.order.vo.PhysicsOrderVo;
import com.okdeer.mall.order.vo.TradeOrderExportVo;
import com.okdeer.mall.order.vo.TradeOrderOperateParamVo;
import com.okdeer.mall.order.vo.TradeOrderPayQueryVo;
import com.okdeer.mall.order.vo.TradeOrderQueryVo;
import com.okdeer.mall.order.vo.TradeOrderStatisticsVo;
import com.okdeer.mall.order.vo.TradeOrderStatusVo;
import com.okdeer.mall.order.vo.TradeOrderVo;

import net.sf.json.JSONObject;

/**
 * @DESC:
 * @author YSCGD
 * @date 2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *    重构4.1            2016-7-19            wusw                添加财务系统的订单接口（包含服务店订单情况）
 *    重构4.1            2016-8-16            zhaoqc              新增根绝交易号修改订单状态的方法
 *    V1.1.0             2016-09-23           wusw               修改根据消费码查询相应订单信息的方法为批量
 * 	  V1.1.0            2016-09-26			 luosm               查询商家版APP服务店到店消费订单信息
 * 	  V1.1.0            2016-10-10			 luosm 				 查询服务店铺到店消费当天的交易列表,包括订单支付信息和退款信息
 *    V1.2.0            2016-11-08			 zengjz 			 优化方法
 */
public interface TradeOrderService {

	PageUtils<TradeOrder> selectByParams(Map<String, Object> map, int pageNumber, int pageSize) throws ServiceException;

	PageUtils<TradeOrder> getTradeOrderByParams(Map<String, Object> map, int pageNumber, int pageSize)
			throws ServiceException;

	PageUtils<TradeOrder> getOnlineTradeOrderList(Map<String, Object> map, int pageNumber, int pageSize)
			throws ServiceException;

	/**
	 * pos 订单回款列表
	 * 
	 * @param map
	 *            Map
	 * @param pageNumber
	 *            int
	 * @param pageSize
	 *            int
	 * @return PageUtils
	 * @throws ServiceException
	 */
	PageUtils<TradeOrder> posOrderReceivedList(Map<String, Object> map, int pageNumber, int pageSize)
			throws ServiceException;

	/**
	 * pos 订单回款详情
	 * 
	 * @param map
	 *            Map
	 * @param pageNumber
	 *            int
	 * @param pageSize
	 *            int
	 * @return PageUtils
	 * @throws ServiceException
	 */
	List<TradeOrderItem> orderReceivedDetail(Map<String, Object> map) throws ServiceException;

	/**
	 * 
	 * @desc 查询订单导出的列表
	 *
	 * @param map
	 * @return
	 */
	public List<TradeOrderExportVo> selectExportList(Map<String, Object> map);

	/**
	 * 查询店铺指定状态订单数量
	 * 
	 * @param orderStatus
	 * @param storeId
	 * @return
	 */
	Integer selectOrderNum(OrderStatusEnum orderStatus, String storeId);
	
	/**
	 * 查询指定店铺下各种状态的订单数  目前为提供给ERP接口调用
	 * @param orderStatus 订单状态集合
	 * @param storeId 店铺id
	 * @param refundsStatusList 退款单状态
	 * @return
	 */
	public Integer selectOrderNumByList(List<OrderStatusEnum> orderStatus, String storeId,
									List<RefundsStatusEnum> refundsStatusList);

	TradeOrder selectById(String id) throws ServiceException;

	/**
	 * 
	 * 根据查询条件,查询订单详细信息列表（参数为map类型，用于历史回款记录，注意：支付状态条件为大于等于，分页）
	 * 
	 * @author wusw
	 * @param map
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 * @throws ServiceException
	 */
	List<TradeOrderQueryVo> findShippedOrderByParams(Map<String, Object> params)
			throws ServiceException;

	/**
	 * 
	 * 根据查询条件,查询订单详细信息列表（参数为实体类型，用于历史回款记录，注意：支付状态条件为大于等于，分页）
	 * 
	 * @author wusw
	 * @param tradeOrderQueryVo
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 * @throws ServiceException
	 */
	PageUtils<TradeOrderQueryVo> findShippedOrderByEntity(TradeOrderQueryVo tradeOrderQueryVo, int pageNumber,
			int pageSize) throws ServiceException;

	/**
	 * 
	 * 批量将订单回款
	 * 
	 * @author wusw
	 * @param ids
	 * @throws ServiceException
	 */
	void receivableOrder(String[] ids, String paymentUserId) throws ServiceException;

	/**
	 * 
	 * 根据订单id，查询指定回款状态的记录数量
	 * 
	 * @author wusw
	 * @param ids
	 * @param paymentStatus
	 * @return
	 * @throws ServiceException
	 */
	int selectPaymentStatusCountByIds(String[] ids, PaymentStatusEnum paymentStatus) throws ServiceException;

	/**
	 * 
	 * 根据订单id，获取订单、支付、发票信息
	 * 
	 * @author wusw
	 * @param id
	 * @return
	 * @throws ServiceException
	 */
	TradeOrder findOrderPayInvoiceById(String id) throws ServiceException;

	/**
	 * 
	 * 根据订单id，获取订单详细信息（包括订单基本信息、支付信息、发票信息、店铺基本信息等）
	 * 
	 * @author wusw
	 * @param orderId
	 * @return
	 */
	TradeOrder findOrderDetail(String orderId) throws ServiceException;

	/**
	 * 
	 * 根据订单id，获取订单详细信息（包括订单基本信息、支付信息、发票信息、店铺基本信息等）
	 * 
	 * @author wusw
	 * @param orderId
	 * @return
	 */
	TradeOrder getOnlineOrderDetail(String orderId) throws ServiceException;

	/**
	 * 
	 * 根据订单id，更新是否显示字段
	 * 
	 * @author wusw
	 * @param orderId
	 * @throws ServiceException
	 */
	void updateIsShowById(String orderId) throws ServiceException;

	/**
	 * 
	 * 根据订单id，获取订单支付时间和当前时间的差值
	 * 
	 * @author wusw
	 * @param orderId
	 * @return
	 * @throws ServiceException
	 */
	Map<String, Object> getPayRemainTime(String orderId) throws ServiceException;

	/**
	 * 
	 * 查询微信或支付宝支付的，订单状态处于已取消、已拒收、取消中、拒收中的订单 （主要用于财务系统接口，分页）
	 * 
	 * @author wusw
	 * @param params
	 * @return
	 * @throws ServiceException
	 */
	PageUtils<TradeOrderPayQueryVo> findByStatusPayType(Map<String, Object> params, int pageNumber, int pageSize)
			throws ServiceException;

	/**
	 * 
	 * 查询微信或支付宝支付的，订单状态处于已取消、已拒收、取消中、拒收中的订单 （主要用于财务系统接口，不分页）
	 * 
	 * @author wusw
	 * @param params
	 * @return
	 * @throws ServiceException
	 */
	List<TradeOrderPayQueryVo> findListByStatusPayType(Map<String, Object> params) throws ServiceException;

	/**
	 * 
	 * 查询微信或支付宝支付的，订单状态处于已取消、已拒收、取消中、拒收中的订单 数量（主要用于财务系统接口）
	 * 
	 * @author wusw
	 * @param orderId
	 * @return
	 * @throws ServiceException
	 */
	int selectCountByStatusPayType(Map<String, Object> params) throws ServiceException;

	/**
	 * 
	 * 查询微信或支付宝支付的，订单状态处于取消中、拒收中的订单 数量（主要用于财务系统接口）
	 * 
	 * @author wusw
	 * @return
	 * @throws ServiceException
	 */
	int selectCountForUnRefund() throws ServiceException;

	/**
	 * 
	 * 根据订单id集合，查询订单信息 （主要用于财务系统接口）
	 * 
	 * @author wusw
	 * @param orderIds
	 * @return
	 * @throws ServiceException
	 */
	List<TradeOrderPayQueryVo> findByOrderIdList(List<String> orderIds) throws ServiceException;

	/**
	 * 添加实物、服务订单查询
	 * 
	 * @author zhongy
	 * @param map
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 * @throws ServiceException
	 */
	PageUtils<PhysicsOrderVo> findOrderBackStage(PhysicsOrderVo vo, int pageNumber, int pageSize)
			throws ServiceException;

	/**
	 * 用户订单列表查询 zhongy
	 * 
	 * @param map
	 *            请求参数
	 * @return 返回结果集
	 */
	PageUtils<TradeOrder> findUserTradeOrderList(Map<String, Object> map, int pageNumber, int pageSize)
			throws ServiceException;

	/**
	 * 用户订单详细查询 zhongy
	 * 
	 * @param orderId
	 *            请求参数orderId
	 * @return 返回查询结果
	 */
	JSONObject findUserOrderDetailList(String orderId) throws ServiceException;

	// begin update by wushp
	/**
	 * 
	 * @Description:  用户服务订单列表查询
	 * @param map 参数
	 * @param pageNumber 当前页码
	 * @param pageSize 每页条数
	 * @throws ServiceException serviceException
	 * @return PageUtils<TradeOrder>  page
	 * @author YSCGD
	 * @date 2016年7月16日
	 */
	PageUtils<TradeOrder> findUserServiceOrderList(Map<String, Object> map, int pageNumber, int pageSize)
			throws ServiceException;
	// end update by wushp

	/**
	 * zhongy 服务订单详细
	 * 
	 * @param orderId
	 *            请求参数
	 * @return 返回实体
	 * @throws ServiceException
	 */
	JSONObject findUserServiceOrderDetail(String orderId) throws ServiceException;

	/**
	 * 
	 * @desc 商家APP订单查询
	 *
	 * @param map
	 *            查询条件
	 * @param pageSize
	 *            每页大小
	 * @param pageNumber
	 *            当前页
	 * @return
	 */
	public PageUtils<TradeOrderVo> selectMallAppOrderInfo(Map<String, Object> map, int pageSize, int pageNumber);
	
	//Begin V2.1.0 added by luosm 20170315
	/**
	 * 
	 * @desc 商家APP订单查询
	 *
	 * @param map
	 *            查询条件
	 * @param pageSize
	 *            每页大小
	 * @param pageNumber
	 *            当前页
	 * @return
	 */
	public PageUtils<TradeOrderVo> selectNewMallAppOrderInfo(Map<String, Object> map, int pageSize, int pageNumber);
	//End V2.1.0 added by luosm 20170315
	
	/**
	 * 
	 * @desc 商家版APP获取订单详情信息
	 *
	 * @param orderId
	 *            订单ID
	 * @return
	 */
	public TradeOrderVo getOrderDetail(String orderId);

	/**
	 * 
	 * 商家版APP获取订单状态对应的订单数量
	 *
	 * @param map
	 * @return
	 */
	List<TradeOrderStatusVo> getOrderCount(Map<String, Object> map);

	// start added by luosm 20160924 V1.1.1
	/***
	 * 
	 * @Description: 查询商家版APP服务店到店消费订单信息
	 * @param map
	 * @return
	 * @author luosm
	 * @date 2016年9月24日
	 */
	List<TradeOrderStatusVo> selectArrivedOrderCount(Map<String, Object> map);
	// end added by luosm 20160924 V1.1.1

	/**
	 * 更新订单信息
	 *
	 * @param tradeOrder
	 */
	public void update(TradeOrder tradeOrder) throws Exception;

	/**
	 * @desc 用户下定单
	 *       </p>
	 * @author yangq
	 * @return
	 * @throws ServiceException
	 * @throws MQClientException
	 * @throws UnsupportedEncodingException
	 */
	boolean insertTradeOrder(TradeOrder tradeOrder) throws Exception;

	/**
	 * POS版App下单
	 * </p>
	 * 
	 * @author yangq
	 * @param tradeOrder
	 * @return
	 * @throws Exception
	 */
	void insertPosTradeOrder(Object tradeOrder) throws Exception;

	/**
	 * 确认收货
	 *
	 * @param tradeOrder
	 * @return
	 * @throws MQClientException
	 * @throws ServiceException
	 */
	boolean updateWithConfirm(TradeOrder tradeOrder) throws Exception;

	/**
	 * 用户评价
	 *
	 * @param tradeOrder
	 * @return
	 * @throws MQClientException
	 * @throws ServiceException
	 */
	/*
	 * boolean updateWithUserEvaluate(TradeOrder tradeOrder) throws MQClientException, ServiceException;
	 */

	/**
	 * 用户支付
	 *
	 * @param tradeOrder
	 * @return
	 */
	boolean updateWithApply(TradeOrder tradeOrder) throws Exception;

	/**
	 * @desc 订单状态 from(未发货)---->to(已经货)
	 * @param param
	 * @throws ServiceException
	 * @throws Exception
	 */
	void updateOrderShipment(TradeOrderOperateParamVo param) throws Exception;


	/**
	 * 
	 * @desc 取消充值超时未支付订单
	 * @param tradeOrder
	 * @throws Exception
	 */
	void updateCancelRechargeOrder(TradeOrder tradeOrder) throws Exception;

	/**
	 * 更新订单
	 * 
	 * @param tradeOrder
	 * @return
	 * @throws ServiceException
	 */
	Integer updateOrderStatus(TradeOrder tradeOrder) throws ServiceException;

	/**
	 * 根据条件获取订单数量
	 * 
	 * @param map
	 *            Map
	 * @return Integer
	 */
	Integer getTradeOrderCount(Map<String, Object> map);

	/**
	 * 根据条件获取 订单项集合
	 * 
	 * @param map
	 * @param pageNumber
	 *            int
	 * @param pageSize
	 *            int
	 * @return PageUtils
	 * @throws ServiceException
	 */
	List<TradeOrderItem> getTradeOrderItems(Map<String, Object> map) throws ServiceException;

	List<TradeOrderItem> findTradeOrderItems(Map<String, Object> map) throws ServiceException;

	/**
	 * 微商城App订单查询
	 * </p>
	 * 
	 * @author yangq
	 * @param map
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 * @throws ServiceException
	 */
	public PageUtils<TradeOrderVo> selectWXAppOrderInfo(Map<String, Object> map, int pageSize, int pageNumber)
			throws ServiceException;

	/**
	 * 
	 * @desc 商家版APP获取订单详情信息
	 * @author yangq
	 * @param orderId
	 *            订单ID
	 * @return
	 */
	public TradeOrderVo getWXOrderDetail(String orderId);

	/**
	 * 微商城App订单查询
	 * </p>
	 * 
	 * @author yangq
	 * @param map
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 * @throws ServiceException
	 */
	public PageUtils<TradeOrderVo> selectWXUnpaidOrderInfo(Map<String, Object> map, int pageSize, int pageNumber)
			throws ServiceException;

	/**
	 * 微商城App订单查询
	 * </p>
	 * 
	 * @author yangq
	 * @param map
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 * @throws ServiceException
	 */
	public PageUtils<TradeOrderVo> selectWXDropShippingOrderInfo(Map<String, Object> map, int pageSize, int pageNumber)
			throws ServiceException;

	/**
	 * 微商城App订单查询
	 * </p>
	 * 
	 * @author yangq
	 * @param map
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 * @throws ServiceException
	 */
	public PageUtils<TradeOrderVo> selectWXToBeOrderInfo(Map<String, Object> map, int pageSize, int pageNumber)
			throws ServiceException;

	/**
	 * 统计店铺pos订单收支额
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	BigDecimal findCashCount(String storeId, Date start, Date end);

	//Begin 添加查询条件 update by tangy  2016-10-31
	/**
	 * Pos交班统计
	 *
	 * @param storeId
	 * @param start
	 * @param end
	 */
	Map<String, BigDecimal> findShiftCount(String storeId, Date start, Date end, String userId);
	//End added by tangy
	
	/**
	 * 根据交班获取Pos交班统计
	 * 
	 * @param posShiftExchange
	 *            交班记录
	 * @return
	 */
	PosShiftExchange getPosShiftExchangeCount(PosShiftExchange posShiftExchange);

	/**
	 * 销售统计
	 * 
	 * @param parames
	 *            Map
	 * @return JSONObject
	 */
	@Deprecated
	JSONObject getSaleOrderStatistics(Map<String, Object> parames);

	/**
	 * 销售统计
	 * @desc TODO Add a description 
	 * @author zengj
	 * @param parames
	 * @return
	 */
	JSONObject getSaleOrderStatisticsNew(Map<String, Object> params);

	/**
	 * 查询订单实际金额-用于今日营收的实际收入(商家云钱包的实际入账金额) 注：该退款金额包括活动金额，如果该退款单参与的活动是运营商发布的，该金额=
	 * 订单实付金额+活动优惠金额， 如果该退款单参与的活动是商家发布的。那么该金额=订单实付金额
	 * 
	 * @author zengj
	 * @param params
	 * @return
	 */
	BigDecimal selectOrderAmount(Map<String, Object> params);

	/**
	 * 查询退款负收入金额-用于今日营收的负增长(商家云钱包的实际扣款金额) 注：该退款金额包括活动金额，如果该退款单参与的活动是运营商发布的，该金额=
	 * 订单实付金额+活动优惠金额， 如果该退款单参与的活动是商家发布的。那么该金额=订单实付金额
	 * 
	 * @author zengj
	 * @param params
	 * @return
	 */
	BigDecimal selectRefundAmount(Map<String, Object> params);

	/**
	 * zengj:查询店铺当天的交易列表,包括订单支付信息和退款信息
	 *
	 * @param params
	 *            查询条件
	 * @param pageSize
	 *            每页展示数量
	 * @param pageNumber
	 *            当前页
	 * @return
	 */
	PageUtils<Map<String, Object>> selectOrderIncomeList(Map<String, Object> params, int pageSize, int pageNumber);

	// start added by luosm 20161010 V1.1.0
	/***
	 * 
	 * @Description: 查询服务店铺到店消费当天的收入-消费码已消费
	 * @param params
	 * @return
	 * @author luosm
	 * @date 2016年10月10日
	 */
	BigDecimal selectServiceOrderAmount(Map<String, Object> params);
	
	/***
	 * 
	 * @Description: 查询服务店铺到店消费当天的退单(负收入)
	 * @param params
	 * @return
	 * @author luosm
	 * @date 2016年10月11日
	 */
	BigDecimal selectServiceRefundAmount(Map<String, Object> params);
	
	/***
	 * 
	 * @Description: 查询服务店铺到店消费当天的交易列表,包括订单支付信息和退款信息
	 * @param params
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 * @author luosm
	 * @date 2016年10月10日
	 */
	PageUtils<Map<String, Object>> selectServiceOrderIncomeList(Map<String, Object> params, int pageSize,
			int pageNumber);
	// end added by luosm 20161010 V1.1.0

	/**
	 * zengj:根据参数查询订单信息
	 *
	 * @param params
	 * @return
	 */
	List<TradeOrder> selectByParams(Map<String, Object> params);

	/**
	 * 
	 * zengj:提货码验证记录
	 *
	 * @param params
	 * @return
	 */
	PageUtils<Map<String, Object>> selectPickUpRecord(Map<String, Object> params, int pageSize, int pageNumber);

	/**
	 * zengj:查询提货码订单总额
	 *
	 * @param params
	 * @return
	 */
	BigDecimal selectPickUpTotalAmount(Map<String, Object> params);

	/**
	 * zengj:查询消费码使用记录
	 *
	 * @param params
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	PageUtils<Map<String, Object>> selectConsumeCodeUseRecord(Map<String, Object> params, int pageNumber, int pageSize);

	/**
	 * 
	 * zengj:查询消费码订单总额
	 *
	 * @param params
	 * @return
	 */
	BigDecimal selectConsumeTotalAmount(Map<String, Object> params);

	/**
	 * 
	 * zengj:根据消费码查询订单信息
	 *
	 * @param params
	 * @return
	 */
	Map<String, Object> selectOrderDetailByConsumeCode(Map<String, Object> params);

	/**
	 * 未支付订单倒计时时间查询
	 * </p>
	 * 
	 * @author yangq
	 * @param jsonData
	 * @return
	 * @throws Exception
	 */
	public JSONObject orderCountDown(JSONObject jsonData) throws Exception;

	/**
	 * 
	 * @desc 查询买家实物订单各状态订单数量
	 * @author zengj
	 * @param userId
	 *            用户ID
	 * @return
	 */
	List<TradeOrderStatusVo> selectBuyerPhysicalOrderCount(String userId);

	/**
	 * 
	 * @desc 查询买家服务订单各状态订单数量
	 * @author zengj
	 * @param userId
	 *            用户ID
	 * @return
	 */
	List<TradeOrderStatusVo> selectBuyerServiceOrderCount(String userId);

	/**
	 * DESC: 首页交易订单统计
	 * 
	 * @author LIU.W
	 * @param storeId
	 * @return
	 */
	public List<TradeOrderStatisticsVo> findTradeOrderStatistics(String storeId) throws ServiceException;

	/**
	 * DESC: 右下角弹窗交易订单统计
	 * 
	 * @author LIU.W
	 * @param storeId
	 *            店铺ID
	 * @param type
	 *            类型 1= 订单 2=售后单3=纠纷单
	 * @return
	 */
	public Map<String, Object> findWindowTipOrderCounts(String storeId, String type) throws ServiceException;

	public TradeOrder selectOrderByInfo(Map<String, Object> map) throws Exception;

	/**
	 * 根据订单条件分页查询订单列表
	 * 
	 * @author luosm
	 * @param id
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 * @throws ServiceException
	 */
	public PageUtils<ERPTradeOrderVo> erpSelectByParams(Map<String, Object> params, int pageSize, int pageNum)
			throws ServiceException;

	/**
	 * 根据订单条件查询订单列表
	 * 
	 * @author luosm
	 * @param id
	 * @return
	 * @throws ServiceException
	 */
	public List<ERPTradeOrderVo> erpSelectByParam(Map<String, Object> params) throws ServiceException;

	/***
	 * erp调用根据订单id获取订单详情-实物订单
	 * 
	 * @author luosm
	 * @param orderId
	 * @return TradeOrder
	 * @throws ServiceException
	 */
	public TradeOrder erpSelectByOrderId(String orderId) throws ServiceException;

	/***
	 * erp调用根据订单id获取订单详情-服务订单
	 * 
	 * @author luosm
	 * @param orderId
	 * @return TradeOrder
	 * @throws ServiceException
	 */
	public TradeOrder erpSelectByServiceOrderId(String orderId) throws ServiceException;

	/**
	 * @desc 根据退款交易号查询订单
	 *
	 * @author yangq
	 * @param tradeNum
	 *            订单交易号
	 * @return 订单
	 */
	public TradeOrder getByTradeNum(String tradeNum) throws Exception;

	/**
	 * 根据交易号修改定单状态
	 * 
	 * @author yangq
	 * @param tradeOrder
	 */
	public void updateTradeOrderByTradeNum(TradeOrder tradeOrder) throws Exception;

	/**
	 * 
	 * @Description: 根据交易号修改充值订单状态
	 * @param tradeOrder 
	 * @throws Exception
	 * @author zhaoqc
	 * @date 2016年8月16日
	 */
	public void updateRechargeOrderByTradeNum(TradeOrder tradeOrder) throws Exception;

	/**
	 * 根据用户ID查询团购店是否有购买商品
	 * 
	 * @author yangq
	 * @param map
	 * @return
	 */
	int selectTradeOrderInfo(Map<String, Object> map) throws Exception;

	/**
	 * 根据交易号查询订单状态是否已发货
	 * 
	 * @author yangq
	 * @param tradeNum
	 * @return
	 */
	int selectOrderStatusByTradeNum(String tradeNum) throws Exception;

	/**
	 * pos发货
	 * 
	 * @param tradeOrder
	 *            TradeOrder
	 */
	public void updateOrderDelivery(TradeOrder tradeOrder) throws Exception;

	/**
	 * 修改代金券领取记录状态
	 * </p>
	 * 
	 * @author yangq
	 * @param map
	 * @throws Exception
	 */
	void updateActivityCouponsStatus(Map<String, Object> map) throws Exception;

	void updateMyOrderStatus(TradeOrder tradeOrder) throws Exception;

	/**
	 * 更新订单为已完成
	 */
	void updateWithComplete(TradeOrder order) throws Exception;

	/**
	 * 根据交易号修改定单状态
	 * 
	 * @author yangq
	 * @param tradeOrder
	 */
	void updateTradeOrderByTradeNumIsOder(TradeOrder tradeOrder) throws Exception;

	/**
	 * 
	 * POS订单列表（商家中心）
	 * 
	 * @author wusw
	 * @param map
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 * @throws ServiceException
	 */
	PageUtils<TradeOrder> selectPosOrderListByParams(Map<String, Object> map, int pageNumber, int pageSize)
			throws ServiceException;

	/**
	 * 更新订单信息
	 * @param tradeOrder
	 */
	public void updateByPrimaryKeySelective(TradeOrder tradeOrder);

	/**
	 * 查询导出POS销售单列表
	 * @desc TODO Add a description 
	 * @author zengj
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> selectPosOrderExportList(Map<String, Object> params);

	// Begin 重构4.1 add by wusw 20160719
	/**
	 * 
	 * @Description: 据查询条件，查询订单列表（用于财务系统，包含服务店订单，分页）
	 * @param params 查询条件
	 * @param pageSize 每页数量
	 * @param pageNumber 页码
	 * @throws ServiceException service异常
	 * @return PageUtils<ERPTradeOrderVo> 订单列表（分页）
	 * @author wusw
	 * @date 2016年7月19日
	 */
	PageUtils<ERPTradeOrderVo> findOrderForFinanceByParams(Map<String, Object> params, int pageNumber, int pageSize)
			throws ServiceException;

	/**
	 * 
	 * @Description: 据查询条件，查询订单列表（用于财务系统，包含服务店订单，不分页）
	 * @param params
	 * @throws ServiceException   
	 * @return List<ERPTradeOrderVo>  
	 * @author wusw
	 * @date 2016年7月23日
	 */
	List<ERPTradeOrderVo> findOrderListForFinanceByParams(Map<String, Object> params) throws ServiceException;
	// End 重构4.1 add by wusw 20160719

	// Begin 重构4.1 add by zhaoqc 20160730
	/**
	 * 
	 * @Description: 根据订单状态查询订单列表
	 * @param status 订单状态
	 * @throws ServiceException   
	 * @return Map<String, List<TradeOrder>>  
	 * @author zhaoqc
	 * @date 2016年7月30日
	 */
	Map<String, List<TradeOrder>> findRechargeOrdersByStatus(OrderStatusEnum status) throws ServiceException;
	// End 重构4.1 add by zhaoqc 20160730

	// Begin v1.1.0 add by zengjz 20160912
	/**
	 * @Description: 按条件统计订单交易量与金额
	 * @param params 查询参数
	 * @return Map<String,Object>  返回结果
	 * @author zengjizu
	 * @date 2016年9月12日
	 */
	Map<String, Object> statisOrderForFinanceByParams(Map<String, Object> params);

	/**
	 * @Description: 按条件统计取消订单退款交易量与金额
	 * @param params 查询条件
	 * @return Map<String,Object>  统计结果
	 * @throws
	 * @author zengjizu
	 * @date 2016年9月17日
	 */
	Map<String, Object> statisOrderCannelRefundByParams(Map<String, Object> params);
	// End v1.1.0 add by zengjz 20160912
	
	// Begin 重构4.1 add by zhaoqc 20160722
	/**
	 * @Description: 添加充值订单状态更改
	 * @param tradeOrder 订单
	 * @param sporderId 第三方平台订单号
	 * @throws ServiceException   
	 * @return 
	 * @author zhaoqc
	 * @date 2016年7月22日
	 */
	void updataRechargeOrderStatus(TradeOrder tradeOrder, String sporderId) throws ServiceException;
	// End 重构4.1 add by zhaoqc 20160722
	
	/**
     * 创建订单支付方式
     * @param tradeOrder
     * @param result
     * @throws Exception
     */
    public void dealWithStoreConsumeOrder(TradeOrder tradeOrder) throws Exception;
    
    /**
	 * @Description: tuzhd根据用户id查询其支付完成的订单总量 用于首单条件判断
	 * @param userId 用户id
	 * @return int 返回统计值
	 * @author tuzhd
	 * @date 2016年12月31日
	 */
	public int selectCountByUserStatus(String userId);
	/**
	 * @Description: 校验用户使用新人专享代金券时 是否符合新用户及未使用该类型代金券的条件
	 * @param userId
	 * @return boolean  不符合新用户专享条件返回false，否则为true
	 * @author tuzhd
	 * @date 2016年12月31日
	 */
	public boolean checkUserUseCoupons(String userId);
	
	// Begin V2.1 added by maojj 2017-02-18
	/**
	 * @Description: 查询用户订单列表
	 * @param paramBo
	 * @return   
	 * @author maojj
	 * @date 2017年2月18日
	 */
	PageUtils<TradeOrder> findUserOrders(UserOrderParamBo paramBo);
	// End V2.1 added by maojj 2017-02-18
	
	//Begin V2.1.0 added by luosm 20170220
	/**
	 * @Description: 根据订单获取订单的优惠信息
	 * @param orderIds 订单id集合
	 * @return List
	 * @author zhulq
	 * @date 2017年2月18日
	 */
	List<ActivityInfoVO> findActivityInfo(List<String> orderIds);
	//End V2.1.0 added by luosm 20170220

	// begin V2.1 add by chenzc 2017-2-28
	/**
	 * 
	 * @Description: 统计用户订单数量
	 * @return PageUtils<TradeOrder>  
	 * @author chenzc
	 * @date 2017年2月28日
	 */
	long countUserOrders(UserOrderParamBo paramBo);
	// end V2.1 add by chenzc 2017-2-28
}