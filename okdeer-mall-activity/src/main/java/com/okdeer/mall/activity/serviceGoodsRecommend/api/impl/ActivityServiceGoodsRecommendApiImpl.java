package com.okdeer.mall.activity.serviceGoodsRecommend.api.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.activity.serviceGoodsRecommend.entity.ActivityServiceGoodsRecommend;
import com.okdeer.mall.activity.serviceGoodsRecommend.entity.ActivityServiceGoodsRecommendArea;
import com.okdeer.mall.activity.serviceGoodsRecommend.entity.ActivityServiceGoodsRecommendGoods;
import com.okdeer.mall.activity.serviceGoodsRecommend.service.ActivityServiceGoodsRecommendService;
import com.okdeer.mall.activity.serviceGoodsRecommend.service.ActivityServiceGoodsRecommendApi;

@Service(version="1.0.0")
public class ActivityServiceGoodsRecommendApiImpl implements ActivityServiceGoodsRecommendApi{

	@Autowired
	ActivityServiceGoodsRecommendService recommendService;
	
	/**
	 * @Description: 保存
	 * @param obj 服务商品推荐对象
	 * @param goodsIds 商品id集合
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年11月4日
	 */
	public void add(ActivityServiceGoodsRecommend obj,String sorts,List<String> goodsIds,String areaIds) throws Exception{
		recommendService.add(obj,sorts, goodsIds, areaIds);
	}

	/**
	 * @Description: 修改
	 * @param obj 服务商品推荐对象
	 * @param goodsIds
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年11月4日
	 */
	public void update(ActivityServiceGoodsRecommend obj,String sorts,List<String> goodsIds,String areaIds) throws Exception{
		recommendService.update(obj,sorts, goodsIds, areaIds);
	}

	/**
	 * @Description: 通过id取对象
	 * @param id
	 * @return
	 * @author zhangkn
	 * @date 2016年11月4日
	 */
	public ActivityServiceGoodsRecommend findById(String id){
		return recommendService.findById(id);
	}
	
	/**
	 * @Description: 查询服务商品推荐列表
	 * @param map 参数
	 * @param pageNumber 当前页码
	 * @param pageSize 每页条数
	 * @return PageUtils<ActivityCollectCoupons>  
	 * @throws ServiceException service异常
	 * @author zhangkn
	 * @date 2016年7月13日
	 */
	public PageUtils<ActivityServiceGoodsRecommend> list(Map<String,Object> map,int pageNumber,int pageSize) throws Exception{
		return recommendService.list(map, pageNumber, pageSize);
	}
	
	/**
	 * @Description: 查询可选商品列表
	 * @param map 参数
	 * @param pageNumber 当前页码
	 * @param pageSize 每页条数
	 * @return PageUtils<ActivityCollectCoupons>  
	 * @throws Exception service异常
	 * @author zhangkn
	 * @date 2016年7月13日
	 */
	public PageUtils<Map<String,Object>> listGoods(Map<String,Object> map,int pageNumber,int pageSize) throws Exception{
		return recommendService.listGoods(map, pageNumber, pageSize);
	}
	
	/**
	 * @Description: 批量修改状态
	 * @param id
	 * @param status 活动状态 0 未开始 ，1：进行中2:已结束 3 已关闭
	 * @param updateUserId 修改人
	 * @param updateTime 修改时间
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年11月4日
	 */
	public void updateBatchStatus(String id,int status,String updateUserId,Date updateTime) throws Exception{
		recommendService.updateBatchStatus(id, status, updateUserId, updateTime);
	}
	
	/**
	 * @desc 用于判断某个时间段内活动是否冲突
	 * @param map
	 * @return
	 */
	public int countTimeQuantum(Map<String,Object> map){
		return recommendService.countTimeQuantum(map);
	}
	
	/**
	 * @desc 查询出需要跑job的活动
	 * @return
	 */
	public List<ActivityServiceGoodsRecommend> listByJob(){
		return recommendService.listByJob();
	}
	
	/**
	 * @Description: 通过活动id获取关联商品列表
	 * @param activityId
	 * @return
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年11月7日
	 */
	public List<ActivityServiceGoodsRecommendGoods> listActivityGoods(String activityId) throws Exception{
		return recommendService.listActivityGoods(activityId);
	}
	
	/**
	 * @Description: 通过活动id获取关联地区列表
	 * @param activityId
	 * @return
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年11月7日
	 */
	public List<ActivityServiceGoodsRecommendArea> listActivityArea(String activityId) throws Exception{
		return recommendService.listActivityArea(activityId);
	}
}
