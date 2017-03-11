package com.okdeer.mall.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.system.entity.SysOrganStore;
import com.okdeer.mall.system.mapper.SysOrganStoreMapper;
import com.okdeer.mall.system.service.SysOrganStoreService;

@Service
public class SysOrganStoreServiceImpl extends BaseServiceImpl implements SysOrganStoreService {

	@Autowired
	SysOrganStoreMapper sysOrganStoreMapper;
	
	
	@Override
	public int batchAdd(List<SysOrganStore> list) {
		return sysOrganStoreMapper.batchAdd(list);
	}
	

	@Override
	public List<SysOrganStore> findByOrgId(String orgId) {
		return sysOrganStoreMapper.findByOrgId(orgId);
	}
	
	@Override
	public IBaseMapper getBaseMapper() {
		return sysOrganStoreMapper;
	}


}
