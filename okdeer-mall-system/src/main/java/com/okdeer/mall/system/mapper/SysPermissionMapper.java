package com.okdeer.mall.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.archive.system.entity.SysPermission;

/**
 * DESC: 系统菜单权限
 * @author LIU.W
 * @DATE 2015年7月29日上午10:58:35
 * @version 0.1.0
 * @copyright www.yschome.com
 */
public interface SysPermissionMapper {
	
	/**
	 * 
	 * @desc 查询全部权限资源，只给超级管理员使用
	 *
	 * @return 权限资源集合
	 */
	List<SysPermission> findAll();
	
    /**
     * @desc 全字段添加
     * @param record 
     * @return	执行条数
     */
    int insert(SysPermission record);

    /**
     * @desc 可选字段添加
     * @param record	
     * @return	执行条数
     */
    int insertSelective(SysPermission record);

    /**
     * @desc 通过主键查询
     * @param id 主键
     */
    SysPermission selectByPrimaryKey(String id);

    /**
     * @desc 可选字段根据主键更新
     * @return	执行条数
     */
    int updateByPrimaryKeySelective(SysPermission record);

    /**
     * @desc 全字段根据主键更新
     * @param record 
     * @return 执行条数
     */
    int updateByPrimaryKey(SysPermission record);
    
    /**
	 * @desc 根据主键删除
	 * @param id 主键
	 * @return	执行条数
	 */
    int deleteByPrimaryKey(String id);
	
    
    List<SysPermission> selectByParams(@Param("params")Map<String,Object> params);
    
    /**
     * DESC: 仅查询所有菜单类型的菜单数据
     * @return List<SysPermission>
     */
    List<SysPermission> selectAllMenu();

    /**
     * 通过父菜单ID，查询子菜单or功能 add by Laven
     * @param pid
     * @return
     */
    List<SysPermission> selectByPID(@Param("pid") String pid);
    
    /**
     * @desc 根据菜单id查询下级菜单信息
     * @param pid 菜单id
     * @return 下级菜单信息
     */
    List<SysPermission> selectMenuById(@Param("pid") String pid);
    
    /**
     * DESC: 查询菜单下的功能操作权限数据
     * @return List<SysPermission>
     */
    List<SysPermission> selectMenuOperation(String pid);
    
    /**
     * DESC: 查询用户角色权限
     * @param userId
     * @return
     */
    List<SysPermission> selectByUserId(String userId);
    
    List<SysPermission> findByCompanyId(String companyId);
    
    
    
    List<String> selectById(String userId);
    
    
}
