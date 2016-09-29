/** 
 *@Project: yschome-mall-activity 
 *@Author: lijun
 *@Date: 2016年7月13日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */

package com.okdeer.mall.activity.seckill.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.activity.seckill.entity.ActivitySeckill;
import com.okdeer.mall.activity.seckill.enums.SeckillStatusEnum;
import com.okdeer.mall.activity.seckill.vo.ActivitySeckillItemVo;
import com.okdeer.mall.activity.seckill.vo.ActivitySeckillListPageVo;
import com.okdeer.mall.activity.seckill.vo.ActivitySeckillQueryFilterVo;

/**
 * ClassName: ActivitySeckillMapper 
 * @Description: 秒杀活动mapper接口
 * @author lijun
 * @date 2016年7月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *      重构 4.1         2016年7月13日                                 lijun               新增
 *      重构 4.1         2016年7月14日                                 zengj               根据主键获取秒杀活动信息
 *      重构 4.1         2016年7月16日                                 zengj               查询定位地址是否在秒杀活动范围
 *      重构 4.1         2016年7月22日                                 luosm               优化方法
 *
 */
public interface ActivitySeckillMapper {

	/**
	 * @Description: 秒杀活动列表页查询 
	 * @param activitySeckillQueryFilterVo 秒杀活动查询条件VO类
	 * @return List 结果集
	 * @author lijun
	 * @date 2016年7月13日
	 */
	List<ActivitySeckillListPageVo> findListPageByFilter(ActivitySeckillQueryFilterVo activitySeckillQueryFilterVo);

	/**
	 * @Description: 通过主键id获取秒杀活动信息
	 * @param id 主键id
	 * @return ActivitySeckill 秒杀活动实体
	 * @author lijun
	 * @date 2016年7月14日
	 */
	ActivitySeckill findByPrimaryKey(@Param("id") String id);

	/**
	 * @Description: 更新秒杀活动状态
	 * @param id 主键id
	 * @param status 活动状态枚举
	 * @return Integer 返回影响行数
	 * @author lijun
	 * @date 2016年7月14日
	 */
	Integer updateSeckillStatus(@Param("id") String id, @Param("status") SeckillStatusEnum status);

	/**
	 * @Description: 通过主键id获取秒杀详情信息
	 * @param id 活动主键id 
	 * @return ActivitySeckillListPageVo 秒杀活动详情Vo
	 * @author lijun
	 * @date 2016年7月15日
	 */
	ActivitySeckillListPageVo findDetailByPrimaryKey(@Param("id") String id);

	/**
	 * @Description: 同一个区域同一个时间范围只能有一个秒杀活动，查询现有的活动记录数
	 * @param params Map参数
	 * @return Integer 记录数
	 * @author lijun
	 * @date 2016年7月17日
	 */
	Integer findSeckillCountByRange(@Param("params") Map<String, Object> params);

	/**
	 * @Description: 保存秒杀活动信息
	 * @param activitySeckill 实体类
	 * @return Integer 受影响行数
	 * @author lijun
	 * @date 2016年7月17日
	 */
	Integer add(ActivitySeckill activitySeckill);

	/**
	 * @Description: 更新秒杀活动信息
	 * @param activitySeckill 实体类
	 * @return Integer 受影响行数
	 * @author lijun
	 * @date 2016年7月17日
	 */
	Integer updateByPrimaryKeySelective(ActivitySeckill activitySeckill);

	/**
	 * @Description: 根据状态查询秒杀活动的集合
	 * @param param 参数信息
	 * @return List 秒杀活动集合
	 * @author lijun
	 * @date 2016年7月18日
	 */
	List<ActivitySeckill> findActivitySeckillByStatus(@Param("params") Map<String, Object> param);

	// Begin 重构4.1 add by zengj
	/**
	 * 
	 * @Description: 查询定位地址是否在秒杀活动范围
	 * @param params 查询参数
	 * @return ActivitySeckill 秒杀活动实体  
	 * @author zengj
	 * @date 2016年7月16日
	 */
	ActivitySeckill findSecKillByCityId(Map<String, Object> params);

	/**
	 * @Description: 查询商品在定位地址是否存在秒杀活动
	 * @param params 查询参数
	 * @return ActivitySeckill 秒杀活动实体  
	 * @author zengj
	 * @date 2016年7月16日
	 */
	ActivitySeckill findSecKillByGoodsId(Map<String, Object> params);
	// End 重构4.1 add by zengj

	// begin update by luosm 2016-07-22
	/***
	 * 
	 * @Description: App首页根据城市名查询当前区域是否有秒杀活动
	 * @param cityId 城市id
	 * @return List 秒杀信息
	 * @author luosm
	 * @date 2016年7月20日
	 */
	List<ActivitySeckill> findAppUserSecKillByCityId(@Param("cityId") String cityId);
	
	/***
	 * 
	 * @Description: App首页根据城市名查询当前区域是否有秒杀活动集合 </p>
	 * @param cityId 城市id
	 * @return List 秒杀信息
	 * @author luosm
	 * @date 2016年7月20日
	 */
	List<ActivitySeckill> findAppUserSecKillListByCityId(@Param("cityId") String cityId);
	

	/**
	 * 
	 * @Description: 通过秒杀活动id查询，秒杀活动详情
	 * @param id id
	 * @return ActivitySeckillItemVo 秒杀信息
	 * @author luosm
	 * @date 2016年7月20日
	 */
	ActivitySeckillItemVo findAppUserSecKillBySeckill(@Param("id") String id);
	// end add by luosm 2016-07-22
}
