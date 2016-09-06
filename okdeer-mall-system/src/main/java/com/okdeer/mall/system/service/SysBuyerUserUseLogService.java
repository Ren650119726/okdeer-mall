package com.okdeer.mall.system.service;

import com.okdeer.archive.system.entity.SysBuyerUserUseLog;

/**
 * 手机用户使用日志  
 * @project yschome-psms
 * @author tangy
 * @date 2016年4月20日 下午6:56:09
 */
public interface SysBuyerUserUseLogService {
	
	/**
	 * 保存日志信息 
	 * @param sysBuyerUserUseLog  手机用户使用日志信息
	 */
	void save(SysBuyerUserUseLog sysBuyerUserUseLog);

}
