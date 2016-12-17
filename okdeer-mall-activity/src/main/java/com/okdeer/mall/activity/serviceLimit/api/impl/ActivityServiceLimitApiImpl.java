
package com.okdeer.mall.activity.serviceLimit.api.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.activity.serviceLimit.dto.StoreActivityLimitDto;
import com.okdeer.mall.activity.serviceLimit.entity.ActivityServiceLimit;
import com.okdeer.mall.activity.serviceLimit.entity.ActivityServiceLimitGoods;
import com.okdeer.mall.activity.serviceLimit.service.ActivityServiceLimitApi;
import com.okdeer.mall.activity.serviceLimit.service.ActivityServiceLimitService;

@Service(version = "1.0.0")
public class ActivityServiceLimitApiImpl implements ActivityServiceLimitApi {

	@Autowired
	ActivityServiceLimitService limitService;

	@Reference(version = "1.0.0")
	private StoreInfoServiceApi storeInfoServiceApi;

	@Override
	public void add(ActivityServiceLimit activityLimit, List<ActivityServiceLimitGoods> asgList) throws Exception {
		limitService.add(activityLimit, asgList);
	}

	public void update(ActivityServiceLimit activityLimit, List<ActivityServiceLimitGoods> asgList) throws Exception {
		limitService.update(activityLimit, asgList);
	}

	@Override
	public ActivityServiceLimit findById(String id) {
		return limitService.findById(id);
	}

	@Override
	public PageUtils<ActivityServiceLimit> pageList(Map<String, Object> map, Integer pageNumber, Integer pageSize) {
		return limitService.pageList(map, pageNumber, pageSize);
	}

	@Override
	public void updateBatchStatus(List<String> ids, int status, String storeId, String createUserId) throws Exception {
		limitService.updateBatchStatus(ids, status, storeId, createUserId);
	}

	@Override
	public List<Map<String, Object>> listGoodsStoreSku(Map<String, Object> map) {
		return limitService.listGoodsStoreSku(map);
	}

	@Override
	public PageUtils<Map<String, Object>> pageListGoodsStoreSku(Map<String, Object> map, Integer pageNumber,
			Integer pageSize) {
		return limitService.pageListGoodsStoreSku(map, pageNumber, pageSize);
	}

	@Override
	public List<ActivityServiceLimitGoods> listActivityLimitGoods(String activityId) {
		return limitService.listActivityLimitGoods(activityId);
	}

	/**
	 * 判断某个时间段内是否存在特惠活动
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Override
	public int validateExist(Map<String, Object> map) {
		return limitService.validateExist(map);
	}

	@Override
	public void processJob() {
		limitService.processJob();
	}

	@Override
	public List<StoreActivityLimitDto> findStoreActivityLimit(List<String> storeIdList) {
		
		//根据店铺id查询限购活动
		List<ActivityServiceLimit> list = limitService.findByStoreIds(storeIdList);

		List<StoreActivityLimitDto> dtoList = Lists.newArrayList();
		StoreActivityLimitDto storeActivityLimitDto = null;

		if (CollectionUtils.isEmpty(list)) {
			return dtoList;
		}

		for (ActivityServiceLimit activityServiceLimit : list) {
			storeActivityLimitDto = new StoreActivityLimitDto();
			storeActivityLimitDto.setActiveId(activityServiceLimit.getId());
			storeActivityLimitDto.setActiveName(activityServiceLimit.getName());
			storeActivityLimitDto.setLimitNum(activityServiceLimit.getLimit());
			storeActivityLimitDto.setStoreId(activityServiceLimit.getStoreId());

			//查询店铺信息
			StoreInfo storeInfo = storeInfoServiceApi.findById(activityServiceLimit.getStoreId());
			if (storeInfo != null) {
				storeActivityLimitDto.setStoreName(storeInfo.getStoreName());
				storeActivityLimitDto.setStoreLogo(storeInfo.getLogoUrl());
			}
			dtoList.add(storeActivityLimitDto);
			storeActivityLimitDto = null;
		}
		return dtoList;
	}
}
