/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2016年12月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.prize.entity.ActivityPrizeWeight;
import com.okdeer.mall.activity.prize.entity.ActivityPrizeWeightVo;
import com.okdeer.mall.activity.prize.mapper.ActivityPrizeWeightMapper;
import com.okdeer.mall.activity.prize.service.ActivityPrizeWeightService;

import net.sf.json.JSONObject;

/**
 * ClassName: ActivityPrizeWeightApiImpl 
 * @Description: 活动奖品权重表Service实现类
 * @author xuzq01
 * @date 2016年12月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2.3			2016年12月8日		xuzq01				活动奖品权重表Service实现类
 */
@Service
public class ActivityPrizeWeightServiceImpl extends BaseServiceImpl implements ActivityPrizeWeightService{

	/**
	 * 活动奖品权重表mapper
	 */
	@Autowired
	ActivityPrizeWeightMapper activityPrizeWeightMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return activityPrizeWeightMapper;
	}
	
	/**
	 * 根据活动id查询所有奖品的比重信息 按顺序查询 顺序与奖品对应 
	 * @param activityId 活动id
	 * @return List<ActivityPrizeWeight>  
	 * @author tuzhd
	 * @date 2016年12月14日
	 */
	@Override
	public List<ActivityPrizeWeight> findPrizesByactivityId(String activityId) {
		return activityPrizeWeightMapper.findPrizesByactivityId(activityId);
	}
	
	/**
	 * 根据活动id扣减奖品数量
	 * @param activityId 活动id
	 * @return List<ActivityPrizeWeight>  
	 * @author tuzhd
	 * @date 2016年12月14日
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject updatePrizesNumber(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		int no = activityPrizeWeightMapper.updatePrizesNumber(id);
		if(no > 0){
			map.put("code", 100);
			map.put("msg", "恭喜你，领取成功！");
		}else{
			map.put("msg", "奖品已经领完，下次请早点！");
			map.put("code", 101);
		}
		return JSONObject.fromObject(map);
	}

	@Override
	public PageUtils<ActivityPrizeWeightVo> findPrizeWeightList(ActivityPrizeWeightVo activityPrizeWeightVo,
			int pageNumber, int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ActivityPrizeWeightVo> result = activityPrizeWeightMapper.findPrizeRecordList(activityPrizeWeightVo);
		return new PageUtils<ActivityPrizeWeightVo>(result);
	}
	
	

}
