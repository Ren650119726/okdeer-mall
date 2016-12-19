package com.okdeer.mall.activity.coupons.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsOrderVo;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsRecordVo;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsSimpleVo;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsVo;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;

/**
 * @DESC: 商品类目-分类类型关系表dao
 * @author zhongy
 * @date  2015-11-25 16:24:57
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface ActivityCollectCouponsMapper {
	
	public void save(ActivityCollectCoupons activityCollectCoupons);
	
	/**
	 * @desc 修改对象
	 * @param activityCollectCoupons
	 */
	public void updateDynamic(ActivityCollectCoupons activityCollectCoupons);
	
	/**
	 * @desc 通过主键获取对象
	 * @param id
	 * @return
	 */
	public ActivityCollectCoupons get(String id);
	
	/**
	 * @desc 列表搜索
	 * @param map
	 * @return
	 */
	public List<ActivityCollectCoupons> list(Map<String,Object> map);
	
	public void updateBatchStatus(Map<String,Object> map);
	
	public List<ActivityCollectCoupons> listByJob();
	
	/**
	 * 
	 * 根据店铺id、活动状态、客户端限制类型，查询该店铺下的满减满折活动
	 * 
	 * @author wusw
	 * @param map
	 * @return
	 */
	List<ActivityCollectCouponsVo> selectByStoreAndLimitType(Map<String,Object> map);
	
	/**
	 * 根据店铺id、活动状态、客户端限制类型等，获取快到期代金券领券活动及代金券信息 </p>
	 * 
	 * @author yangq
	 * @param map
	 * @return
	 */
	List<ActivityCollectCouponsVo> selectByStoreAndLimitTypeExpire(Map<String,Object> map);
	
	/**
	 * @desc 用于判断某个时间段内活动是否冲突
	 * @param map
	 * @return
	 */
	int countTimeQuantum(Map<String,Object> map);
	
	/**
	 * @desc 通过活动id查询该活动一共有多少被使用的代金券的总额
	 * @param id 活动id
	 * @return
	 */
	BigDecimal selectTotalFaceValueByCollectId(String id) ;
	
	/**
	 * 
	 * 已经失效的所有代金卷
	 *
	 * @param map
	 * @return
	 */
	List<ActivityCollectCouponsVo> selectByCollectStatus(Map<String,Object> map);
	
	/**
	 * 
	 * 已经关闭和已经结束活动未使用或者已经过期的
	 *
	 * @param map
	 * @return
	 */
	List<ActivityCollectCouponsRecordVo> selectByUnusedOrExpires(Map<String,Object> map);
	
	/**
	 * 
	 * 将未领取的代金卷金额返还后 
	 *
	 * @param activityCollectCouponsVoList
	 */
	public void updateRefundType(List<ActivityCollectCouponsVo> activityCollectCouponsVoList);
	
	/**
	 * 
	 * 将未使用的代金卷金额返还后
	 *
	 * @param ids
	 */
	public void updateRefundTypeByVo(String id);
	
	/**
	 * 
	 * 根据店铺id、活动状态、客户端限制类型等，统计代金券领券活动及代金券数量
	 * 
	 * @author wusw
	 * @param map
	 * @return
	 */
	int selectCountByStoreAndLimitType(Map<String,Object> map);
	
	
	// begin add by wushp 2016-07-14
	/**
	 * 
	 * @Description: 查询代金券活动以及关联地区列表
	 * @param map map
	 * @return int  记录数
	 * @date 2016年7月14日
	 */
	int findCollectCouponsAreaList(Map<String,Object> map);
	// end add by wushp 2016-07-14
	
	//Begin 根据城市查询代金券活动  added by tangy  2016-7-22
	// modiby by zengjz  增加小区id查询参数
	/**
	 * 
	 * @Description: 根据城市查询代金券活动  
	 * @param provinceId  省id
	 * @param cityId      市id
	 * @return ActivityCollectCouponsVo
	 * @author tangy
	 * @date 2016年7月22日
	 */
	ActivityCollectCouponsVo findActivityCollectCouponsByCity(@Param("provinceId") String provinceId,
			@Param("cityId") String cityId, @Param("communityId") String communityId);
	//End added by tangy
	
	/**
	 * @Description: 根据筛选条件加载代金券列表
	 * @param map
	 * @return 代金券列表
	 * @author zhangkn
	 * @date 2016年9月14日
	 */
	List<ActivityCoupons> findCouponsByParams(Map<String,Object> map);
	
	// begin add by wushp V1.1.0 20160923
	/**
	 * 
	 * @Description: 消费返券：活动代金券查询
	 * @param map 参数map
	 * @return list
	 * @author wushp
	 * @date 2016年9月23日
	 */
	List<ActivityCollectCouponsOrderVo> findCollCouponsLinks(Map<String,Object> map);
	// end add by wushp V1.1.0 20160923
	
	//Begin added by zhaoqc 2016-09-29
	List<ActivityCollectCouponsSimpleVo> findRecommendAcvititys();
	//End added by zhaoqc 2016-09-29
	
	// add by zhulq 2016-10-25
	/**
	 * @Description: 获取随机码的代金卷
	 * @param map 参数
	 * @return List
	 * @author zhulq
	 * @date 2016年10月25日
	 */
	List<ActivityCollectCouponsVo> selectRandCodeVoucher(Map<String,Object> map);
	// add by zhulq 2016-10-25
	
	/**
	 * @Description: 获取广告活动的代金卷
	 * @param map 参数
	 * @return List
	 * @author zhulq
	 * @date 2016年10月25日
	 */
	List<ActivityCollectCouponsVo> selectAdvertVoucher(Map<String,Object> map);
	
	/**
	 * 判断该要用户是否已经领取广告代金劵
	 * @param map 参数
	 * @return
	 */
	int selectCountByUserId(Map<String,Object> map);
}