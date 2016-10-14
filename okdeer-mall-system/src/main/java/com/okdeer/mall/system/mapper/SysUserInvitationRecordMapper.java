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
 *		v1.1.0			 2016年9月19日 			zhulq
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
	int updateCodeRecord(SysUserInvitationRecord sysUserInvitationRecord);
	
	/**
	 * 根据买家id查询邀请记录
	 * @param buyerUserId 买家用户Id
	 * @return 邀请记录
	 */
	SysUserInvitationRecord findInvitationRecordByUserId(@Param("buyerUserId") String buyerUserId);
	
	/**
	 * 
	 * @Description: 根据邀请人ID查询被邀请人首单列表 </p>
	 * @param buyerUserId
	 * @return
	 * @author yangq
	 * @date 2016年10月4日
	 */
	List<SysUserInvitationRecordVo> selectInvitationFirstOrderById(@Param("invitationCodeId") String invitationCodeId);
	
	/**
	 * @Description: 判断消费码消费时改下单的用户是否是首单
	 * @param orderId 订单id
	 * @return 数量
	 * @author zhulq
	 * @date 2016年10月12日
	 */
	SysUserInvitationRecord selectIdByOrderId(@Param("orderId") String orderId);
}
