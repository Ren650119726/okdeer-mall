package com.okdeer.mall.activity.serviceGoodsRecommend.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.serviceGoodsRecommend.entity.ActivityServiceGoodsRecommend;

/**
 * @pr yscm
 * @desc 服务商品推荐活动
 * @author zhangkn
 * @date 2016-11-08 下午3:12:43
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 */
public interface ActivityServiceGoodsRecommendMapper extends IBaseMapper{
	
	/**
	 * @Description: 查询列表
	 * @param map 参数
	 * @param pageNumber 当前页码
	 * @param pageSize 每页条数
	 * @return PageUtils<ActivityCollectCoupons>  
	 * @throws ServiceException service异常
	 * @author zhangkn
	 * @date 2016年11月7日
	 */
	List<ActivityServiceGoodsRecommend> list(Map<String,Object> map) throws ServiceException;
	
	/**
	 * @Description: 查询可选商品列表
	 * @param map 参数
	 * @param pageNumber 当前页码
	 * @param pageSize 每页条数
	 * @return PageUtils<ActivityCollectCoupons>  
	 * @throws Exception service异常
	 * @author tuzhiding
	 * @date 2016年11月7日
	 */
	List<Map<String,Object>> listGoods(Map<String,Object> map) throws Exception;
	
	
	/**
	 * @desc 用于判断某个时间段内活动是否冲突
	 * @param map
	 * @return
	 */
	int countTimeQuantum(Map<String,Object> map);
	
	/**
	 * 1、查询活动未开始，开始时间小于当前的数据 即为要设置开始，2、活动开始、结束时间小于当前的数据 即为要设置结束
	 * @Description: TODO
	 * @param map 传递查询参数
	 * @return List<ActivityServiceGoodsRecommend>  
	 * @author tuzhd
	 * @date 2016年11月12日
	 */
	public List<ActivityServiceGoodsRecommend>  listByJob(Map<String,Object> map);
	
}
