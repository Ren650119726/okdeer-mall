package com.okdeer.mall.activity.serviceLimit.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.serviceLimit.entity.ActivityServiceLimit;

/**
 * @DESC:限购活动dao
 * @author zhangkn
 * @date  2015-11-25 16:24:57
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface ActivityServiceLimitMapper extends IBaseMapper{
	
	/**
	 * @desc 保存 
	 * @param activityLimit 限购活动对象
	 */
	void add(ActivityServiceLimit activityLimit);
	
	/**
	 * @desc 修改
	 * @param activityLimit 限购活动对象
	 */
	void update(ActivityServiceLimit activityLimit);
	
	/**
	 * @desc 列表搜索
	 * @param map 封装了搜索条件
	 * @return 限购活动列表
	 */
	List<ActivityServiceLimit> list(Map<String,Object> map);
	
	/**
	 * @desc 批量更新
	 * @param map 
	 */
	void updateBatchStatus(Map<String,Object> map);
	
	/**
	 * @Description: 商品列表
	 * @param map
	 * @return
	 * @author zhangkn
	 * @date 2016年12月17日
	 */
	List<Map<String,Object>> listGoodsStoreSku(Map<String,Object> map);
	
	/**
	 * @Description: 校验活动是否冲突
	 * @param map
	 * @return
	 * @author zhangkn
	 * @date 2016年12月17日
	 */
	int validateExist(Map<String,Object> map);
	
	/**
	 * @Description: TODO
	 * @param map
	 * @return
	 * @author zhangkn
	 * @date 2016年10月21日
	 */
	List<ActivityServiceLimit> listByJob(Map<String,Object> map);
	
	/**
	 * @Description: 根据店铺id查询限购活动方法
	 * @param storeIdList
	 * @return
	 * @author zengjizu
	 * @date 2016年12月17日
	 */
	List<ActivityServiceLimit> findByStoreIds(@Param("storeIdList") List<String> storeIdList);
	
}