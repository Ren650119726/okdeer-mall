package com.okdeer.mall.system.mapper;

import java.util.List;

import com.okdeer.archive.system.entity.SysRolePermission;


public interface SysRolePermissionMapper {

	int deleteRP(SysRolePermission sysRolePermission);
	
	int insert(SysRolePermission sysRolePermission);
	
	/**
	 * 查询角色对应的资源权限
	 * @param roleId
	 * @return
	 */
	List<String> findRoleRPPermission(String roleId);
	
}
