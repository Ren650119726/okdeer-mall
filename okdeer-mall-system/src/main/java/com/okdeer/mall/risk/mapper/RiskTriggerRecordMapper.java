/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * RiskTriggerRecordMapper.java
 * @Date 2016-11-17 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.risk.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.risk.entity.RiskTriggerRecord;

public interface RiskTriggerRecordMapper extends IBaseMapper {

	/**
	 * @Description: 风控记录列表 运营后台用
	 * @param params
	 * @return
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年11月17日
	 */
	 List<RiskTriggerRecord> list(Map<String,Object> params) throws Exception;
	
	/**
	 * @Description: 批量删除
	 * @param ids
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年11月17日
	 */
	void deleteBatch(@Param("ids") List<String> ids) throws Exception;
}