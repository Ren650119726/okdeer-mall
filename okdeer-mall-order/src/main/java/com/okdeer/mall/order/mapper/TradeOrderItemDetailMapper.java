package com.okdeer.mall.order.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.enums.ConsumeStatusEnum;
import com.okdeer.mall.order.vo.ExpireStoreConsumerOrderVo;
import com.okdeer.mall.order.vo.OrderItemDetailConsumeVo;

/**
 * @DESC:
 * @author YSCGD
 * @date 2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *    V1.1.0            2016-09-23           wusw                 添加消费码验证（到店消费）相应方法
 *    V1.1.0            2016-09-24           wusw                 添加消费码验证（到店消费）相应方法
 *    V1.1.0            2016-09-29           zhaoqc               查询验证码在店铺中是否存在
 */
public interface TradeOrderItemDetailMapper{

	/**
	 * 更新消费码状态为退款(sql中更新的状态是2.为已退款，更新注释)
	 */
	int updateStatusWithRefund(String orderItemId);
	
	/**
	 * 更新消费码状态为已消费
	 * @author zengj
	 * @param orderItemId
	 * @return
	 */
	int updateStatusWithConsumed(String orderItemId);
	
	/**
	 * 更新消费码状态为过期
	 * @param orderItemId
	 * @return
	 */
	int updateStatusWithExpire(String orderItemId);
	
	/**
	 * 根据orderId查询交易订单项消费详细表(仅服务型商品有)
	 *
	 * @param orderItemId 订单orderItemId
	 * @return 返回查询结果集
	 */
	TradeOrderItemDetail selectByOrderId(String orderItemId);
	
	/**
	 * 根据orderItemId查询交易订单项消费详细表(仅服务型商品有)
	 *
	 * @param orderItemId 订单orderItemId
	 * @return 返回查询结果集
	 */
	List<TradeOrderItemDetail> selectByOrderItemId(String orderItemId);
	
	/**
	 * 消费码生成 </p>
	 * 
	 * @author yangq
	 * @param itemDetail
	 */
	void insertSelective(TradeOrderItemDetail itemDetail);
	
	void insertBatch(List<TradeOrderItemDetail> itemDetailList);
	
	/**
	 * 查询是否生成消息码 </p>
	 * 
	 * @author yangq
	 * @param orderItemId
	 * @return
	 */
	List<TradeOrderItemDetail> selectByOrderItemById(String orderItemId);
	
	/**
	 * 查询未消费数量
	 */
	Integer selectUnConsumerCount(String orderItemId);
	
	// Begin V1.1.0 update by wusw 20160923 
	/**
	 * 
	 * @Description: 批量更新订单项详细状态和时间
	 * @param status 状态
	 * @param useTime 当前时间
	 * @param orderItemIds 订单项目id集合
	 * @return 更新结果
	 * @author wusw
	 * @date 2016年9月23日
	 */
	int updateStatusByDetailId(@Param("status")ConsumeStatusEnum status,@Param("useTime")Date useTime,@Param("orderItemIds")List<String> orderItemIds);
	
	/**
	 * 
	 * @Description: 批量分组统计同一订单的不同消费码状态数量
	 * @param orderIds 订单id集合
	 * @return 不同订单的不同消费码状态数据
	 * @author wusw
	 * @date 2016年9月23日
	 */
	List<Map<String,Object>> findStatusCountByOrderIds(@Param("orderIds")List<String> orderIds);
	// End V1.1.0 update by wusw 20160923 
	
	// Begin V1.1.0 update by zengjz 20160923 
	/**
	 * @Description: 根据订单id查询明细列表
	 * @param orderId 订单id
	 * @return 订单明细列表
	 * @author zengjizu
	 * @date 2016年9月23日
	 */
	List<TradeOrderItemDetail> selectByOrderItemDetailByOrderId(String orderId);
	
	// End V1.1.0 update by zengjz 20160923 
	
	// Begin V1.1.0 add by wusw 20160924
	/**
	 * 
	 * @Description: 批量查询指定店铺的消费码相应的订单信息
	 * @param params 查询参数
	 * @return 消费码的相应订单信息集合
	 * @author wusw
	 * @date 2016年9月24日
	 */
	List<OrderItemDetailConsumeVo> findOrderInfoByConsumeCode(Map<String,Object> params);
	// End V1.1.0 add by wusw 20160924
	
	// Begin V1.1.0 add by zengjz 20160924
	/**
	 * @Description: 根据订单id查询明细列表
	 * @param orderId 订单id
	 * @return 订单明细列表
	 * @author zengjizu
	 * @date 2016年9月23日
	 */
	List<TradeOrderItemDetail> selectItemDetailByOrderIdAndStatus(@Param("orderId") String orderId,@Param("status")int status);
	// End V1.1.0 add by zengjz 20160924
	   
    // Begin V1.1.0 add by zengjz 20160929
    /**
     * @Description: 查询过期的消费码订单列表
     * @return 过期订单列表
     * @author zengjizu
     * @date 2016年9月29日
     */
    List<ExpireStoreConsumerOrderVo> findExpireList();
    
    /**
     * @Description: 更新订单项明细信息
     * @param orderItemDetail  订单项明细信息
     * @return 影响行数
     * @author zengjizu
     * @date 2016年9月29日
     */
    int updateByPrimaryKeySelective(TradeOrderItemDetail orderItemDetail);
    // End V1.1.0 add by zengjz 20160929
    
	/**
	 * 验证消费码在店铺中是否已经存在
	 * @param storeId 店铺Id
	 * @param consumeCode 消费码
	 * @return
	 */
	TradeOrderItemDetail checkConsumeHasExsit(@Param("storeId") String storeId, @Param("consumeCode") String consumeCode);
	
	// Begin V1.1.0 add by zengjz 20160924
	/**
	 * @Description: 根据订单项id查询明细列表
	 * @param itemId 订单项id
	 * @param status 状态
	 * @return 明细列表
	 * @author zengjizu
	 * @date 2016年9月30日
	 */
	List<TradeOrderItemDetail> selectItemDetailByItemIdAndStatus(@Param("itemId") String itemId,@Param("status")int status);
	
	// End V1.1.0 add by zengjz 20160924
	
	// Begin V1.1.0 add by zengjz 20161011
	/**
	 * @Description: 根据id查询详情
	 * @param id id
	 * @return 订单项detail详情
	 * @author zengjizu
	 * @date 2016年10月11日
	 */
	TradeOrderItemDetail selectByPrimaryKey(String id);
	
	// End V1.1.0 add by zengjz 20161011
}