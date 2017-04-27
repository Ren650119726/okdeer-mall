
package com.okdeer.mall.order.mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.archive.system.pos.entity.PosShiftExchange;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.order.bo.TradeOrderDetailBo;
import com.okdeer.mall.order.bo.UserOrderParamBo;
import com.okdeer.mall.order.dto.TradeOrderQueryParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderRechargeVo;
import com.okdeer.mall.order.enums.ConsumerCodeStatusEnum;
import com.okdeer.mall.order.enums.OrderIsShowEnum;
import com.okdeer.mall.order.enums.PaymentStatusEnum;
import com.okdeer.mall.order.vo.ActivityInfoVO;
import com.okdeer.mall.order.vo.ERPTradeOrderVo;
import com.okdeer.mall.order.vo.PhysicsOrderVo;
import com.okdeer.mall.order.vo.TradeOrderPayQueryVo;
import com.okdeer.mall.order.vo.TradeOrderQueryVo;
import com.okdeer.mall.order.vo.TradeOrderStatisticsVo;
import com.okdeer.mall.order.vo.TradeOrderStatusVo;
import com.okdeer.mall.order.vo.TradeOrderVo;
import com.okdeer.mall.order.vo.UserTradeOrderDetailVo;
import com.okdeer.mall.order.vo.UserTradeServiceOrderVo;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *    重构4.1            2016-7-13            wusw                添加服务店订单列表查询
 *    重构4.1            2016-7-15            wusw                添加服务店订单详情查询
 *    重构4.1            2016-7-16            zhulq               充值订单
 *    重构4.1            2016-7-17            wusw                添加服务店订单列表查询（商城后台）
 *    重构4.1            2016-7-19            wusw                添加财务系统的订单接口（包含服务店订单情况）
 *    重构4.1            2016-7-29            wusw                添加服务店订单详情查询（商城后台）
 *    重构4.1            2016-7-30            zhaoqc              添加根据订单状态查询订单列表
 *    v1.1.0            2016-9-17            zengjz              添加财务系统统计接口
 *    V1.1.0			2016-9-23 			 zengjz				 增加查询到店消费订单列表
 *    V1.1.0            2016-09-23           wusw                添加消费码验证（到店消费）相应方法
 *    V1.1.0            2016-09-26          luosm               查询商家版APP服务店到店消费订单信息
 *    V1.1.0			2016-10-10          luosm				服务店到店消费订单金额统计及订单列表
 *    V1.1.0			2016-10-11          luosm				服务店到店消费订单列表查询
 *    友门鹿V2.1			2017-02-18			maojj				全部订单查询优化
 *    V2.1.0            2017-02-24        wusw              修改实物订单导出
 */
public interface TradeOrderMapper {

	// begin V2.3.0 add by wangf01 20170419
	/**
	 * 根据条件查询符合的数据
	 * @param param TradeOrder
	 * @return
	 */
	List<TradeOrder> selectByParam(TradeOrder param);
	// end add by wangf01 20170419

	void insertSelective(TradeOrder tradeOrder);

	List<TradeOrderItem> selectTraderOrderList(Map<String, Object> map);

	List<TradeOrderItem> findTraderOrderList(Map<String, Object> map);

	List<TradeOrder> selectOrderByPayType(Map<String, Object> map);

	/**
	 * 查询订单列表信息
	 * @author zengj
	 * @param map
	 * @return
	 */
	List<TradeOrder> selectOrderList(Map<String, Object> map);

	/**
	 * 用户订单列表查询 zhongy
	 * @param params 请求参数
	 * @return 返回结果集
	 */
	List<TradeOrder> selectUserOrderList(@Param("params") Map<String, Object> params);

	/**
	 * 用户未评价订单列表查询
	 * @param params 请求参数
	 * @return 返回结果
	 */
	List<TradeOrder> selectUserIsCommentOrderList(@Param("params") Map<String, Object> params);

	// begin add by wushp
	/**
	 * 
	 * @Description: 用户未评价服务店订单列表查询
	 * @param params 查询参数
	 * @return list
	 * @author wushp
	 * @date 2016年7月16日
	 */
	List<TradeOrder> findNotCommentServiceOrderList(@Param("params") Map<String, Object> params);
	// end add by wushp

	/**
	 * 用户订单详细查询 zhongy
	 * @param orderId 请求参数orderId
	 * @return 返回查询结果
	 */
	UserTradeOrderDetailVo selectUserOrderDetail(String orderId);

