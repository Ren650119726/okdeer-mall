package com.okdeer.mall.system.mapper;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.system.entity.SysBuyerFirstOrderRecord;

public interface SysBuyerFirstOrderRecordMapper extends IBaseMapper {

	/**
	 * @Description: 根据用户Id查询首单用户记录
	 * @param userId
	 * @return   
	 * @author maojj
	 * @date 2017年2月21日
	 */
	SysBuyerFirstOrderRecord findByUserId(String userId);
}