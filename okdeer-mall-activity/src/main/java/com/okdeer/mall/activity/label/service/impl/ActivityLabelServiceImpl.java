package com.okdeer.mall.activity.label.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.label.entity.ActivityLabel;
import com.okdeer.mall.activity.label.entity.ActivityLabelGoods;
import com.okdeer.mall.activity.label.mapper.ActivityLabelGoodsMapper;
import com.okdeer.mall.activity.label.mapper.ActivityLabelMapper;
import com.okdeer.mall.activity.label.service.ActivityLabelService;

/**
 * ClassName: ActivityLabelServiceImpl 
 * @Description: 标签活动service
 * @author zhangkn
 * @date 2016年11月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.1			 2016年11月8日 			zhagnkn
 */
@Service
public class ActivityLabelServiceImpl extends BaseServiceImpl implements ActivityLabelService {

	private static final Logger log = Logger.getLogger(ActivityLabelServiceImpl.class);

	@Autowired
	private ActivityLabelMapper activityLabelMapper;
	@Autowired
	private ActivityLabelGoodsMapper activityLabelGoodsMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return activityLabelMapper;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void add(ActivityLabel activityLabel,List<String> goodsIds) throws Exception {
		// 先保存活动主对象
		activityLabelMapper.add(activityLabel);
		
		//批量保存商品关联信息
		addLabelGoodsList(activityLabel.getId(),goodsIds);
		
	}
	
	private void addLabelGoodsList(String activityId,List<String> goodsIds) throws Exception{ 
		//批量保存关联商品信息
		if(CollectionUtils.isNotEmpty(goodsIds)){
			List<ActivityLabelGoods> list = new ArrayList<ActivityLabelGoods>();
			
			int i = 1;
			for (String str : goodsIds) {
				ActivityLabelGoods a = new ActivityLabelGoods();
				a.setId(UuidUtils.getUuid());
				a.setActivityId(activityId);
				a.setGoodsId(str);
				a.setSort(i);
				list.add(a);
				i++;
			}
			activityLabelGoodsMapper.addBatch(list);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void update(ActivityLabel activityLabel,List<String> goodsIds) throws Exception {
		//修改主表
		activityLabelMapper.update(activityLabel);
		//先删除老信息,再批量插入新信息
		activityLabelGoodsMapper.deleteByActivityId(activityLabel.getId());
		//批量保存商品关联信息
		addLabelGoodsList(activityLabel.getId(),goodsIds);
	}

	@Transactional(readOnly = true)
	public ActivityLabel findById(String id) {
		return activityLabelMapper.findById(id);
	}
	
	@Override
	@Transactional(readOnly = true)
	public PageUtils<ActivityLabel> list(Map<String, Object> map,int pageNumber,int pageSize) throws Exception{
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ActivityLabel> result = activityLabelMapper.list(map);
		return new PageUtils<ActivityLabel>(result);
	}
	
	@Override
	@Transactional(readOnly = true)
	public PageUtils<Map<String,Object>> listGoods(Map<String, Object> map,int pageNumber,int pageSize) throws Exception{
		PageHelper.startPage(pageNumber, pageSize, true);
		List<Map<String,Object>> result = activityLabelMapper.listGoods(map);
		return new PageUtils<Map<String,Object>>(result);
	}

	/**
	 * @desc 用于判断某个时间段内活动是否冲突
	 * @param map
	 * @return
	 */
	@Transactional(readOnly = true)
	public int countTimeQuantum(Map<String, Object> map) {
		return activityLabelMapper.countTimeQuantum(map);
	}

	/**
	 * 1、查询活动未开始，开始时间小于当前的数据 即为要设置开始，2、活动开始、结束时间小于当前的数据 即为要设置结束
	 * @author tuzhd
	 * @param map 传递参数
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ActivityLabel> listByJob(Map<String,Object> map) {
		return activityLabelMapper.listByJob(map);
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
		//修改主表信息
		ActivityLabel c = new ActivityLabel();
		c.setId(id);
		c.setStatus(status);
		c.setUpdateTime(updateTime);
		activityLabelMapper.update(c);
	}

	@Override
	public List<ActivityLabelGoods> listActivityLabelGoods(String activityId) throws Exception {
		return activityLabelGoodsMapper.listByActivityId(activityId);
	}
}