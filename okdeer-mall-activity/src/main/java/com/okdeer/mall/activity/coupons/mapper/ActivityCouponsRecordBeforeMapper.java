package com.okdeer.mall.activity.coupons.mapper;


import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.dal.IBaseCrudMapper;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecordBefore;
import com.okdeer.mall.activity.dto.ActivityCouponsRecordBeforeParamDto;
import com.okdeer.mall.common.enums.GetUserType;

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
	
	int getCountByDayParams(ActivityCouponsRecordBeforeParamDto activityCouponsRecordBeforeParam);
	
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
	 * 查询该用户已领取， 新人限制， 未使用，的代金劵活动的代金劵数量   
	 * tuzhiding
	 * @param collectUser 用户信息
	 * @param collectId  代金劵活动id
	 * @return
	 */
	Integer countCouponsByType(@Param("getUserType")GetUserType getUserType,@Param("collectUser")String collectUser,@Param("nowDate")Date nowDate);
	
	
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
  	Integer findInviteUserCount(@Param("inviteUserId")String inviteUserId,@Param("activityId")String activityId);
  	
  	
  	/**
	 * @Description: 根据活动统计用户领取次数
	 * @param activityCouponsRecordQueryParamDto
	 * @return
	 * @author zengjizu
	 * @date 2017年8月26日
	 */
	int selectActivityCountByParams(ActivityCouponsRecordBeforeParamDto activityCouponsRecordBeforeParam);
	
	/**
	 * @Description: 根据活动统计用户领取次数
	 * @param activityCouponsRecordQueryParamDto
	 * @return
	 * @author zengjizu
	 * @date 2017年8月26日
	 */
	int selectOrderCountByParams(ActivityCouponsRecordBeforeParamDto activityCouponsRecordBeforeParam);
	
	/**
	 * @Description: 批量插入代金券
	 * @param list
	 * @author tuzhd
	 * @date 2017年11月24日
	 */
	int insertSelectiveBatch(List<ActivityCouponsRecordBefore> list);
}