	/**
	 * 服务订单详细
	 * @param orderId 请求参数
	 * @return 返回实体
	 */
	UserTradeOrderDetailVo selectUserOrderServiceDetail(String orderId);

	/**
	 * 用户服务订单列表查询
	 * 条件：全部、未付款
	 * @param params 请求参数
	 * @return 返回结果
	 */
	List<UserTradeServiceOrderVo> selectUserServiceOrderList(@Param("params") Map<String, Object> params);

	/**
	 * 用户服务订单列表查询
	 * 查询条件，未消费、已消费、已过期 
	 * @param params 请求参数
	 * @return 返回结果
	 */
	List<UserTradeServiceOrderVo> selectUserServiceOrderAlreadyPayList(@Param("params") Map<String, Object> params);

	/**
	 *运营后台实物、服务订单列表查询
	 *
	 * @param map 请求参数
	 * @return 返回结果集
	 */
	List<PhysicsOrderVo> selectOrderBackStage(PhysicsOrderVo vo);

	/**
	 * @desc 查询订单导出列表
	 *
	 * @param map
	 * @return
	 */
	List<TradeOrder> selectExportOrder(Map<String, Object> map);

	Integer selectOrderNum(Map<String, Object> map);

	TradeOrder selectByPrimaryKey(String id);
	
	/**
	 * 查询指定店铺下各种状态的订单数  目前为提供给ERP接口调用-->
	 * @param map tuzhd 
	 * @return
	 */
	Integer selectOrderNumByStatus(Map<String, Object> map);
	/**
	 * 
	 * 根据查询条件,查询订单详细信息列表（参数为map类型，用于历史回款记录，注意：支付状态条件为大于等于）
	 *
	 * @author wusw
	 * @param params
	 * @return
	 */
	List<TradeOrderQueryVo> selectShippedOrderByParams(@Param("params") Map<String, Object> params);

	/**
	 * 
	 * 根据查询条件,查询订单详细信息列表（参数为实体类型，用于历史回款记录，注意：支付状态条件为大于等于）
	 * 
	 * @author wusw
	 * @param tradeOrderQueryVo
	 * @return
	 */
	List<TradeOrderQueryVo> selectShippedOrderByEntity(TradeOrderQueryVo tradeOrderQueryVo);

	/**
	 * 
	 * 根据订单id，查询订单项信息类别（用户历史回款记录）
	 * 
	 * @author wusw
	 * @param tradeOrderItem
	 * @return
	 */
	List<TradeOrderItem> selectShippedOrderItemByEntity(@Param("params") Map<String, Object> params);

	/**
	 * 
	 * 批量修改订单的回款状态 
	 * 
	 * @author wusw
	 * @param ids
	 * @param paymentStatus
	 * @param paymentTime
	 */
	void updatePaymentStatusByIds(@Param("ids") String[] ids, @Param("paymentStatus") PaymentStatusEnum paymentStatus,
			@Param("paymentTime") Date paymentTime, @Param("paymentUserId") String paymentUserId);

	/**
	 * 
	 * 根据订单id，查询指定回款状态的记录数量
	 * 
	 * @author wusw
	 * @param ids
	 * @param paymentStatus
	 * @return
	 */
	int selectPaymentStatusCountByIds(@Param("ids") String[] ids,
			@Param("paymentStatus") PaymentStatusEnum paymentStatus);

	/**
	 * 
	 * 根据订单id，获取订单、支付、发票信息  
	 * 
	 * @author wusw
	 * @param id
	 * @return
	 */
	TradeOrder selectOrderPayInvoiceById(@Param("id") String id);

	/**
	 * 
	 * 根据订单id，更新是否显示字段
	 * 
	 * @author wusw
	 * @param isShow
	 * @param updateTime
	 * @param id
	 */
	void updateIsShowById(@Param("isShow") OrderIsShowEnum isShow, @Param("updateTime") Date updateTime,
			@Param("id") String id);

	/**
	 * 
	 * 查询微信或支付宝支付的，订单状态处于已取消、已拒收、取消中、拒收中的订单 （主要用于财务系统接口）
	 * 
	 * @author wusw
	 * @param params
	 * @return
	 */
	List<TradeOrderPayQueryVo> selectByStatusPayType(@Param("params") Map<String, Object> params);

	/**
	 * 
	 * 查询微信或支付宝支付的，订单状态处于已取消、已拒收、取消中、拒收中的订单数量 （主要用于财务系统接口）
	 * 
	 * @author wusw
	 * @param orderId
	 * @return
	 */
	int selectCountByStatusPayType(@Param("params") Map<String, Object> params);

