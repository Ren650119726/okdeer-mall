/** 
 *@Project: yschome-mall-activity 
 *@Author: lijun
 *@Date: 2016年7月12日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */

package com.okdeer.mall.activity.seckill.service;

import java.util.List;
import java.util.Map;

import com.okdeer.mall.activity.seckill.entity.ActivitySeckill;
import com.okdeer.mall.activity.seckill.enums.SeckillStatusEnum;
import com.okdeer.mall.activity.seckill.vo.ActivitySeckillItemVo;

/**
 * ClassName: ActivitySeckillService 
 * @Description: 秒杀活动service接口
 * @author lijun
 * @date 2016年7月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *      重构 4.1         2016年7月13日                                 lijun               新增
 *		重构 4.1         2016年7月20日                                 luosm               新增方法
 *		重构 4.1         2016年7月22日                                 luosm               优化方法
 */
public interface ActivitySeckillService {

	/**
	 * @Description: 通过主键id获取秒杀活动信息
	 * @param id 主键id
	 * @return ActivitySeckill 秒杀活动实体
	 * @throws 抛出异常
	 * @author lijun
	 * @date 2016年7月14日
	 */
	ActivitySeckill findSeckillById(String id) throws Exception;

	/**
	 * @Description: 根据状态查询秒杀活动的集合
	 * @param param 参数信息
	 * @return List 秒杀活动集合
	 * @throws Exception 抛出异常
	 * @author lijun
	 * @date 2016年7月18日
	 */
	List<ActivitySeckill> findActivitySeckillByStatus(Map<String, Object> param) throws Exception;

	/**
	 * @Description: 更新秒杀活动状态
	 * @param id 主键id
	 * @param status 秒杀状态
	 * @return Integer 受影响行数
	 * @throws Exception 抛出异常
	 * @author lijun
	 * @date 2016年7月18日
	 */
	Integer updateSeckillStatus(String id, SeckillStatusEnum status) throws Exception;
	
	/**
	 * @Description: 活动结束(更新状态、释放库存、解除商品关系)
	 * @param activitySeckill 秒杀实体
	 * @throws Exception 抛出异常
	 * @author lijun
	 * @date 2016年7月30日
	 */
	void updateSeckillByEnd(ActivitySeckill activitySeckill) throws Exception;

	// begin add by luosm 2016-07-22
	/***
	 * 
	 * @Description: 根据城市id查询当前区域是否有秒杀活动
	 * @param cityId 城市id
	 * @return List 秒杀信息
	 * @throws Exception 抛出异常
	 * @author luosm
	 * @date 2016年7月20日
	 */
	List<ActivitySeckill> findByUserAppSecKillByCityId(String cityId) throws Exception;
	
	/**
	 * 
	 * @Description: 通过秒杀活动id查询，秒杀活动详情
	 * @param id id
	 * @return ActivitySeckillItemVo 秒杀信息
	 * @throws Exception 抛出异常
	 * @author luosm
	 * @date 2016年7月20日
	 */
	ActivitySeckillItemVo findAppUserSecKillBySeckill(String id) throws Exception;
	// end add by luosm 2016-07-22

	void saveByCloseSeckill(ActivitySeckill activitySeckill) throws Exception;
	
	/**
	 * @Description: 根据id列表查询
	 * @param idList id列表
	 * @return
	 * @author zengjizu
	 * @date 2017年7月27日
	 */
	List<ActivitySeckill> findByIds(List<String> idList);
}
