package com.okdeer.mall.activity.coupons.mapper;


import java.util.List;

import com.okdeer.mall.activity.coupons.entity.ActivityCouponsLimitCategory;
import com.okdeer.base.dal.IBaseCrudMapper;

/**
 * 代金卷关联类目表 
 * @project yschome-mall
 * @author zhulq
 * @date 2016年3月3日 下午7:00:10
 */
public interface ActivityCouponsLimitCategoryMapper extends IBaseCrudMapper{
	
    /**
     * 根据couponsId 获取关联的限制类目
     * @param couponsId couponsId
     * @return  list
     */
	List<ActivityCouponsLimitCategory> selectByCouponsId(String couponsId);
	
	
}
