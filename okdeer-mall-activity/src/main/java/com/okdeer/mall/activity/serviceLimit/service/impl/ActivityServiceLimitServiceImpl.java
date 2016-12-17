package com.okdeer.mall.activity.serviceLimit.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageHelper;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuStock;
import com.okdeer.archive.goods.store.enums.BSSC;
import com.okdeer.archive.goods.store.enums.IsActivity;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuStockServiceApi;
import com.okdeer.archive.stock.exception.StockException;
import com.okdeer.archive.store.enums.StoreActivityTypeEnum;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.coupons.enums.ActivitySaleStatus;
import com.okdeer.mall.activity.serviceLimit.entity.ActivityServiceLimit;
import com.okdeer.mall.activity.serviceLimit.entity.ActivityServiceLimitGoods;
import com.okdeer.mall.activity.serviceLimit.mapper.ActivityServiceLimitGoodsMapper;
import com.okdeer.mall.activity.serviceLimit.mapper.ActivityServiceLimitMapper;
import com.okdeer.mall.activity.serviceLimit.service.ActivityServiceLimitService;
import com.okdeer.mall.common.utils.RobotUserUtil;

/**
 * ClassName: ActivityLabelServiceImpl 
 * @Description: 标签活动service
 * @author zhangkn
 * @date 2016年12月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.1			 2016年11月8日 			zhagnkn
 */
