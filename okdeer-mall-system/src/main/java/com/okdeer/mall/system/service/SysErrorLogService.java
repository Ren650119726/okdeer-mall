/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2017年1月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.system.service;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.system.dto.SysErrorLogDto;
import com.okdeer.mall.system.entity.SysErrorLog;

/**
 * ClassName: SysErrorLogService 
 * @Description: TODO
 * @author xuzq01
 * @date 2017年1月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		   				2017年1月8日         	   xuzq01				系统错误日志
 */

public interface SysErrorLogService extends IBaseService {

	/**
	 * @Description: 获取列表
	 * @param sysErrorLog
	 * @return   
	 * @author xuzq01
	 * @date 2017年1月8日
	 */
	PageUtils<SysErrorLog> findList(SysErrorLogDto sysErrorLogDto,int pageNumber,int pageSize);

}
