/** 
 *@Project: yschome-mall-operate 
 *@Author: luosm
 *@Date: 2016年7月18日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.column.service;

import java.util.List;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.operate.entity.ServerColumnArea;

/**
 * ClassName: ServerColumnAreaService 
 * @Description: 服务栏目区域service
 * @author luosm
 * @date 2016年7月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *	        重构4.0			2016-07-18			luosm			新建类
 *	       重构4.1			2016-07-19		    luosm		                优化findByCityName方法
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.operate.service.IServerColumnAreaServiceApi")
public interface ServerColumnAreaService {

	/***
	 * 
	 * @Description: 根据城市名查询服务栏目id
	 * @param cityName
	 * @return
	 * @author luosm
	 * @date 2016年7月18日
	 */
	List<String> findByCityName(String cityName,String provinceName);
}
