package com.okdeer.mall.activity.serviceGoodsRecommend.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageHelper;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.serviceGoodsRecommend.entity.ActivityServiceGoodsRecommend;
import com.okdeer.mall.activity.serviceGoodsRecommend.entity.ActivityServiceGoodsRecommendArea;
import com.okdeer.mall.activity.serviceGoodsRecommend.entity.ActivityServiceGoodsRecommendGoods;
import com.okdeer.mall.activity.serviceGoodsRecommend.enums.ActivityServiceGoodsRecommendStatus;
import com.okdeer.mall.activity.serviceGoodsRecommend.mapper.ActivityServiceGoodsRecommendAreaMapper;
import com.okdeer.mall.activity.serviceGoodsRecommend.mapper.ActivityServiceGoodsRecommendGoodsMapper;
import com.okdeer.mall.activity.serviceGoodsRecommend.mapper.ActivityServiceGoodsRecommendMapper;
import com.okdeer.mall.activity.serviceGoodsRecommend.service.ActivityServiceGoodsRecommendService;
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
public class ActivityServiceGoodsRecommendServiceImpl extends BaseServiceImpl
		implements ActivityServiceGoodsRecommendService{

	private static final Logger log = Logger.getLogger(ActivityServiceGoodsRecommendServiceImpl.class);

	@Autowired
	private ActivityServiceGoodsRecommendMapper recommendMapper;
	@Autowired
	private ActivityServiceGoodsRecommendGoodsMapper recommendGoodsMapper;
	@Autowired
	private ActivityServiceGoodsRecommendAreaMapper recommendAreaMapper;
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;
	
	
	@Override
	public IBaseMapper getBaseMapper() {
		return recommendMapper;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void add(ActivityServiceGoodsRecommend obj,String sorts,List<String> goodsIds,String areaIds) throws Exception {
		// 先保存活动主对象
		recommendMapper.add(obj);
		
		//批量保存商品关联信息
		addRecommendGoodsList(obj.getId(),sorts,goodsIds,areaIds,obj.getAreaType());
	}
	
	private void addRecommendGoodsList(String activityId,String sorts,List<String> goodsIds,String areaIds,Integer areaType) throws Exception{ 
		//批量保存关联商品信息
		if(CollectionUtils.isNotEmpty(goodsIds)){
			List<ActivityServiceGoodsRecommendGoods> list = new ArrayList<ActivityServiceGoodsRecommendGoods>();
			
			String[] sortArray = null;
			//排序值的数组,和goodsIds匹配
			if(StringUtils.isNotEmpty(sorts)){
				sortArray = sorts.split(",");
			}
			int i = 0;
			for (String str : goodsIds) {
				//关联的多个商品对象
				ActivityServiceGoodsRecommendGoods a = new ActivityServiceGoodsRecommendGoods();
				a.setId(UuidUtils.getUuid());
				a.setActivityId(activityId);
				a.setGoodsId(str);
				//排序字段
				if(sortArray != null){
					if(StringUtils.isEmpty(sortArray[i])){
						a.setSort(0);
					} else {
						a.setSort(Integer.parseInt(sortArray[i]));
					}
				}
				list.add(a);
				i++;
			}
			recommendGoodsMapper.addBatch(list);
		}
		
		//批量保存地区信息
		if(StringUtils.isNoneEmpty(areaIds) && areaType == 1){
			String[] array = areaIds.split(",");

			List<ActivityServiceGoodsRecommendArea> areaList = new ArrayList<ActivityServiceGoodsRecommendArea>();
			for (String str : array) {
				ActivityServiceGoodsRecommendArea a = new ActivityServiceGoodsRecommendArea();
				a.setId(UuidUtils.getUuid());
				a.setActivityId(activityId);
				a.setType(Integer.parseInt(str.split("-")[1]));
				a.setAreaId(str.split("-")[0]);
				areaList.add(a);
			}
			recommendAreaMapper.addBatch(areaList);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void update(ActivityServiceGoodsRecommend obj,String sorts,List<String> goodsIds,String areaIds) throws Exception {
		//修改主表
		recommendMapper.update(obj);
		//先删除老信息,再批量插入新信息
		recommendGoodsMapper.deleteByActivityId(obj.getId());
		recommendAreaMapper.deleteByActivityId(obj.getId());
		//批量保存商品关联信息
		addRecommendGoodsList(obj.getId(),sorts,goodsIds,areaIds,obj.getAreaType());
	}

	@Transactional(readOnly = true)
	@Override
	public ActivityServiceGoodsRecommend findById(String id) {
		return recommendMapper.findById(id);
	}
	
	@Override
	@Transactional(readOnly = true)
	public PageUtils<ActivityServiceGoodsRecommend> list(Map<String, Object> map,int pageNumber,int pageSize) throws Exception{
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ActivityServiceGoodsRecommend> result = recommendMapper.list(map);
		return new PageUtils<ActivityServiceGoodsRecommend>(result);
	}
	
	@Override
	@Transactional(readOnly = true)
	public PageUtils<Map<String,Object>> listGoods(Map<String, Object> map,int pageNumber,int pageSize) throws Exception{
		PageHelper.startPage(pageNumber, pageSize, true);
		List<Map<String,Object>> result = recommendMapper.listGoods(map);
		return new PageUtils<Map<String,Object>>(result);
	}

	/**
	 * @desc 用于判断某个时间段内活动是否冲突
	 * @param map
	 * @return
	 */
	@Transactional(readOnly = true)
	public int countTimeQuantum(Map<String, Object> map) {
		return recommendMapper.countTimeQuantum(map);
	}

	/**
	 * 1、查询活动未开始，开始时间小于当前的数据 即为要设置开始，2、活动开始、结束时间小于当前的数据 即为要设置结束
	 * @Description: TODO
	 * @param map 传递查询参数
	 * @return List<ActivityServiceGoodsRecommend>  
	 * @author tuzhd
	 * @date 2016年11月12日
	 */
	@Transactional(readOnly = true)
	public List<ActivityServiceGoodsRecommend>  listByJob(Map<String,Object> map) {
		return recommendMapper.listByJob(map);
	}
	
	/**
	 * @Description: 根据id修改服务商品活动状态
	 * @param id
	 * @param status 活动状态 0 未开始 ，1：进行中2:已结束 3 已关闭
	 * @param updateUserId 修改人
	 * @param updateTime 修改时间
	 * @throws Exception
	 * @author tuzhiding
	 * @date 2016年11月4日
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateStatusById(String id, int status, String updateUserId, Date updateTime) throws Exception {
		//修改主表信息
		ActivityServiceGoodsRecommend c = new ActivityServiceGoodsRecommend();
		c.setId(id);
		c.setStatus(status);
		c.setUpdateTime(updateTime);
		recommendMapper.update(c);
	}

	@Override
	public List<ActivityServiceGoodsRecommendGoods> listActivityGoods(String activityId) throws Exception {
		return recommendGoodsMapper.listByActivityId(activityId);
	}

	@Override
	public List<ActivityServiceGoodsRecommendArea> listActivityArea(String activityId) throws Exception {
		return recommendAreaMapper.listByActivityId(activityId);
	}

	@Override
	public List<Map<String, Object>> listRecommendGoodsFowWx(Map<String, Object> map) {
		return recommendMapper.listRecommendGoodsFowWx(map);
	}

	/**
	 * 服务商品推荐定时器 job 执行方法
	 * @Description: TODO   
	 * @return void  
	 * @throws
	 * @author tuzhd
	 * @date 2016年11月16日
	 */
	public void processServiceGoodsJob(){
		try{
			log.info("服务商品推荐定时器开始");
			
			Map<String,Object> map = new HashMap<String,Object>();
			Date nowTime = new Date();
			map.put("nowTime", nowTime);
			//1、查询活动未开始，开始时间小于当前的数据 即为要设置开始，2、活动开始、结束时间小于当前的数据 即为要设置结束
			List<ActivityServiceGoodsRecommend> accList = listByJob(map);
			//获得系统当前系统用户id
			String updateUserId = RobotUserUtil.getRobotUser().getId();
			
			
			//需要更新状态的活动新不为空进行定时任务处理
			if(CollectionUtils.isNotEmpty(accList)){
				for(ActivityServiceGoodsRecommend a : accList){
					try{
						//未开始的 
						if(ActivityServiceGoodsRecommendStatus.noStart.getValue().equals(a.getStatus())){
							//根据id修改服务商品活动状态
							updateStatusById(a.getId(), ActivityServiceGoodsRecommendStatus.ing.getValue(), updateUserId, nowTime);
						
						//进行中的改为已结束的
						}else if(ActivityServiceGoodsRecommendStatus.ing.getValue().equals(a.getStatus())){
							//根据id修改服务商品活动状态
							updateStatusById(a.getId(), ActivityServiceGoodsRecommendStatus.end.getValue(), updateUserId, nowTime);
						}
					}catch(Exception e){
						log.error(a.getStatus()+"状态服务标签管理"+a.getId()+"job修改异常 :",e);
					}
				}
			}
			
			log.info("服务商品推荐定时器结束");
		}catch(Exception e){
			log.error("服务商品推荐job异常",e);
		}
	}
}