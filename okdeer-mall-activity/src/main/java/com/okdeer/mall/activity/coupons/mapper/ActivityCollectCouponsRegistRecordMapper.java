package com.okdeer.mall.activity.coupons.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseCrudMapper;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsRegisteRecord;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsRegisteRecordVo;
import com.okdeer.mall.activity.coupons.vo.InvitationRegisterRecordVo;

/**
 * ClassName: ActivityCollectCouponsRegistRecordMapper 
 * @Description: 邀请注册记录
 * @author zhulq
 * @date 2016年9月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.1			 2016年9月18日 			zhulq
 *		V1.1.0			2016-10-15 			wushp			V1.1.0
 *		Bug:14408		 2016年10月17日 	    maojj			邀请注册送代金券活动首页显示邀请记录（包括成功邀请人数、获得奖励、被邀请人头像、是否完成首单）
 */
public interface ActivityCollectCouponsRegistRecordMapper extends IBaseCrudMapper {

	/**
	 * @Description: 邀请注册记录列表数据查询
	 * @param registeRecordVo  实体
	 * @return list
	 * @author zhulq
	 * @date 2016年9月18日
	 */
	List<ActivityCollectCouponsRegisteRecordVo> findRegisteRecord(ActivityCollectCouponsRegisteRecordVo registeRecordVo);

	/**
	 * @Description:导出邀请注册记录列表
	 * @param registeRecordVo 邀请注册记录VO
	 * @return List
	 * @author zhulq
	 * @date 2016年9月21日
	 */
	List<ActivityCollectCouponsRegisteRecordVo> findRegisteRecordForExport(ActivityCollectCouponsRegisteRecordVo registeRecordVo);
	
	/**
	 * @Description: 某个账号的邀请记录详情
	 * @param userQueryId 账号id
	 * @return  list
	 * @author zhulq
	 * @date 2016年9月18日
	 */
	List<ActivityCollectCouponsRegisteRecordVo> findByUserId(@Param("userQueryId")String userQueryId,@Param("activityId")String activityId);
	
	/**
	 * 
	 * @Description: 查询当前注册送代金券面额 </p>
	 * @return
	 * @author yangq
	 * @date 2016年10月4日
	 */
	int selectActivityCouponsFaceValue();
	
	/**
	 * 
	 * @Description: 查询邀请的人数 </p>
	 * @param userId
	 * @return
	 * @throws Exception
	 * @author yangq
	 * @date 2016年10月4日
	 */
	int selectInvitationNum(String userId) ; 

	void saveRecord(ActivityCollectCouponsRegisteRecord registRecord);
	
	// begin add by wushp 20161015
	/**
	 * 
	 * @Description: 根据被邀请人查询邀请注册记录
	 * @param inviteId 被邀请人用户id
	 * @return ActivityCollectCouponsRegisteRecord
	 * @author wushp
	 * @date 2016年10月15日
	 */
	ActivityCollectCouponsRegisteRecord selectByInviteId(String inviteId);
	// end add by wushp 20161015
	
	// Begin Bug:14408 added by maojj 2016-10-17
	List<InvitationRegisterRecordVo> findInviteRegisterRecord(@Param("userId")String userId,@Param("activityId")String activityId);
	// End Bug:14408 added by maojj 2016-10-17

}
