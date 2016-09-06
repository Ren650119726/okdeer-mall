/** 
 *@Project: yschome-mall-operate 
 *@Author: luosm
 *@Date: 2016年7月18日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.column.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.operate.service.IServerColumnAreaServiceApi;
import com.okdeer.mall.operate.column.mapper.ServerColumnAreaMapper;
import com.okdeer.mall.operate.column.service.ServerColumnAreaService;


/**
 * ClassName: ServerColumnAreaServiceImpl 
 * @Description: 服务栏目区域service实现类
 * @author luosm
 * @date 2016年7月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.0			2016-07-18			luosm				新建类
 *		重构4.1			2016-07-19		    luosm				 优化
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.operate.service.IServerColumnAreaServiceApi")
public class ServerColumnAreaServiceImpl implements ServerColumnAreaService,IServerColumnAreaServiceApi {

	@Autowired
	private ServerColumnAreaMapper serverColumnAreaMapper;
	
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.column.service.ServerColumnAreaService#findByCityName(java.lang.String)
	 */
	@Override
	public List<String> findByCityName(String cityName,String provinceName) {
		return serverColumnAreaMapper.findByCityName(cityName,provinceName);
	}

}
