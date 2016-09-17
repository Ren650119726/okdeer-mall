/** 
 *@Project: okdeer-archive-system 
 *@Author: wangf01
 *@Date: 2016年9月8日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */

package com.okdeer.mall.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.system.entity.SysUserInvitationCode;
import com.okdeer.mall.system.entity.SysUserInvitationCodeVo;

/**
 * ClassName: SysUserInvitationCode 
 * @Description: 邀请码mapper
 * @author wangf01
 * @date 2016年9月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface SysUserInvitationCodeMapper {

	/**
	 * 
	 * @Description: 根据id获取信息
	 * @param id String
	 * @return entity SysUserInvitationCode
	 * @author wangf01
	 * @date 2016年9月8日
	 */
	SysUserInvitationCode findById(@Param(value = "id") String id);

	/**
	 * 
	 * @Description: 根据sysUserId获取信息
	 * @param sysUserId String
	 * @return entity SysUserInvitationCode
	 * @author wangf01
	 * @date 2016年9月8日
	 */
	SysUserInvitationCode findBySysUserId(@Param(value = "sysUserId") String sysUserId);

	/**
	 * 
	 * @Description: 根据sysBuyerUserId获取信息
	 * @param sysBuyerUserId String
	 * @return entity SysUserInvitationCode
	 * @author wangf01
	 * @date 2016年9月8日
	 */
	SysUserInvitationCode findBySysBuyerUserId(@Param(value = "sysBuyerUserId") String sysBuyerUserId);

	/**
	 * 
	 * @Description: 根据参数获取信息
	 * @param params Map
	 * @return list List<SysUserInvitationCode>
	 * @author wangf01
	 * @date 2016年9月8日
	 */
	@SuppressWarnings("rawtypes")
	List<SysUserInvitationCodeVo> findByParam(Map params);

	/**
	 * 
	 * @Description: 保存信息
	 * @param entity SysUserInvitationCode
	 * @return flag Integer
	 * @author wangf01
	 * @date 2016年9月8日
	 */
	int save(SysUserInvitationCode entity);

	/**
	 * 
	 * @Description: 修改信息
	 * @param entity SysUserInvitationCode
	 * @return flag Integer
	 * @author wangf01
	 * @date 2016年9月8日
	 */
	int update(SysUserInvitationCode entity);
}
