package com.okdeer.mall.system.service;

import com.okdeer.base.service.IBaseService;

public interface SysBuyerFirstOrderRecordService extends IBaseService{

	/**
	 * @Description: 是否存在订单记录
	 * @param userId
	 * @return   
	 * @author maojj
	 * @date 2017年2月22日
	 */
	boolean isExistsOrderRecord(String userId);
}