	/**
	 * 
	 * 根据订单id集合，查询订单信息 （主要用于财务系统接口）
	 * 
	 * @author wusw
	 * @param orderIds
	 * @return
	 */
	List<TradeOrderPayQueryVo> selectByOrderIdList(@Param("orderIds") List<String> orderIds);

	/**
	 * 
	 * 根据订单id，查询订单支付剩余时间
	 * 
	 * @author wusw
	 * @param orderId
	 * @return
	 */
	UserTradeOrderDetailVo selectRemainTimeById(String orderId);

	/**
	 * 查询商家版APP订单信息
	 * @desc TODO Add a description 
	 *
	 * @param map
	 * @return
	 */
	List<TradeOrderVo> selectOrderInfo(Map<String, Object> map);
	
	//begin added by luosm V1.1.0 20161011
	/***
	 * 
	 * @Description: 商家版APP服务店到店消费订单查询 
	 * @param map
	 * @return
	 * @author luosm
	 * @date 2016年10月11日
	 */
	List<TradeOrderVo> selectServiceOrderInfo(Map<String, Object> map);
	//end added by luosm V1.1.0 20161011

	// begin added by luosm V2.1.0 20170315
	/***
	 * 
	 * @Description: 商家版APP服务店到店消费订单查询 
	 * @param map
	 * @return
	 * @author luosm
	 * @date 2016年10月11日
	 */
	List<TradeOrderVo> selectNewServiceOrderInfo(Map<String, Object> map);
	
	/**
	 * 查询商家版APPV2.1.0订单信息
	 * @desc TODO Add a description 
	 *
	 * @param map
	 * @return
	 */
	List<TradeOrderVo> selectNewOrderInfo(Map<String, Object> map);
	// end added by luosm V2.1.0 20170315

	/**
	 * 
	 * @desc 商家版APP获取订单详情信息
	 *
	 * @param orderId 订单ID
	 * @return
	 */
	TradeOrderVo getOrderDetail(@Param("orderId") String orderId);

	/**
	 * 
	 * 商家版APP获取订单状态对应的订单数量 
	 *
	 * @param map
	 * @return
	 */
	List<TradeOrderStatusVo> getOrderCount(Map<String, Object> map);
	
	//start added by luosm 20160924 V1.1.1
	/***
	 * 
	 * @Description: 查询商家版APP服务店到店消费订单信息
	 * @param map
	 * @return
	 * @author luosm
	 * @date 2016年9月24日
	 */
	List<TradeOrderStatusVo> selectArrivedOrderCount(Map<String, Object> map);
	//end added by luosm 20160924 V1.1.1


	/**
	 * 更新订单信息
	 *
	 * @param tradeOrder
	 */
	void updateByPrimaryKeySelective(TradeOrder tradeOrder);

	/**
	 * 根据订单ID查询订单支付信息 </p>
	 * 
	 * @param orderId
	 * @return
	 */
	TradeOrder findOrderPayWay(String orderId);

	/**
	 * 修改订单状态
	 * from(等待买家付款)----->to(待发货)
	 * @author yangq
	 * @param tradeOrder
	 */
	Integer updateOrderStatus(TradeOrder tradeOrder);

	/**
	 * 根据参数获取订单列表
	 * @param map Map
	 * @return List
	 */
	List<TradeOrder> getTradeOrderByParams(Map<String, Object> map);

	/**
	 * 根据条件获取订单数量
	 * @param map Map
	 * @return Integer
	 */
	Integer getTradeOrderCount(Map<String, Object> map);

	/**
	 * 查询微信版APP订单信息
	 * @desc TODO Add a description 
	 * @author yangq
	 * @param map
	 * @return
	 */
	List<TradeOrderVo> selectOrderInfoByUserId(Map<String, Object> map);

	/**
	 * 
	 * 微信版APP获取订单状态对应的订单数量 
	 *
	 * @param map
	 * @return
	 */
	List<TradeOrderStatusVo> getWXOrderCount(Map<String, Object> map);

	/**
	 * 微信版App订单代付款状态查询
	 * @author yangq
	 * @param map
	 * @return
	 */
	List<TradeOrderVo> selectUnpaidOrderInfoByUserId(Map<String, Object> map);

	/**
	 * 微信版App订单代发货状态查询
	 * @author yangq
	 * @param map
	 * @return
	 */
	List<TradeOrderVo> selectDropShippingOrderInfoByUserId(Map<String, Object> map);

