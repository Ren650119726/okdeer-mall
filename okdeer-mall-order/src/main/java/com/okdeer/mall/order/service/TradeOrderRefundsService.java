package com.okdeer.mall.order.service;

import java.util.Date;
import java.util.List;
import java.util.Map;


import com.okdeer.mall.order.dto.OrderRefundQueryParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.entity.TradeOrderRefundsLogistics;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.vo.TradeOrderRefundsCertificateVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsChargeVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsExportVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsQueryVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsStatusVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsVo;
import com.okdeer.base.common.utils.PageUtils;

/**
 * @DESC:售后订单Service
 * @author guocp
 * @date 2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 *  =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *    重构4.1            2016-7-13            wusw              添加退款中、第三方支付的充值退款记录数方法、查询充值订单列表方法（用于财务系统）
 * 	  重构4.1	            2016-7-13            zhaoqc            添加支付订单退款
 *    v1.1.0            2016-9-17 			zengjz 		                  添加统计订单退款金额、数量接口
 *    V1.1.0			2016-09-27			luosm			     查询服务店到店消费退款单状态下对应的退款单数量
 *    V1.1.0			2016-09-28			wusw			       修改查询退款单数量，如果是服务店，只查询到店消费退款单
 */
public interface TradeOrderRefundsService {

	/**
	 * 买家申请退货
	 */
	void insertRefunds(TradeOrderRefunds orderRefunds) throws Exception;

	/**
	 * 插入退款单
	 *
	 * @param orderRefunds 退款单信息
	 * @param certificate 凭证信息
	 */
	void insertRefunds(TradeOrderRefunds orderRefunds, TradeOrderRefundsCertificateVo certificate) throws Exception;

	/**
	 * 更新退款单状态
	 * @throws Exception 
	 */
	int updateRefunds(TradeOrderRefunds tradeOrderRefunds) throws Exception;

	/**
	 * 用户更新退款单
	 * @throws Exception 
	 */
	void updateRefunds(TradeOrderRefunds orderRefunds, TradeOrderRefundsCertificateVo certificate) throws Exception;

	/**
	 * 用户修改退单申请
	 */
	void alterRefunds(TradeOrderRefunds orderRefunds, TradeOrderRefundsCertificateVo certificate);



	/**
	 * 卖家同意退款申请
	 */
	void updateStatusWithAgree(String id, String addressId, String remark, String userId) throws Exception;

	/**
	 * 卖家拒绝退款申请
	 */
	void updateStatusWithRefuse(String id, String reason, String userId) throws Exception;

	/**
	 * 卖家同意退款
	 */
	void updateAgreePayment(String id, String userId) throws Exception;

	/**
	 * 卖家拒绝退款
	 */
	void updateRefusePayment(String id, String userId, String reason) throws Exception;

	/**
	 * 删除退货/退款单
	 */
	int delete(TradeOrderRefunds tradeOrderRefunds) throws Exception;

	/**
	 * 根据主键查询退款单
	 *
	 * @param id 主键ID
	 */
	TradeOrderRefunds getById(String id);

	/**
	 * 根据主键查询退款单
	 *
	 * @param id 主键ID
	 */
	TradeOrderRefunds findById(String id);

	/**
	 * 根据主键查询退款单 
	 */
	List<TradeOrderRefunds> findByIds(List<String> ids);

	/**
	 * 判断订单项是否有退款
	 */
	boolean isRefundOrderItemId(String orderId);

	/**
	 * 查询退款单详情
	 */
	TradeOrderRefunds findInfoById(String id) throws Exception;

	/**
	 * 根据支付号查询售后单
	 */
	TradeOrderRefunds getByTradeNum(String tradeNum);

	/**
	 * 根据售后订单号查询 
	 */
	TradeOrderRefunds getByRefundNo(String refundNo);

	/**
	 * 根据售后订单号查询 
	 */
	List<TradeOrderRefunds> findByOrderNo(String orderNo);

