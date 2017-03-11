
package com.okdeer.mall.system.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.system.dto.SysOrganStoreDto;
import com.okdeer.mall.system.dto.SysOrganiDto;
import com.okdeer.mall.system.dto.SysOrganiQueryParamDto;
import com.okdeer.mall.system.entity.SysOrganization;
import com.okdeer.mall.system.service.SysOrganiApi;
import com.okdeer.mall.system.service.SysOrganiService;

@Service(interfaceName = "com.okdeer.mall.system.service.SysOrganiApi", interfaceClass = SysOrganiApi.class, version = "1.0.0")
public class SysOrganiApiImpl implements SysOrganiApi {

	@Autowired
	private SysOrganiService sysOrganiService;

	@Override
	public List<SysOrganiDto> findList(SysOrganiQueryParamDto paramDto) {
		List<SysOrganization> list = sysOrganiService.findList(paramDto);
		if (CollectionUtils.isNotEmpty(list)) {
			return BeanMapper.mapList(list, SysOrganiDto.class);
		}
		return new ArrayList<SysOrganiDto>();
	}

	@Override
	public void save(SysOrganiDto dto) throws Exception {
		int code = sysOrganiService.findMaxCode() + 1;
		dto.setCode(code);
		SysOrganization sysOrganization = BeanMapper.map(dto, SysOrganization.class);
		sysOrganiService.add(sysOrganization);
	}

	@Override
	public void update(SysOrganiDto dto) throws Exception {

	}

	@Override
	public SysOrganiDto findById(String id) throws Exception {
		SysOrganization sysOrganization = sysOrganiService.findById(id);
		if (sysOrganization != null) {
			return BeanMapper.map(sysOrganization, SysOrganiDto.class);
		}
		return new SysOrganiDto();
	}

	@Override
	public List<SysOrganStoreDto> findRelationStores(String orgId) {
		return null;
	}

	@Override
	public void saveRelationStores(List<SysOrganStoreDto> dtoList) throws Exception {

	}

}
