/** 
 *@Project: yschome-mall-activity 
 *@Author: zhongy
 *@Date: 2016年7月18日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */
package com.okdeer.mall.activity.recommend.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.goods.store.vo.GoodsStoreSkuRecommendActivityVo;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.common.consts.LogConstants;
import com.okdeer.mall.activity.recommend.entity.ActivityRecommend;
import com.okdeer.mall.activity.recommend.entity.ActivityRecommendGoods;
import com.okdeer.mall.activity.recommend.entity.ActivityRecommendRange;
import com.okdeer.mall.activity.recommend.entity.ActivityRecommendVo;
import com.okdeer.mall.activity.recommend.service.ActivityRecommendServiceApi;
import com.okdeer.mall.common.enums.AreaType;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.recommend.mapper.ActivityRecommendMapper;
import com.okdeer.mall.activity.recommend.service.ActivityRecommendService;

/**
 * ClassName: ActivityRecommendServiceImpl 
 * 推荐活动service实现类 
 * @author zhongy
 * @date 2016年7月18日
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构4.1          2016年7月18日                   zhongy             推荐活动mapper
 *     重构4.1          2016年7月28日                   zhongy             修改添加事务
 *
 */

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.recommend.service.ActivityRecommendServiceApi")
public class ActivityRecommendServiceImpl implements ActivityRecommendServiceApi, ActivityRecommendService {

	private static final Logger logger = LoggerFactory.getLogger(ActivityRecommendServiceImpl.class);

	/**
	 * 自动注入activityRecommendMapper
	 */
	@Autowired
	private ActivityRecommendMapper activityRecommendMapper;

	/**
	 * 自动注入storeInfoServiceApi
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoServiceApi;

	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;

	@Transactional(rollbackFor = ServiceException.class)
	@Override
	public PageUtils<ActivityRecommend> findByPage(ActivityRecommend activityRecommend, int pageNumber, int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ActivityRecommend> list = activityRecommendMapper.findActivityRecommend(activityRecommend);
		return new PageUtils<ActivityRecommend>(list);
	}

	@Transactional(rollbackFor = ServiceException.class)
	@Override
	public void save(ActivityRecommendVo activityRecommendVo) throws ServiceException {
		logger.info(LogConstants.INCOMING_METHOD, activityRecommendVo);
		// 1 添加推荐活动
		String activityRecommendId = addActivityRecommend(activityRecommendVo);
		// 2 添加推荐活动关联区域,只有区域范围为全国不添加
		AreaType areaType = activityRecommendVo.getRecommendRangeType();
		if (areaType == AreaType.area) {
			List<ActivityRecommendRange> activityRecommendRanges = activityRecommendVo.getActivityRecommendRanges();
			addActivityRecommendRange(activityRecommendRanges, activityRecommendId);
		}
		// 3 添加推荐活动关联商品
		List<String> skuIds = activityRecommendVo.getGoodsSkuIds();
		addActivityRecommendGoods(skuIds, activityRecommendId);
	}

	@Transactional(rollbackFor = ServiceException.class)
	@Override
	public Integer checkForm(ActivityRecommendVo activityRecommendVo) throws ServiceException {
		logger.info(LogConstants.INCOMING_METHOD, activityRecommendVo);
		String id = activityRecommendVo.getId();
		Date startTime = activityRecommendVo.getStartTime();
		Date endTime = activityRecommendVo.getEndTime();
		List<ActivityRecommendRange> areaIdList = activityRecommendVo.getActivityRecommendRanges();
		List<String> areaIds = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(areaIdList)) {
			for (ActivityRecommendRange activityRecommendRange : areaIdList) {
				areaIds.add(activityRecommendRange.getCityId());
			}
		}
		AreaType areaType = activityRecommendVo.getRecommendRangeType();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("startTime", startTime);
		params.put("endTime", endTime);
		params.put("areaIds", areaIds);
		params.put("areaType", areaType);
		return activityRecommendMapper.selectCountByDistrict(params);
	}

	/**
	 * 添加推荐活动关联商品
	 * @param skuIds 商品ids
	 * @param activityRecommendId 推荐活动id
	 * @throws ServiceException
	 */
	private void addActivityRecommendGoods(List<String> skuIds, String activityRecommendId) throws ServiceException {
		List<ActivityRecommendGoods> list = new ArrayList<ActivityRecommendGoods>();
		for (String skuId : skuIds) {
			ActivityRecommendGoods activityRecommendGoods = new ActivityRecommendGoods();
			activityRecommendGoods.setId(UuidUtils.getUuid());
			activityRecommendGoods.setGoodsSkuId(skuId);
			activityRecommendGoods.setActivityRecommendId(activityRecommendId);
			list.add(activityRecommendGoods);
		}
		if (CollectionUtils.isNotEmpty(list)) {
			activityRecommendMapper.insertBatchActivityRecommendGoods(list);
		}
	}