	/**
	 * @desc 退款单搜索
	 *
	 * @param map 查询条件
	 * @param pageNumber 当前页
	 * @param pageSize 每页展示记录数
	 */
	PageUtils<TradeOrderRefundsVo> searchOrderRefundByParams(Map<String, Object> map, int pageNumber, int pageSize);

	/**
	 * 查询退款单导出列表
	 * @author zengj
	 * @param map 查询条件
	 * @param maxSize 导出最大值，如为空，不限制导出数量
	 */
	List<TradeOrderRefundsExportVo> selectExportList(Map<String, Object> map, Integer maxSize);

	/**
	 * 
	 * @desc 退款单搜索
	 *
	 * @param map 查询条件
	 * @param pageNumber 当前页
	 * @param pageSize 每页展示记录数
	 */
	PageUtils<TradeOrderRefunds> getOrderRefundByParams(Map<String, Object> map, int pageNumber, int pageSize);

	/**
	 * zhongy
	 * 买家退款单列表
	 * 根据用户id订单类型查询退款单列表
	 * @param params 请求参数
	 * @return 返回查询结果
	 */
	PageUtils<TradeOrderRefunds> findByUserIdAndType(Map<String, Object> params, int pageNumber, int pageSize)
			throws Exception;

	/**
	 * 列表查询by财务
	 */
	List<TradeOrderRefundsVo> findListByFinance(OrderRefundQueryParamDto orderRefundQueryParamDto) throws Exception;

	/**
	 * 统计查询列表数by财务
	 */
	Integer findCountByFinance(OrderRefundQueryParamDto orderRefundQueryParamDto) throws Exception;

	/**
	 * 分页查询
	 */
	PageUtils<TradeOrderRefundsVo> findPageByFinance(OrderRefundQueryParamDto orderRefundQueryParamDto, int pageNumber, int pageSize)
			throws Exception;

	/**
	 * 财务系统查询售后单详细信息
	 */
	TradeOrderRefundsVo findDetailByFinance(String refundId) throws Exception;

	/**
	 * 根据订单id，统计退款金额
	 * @author wusw
	 */
	Double getSumAmountByOrderId(String orderId) throws Exception;

	/**
	 * 根据退款状态，查询线上退款单信息（pos--线上退货列表）
	 * @author wusw
	 */
	List<TradeOrderRefunds> findOnlineByRefundsStatus(String refundsStatus) throws Exception;

	/**
	 * 根据退款单id，查询退款单详细信息（包括退款单、商品、订单、支付等信息）
	 * @author wusw
	 */
	TradeOrderRefundsQueryVo findDetailById(String id) throws Exception;

	/**
	 * @desc 根据退款单id，查询退款单详细信息
	 * @author zengj
	 */
	TradeOrderRefundsVo selectRefundOrderDetailById(String id) throws Exception;

	/**
	 * 根据状态查询退款订单数量
	 * @return Integer
	 */
	Integer getTradeOrderRefundsCount(Map<String, Object> map);

	/**
	 * 根据订单id查询订单退退款数量
	 * 
	 * @param orderId String
	 * @return Integer
	 */
	Integer getTradeOrderRefundsCountByOrderId(String orderId);

	/**
	 * 商家版APP查询退款单信息
	 *
	 * @param map 查询条件
	 * @param pageNumber 当前页
	 * @param pageSize 每页展示数量
	 */
	PageUtils<TradeOrderRefundsVo> selectMallAppByParams(Map<String, Object> map, int pageNumber, int pageSize);

	/**
	 * 查询退款单状态下对应的退款单数量
	 *
	 * @param storeId 店铺ID
	 */
	List<TradeOrderRefundsStatusVo> getOrderRefundsCount(String storeId);
	
	//start added by luosm 20160927 V1.1.0
	/***
	 * 
	 * @Description: 查询服务店到店消费退款单状态下对应的退款单数量
	 * @param storeId
	 * @return
	 * @author luosm
	 * @date 2016年9月27日
	 */
	List<TradeOrderRefundsStatusVo> selectServiceOrderRefundsCount(String storeId);
	//end added by luosm 20160927 V1.1.0

