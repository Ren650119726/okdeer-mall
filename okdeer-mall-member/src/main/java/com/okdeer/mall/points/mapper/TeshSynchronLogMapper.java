package com.okdeer.mall.points.mapper;

import com.okdeer.mall.points.entity.TeshSynchronLog;

/**
 * 
 * ClassName: TeshSynchronLogMapper 
 * @Description: 积分商品同步日志
 * @author tangy
 * @date 2016年12月19日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     1.3.0          2016年12月19日                               tangy
 */
public interface TeshSynchronLogMapper {
	/**
	 * 
	 * @Description: 插入同步日志
	 * @param teshSynchronLog   日志信息
	 * @return void  
	 * @author tangy
	 * @date 2016年12月19日
	 */
	void insert(TeshSynchronLog teshSynchronLog);

}
