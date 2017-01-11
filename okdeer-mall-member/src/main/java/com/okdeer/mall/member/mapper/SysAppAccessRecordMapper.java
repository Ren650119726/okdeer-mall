/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * SysAppAccessRecordMapper.java
 * @Date 2017-01-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.member.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.member.entity.SysAppAccessRecord;

public interface SysAppAccessRecordMapper extends IBaseMapper {

	/**
	 * @Description: 查询是否已经存在相应的设备记录号
	 * @param param 查询参数
	 * @return List<SysAppAccessRecord>  
	 * @author tangzj02
	 * @date 2017年1月10日
	 */
	List<SysAppAccessRecord> findBySysAppAccessRecord(SysAppAccessRecord param);

}