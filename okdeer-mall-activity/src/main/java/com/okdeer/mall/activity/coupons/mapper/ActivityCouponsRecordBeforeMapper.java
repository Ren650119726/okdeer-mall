package com.okdeer.mall.activity.coupons.mapper;


import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.dal.IBaseCrudMapper;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecordBefore;

/**
 * @DESC: 代金劵预领取记录操作类
 * @author 涂志定
 * @date  2016-11-9
 * @copyright ©2005-2020 okdeer.com Inc. All rights reserved
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.1.6		2016-11-9			tuzhiding			代金劵预领取记录
 */
public interface ActivityCouponsRecordBeforeMapper extends IBaseCrudMapper {

	
	List<ActivityCouponsRecordBefore> getCopyRecords(@Param("userId")String userId,
			@Param("nowDate")Date nowDate,@Param("phone")String phone) throws ServiceException;
	
	int getCountByDayParams(ActivityCouponsRecord activityCouponsRecord);
	
	/**
	 * @Description: 根据用户id判断完成首单的用户是否 有效完成邀请
	 * 1、是否完成首单
	 * 2、活动是否未结束
	 * @param userId 用户id
	 * @return List<ActivityCouponsRecord>  
	 * @author tuzhd
	 * @date 2016年12月13日
	 */
	List<ActivityCouponsRecordBefore> findRecordVaildByUserId(@Param("phone")String phone);
	
	/**
	 * 根据代金劵活动id代金劵预领取统计
	 * tuzhiding
	 * @param collectUser 用户信息
	 * @param collectId  代金劵活动id
	 * @return
	 */
	Integer countCouponsAllId(@Param("collectUser")String collectUser,@Param("collectId")String collectId);
	/**
	 * 
	 * @Description: 查询用户的邀请信息
	 * @param inviteUserId
	 * @return   
	 * @author xuzq01
	 * @date 2016年12月10日
	 */
	List<ActivityCouponsRecordBefore> findInviteInfoByInviteUserId(@Param("inviteUserId")String inviteUserId);
	
	/**
	 * 根据邀请号码查询 已成功邀请的次数
	 * tuzhiding
	 * @param collectUser 用户信息
	 * @param collectId  代金劵活动id
	 * @return
	 */
  	public Integer findInviteUserCount(@Param("inviteUserId")String inviteUserId,@Param("activityId")String activityId);
	
}