package com.okdeer.mall.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.archive.system.entity.SysRole;



public interface SysRoleMapper {

	List<SysRole> selectByParams(@Param("params")Map<String,Object> params);
	
	int insertRole(SysRole sysRole);
	
	SysRole findSysRole(String id);
	
	int updateRole(SysRole sysRole);
	
	//List<SysRole> findRoleList(@Param("roleIds")List<String> roleIds);
	List<SysRole> findRoleList();
	
	/**
	 * 
	 * @desc 通过传入的实体进行查询
	 *
	 * @param sysRole 角色类
	 * @return 角色集合
	 */
	List<SysRole> findSelective(SysRole sysRole);
	
	/**
	 * 
	 * @desc 通过公司id查询角色，排除掉传入的角色id
	 *
	 * @return 角色集合
	 */
	List<SysRole> findRolesByCompanyIdExceptRoleIds(@Param("companyId") String companyId, @Param("ownRoleIdsAndAminRoleIds") List<String> ownRoleIdsAndAminRoleIds);
	
	/**
	 * 
	 * @desc 超级管理员查询运营商下的所有角色，除了超级管理员权限
	 *
	 * @param orgId
	 * @return
	 */
	List<SysRole> findRolesForSuperAdmin(@Param("orgId") String orgId,@Param("superRoleId") String superRoleId);

	/**
	 * 查询登陆用户拥有的角色,除了某些角色 add by Laven
	 * @param orgId
	 * @param exceptRoleIds
	 * @return
	 */
	List<SysRole> findRolesForSuperAdminExcept(@Param("orgId") String orgId,
										 @Param("exceptRoleIds") List<String> exceptRoleIds);

}