	/**
	 * 微信版App订单已发货(待收货)状态查询
	 * @author yangq
	 * @param map
	 * @return
	 */
	List<TradeOrderVo> selectToBeOrderInfoByUserId(Map<String, Object> map);

	/**
	 * 删除退货/退款单
	 * @author yangq
	 * @param id
	 */
	void deleteRefundOrder(String id);

	/**
	 * pos 订单回款列表
	 * @param map Map
	 * @param pageNumber int
	 * @param pageSize int
	 * @return PageUtils
	 * @throws ServiceException
	 */
	List<TradeOrder> posOrderReceivedList(Map<String, Object> map) throws ServiceException;

	/**
	 * pos 订单回款详情
	 * @param map Map
	 * @throws ServiceException
	 */
	List<TradeOrderItem> orderReceivedDetail(Map<String, Object> map) throws ServiceException;

	/**
	 * 查询pos订单现金总额统计
	 */
	BigDecimal findPosCashCount(Map<String, Object> map);

	/**
	 * 查询在线支付现金总额统计
	 */
	BigDecimal findOnlineCashCount(Map<String, Object> map);

	/**
	 * 线上订单总额统计
	 */
	BigDecimal findOnlineSum(Map<String, Object> map);

	/**
	 * 配送费总额 统计
	 */
	BigDecimal findFareSum(Map<String, Object> map);

	/**
	 * pos销售总额统计
	 */
	BigDecimal findPosSum(Map<String, Object> map);

	// Begin 添加查询条件 update by tangy 2016-10-31
	/**
	 * 根据店铺id、时间段查询订单统计数据
	 * @param storeId      店铺id
	 * @param startTime    登陆时间
	 * @param endTime     结束时间
	 * @return  交班统计
	 */
	PosShiftExchange findPosShiftExchangeByStoreId(@Param("storeId") String storeId, @Param("startTime") Date startTime,
			@Param("endTime") Date endTime, @Param("userId") String userId);
	// End added by tangy

	////////////////////////////// 销售统计
	////////////////////////////// start///////////////////////////////////////////////
	/**
	 * 根据条件获取订单数量
	 * @param map Map
	 * @return Integer
	 */
	Integer getOrderCountByParames(Map<String, Object> map);

	/**
	 * 根据条件获取订单金额
	 * @param map Map
	 * @return BigDecimal
	 */
	BigDecimal getOrderAmountByParames(Map<String, Object> map);

	/**
	 * 根据条件获取退款金额
	 * @param map Map
	 * @return BigDecimal
	 */
	BigDecimal getRefundAmountByParames(Map<String, Object> map);

	/**
	 * 根据条件获取商家优惠金额
	 * @param map Map
	 * @return BigDecimal
	 */
	BigDecimal getStoreDiscountByParames(Map<String, Object> map);

	/**
	 * 根据条件获取平台优惠金额
	 * @param map Map
	 * @return BigDecimal
	 */
	BigDecimal getPlatformDiscountByParames(Map<String, Object> map);

	/**
	 * 根据条件获取代金劵金额
	 * @param map Map
	 * @return BigDecimal
	 */
	BigDecimal getCouponAmountByParames(Map<String, Object> map);

	/**
	 * 根据条件获取配送费
	 * @param map Map
	 * @return BigDecimal
	 */
	BigDecimal getFareAmountByParames(Map<String, Object> map);

	/**
	 * 根据条件获取实收金额
	 * @param map Map
	 * @return BigDecimal
	 */
	BigDecimal getActualAmoutByParames(Map<String, Object> map);

	/**
	 * 根据条件获取支付金额
	 * @param map Map
	 * @return BigDecimal
	 */
	BigDecimal getPayAmoutByParames(Map<String, Object> map);

	/**
	 * 根据条件获取货到付款金额 
	 * @param map Map
	 * @return BigDecimal
	 */
	BigDecimal getDeliveryCashPayAmoutByParames(Map<String, Object> map);

	////////////////////////////// 销售统计
	////////////////////////////// end///////////////////////////////////////////////

	/**
	 * zengj:查询店铺当天的收入
	 *
	 * @param params
	 * @return
	 */
	BigDecimal selectOrderAmount(Map<String, Object> params);
	
	/**
	 * zengj:查询店铺当天的支出(退款) 
	 *
	 * @param params
	 * @return
	 */
	BigDecimal selectRefundAmount(Map<String, Object> params);

	// start added by luosm 20161011 V1.1.0
	/**
	 * 
	 * @Description: 查询服务店铺到店消费当天的收入
	 * @param params
	 * @return
	 * @author luosm
	 * @date 2016年10月10日
	 */
	BigDecimal selectServiceOrderAmount(Map<String, Object> params);

