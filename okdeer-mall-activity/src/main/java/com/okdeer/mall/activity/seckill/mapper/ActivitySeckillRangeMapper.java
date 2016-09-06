/** 
 *@Project: yschome-mall-activity 
 *@Author: lijun
 *@Date: 2016年7月13日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */    

package com.okdeer.mall.activity.seckill.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.activity.seckill.entity.ActivitySeckillRange;

/**
 * ClassName: ActivitySeckillRangeMapper 
 * @Description: 秒杀活动范围mapper接口
 * @author lijun
 * @date 2016年7月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *      重构 4.1         2016年7月13日                                 lijun               新增
 *		重构 4.1         2016年7月20日                                 luosm              删除findByName方法
 */
public interface ActivitySeckillRangeMapper {
	
	/**
	 * @Description: 通过秒杀活动id获取秒杀活动范围信息
	 * @param seckillId 秒杀活动id
	 * @return List 返回范围列表集合
	 * @author lijun
	 * @date 2016年7月15日
	 */
	List<ActivitySeckillRange> findBySeckillId(@Param("seckillId") String seckillId);
	
	/**
	 * @Description: 批量保存区域信息
	 * @param seckillRangeList 区域信息list集合
	 * @return Integer 受影响行数
	 * @author lijun
	 * @date 2016年7月17日
	 */
	Integer addByBatch(@Param("seckillRangeList") List<ActivitySeckillRange> seckillRangeList);
	
	/**
	 * @Description: 根据活动id删除秒杀活动区域信息
	 * @param activityId 活动id
	 * @return Integer 受影响行数
	 * @author lijun
	 * @date 2016年7月18日
	 */
	Integer deleteByActivityId(@Param("activityId") String activityId);
}
