package com.okdeer.mall.order.mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.archive.system.pos.entity.PosShiftExchange;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.dal.IBaseCrudMapper;
import com.okdeer.mall.order.dto.OrderRefundQueryParamDto;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsImage;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.vo.TradeOrderRefundsChargeVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsQueryVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsStatusVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsVo;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-03-31 17:15:36
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *   重构4.1				2016-7-19			zhulq
 *   重构4.1             2016-7-13            wusw              添加退款中、第三方支付的充值退款记录数方法、查询充值订单列表方法（用于财务系统）
 *   v1.1.0				2016-9-17            zengjz            增加财务系统统计数量、金额方法
 *   V1.1.0				2016-09-27			luosm			        查询服务店到店消费退款单状态下对应的退款单数量
 *   V1.1.0				2016-09-28			wusw			       修改查询退款单数量，如果是服务店，只查询到店消费退款单
 */
public interface TradeOrderRefundsMapper extends IBaseCrudMapper {

	/**
	 * 根据主键查询退款单
	 */
	List<TradeOrderRefunds> selectByPrimaryKeys(List<String> ids);

	/**
	 * 查询退款单详情
	 */
	TradeOrderRefunds findInfoById(String id);

	/**
	 * zhongy
	 * 根据用户id订单类型查询退款单列表
	 * @param params 请求参数
	 * @return 返回查询结果
	 */
	List<TradeOrderRefunds> selectByUserIdAndType(@Param("params")
	Map<String, Object> params);

	/**
	 * 根据订单id，统计退款金额
	 * @author wusw
	 * @param orderId 订单id
	 */
	Double selectSumAmountByOrderId(String orderId);

	/**
	 * 根据退款状态，查询线上退款单信息（pos--线上退货列表）
	 */
	List<TradeOrderRefunds> selectOnlineByRefundsStatus(@Param("refundsStatus")
	List<RefundsStatusEnum> refundsStatus, @Param("orderResource")
	OrderResourceEnum orderResource);

	/**
	 * 根据退款单id，查询退款单详细信息（包括退款单、商品、订单、支付等信息）
	 * @author wusw
	 */
	TradeOrderRefundsQueryVo selectDetailById(String id);

	/**
	 * 商家版APP根据退款单id，查询退款单详细信息
	 * 
	 * @author zengj
	 */
	TradeOrderRefundsVo selectRefundOrderDetailById(String id);

	/**
	 * 
	 * @desc 退款单搜索
	 *
	 * @param map 查询条件
	 */
	List<TradeOrderRefundsVo> searchOrderRefundByParams(Map<String, Object> map);
	
	//Begin V2.1.0 added by luosm 20170314
	/**
	 * 
	 * @desc 商家版app退款单搜索
	 *
	 * @param map 查询条件
	 */
	List<TradeOrderRefundsVo> searchOrderRefundForSELLERAPP(Map<String, Object> map);
	//End V2.1.0 added by luosm 20170314
	
	/**
	 * 根据状态查询退款订单数量
	 * @return Integer
	 */
	Integer getTradeOrderRefundsCount(Map<String, Object> map);

	/**
	 * @desc 退款单搜索
	 * @param map 查询条件
	 * @return List
	 */
	List<TradeOrderRefunds> getOrderRefundByParams(Map<String, Object> map);

	/**
	 * 根据订单id查询订单退退款数量
	 * @return Integer
	 */
	Integer getTradeOrderRefundsCountByOrderId(Map<String, Object> map);

	/**
	 * 商家版APP查询退款单信息
	 */
	List<TradeOrderRefundsVo> selectMallAppByParams(Map<String, Object> map);

	/**
	 * 查询退款单状态下对应的退款单数量
	 * @param storeId 店铺ID
	 */
	List<TradeOrderRefundsStatusVo> getOrderRefundsCount(@Param("storeId")String storeId);
	
	//start added by luosm 20160927 V1.1.0
	/***
	 * 
	 * @Description: 查询服务店到店消费退款单状态下对应的退款单数量
	 * @param storeId
	 * @return
	 * @author luosm
	 * @date 2016年9月27日
	 */
	List<TradeOrderRefundsStatusVo> selectServiceOrderRefundsCount(@Param("storeId")String storeId);
	//end added by luosm 20160927 V1.1.0

	/**
	 * 根据退款单ID查看所属图片
	 * @param refundsId String
	 * @return List
	 */
	List<TradeOrderRefundsImage> getTradeOrderRefundsImage(@Param("refundsId")
	String refundsId);

	/**
	 * @desc 退款单查询
	 * @author yangq
	 * @param map 查询条件
	 */
	List<TradeOrderRefundsVo> selectWXRefundsOrder(Map<String, Object> map);

	/**
	 * 退款列表查询
	 */
	List<TradeOrderRefundsVo> selectRefundsByFinance(OrderRefundQueryParamDto orderRefundQueryParamDto);

	/**
	 * 统计退款列表数
	 */
	Integer selectRefundsCountByFinance(OrderRefundQueryParamDto orderRefundQueryParamDto);

	/**
	 * 查询退款详细信息 
	 * @param refundId 退款单ID
	 */
	TradeOrderRefundsVo selectDetailByFinance(String refundId);

	/**
	 * 退货/退款单删除
	 * 
	 */
	int delete(TradeOrderRefunds tradeOrderRefunds);

