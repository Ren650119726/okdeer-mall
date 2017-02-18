package com.okdeer.mall.activity.discount.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountCondition;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountDto;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountQueryVo;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountVo;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountStatus;
import com.okdeer.mall.activity.service.FavourFilterStrategy;
import com.okdeer.mall.order.vo.Discount;
import com.okdeer.mall.order.vo.FullSubtract;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;

/**
 * 满减(满折)Service
 * @pr yscm
 * @desc 满减(满折)Service
 * @author zengj
 * @date 2016年1月26日 下午2:25:34
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构V4.1 			2016-07-22			zengj			查询店铺有效的满减
 *		友门鹿V2.1 		2017-02-17			maojj			查询店铺有效的满减
 */
public interface ActivityDiscountService {
	/**
	 * 
	 * @desc 添加活动
	 *
	 * @param activityDiscount 活动对象
	 * @param activityDiscountConditionList 活动条件集合
	 */
	void insertActivityDiscount(ActivityDiscount activityDiscount, List<ActivityDiscountCondition> activityDiscountConditionList);
	
	/**
	 * 
	 * @desc 修改活动
	 *
	 * @param activityDiscount 活动对象
	 * @param activityDiscountConditionList 活动条件集合
	 */
	void updateActivityDiscount(ActivityDiscount activityDiscount, List<ActivityDiscountCondition> activityDiscountConditionList);
	
	/**
	 * 
	 * @desc 根据活动名称查询活动信息
	 *
	 * @param storeId 店铺ID
	 * @param name 活动名称
	 * @return 活动信息
	 */
	List<ActivityDiscount> selectByActivityName(String storeId, String name);
	/**
	 * 
	 * @desc 查询同个店铺相同时间段的同一类型活动 
	 *
	 * @param ActivityDiscountVo 查询条件-活动对象
	 * @return
	 */
	List<ActivityDiscount> selectActivityByTime(ActivityDiscountVo activityDiscountVo);
	
	/**
	 * 
	 * @desc 店铺活动搜索列表 
	 *
	 * @param activityDiscountVo 查询条件-活动对象
	 * @param pageNumber 当前页
	 * @param pageSize 每页展示的记录数
	 * @return
	 */
	public PageUtils<ActivityDiscount> searchByEntityParams(
			ActivityDiscountVo activityDiscountVo, int pageNumber, int pageSize);
	
	/**
	 * 
	 * @desc 店铺活动搜索列表 
	 *
	 * @param activityDiscountVo 查询条件-活动对象
	 * @param pageNumber 当前页
	 * @param pageSize 每页展示的记录数
	 * @return
	 */
	public PageUtils<ActivityDiscount> searchByMap(
			Map<String, Object> map, int pageNumber, int pageSize);
	/**
	 * 
	 * @desc 关闭活动 
	 *
	 * @param map 
	 */
	public void updateCloseActivityDiscount(Map<String, Object> map);
	
	/**
	 * @desc 根据主键ID获取对象
	 *
	 * @param id 主键ID
	 * @return
	 */
	public ActivityDiscount selectByPrimaryKey(String id);
	
	
	/**
	 * 活动ID获取活动优惠的ID
	 * @param discountId
	 * @return
	 */
	String getDiscountConditionsId(String discountId);
	
	/**
	 * 
	 * @desc 根据活动ID查询活动条件 
	 *
	 * @param discountId 活动ID
	 * @return 活动条件集合
	 */
	public List<ActivityDiscountCondition> selectByDiscountId(String discountId);

	
	/**
	 * 
	 * 根据查询条件，查询满减或满折活动信息列表（参数类型实体，分页）
	 *
	 * @author wusw
	 * @param activityDiscountVo
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 * @throws ServiceException
	 */
	PageUtils<ActivityDiscount> findByEntity(ActivityDiscountVo activityDiscountVo, int pageNumber, int pageSize) throws ServiceException;
	
	
	/**
	 * 
	 * 根据主键id，查询满减（满折）活动详细信息（包括关联信息）
	 * 
	 * @author wusw
	 * @param id
	 * @return
	 * @throws ServiceException
	 */
	ActivityDiscountQueryVo getActivityDiscountQueryVoById(String id) throws ServiceException;
	
	
	/**
	 * 
	 * 新增满减活动
	 * 
	 * @author wusw
	 * @param ActivityDiscountQueryVo 活动Vo
	 * @param currentOperateUserId 
	 * @throws ServiceException
	 */
	void addActivityDiscount(ActivityDiscountVo activityDiscountVo,String currentOperateUserId) throws ServiceException;
	
	/**
	 * 
	 * 修改满减活动 
	 * 
	 * @author wusw
	 * @param activityDiscountVo
	 * @param currentOperateUserId
	 * @throws ServiceException
	 */
	void updateActivityDiscount(ActivityDiscountVo activityDiscountVo,String currentOperateUserId) throws ServiceException;
	
	/**
	 * 
	 * 查询指定id的活动关闭的记录数量  
	 * 
	 * @author wusw
	 * @param ids
	 * @return
	 * @throws ServiceException
	 */
	int selectCountClosedByIds(List<String> ids) throws ServiceException;
	
