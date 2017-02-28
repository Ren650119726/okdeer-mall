/** 
 *@Project: okdeer-mall-system 
 *@Author: tangzj02
 *@Date: 2017年2月27日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.system.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.system.entity.SysRandCodeRecord;
import com.okdeer.mall.system.mapper.SysRandCodeRecordMapper;
import com.okdeer.mall.system.service.SysRandCodeRecordService;

/**
 * ClassName: SysRandCodeRecordServiceImpl 
 * @Description: 随机码service
 * @author tangzj02
 * @date 2017年02月27日
 *
 * =================================================================================================
 *     Task ID            Date               Author           Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     V2.1           2017年02月27日                        tangzj02       添加
 */
@Service
public class SysRandCodeRecordServiceImpl implements SysRandCodeRecordService {

	@Autowired
	private SysRandCodeRecordMapper sysRandCodeRecordMapper;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.system.service.SysRandCodeRecordService#findRecordByRandCode()
	 */
	@Override
	public String findRecordByRandCode() throws ServiceException {
		SysRandCodeRecord record = sysRandCodeRecordMapper.getOneRandCode();
		if (null != record) {
			return record.getRandomCode();
		}
		return null;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.system.service.SysRandCodeRecordService#deleteRecordByRandCodeByCode(java.lang.String)
	 */
	@Override
	public void deleteRecordByRandCodeByCode(String code) throws ServiceException {
		SysRandCodeRecord record = sysRandCodeRecordMapper.findRecordByRandCode(code);
		if (null != record) {
			record.setUpdateTime(new Date());
			record.setDisabled(Disabled.invalid);
			sysRandCodeRecordMapper.updateSysRandCodeRecord(record);
		}

	}

}
