package com.okdeer.mall.activity.coupons.service;

import java.util.List;
import java.util.Map;

import com.okdeer.archive.goods.base.entity.GoodsSpuCategory;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsArea;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsCommunity;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsLimitCategory;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsStore;
import com.okdeer.mall.activity.coupons.entity.CouponsInfoParams;
import com.okdeer.mall.activity.coupons.entity.CouponsInfoQuery;
import com.okdeer.mall.activity.coupons.vo.AddressCityVo;
import com.okdeer.mall.common.entity.AreaScTreeVo;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;


/**
 * 代金劵service 
 * @project yschome-mall
 * @author zhulq
 * @date 2016年1月21日 下午3:06:21
 */
public interface ActivityCouponsService {

	/**
	 * 根据城市id 查询店铺 
	 * @param parStoreInfo  对象
	 * @return 店铺信息
	 */
	List<AreaScTreeVo> getStoreInfoByCityId(StoreInfo parStoreInfo);

	/**
	 * 获取代金券信息
	 * @param params 请求参数
	 * @param pageNum 页码
	 * @param pageSize 行数
	 * @return 基本属性列表
	 * @throws ServiceException 异常信息
	 */
	PageUtils<CouponsInfoQuery> getCouponsInfo(CouponsInfoParams couponsInfoParams, int pageNum, int pageSize)
			throws ServiceException;

	/**
	 * 添加代金券
	 * @param coupons 对象
	 * @throws ServiceException 异常
	 */
	public void addCoupons(ActivityCoupons coupons) throws ServiceException;

	/**
	 * 添加代金券类目关联信息 
	 * @param activityCouponsLimitCatagroy
	 * @return
	 * @throws ServiceException
	 */
	public void addCouponsLimitCategory(List<ActivityCouponsLimitCategory> activityCouponsLimitCategoryList)
			throws ServiceException;

	/**
	 * 添加指定区域
	 * @param activityCouponsAreaList
	 * @return
	 * @throws ServiceException
	 */
	public void addCouponsArea(List<ActivityCouponsArea> activityCouponsAreaList) throws ServiceException;

	/**
	 * 指定的小区
	 * @param activityCouponsStoreList
	 * @return
	 * @throws ServiceException
	 */
	public void addCouponsCommunity(List<ActivityCouponsCommunity> activityCouponsCommunityList)
			throws ServiceException;

	/**
	 * 指定的店铺
	 * @param activityCouponsStoreList
	 * @return
	 * @throws ServiceException
	 */
	public void addCouponsStore(List<ActivityCouponsStore> activityCouponsStoreList) throws ServiceException;

	/**
	 * 添加关联的信息
	 * @param coupons
	 * @throws ServiceException
	 */
	public void addRelatedInfo(ActivityCoupons coupons) throws ServiceException;

	/**
	 * 跟新代金券
	 * @param coupons 代金券
	 * @throws ServiceException 异常
	 */
	void updateCoupons(CouponsInfoQuery coupons) throws ServiceException;
	
	/**
	 * 更新代金券表基本信息
	 * @param coupons 代金券
	 * @throws ServiceException 异常
	 */
	void update(CouponsInfoQuery coupons) throws ServiceException;

	/**
	 * 根据coupons查询  
	 * @param coupons
	 * @return
	 * @throws ServiceException
	 */
	int getByParame(CouponsInfoQuery coupons) throws ServiceException;

	/**
	 * 根据id查询 
	 * @param id  id 
	 * @return
	 * @throws ServiceException 异常
	 */
	CouponsInfoQuery getCouponsInfoById(String id) throws ServiceException;

	/**
	 * 根据name查询  
	 * @param coupons
	 * @return
	 * @throws ServiceException
	 */
	List<ActivityCoupons> getCouponsInfoByName(ActivityCoupons coupons) throws ServiceException;

	/**
	 * 根据 couponsId 获取  ActivityCouponsLimitCategory
	 * @param couponsId 代金券id
	 * @return  List
	 * @throws ServiceException 异常
	 */
	List<ActivityCouponsLimitCategory> getCouponsLimitCategoryByCouponsId(String couponsId) throws ServiceException;

