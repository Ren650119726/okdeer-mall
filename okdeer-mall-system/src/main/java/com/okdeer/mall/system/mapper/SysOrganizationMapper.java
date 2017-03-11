/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * SysOrganizationMapper.java
 * @Date 2017-03-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */

package com.okdeer.mall.system.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.system.dto.SysOrganiQueryParamDto;
import com.okdeer.mall.system.entity.SysOrganization;

public interface SysOrganizationMapper extends IBaseMapper {
	
	/**
	 * @Description: 根据条件查询列表
	 * @param paramDto 查询参数
	 * @return
	 * @author zengjizu
	 * @date 2017年3月11日
	 */
	public List<SysOrganization> findList(SysOrganiQueryParamDto paramDto);
}