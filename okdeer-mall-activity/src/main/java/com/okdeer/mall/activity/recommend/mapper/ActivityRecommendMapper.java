/** 
 *@Project: yschome-mall-activity 
 *@Author: zhongy
 *@Date: 2016年7月18日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */
package com.okdeer.mall.activity.recommend.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.activity.recommend.entity.ActivityRecommend;
import com.okdeer.mall.activity.recommend.entity.ActivityRecommendGoods;
import com.okdeer.mall.activity.recommend.entity.ActivityRecommendRange;
import com.yschome.base.common.exception.ServiceException;

/**
 * 
 * ClassName: ActivityRecommendMapper 
 * @Description: TODO
 * @author zhongy
 * @date 2016年7月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构4.1          2016年7月18日                   zhongy             推荐活动mapper
 */

public interface ActivityRecommendMapper {

	/**
	 * 添加推荐活动
	 * @return void  
	 * @param activityRecommend ActivityRecommend
	 * @throws ServiceException
	 * @author zhongy
	 * @date 2016年7月18日
	 */
	void insert(ActivityRecommend activityRecommend) throws ServiceException;
	
    /**
     * 添加推荐活动和区域关联
     * @return void  
     * @param activityRecommendRanges List<ActivityRecommendRange>
     * @throws ServiceException
     * @author zhongy
     * @date 2016年7月18日
     */
    void insertBatchActivityRecommendRange(@Param("activityRecommendRanges") List<ActivityRecommendRange> activityRecommendRanges) throws ServiceException;
    
    /**
     * 添加推荐活动和商品关联
     * @return void  
     * @param activityRecommendGoods List<ActivityRecommendGoods>
     * @throws ServiceException
     * @author zhongy
     * @date 2016年7月18日
     */
    void insertBatchActivityRecommendGoods(@Param("activityRecommendGoods") List<ActivityRecommendGoods> activityRecommendGoods) throws ServiceException;
	
	/**
	 * 修改推荐活动
	 * @return void  
	 * @param activityRecommend ActivityRecommend
	 * @throws ServiceException
	 * @author zhongy
	 * @date 2016年7月18日
	 */
	void updateByPrimaryKeySelective(ActivityRecommend activityRecommend) throws ServiceException;
	
    /**
     * 删除推荐和区域关联关系
     * @return void  
     * @throws ServiceException
     * @author zhongy
     * @date 2016年7月18日
     */
    void deleteActivityRecommendRange(@Param("activityRecommendId") String activityRecommendId) throws ServiceException;
    
    /**
     * 删除推荐和标准商品库商品关系
     * @param activityRecommendId 推荐活动id
     * @return void  
     * @throws ServiceException
     * @author zhongy
     * @date 2016年7月18日
     */
    void deleteActivityRecommendGoods(@Param("activityRecommendId")  String activityRecommendId) throws ServiceException;
	
    /**
     *  根据ID 查询推荐活动
     * @return ActivityRecommend  
     * @param activityRecommendId 推荐活动id
     * @author zhongy
     * @date 2016年7月18日
     */
    ActivityRecommend selectByPrimaryKey(@Param("id") String id);
    
    /**
     * 查询推荐活动
     * @return List<ActivityRecommend>  返回结果
     * @param activityRecommend ActivityRecommend
     * @author zhongy
     * @date 2016年7月18日
     */
    List<ActivityRecommend> findActivityRecommend(ActivityRecommend activityRecommend);
    
    /**
     * 关闭推荐活动
     * @return void  
     * @param id 推荐活动id
     * @throws ServiceException
     * @author zhongy
     * @date 2016年7月18日
     */
    void closeActivityRecommend(@Param("id") String id) throws ServiceException;
	
	/**
	 * 根据推荐活动id查询推荐活动和区域关联
	 * @return List<ActivityRecommendRange> 返回查询结果
	 * @param activityRecommendId 推荐活动activityRecommendId
	 * @author zhongy
	 * @date 2016年7月18日
	 */
	List<ActivityRecommendRange> findActivityRecommendRange(@Param("activityRecommendId")  String activityRecommendId);
	
	/**
	 * 添加、修改推荐活动校验
	 * @return int 返回查询结果
	 * @param  params Map<String,Object>
	 * @author zhongy
	 * @date 2016年7月18日
	 */
	int selectCountByDistrict(@Param("params")Map<String,Object> params);
	
	/**
     * 根据用户定位城市名称查询推荐活动
     * @return ActivityRecommend 返回结果
     * @param params Map<String,Object>
     * @author zhongy
     * @date 2016年7月18日
     */
	ActivityRecommend findByCurrentCityActivity(@Param("params")Map<String,Object> params);
	
	/**
     * 查询范围为全国的推荐活动
     * @return ActivityRecommend 返回结果
     * @author zhongy
     * @date 2016年7月18日
     */
	ActivityRecommend findByNationActivity();
	
}
