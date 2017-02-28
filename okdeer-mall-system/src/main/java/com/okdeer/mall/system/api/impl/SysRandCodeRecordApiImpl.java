/** 
 *@Project: okdeer-mall-system 
 *@Author: tangzj02
 *@Date: 2017年2月27日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.system.api.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.system.service.SysRandCodeRecordApi;
import com.okdeer.mall.system.service.SysRandCodeRecordService;

/**
 * ClassName: SysRandCodeRecordApiImpl 
 * @Description: 随机码service
 * @author tangzj02
 * @date 2017年2月27日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     V2.1           2017年02月27日                        tangzj02       添加
 */

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.system.service.SysRandCodeRecordApi")
public class SysRandCodeRecordApiImpl implements SysRandCodeRecordApi {

	@Autowired
	private SysRandCodeRecordService sysRandCodeRecordService;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.system.service.SysRandCodeRecordApi#findRecordByRandCode()
	 */
	@Override
	public String findRecordByRandCode() throws ServiceException {
		return sysRandCodeRecordService.findRecordByRandCode();
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.system.service.SysRandCodeRecordApi#deleteRecordByRandCodeByCode(java.lang.String)
	 */
	@Override
	public void deleteRecordByRandCodeByCode(String code) throws ServiceException {
		sysRandCodeRecordService.deleteRecordByRandCodeByCode(code);
	}

}
