package com.okdeer.mall.activity.serviceLimit.api.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.activity.serviceLimit.entity.ActivityServiceLimit;
import com.okdeer.mall.activity.serviceLimit.entity.ActivityServiceLimitGoods;
import com.okdeer.mall.activity.serviceLimit.service.ActivityServiceLimitApi;
import com.okdeer.mall.activity.serviceLimit.service.ActivityServiceLimitService;

@Service(version="1.0.0")
public class ActivityServiceLimitApiImpl implements ActivityServiceLimitApi{

	@Autowired
	ActivityServiceLimitService limitService;
	
	@Override
	public void add(ActivityServiceLimit activityLimit, List<ActivityServiceLimitGoods> asgList) throws Exception{
		limitService.add(activityLimit, asgList);
	}

	public void update(ActivityServiceLimit activityLimit, List<ActivityServiceLimitGoods> asgList) throws Exception{
		limitService.update(activityLimit, asgList);
	}

	@Override
	public ActivityServiceLimit findById(String id){
		return limitService.findById(id);
	}
	
	@Override
	public PageUtils<ActivityServiceLimit> pageList(Map<String, Object> map, Integer pageNumber, Integer pageSize){
		return limitService.pageList(map, pageNumber, pageSize);
	}

	@Override
	public void updateBatchStatus(List<String> ids, int status, String storeId, String createUserId) throws Exception{
		limitService.updateBatchStatus(ids, status, storeId, createUserId);
	}
	
	@Override
	public List<Map<String, Object>> listGoodsStoreSku(Map<String, Object> map){
		return limitService.listGoodsStoreSku(map);
	}

	@Override
	public PageUtils<Map<String, Object>> pageListGoodsStoreSku(Map<String, Object> map, Integer pageNumber, Integer pageSize){
		return limitService.pageListGoodsStoreSku(map, pageNumber, pageSize);
	}
	
	@Override
	public List<ActivityServiceLimitGoods> listActivityLimitGoods(String activityId){
		return limitService.listActivityLimitGoods(activityId);
	}
	/**
	 * 判断某个时间段内是否存在特惠活动
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Override
	public int validateExist(Map<String, Object> map){
		return limitService.validateExist(map);
	}
	
	@Override
	public void processJob(){
		limitService.processJob();
	}
}