	/**
	 * 
	 * 根据主键id，关闭活动（批量）
	 * 
	 * @author wusw
	 * @param ids
	 * @param currentOperateUserId
	 */
	void closeByDiscountId(List<String> ids,String currentOperateUserId) throws ServiceException;
	
	
	/**
	 * 
	 * 查询指定名称相同的数量
	 * 
	 * @author wusw
	 * @param activityDiscountVo
	 * @return
	 * @throws ServiceException
	 */
	int selectCountByName(ActivityDiscountVo activityDiscountVo) throws ServiceException;
	
	/**
	 * 
	 * 查询与指定开始结束时间有交集、指定区域有交集的记录数量  
	 * 
	 * @author wusw
	 * @param activityDiscountVo
	 * @param areaIdList 区域ID（省市ID）集合
	 * @param associateIdList 省下所有市和市所在省的id集合
	 * @return
	 * @throws ServiceException
	 */
	int selectCountByDistrict(ActivityDiscountVo activityDiscountVo,List<String> areaIdList,List<String> associateIdList) throws ServiceException;
	
	/**
	 * 
	 * 根据店铺id、活动状态、客户端限制类型，查询该店铺下的满减满折活动 
	 * 
	 * @author wusw
	 * @param status 活动状态
	 * @param storeId 店铺id
	 * @return
	 * @throws ServiceException
	 */
	List<ActivityDiscount> findByStoreAndLimitType(ActivityDiscountStatus status,String storeId)throws ServiceException;
	
	/**
	 * 
	 * 根据店铺id，查询店铺的满减或满折活动（用于非便利店的店铺）
	 * 
	 * @author wusw
	 * @param storeId
	 * @return
	 */
	List<ActivityDiscount> findDtoByStoreIdForNoCloud(String storeId)throws ServiceException;
	
	
	
	/**
	 * 查询需要修改状态的集合。然后批量更新
	 *
	 * @return
	 */
	public void updateStatus();
	
	/**
	 * 根据店铺id查询店铺创建的正在进行中活动列表
	 * @param storeId 店铺id
	 * @param currentDate 当前时间
	 * @return list
	 * @throws ServiceException
	 */
	List<ActivityDiscountDto> selectByStoreId(@Param("storeId") String storeId,@Param("currentDate") Date currentDate) throws ServiceException;

	
	
	/**
	 * 根据店铺id查询活动
	 * @desc TODO Add a description 
	 *
	 * @param storeId
	 * @return
	 */
	List<ActivityDiscount> findActivityIndustrys(String storeId);

	
	/**
	 * 根据店铺id 查询 ActivityDiscountDto
	 * @param storeId storeId
	 * @return ActivityDiscountDto
	 * @throws ServiceException 异常
	 */
	List<ActivityDiscount> findDtoByStoreId(@Param("storeId") String storeId)throws ServiceException;
	
	/**
	 * 
	 * 根据店铺id、活动状态、客户端限制类型，查询该店铺下的满折活动
	 * 
	 * @author yangq
	 * @param status 活动状态
	 * @param storeId 店铺id
	 * @return
	 */
	List<ActivityDiscountCondition> selectByStoreDiscount(Map<String,String> map)throws Exception;
	
	/**
	 * 
	 * 根据店铺id、活动状态、客户端限制类型，查询该店铺下的满减活动
	 * 
	 * @author yangq
	 * @param status 活动状态
	 * @param storeId 店铺id
	 * @return
	 */
	List<ActivityDiscountCondition> selectByStoreReduce(@Param("status")ActivityDiscountStatus status,@Param("storeId")String storeId)throws Exception;
	
	
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
	 * 根据主键得到活动信息（订单的）
	 * @param id id
	 * @return ActivityDiscount
	 * @throws Exception 异常
	 */
	ActivityDiscount getById(@Param("id") String id) throws ServiceException;
	
	/**
	 * 周边店满减 </p>
	 * @author yangq
	 * @param status
	 * @param storeId
	 */
	List<ActivityDiscountCondition> selectByStoreReduceOff(@Param("status")ActivityDiscountStatus status,@Param("storeId")String storeId)throws Exception;


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
	
	// Begin V2.1 added by maojj 2017-02-17
	/**
	 * @Description: 查询用户有效的折扣优惠
	 * @param params 查询用户有效优惠请求对象
	 * @return List
	 * @author maojj
	 * @date 2016年7月16日
	 */
	List<Discount> findValidDiscount(FavourParamBO paramBo,FavourFilterStrategy favourFilter) throws Exception;
	
	/**
	 * @Description: 查询用户有效的满减优惠
	 * @param params 查询用户有效优惠请求对象
	 * @return List
	 * @author maojj
	 * @date 2016年7月16日
	 */
	List<FullSubtract> findValidFullSubtract(FavourParamBO paramBo,FavourFilterStrategy favourFilter) throws Exception;
	// End V2.1 added by maojj 2017-02-17
}
