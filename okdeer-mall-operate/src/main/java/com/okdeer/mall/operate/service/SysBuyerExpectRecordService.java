package com.okdeer.mall.operate.service;

import com.okdeer.base.service.IBaseService;

/**
 * 
 * ClassName: SysBuyerExpectRecordService 
 * @Description: 用户期待记录
 * @author tangy
 * @date 2017年1月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     2.0.0          2017年1月8日                               tangy
 */
public interface SysBuyerExpectRecordService extends IBaseService {

	/**
	 * 
	 * @Description: 根据machineCode查询提交参数
	 * @param machineCode
	 * @return int  
	 * @author tangy
	 * @date 2017年1月8日
	 */
	int findNumberByMachineCode(String machineCode);
	
}