	/**
	 * 根据id删除
	 * @param id
	 * @throws ServiceException
	 */
	void deleteByIds(String id) throws ServiceException;

	/** 
	 * 删除限制类目信息 
	 * @param id
	 * @throws ServiceException
	 */
	void deleteCouponsLimitCategory(String id) throws ServiceException;

	/** 
	 * 删除区域信息 
	 * @param id
	 * @throws ServiceException
	 */
	void deleteCouponsArea(String id) throws ServiceException;

	/** 
	 * 删除小区信息 
	 * @param id
	 * @throws ServiceException
	 */
	void deleteCouponsCommunity(String id) throws ServiceException;

	/** 
	 * 删除店铺 
	 * @param id
	 * @throws ServiceException
	 */
	void deleteCouponsStroe(String id) throws ServiceException;

	/**
	 * 更新之前将关联信息删除 
	 * @param id
	 * @return
	 * @throws ServiceException
	 */
	int getCategory(String id) throws ServiceException;

	/**
	 * 更新之前将关联信息删除 
	 * @param id
	 * @return
	 * @throws ServiceException
	 */
	int getArea(String id) throws ServiceException;

	/**
	 * 更新之前将关联信息删除 
	 * @param id
	 * @return
	 * @throws ServiceException
	 */
	int getCommunity(String id) throws ServiceException;

	/**
	 * 更新之前将关联信息删除 
	 * @param id
	 * @return
	 * @throws ServiceException
	 */
	int getStore(String id) throws ServiceException;

	/**
	 * @desc 通过活动id获取list(添加或者修改代金券活动用)
	 * @param activityId 活动id
	 * @param belongType 所属代理商ID,0表示运营商
	 * @return
	 * @throws ServiceException
	 */
	public List<ActivityCoupons> listByActivityId(String activityId, String belongType) throws ServiceException;

	/**
	 * 将区域和小区相关的店铺增加进关联表
	 * @param StoreInfoList  ActivityCoupons StoreInfoList coupons
	 * @throws ServiceException 异常
	 */
	public void addRelationStore(List<StoreInfo> StoreInfoList, ActivityCoupons coupons) throws ServiceException;

	/**
	 * 根据代金券id 删除 关联信息
	 * @param couponsId
	 * @throws ServiceException
	 */
	void deleteCouponsRelationStroe(String id) throws ServiceException;

	/**
	 * 跟新之前将之前的关联关系删掉 
	 * @param couponsId 代金券id
	 * @return 记录条数
	 * @throws ServiceException 异常
	 */
	int getCouponsRelationStroe(String id) throws ServiceException;

	/**
	 * 
	 * 根据代金券id，查询代金券基本信息
	 * 
	 * @author wusw
	 * @param id
	 * @return
	 * @throws ServiceException
	 */
	ActivityCoupons getById(String id) throws ServiceException;

	/**
	 * 
	 * 根据代金券id、省id，查询关联店铺id、所在市id
	 * 
	 * @author wusw
	 * @param couponsId
	 * @param provinceId
	 * @return
	 */
	Map<String, List<Map<String, Object>>> findCityRelationStoreByCouponsProvince(String couponsId, String provinceId)
			throws ServiceException;

	/**
	 * 
	 * 根据代金券id，查询关联的省id
	 * 
	 * @author wusw
	 * @param couponsId
	 * @return
	 * @throws ServiceException
	 */
	Map<String, Object> findCouponsProvinceByCouponsId(String couponsId) throws ServiceException;

	/**
	 * 获取最近一个进行中的注册活动的代金券活动关联的代金券集合
	 * @param map 有以下key
	 * type 0代金券领取活动，1注册活动
	 */
	List<ActivityCoupons> listCouponsByType(Map<String, Object> map) throws ServiceException;

	List<ActivityCoupons> getActivityCoupons(String activityId);

	/**
	 * 判断代金券是否能用于注册活动 
	 * @param ids id集合
	 * @return
	 * @throws ServiceException 异常
	 */
	Boolean findByIds(List<String> ids) throws ServiceException;

	void updateActivityCouponsUsedNum(String activityItemId);

	ActivityCoupons selectByPrimaryKey(String id);
}