	/**
	 * 微信版App查询退货/退款单列表
	 * @author yangq
	 */
	PageUtils<TradeOrderRefundsVo> selectWXRefundsOrder(Map<String, Object> map, int pageNumber, int pageSize)
			throws Exception;

	/**
	 * 退货单列表(pos销售查询用)
	 * @author zhangkeneng
	 */
	List<TradeOrderRefunds> listForPos(Map<String, Object> map) throws Exception;

	/**
	 * 退款单未支付统计 
	 */
	Integer findRefundUnPayCount();

	/**
	 * 投诉单未支付统计
	 */
	Integer findComplainUnPayCount();

	/**
	 * zengj:查询店铺的退款数量
	 *
	 * @param storeId 店铺
	 * @return
	 */
	// Begin V1.1.0 add by wusw 20160928
	Long selectRefundsCount(String storeId,OrderTypeEnum type);
	// End V1.1.0 add by wusw 20160928

	List<TradeOrderRefunds> getTradeOrderRefundsByOrderItemId(String orderItemId);

	TradeOrderRefunds getTradeOrderRefundsByOrderNo(String orderNo);

	/**
	 * 客服处理更新订单状态
	 */
	void updateByCustomer(String refundsId, RefundsStatusEnum status, String userId) throws Exception;

	/**
	 * 商家中心首页 根据状态统计退款单数量(张克能加)
	 */
	Integer selectRefundsCountForIndex(String storeId, List<Integer> refundsStatusList);

	/**
	 * 保存物流信息
	 */
	void saveLogistics(TradeOrderRefunds refunds, TradeOrderRefundsLogistics logistics) throws Exception;

	/**
	 * 撤销退款申请
	 */
	void updateWithRevocatory(TradeOrderRefunds refunds, TradeOrderRefundsCertificateVo certificate) throws Exception;

	/**
	 * 查询pos退款单导出列表
	 * @desc TODO Add a description 
	 * @author zengj
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> selectPosRefundExportList(Map<String, Object> params);

	// Begin 重构4.1 add by wusw 20160722
	/**
	 * 
	 * @Description: 查询退款中状态、第三方支付的充值退款记录数（用于财务系统）
	 * @return Integer 
	 * @throws Exception 
	 * @author wusw
	 * @date 2016年7月22日
	 */
	Integer findCountChargeForFinance() throws Exception;

	/**
	 * 
	 * @Description: 查询充值退款列表（用于财务系统，分页）
	 * @param params 查询条件
	 * @throws Exception   
	 * @return PageUtils<TradeOrderRefundsChargeVo>
	 * @author wusw
	 * @date 2016年7月22日
	 */
	PageUtils<TradeOrderRefundsChargeVo> findeChargeRefundsByParams(Map<String, Object> params, int pageNumber,
			int pageSize) throws Exception;

	/**
	 * 
	 * @Description: 查询充值退款列表（用于财务系统，不分页）
	 * @param params 查询条件
	 * @throws Exception   
	 * @return List<TradeOrderRefundsChargeVo>  
	 * @author wusw
	 * @date 2016年7月22日
	 */
	List<TradeOrderRefundsChargeVo> findeChargeRefundsListByParams(Map<String, Object> params) throws Exception;

	// End 重构4.1 add by wusw 20160722

	/**
	 * 插入充值订单退款单
	 * @param tradeOrder
	 * @throws Exception
	 */
	void insertRechargeRefunds(TradeOrder tradeOrder) throws Exception;

	// Begin v1.1.0 add by zengjz 20160917 统计订单退款金额、数量
	Map<String, Object> statisRefundsByParams(OrderRefundQueryParamDto orderRefundQueryParamDto) throws Exception;
	// End v1.1.0 add by zengjz 20160917 统计订单退款金额、数量
	
	/**
	 * @Description: 财务后台退款成功后处理
	 * @param orderRefunds
	 * @throws Exception
	 * @author zengjizu
	 * @date 2017年1月16日
	 */
	void refundSuccess(TradeOrderRefunds orderRefunds) throws Exception;
	
	List<TradeOrderRefunds> selectByOrderIds(List<String> orderIds) throws Exception;
}