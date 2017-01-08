/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * SysBuyerExpectRecordMapper.java
 * @Date 2017-01-08 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.mapper;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;

public interface SysBuyerExpectRecordMapper extends IBaseMapper {
	
	/**
	 * 
	 * @Description: 根据machineCode查询提交参数
	 * @param machineCode
	 * @return int  
	 * @author tangy
	 * @date 2017年1月8日
	 */
	int findNumberByMachineCode(@Param("machineCode")String machineCode);

}