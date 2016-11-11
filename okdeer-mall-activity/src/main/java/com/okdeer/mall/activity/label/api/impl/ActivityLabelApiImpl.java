package com.okdeer.mall.activity.label.api.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.activity.label.entity.ActivityLabel;
import com.okdeer.mall.activity.label.entity.ActivityLabelGoods;
import com.okdeer.mall.activity.label.service.ActivityLabelApi;
import com.okdeer.mall.activity.label.service.ActivityLabelService;

@Service(version="1.0.0")
public class ActivityLabelApiImpl implements ActivityLabelApi{

	@Autowired
	ActivityLabelService labelService;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void add(ActivityLabel activityLabel,List<String> goodsIds) throws Exception {
		labelService.add(activityLabel, goodsIds);
	}

	@Transactional(rollbackFor = Exception.class)
	public void update(ActivityLabel activityLabel,List<String> goodsIds) throws Exception {
		labelService.update(activityLabel, goodsIds);
	}

	@Transactional(readOnly = true)
	public ActivityLabel findById(String id) {
		return labelService.findById(id);
	}
	
	@Override
	@Transactional(readOnly = true)
	public PageUtils<ActivityLabel> list(Map<String, Object> map,int pageNumber,int pageSize) throws Exception{
		return labelService.list(map, pageNumber, pageSize);
	}
	
	@Override
	@Transactional(readOnly = true)
	public PageUtils<Map<String,Object>> listGoods(Map<String, Object> map,int pageNumber,int pageSize) throws Exception{
		return labelService.listGoods(map, pageNumber, pageSize);
	}

	/**
	 * @desc 用于判断某个时间段内活动是否冲突
	 * @param map
	 * @return
	 */
	@Transactional(readOnly = true)
	public int countTimeQuantum(Map<String, Object> map) {
		return labelService.countTimeQuantum(map);
	}

	/**
	 * @desc 查询出需要跑job的活动
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ActivityLabel> listByJob() {
		return labelService.listByJob();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateBatchStatus(String id, int status, String updateUserId, Date updateTime) throws Exception {
		labelService.updateBatchStatus(id, status, updateUserId, updateTime);
	}

	@Override
	public List<ActivityLabelGoods> listActivityLabelGoods(String activityId) throws Exception {
		return labelService.listActivityLabelGoods(activityId);
	}
}
