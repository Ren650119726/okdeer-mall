package com.okdeer.mall.points.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.points.entity.TeshSynchronLog;
import com.okdeer.mall.points.mapper.TeshSynchronLogMapper;
import com.okdeer.mall.points.service.TeshSynchronLogService;

/**
 * 
 * ClassName: TeshSynchronLogServiceImpl 
 * @Description: 同步日志
 * @author tangy
 * @date 2016年12月19日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     1.3.0          2016年12月19日                               tangy
 */
@Service
public class TeshSynchronLogServiceImpl implements TeshSynchronLogService {
	
	/**
	 * 自动注入teshSynchronLogMapper
	 */
	@Autowired
	private TeshSynchronLogMapper teshSynchronLogMapper;

	@Override
	public void addTeshSynchronLog(TeshSynchronLog teshSynchronLog) throws ServiceException {
		teshSynchronLogMapper.insert(teshSynchronLog);
	}

	@Override
	public TeshSynchronLog findBySynchronTime(String synchronTime) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
