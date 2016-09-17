/** 
 *@Project: okdeer-mall-system 
 *@Author: wangf01
 *@Date: 2016年9月10日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */

package com.okdeer.mall.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.system.entity.SysUserInvitationRecord;
import com.okdeer.mall.system.entity.SysUserInvitationRecordVo;

/**
 * ClassName: SysUserInvitationRecordMapper 
 * @Description: 邀请码记录mapper
 * @author wangf01
 * @date 2016年9月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface SysUserInvitationRecordMapper {

	/**
	 * 
	 * @Description: 根据id获取信息
	 * @param id String
	 * @return entity SysUserInvitationCode
	 * @author wangf01
	 * @date 2016年9月10日
	 */
	SysUserInvitationRecord findById(@Param(value = "id") String id);

	/**
	 * 
	 * @Description: 根据参数获取信息
	 * @param params Map
	 * @return list List<SysUserInvitationRecord>
	 * @author wangf01
	 * @date 2016年9月10日
	 */
	@SuppressWarnings("rawtypes")
	List<SysUserInvitationRecordVo> findByParam(Map params);

	/**
	 * 
	 * @Description: 保存信息
	 * @param entity SysUserInvitationRecord
	 * @return flag Integer
	 * @author wangf01
	 * @date 2016年9月10日
	 */
	int save(SysUserInvitationRecord entity);

	/**
	 * 
	 * @Description: 修改信息
	 * @param entity SysUserInvitationRecord
	 * @return flag Integer
	 * @author wangf01
	 * @date 2016年9月10日
	 */
	int update(SysUserInvitationRecord entity);
}
