/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: TradeOrderCommentService.java 
 * @Date: 2016年1月30日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */

package com.okdeer.mall.order.service;

import java.util.List;

import com.okdeer.mall.order.entity.TradeOrderComment;
import com.okdeer.mall.order.vo.TradeOrderCommentVo;
import com.okdeer.archive.store.dto.StoreOrderCommentDto;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;

/**
 * 商品评论接口
 * @project yschome-mall
 * @author zhongy
 * @date 2016年1月30日 上午10:43:18
 *  =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *    V2.1.0           2017-02-16          wusw              在原来的订单评价方法中，增加店铺评分参数
 */
public interface TradeOrderCommentService {

	/**
	* 根据主键id查询评论
	* @param id 评论id
	* @return 返回查询结果
	*/
	TradeOrderCommentVo findById(String id) throws ServiceException;

	/**
	* @param tradeOrderCommentVo 条件查询参数
	* 必填 ：skuId 商品skuId
	* 必填 ：orderItemId 订单项orderItemId
	* @return 返回查询结果
	*/
	List<TradeOrderCommentVo> findByParams(TradeOrderCommentVo tradeOrderCommentVo) throws ServiceException;

	/**
	 * 添加商品评论
	 * @param tradeOrderCommentVo 评论tradeOrderCommentVo
	 */
	void add(TradeOrderCommentVo tradeOrderCommentVo) throws ServiceException;

	/**
	 * 根据主键id删除评论
	 * @param id 评论id
	 */
	void deleteByPrimaryKey(String id) throws ServiceException;

	/**zhulq
	 * 根据店铺skuid 查询商品评价详情
	 * @param tradeOrderComment tradeOrderComment
	 * @return List<TradeOrderCommentVo>
	 * @throws ServiceException 异常
	 */
	PageUtils<TradeOrderCommentVo> getByStoreSkuId(TradeOrderComment tradeOrderComment, int pageNumber, int pageSize)
			throws ServiceException;

	/**
	* sku评论总条数
	*
	* @param skuId 店铺skuId
	* @return 评论总条数
	* @throws ServiceException
	*/
	Integer getSkuCommentCount(String skuId) throws ServiceException;

	/**zhulq
	 * 批量添加  tradeOrderCommentImage
	 * @param tradeOrderCommentImageList
	 * @throws ServiceException
	 */
	// void addByBatch(List<TradeOrderCommentImage> tradeOrderCommentImageList)
	// throws ServiceException;

	/**zhulq
	 * 提交商品评论
	 * @param tradeOrderComment
	 * @throws ServiceException
	 */
	void addCommentByBatch(List<TradeOrderCommentVo> tradeOrderCommentVoList) throws ServiceException;

	/**
	 * 根据订单id获取关联信息 
	 * @param orderId
	 * @return
	 * @throws ServiceException
	 */
	List<TradeOrderCommentVo> findOrderCommentByOrderId(String orderId) throws ServiceException;

	// Begin V2.1 add by wusw 20170216 增加店铺评分参数
	boolean updateUserEvaluate(List<TradeOrderCommentVo> tradeOrderCommentVoList,StoreOrderCommentDto storeOrderCommentDto) throws Exception;
	// End V2.1 add by wusw 20170216  增加店铺评分参数
	/**
	 * 根据订单项查询评价
	 * @return
	 */
	List<TradeOrderCommentVo> findListByOrderId(String orderItemId);
}
