
package com.okdeer.mall.activity.coupons.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.archive.goods.spu.enums.SpuTypeEnum;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.enums.BSSC;
import com.okdeer.archive.goods.store.enums.IsActivity;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.archive.stock.service.StockManagerJxcServiceApi;
import com.okdeer.archive.stock.service.StockManagerServiceApi;
import com.okdeer.archive.stock.vo.AdjustDetailVo;
import com.okdeer.archive.stock.vo.StockAdjustVo;
import com.okdeer.archive.store.enums.StoreActivityTypeEnum;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;
import com.okdeer.mall.activity.coupons.enums.ActivitySaleStatus;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleGoodsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleMapper;
import com.okdeer.mall.activity.coupons.service.ActivitySaleService;
import com.okdeer.mall.activity.coupons.service.ActivitySaleServiceApi;
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
	 private StockManagerServiceApi stockManagerServiceApi;

	// Begin 1.0.Z add by zengj
	/**
	 * 库存管理Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StockManagerJxcServiceApi stockManagerJxcServiceApi;
	// End 1.0.Z add by zengj

	/**
	 * 回滚MQ
	 */
	@Autowired
	RollbackMQProducer rollbackMQProducer;

	@Transactional(rollbackFor = Exception.class)
	public void save(ActivitySale activitySale, List<ActivitySaleGoods> asgList) throws Exception {
		List<String> rpcIdByStockList = new ArrayList<String>();
		List<String> rpcIdBySkuList = new ArrayList<String>();
		try {
			// 先保存特惠主对象
			activitySaleMapper.save(activitySale);
			// 库存同步--库存出错的几率更大。先处理库存
			this.syncGoodsStockBatch(asgList, activitySale.getCreateUserId(), activitySale.getStoreId(),
					StockOperateEnum.ACTIVITY_STOCK, rpcIdByStockList);
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
			activitySaleGoodsMapper.saveBatch(asgList);

		} catch (Exception e) {
			// 现在库存放入商业管理系统管理。那边没提供补偿机制，先不发消息
			// rollbackMQProducer.sendStockRollbackMsg(rpcIdByStockList);
			rollbackMQProducer.sendSkuRollbackMsg(rpcIdBySkuList);
			throw e;
		}

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
	private void syncGoodsStockBatch(List<ActivitySaleGoods> goodsList, String userId, String storeId,
			StockOperateEnum soe, List<String> rpcIdByStockList) throws Exception {

		String rpcId = UuidUtils.getUuid();
		rpcIdByStockList.add(rpcId);

		StockAdjustVo stockAdjustVo = new StockAdjustVo();
		stockAdjustVo.setUserId(userId);
		stockAdjustVo.setStoreId(storeId);
		stockAdjustVo.setRpcId(rpcId);
		stockAdjustVo.setMethodName(this.getClass().getName() + ".syncGoodsStock");
		/***********************/
		/***********************/
		//非组合商品
		List<AdjustDetailVo> adjustDetailList = new ArrayList<AdjustDetailVo>();
		//组合商品
		List<AdjustDetailVo> assDtailList = new ArrayList<AdjustDetailVo>();
		for (ActivitySaleGoods goods : goodsList) {
			GoodsStoreSku entity = goodsStoreSkuServiceApi.getById(goods.getStoreSkuId());
			if(goods.getSaleStock() > 0){
				AdjustDetailVo adjustDetailVo = new AdjustDetailVo();
				adjustDetailVo.setStoreSkuId(goods.getStoreSkuId());
				adjustDetailVo.setNum(goods.getSaleStock());
				if(entity.getSpuTypeEnum() == SpuTypeEnum.assembleSpu){
					//如果是组合商品,不需要同步进销存的库存
					adjustDetailVo.setGoodsName(entity.getName());
					adjustDetailVo.setBarCode(entity.getBarCode());
					assDtailList.add(adjustDetailVo);
				}else{
					/*************新增字段 start **************/
					adjustDetailVo.setGoodsName(entity.getName());
					adjustDetailVo.setBarCode(entity.getBarCode());
					adjustDetailVo.setStyleCode(entity.getStyleCode());
					adjustDetailVo.setPrice(goods.getSalePrice());
					/*************新增字段 end  **************/
					adjustDetailList.add(adjustDetailVo);
				}
			}
		}
		stockAdjustVo.setStockOperateEnum(soe);
		//更新商城活动库存
		if(assDtailList.size() > 0){
			stockAdjustVo.setAdjustDetailList(assDtailList);
			stockManagerServiceApi.updateStock(stockAdjustVo);
		}
		log.info("特惠活动时同步erp库存开始:");
		if(adjustDetailList.size() > 0){
			stockAdjustVo.setAdjustDetailList(adjustDetailList);
			stockManagerJxcServiceApi.updateStock(stockAdjustVo);
		}
		log.info("特惠活动时同步erp完成:");
	}

	/**
	 * @desc 同步erp库存
	 * @param goods
	 * @param userId
	 * @param storeId
	 * @throws Exception 
	 */
	private void syncGoodsStock(ActivitySaleGoods goods, String userId, String storeId, StockOperateEnum soe,
			List<String> rpcIdByStockList) throws Exception {

		String rpcId = UuidUtils.getUuid();
		rpcIdByStockList.add(rpcId);

		StockAdjustVo stockAdjustVo = new StockAdjustVo();
		stockAdjustVo.setUserId(userId);
		stockAdjustVo.setStoreId(storeId);
		stockAdjustVo.setRpcId(rpcId);
		stockAdjustVo.setMethodName(this.getClass().getName() + ".syncGoodsStock");
		/***********************/
		GoodsStoreSku entity = goodsStoreSkuServiceApi.getById(goods.getStoreSkuId());
		/***********************/
		List<AdjustDetailVo> adjustDetailList = new ArrayList<AdjustDetailVo>();
		AdjustDetailVo adjustDetailVo = new AdjustDetailVo();
		adjustDetailVo.setStoreSkuId(goods.getStoreSkuId());
		// adjustDetailVo.setNum(goods.getSaleStock());
		// begin zhangkn 和曾俊和刘玄确认过,这个值,erp那边没用,传0过去,减少对erp的干扰
		adjustDetailVo.setNum(0);
		// end zhangkn
		/*************新增字段 start **************/
		adjustDetailVo.setGoodsName(entity.getName());
		adjustDetailVo.setBarCode(entity.getBarCode());
		adjustDetailVo.setStyleCode(entity.getStyleCode());
		adjustDetailVo.setPrice(goods.getSalePrice());
		/*************新增字段 end  **************/
		adjustDetailList.add(adjustDetailVo);
		stockAdjustVo.setAdjustDetailList(adjustDetailList);
		stockAdjustVo.setStockOperateEnum(soe);

		log.info("特惠活动时同步erp库存开始:");
		stockManagerJxcServiceApi.updateStock(stockAdjustVo);
		log.info("特惠活动时同步erp完成:");
	}

	@Transactional(rollbackFor = Exception.class)
	public void update(ActivitySale activitySale, List<ActivitySaleGoods> asgList) throws Exception {
		List<String> rpcIdByStockList = new ArrayList<String>();
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
			activitySaleGoodsMapper.saveBatch(asgList);
			// 同步差集部分商品，也就是原来是特惠商品，然后本次编辑没勾选，所以这批商品需要释放库存。
			if (CollectionUtils.isNotEmpty(saleGoodsList)) {
				// 库存同步
				this.syncGoodsStockBatch(saleGoodsList, activitySale.getCreateUserId(), activitySale.getStoreId(),
						StockOperateEnum.ACTIVITY_END, rpcIdByStockList);
			}
			// 新加商品的库存同步，需要增加锁定库存
			// 库存同步
			this.syncGoodsStockBatch(asgList, activitySale.getCreateUserId(), activitySale.getStoreId(),
					StockOperateEnum.ACTIVITY_STOCK, rpcIdByStockList);
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
		return activitySaleMapper.get(id);
	}

	@Override
	public PageUtils<ActivitySale> pageList(Map<String, Object> map, Integer pageNumber, Integer pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<ActivitySale> list = activitySaleMapper.list(map);
		return new PageUtils<ActivitySale>(list);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<String> updateBatchStatus(List<String> ids, int status, String storeId, String createUserId,Integer activityType) throws Exception {
		List<String> rpcIdByStockList = new ArrayList<String>();
		List<String> rpcIdByBathSkuList = new ArrayList<String>();
		List<String> storeSkuIdList = Lists.newArrayList();
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ids", ids);
			params.put("status", status);
			//modify by mengsj 如果是低价抢购活动，关闭时设置活动结束时间为当前日期
			if(activityType == ActivityTypeEnum.LOW_PRICE.ordinal() && status == ActivitySaleStatus.closed.ordinal()){
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
							storeSkuIdList.add(a.getStoreSkuId());
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

						for (ActivitySaleGoods asg : asgList) {
							goodsStoreSkuIds.add(asg.getStoreSkuId());

							// // 手动关闭或者定时器结束都要把未卖完的数量释放库存
							// // 和erp同步库存
							// this.syncGoodsStock(asg, "", storeId,
							// StockOperateEnum.ACTIVITY_END, rpcIdByStockList);
						}

						// 把所有店铺商品online改成下架
						if (goodsStoreSkuIds.size() > 0) {
							Date date = new Date();
							goodsStoreSkuServiceApi.updateBatchOnline(goodsStoreSkuIds, BSSC.UNSHELVE.ordinal(), date);
						}

					}
				}
				// 手动关闭或者定时器结束都要把未卖完的数量释放库存
				// 和erp同步库存
				if(CollectionUtils.isNotEmpty(saleGoodsList)){
					this.syncGoodsStockBatch(saleGoodsList, "", storeId, StockOperateEnum.ACTIVITY_END, rpcIdByStockList);
				}
			}
		} catch (Exception e) {
			// 现在库存放入商业管理系统管理。那边没提供补偿机制，先不发消息
			//rollbackMQProducer.sendStockRollbackMsg(rpcIdByStockList);
			rollbackMQProducer.sendSkuBatchRollbackMsg(rpcIdByBathSkuList);
			throw e;
		}
		return storeSkuIdList;
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
		List<String> rpcIdByStockList = new ArrayList<String>();
		List<String> rpcIdBySkuList = new ArrayList<String>();
		try {
			// 在删除前,先获取对象
			ActivitySaleGoods asg = activitySaleGoodsMapper.get(activitySaleGoodsId);
			// 和erp同步库存
			this.syncGoodsStock(asg, createUserId, storeId, StockOperateEnum.ACTIVITY_END, rpcIdByStockList);

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
}