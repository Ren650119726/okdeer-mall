/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2017年1月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.system.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.operate.dto.SkinManagerDto;
import com.okdeer.mall.system.dto.SysErrorLogDto;
import com.okdeer.mall.system.entity.SysErrorLog;
import com.okdeer.mall.system.mapper.SysErrorLogMapper;
import com.okdeer.mall.system.service.SysErrorLogService;


/**
 * ClassName: SysErrorLogServiceImpl 
 * @Description: TODO
 * @author xuzq01
 * @date 2017年1月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		   				2017年1月8日         	   xuzq01				系统错误日志
 */
@Service
public class SysErrorLogServiceImpl extends BaseServiceImpl implements SysErrorLogService {
	/**
	 * 系统错误日志mapper
	 */
	@Autowired
	private SysErrorLogMapper sysErrorLogMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return sysErrorLogMapper;
	}

	@Override
	public PageUtils<SysErrorLog> findList(SysErrorLogDto sysErrorLogDto,int pageNumber,int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<SysErrorLog> result = sysErrorLogMapper.findList(sysErrorLogDto);
		if (result == null) {
			result = new ArrayList<SysErrorLog>();
		}
		return new PageUtils<SysErrorLog>(result);
	
	}
}
