package com.okdeer.mall.activity.coupons.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.activity.coupons.entity.ActivityCouponsCategory;

/**
 * 代金卷关联类目表 
 * @project yschome-mall
 * @author zhulq
 * @date 2016年3月3日 下午7:00:10
 */
public interface ActivityCouponsCategoryMapper {
	
    /**
     * 根据couponsId 获取关联的限制类目
     * @param couponsId couponsId
     * @return  list
     */
	List<ActivityCouponsCategory> findActivityCouponsCategoryByCouponsId
		(Map<String,Object> map);
	
	/**
     * 批量插入
     * @param list ActivityCouponsCategory列表
     * @return  list
     */
	void saveBatch(List<ActivityCouponsCategory> list);
	
	/**
	 * @Description: 根据代金券id删除
	 * @param couponsId 代金券id
	 * @author zhangkn
	 * @date 2016年9月10日
	 */
	void deleteByCouponsId(String couponsId);
	
	// Begin V2.6.4 added by maojj 2017-11-09
	/**
	 * @Description: 查询限制的商品类目列表
	 * @param couponsId
	 * @param categoryIds
	 * @return   
	 * @author maojj
	 * @date 2017年11月9日
	 */
	List<String> findLimitCategoryList(@Param("couponsId")String couponsId,@Param("categoryIds")Set<String> categoryIds);
	// End V2.6.4 added by maojj 2017-11-09
}
