package com.okdeer.mall.system.mapper;

import java.util.List;

import com.okdeer.archive.system.entity.SysUserRole;


public interface SysUserRoleMapper {

	/**
     * 
     * @desc 根据用户id获取用户拥有的 角色id集合
     *
     * @param userId 用户id
     * @return 角色id集合
     */
    List<String> getRoleIdList(String userId);
    
    /**
     * 根据用户id获取用户拥有的 角色id集合
     * @param sysUserRole
     * @return
     */
    List<String> findUserRPRole(SysUserRole sysUserRole);
    
    /**
     * 删除用户与角色的关联关系
     * @param sysUserRole
     * @return
     */
    int deleteRP(SysUserRole sysUserRole);
    
    /**
     * 创建用户与角色的关联关系
     * @param sysUserRole
     * @return
     */
    int insertRP(SysUserRole sysUserRole);
    
    List<SysUserRole> findSysUserRoleList(String userId);
	
    /**
     * 
     * @desc 查询用户拥有哪些角色 add by wulm
     *
     * @param userId
     * @return
     */
    List<String> findUserRoleByUserId(String userId);
    
}
