package com.okdeer.mall.system.mapper;

import com.okdeer.archive.system.entity.SysBuyerUserUseLog;


/**
 * 手机用户使用日志 
 * @project yschome-psms
 * @author tangy
 * @date 2016年4月20日 下午7:05:05
 */
public interface SysBuyerUserUseLogMapper {

	/**
	 * 保存日志信息 
	 * @param sysBuyerUserUseLog  手机用户使用日志信息
	 * @return int
	 */
	int insertSysBuyerUserUseLog(SysBuyerUserUseLog sysBuyerUserUseLog);
	
}
