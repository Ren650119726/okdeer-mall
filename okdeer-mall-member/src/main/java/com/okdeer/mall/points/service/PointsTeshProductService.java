package com.okdeer.mall.points.service;

import java.util.List;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.points.entity.PointsTeshProduct;
import com.okdeer.mall.points.entity.PointsTeshProductQuery;

/**
 * ClassName: PointsTeshProductService 
 * @Description: 积分商品service
 * @author zengjizu
 * @date 2016年12月19日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface PointsTeshProductService extends IBaseService{
	
	/**
	 * @Description: 查询积分商品列表（分页）
	 * @param pointsTeshProductQuery 查询条件
	 * @param pageNum 当前页
	 * @param pageSize 每页大小
	 * @return 积分商品列表
	 * @author zengjizu
	 * @date 2016年12月19日
	 */
	PageUtils<PointsTeshProduct> findList(PointsTeshProductQuery pointsTeshProductQuery, Integer pageNum, Integer pageSize);
	
	/**
	 * @Description: 查询积分商品列表（不分页）
	 * @param pointsTeshProductQuery 查询条件
	 * @return 积分商品列表
	 * @author zengjizu
	 * @date 2016年12月19日
	 */
	List<PointsTeshProduct> findList(PointsTeshProductQuery pointsTeshProductQuery);
}