@Service
public class ActivityServiceLimitServiceImpl extends BaseServiceImpl
		implements ActivityServiceLimitService{

	private static final Logger log = Logger.getLogger(ActivityServiceLimitServiceImpl.class);

	@Autowired
	private ActivityServiceLimitMapper limitMapper;
	@Autowired
	private ActivityServiceLimitGoodsMapper limitGoodsMapper;
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;
	@Reference(version = "1.0.0", check = false)
	GoodsStoreSkuStockServiceApi stockApi;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return limitMapper;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void add(ActivityServiceLimit activityLimit, List<ActivityServiceLimitGoods> asgList) throws Exception {
		// 先保存活动主对象
		limitMapper.add(activityLimit);
		//保存活动关联的商品列表
		addBatchLimitGoodsList(new Date(),activityLimit,asgList);
	}
	
	private void addBatchLimitGoodsList(Date date,ActivityServiceLimit activityLimit,List<ActivityServiceLimitGoods> asgList) throws Exception{
		//批量保存商品关联信息
		limitGoodsMapper.addBatch(asgList);
		
		// 商品表修改对应活动信息,和活动绑定
		int i = 0;
		for (ActivityServiceLimitGoods a : asgList) {
			
			//处理库存 可销售库存- 活动库存+
			GoodsStoreSkuStock stock = stockApi.getBySkuId(a.getStoreSkuId());
			if(stock == null){
				throw new StockException("第"+(i+1)+"个商品库存不存在");
			}
			stock.setSellable(stock.getSellable() - a.getActivityStock());
			stock.setLocked(stock.getLocked() + a.getActivityStock());
			stock.setUpdateTime(date);
			stockApi.updateByEditPrivate(stock);
			
			// goodsStoreSku表的is_activity,activity_id,activity_name要修改
			GoodsStoreSku goodsStoreSku = new GoodsStoreSku();
			// 商品主键id
			goodsStoreSku.setId(a.getStoreSkuId());
			// 活动名字
			goodsStoreSku.setActivityName(activityLimit.getName());
			// 活动id
			goodsStoreSku.setActivityId(activityLimit.getId());
			// 已经参加活动
			goodsStoreSku.setIsActivity(IsActivity.ATTEND);
			// 活动类型
			goodsStoreSku.setActivityType(StoreActivityTypeEnum.LIMIT);
			goodsStoreSkuServiceApi.updateByPrimaryKeySelective(goodsStoreSku);
			i++;
		}
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(ActivityServiceLimit activityLimit, List<ActivityServiceLimitGoods> asgList) throws Exception {
		// 修改活动主对象
		limitMapper.update(activityLimit);
		
		Date date = new Date();
		// 店铺商品表先把老数据,和活动解绑
		List<ActivityServiceLimitGoods> oldLimitGoodsList = listActivityLimitGoods(activityLimit.getId());
		cancelActivity(date,activityLimit.getId(),oldLimitGoodsList);

		//删除老数据
		limitGoodsMapper.deleteByActivityId(activityLimit.getId());
		//批量保存关联的商品列表
		addBatchLimitGoodsList(date,activityLimit,asgList);
	}
	
	private void cancelActivity(Date date,String activityId,List<ActivityServiceLimitGoods> oldLimitGoodsList) throws Exception{
		//通过活动id得到关联的商品列表,然后把商品和活动解绑,并且释放活动占用库存
		if(CollectionUtils.isNotEmpty(oldLimitGoodsList)){
			int i = 0;
			for (ActivityServiceLimitGoods a : oldLimitGoodsList) {
				
				//返还库存 可销售库存+ 活动库存-
				GoodsStoreSkuStock stock = stockApi.getBySkuId(a.getStoreSkuId());
				if(stock == null){
					throw new StockException("第"+(i+1)+"个商品库存不存在");
				}
				stock.setSellable(stock.getSellable() + a.getActivityStock());
				stock.setLocked(stock.getLocked() - a.getActivityStock());
				stock.setUpdateTime(date);
				stockApi.updateByEditPrivate(stock);
				
				GoodsStoreSku sku = new GoodsStoreSku();
				sku.setId(a.getStoreSkuId());
				sku.setActivityId("0");
				sku.setActivityName("");
				sku.setActivityType(StoreActivityTypeEnum.NONE);
				sku.setIsActivity(IsActivity.ABSTENTION);
				sku.setUpdateTime(date);
				goodsStoreSkuServiceApi.updateByPrimaryKeySelective(sku);
			}
		}
	}
	
	@Override
	public PageUtils<ActivityServiceLimit> pageList(Map<String, Object> map, Integer pageNumber, Integer pageSize){
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<ActivityServiceLimit> list = limitMapper.list(map);
		return new PageUtils<ActivityServiceLimit>(list);
	}
	
	@Override
	public ActivityServiceLimit findById(String id) {
		return limitMapper.findById(id);
	}
	
	@Override
	public List<ActivityServiceLimitGoods> listActivityLimitGoods(String activityId) {
		return limitGoodsMapper.listByActivityId(activityId);
	}
	
	@Override
	public List<Map<String, Object>> listGoodsStoreSku(Map<String, Object> map) {
		return limitMapper.listGoodsStoreSku(map);
	}

	@Override
	public PageUtils<Map<String, Object>> pageListGoodsStoreSku(Map<String, Object> map, Integer pageNumber,
			Integer pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<Map<String, Object>> list = limitMapper.listGoodsStoreSku(map);
		return new PageUtils<Map<String, Object>>(list);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateBatchStatus(List<String> ids, int status,String storeId,String updateUserId) throws Exception {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ids", ids);
			params.put("status", status);
			limitMapper.updateBatchStatus(params);
			
			Date date = new Date();
			
			List<String> goodsStoreSkuIds = new ArrayList<String>();
			
			// 如果状态时进行中,要把活动关联的所有商品状态改为上架
			if (status == ActivitySaleStatus.ing.getValue()) {
				for (String id : ids) {
					//活动关联的商品列表
					List<ActivityServiceLimitGoods> asgList = listActivityLimitGoods(id);
					if (CollectionUtils.isNotEmpty(asgList)) {
						for (ActivityServiceLimitGoods a : asgList) {
							goodsStoreSkuIds.add(a.getStoreSkuId());
						}
					}
				}
				// 把所有店铺商品online改成上架
				if (goodsStoreSkuIds.size() > 0) {
					goodsStoreSkuServiceApi.updateBatchOnline(goodsStoreSkuIds, BSSC.PUTAWAY.ordinal(), date);
				}
			}
			// 状态如果是已经结束,或者关闭,goodsStoreSku表的is_activity,activity_id,activity_name要修改,解绑活动和商品的关系
			else if (status == ActivitySaleStatus.end.getValue() || status == ActivitySaleStatus.closed.getValue()) {
				// 已经结束或者已关闭,解绑商品,释放活动占用库存
				for (String id : ids) {
					//活动关联的商品列表
					List<ActivityServiceLimitGoods> asgList = listActivityLimitGoods(id);
					if (CollectionUtils.isNotEmpty(asgList)) {
						for (ActivityServiceLimitGoods a : asgList) {
							goodsStoreSkuIds.add(a.getStoreSkuId());
						}
						//释放库存,解绑活动
						cancelActivity(date, id,asgList);
					}
				}
				// 把所有店铺商品online改成下架
				if (goodsStoreSkuIds.size() > 0) {
					goodsStoreSkuServiceApi.updateBatchOnline(goodsStoreSkuIds, BSSC.UNSHELVE.ordinal(), date);
				}
			}
		} catch (Exception e) {
			log.error("限购活动批量修改状态错误", e);
			throw e;
		}
	}

	@Override
	public int validateExist(Map<String, Object> map) {
		return limitMapper.validateExist(map);
	}
	
	@Override
	public void processJob(){
		log.info("促销限购活动定时器开始");
		Map<String,Object> map = new HashMap<String,Object>();
		Date nowTime = new Date();
		map.put("nowTime", nowTime);
		//1、查询活动未开始，开始时间小于当前的数据 即为要设置开始，2、活动开始、结束时间小于当前的数据 即为要设置结束
		List<ActivityServiceLimit> list = limitMapper.listByJob(map);
		//获得系统当前系统用户id
//		String updateUserId = RobotUserUtil.getRobotUser().getId();

		if (CollectionUtils.isNotEmpty(list)) {
			for (ActivityServiceLimit a : list) {
				try {
					if (a.getStatus() == ActivitySaleStatus.noStart.getValue()) {
						List<String> idList = new ArrayList<String>();
						idList.add(a.getId());
						updateBatchStatus(idList, ActivitySaleStatus.ing.getValue(),
								a.getStoreId(), "0");
					} else if (a.getStatus() == ActivitySaleStatus.ing.getValue()) {
						List<String> idList = new ArrayList<String>();
						idList.add(a.getId());
						updateBatchStatus(idList, ActivitySaleStatus.end.getValue(),
								a.getStoreId(), "0");
					}
				} catch (Exception e) {
					log.error("促销限购活动定时器异常" + a.getId(), e);
				}

			}
		}
		log.info("促销限购活动定时器结束");
	}
	
}