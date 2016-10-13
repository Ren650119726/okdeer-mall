package com.okdeer.mall.activity.coupons.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.archive.goods.base.entity.GoodsNavigateCategory;
import com.okdeer.archive.goods.base.entity.GoodsSpuCategory;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsArea;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsCommunity;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsLimitCategory;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsStore;
import com.okdeer.mall.activity.coupons.entity.CouponsInfoParams;
import com.okdeer.mall.activity.coupons.entity.CouponsInfoQuery;
import com.okdeer.mall.common.entity.AreaScTreeVo;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.dal.IBaseCrudMapper;

/**
 * ClassName: ActivityCouponsMapper 
 * @Description: 代金卷管理
 * @author zhulq
 * @date 2016年9月27日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.1			 2016年9月27日 			zhulq    新增查询对应类目名称
 */
public interface ActivityCouponsMapper extends IBaseCrudMapper{
	
	/**
	 * 根据parStoreInfo 得到店铺信息
	 * @param parStoreInfo 对象
	 * @return 店铺信息
	 */
	List<AreaScTreeVo> selectStoreInfoByCityId(StoreInfo parStoreInfo);
	
	/**
	 * 查询一级导航类目
	 * @param goodsSpuCategory 对象
	 * @return 结果
	 */
	List<GoodsNavigateCategory> findNavigateCategoryList(Map<String,Object> map);
	/**
	 * 查询三级商品类目
	 * @param goodsSpuCategory 对象
	 * @return 结果
	 */
	List<GoodsSpuCategory> findSpuCategoryList(Map<String,Object> map);
	
	/**
	 * 查询服务店一级分类id
	 * @param goodsSpuCategory 对象
	 * @return 结果
	 */
	List<String> findFwdFirstSpuCategoryList(Map<String,Object> map);
	
	/**
	 * 根据条件查询代金卷信息 
	 * @param couponsInfoParams 条件
	 * @return 代金卷信息
	 */
	List<CouponsInfoQuery> selectCoupons(CouponsInfoParams couponsInfoParams);
	
	/**
	 * 根据条件查询代金卷
	 * @param coupons 对象
	 * @return 结果
	 */
	List<ActivityCoupons> selectCouponsByName(ActivityCoupons coupons);
	
	/**
	 * 修改验证
	 * @param coupons 对象
	 * @return list
	 */
	CouponsInfoQuery selectByParams(CouponsInfoQuery coupons);
	
	/**
	 * 根据id获取代金卷 
	 * @param id id 
	 * @return 结果
	 */
	CouponsInfoQuery selectCouponsById(String id);
	
	/**
	 * 添加代金卷
	 * @param coupons 对象
	 */
	void insert(ActivityCoupons coupons);
	
	/**
	 * 更新代金卷信息
	 * @param coupons 代金卷信息
	 */
	void updateCoupons(CouponsInfoQuery coupons);
	
	/**
	 * 添加代金卷限制类目 
	 * @param activityCouponsLimitCatagroyList 对象
	 */
	void insertCouponsLimitCategory(List<ActivityCouponsLimitCategory> activityCouponsLimitCategoryList);
	
	
	/**
	 * 添加代金卷区域
	 * @param couponsAreaList 对象
	 */
	void insertCouponsArea(List<ActivityCouponsArea> couponsAreaList);   
	
	/**
	 * 添加代金卷小区
	 * @param couponsCommunity 对象
	 */
	void insertCouponsCommunity(List<ActivityCouponsCommunity> couponsCommunityList);   
	
	/**
	 * 添加代金卷店铺
	 * @param couponsStoreList 对象
	 */
	void insertCouponsStore(List<ActivityCouponsStore> couponsStoreList);
	
	/**
	 * 批量删除
	 * @param id  id
	 */
	void deleteByIds(String id);
	
	/**
	 * 删除限制类目信息 
	 * @param id 代金卷id
	 */
	void deleteCouponsLimitCategory(String id);
	
	/**
	 * 删除区域信息 
	 * @param id 代金卷id
	 */
	void deleteCouponsArea(String id);
	
	/**
	 * 删除小区信息
	 * @param id 代金卷id
	 */
	void deleteCouponsCommunity(String id);
	
