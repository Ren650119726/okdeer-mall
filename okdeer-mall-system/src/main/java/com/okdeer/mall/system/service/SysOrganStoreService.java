
package com.okdeer.mall.system.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.system.entity.SysOrganStore;

public interface SysOrganStoreService extends IBaseService {

	int batchAdd(List<SysOrganStore> list);

	List<SysOrganStore> findByOrgId(String orgId);
}
