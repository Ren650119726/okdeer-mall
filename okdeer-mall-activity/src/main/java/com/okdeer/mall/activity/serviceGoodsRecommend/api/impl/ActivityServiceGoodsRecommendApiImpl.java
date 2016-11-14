package com.okdeer.mall.activity.serviceGoodsRecommend.api.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.activity.label.service.ActivityLabelService;
import com.okdeer.mall.activity.serviceGoodsRecommend.entity.ActivityServiceGoodsRecommend;
import com.okdeer.mall.activity.serviceGoodsRecommend.entity.ActivityServiceGoodsRecommendArea;
import com.okdeer.mall.activity.serviceGoodsRecommend.entity.ActivityServiceGoodsRecommendGoods;
import com.okdeer.mall.activity.serviceGoodsRecommend.service.ActivityServiceGoodsRecommendService;
import com.okdeer.mall.activity.serviceGoodsRecommend.service.ActivityServiceGoodsRecommendApi;

@Service(version="1.0.0")
public class ActivityServiceGoodsRecommendApiImpl implements ActivityServiceGoodsRecommendApi{

	@Autowired
	ActivityServiceGoodsRecommendService recommendService;
	@Autowired
	ActivityLabelService labelService;
	
	/**
	 * @Description: 保存
	 * @param obj 服务商品推荐对象
	 * @param goodsIds 商品id集合
	 * @throws Exception
	 * @author tuzhiding
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
	 * @Description: 根据id修改服务商品活动状态
	 * @param id
	 * @param status 活动状态 0 未开始 ，1：进行中2:已结束 3 已关闭
	 * @param updateUserId 修改人
	 * @param updateTime 修改时间
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年11月4日
	 */
	public void updateStatusById(String id,int status,String updateUserId,Date updateTime) throws Exception{
		recommendService.updateStatusById(id, status, updateUserId, updateTime);
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
	 * 1、查询活动未开始，开始时间小于当前的数据 即为要设置开始，2、活动开始、结束时间小于当前的数据 即为要设置结束
	 * @Description: TODO
	 * @param map 传递查询参数
	 * @return List<ActivityServiceGoodsRecommend>  
	 * @author tuzhd
	 * @date 2016年11月12日
	 */
	public List<ActivityServiceGoodsRecommend>  listByJob(Map<String,Object> map){
		return recommendService.listByJob(map);
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
	
	/**
	 * @Description: 查询微信推荐商品列表
	 * @param map 传递查询参数
	 * @return  List<Map<String,Object>>
	 * @author zhangkn
	 * @date 2016年11月14日
	 */
	public List<Map<String,Object>> listRecommendGoodsFowWx(Map<String,Object> map){
		List<Map<String,Object>> goodsList = recommendService.listRecommendGoodsFowWx(map);
		//把商品id累加出来,一次得到所有商品所属的标签,避免多次连接数据库
		if(CollectionUtils.isNotEmpty(goodsList)){
			List<String> skuIdList = new ArrayList<String>();
			for(Map<String,Object> goods : goodsList){
				skuIdList.add(goods.get("goodsStoreSkuId").toString());
			}
			
			//所有商品的标签集合
			Map<String,List<String>> allLabelMap  = labelService.listLabelNameBySkuIds(skuIdList);
			if(allLabelMap != null && allLabelMap.size() > 0){
				//每个商品的标签集合
				for(Map<String,Object> goods : goodsList){
					goods.put("labelList", allLabelMap.get(goods.get("goodsStoreSkuId").toString()));
				}
			}
		}
		return goodsList;
	}
}
