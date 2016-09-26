package com.okdeer.mall.activity.discount.mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountCondition;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountDto;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountQueryVo;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountVo;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountStatus;
import com.okdeer.mall.common.enums.AreaType;
import com.okdeer.mall.order.vo.Discount;
import com.okdeer.mall.order.vo.FullSubtract;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.dal.IBaseCrudMapper;

/**
 * 店铺活动mapper
 * @pr yscm
 * @desc 店铺活动mapper
 * @author zengj
 * @date 2016年1月22日 下午3:05:17
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构V4.1			2016-07-14			maojj			查询满减满折折扣金额
 *		重构V4.1			2016-07-16			maojj			查询用户有效的满折
 *		重构V4.1 			2016-07-16			maojj			查询用户有效的满减
 *		重构V4.1 			2016-07-22			zengj			查询店铺有效的满减
 */
public interface ActivityDiscountMapper extends IBaseCrudMapper {
	/**
	 * 
	 * @desc 添加活动
	 *
	 * @param activityDiscount 活动对象
	 */
	void insertActivityDiscount(ActivityDiscount activityDiscount);
	
	/**
	 * 
	 * @desc 修改活动
	 *
	 * @param activityDiscount 活动对象
	 */
	void updateActivityDiscount(ActivityDiscount activityDiscount);
	
	/**
	 * 
	 * @desc 查询同个店铺相同时间段的同一类型活动 
	 *
	 * @param activityDiscountVo 查询条件-活动对象
	 * @return
	 */
	List<ActivityDiscount> selectActivityByTime(ActivityDiscountVo activityDiscountVo);
	
	/**
	 * 
	 * @desc 店铺活动搜索列表 
	 *
	 * @param activityDiscountVo 查询条件-活动对象
	 * @return
	 */
	List<ActivityDiscount> searchByEntityParams(ActivityDiscountVo activityDiscountVo);
	
	/**
	 * 
	 * @desc 店铺活动搜索列表 
	 *
	 * @param activityDiscountVo 查询条件-活动对象
	 * @return
	 */
	List<ActivityDiscount> searchByMap(Map<String, Object> map);
	

	/**
	 * 
	 * @desc 根据活动名称查询活动信息
	 *
	 * @param storeId 店铺ID
	 * @param name 活动名称
	 * @return 活动信息
	 */
	List<ActivityDiscount> selectByActivityName(@Param("storeId") String storeId, @Param("name") String name);
	/**
	 * 
	 * @desc 关闭活动 
	 *
	 * @param map
	 */
	void updateCloseActivityDiscount(Map<String, Object> map);
	
	
	
	/**
	 * 
	 * 查询指定id的活动关闭的记录数量 
	 * 
	 * @author wusw
	 * @param disabled
	 * @param status
	 * @param ids
	 * @return
	 */
	int selectCountClosedByIds(@Param("disabled")Disabled disabled,
            @Param("status")ActivityDiscountStatus status,@Param("ids")List<String> ids);
	
	/**
	 * 
	 * 根据主键id，关闭活动（批量）
	 * 
	 * @author wusw
	 * @param ids
	 */
	void closeByDiscountId(@Param("ids")List<String> ids,@Param("status")ActivityDiscountStatus status,
            @Param("updateTime")Date updateTime,@Param("updateUserId")String updateUserId);
	
	/**
	 * 
	 * 根据查询条件，查询满减（满折）活动信息列表 
	 * 
	 * @author wusw
	 * @param activityDiscountVo
	 * @return
	 */
	List<ActivityDiscount> selectByEntity(ActivityDiscountVo activityDiscountVo);
	
	/**
	 * 
	 * 根据主键id，查询满减（满折）活动详细信息（包括关联信息） 
	 * 
	 * @author wusw
	 * @param id
	 * @return
	 */
	ActivityDiscountQueryVo selectDiscountAssociateById(String id);
	
	/**
	 * 
	 * 查询指定名称相同的数量 
	 * 
	 * @author wusw
	 * @param activityDiscountVo
	 * @return
	 */
	int selectCountByName(ActivityDiscountVo activityDiscountVo);
	
	/**
	 * 
	 * 查询与指定开始结束时间有交集、指定区域有交集的记录数量 
	 *
	 * @author wusw
	 * @param id 主键id
	 * @param storeId 店铺id（运营商为0）
	 * @param disabled 未删除
	 * @param noStartStatus 未开始活动状态
	 * @param startStatus 进行中活动状态
	 * @param areaType 范围类型
	 * @param startTime 活动开始时间
	 * @param endTime 活动结束时间
	 * @param areaIdList 区域ID（省市ID）集合
	 * @param associateIdList 省下所有市和市所在省的id集合
	 * @return
	 */
	int selectCountByDistrict(@Param("id")String id,@Param("storeId")String storeId,
            @Param("disabled")Disabled disabled,@Param("noStartStatus")ActivityDiscountStatus noStartStatus,
            @Param("startStatus")ActivityDiscountStatus startStatus,@Param("areaType")AreaType areaType,
            @Param("startTime")Date startTime,@Param("endTime") Date endTime,
            @Param("areaIdList")List<String> areaIdList,@Param("associateIdList")List<String> associateIdList);
	
