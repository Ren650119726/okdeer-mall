package com.okdeer.mall.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.system.entity.SysUserInvitationRecord;
import com.okdeer.mall.system.entity.SysUserInvitationRecordVo;
import com.okdeer.base.dal.IBaseCrudMapper;

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
public interface SysUserInvitationRecordMapper extends IBaseCrudMapper {

	/**
	 * @Description: 获取邀请码记录列表
	 * @param invitationRecordVo 邀请码记录vo
	 * @return  结果集
	 * @author zhulq
	 * @date 2016年9月20日
	 */
	List<SysUserInvitationRecordVo> findByQueryRecordVo(SysUserInvitationRecordVo invitationRecordVo);
	
	/**
	 * @Description: 保存邀请记录
	 * @param sysUserInvitationRecord  sysUserInvitationRecord
	 * @author zhulq
	 * @date 2016年9月26日
	 */
	void saveCodeRecord(SysUserInvitationRecord sysUserInvitationRecord);
	
	/**
	 * @Description: 跟新记录信息
	 * @param sysUserInvitationRecord  sysUserInvitationRecord
	 * @author zhulq
	 * @date 2016年9月26日
	 */
	void updateCodeRecord(SysUserInvitationRecord sysUserInvitationRecord);
	
	/**
	 * 根据买家id查询邀请记录
	 * @param buyerUserId 买家用户Id
	 * @return 邀请记录
	 */
	List<SysUserInvitationRecord> findInvitationRecordByUserId(@Param("buyerUserId") String buyerUserId);
}