	/**
	 * 
	 * @Description: 查询服务店铺到店消费当天的退单(负收入)
	 * @param params
	 * @return
	 * @author luosm
	 * @date 2016年10月10日
	 */
	BigDecimal selectServiceRefundAmount(Map<String, Object> params);

	/***
	 * 
	 * @Description: 查询商家版APP服务店到店消费订单信息
	 * @param params
	 * @return
	 * @author luosm
	 * @date 2016年10月10日
	 */
	List<Map<String, Object>> selectServiceOrderIncomeList(Map<String, Object> params);
	// end added by luosm 20161011 V1.1.0

	/**
	 * zengj:查询店铺当天的交易列表,包括订单支付信息和退款信息 
	 *
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> selectOrderIncomeList(Map<String, Object> params);

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
	List<Map<String, Object>> selectPickUpRecord(Map<String, Object> params);

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
	 * @return
	 */
	List<Map<String, Object>> selectConsumeCodeUseRecord(Map<String, Object> params);

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
	 * 
	 * @desc 查询买家实物订单各状态订单数量
	 * @author zengj
	 * @param userId 用户ID
	 * @return
	 */
	List<TradeOrderStatusVo> selectBuyerPhysicalOrderCount(String userId);

	// begin add by wushp 20160823
	/**
	 * 
	 * @desc 微信查询买家实物订单各状态订单数量
	 * @author wushp
	 * @param userId 用户ID
	 * @return
	 */
	List<TradeOrderStatusVo> selectWxBuyerPhysicalOrderCount(String userId);
	// end add by wushp 20160823

	/**
	 * 
	 * @desc 查询买家服务订单各状态订单数量
	 * @author zengj
	 * @param userId 用户ID
	 * @return
	 */
	List<TradeOrderStatusVo> selectBuyerServiceOrderCount(String userId);

	/**
	 * DESC: 首页交易订单统计
	 * @author LIU.W
	 * @param storeId
	 * @return
	 */
	public List<TradeOrderStatisticsVo> selectTradeOrderStatistics(String storeId);

	/**
	 * DESC: 右下角弹窗交易订单统计
	 * @author LIU.W
	 * @param storeId 店铺ID
	 * @param type 类型 1= 订单 2=售后单3=纠纷单
	 * @return
	 */
	public Map<String, Object> selectWindowTipOrderCounts(@Param("storeId") String storeId, @Param("type") String type);

	/**
	 * 查询用户对应的订单信息 </p>
	 * 
	 * @author yangq
	 * @param map
	 * @return
	 */
	TradeOrder selectOrderByInfo(Map<String, Object> map);

	/**
	 * 
	 * @desc erp根据条件查询订单列表
	 * @author luosm
	 * @param id 订单id
	 * @return
	 */
	List<ERPTradeOrderVo> ERPSelectByParams(Map<String, Object> params);

	/**
	 * 财务系统接口
	 * 根据订单id查询订单详情
	 * @author luosm
	 * @param id
	 * @return TradeOrder
	 */
	TradeOrder selectByOrderId(String id);

	/**
	 * 查询订单交易号 </p>
	 * 
	 * @author yangq
	 * @param map
	 * @return
	 */
	TradeOrder selectByParamsTrade(String tradeNum);

	/**
	 * 根据交易号修改定单状态
	 * 
	 * @author yangq
	 * @param tradeOrder
	 */
	void updateTradeOrderByTradeNum(TradeOrder tradeOrder);

	/**
	 * 根据交易号修改定单状态
	 * 
	 * @author yangq
	 * @param tradeOrder
	 */
	void updateTradeOrderByTradeNumIsOder(TradeOrder tradeOrder);

	/**
	 * 根据用户ID查询团购店是否有购买商品
	 * 
	 * @author yangq
	 * @param map
	 * @return
	 */
	int selectTradeOrderInfo(Map<String, Object> map);

	/**
	 * 根据交易号查询订单状态是否已发货
	 * 
	 * @author yangq
	 * @param tradeNum
	 * @return
	 */
	int selectOrderStatusByTradeNum(String tradeNum);

	TradeOrder selectTradeDetailInfoById(String orderId);

	/**
	 * 查询特惠活动购买款数 </p>
	 * 
	 * @author yangq
	 * @param map
	 * @return
	 */
	int selectTradeOrderInfoCount(Map<String, Object> map);

