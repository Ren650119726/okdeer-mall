/** 
 *@Project: yschome-mall-activity 
 *@Author: zhongy
 *@Date: 2016年7月18日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */
package com.okdeer.mall.activity.recommend.service;

import java.util.List;

import com.okdeer.mall.activity.recommend.entity.ActivityRecommend;
import com.okdeer.mall.activity.recommend.entity.ActivityRecommendRange;
import com.okdeer.mall.activity.recommend.entity.ActivityRecommendVo;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;

/**
 * TODO 推荐活动服务接口类
 * @project yschome-mall
 * @author zhongy
 * @date 2016年7月13日 下午5:59:04
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构4.1          2016年7月18日                   zhongy             推荐活动mapper
 *
 */

public interface ActivityRecommendService {

    /**
     * 推荐活动分页查询 
     * @param activityRecommend 查询条件
     * @param pageNumber 当前页
     * @param pageSize 每页显示条数
     * @return 返回查询结果
     */
    PageUtils<ActivityRecommend> findByPage(ActivityRecommend activityRecommend,int pageNumber, int pageSize);
    
    /**
     * 根据条件查询推荐活动，用于job查询
     * @param activityRecommend
     * @return
     */
    List<ActivityRecommend> findList(ActivityRecommend activityRecommend);
    
    /**
     * 添加推荐活动
     * @param activityRecommendVo ActivityRecommendVo
     * @throws ServiceException
     */
    void save(ActivityRecommendVo activityRecommendVo) throws ServiceException; 
    
    /**
     * 修改推荐活动
     * @param activityRecommendVo ActivityRecommendVo
     * @throws ServiceException
     */
    void update(ActivityRecommendVo activityRecommendVo) throws ServiceException; 
    
    /**
     * 根据id查询推荐活动
     * @param id 请求参数
     * @return 返回查询结果
     */
    ActivityRecommend findById(String id);
    
    /**
     * 根据推荐活动id查询推荐活动和区域关联
     * @param activityRecommendId String
     * @return List 返回查询结果
     */
    List<ActivityRecommendRange> findActivityRecommendRange(String activityRecommendId);
    
    /**
     * 关闭推荐活动
     * @param id 请求参数
     * @throws ServiceException
     */
    void updateActivityRecommend(String id) throws ServiceException;
    
}
