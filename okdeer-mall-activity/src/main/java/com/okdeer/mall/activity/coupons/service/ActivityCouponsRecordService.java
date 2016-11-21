package com.okdeer.mall.activity.coupons.service;

import java.util.List;
import java.util.Map;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecordQueryVo;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecordVo;
import com.okdeer.mall.activity.coupons.entity.CouponsFindVo;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsType;

import net.sf.json.JSONObject;

/**
 * @DESC:
 * @author YSCGD
 * @date 2016-04-08 19:39:19
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface ActivityCouponsRecordService {

	/**
	 * 领取记录列表
	 * 
	 * @param activityCouponsRecordVo
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @throws ServiceException
	 */
	PageUtils<ActivityCouponsRecordVo> getAllRecords(ActivityCouponsRecordVo activityCouponsRecordVo, int pageNum,
			int pageSize) throws ServiceException;

	/**
	 * 导出领取记录信息
	 * 
	 * @param paraMap
	 * @return
	 */
	List<ActivityCouponsRecordVo> getRecordExportData(Map<String, Object> paraMap);

	/**
	 * 
	 * 根据代金券id、活动id、活动类型和领取人id，查询代金券领取记录信息
	 * 
	 * @author wusw
	 * @param couponsCollectRecord
	 * @return
	 */
	int selectCountByParams(ActivityCouponsRecord activityCouponsRecord) throws ServiceException;

	/**
	 * 
	 * 根据领取人id、领取状态，查询领取的代金券详细信息
	 * 
	 * @author wusw
	 * @param couponsCollectRecord
	 * @return
	 * @throws ServiceException
	 */
	List<ActivityCouponsRecordQueryVo> findMyCouponsDetailByParams(ActivityCouponsRecordStatusEnum status,
			String currentOperateUserId,Boolean flag) throws ServiceException;

	/**
	 * 
	 * 插入代金券领取记录
	 * 
	 * @author wusw
	 * @param couponsId
	 * @param currentOperatUserId
	 * @throws ServiceException
	 */
	JSONObject addRecordForRecevie(String couponsId, String currentOperatUserId,
			ActivityCouponsType activityCouponsType) throws ServiceException;

	/**
	 * DESC: 领取活动优惠券
	 * 
	 * @author LIU.W
	 * @param lstActivityCoupons
	 * @param activityCouponsType
	 * @param userId
	 * @throws ServiceException
	 */
	public void drawCouponsRecord(List<ActivityCoupons> lstActivityCoupons, ActivityCouponsType activityCouponsType,
			String userId) throws ServiceException;

	/**
	 * 
	 * 输入优惠码，插入代金券领取记录
	 * 
	 * @author wusw
	 * @param exchangeCode
	 * @param currentOperatUserId
	 * @return
	 * @throws ServiceException
	 */
	JSONObject addRecordForExchangeCode(Map<String, Object> params, String exchangeCode, String currentOperatUserId,
			ActivityCouponsType activityCouponsType) throws ServiceException;

	/**
	 * 根据领取人id、领取状态，店铺ID，查询领取的代金券详细信息
	 * 
	 * @author yangq
	 * @param couponsCollectRecord
	 * @return
	 */
	List<ActivityCouponsRecordQueryVo> selectCouponsDetailByStoreId(ActivityCouponsRecord activityCouponsRecord)
			throws Exception;

	/**
	 * 查询代金券具体金额
	 * </p>
	 * 
	 * @author yangq
	 * @param couponsFindVo
	 * @return
	 */
	ActivityCoupons selectCouponsItem(CouponsFindVo couponsFindVo) throws Exception;

	/**
	 * 定时更新代金券领取记录状态
	 *
	 */
	void updateStatusByJob() throws Exception;

	/**
	 * 定时更新代金券活动 退款状态 退钱
	 */
	void updateRefundStatus(List<ActivityCouponsRecordVo> couponsRecordVoList, String id) throws Exception;

	/**
	 * 订单更新代金卷状态
	 * @param orderId
	 */
	void updateUseStatus(String orderId);

	ActivityCouponsRecord selectByPrimaryKey(String id);
	
	void updateActivityCouponsStatus(Map<String,Object> params);
	
	/**
	 * 执行代金劵提醒JOB
	 * @Description: TODO   
	 * @return void  
	 * @throws
	 * @author tuzhd
	 * @date 2016年11月21日
	 */
	public void procesRecordNoticeJob();
}