	/**
	 * 获取退款单的平台优惠金额
	 * @param map
	 * @return BigDecimal
	 */
	BigDecimal getRefundPlatformDiscountByParames(Map<String, Object> map);

	/**
	 * 获取退款单的平台代金劵金额
	 */
	BigDecimal getRefundPlatformCouponByParames(Map<String, Object> map);

	/**
	 * 统计订单金额-实收 -POS销售统计
	 * @author zengj
	 * @param params
	 * @return
	 */
	Map<String, Object> selectOrderIncome(Map<String, Object> params);

	/**
	 * 统计退款金额 -实退-POS销售统计
	 * @author zengj
	 * @param params
	 * @return
	 */
	Map<String, Object> selectRefundIncome(Map<String, Object> params);

	/**
	 * 统计优惠金额 -POS销售统计
	 * @author zengj
	 * @param params
	 * @return
	 */
	Map<String, Object> selectPreferentialAmount(Map<String, Object> params);

	/**
	 * 统计代金券金额 -POS销售统计
	 * @author zengj
	 * @param params
	 * @return
	 */
	Map<String, Object> selectCouponAmount(Map<String, Object> params);

	/**
	 * 统计配送费金额 -POS销售统计
	 * @author zengj
	 * @param params
	 * @return
	 */
	Map<String, Object> selectFareAmount(Map<String, Object> params);

	/**
	 * 统计订单各支付方式金额
	 * @desc TODO Add a description 
	 * @author tzd
	 * @param params
	 * @return
	 */
	Map<String, Object> selectOrderIncomeByPayType(Map<String, Object> params);

	/**
	 * 统计退款各支付方式金额
	 * @desc TODO Add a description 
	 * @author tzd
	 * @param params
	 * @return
	 */
	Map<String, Object> selectRefundIncomeByPayType(Map<String, Object> params);

	/**
	 * 统计线上线下订单数量
	 * @desc TODO Add a description 
	 * @author tzd
	 * @param params
	 * @return
	 */
	Map<String, Object> selectOrderCount(Map<String, Object> params);

	/**
	 * 
	 * pos订单列表查询（商家中心） 
	 *
	 * @author wusw
	 * @param map
	 * @return
	 */
	List<TradeOrder> selectPosOrderListForSeller(Map<String, Object> map);

	/**
	 * 
	 * pos订单项查询（商家中心）
	 * 
	 * @author wusw
	 * @param params
	 * @return
	 */
	List<TradeOrder> selectOrderItemListForSeller(@Param("params") Map<String, Object> params);

	/**
	 * 查询导出POS销售单列表
	 * @desc TODO Add a description 
	 * @author zengj
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> selectPosOrderExportList(Map<String, Object> params);

	/**
	* 
	* 实物订单列表查询 （商家中心）
	*
	* @author wusw
	* @param map
	* @return
	*/
	// Begin V2.1 add by wusw 20170224
	List<TradeOrderVo> selectRealOrderList(Map<String, Object> map);
	// End V2.1 add by wusw 20170224

	/**
	 * 
	 * 实物订单列表订单项查询 （商家中心）
	 * 
	 * @author wusw
	 * @param params
	 * @return
	 */
	List<TradeOrder> selectRealOrderItemList(@Param("params") Map<String, Object> params);

	// Begin 重构4.1 add by wusw
	/**
	 *   
	 * @Description: 查询服务店订单列表
	 * @param map
	 * @return   
	 * @return List<TradeOrder>  
	 * @throws
	 * @author wusw
	 * @date 2016年7月13日
	 */
	List<TradeOrder> selectServiceStoreOrderList(Map<String, Object> map);
	// End 重构4.1 add by wusw

	// Begin 重构4.1 add by wusw
	/**
	 * 
	 * @Description: 服务店订单详情
	 * @param orderId 
	 * @return TradeOrderVo  
	 * @author wusw
	 * @date 2016年7月14日
	 */
	TradeOrderVo selectServiceStoreOrderDetail(String orderId);
	// End 重构4.1 add by wusw

	// Begin 重构4.1 add by wusw
	/** 
	 * @Description: 根据查询条件，获取服务店订单列表（商城后台）
	 * @param map
	 * @return   
	 * @return PhysicsOrderVo  
	 * @throws
	 * @author wusw
	 * @date 2016年7月17日
	 */
	List<PhysicsOrderVo> selectServiceStoreListForOperate(Map<String, Object> map);
	// End 重构4.1 add by wusw

