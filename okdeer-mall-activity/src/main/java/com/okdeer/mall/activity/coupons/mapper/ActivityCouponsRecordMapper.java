package com.okdeer.mall.activity.coupons.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseCrudMapper;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecordQueryVo;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecordVo;
import com.okdeer.mall.activity.coupons.entity.CouponsFindVo;
import com.okdeer.mall.activity.coupons.entity.CouponsStatusCountVo;
import com.okdeer.mall.order.vo.Coupons;
import com.okdeer.mall.order.vo.RechargeCouponVo;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-04-08 19:39:19
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构V4.1			2016-07-16			maojj			添加查找用户有效的代金券信息方法
 * 		V1.1.0			2016-09-19			wushp			V1.1.0
 *      V1.1.0          2016-09-21          zhaoqc          添加充值获取代金券的方法
 *		V1.1.0			2016-09-23			tangy			代金券指定分类使用
 *      V1.1.0			2016-10-15			yangq           查询邀请记录列表
 *      V1.1.0			2016-10-18			maojj			查询用户参与邀请注册送代金券领取的总奖励
 */
public interface ActivityCouponsRecordMapper extends IBaseCrudMapper {

	/**
	 * 得到代金卷领取记录
	 * @param activityCouponsRecordVo 对象
	 * @return 列表
	 */
	List<ActivityCouponsRecordVo> selectAllRecords(ActivityCouponsRecordVo activityCouponsRecordVo);

	/**
	 * 导出代金卷领取记录信息
	 * @param paraMap map参数
	 * @return 结果
	 */
	List<ActivityCouponsRecordVo> selectExportRecords(Map<String, Object> paraMap);

	/**
	 * 
	 * 根据代金券id、活动id、活动类型和领取人id，查询代金券领取记录信息 
	 * 
	 * @author wusw
	 * @param couponsCollectRecord
	 * @return
	 */
	int selectCountByParams(ActivityCouponsRecord activityCouponsRecord);

	/**
	 * 
	 * 根据领取人id、领取状态，查询领取的代金券详细信息
	 * 
	 * @author wusw
	 * @param couponsCollectRecord
	 * @return
	 */
	List<ActivityCouponsRecordQueryVo> selectMyCouponsDetailByParams(ActivityCouponsRecord activityCouponsRecord);

	/**
	 * 根据领取人id、领取状态，店铺ID，查询领取的代金券详细信息
	 * 
	 * @author yangq
	 * @param couponsCollectRecord
	 * @return
	 */
	List<ActivityCouponsRecordQueryVo> selectCouponsDetailByStoreId(ActivityCouponsRecord activityCouponsRecord);

	/**
	 * 查询代金券具体金额 </p>
	 * 
	 * @author yangq
	 * @param couponsFindVo
	 * @return
	 */
	ActivityCoupons selectCouponsItem(CouponsFindVo couponsFindVo);

	/**
	* 更新代金卷使用状态 
	*/
	Integer updateUseStatus(String orderId);

	/**
	* 更新代金卷状态为过期
	* @param orderId
	*/
	Integer updateUseStatusAndExpire(String orderId);

	/**
	* 
	* 查询所有记录
	*
	* @return
	*/
	List<ActivityCouponsRecord> selectAllForJob();

	public void updateAllByBatch(Map<String, Object> map);

	/**
	* 查询全国的代金券 </p>
	* 
	* @author yangq
	* @param userId
	* @return
	*/
	List<ActivityCouponsRecord> selectAllVendorById(String userId);

	/**
	 * 根据领取人id、领取状态，查询所有代金券
	 * 
	 * @author yangq
	 * @param activityCouponsRecord
	 * @return
	 */
	List<ActivityCouponsRecordQueryVo> selectCouponsAllId(ActivityCouponsRecord activityCouponsRecord);

	int updateActivityCouponsStatus(Map<String, Object> map);

	/**
	 * DESC: 批量插入代金券 
	 * @author LIU.W
	 * @param lstRecords
	 * @return
	 */
	public int insertSelectiveBatch(List<ActivityCouponsRecord> lstRecords);

	/**
	 * 
	 * 其他人领取改代金卷的总条数 
	 *
	 * @param activityCouponsRecord
	 * @return
	 */
	int selectOtherCountByParams(ActivityCouponsRecord activityCouponsRecord);

