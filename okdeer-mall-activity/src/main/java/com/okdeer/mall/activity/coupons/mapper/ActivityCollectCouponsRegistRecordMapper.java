package com.okdeer.mall.activity.coupons.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsRegisteRecordVo;
import com.okdeer.base.dal.IBaseCrudMapper;

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
	List<ActivityCollectCouponsRegisteRecordVo> findByUserId(@Param("userQueryId")String userQueryId);
	
}