	// Begin 重构4.1 add by zhulq 2016-7-16
	/**
	 * @Description: 充值订单
	 * @param vo TradeOrderRechargeVo
	 * @return   List 
	 * @author zhulq
	 * @date 2016年7月16日
	 */
	List<TradeOrderRechargeVo> selectRechargeOrder(TradeOrderRechargeVo vo);
	// End 重构4.1 add by zhulq 2016-7-16

	// Begin 重构4.1 导出数据 add by zhulq 2016-7-18
	/**
	 * @Description: 导出充值订单信息学
	 * @param map   参数map
	 * @return   集合
	 * @author zhulq
	 * @date 2016年7月18日
	 */
	// List<TradeOrderRechargeVo> selectRechargeOrderExport(Map<String, Object> map);
	List<TradeOrderRechargeVo> findRechargeOrderExport(TradeOrderRechargeVo vo);
	// End 重构4.1 add by zhulq 2016-7-18

	// Begin 重构4.1 订单详情 add by zhulq 2016-7-18
	/**
	 * @Description: 充值订单详情页面
	 * @param id   订单id
	 * @return    TradeOrderRechargeVo
	 * @author zhulq
	 * @date 2016年7月18日
	 */
	List<TradeOrderRechargeVo> selectRechargeOrderDetail(TradeOrderRechargeVo vo);
	// End 重构4.1 订单详情 add by zhulq 2016-7-18

	// Begin 重构4.1 add by wusw 20160719
	/**
	 * 
	 * @Description: 据查询条件，查询订单列表（用于财务系统，包含服务店订单）
	 * @param tradeOrderQueryParamDto 查询参数
	 * @return List<ERPTradeOrderVo> 
	 * @author wusw
	 * @date 2016年7月19日
	 */
	List<ERPTradeOrderVo> findOrderForFinanceByParams(TradeOrderQueryParamDto tradeOrderQueryParamDto);
	
	/**
	 * @Description: 据查询数量，查询订单列表（用于财务系统，包含服务店订单）
	 * @param tradeOrderQueryParamDto 查询参数
	 * @return 数量
	 * @author zengjizu
	 * @date 2017年4月26日
	 */
	int countOrderForFinanceByParams(TradeOrderQueryParamDto tradeOrderQueryParamDto);
	/**
	 * 
	 * @Description: 服务店订单详情（商城后台）
	 * @param orderId 订单id
	 * @return 订单、发票、物流信息
	 * @author wusw
	 * @date 2016年7月29日
	 */
	TradeOrderVo selectServiceStoreDetailForOperate(String orderId);
	// End 重构4.1 add by wusw 20160729

	// Begin 重构4.1 add by zhaoqc 20160730
	/**
	 * 
	 * @Description: 服务店订单详情（商城后台）
	 * @param orderId 订单id
	 * @return 订单、发票、物流信息
	 * @author wusw
	 * @date 2016年7月29日
	 */
	List<TradeOrder> findRechargeOrdersByStatus(int status);
	// End 重构4.1 add by zhaoqc 20160730

	// Begin sql优化，将复杂sql拆分开来 add by zengj
	/**
	 * 
	 * @Description: 运营后台实物订单列表查询，优化sql后
	 * @param vo 请求参数
	 * @return   返回订单列表
	 * @author zengj
	 * @date 2016年8月17日
	 */
	List<PhysicsOrderVo> selectOrderBackStageNew(PhysicsOrderVo vo);

	// End sql优化，将复杂sql拆分开来 add by zengj

	/**
	 * 
	 * @Description: 查询POS确认收货订单列表
	 * @param storeId 店铺ID
	 * @return List 确认收货订单列表  
	 * @author zengj
	 * @date 2016年9月13日
	 */
	List<Map<String, Object>> findConfirmDeliveryOrderListByPos(@Param("storeId") String storeId);

	// Begin v1.1.0 add by zengjz
	/**
	 * @Description: 按条件统计订单交易量与金额
	 * @param params 查询参数
	 * @return Map<String,Object>  返回结果
	 * @author zengjizu
	 * @date 2016年9月12日
	 */
	Map<String, Object> statisOrderForFinanceByParams(TradeOrderQueryParamDto tradeOrderQueryParamDto);

	/**
	 * @Description: 按查询条件统计取消订单退款 金额、数量
	 * @param params 查询参数
	 * @return Map<String,Object>  返回结果
	 * @author zengjizu
	 * @date 2016年9月17日
	 */
	Map<String, Object> statisOrderCannelRefundByParams(@Param("params") Map<String, Object> params);

	// end v1.1.0 add by zengjz

