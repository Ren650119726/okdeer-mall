package com.okdeer.mall.activity.serviceLimit.service;

import java.util.List;
import java.util.Map;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.serviceLimit.entity.ActivityServiceLimit;
import com.okdeer.mall.activity.serviceLimit.entity.ActivityServiceLimitGoods;

/**
 * ClassName: ActivityLimitService 
 * @Description: TODO
 * @author zhangkn
 * @date 2016年12月9日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.3			 2016年12月9日 			zhagnkn
 *		v1.3.0          2016-12-17           zengjz          增加根据店铺id查询限购活动方法
 */
public interface ActivityServiceLimitService extends IBaseService{
	
	/**
	 * @Description: 添加促销活动
	 * @param activityLimit
	 * @param algList
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年12月12日
	 */
	void add(ActivityServiceLimit activityLimit, List<ActivityServiceLimitGoods> algList) throws Exception;

	/**
	 * @Description: 编辑促销活动
	 * @param activityLimit
	 * @param algList
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年12月12日
	 */
	void update(ActivityServiceLimit activityLimit, List<ActivityServiceLimitGoods> algList) throws Exception;

	/**
	 * @Description: 通过id加载对象
	 * @param id
	 * @return
	 * @author zhangkn
	 * @date 2016年12月12日
	 */
	ActivityServiceLimit findById(String id);

	/**
	 * @Description: 分页查询列表
	 * @param map
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 * @author zhangkn
	 * @date 2016年12月12日
	 */
	PageUtils<ActivityServiceLimit> pageList(Map<String, Object> map, Integer pageNumber, Integer pageSize);

	/**
	 * @Description: 批量修改状态
	 * @param ids
	 * @param status
	 * @param storeId
	 * @param createUserId
	 * @throws Exception
	 * @author zhangkn
	 * @date 2016年12月12日
	 */
	void updateBatchStatus(List<String> ids, int status, String storeId, String createUserId) throws Exception;

	/**
	 * @Description: 商品列表
	 * @param map
	 * @return
	 * @author zhangkn
	 * @date 2016年12月12日
	 */
	List<Map<String, Object>> listGoodsStoreSku(Map<String, Object> map);

	/**
	 * @Description: 商品列表分页
	 * @param map
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 * @author zhangkn
	 * @date 2016年12月12日
	 */
	PageUtils<Map<String, Object>> pageListGoodsStoreSku(Map<String, Object> map, Integer pageNumber, Integer pageSize);

	/**
	 * @Description: 通过活动id加载关联商品列表
	 * @param activityId
	 * @return
	 * @author zhangkn
	 * @date 2016年12月12日
	 */
	List<ActivityServiceLimitGoods> listActivityLimitGoods(String activityId);

	/**
	 * 判断某个时间段内是否存在特惠活动
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	int validateExist(Map<String, Object> map);
	
	/**
	 * @Description: 处理定时器
	 * @author zhangkn
	 * @date 2016年12月12日
	 */
	void processJob();
	
	/**
	 * @Description: 根据店铺id查询限购活动
	 * @param storeIdList 店铺id列表
	 * @return  活动列表
	 * @author zengjizu
	 * @date 2016年12月17日
	 */
	List<ActivityServiceLimit> findByStoreIds(List<String> storeIdList);
}
