/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * RiskSettingDetailMapper.java
 * @Date 2016-11-11 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.risk.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.risk.entity.RiskSettingDetail;

/**
 * ClassName: RiskSettingDetailMapper 
 * @Description: 风控设置明细
 * @author zhangkn
 * @date 2016年11月16日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.1			 2016年11月16日 			zhagnkn
 */
public interface RiskSettingDetailMapper extends IBaseMapper {
	
	/**
	 * @Description: 根据风控设置id加载数据
	 * @param settingId
	 * @return
	 * @throws Exception
	 * @author YSCGD
	 * @date 2016年11月16日
	 */
	List<RiskSettingDetail> listBySettingId(String settingId) throws Exception;
	
	/**
	 * @Description: 根据风控设置id删除数据
	 * @param settingId
	 * @throws Exception
	 * @author YSCGD
	 * @date 2016年11月16日
	 */
	void deleteBySettingId(String settingId) throws Exception;
	
}