/** 
 *@Project: yschome-mall-activity 
 *@Author: lijun
 *@Date: 2016年7月13日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */    

package com.okdeer.mall.activity.seckill.service;

import java.util.List;

import com.okdeer.mall.activity.seckill.entity.ActivitySeckillRange;

/**
 * ClassName: ActivitySeckillRangeService 
 * @Description: 秒杀活动范围service接口
 * @author lijun
 * @date 2016年7月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *      重构 4.1         2016年7月13日                                 lijun               新增
 *		重构 4.1         2016年7月19日                                 luosm               删除findByName方法
 *		重构 4.1         2016年7月27日                                 zengj               添加根据秒杀活动获取秒杀范围信息
 */
public interface ActivitySeckillRangeService {
	/**
	 * @Description: 获取秒杀活动范围信息
	 * @param seckillId 秒杀活动Id 
	 * @return List  范围集合
	 * @throws Exception 抛出异常
	 * @author zengj
	 * @date 2016年7月27日
	 */
	List<ActivitySeckillRange> findSeckillRangeAllBySeckillId(String seckillId) throws Exception;
}