	/**
	 * 
	 * 根据店铺id、活动状态、客户端限制类型，查询该店铺下的满减满折活动
	 * 
	 * @author wusw
	 * @param status 活动状态
	 * @param storeId 店铺id
	 * @return
	 */
	List<ActivityDiscount> selectByStoreAndLimitType(@Param("status")ActivityDiscountStatus status,@Param("storeId")String storeId);

	/**
	 * 
	 * 根据店铺id，查询店铺的满减或满折活动（用于非便利店的店铺）
	 * 
	 * @author wusw
	 * @param storeId
	 * @return
	 */
	List<ActivityDiscount> selectDtoByStoreIdForNoCloud(@Param("storeId") String storeId);
	
	/**
	 * 查询需要修改状态的集合。用于定时任务扫描
	 *
	 * @param curDate 当前时间
	 * @return
	 */
	List<ActivityDiscount> selectNeedUpdateList(@Param("curDate")Date curDate);
	
	/**
	 * 批量更新活动状态
	 *
	 * @param map
	 */
	void updateStatus(Map<String , Object> map);
	
	/**
	 * 根据店铺id查询店铺创建的正在进行中活动列表
	 * @param storeId 店铺id
	 * @param currentDate 当前时间
	 * @return list
	 */
	List<ActivityDiscountDto> selectByStoreId(@Param("storeId") String storeId,@Param("currentDate") Date currentDate);
	
	/**
	 * 根据店铺id查询活动
	 * @desc TODO Add a description 
	 *
	 * @param storeId
	 * @return
	 */
	List<ActivityDiscount> findActivityIndustrys(String storeId);

	
	/**
	 *根据店铺id查询正在进行中活动列表 
	 * @param storeId storeId
	 * @return storeId
	 */
	List<ActivityDiscount> selectDtoByStoreId(@Param("storeId") String storeId);
	
	/**
	 * 活动ID获取活动优惠的ID
	 * @param discountId
	 * @return
	 */
	String getDiscountConditionsId(String discountId);
	
	/**
	 * 查询具体满减活动 </p>
	 * 
	 * @author yangq
	 * @param id
	 * @return
	 */
	ActivityDiscountCondition getDiscountConditions(Map<String,String> map);
	
	/**
	 * 查询具体满折活动 </p>
	 * 
	 * @author yangq
	 * @param id
	 * @return
	 */
	ActivityDiscountCondition getReduceConditions(Map<String,String> map);
	
	/**
	 * 
	 * 根据店铺id、活动状态、客户端限制类型，查询该店铺下的满折活动
	 * 
	 * @author yangq
	 * @param status 活动状态
	 * @param storeId 店铺id
	 * @return
	 */
	List<ActivityDiscountCondition> selectByStoreDiscount(Map<String,String> map);
	
	/**
	 * 
	 * 根据店铺id、活动状态、客户端限制类型，查询该店铺下的满减活动
	 * 
	 * @author yangq
	 * @param status 活动状态
	 * @param storeId 店铺id
	 * @return
	 */
	List<ActivityDiscountCondition> selectByStoreReduce(@Param("status")ActivityDiscountStatus status,@Param("storeId")String storeId);
	
	
	ActivityDiscount selectActivityDiscount(ActivityDiscountVo discountVo);
	
	/**
	 * 根据主键获取活动信息 （订单关联的活动信息）
	 * @param id 主键id
	 * @return
	 */
	ActivityDiscount selectRelaveOrderById(@Param("id") String id);
	
	/**
	 * 周边店满减 </p>
	 * @author yangq
	 * @param status
	 * @param storeId
	 */
	List<ActivityDiscountCondition> selectByStoreReduceOff(@Param("status")ActivityDiscountStatus status,@Param("storeId")String storeId);
	
	// Begin added by maojj 2016-07-14
	/**
	 * @Description: 查询满减满折折扣金额
	 * @param params 查询条件
	 * @return BigDecimal  
	 * @author maojj
	 * @date 2016年7月15日
	 */
	BigDecimal getDiscountValue(String id);
	
	/**
	 * @Description: 查询用户有效的折扣优惠
	 * @param params 查询用户有效优惠请求对象
	 * @return List
	 * @author maojj
	 * @date 2016年7月16日
	 */
	List<Discount> findValidDiscount(Map<String,Object> params);
	
	/**
	 * @Description: 查询用户有效的满减优惠
	 * @param params 查询用户有效优惠请求对象
	 * @return List
	 * @author maojj
	 * @date 2016年7月16日
	 */
	List<FullSubtract> findValidFullSubtract(Map<String,Object> params);
	// End added by maojj 2016-07-14
	
	//begin 重构4.1 added by zhangkn
	/**
	 * @Description: 查询店铺的满减满折列表 (用户app首页用)
	 * @param map 查询参数
	 * @return 满件满折列表
	 * @author zhangkn
	 * @date 2016年7月18日
	 */
	List<ActivityDiscount> findActivityDiscountForUserApp(Map<String,Object> map);
	//end 重构4.1 added by zhangkn
	
	// Begin 重构4.1 add by zengj
	/**
	 * 
	 * @Description: 查询店铺的满减满折活动和条件
	 * @param params 查询参数
	 * @return List 
	 * @author zengj
	 * @date 2016年7月22日
	 */
	List<Map<String, Object>> findActivityDiscountByStoreId(Map<String, Object> params);
	// End 重构4.1 add by zengj
}