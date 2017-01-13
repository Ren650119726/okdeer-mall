/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2017年1月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.system.api.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.system.dto.SysErrorLogDto;
import com.okdeer.mall.system.entity.SysErrorLog;
import com.okdeer.mall.system.service.SysErrorLogApi;
import com.okdeer.mall.system.service.SysErrorLogService;


/**
 * ClassName: SysErrorLogApiImpl 
 * @Description: TODO
 * @author xuzq01
 * @date 2017年1月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		   				2017年1月8日         	   xuzq01				系统错误日志
 */

@Service(version = "1.0.0")
public class SysErrorLogApiImpl implements SysErrorLogApi {
	
	/**
	 * 系统错误日志service
	 */
	@Autowired
	private SysErrorLogService sysErrorLogService;
	
	@Override
	public int insertLog(SysErrorLog sysErrorLog) throws Exception{
		sysErrorLog.setId(UuidUtils.getUuid());
		return sysErrorLogService.add(sysErrorLog);
	}
	
	@Override
	public PageUtils<SysErrorLog> findList(SysErrorLogDto sysErrorLogDto,int pageNumber,int pageSize) throws Exception{
		return sysErrorLogService.findList(sysErrorLogDto,pageNumber,pageSize);
	}
	
	@Override
	public SysErrorLog findById(String id) throws Exception{
		return sysErrorLogService.findById(id);
	}
}
