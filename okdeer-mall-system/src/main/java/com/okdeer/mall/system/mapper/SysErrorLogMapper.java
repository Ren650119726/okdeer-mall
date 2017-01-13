/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * SysErrorLogMapper.java
 * @Date 2017-01-08 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.system.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.system.dto.SysErrorLogDto;
import com.okdeer.mall.system.entity.SysErrorLog;

public interface SysErrorLogMapper extends IBaseMapper {

	/**
	 * @Description: TODO
	 * @param sysErrorLog
	 * @return   
	 * @author xuzq01
	 * @date 2017年1月8日
	 */
	List<SysErrorLog> findList(SysErrorLogDto sysErrorLogDto);

}