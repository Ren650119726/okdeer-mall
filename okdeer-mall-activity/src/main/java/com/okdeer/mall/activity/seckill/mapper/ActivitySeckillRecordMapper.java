/** 
 *@Project: yschome-mall-activity 
 *@Author: lijun
 *@Date: 2016年7月13日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.seckill.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.activity.seckill.entity.ActivitySeckillRecord;

/**
 * ClassName: ActivitySeckillRecordMapper 
 * @Description: TODO
 * @author lijun
 * @date 2016年7月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *      重构 4.1         2016年7月13日                                 lijun               新增
 *      重构 4.1         2016年7月14日                                 zengj               新增方法
 *      重构 4.1         2016年7月20日                                 luosm               新增方法
 * 		重构 4.1        2016年7月23日                                 	wushp             逻辑删除秒杀记录
 */
public interface ActivitySeckillRecordMapper {

	/**
	 * 统计买家总共的秒杀数量
	 * @Description: TODO
	 * @param params
	 * @return int 购买的秒杀数量
	 * @throws
	 * @author zengj
	 * @date 2016年7月14日
	 */
	int findSeckillCount(Map<String, Object> params);
	
	/**
	 * 新增秒杀活动买家购买记录
	 * @Description: TODO
	 * @param activitySeckillRecord   
	 * @return void  
	 * @throws
	 * @author zengj
	 * @date 2016年7月14日
	 */
	void add(ActivitySeckillRecord activitySeckillRecord);
	
	// begin add by wushp 
	/**
	 * 
	 * @Description: 根据秒杀id逻辑删除秒杀购买记录
	 * @param tradeOrderId 订单id
	 * @return int  影响行数
	 * @author wushp
	 * @date 2016年7月23日
	 */
	int updateStatusBySeckillId(@Param("tradeOrderId") String tradeOrderId);
	// end add by wushp
	
	//begin add by luosm 2016-07-20
	/***
	 * 
	 * @Description: 根据秒杀活动id统计秒杀已售数量
	 * @param seckillId
	 * @return
	 * @author luosm
	 * @date 2016年7月20日
	 */
	int findByActivitySeckillId(@Param("seckillId")String seckillId);
	//end add by luosm 2016-07-20
}
