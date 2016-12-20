package com.okdeer.mall.points.service;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.points.entity.TeshSynchronLog;

/**
 * 
 * ClassName: TeshSynchronLogService 
 * @Description: 特奢汇同步日志
 * @author tangy
 * @date 2016年12月19日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     1.3.0          2016年12月19日                               tangy
 */
public interface TeshSynchronLogService {
	/**
	 * 
	 * @Description: 添加同步日志
	 * @param teshSynchronLog   日志信息
	 * @throws ServiceException   
	 * @return void  
	 * @author tangy
	 * @date 2016年12月19日
	 */
	public void addTeshSynchronLog(TeshSynchronLog teshSynchronLog) throws ServiceException;
	
	/**
	 * 
	 * @Description: 根据同步时间查询
	 * @param synchronTime
	 * @throws ServiceException   
	 * @return TeshSynchronLog  
	 * @author tangy
	 * @date 2016年12月19日
	 */
	public TeshSynchronLog findBySynchronTime(String synchronTime) throws ServiceException;
  
}