	/**
	 * 
	 * 批量添加推荐活动关联区域范围
	 * @param activityRecommendVo 
	 * @param activityRecommendId 推荐活动id
	 * @throws ServiceException
	 */
	private void addActivityRecommendRange(List<ActivityRecommendRange> activityRecommendRanges,
			String activityRecommendId) throws ServiceException {
		for (ActivityRecommendRange activityRecommendRange : activityRecommendRanges) {
			activityRecommendRange.setId(UuidUtils.getUuid());
			activityRecommendRange.setActivityRecommendId(activityRecommendId);
		}
		if (CollectionUtils.isNotEmpty(activityRecommendRanges)) {
			activityRecommendMapper.insertBatchActivityRecommendRange(activityRecommendRanges);
		}
	}

	/**
	* 添加推荐活动
	* @param activityRecommendVo
	* @return
	* @throws ServiceException
	*/
	private String addActivityRecommend(ActivityRecommendVo activityRecommendVo) throws ServiceException {
		ActivityRecommend activityRecommend = new ActivityRecommend();
		Date nowDate = new Date();
		String id = UuidUtils.getUuid();
		activityRecommend.setId(id);
		activityRecommend.setRecommendName(activityRecommendVo.getRecommendName());
		activityRecommend.setLinkUrl(activityRecommendVo.getLinkUrl());
		activityRecommend.setStartTime(activityRecommendVo.getStartTime());
		activityRecommend.setEndTime(activityRecommendVo.getEndTime());
		activityRecommend.setPicUrl(activityRecommendVo.getPicUrl());
		activityRecommend.setRecommendRangeType(activityRecommendVo.getRecommendRangeType());
		activityRecommend.setRecommendStatus(activityRecommendVo.getRecommendStatus());
		activityRecommend.setCreateTime(nowDate);
		activityRecommend.setCreateUserId(activityRecommendVo.getCreateUserId());
		activityRecommend.setUpdateTime(nowDate);
		activityRecommend.setUpdateUserId(activityRecommendVo.getUpdateUserId());
		activityRecommend.setDisabled(Disabled.valid);
		activityRecommendMapper.insert(activityRecommend);
		return id;
	}

	@Transactional(rollbackFor = ServiceException.class)
	@Override
	public void update(ActivityRecommendVo activityRecommendVo) throws ServiceException {
		logger.info(LogConstants.INCOMING_METHOD, activityRecommendVo);
		// 1 修改推荐活动
		activityRecommendMapper.updateByPrimaryKeySelective(activityRecommendVo);
		// 2 修改推荐活动关联范围
		updateActivityRecommendRange(activityRecommendVo);
		// 3 修改推荐活动关联商品
		updateActivityRecommendGoods(activityRecommendVo);
	}

	/**
	 * 修改推荐活动关联商品
	 * @param activityRecommendVo 请求参数
	 * @throws ServiceException
	 */
	private void updateActivityRecommendGoods(ActivityRecommendVo activityRecommendVo) throws ServiceException {
		// 1 删除推荐活动关联商品
		String activityRecommendId = activityRecommendVo.getId();
		activityRecommendMapper.deleteActivityRecommendGoods(activityRecommendId);
		// 2 添加推荐活动关联商品
		List<String> skuIds = activityRecommendVo.getGoodsSkuIds();
		addActivityRecommendGoods(skuIds, activityRecommendId);
	}

