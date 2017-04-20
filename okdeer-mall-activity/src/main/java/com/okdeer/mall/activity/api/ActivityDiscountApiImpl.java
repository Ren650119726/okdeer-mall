package com.okdeer.mall.activity.api;

import java.util.List;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.common.entity.ReturnInfo;
import com.okdeer.mall.activity.bo.ActivityParamBo;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.service.ActivityDiscountApi;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.activity.dto.ActivityInfoDto;
import com.okdeer.mall.activity.dto.ActivityParamDto;

/**
 * ClassName: ActivityDiscountApiImpl 
 * @Description: 活动Api实现类
 * @author maojj
 * @date 2017年4月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.3 		2017年4月17日				maojj
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.discount.service.ActivityDiscountApi")
public class ActivityDiscountApiImpl implements ActivityDiscountApi{
	
	@Resource
	private ActivityDiscountService activityDiscountService;

	@Override
	public ReturnInfo add(ActivityInfoDto actInfoDto) {
		return activityDiscountService.add(actInfoDto);
	}
	
	@Override
	public ReturnInfo update(ActivityInfoDto actInfoDto) {
		return activityDiscountService.update(actInfoDto);
	}

	@Override
	public ActivityDiscount findById(String id) throws Exception {
		return activityDiscountService.findById(id);
	}

	@Override
	public PageUtils<ActivityDiscount> findListByParam(ActivityParamDto paramDto) {
		List<ActivityDiscount> actList = activityDiscountService.findListByParam(paramDto);
		return new PageUtils<ActivityDiscount>(actList);
	}

	@Override
	public ReturnInfo batchClose(ActivityParamDto paramDto) {
		ActivityParamBo paramBo = BeanMapper.map(paramDto, ActivityParamBo.class);
		return activityDiscountService.batchClose(paramBo);
	}

	@Override
	public ActivityInfoDto findInfoById(String id) throws Exception {
		return activityDiscountService.findInfoById(id);
	}

	@Override
	public List<ActivityInfoDto> findByStore(ActivityParamDto paramDto) throws Exception {
		return activityDiscountService.findByStore(paramDto);
	}

}
