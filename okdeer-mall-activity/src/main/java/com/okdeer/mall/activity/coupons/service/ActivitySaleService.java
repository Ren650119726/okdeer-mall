package com.okdeer.mall.activity.coupons.service;

import java.util.List;
import java.util.Map;

import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;
import com.yschome.base.common.utils.PageUtils;

/**
 * @pr yscm
 * @desc 特惠活动service
 * @author zhangkn
 * @date 2016-1-26 下午3:12:43
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 */
public interface ActivitySaleService {

	void save(ActivitySale activitySale, List<ActivitySaleGoods> asgList) throws Exception;

	void update(ActivitySale ActivitySale, List<ActivitySaleGoods> asgList) throws Exception;

	ActivitySale get(String id);

	PageUtils<ActivitySale> pageList(Map<String, Object> map, Integer pageNumber, Integer pageSize);

	void updateBatchStatus(List<String> ids, int status, String storeId, String createUserId, List<ActivitySale> asList)
			throws Exception;

	List<Map<String, Object>> listGoodsStoreSku(Map<String, Object> map);

	PageUtils<Map<String, Object>> pageListGoodsStoreSku(Map<String, Object> map, Integer pageNumber, Integer pageSize);

	List<ActivitySaleGoods> listActivitySaleGoods(String activitySaleId);

	/**
	 * 判断某个时间段内是否存在特惠活动
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	int validateExist(Map<String, Object> map);

	void deleteActivitySaleGoods(String storeId, String createUserId, String activitySaleGoodsId, String goodsStoreSkuId)
			throws Exception;

	/**
	 * 执行定时任务
	 */
	void updateByTask() throws Exception;

	/**
	 * 
	 * 根据直接查询已经开始的活动
	 *
	 * @param id
	 * @return
	 */
	ActivitySale findByPrimaryKey(String id);

	ActivitySale getAcSaleStatus(String activityId);
	
	int selectActivitySale(String activityId);
}
