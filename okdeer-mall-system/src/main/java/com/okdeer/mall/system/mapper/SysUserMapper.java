
package com.okdeer.mall.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.okdeer.archive.system.entity.SysUser;
import com.okdeer.base.dal.IBaseCrudMapper;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2015-11-13 19:03:02
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
@Repository(value = "sysUserMapper")
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

	// begin 根据用户id集合获取用户信息，拆分耦合性 add by wangf01
	/**
	 * 
	 * @Description: 根据id集合获取用户信息
	 * @param ids List<String>
	 * @return list List<SysUser>
	 * @author wangf01
	 * @date 2016年9月9日
	 */
	List<SysUser> findByIds(@Param(value = "ids") List<String> ids);
	// end 根据用户id集合获取用户信息，拆分耦合性 add by wangf01
}