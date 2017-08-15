package com.okdeer.mall.activity.serviceGoodsRecommend.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.serviceGoodsRecommend.entity.ActivityServiceGoodsRecommend;
import com.okdeer.mall.activity.serviceGoodsRecommend.entity.ActivityServiceGoodsRecommendArea;
import com.okdeer.mall.activity.serviceGoodsRecommend.entity.ActivityServiceGoodsRecommendGoods;
/**
 * 
 * ClassName: ActivityServiceGoodsRecommendService 
 * @Description: 服务商品推荐
 * @author YSCGD
 * @date 2016年11月9日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.1			 2016年11月9日 			zhagnkn
 */
public interface ActivityServiceGoodsRecommendService extends IBaseService{
	/**
	 * @Description: 保存
	 * @param obj 服务商品推荐对象
	 * @param goodsIds 商品id集合
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年11月4日
	 */
	void add(ActivityServiceGoodsRecommend obj,String sorts,List<String> goodsIds,String areaIds) throws Exception;
	

	/**
	 * @Description: 修改
	 * @param obj 服务商品推荐对象
	 * @param goodsIds
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年11月4日
	 */
	void update(ActivityServiceGoodsRecommend obj,String sorts,List<String> goodsIds,String areaIds) throws Exception;

	/**
	 * @Description: 通过id取对象
	 * @param id
	 * @return
	 * @author zhangkn
	 * @date 2016年11月4日
	 */
	ActivityServiceGoodsRecommend findById(String id);
	
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
	PageUtils<ActivityServiceGoodsRecommend> list(Map<String,Object> map,int pageNumber,int pageSize) throws Exception;
	
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
	PageUtils<Map<String,Object>> listGoods(Map<String,Object> map,int pageNumber,int pageSize) throws Exception;
	
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
	void updateStatusById(String id,int status,String updateUserId,Date updateTime) throws Exception;
	
	/**
	 * @desc 用于判断某个时间段内活动是否冲突
	 * @param map
	 * @return
	 */
	int countTimeQuantum(Map<String,Object> map);
	
	/**
	 * 1、查询活动未开始，开始时间小于当前的数据 即为要设置开始，2、活动开始、结束时间小于当前的数据 即为要设置结束
	 * @param map 传递查询参数
	 * @return List<ActivityServiceGoodsRecommend>  
	 * @author tuzhd
	 * @date 2016年11月12日
	 */
	List<ActivityServiceGoodsRecommend>  listByJob(Map<String,Object> map);
	
	/**
	 * @Description: 通过活动id获取关联商品列表
	 * @param activityId
	 * @return
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年11月7日
	 */
	List<ActivityServiceGoodsRecommendGoods> listActivityGoods(String activityId) throws Exception;
	
	/**
	 * @Description: 通过活动id获取关联地区列表
	 * @param activityId
	 * @return
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年11月7日
	 */
	List<ActivityServiceGoodsRecommendArea> listActivityArea(String activityId) throws Exception;
	
	 /**
	 * @Description: 查询微信推荐商品列表
	 * @param map 传递查询参数
	 * @return  List<Map<String,Object>>
	 * @author zhangkn
	 * @date 2016年11月14日
	 */
	List<Map<String,Object>> listRecommendGoodsFowWx(Map<String,Object> map);
	
	/**
	 * @Description: 服务商品推荐定时器 job 执行方法   
	 * @return void  
	 * @throws
	 * @author tuzhd
	 * @date 2016年11月16日
	 */
	void processServiceGoodsJob();
}
