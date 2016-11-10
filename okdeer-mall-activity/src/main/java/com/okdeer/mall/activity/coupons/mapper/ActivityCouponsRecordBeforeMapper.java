package com.okdeer.mall.activity.coupons.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.dal.IBaseCrudMapper;

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

	
	int insertCopyRecords(@Param("userId")String userId) throws ServiceException;
	/**
	 * 根据代金劵活动id代金劵预领取统计
	 * tuzhiding
	 * @param collectUser 用户信息
	 * @param collectId  代金劵活动id
	 * @return
	 */
	Integer countCouponsAllId(@Param("collectUser")String collectUser,@Param("collectId")String collectId);
	
}