
package com.okdeer.mall.activity.group.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.archive.goods.spu.vo.ActivityGroupGoodsVo;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.archive.stock.service.StockManagerJxcServiceApi;
import com.okdeer.archive.stock.vo.AdjustDetailVo;
import com.okdeer.archive.stock.vo.StockAdjustVo;
import com.okdeer.mall.activity.group.entity.ActivityGroupGoods;
import com.okdeer.mall.activity.group.mapper.ActivityGroupGoodsMapper;
import com.okdeer.mall.activity.group.mapper.ActivityGroupMapper;
import com.okdeer.mall.activity.group.service.ActivityGroupGoodsService;
import com.okdeer.mall.activity.group.service.ActivityGroupGoodsServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;

/**
 * 
 * 
 * @pr mall
 * @desc 团购活动 Service
 * @author chenwj
 * @date 2016年1月6日 下午5:21:17
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     1.0.Z	          2016年9月07日                 zengj              库存管理修改，采用商业管理系统校验
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.group.service.ActivityGroupGoodsServiceApi")
public class ActivityGroupGoodsServiceImpl implements ActivityGroupGoodsServiceApi, ActivityGroupGoodsService {

	private static final Logger logger = LoggerFactory.getLogger(ActivityGroupGoodsServiceImpl.class);

	/**
	 * activityGroupGoodsMapper
	 */
	@Autowired
	private ActivityGroupGoodsMapper activityGroupGoodsMapper;

	@Autowired
	private ActivityGroupMapper activityGroupMapper;

	// @Reference(version = "1.0.0", check = false)
	// private StockManagerServiceApi stockManagerServiceApi;

	// Begin 1.0.Z add by zengj
	/**
	 * 库存管理Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StockManagerJxcServiceApi stockManagerServiceApi;
	// End 1.0.Z add by zengj

	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;

	@Value("${storeImagePrefix}")
	private String storeImagePrefix;

	@Override
	public void insert(ActivityGroupGoods activityGroupGoods) throws ServiceException {
		activityGroupGoodsMapper.insert(activityGroupGoods);
	}

	@Override
	public void deleteByPrimaryKey(String id) throws ServiceException {
		activityGroupGoodsMapper.deleteByPrimaryKey(id);
	}

	@Override
	public List<ActivityGroupGoods> getActivityGroupGoods(String groupId) {
		return activityGroupGoodsMapper.getActivityGroupGoods(groupId);
	}

	@Override
	public ActivityGroupGoods selectByPrimaryKey(String id) {
		return activityGroupGoodsMapper.selectByPrimaryKey(id);
	}

	@Override
	public PageUtils<ActivityGroupGoods> findActivityGroupGoods(ActivityGroupGoods activityGroupGoods, int pageNumber,
			int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ActivityGroupGoods> list = activityGroupGoodsMapper.findActivityGroupGoods(activityGroupGoods);
		if (list != null && list.size() > 0) {
			for (ActivityGroupGoods goods : list) {
				goods.setGoodsPic(storeImagePrefix + goods.getGoodsPic());
			}
		}
		return new PageUtils<ActivityGroupGoods>(list);
	}

	@Override
	public List<ActivityGroupGoods> findActivityGroupGoodsByParam(Map<String, Object> map, int pageNumber,
			int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ActivityGroupGoods> list = activityGroupGoodsMapper.findActivityGroupGoodsByParam(map);
		return list;
	}

	@Override
	public PageUtils<ActivityGroupGoodsVo> findSpuBySkuId(List<String> ids, String storeId, int pageNumber,
			int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<ActivityGroupGoodsVo> list = activityGroupGoodsMapper.findSpuBySkuId(ids, storeId);
		for (ActivityGroupGoodsVo good : list) {
			good.setUrl(storeImagePrefix + good.getUrl());
		}
		return new PageUtils<ActivityGroupGoodsVo>(list);
	}

	@Override
	public List<ActivityGroupGoodsVo> findSpuBySkuIds(List<String> ids, String storeId) {
		List<ActivityGroupGoodsVo> list = activityGroupGoodsMapper.findSpuBySkuId(ids, storeId);
		for (ActivityGroupGoodsVo good : list) {
			good.setUrl(storeImagePrefix + good.getUrl());
		}
		return list;
	}

	@Override
	public PageUtils<ActivityGroupGoodsVo> getActivityGroupGoodsByParam(Map<String, Object> map, int pageNumber,
			int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<ActivityGroupGoodsVo> list = activityGroupGoodsMapper.getActivityGroupGoodsByParam(map);
		for (ActivityGroupGoodsVo good : list) {
			good.setUrl(storeImagePrefix + good.getUrl());
		}
		return new PageUtils<ActivityGroupGoodsVo>(list);
	}

	@Override
	public void deleteByGroupId(String groupId) throws ServiceException {
		activityGroupGoodsMapper.deleteByGroupId(groupId);
	}

	@Override
	public List<ActivityGroupGoodsVo> findSpuByGoodsStoreId(List<String> ids, String storeId, String online) {
		List<ActivityGroupGoodsVo> list = activityGroupGoodsMapper.findSpuByGoodsStoreId(ids, storeId, online);
		for (ActivityGroupGoodsVo good : list) {
			good.setUrl(storeImagePrefix + good.getUrl());
		}
		return list;
	}

	@Override
	public PageUtils<ActivityGroupGoodsVo> findSpuByGoodsStoreId(List<String> ids, String groupId, String storeId,
			String online, int pageNumber, int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<ActivityGroupGoodsVo> list = activityGroupGoodsMapper.findSpuByGoodsStoreId(ids, storeId, online);
		for (ActivityGroupGoodsVo good : list) {

			if (StringUtils.isNotBlank(groupId)) {
				ActivityGroupGoods vo = new ActivityGroupGoods();
				vo.setGroupId(groupId);
				vo.setStoreSkuId(good.getStoreSkuId());
				List<ActivityGroupGoods> goodsList = activityGroupGoodsMapper.getActivityGroupGoodsGByParams(vo);
				if (goodsList != null && goodsList.size() > 0) {
					good.setLimitNum(goodsList.get(0).getLimitNum());
					good.setGourpPrice(goodsList.get(0).getGourpPrice());
					good.setGroupInventory(goodsList.get(0).getGroupInventory());
				}
				String strGoodsStock = good.getGoodsStock();
				if (StringUtils.isBlank(strGoodsStock)) {
					strGoodsStock = "0";
				}

				Integer goodsStock = Integer.parseInt(strGoodsStock)
						+ (good.getGroupInventory() != null ? good.getGroupInventory() : 0);
				good.setGoodsStock(String.valueOf(goodsStock));

			}

			if (StringUtils.isEmpty(good.getSort())) {
				good.setSort("0");
			}

			good.setUrl(storeImagePrefix + good.getUrl());
		}
		return new PageUtils<ActivityGroupGoodsVo>(list);
	}

	@Override
	public List<ActivityGroupGoods> getActivityGroupGoodsGByParams(ActivityGroupGoods activityGroupGoods) {
		return activityGroupGoodsMapper.getActivityGroupGoodsGByParams(activityGroupGoods);
	}

	@Override
	public void updateActivityGroupGoods(ActivityGroupGoods activityGroupGoods) {
		activityGroupGoodsMapper.updateActivityGroupGoods(activityGroupGoods);
	}

	@Override
	public void syncGoodsStock(ActivityGroupGoods activityGroupGoods, String userId,
			StockOperateEnum stockOperateEnum) {
		try {

			GoodsStoreSku goodsStoreSku = goodsStoreSkuServiceApi.getById(activityGroupGoods.getStoreSkuId());

			StockAdjustVo stockAdjustVo = new StockAdjustVo();
			stockAdjustVo.setUserId(userId);
			stockAdjustVo.setStoreId(activityGroupGoods.getStoreId());
			List<AdjustDetailVo> adjustDetailList = new ArrayList<AdjustDetailVo>();
			AdjustDetailVo adjustDetailVo = new AdjustDetailVo();
			adjustDetailVo.setStoreSkuId(activityGroupGoods.getStoreSkuId());
			adjustDetailVo.setNum(activityGroupGoods.getGroupInventory());
			adjustDetailVo.setGoodsName(goodsStoreSku.getName());
			adjustDetailList.add(adjustDetailVo);
			stockAdjustVo.setAdjustDetailList(adjustDetailList);
			stockAdjustVo.setStockOperateEnum(stockOperateEnum);
			logger.info("商家中心修改团购活动商品库存参数:" + stockAdjustVo.toString());
			stockManagerServiceApi.updateStock(stockAdjustVo);
			logger.info("商家中心修改团购活动商品完成:");
		} catch (Exception e) {
			logger.info("商家中心修改团购活动商品发生异常:", e);
		}
	}

	@Override
	public ActivityGroupGoods selectActivityGroupLimitNum(Map<String, Object> map) throws Exception {
		return activityGroupGoodsMapper.selectActivityGroupLimitNum(map);
	}

	@Override
	public void removeByGroupId(String groupId) throws ServiceException {
		activityGroupGoodsMapper.removeByGroupId(groupId);
	}

	@Override
	public ActivityGroupGoods selectByObject(ActivityGroupGoods activityGroupGoods) throws ServiceException {
		return activityGroupGoodsMapper.selectByObject(activityGroupGoods);
	}

}
