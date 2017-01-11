/** 
 *@Project: okdeer-mall-member 
 *@Author: tangzj02
 *@Date: 2017年1月10日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.member.api;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.member.entity.SysAppAccessRecord;
import com.okdeer.mall.member.member.entity.SysAppAccessRecordDto;
import com.okdeer.mall.member.member.service.SysAppAccessRecordApi;
import com.okdeer.mall.member.service.SysAppAccessRecordService;

/**
 * ClassName: SysAppAccessRecordApiImpl 
 * @Description: APP设备访问记录接口实现
 * @author tangzj02
 * @date 2017年1月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *       V2.0		  2017年1月10日                   tangzj02                     添加
 */

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.member.member.service.SysAppAccessRecordApi")
public class SysAppAccessRecordApiImpl implements SysAppAccessRecordApi {

	@Autowired
	private SysAppAccessRecordService SysAppAccessRecordService;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.member.member.service.SysAppAccessRecordApi#save(com.okdeer.mall.member.member.entity.SysAppAccessRecordDto)
	 */
	@Override
	public int save(SysAppAccessRecordDto dto) throws Exception {
		if (null == dto) {
			return 0;
		}
		SysAppAccessRecord entity = BeanMapper.map(dto, SysAppAccessRecord.class);
		return SysAppAccessRecordService.save(entity);
	}

}
