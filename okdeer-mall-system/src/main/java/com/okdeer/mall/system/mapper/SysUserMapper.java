package com.okdeer.mall.system.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.okdeer.archive.system.entity.SysUser;
import com.yschome.base.dal.IBaseCrudMapper;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2015-11-13 19:03:02
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
@Repository(value="sysUserMapper")
public interface SysUserMapper extends IBaseCrudMapper {

	/**
	 * 根据登陆名字查询用户
	 *
	 * @param loginName
	 * @return
	 */
	SysUser selectUserByLoginName(String loginName);
	
	/**
	 * 根据登陆名字查询用户
	 *
	 * @param loginName
	 * @return
	 */
	SysUser selectUserById(String logId);
	
	
	/**
	 * 根据登陆ID查询用户
	 * 
	 * @param loginId
	 * @return
	 */
	SysUser getUserById(String loginId);
	
	/**
	 * 根据店铺ID查询店铺内的用户
	 * @author zengj
	 * @param storeId
	 * @return
	 */
	List<SysUser> selectUserByStoreId(String storeId);
	
}