	/**
	 * @Description: 查找用户有效的优惠信息
	 * @param params 查询条件 
	 * @return List 有效的代金券列表
	 * @author maojj
	 * @date 2016年7月16日
	 */
	List<Coupons> findValidCoupons(Map<String, Object> params);

    //Begin 获取用户充值代金券列表 added by zhaoqc
    /**
     * @Description: 査取用户有效的手机充值优惠信息
     * @param params 查询条件 
     * @return List 有效的代金券列表
     * @author zhaoqc
     * @date 2016年9月23日
     */
    List<RechargeCouponVo> findValidRechargeCoupons(Map<String, Object> params);
    
    /**
     * @Description: 查询优惠券的信息
     * @param params 查询条件 
     * @return rechargeCouponVo 优惠券信息VO
     * @author zhaoqc
     * @date 2016年9月23日
     */
    RechargeCouponVo findRechargeCouponInfo(Map<String, String> params);
    //END added by zhaoqc
    
	//Begin 代金券领取列表优化 added by tangy  2016-8-17
	/**
	 * 
	 * @Description: 根据记录id查询订单相关信息
	 * @param recordIds  记录id集
	 * @return 
	 * @author tangy
	 * @date 2016年8月17日
	 */
	List<ActivityCouponsRecordVo> findOrderByRecordId(@Param("recordIds") List<String> recordIds);
	//End added by tangy 

	//begin add by wushp 20160919 V1.1.0
	/**
	 * @Description: 根据用户统计各种状态的代金券数量
	 * @param userId 用户id
	 * @return list
	 * @author wushp
	 * @date 2016年9月19日
	 */
	List<CouponsStatusCountVo> findStatusCountByUserId(@Param("userId") String userId);
	//end add by wushp 20160919 V1.1.0

	//Begin added by tangy  2016-9-23
	/**
	 * 
	 * @Description: 根据商品类目ids查询商品是否符合代金券指定分类
	 * @param skuIds     商品类目ids
	 * @param couponsId  代金券id
	 * @return int  
	 * @author tangy
	 * @date 2016年9月23日
	 */
	int findIsContainBySpuCategoryIds(@Param("spuCategoryIds") List<String> spuCategoryIds,
			@Param("couponsId") String couponsId);
	
	/**
	 * 
	 * @Description: 根据商品类目ids查询商品是否符合代金券指定分类
	 * @param spuCategoryIds  商品类目ids
	 * @param couponsId  代金券id
	 * @return int  
	 * @author tangy
	 * @date 2016年9月23日
	 */
	int findServerBySpuCategoryIds(@Param("spuCategoryIds") List<String> spuCategoryIds,
			@Param("couponsId") String couponsId);
	
	/**
	 * @Description: 根据代金券id获取类目名称集
	 * @param couponIds  代金券id集
	 * @return Map<String,Object>  
	 * @author tangy
	 * @date 2016年9月29日
	 */
	List<Map<String, Object>> findByCategoryNames(@Param("couponIds") List<String> couponIds);
	//End added by tangy
	
	/**
     * 
     * @Description: 保存注册邀请码注册 </p>
     * @param couponsRecord
     * @author yangq
     * @date 2016年10月12日
     */
    void saveActivityCouponsRecord(ActivityCouponsRecord couponsRecord);
    
    /**
     * 
     * @Description: 查询邀请记录列表
     * @param couponsRecord
     * @return
     * @author yangq
     * @date 2016年10月15日
     */
    List<ActivityCouponsRecord> selectAllRecordsByUserId(ActivityCouponsRecord couponsRecord);

    /**
     * 
     * @Description: 1.0.0 查询我的代金券
     * @param activityCouponsRecord 代金卷记录
     * @return List
     * @author zhulq
     * @date 2016年10月17日
     */
	List<ActivityCouponsRecordQueryVo> selectMyCouponsDetailByParamsOld(ActivityCouponsRecord activityCouponsRecord);
	
	// Begin added by maojj 2016-10-18
	/**
	 * @Description: 查询用户参与邀请送代金券领取的总奖励
	 * @param params
	 * @return   
	 * @author maojj
	 * @date 2016年10月18日
	 */
	Integer findTotalRewardAmount(Map<String,Object> params);
	// End added by maojj 2016-10-18
	/**
	 * zhulq:根据随机码查询代金券领取记录信息 
	 * @param randCode
	 * @return
	 */
	Integer selectCountByRandCode(String randCode);
	
}