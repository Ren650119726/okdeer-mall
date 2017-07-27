package com.okdeer.mall.activity.label.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import com.okdeer.mall.activity.label.enums.ActivityLabelStatus;
import com.okdeer.mall.activity.label.mapper.ActivityLabelGoodsMapper;
import com.okdeer.mall.activity.label.mapper.ActivityLabelMapper;
import com.okdeer.mall.activity.label.service.ActivityLabelService;
import com.okdeer.mall.common.utils.RobotUserUtil;

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

	@Override
	public Map<String,List<String>> listLabelNameBySkuIds(List<String> skuIdList) {
		//所有商品的标签集合
		Map<String,List<String>> labelList = new LinkedHashMap<String, List<String>>();
		
		List<Map<String,Object>> allLabelList = activityLabelMapper.listLabelNameBySkuIds(skuIdList);
		if(CollectionUtils.isNotEmpty(allLabelList)){
			for(String id : skuIdList){
				//每个商品的标签集合
				List<String> goodsLabelList = new ArrayList<String>();
				for(Map<String,Object> label : allLabelList){
					if(id.equals(label.get("skuId").toString())){
						goodsLabelList.add(label.get("labelName") != null ?
							label.get("labelName").toString() : "");
					}
				}
				labelList.put(id, goodsLabelList);
			}
		}
		return labelList;
	}
	
	@Override
	public List<Map<String,Object>> findLabelBySkuId(String skuId) {
		return activityLabelMapper.findLabelBySkuId(skuId);
	}
	
	/**
	 * 执行服务标签管理 JOB 任务
	 * @Description:   
	 * @return void  
	 * @throws
	 * @author tuzhd
	 * @date 2016年11月16日
	 */
	public void processLabelJob(){
		try{
			log.info("服务标签管理定时器开始");
			Map<String,Object> map = new HashMap<String,Object>();
			Date nowTime = new Date();
			map.put("nowTime", nowTime);
			//1、查询活动未开始，开始时间小于当前的数据 即为要设置开始，2、活动开始、结束时间小于当前的数据 即为要设置结束
			List<ActivityLabel> accList = listByJob(map);
			//获得系统当前系统用户id
			String updateUserId = RobotUserUtil.getRobotUser().getId();
			//需要更新状态的活动新不为空进行定时任务处理
			if(CollectionUtils.isNotEmpty(accList)){
				for(ActivityLabel a : accList){
					try{
						//未开始的 
						if(ActivityLabelStatus.noStart.getValue().equals(a.getStatus())){
							//根据id修改服务标签活动状态
							updateStatusById(a.getId(),  ActivityLabelStatus.ing.getValue(), updateUserId, nowTime);
						
						//进行中的改为已结束的
						}else if(ActivityLabelStatus.ing.getValue().equals(a.getStatus())){
							//根据id修改服务标签活动状态
							updateStatusById(a.getId(),  ActivityLabelStatus.end.getValue(), updateUserId, nowTime);
						}
					}catch(Exception e){
						log.error(a.getStatus()+"状态服务标签管理"+a.getId()+"job修改异常 :",e);
					}
				}
			}
			log.info("服务标签管理定时器结束");
		}catch(Exception e){
			log.error("服务标签管理job异常",e);
		}
	}
}