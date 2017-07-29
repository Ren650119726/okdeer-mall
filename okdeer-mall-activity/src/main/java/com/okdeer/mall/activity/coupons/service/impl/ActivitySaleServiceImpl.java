
package com.okdeer.mall.activity.coupons.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.okdeer.archive.goods.spu.enums.SpuTypeEnum;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.enums.BSSC;
import com.okdeer.archive.goods.store.enums.IsActivity;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.stock.dto.StockUpdateDetailDto;
import com.okdeer.archive.stock.dto.StockUpdateDto;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.archive.stock.service.GoodsStoreSkuStockApi;
import com.okdeer.archive.store.enums.StoreActivityTypeEnum;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.coupons.bo.ActivitySaleRemindBo;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleRemind;
import com.okdeer.mall.activity.coupons.enums.ActivitySaleStatus;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleGoodsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleMapper;
import com.okdeer.mall.activity.coupons.service.ActivitySaleRemindService;
import com.okdeer.mall.activity.coupons.service.ActivitySaleService;
import com.okdeer.mall.activity.coupons.service.ActivitySaleServiceApi;
import com.okdeer.mall.activity.coupons.vo.ActivitySaleRemindVo;
import com.okdeer.mall.system.mq.RollbackMQProducer;

/**
 * ClassName: ActivitySaleServiceImpl
 * @Description: 特惠活动服务
 * @author zengj 
 * @date 2016年9月7日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     1.0.Z	          2016年9月07日                 zengj              库存管理修改，采用商业管理系统校验
 *     V2.1.0             2017年02月20日               tangy              添加活动安全库存及预警联系人
 *     V2.1.0             2017年02月21日               tangy              同步erp添加字段
 *     V2.5.0             2017年06月19日        wangf01     V2.5.0新增，如果是特惠活动结束，将所有商品变成上架状态，在普通分类下面可以看见
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivitySaleServiceApi")
public class ActivitySaleServiceImpl implements ActivitySaleServiceApi, ActivitySaleService {

	private static final Logger log = Logger.getLogger(ActivitySaleServiceImpl.class);

	@Autowired
	private ActivitySaleMapper activitySaleMapper;

	@Autowired
	private ActivitySaleGoodsMapper activitySaleGoodsMapper;

	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;

	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuStockApi goodsStoreSkuStockApi;

	//Begin V2.1.0 added by tangy  2017-02-20
	/**
	 * 活动商品安全库类型人
	 */
	@Autowired
	private ActivitySaleRemindService activitySaleRemindService;
	//End added by tangy
	
	/**
	 * 回滚MQ
	 */
	@Autowired
	RollbackMQProducer rollbackMQProducer;

	@Transactional(rollbackFor = Exception.class)
	public void save(ActivitySale activitySale, List<ActivitySaleGoods> asgList) throws Exception {
		// List<String> rpcIdByStockList = new ArrayList<String>();
		List<String> rpcIdBySkuList = new ArrayList<String>();
		try {
			// 先保存特惠主对象
			activitySaleMapper.save(activitySale);
			// 库存同步--库存出错的几率更大。先处理库存
			this.updateLockedStock(asgList, activitySale,StockOperateEnum.ACTIVITY_STOCK);
			// 再保存特惠商品列表
			for (ActivitySaleGoods a : asgList) {
				a.setDisabled(Disabled.valid);
				a.setSaleId(activitySale.getId());
				a.setId(UuidUtils.getUuid());

				// goodsStoreSku表的is_activity,activity_id,activity_name要修改
				GoodsStoreSku goodsStoreSku = new GoodsStoreSku();
				// 商品主键id
				goodsStoreSku.setId(a.getStoreSkuId());
				// 活动名字
				goodsStoreSku.setActivityName(activitySale.getName());
				// 活动id
				goodsStoreSku.setActivityId(activitySale.getId());
				// 已经参加活动
				goodsStoreSku.setIsActivity(IsActivity.ATTEND);
				// 活动类型
				//modify by mengsj begin  增加活动类型判断
				if(activitySale.getType() == ActivityTypeEnum.LOW_PRICE){
					//如果是低价活动，需要把商品改为上架状态，因为上个低价活动关闭时，已把该商品下架了
					goodsStoreSku.setOnline(BSSC.PUTAWAY);
					goodsStoreSku.setActivityType(StoreActivityTypeEnum.LOW_PRICE);
				}else{
					goodsStoreSku.setActivityType(StoreActivityTypeEnum.PRIVLIEGE);
				}
				//modify by mengsj end
				// 记录rpcId
				String rpcId = UuidUtils.getUuid();
				goodsStoreSku.setRpcId(rpcId);
				goodsStoreSku.setMethodName(this.getClass().getName() + ".save");
				rpcIdBySkuList.add(rpcId);

				goodsStoreSkuServiceApi.updateByPrimaryKeySelective(goodsStoreSku);
				// 和erp同步库存
				// this.syncGoodsStock(a, activitySale.getCreateUserId(),
				// activitySale.getStoreId(),
				// StockOperateEnum.ACTIVITY_STOCK, rpcIdByStockList);
			}
			
			//Begin V2.1.0 added 活动如果设置安全库存提醒联系人则保存  by tangy  2017-02-20
			//删除安全库存联系人关联
			activitySaleRemindService.deleteBySaleId(activitySale.getId());
			if (CollectionUtils.isNotEmpty(activitySale.getActivitySaleRemindVos())) {
				List<ActivitySaleRemind> list = BeanMapper.mapList(activitySale.getActivitySaleRemindVos(), ActivitySaleRemind.class);				
				activitySaleRemindService.insertSelectiveBatch(list);
			}	
			//End added by tangy
			activitySaleGoodsMapper.saveBatch(asgList);

		} catch (Exception e) {
			// 现在库存放入商业管理系统管理。那边没提供补偿机制，先不发消息
			// rollbackMQProducer.sendStockRollbackMsg(rpcIdByStockList);
			rollbackMQProducer.sendSkuRollbackMsg(rpcIdBySkuList);
			throw e;
		}

	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateSaleStock(ActivitySale activitySale, ActivitySaleGoods activitySaleGoods) throws Exception {
		// List<String> rpcIdByStockList = new ArrayList<String>();
		List<String> rpcIdBySkuList = new ArrayList<String>();
		ActivitySaleGoods saleGoods = activitySaleGoodsMapper.get(activitySaleGoods.getId());
		//活动商品存在并活动库存小于修改库存
//		if (saleGoods == null || (saleGoods.getSaleStock() != null 
//				&& saleGoods.getSaleStock().intValue() >= activitySaleGoods.getSaleStock().intValue() )) {
//			return;
//		}
		//zhangkn v2.2.0 冒烟用例注释掉部分代码
		if (saleGoods == null) {
			return;
		}
		//如果是组合商品,不需要同步进销存的库存
		saleGoods.setSaleStock(activitySaleGoods.getSaleStock() - saleGoods.getSaleStock());
		
		List<ActivitySaleGoods> list = new ArrayList<ActivitySaleGoods>();
		list.add(saleGoods);
		// 库存同步--库存出错的几率更大。先处理库存
		try {
			this.updateLockedStock(list, activitySale,StockOperateEnum.ACTIVITY_STOCK_INCREMENT);
			activitySaleGoodsMapper.updateById(activitySaleGoods);
		} catch (Exception e) {
			// 现在库存放入商业管理系统管理。那边没提供补偿机制，先不发消息
			// rollbackMQProducer.sendStockRollbackMsg(rpcIdByStockList);
			rollbackMQProducer.sendSkuRollbackMsg(rpcIdBySkuList);
			throw e;
		}
	}
	
	private void updateLockedStock(ActivitySaleGoods goods, String userId, String storeId, StockOperateEnum stockOptType) throws Exception {
		List<ActivitySaleGoods> goodsList = Arrays.asList(new ActivitySaleGoods[]{goods});
		updateLockedStock(goodsList,userId,storeId,null,stockOptType);
	}
	
	
	private void updateLockedStock(List<ActivitySaleGoods> goodsList,ActivitySale actSale,StockOperateEnum stockOptType) throws Exception {
		updateLockedStock(goodsList,actSale.getUpdateUserId(),actSale.getStoreId(),actSale.getType(),stockOptType);
	}

	/**
	 * 
	 * @Description: 同步erp库存-批量
	 * @param goodsList 特惠商品集合
	 * @param userId 用户ID
	 * @param storeId 店铺ID
	 * @param soe 操作
	 * @param rpcIdByStockList rpcId
	 * @throws Exception    一次信息
	 * @author zengj
	 * @date 2016年9月12日
	 */
	private void updateLockedStock(List<ActivitySaleGoods> goodsList,String userId,String storeId,ActivityTypeEnum actType ,StockOperateEnum stockOptType) throws Exception {

		StockUpdateDto stockUpdateDto = new StockUpdateDto();

		stockUpdateDto.setRpcId(UuidUtils.getUuid());
		stockUpdateDto.setMethodName("");
		stockUpdateDto.setStoreId(storeId);
		stockUpdateDto.setUserId(userId);
		stockUpdateDto.setStockOperateEnum(stockOptType);

		List<StockUpdateDetailDto> updateDetailList = new ArrayList<StockUpdateDetailDto>();
		StockUpdateDetailDto updateDetail = null;
		for(ActivitySaleGoods actSaleGoods : goodsList){
			updateDetail = new StockUpdateDetailDto();
			updateDetail.setStoreSkuId(actSaleGoods.getStoreSkuId());
			updateDetail.setSpuType(SpuTypeEnum.physicalSpu);
			updateDetail.setActType(actType);
			if(stockOptType == StockOperateEnum.ACTIVITY_END){
				// 如果活动结束，将活动数量归0
				updateDetail.setUpdateLockedNum(0);
			}else{
				updateDetail.setUpdateLockedNum(actSaleGoods.getSaleStock());
			}
			
			updateDetailList.add(updateDetail);
		}
		stockUpdateDto.setUpdateDetailList(updateDetailList);
		
		goodsStoreSkuStockApi.updateStock(stockUpdateDto);
	}

	@Transactional(rollbackFor = Exception.class)
	public void update(ActivitySale activitySale, List<ActivitySaleGoods> asgList) throws Exception {
		// List<String> rpcIdByStockList = new ArrayList<String>();
		List<String> rpcIdBySkuList = new ArrayList<String>();
		List<String> rpcIdByBathSkuList = new ArrayList<String>();
		try {
			// 先保存特惠主对象
			activitySaleMapper.update(activitySale);

			// 店铺商品表也要先把关联活动清空
			String rcpId = UuidUtils.getUuid();
			rpcIdByBathSkuList.add(rcpId);
			goodsStoreSkuServiceApi.updateActivityByActivityIds(new String[] { activitySale.getId() }, rcpId);

			// 获取原来选中的ActivitySaleGoodsList
			List<ActivitySaleGoods> sourceAsgList = activitySaleGoodsMapper.listBySaleId(activitySale.getId());
			// 求出原来ActivitySaleGoodsList和传入的asgList的差集,这部分商品要返还库存
			List<String> sourceIdList = new ArrayList<String>();
			if (sourceAsgList != null && sourceAsgList.size() > 0) {
				for (ActivitySaleGoods asg : sourceAsgList) {
					sourceIdList.add(asg.getId());
				}
			}
			List<String> newIdList = new ArrayList<String>();
			if (asgList != null && asgList.size() > 0) {
				for (ActivitySaleGoods asg : asgList) {
					newIdList.add(asg.getId());
				}
			}
			// 差集 (比如添加活动 选的是1,2,3商品,修改活动选的是3,4,5商品,差集就是1,2)
			sourceIdList.removeAll(newIdList);
			List<ActivitySaleGoods> saleGoodsList = null;
			if (sourceIdList.size() > 0) {
				saleGoodsList = new ArrayList<ActivitySaleGoods>();
				// 同步库存
				for (String activitySaleGoodsId : sourceIdList) {
					// 和erp同步库存
					ActivitySaleGoods saleGoods = activitySaleGoodsMapper.get(activitySaleGoodsId);
					saleGoodsList.add(saleGoods);
					// if (a != null) {
					// this.syncGoodsStock(a, activitySale.getCreateUserId(),
					// activitySale.getStoreId(),
					// StockOperateEnum.ACTIVITY_END, rpcIdByStockList);
					// }
				}
			}

			// 先清除原记录
			activitySaleGoodsMapper.deleteBySaleId(activitySale.getId());

			// 再保存特惠商品列表
			for (ActivitySaleGoods a : asgList) {
				a.setDisabled(Disabled.valid);
				a.setSaleId(activitySale.getId());
				a.setId(UuidUtils.getUuid());
				
				a.setCreateTime(activitySale.getUpdateTime());
				a.setUpdateTime(activitySale.getUpdateTime());
				a.setCreateUserId(activitySale.getUpdateUserId());
				a.setUpdateUserId(activitySale.getUpdateUserId());

				// 记录rpcId
				String rpcId = UuidUtils.getUuid();
				rpcIdBySkuList.add(rpcId);

				// goodsStoreSku表的is_activity,activity_id,activity_name要修改
				GoodsStoreSku goodsStoreSku = new GoodsStoreSku();
				// 商品主键id
				goodsStoreSku.setId(a.getStoreSkuId());
				// 活动名字
				goodsStoreSku.setActivityName(activitySale.getName());
				// 活动id
				goodsStoreSku.setActivityId(activitySale.getId());
				// 已经参加活动
				goodsStoreSku.setIsActivity(IsActivity.ATTEND);
				// 活动类型
				//modify by mengsj begin  增加活动类型判断
				if(activitySale.getType() == ActivityTypeEnum.LOW_PRICE){
					goodsStoreSku.setActivityType(StoreActivityTypeEnum.LOW_PRICE);
				}else{
					goodsStoreSku.setActivityType(StoreActivityTypeEnum.PRIVLIEGE);
				}
				//modify by mengsj end

				goodsStoreSku.setRpcId(rpcId);
				goodsStoreSku.setMethodName(this.getClass().getName() + ".update");
				goodsStoreSkuServiceApi.updateByPrimaryKeySelective(goodsStoreSku);

				// 和erp同步库存
				// this.syncGoodsStock(a, activitySale.getCreateUserId(),
				// activitySale.getStoreId(),
				// StockOperateEnum.ACTIVITY_STOCK, rpcIdByStockList);
			}
			
			//Begin V2.1.0 added 活动如果设置安全库存提醒联系人则保存  by tangy  2017-02-20
			//删除安全库存联系人关联
			activitySaleRemindService.deleteBySaleId(activitySale.getId());
			if (CollectionUtils.isNotEmpty(activitySale.getActivitySaleRemindVos())) {
				List<ActivitySaleRemind> list = BeanMapper.mapList(activitySale.getActivitySaleRemindVos(), ActivitySaleRemind.class);				
				activitySaleRemindService.insertSelectiveBatch(list);
			}	
			//End added by tangy
			
			activitySaleGoodsMapper.saveBatch(asgList);
			// 同步差集部分商品，也就是原来是特惠商品，然后本次编辑没勾选，所以这批商品需要释放库存。
			if (CollectionUtils.isNotEmpty(saleGoodsList)) {
				StockOperateEnum operateEnum = StockOperateEnum.ACTIVITY_END;
				if (activitySale != null && activitySale.getType() == ActivityTypeEnum.SALE_ACTIVITIES) {
					operateEnum = StockOperateEnum.OVER_SALE_ORDER_INTEGRAL;
				} else if (activitySale != null && activitySale.getType() == ActivityTypeEnum.LOW_PRICE) {
					operateEnum = StockOperateEnum.OVER_SALE_ORDER_INTEGRAL;
				}
				// 库存同步
				this.updateLockedStock(saleGoodsList, activitySale,operateEnum);
			}
			// 新加商品的库存同步，需要增加锁定库存
			// 库存同步
			this.updateLockedStock(asgList, activitySale,	StockOperateEnum.ACTIVITY_STOCK);
		} catch (Exception e) {
			// 现在实物订单库存放入商业管理系统管理。那边没提供补偿机制，先不发消息
			// rollbackMQProducer.sendStockRollbackMsg(rpcIdByStockList);
			rollbackMQProducer.sendSkuRollbackMsg(rpcIdBySkuList);
			rollbackMQProducer.sendSkuBatchRollbackMsg(rpcIdByBathSkuList);
			throw e;
		}

	}
	
	@Override
	public ActivitySale get(String id) {
		ActivitySale activitySale = activitySaleMapper.get(id);
		//Begin V2.1.0 added 活动安全库存关联人  by tangy  2017-02-20
		if (activitySale != null) {
			List<ActivitySaleRemindBo> activitySaleRemindBos = activitySaleRemindService.findActivitySaleRemindBySaleId(activitySale.getId());
			if (CollectionUtils.isNotEmpty(activitySaleRemindBos)) {
				activitySale.setActivitySaleRemindVos(BeanMapper.mapList(activitySaleRemindBos, ActivitySaleRemindVo.class));
			}
		}
		//End added by tangy
		return activitySale;
	}

	@Override
	public PageUtils<ActivitySale> pageList(Map<String, Object> map, Integer pageNumber, Integer pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<ActivitySale> list = activitySaleMapper.list(map);
		return new PageUtils<ActivitySale>(list);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateBatchStatus(List<String> ids, int status, String storeId, String createUserId,Integer activityType) throws Exception {
		// List<String> rpcIdByStockList = new ArrayList<String>();
		List<String> rpcIdByBathSkuList = new ArrayList<String>();
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ids", ids);
			params.put("status", status);
			//modify by mengsj 如果是低价抢购活动，关闭时设置活动结束时间为当前日期
			if(activityType != null && activityType == ActivityTypeEnum.LOW_PRICE.ordinal() && status == ActivitySaleStatus.closed.ordinal()){
				params.put("endTime", new Date());
			}
			activitySaleMapper.updateBatchStatus(params);
			
			// 如果状态时进行中,要把活动关联的所有商品状态改为上架
			if (status == ActivitySaleStatus.ing.getValue()) {
				List<String> goodsStoreSkuIds = new ArrayList<String>();
				for (String id : ids) {
					List<ActivitySaleGoods> asgList = listActivitySaleGoods(id);
					if (asgList != null && asgList.size() > 0) {
						for (ActivitySaleGoods a : asgList) {
							goodsStoreSkuIds.add(a.getStoreSkuId());
						}
					}
				}

				// 把所有店铺商品online改成上架
				if (goodsStoreSkuIds.size() > 0) {
					Date date = new Date();
					goodsStoreSkuServiceApi.updateBatchOnline(goodsStoreSkuIds, BSSC.PUTAWAY.ordinal(), date);
				}
			}
			// 状态如果是
			// 已经结束,或者关闭,goodsStoreSku表的is_activity,activity_id,activity_name要修改,解绑活动和商品的关系
			else if (status == ActivitySaleStatus.end.getValue() || status == ActivitySaleStatus.closed.getValue()) {
				String rcpId = UuidUtils.getUuid();
				rpcIdByBathSkuList.add(rcpId);
				goodsStoreSkuServiceApi.updateActivityByActivityIds(ids.toArray(new String[ids.size()]), rcpId);
				// 需要库存同步的商品集合
				List<ActivitySaleGoods> saleGoodsList = new ArrayList<ActivitySaleGoods>();
				// 已经结束或者已关闭才同步,数字改为0
				for (String id : ids) {
					List<ActivitySaleGoods> asgList = listActivitySaleGoods(id);

					if (asgList != null && asgList.size() > 0) {
						saleGoodsList.addAll(asgList);
						// 所有店铺商品id
						List<String> goodsStoreSkuIds = new ArrayList<String>();
						
						//组合商品skuid列表
						List<String> goodsSkuIds = new ArrayList<String>();

						for (ActivitySaleGoods asg : asgList) {
							GoodsStoreSku sku = goodsStoreSkuServiceApi.getById(asg.getStoreSkuId());
							if(sku != null && sku.getSpuTypeEnum() == SpuTypeEnum.assembleSpu){
								goodsSkuIds.add(asg.getStoreSkuId());
							}
							goodsStoreSkuIds.add(asg.getStoreSkuId());

							// // 手动关闭或者定时器结束都要把未卖完的数量释放库存
							// // 和erp同步库存
							// this.syncGoodsStock(asg, "", storeId,
							// StockOperateEnum.ACTIVITY_END, rpcIdByStockList);
						}

						// 如果是特惠活动，把所有店铺商品online改成下架
						if (goodsStoreSkuIds.size() > 0 && activityType != null && activityType == ActivityTypeEnum.SALE_ACTIVITIES.ordinal()) {
							Date date = new Date();
							// begin add by wangf01 20170619
							// @TODO V2.5.0新增，如果是特惠活动结束，将所有商品变成上架状态，在普通分类下面可以看见
							//goodsStoreSkuServiceApi.updateBatchOnline(goodsStoreSkuIds, BSSC.UNSHELVE.ordinal(), date);
							goodsStoreSkuServiceApi.updateBatchOnline(goodsStoreSkuIds, BSSC.PUTAWAY.ordinal(), date);
							// begin add by wangf01 20170619
						}
						if(goodsSkuIds.size() > 0 && activityType != null && activityType == ActivityTypeEnum.LOW_PRICE.ordinal()){
							//如果是低价抢购活动并且商品是组合商品，则把关联的组合商品下架
							Date date = new Date();
							goodsStoreSkuServiceApi.updateBatchOnline(goodsSkuIds, BSSC.UNSHELVE.ordinal(), date);
						}

					}
				}
				// 手动关闭或者定时器结束都要把未卖完的数量释放库存
				// 和erp同步库存
				if(CollectionUtils.isNotEmpty(saleGoodsList)){
					StockOperateEnum stockOperateEnum = StockOperateEnum.ACTIVITY_END;
					ActivitySale activitySale = new ActivitySale();
					activitySale.setType(ActivityTypeEnum.enumValueOf(activityType));
					this.updateLockedStock(saleGoodsList, activitySale, stockOperateEnum);
				}
			}
		} catch (Exception e) {
			// 现在库存放入商业管理系统管理。那边没提供补偿机制，先不发消息
			//rollbackMQProducer.sendStockRollbackMsg(rpcIdByStockList);
			rollbackMQProducer.sendSkuBatchRollbackMsg(rpcIdByBathSkuList);
			throw e;
		}
	}

	@Override
	public List<Map<String, Object>> listGoodsStoreSku(Map<String, Object> map) {
		return activitySaleMapper.listGoodsStoreSku(map);
	}

	@Override
	public PageUtils<Map<String, Object>> pageListGoodsStoreSku(Map<String, Object> map, Integer pageNumber,
			Integer pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<Map<String, Object>> list = activitySaleMapper.listGoodsStoreSku(map);
		return new PageUtils<Map<String, Object>>(list);
	}

	@Override
	public List<ActivitySaleGoods> listActivitySaleGoods(String activitySaleId) {
		return activitySaleGoodsMapper.listBySaleId(activitySaleId);
	}

	@Override
	public int validateExist(Map<String, Object> map) {
		return activitySaleMapper.validateExist(map);
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteActivitySaleGoods(String storeId, String createUserId, String activitySaleGoodsId,
			String goodsStoreSkuId) throws Exception {
		// List<String> rpcIdByStockList = new ArrayList<String>();
		List<String> rpcIdBySkuList = new ArrayList<String>();
		try {
			// 在删除前,先获取对象
			ActivitySaleGoods asg = activitySaleGoodsMapper.get(activitySaleGoodsId);
			// 和erp同步库存
			this.updateLockedStock(asg, createUserId, storeId, StockOperateEnum.ACTIVITY_END);

			// activitySaleGoods表根据activitySaleGoodsId删除
			activitySaleGoodsMapper.deleteById(activitySaleGoodsId);

			// goodsStoreSku表解绑商品绑定活动
			// goodsStoreSku商品online改成下架
			GoodsStoreSku goodsStoreSku = new GoodsStoreSku();
			// 商品主键id
			goodsStoreSku.setId(goodsStoreSkuId);
			// 活动名字
			goodsStoreSku.setActivityName("");
			// 活动id
			//added by liyb01 2016-12-14 fix bug 16075 
			goodsStoreSku.setActivityId("0");
			//ended by liyb01 2016-12-14
			// 已经参加活动
			goodsStoreSku.setIsActivity(IsActivity.ABSTENTION);
			// 活动类型
			goodsStoreSku.setActivityType(StoreActivityTypeEnum.NONE);
			goodsStoreSku.setUpdateTime(new Date());
			goodsStoreSku.setOnline(BSSC.UNSHELVE);

			// 记录rpcId
			String rpcId = UuidUtils.getUuid();
			goodsStoreSku.setRpcId(rpcId);
			goodsStoreSku.setMethodName(this.getClass().getName() + ".deleteActivitySaleGoods");
			rpcIdBySkuList.add(rpcId);
			goodsStoreSkuServiceApi.updateByPrimaryKeySelective(goodsStoreSku);
		} catch (Exception e) {
			// 现在库存放入商业管理系统管理。那边没提供补偿机制，不发送库存回滚的消息
			//rollbackMQProducer.sendStockRollbackMsg(rpcIdByStockList);
			rollbackMQProducer.sendSkuRollbackMsg(rpcIdBySkuList);
			throw e;
		}
	}

	@Override
	public ActivitySale findByPrimaryKey(String id) {
		return activitySaleMapper.selectById(id);
	}

	@Override
	public ActivitySale getAcSaleStatus(String activityId) {
		return activitySaleMapper.getAcSaleStatus(activityId);
	}

	@Override
	public int selectActivitySale(String activityId) {
		return activitySaleMapper.selectActivitySale(activityId);
	}

	@Override
	public List<ActivitySale> listByTask() {
		return activitySaleMapper.listByTask();
	}
	
	@Override
	public List<ActivitySale> listByStoreId(Map<String,Object> map) {
		return activitySaleMapper.listByStoreId(map);
	}

	@Override
	public ActivitySale findActivitySaleByStoreId(String storeId,
			Integer activiType,Integer status) {
		if(StringUtils.isNotBlank(storeId) && activiType != null){
			return activitySaleMapper.findByActivitySaleByStoreId(storeId, activiType, status);
		}
		return new ActivitySale();
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.coupons.service.ActivitySaleServiceApi#findLowPriceActivitySaleByStoreId(java.lang.String)
	 */
	@Override
	public ActivitySale findLowPriceActivitySaleByStoreId(String storeId) throws Exception {
		if (StringUtils.isBlank(storeId)) {
			return null;
		}
		return activitySaleMapper.findByActivitySaleByStoreId(storeId, ActivityTypeEnum.LOW_PRICE.ordinal(),
				ActivitySaleStatus.ing.getValue());
	}
	
	@Override
	public PageUtils<Map<String, Object>> pageListGoodsStoreSkuV220(Map<String, Object> map, Integer pageNumber,
			Integer pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<Map<String, Object>> list = activitySaleMapper.listGoodsStoreSkuV220(map);
		return new PageUtils<Map<String, Object>>(list);
	}

	@Override
	public List<ActivitySale> findByIds(List<String> idList) {
		if(CollectionUtils.isEmpty(idList)){
			return Lists.newArrayList();
		}
		Set<String> idSet = new HashSet<>();         
		idSet.addAll(idList);     
		return activitySaleMapper.findBySaleIds(idSet);
	}

}