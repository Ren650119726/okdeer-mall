
package com.okdeer.mall.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.system.dto.SysOrganiQueryParamDto;
import com.okdeer.mall.system.entity.SysOrganization;
import com.okdeer.mall.system.mapper.SysOrganizationMapper;
import com.okdeer.mall.system.service.SysOrganiService;

@Service
public class SysOrganiServiceImpl extends BaseServiceImpl implements SysOrganiService {

	@Autowired
	private SysOrganizationMapper sysOrganizationMapper;

	@Override
	public List<SysOrganization> findList(SysOrganiQueryParamDto paramDto) {
		return sysOrganizationMapper.findList(paramDto);
	}

	@Override
	public IBaseMapper getBaseMapper() {
		return sysOrganizationMapper;
	}

	@Override
	public int findMaxCode() {
		return sysOrganizationMapper.findMaxCode();
	}

}
