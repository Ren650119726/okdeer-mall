package com.okdeer.mall.system.service;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.system.entity.SysBuyerFirstOrderRecord;

public interface SysBuyerFirstOrderRecordService extends IBaseService{

	/**
	 * @Description: 是否存在订单记录
	 * @param userId
	 * @return   
	 * @author maojj
	 * @date 2017年2月22日
	 */
	boolean isExistsOrderRecord(String userId);

	/**
	 * @Description: 通过用户id查询首单记录对象
	 * @param userId
	 * @return   
	 * @author xuzq01
	 * @date 2017年9月12日
	 */
	SysBuyerFirstOrderRecord findByUserId(String userId);
}
