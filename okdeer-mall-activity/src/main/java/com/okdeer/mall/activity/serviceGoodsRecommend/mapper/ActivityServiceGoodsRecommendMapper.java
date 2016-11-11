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
	 * @author zhangkn
	 * @date 2016年11月7日
	 */
	List<Map<String,Object>> listGoods(Map<String,Object> map) throws Exception;
	
	/**
	 * @Description: 批量修改状态
	 * @param id
	 * @param status 活动状态 0 未开始 ，1：进行中2:已结束 3 已关闭
	 * @param updateUserId 修改人
	 * @param updateTime 修改时间
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年11月4日
	 */
	void updateBatchStatus(String id,int status,String updateUserId,Date updateTime) throws Exception;
	
	/**
	 * @desc 用于判断某个时间段内活动是否冲突
	 * @param map
	 * @return
	 */
	int countTimeQuantum(Map<String,Object> map);
	
	/**
	 * @desc 查询出需要跑job的活动
	 * @return
	 */
	public List<ActivityServiceGoodsRecommend> listByJob();
	
}
