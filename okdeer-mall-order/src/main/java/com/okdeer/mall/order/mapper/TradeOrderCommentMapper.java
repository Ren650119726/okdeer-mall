package com.okdeer.mall.order.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.order.entity.TradeOrderComment;
import com.okdeer.mall.order.vo.TradeOrderCommentVo;

/**
 * @DESC: 商品评论dao
 * @author zhongy
 * @date  2016-01-30 09:59:24
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface TradeOrderCommentMapper {
	
	 /**
	 * 根据主键id查询评论
	 * @param id 评论id
	 * @return 返回查询结果
	 */
	TradeOrderCommentVo selectByPrimaryKey(String id);
	    
	 /**
	 * @param params 条件查询参数
	 * @return 返回查询结果
	 */
	List<TradeOrderCommentVo> selectByParams(@Param("params")Map<String,Object> params);
	    
    /**
	 * 添加商品评论
	 * @param tradeOrderComment 评论tradeOrderComment
	 */
    void insert(TradeOrderComment tradeOrderComment);

    /**
	 * 按需添加商品评论
	 * @param tradeOrderComment 评论tradeOrderComment
	 */
    void insertSelective(TradeOrderComment tradeOrderComment);
	/**
	 * 根据主键id删除评论
	 * @param id 评论id
	 */
	void deleteByPrimaryKey(String id);

	
	/**
	 * 根据条件查询评论信息
	 *
	 * @param map
	 * @return
	 */
	List<TradeOrderCommentVo> selectCommentByParams(Map<String, Object> map);
	
	/**zhulq 
	 *  用户app 查询店铺商品的评价详情
	 * @param tradeOrderComment tradeOrderComment
	 * @return List<TradeOrderCommentVo> 
	 */ 
	List<TradeOrderCommentVo> selectByStoreSkuId(TradeOrderComment tradeOrderComment);
	
	 /**
     * sku评论总条数
     *
     * @param skuId 店铺skuId
     * @return 评论总条数
     */
    Integer selectSkuCommentCount(@Param("skuId") String skuId);
    
    /**zhulq
     * 根据订单id 查询关联信息 
     * @param orderId
     * @return
     */
    List<TradeOrderCommentVo> selectOrderCommentByOrderId(@Param("orderId") String orderId);
   
    /**zhulq
     * 订单商品评论提交  批量
     * @param tradeOrderCommentList
     */
    void insertByBatch(@Param("tradeOrderCommentList") List<TradeOrderComment> tradeOrderCommentList);  
}