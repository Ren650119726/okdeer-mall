package com.okdeer.mall.points.service;

import java.util.List;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.points.vo.TeshProductDetailVo;
import com.okdeer.mall.points.vo.TeshProductVo;

/**
 * 
 * ClassName: PointsTeshSynchronService 
 * @Description: 特奢汇API对接
 * @author tangy
 * @date 2016年12月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     1.3.0          2016年12月15日                               tangy
 */
public interface PointsTeshSynchronService {
	/**
	 * 
	 * @Description: 商品定时同步
	 * @throws ServiceException   
	 * @return void  
	 * @author tangy
	 * @date 2016年12月19日
	 */
	public void synchron() throws ServiceException;
	
    /**
     * 
     * @Description: 分页查询特奢汇商品列表
     * @param pageNo    页码
     * @param pageSize  每页数
     * @return List<TeshProductVo>  商品列表
     * @author tangy
     * @date 2016年12月16日
     */
	public PageUtils<TeshProductVo> findTeshProductByAll(Integer pageNo, Integer pageSize) throws ServiceException;
	
	/**
	 * 
	 * @Description: 根据商品skuCode查询商品详情
	 * @param skuCodes
	 * @return List<TeshProductDetailVo>  
	 * @author tangy
	 * @date 2016年12月16日
	 */
	public List<TeshProductDetailVo> findDetailBySkuCodes(List<String> skuCodes)throws ServiceException;

}