	/**
	 * 删除店铺
	 * 信息
	 * @param id 代金卷id
	 */
	void deleteCouponsStore(String id);
	
	/**
	 * 查询关联信息 
	 * @param id  代金卷id
	 * @return 结果
	 */
	int selectCouponsLimitCategory(String id);
	
	/**
	 * 查询关联信息 
	 * @param id  代金卷id
	 * @return  结果
	 */
	int selectCouponsArea(String id);
	
	/**
	 * 查询关联信息 
	 * @param id  代金卷id
	 * @return  结果
	 */
	int selectCouponsCommunity(String id);
	
	/**
	 * 查询关联信息 
	 * @param id  代金卷id
	 * @return  结果
	 */
	int selectCouponsStore(String id);

	/**
	 * @desc 通过活动id获取list
	 * @param map 活动id
	 * @return 代金券列表
	 * @throws ServiceException
	 */
	List<ActivityCoupons> listByActivityId(Map<String,Object> map);
	
	List<ActivityCoupons> getActivityCoupons(String activityId);
	
	/**
	 * @desc 把活动所属的代金券改为null
	 * @param activityId 活动id
	 */
	void updateActivityIdNull(String activityId);
	
	/**
	 * @desc 批量修改代金券所属的活动
	 * @param map 封装了代金券idlist,活动id(多个参数用map方便一点)
	 */
	void updateBatchActivityId(Map<String,Object> map);
	
	/**
	 * 
	 * 根据优惠码，查询代金券信息
	 * 
	 * @author wusw
	 * @param exchangeCode
	 * @return
	 */
	ActivityCoupons selectByExchangeCode(String exchangeCode);
	
	/**
	 * 获取最近一个进行中的注册活动的代金券活动关联的代金券集合
	 * @param map 有以下key
	 * type 0代金券领取活动，1注册活动
	 */
	List<ActivityCoupons> listCouponsByType(Map<String,Object> map) ;
	
	/**
	 * 判断代金卷是否用于注册活动
	 * @param ids 代金卷id 集合
	 * @return
	 */
	int selectByIds(@Param("ids")List<String> ids);
	/**
	 * DESC: 更新 代金卷领取数量
	 * @author LIU.W
	 * @param activityCoupons
	 * @return
	 */
	public int updateRemainNum(String activityCouponsId);
	
	/**
	 * 更新代金卷使用数量
	 */
	public int updateReduceUseNum(String activityCouponsId);
	
	/**
	 * 修改代金券的使用情况 </p>
	 * 
	 * @author yangq
	 * @param id
	 */
	void updateActivityCouponsUsedNum(String id);
	
	/**
	 * 查询有效的代金券 </p>
	 * 
	 * @author yangq
	 * @param id
	 * @return
	 */
	ActivityCoupons selectById(String id);
	
	//add  by  zhuliq
	/**
	 * @Description: 根据代金券id查询对应的导航类目
	 * @param id  代金券id
	 * @return  CouponsInfoQuery
	 * @author zhulq
	 * @date 2016年9月27日
	 */
	//CouponsInfoQuery findNavCategoryByCouponsId(@Param("id")String id);
	//end  by  zhuliq
	
	//add  by  zhuliq
	/**
	 * @Description: 根据代金券id查询对应的导航类目
	 * @param id  代金券id
	 * @return  CouponsInfoQuery
	 * @author zhulq
	 * @date 2016年9月27日
	 */
	//CouponsInfoQuery findSpuCategoryByCouponsId(@Param("id")String id);
	//end  by  zhuliq
	
	/**
	 * @Description: 领卷中心
	 * @param map map
	 * @return CouponsInfoQuery
	 * @author zhulq
	 * @date 2016年9月27日
	 */
	//List<CouponsInfoQuery> findForCouponsCenter(Map<String,Object> map);
	
	/**
	 * 
	 * @Description: 查询面额
	 * @param id
	 * @return
	 * @author yangq
	 * @date 2016年10月13日
	 */
	List<ActivityCoupons> selectByActivityId(String id);
	
	/**
	 * 
	 * @Description: 查询邀请注册代金券列表
	 * @param id
	 * @return
	 * @author yangq
	 * @date 2016年10月13日
	 */
	int selectFaceMoney(String id);
}
