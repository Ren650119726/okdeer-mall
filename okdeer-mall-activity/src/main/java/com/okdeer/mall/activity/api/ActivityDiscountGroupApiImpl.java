package com.okdeer.mall.activity.api;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.goods.base.entity.GoodsSpuCategory;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.activity.discount.dto.ActivityGoodsGroupSkuDto;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountGroup;
import com.okdeer.mall.activity.discount.service.ActivityDiscountGroupApi;
import com.okdeer.mall.activity.discount.service.ActivityDiscountGroupService;

/**
 * ClassName: ActivityDiscountGroupMapper 
 * @Description: 团购商品关联表实体操作类
 * @author tuzhd
 * @date 2017年10月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *	   2.6.3			2017-10-10			tuzhd			团购商品关联表实体操作类
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.discount.service.ActivityDiscountGroupApi")
public class ActivityDiscountGroupApiImpl implements ActivityDiscountGroupApi {
	@Resource
	private ActivityDiscountGroupService activityDiscountGroupService;
	
	@Override
	public PageUtils<GoodsSpuCategory> findGroupGoodsCategory(Integer pageNumber, Integer pageSize) {
		return activityDiscountGroupService.findGroupGoodsCategory(pageNumber, pageSize);
	}

	@Override
	public PageUtils<ActivityGoodsGroupSkuDto> findGroupGoodsList(Map<String, Object> param, Integer pageNumber,
			Integer pageSize) {
		return activityDiscountGroupService.findGroupGoodsList(param, pageNumber, pageSize);
	}

	@Override
	public void update(ActivityDiscountGroup group) throws Exception{
		activityDiscountGroupService.update(group);
	}
}
