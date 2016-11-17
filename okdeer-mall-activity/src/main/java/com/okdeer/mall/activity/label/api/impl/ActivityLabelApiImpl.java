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
	 * 1、查询活动未开始，开始时间小于当前的数据 即为要设置开始，2、活动开始、结束时间小于当前的数据 即为要设置结束
	 * @author tuzhd
	 * @param map 传递参数
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ActivityLabel> listByJob(Map<String,Object> map)  {
		return labelService.listByJob(map);
	}
	/**
	 * @Description: 根据id修改服务标签活动状态
	 * @param id
	 * @param status 活动状态 0 未开始 ，1：进行中2:已结束 3 已关闭
	 * @param updateUserId 修改人
	 * @param updateTime 修改时间
	 * @throws Exception
	 * @author tuzhiding
	 * @date 2016年11月12日
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateStatusById(String id, int status, String updateUserId, Date updateTime) throws Exception {
		labelService.updateStatusById(id, status, updateUserId, updateTime);
	}

	@Override
	public List<ActivityLabelGoods> listActivityLabelGoods(String activityId) throws Exception {
		return labelService.listActivityLabelGoods(activityId);
	}
	
	/**
	 * 执行服务标签的JOB 任务
	 * @Description: TODO   
	 * @return void  
	 * @throws
	 * @author tuzhd
	 * @date 2016年11月16日
	 */
	public void processLabelJob(){
		labelService.processLabelJob();
	}
}
