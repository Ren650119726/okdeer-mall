/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * RiskSettingMapper.java
 * @Date 2016-11-11 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.risk.mapper;

import java.util.List;
import java.util.Map;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.risk.entity.RiskSetting;

/**
 * ClassName: RiskSettingMapper 
 * @Description: 风控设置
 * @author zhangkn
 * @date 2016年11月16日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.1			 2016年11月16日 			zhagnkn
 */
public interface RiskSettingMapper extends IBaseMapper {
	
	/**
	 * @Description: 风控设置列表
	 * @param params
	 * @return
	 * @throws Exception
	 * @author zhangkeneng
	 * @date 2016年11月16日
	 */
	List<RiskSetting> list(Map<String, Object> params) throws Exception;
	
	/**
	 * @Description: 根据isCoupon删除老数据
	 * @param isCoupon
	 * @throws Exception
	 * @author YSCGD
	 * @date 2016年11月16日
	 */
	void deleteByIsCoupon(Integer isCoupon) throws Exception;
}