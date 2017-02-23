
package com.okdeer.mall.system.service;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.system.entity.SysUserInvitationCode;
import com.okdeer.mall.system.enums.InvitationUserType;

/**
 * ClassName: InvitationCodeService 
 * @Description: 邀请码管理service
 * @author zhulq
 * @date 2016年9月19日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.1			 2016年9月19日 	     zhulq
 *      V1.1.0           2016年9月28日                        zhaoqc          新增保存邀请码数据的方法    
 */
public interface InvitationCodeService {

	/**
	 * @Description: 保存
	 * @throws ServiceException 异常
	 * @author zhapqc
	 * @date 2016年9月28日
	 */
	void saveCode(SysUserInvitationCode sysUserInvitationCode) throws ServiceException;

	/**
	 * 根据用户Id查询邀请码信息
	 * @param userId 用户ID
	 * @param userType 用户类型
	 * @return 用户邀请码实体
	 */
	SysUserInvitationCode findInvitationCodeByUserId(String userId, InvitationUserType userType)
			throws ServiceException;

	// Begin V2.1.0 added by luosm 20170215
	/***
	 * 
	 * @Description: 根据用户id查询邀请人账户名
	 * @param userId
	 * @return
	 * @throws ServiceException
	 * @author luosm
	 * @date 2017年2月15日
	 */
	String findInvitationNameByUserId(String userId) throws ServiceException;
	// End V2.1.0 added by luosm 20170215
    
    /**
	 * 保存邀请码记录 
	 * @param invatitationInfo 邀请人邀请码记录
	 * @param userId 被邀请人id
	 * @param machineCode 机器编码
	 * @author tuzhd 2017-2-23修改添加机器编码
	 */
    public int saveInvatationRecord(SysUserInvitationCode invatitationInfo, String userId, String machineCode)throws Exception;

}
