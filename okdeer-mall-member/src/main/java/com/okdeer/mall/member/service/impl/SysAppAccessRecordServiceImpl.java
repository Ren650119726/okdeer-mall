/** 
 *@Project: okdeer-mall-member 
 *@Author: tangzj02
 *@Date: 2017年1月10日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.member.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.member.entity.SysAppAccessRecord;
import com.okdeer.mall.member.mapper.SysAppAccessRecordMapper;
import com.okdeer.mall.member.service.SysAppAccessRecordService;

/**
 * ClassName: SysAppAccessRecordService 
 * @Description: APP设备访问记录接口服务实现
 * @author tangzj02
 * @date 2017年1月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *       V2.0		  2017年1月10日                   tangzj02                     添加
 */
@Service
public class SysAppAccessRecordServiceImpl extends BaseServiceImpl implements SysAppAccessRecordService {

	@Autowired
	private SysAppAccessRecordMapper sysAppAccessRecordMapper;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.BaseServiceImpl#getBaseMapper()
	 */
	@Override
	public IBaseMapper getBaseMapper() {
		return sysAppAccessRecordMapper;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.member.service.SysAppAccessRecordService#save(com.okdeer.mall.member.entity.SysAppAccessRecord)
	 */
	@Override
	public int save(SysAppAccessRecord entity) {
		// 根据设备号、APP类型查询是否存在数据
		SysAppAccessRecord param = new SysAppAccessRecord();
		param.setAppType(entity.getAppType());
		param.setMachineCode(entity.getMachineCode());
		List<SysAppAccessRecord> list = sysAppAccessRecordMapper.findBySysAppAccessRecord(param);
		int result = 0;
		if (null == list || list.size() == 0) {
			entity.setId(UuidUtils.getUuid());
			entity.setCreateTime(DateUtils.getSysDate());
			result = sysAppAccessRecordMapper.add(entity);
		} else {
			entity.setCreateTime(null);
			entity.setId(list.get(0).getId());
			result = sysAppAccessRecordMapper.update(entity);
		}
		return result;
	}

}
