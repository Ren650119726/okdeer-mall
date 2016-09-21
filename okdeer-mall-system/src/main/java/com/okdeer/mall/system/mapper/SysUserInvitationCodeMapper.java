package com.okdeer.mall.system.mapper;

import java.util.List;

import com.okdeer.mall.system.entity.SysUserInvitationCodeVo;
import com.yschome.base.dal.IBaseCrudMapper;

/**
 * ClassName: SysUserInvitationCodeMapper 
 * @Description: 用户邀请码mapper
 * @author zhulq
 * @date 2016年9月19日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.1			 2016年9月19日 			zhulq
 */
public interface SysUserInvitationCodeMapper extends IBaseCrudMapper{

	/**
	 * @Description: 获取邀请码列表
	 * @param sysUserInvitationCodeVo 邀请码vo
	 * @return 结果集
	 * @author zhulq
	 * @date 2016年9月20日
	 */
	List<SysUserInvitationCodeVo> findByQueryVo(SysUserInvitationCodeVo sysUserInvitationCodeVo);
}
