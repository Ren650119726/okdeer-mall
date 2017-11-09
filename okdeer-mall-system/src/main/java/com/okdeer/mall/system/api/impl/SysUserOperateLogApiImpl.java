/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2017年11月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.system.api.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.system.dto.SysUserOperateLogDto;
import com.okdeer.archive.system.service.SysUserOperateLogApi;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.system.entity.SysUserOperateLog;
import com.okdeer.mall.system.service.SysUserOperateLogService;


/**
 * ClassName: SysUserOperateLogApiImpl 
 * @Description: TODO
 * @author xuzq01
 * @date 2017年11月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version="1.0.0")
public class SysUserOperateLogApiImpl implements SysUserOperateLogApi {

	@Autowired
	private SysUserOperateLogService sysUserOperateLogService;

	@Override
	public void addLog(SysUserOperateLogDto logDto) throws Exception {
		SysUserOperateLog entity = BeanMapper.map(logDto, SysUserOperateLog.class);
		sysUserOperateLogService.add(entity);
		
	}
}
