/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2017年11月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.system.mapper.SysUserOperateLogMapper;
import com.okdeer.mall.system.service.SysUserOperateLogService;


/**
 * ClassName: SysUserOperateLogServiceImpl 
 * @Description: 系统用户操作日志表service实现
 * @author xuzq01
 * @date 2017年11月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class SysUserOperateLogServiceImpl extends BaseServiceImpl implements SysUserOperateLogService {

    @Autowired
	private SysUserOperateLogMapper sysUserOperateLogMapper;
    
	@Override
	public IBaseMapper getBaseMapper() {
		return sysUserOperateLogMapper;
	}

}