	// Begin v1.1.0 add by zengjz 增加查询到店消费列表
	/**
	 * @Description: 增加查询到店消费方法
	 * @param map
	 * @return   
	 * @return List<TradeOrder>  
	 * @throws
	 * @author zengjizu
	 * @date 2016年9月22日
	 */
	List<TradeOrder> selectStoreConsumeOrderList(Map<String, Object> map);

	// End v1.1.0 add by zengjz 增加查询到店消费列表

	// Begin V1.1.0 add by wusw 20160923
	/**
	 * 
	 * @Description: 批量根据订单id，修改订单消费码状态
	 * @param status 消费码状态
	 * @param updateTime 更新时间
	 * @param ids 订单id集合
	 * @return 更新结果
	 * @author wusw
	 * @date 2016年9月23日
	 */
	int updateConsumerStatusByIds(@Param("status") ConsumerCodeStatusEnum status, @Param("updateTime") Date updateTime,
			@Param("ids") List<String> ids);
	// End V1.1.0 add by wusw 20160923

	// Begin V1.1.0 add by zengjz 20160923
	/**
	 * @Description: 查询到店消费订单详情
	 * @param orderId 订单id
	 * @return 订单详情 
	 * @author zengjizu
	 * @date 2016年9月23日
	 */
	UserTradeOrderDetailVo findStoreConsumeOrderById(String orderId);

	// End V1.1.0 add by zengjz 20160923

	// Begin V1.2 added by tangy 2016-11-21
	/**
	 * @Description: 查询回款记录订单导出的列表
	 * @param map
	 * @return List<TradeOrder>  
	 * @author tangy
	 * @date 2016年11月21日
	 */
	List<TradeOrder> findExportList(Map<String, Object> map);
	// End added by tangy

	/**
	 * @Description: tuzhd根据用户id查询其支付完成的订单总量 用于首单条件判断
	 * @param status 符合条件的状态集合
	 * @param userId 用户id
	 * @return int 返回统计值
	 * @author tuzhd
	 * @date 2016年12月31日
	 */
	int selectCountByUserStatus(@Param("userId") String userId);

	// begin add by zhulq 2017-02-17 根据城市名称搜索订单列表
	/**
	 * @Description: 根据城市获取订单id
	 * @param cityId cityId
	 * @return List
	 * @author zhulq
	 * @date 2017年2月17日
	 */
	List<String> findOrderIds(@Param("cityId") String cityId);
	// end add by zhulq 2017-02-17 根据城市名称搜索订单列表

	// begin V2.1 add by zhulq 2017-02-17
	/**
	 * @Description: 根据订单id 获取订单所属店铺 的城市id
	 * @param orderIds  订单id集合
	 * @return List
	 * @author zhulq
	 * @date 2017年2月17日
	 */
	List<PhysicsOrderVo> findCityIds(@Param("orderIds") List<String> orderIds);

	/**
	 * @Description: 根据用户id获取改用户的邀请人登录名
	 * @param userIds 用户集合
	 * @return list
	 * @author zhulq
	 * @date 2017年2月17日
	 */
	List<PhysicsOrderVo> findInvitationInfo(@Param("userIds") List<String> userIds);

	/**
	 * 
	 * @Description: TODO
	 * @param orderIds
	 * @return
	 * @author zhulq
	 * @date 2017年2月22日
	 */
	List<ActivityInfoVO> findActivityInfo(@Param("orderIds") List<String> orderIds);
	// END V2.1 add by zhulq 2017-02-17

	// Begin V2.1 added by maojj 2017-02-18
	/**
	 * @Description: 查询用户订单列表
	 * @param paramBo
	 * @return   
	 * @author maojj
	 * @date 2017年2月18日
	 */
	List<TradeOrder> findUserOrders(UserOrderParamBo paramBo);
	// End V2.1 added by maojj 2017-02-18
	
	/**
	 * 
	 * @Description: 查询所有用户的首购
	 * @return List<TradeOrder>  
	 * @author chenzc
	 * @date 2017年3月20日
	 */
	List<TradeOrder> findFirstTradeOrder();
	
	/**
	 * 查询服务店订单信息
	 * @param map
	 * @return
	 */
	List<TradeOrderDetailBo> findServiceOrderInfo(Map<String, Object> map);
	
	/**
	 * 查询便利店订单信息
	 * @param map
	 * @return
	 */
	List<TradeOrderDetailBo> findCloudOrderInfo(Map<String, Object> map);

	List<TradeOrderStatusVo> getServiceOrderCount(Map<String, Object> params);
}