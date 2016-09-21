package com.okdeer.mall.system.mapper;

import java.util.List;

import com.okdeer.mall.system.entity.SysUserInvitationRecordVo;
import com.yschome.base.dal.IBaseCrudMapper;

/**
 * ClassName: SysUserInvitationRecordMapper 
 * @Description: 邀请记录mapper
 * @author zhulq
 * @date 2016年9月19日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.1			 2016年9月19日 			zhulq
 */
public interface SysUserInvitationRecordMapper extends IBaseCrudMapper{

	/**
	 * @Description: 获取邀请码记录列表
	 * @param invitationRecordVo 邀请码记录vo
	 * @return  结果集
	 * @author zhulq
	 * @date 2016年9月20日
	 */
	List<SysUserInvitationRecordVo> findByQueryRecordVo(SysUserInvitationRecordVo invitationRecordVo);
}
