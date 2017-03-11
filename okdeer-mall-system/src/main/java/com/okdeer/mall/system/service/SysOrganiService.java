
package com.okdeer.mall.system.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.system.dto.SysOrganiQueryParamDto;
import com.okdeer.mall.system.entity.SysOrganization;

public interface SysOrganiService extends IBaseService {

	/**
	 * @Description: 查询列表
	 * @param paramDto 查询条件
	 * @return
	 * @author zengjizu
	 * @date 2017年3月11日
	 */
	List<SysOrganization> findList(SysOrganiQueryParamDto paramDto);

}