	/**
	 * 修改推荐活动关联范围
	 * @param activityRecommendVo
	 * @throws ServiceException
	 */
	private void updateActivityRecommendRange(ActivityRecommendVo activityRecommendVo) throws ServiceException {
		// 1 删除推荐活动关联范围
		String activityRecommendId = activityRecommendVo.getId();
		activityRecommendMapper.deleteActivityRecommendRange(activityRecommendId);
		// 2 添加推荐活动关联范围
		if (activityRecommendVo.getRecommendRangeType() == AreaType.area) {
			List<ActivityRecommendRange> activityRecommendRanges = activityRecommendVo.getActivityRecommendRanges();
			addActivityRecommendRange(activityRecommendRanges, activityRecommendId);
		}
	}

	@Transactional(rollbackFor = ServiceException.class)
	@Override
	public ActivityRecommend findById(String id) {
		return activityRecommendMapper.selectByPrimaryKey(id);
	}

	@Transactional(rollbackFor = ServiceException.class)
	@Override
	public List<ActivityRecommendRange> findActivityRecommendRange(String activityRecommendId) {
		return activityRecommendMapper.findActivityRecommendRange(activityRecommendId);
	}

	@Transactional(rollbackFor = ServiceException.class)
	@Override
	public void updateActivityRecommend(String id) throws ServiceException {
		activityRecommendMapper.closeActivityRecommend(id);
	}

	@Transactional(rollbackFor = ServiceException.class)
	@Override
	public List<ActivityRecommend> findList(ActivityRecommend activityRecommend) {
		return activityRecommendMapper.findActivityRecommend(activityRecommend);
	}

	@Transactional(rollbackFor = ServiceException.class)
	@Override
	public ActivityRecommend findActivityRecommend(Map<String, Object> params) throws ServiceException {
		// cityName:'', latitude:'', longitude:''
		// 1 根据城市名称查询用户选择城市是否有推荐活动，经纬度。
		ActivityRecommend activityRecommend = activityRecommendMapper.findByCurrentCityActivity(params);
		if (activityRecommend == null) {
			// 2 如果没有就查询推荐活动范围为全国是否存在。
			activityRecommend = activityRecommendMapper.findByNationActivity();
		}
		// 3 没有推荐活动返回空
		if (activityRecommend == null) {
			return new ActivityRecommend();
		}
		// 4判断该城市用户配送范围的便利店是否有参与特惠活动的推荐商品。(用户选择的地址经纬度)
		double longitude = (double) params.get("longitude");
		double latitude = (double) params.get("latitude");
		List<StoreInfo> stores = storeInfoServiceApi.findBytypeAndLocation(longitude, latitude,
				StoreTypeEnum.CLOUD_STORE);
		Integer count = 0;
		if (CollectionUtils.isNotEmpty(stores)) {
			List<String> storeIds = new ArrayList<String>();
			for (StoreInfo storeInfo : stores) {
				storeIds.add(storeInfo.getId());
			}
			count = goodsStoreSkuServiceApi.findCountByStoreIds(storeIds);
		}
		if (count == 0) {
			return new ActivityRecommend();
		}
		return activityRecommend;
	}

	@Transactional(rollbackFor = ServiceException.class)
	@Override
	public PageUtils<GoodsStoreSkuRecommendActivityVo> findGoodsStoreSku(Map<String, Object> params)
			throws ServiceException {
		Double longitude = (Double) params.get("longitude");
		Double latitude = (Double) params.get("latitude");
		List<StoreInfo> stores = storeInfoServiceApi.findBytypeAndLocation(longitude, latitude,
				StoreTypeEnum.CLOUD_STORE);
		Integer count = 0;
		String storeId = "";
		if (CollectionUtils.isNotEmpty(stores)) {
			for (StoreInfo storeInfo : stores) {
				count = goodsStoreSkuServiceApi.findCountByStoreId(storeInfo.getId());
				if (count > 0) {
					storeId = storeInfo.getId();
					break;
				}
			}
		}
		if (StringUtils.isNotBlank(storeId)) {
			Integer pageNumber = (Integer) params.get("pageNumber");
			Integer pageSize = (Integer) params.get("pageSize");
			return goodsStoreSkuServiceApi.findGoodsStoreSkuByStoreId(storeId, pageNumber, pageSize);
		} else {
			return null;
		}
	}
}