	/**
	 * 获取退单现金统计
	 */
	BigDecimal findRefundCashCount(Map<String, Object> params);

	/**
	 * 线上支付退款统计
	 */
	BigDecimal findOnlineSum(Map<String, Object> params);

	/**
	 * 货到付款退款统计
	 */
	BigDecimal findCashDelierySum(Map<String, Object> params);

	/**
	 * POS销售退款统计
	 */
	BigDecimal findPosSum(Map<String, Object> params);

	//Begin 添加查询条件 update by tangy  2016-10-31
	/**
	 * 根据店铺id、时间段查询订单统计数据
	 * @param storeId      店铺id
	 * @param startTime    登陆时间
	 * @param endTime     结束时间
	 * @return  交班统计
	 */
	PosShiftExchange findPosShiftExchangeByStoreId(@Param("storeId")
	String storeId, @Param("startTime")
	Date startTime, @Param("endTime")
	Date endTime, @Param("userId") String userId);
	//End added by tangy
	
	/**
	 * 退货单列表(pos销售查询用)
	 * @author zhangkeneng
	 * @param map
	 * @throws ServiceException
	 */
	List<TradeOrderRefunds> listForPos(Map<String, Object> map);

	/**
	 * 退款订单未支付统计
	 */
	Integer findRefundUnPayCount();

	/**
	 * 投诉订单未支付统计
	 */
	Integer findComplainUnPayCount();

	/**
	 * zengj:查询店铺一段时间内退款实际金额
	 *
	 * @param map
	 * @return
	 */
	BigDecimal selectRefundTotalAmount(Map<String, Object> map);

	/**
	 * zengj:查询店铺一段时间内退款单参与店铺本身发起的活动优惠金额
	 *
	 * @param map
	 * @return
	 */
	BigDecimal selectRefundPreferentialPrice(Map<String, Object> map);

	/**
	 * zengj:查询店铺内所有的退单数
	 *
	 * @param storeId 店铺ID
	 * @param type 订单类型
	 * @return
	 */
	// Begin V1.1.0 add by wusw 20160928
	Long selectRefundsCount(@Param("storeId")String storeId,@Param("type")OrderTypeEnum type);
	// End V1.1.0 add by wusw 20160928

	/**
	 * 根据订单项查询退款单
	 *
	 * @param orderItemId
	 * @return
	 */
	List<TradeOrderRefunds> getTradeOrderRefundsByOrderItemId(String orderItemId);

	TradeOrderRefunds getTradeOrderRefundsByOrderNo(String orderNo);

	/**
	 * 商家中心首页 根据状态统计退款单数量(张克能加)
	 */
	public Integer selectRefundsCountForIndex(@Param("storeId")
	String storeId, @Param("refundsStatusList")
	List<Integer> refundsStatusList);

	/**
	 * 查询pos退款单导出列表
	 * @desc TODO Add a description 
	 * @author zengj
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> selectPosRefundExportList(Map<String, Object> params);

	// begin 根据订单id查询退款详情 add by zhulq 2019-7-19
	/**
	 * @Description: 根据订单号查询退款信息
	 * @param orderId  订单id
	 * @return   TradeOrderRefunds
	 * @author zhulq
	 * @date 2016年7月19日
	 */
	List<TradeOrderRefunds> selectByOrderId(String orderId);
	
	/**
	 * 
	 * @Description: 根据订单id查询退款单信息
	 * @param orderId
	 * @return
	 * @author luosm
	 * @date 2017年2月20日
	 */
	TradeOrderRefunds selectByOrderIdOne(String orderId);

	// end 根据订单id查询退款详情 add by zhulq 2019-7-19
	
	//Begin V2.1.0 added by luosm 20170222
	/**
	 * 
	 * @Description: 根据orderIds查询退款单列表
	 * @param orderIds
	 * @return
	 * @author luosm
	 * @date 2017年2月22日
	 */
	List<TradeOrderRefunds> selectByOrderIds(@Param("orderIds") List<String> orderIds);
	//End V2.1.0 added by luosm 20170222

	// Begin 重构4.1 add by wusw 20160722
	/**
	 * 
	 * @Description: 查询退款中状态、第三方支付的充值退款记录数（用于财务系统）
	 * @param params 查询条件（退款状态、支付方式、退款单类型）
	 * @return Integer 
	 * @author wusw
	 * @date 2016年7月22日
	 */
	Integer findCountChargeForFinance(Map<String, Object> params);

	/**
	 * 
	 * @Description: 查询充值退款列表（用于财务系统）
	 * @param params 查询条件
	 * @return List<TradeOrderRefundsChargeVo> 
	 * @author wusw
	 * @date 2016年7月22日
	 */
	List<TradeOrderRefundsChargeVo> findeChargeRefundsByParams(Map<String, Object> params);

	// End 重构4.1 add by wusw 20160722

	// Begin v1.1.0 add by zengjz 20160914

	/**
	 * @Description: 根据条件统计金额、数量
	 * @param params 查询条件
	 * @return Map<String,Object>  返回结果
	 * @author zengjizu
	 * @date 2016年9月14日
	 */
	Map<String, Object> statisRefundsByFinance(OrderRefundQueryParamDto orderRefundQueryParamDto);
	
	List<TradeOrderRefunds> getListByParams(Map<String, Object> params);
	
	TradeOrderRefunds findStoreConsumeOrderDetailById(String refundId);

	// end v1.1.0 add by zengjz 20160914
}