package com.okdeer.mall.order.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.order.entity.TradeOrderCommentImage;

/**
 * @DESC: 商品评论图片dao
 * @author zhongy
 * @date  2016-01-30 09:59:24
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface TradeOrderCommentImageMapper{
	
	/**
	 * 添加商品评论图片地址
	 * @param tradeOrderCommentImage TradeOrderCommentImage
	 */
	void insert(TradeOrderCommentImage tradeOrderCommentImage);
	    
	/**
	 * 商品评论图片地址
	 *
	 * @param commentId 请求参数
	 * @return 返回查询结果
	 */
	List<TradeOrderCommentImage> selectByCommentId(String commentId);
	
	/**
	 * 根据主键id删除评论图片
	 * @param commentId 评论commentId
	 */
	void deleteByCommentId(String commentId);
	
	/**
	 * 提交评价时候  将图片保存
	 * @param tradeOrderCommentImageList
	 */
	void insertByBatch(@Param("tradeOrderCommentImageList") List<TradeOrderCommentImage> tradeOrderCommentImageList);
}