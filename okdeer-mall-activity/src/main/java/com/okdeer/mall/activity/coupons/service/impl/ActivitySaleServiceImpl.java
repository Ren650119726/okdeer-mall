
package com.okdeer.mall.activity.coupons.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.enums.BSSC;
import com.okdeer.archive.goods.store.enums.IsActivity;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.archive.stock.service.StockManagerJxcServiceApi;
import com.okdeer.archive.stock.vo.AdjustDetailVo;
import com.okdeer.archive.stock.vo.StockAdjustVo;
import com.okdeer.archive.store.enums.StoreActivityTypeEnum;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;
import com.okdeer.mall.activity.coupons.enums.ActivitySaleStatus;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleGoodsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleMapper;
import com.okdeer.mall.activity.coupons.service.ActivitySaleService;
import com.okdeer.mall.activity.coupons.service.ActivitySaleServiceApi;
import com.okdeer.mall.system.mq.RollbackMQProducer;
import com.yschome.api.ims.service.StockOperaterService;
import com.yschome.base.common.enums.Disabled;
import com.yschome.base.common.utils.PageUtils;
import com.yschome.base.common.utils.UuidUtils;

/**
 * 
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

	@Reference
	private StockOperaterService stockOperaterService;

	// @Reference(version = "1.0.0", check = false)
	// private StockManagerServiceApi stockManagerServiceApi;

	// Begin 1.0.Z add by zengj
	/**
	 * 库存管理Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StockManagerJxcServiceApi stockManagerServiceApi;
	// End 1.0.Z add by zengj

	/**
	 * 回滚MQ
	 */
	@Autowired
	RollbackMQProducer rollbackMQProducer;

	public StockOperaterService getStockOperaterService() {
		return stockOperaterService;
	}

	public void setStockOperaterService(StockOperaterService stockOperaterService) {
		this.stockOperaterService = stockOperaterService;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(ActivitySale activitySale, List<ActivitySaleGoods> asgList) throws Exception {
		List<String> rpcIdByStockList = new ArrayList<String>();
		List<String> rpcIdBySkuList = new ArrayList<String>();
		try {
			// 先保存特惠主对象
			activitySaleMapper.save(activitySale);
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
				goodsStoreSku.setActivityType(StoreActivityTypeEnum.PRIVLIEGE);

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
			// 库存同步
			this.syncGoodsStockBatch(asgList, activitySale.getCreateUserId(), activitySale.getStoreId(),
					StockOperateEnum.ACTIVITY_STOCK, rpcIdByStockList);
		} catch (Exception e) {
			rollbackMQProducer.sendStockRollbackMsg(rpcIdByStockList);
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
		List<AdjustDetailVo> adjustDetailList = new ArrayList<AdjustDetailVo>();
		for (ActivitySaleGoods goods : goodsList) {
			GoodsStoreSku entity = goodsStoreSkuServiceApi.getById(goods.getStoreSkuId());

			AdjustDetailVo adjustDetailVo = new AdjustDetailVo();
			adjustDetailVo.setStoreSkuId(goods.getStoreSkuId());
			adjustDetailVo.setNum(goods.getSaleStock());
			/*************新增字段 start **************/
			adjustDetailVo.setGoodsName(entity.getName());
			adjustDetailVo.setBarCode(entity.getBarCode());
			adjustDetailVo.setStyleCode(entity.getStyleCode());
			adjustDetailVo.setPrice(goods.getSalePrice());
			/*************新增字段 end  **************/
			adjustDetailList.add(adjustDetailVo);
		}
		stockAdjustVo.setAdjustDetailList(adjustDetailList);
		stockAdjustVo.setStockOperateEnum(soe);

		log.info("特惠活动时同步erp库存开始:");
		stockManagerServiceApi.updateStock(stockAdjustVo);
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
		adjustDetailVo.setNum(goods.getSaleStock());
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
		stockManagerServiceApi.updateStock(stockAdjustVo);
		log.info("特惠活动时同步erp完成:");
	}

	@Override
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
				goodsStoreSku.setActivityType(StoreActivityTypeEnum.PRIVLIEGE);

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
			rollbackMQProducer.sendStockRollbackMsg(rpcIdByStockList);
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
	public void updateBatchStatus(List<String> ids, int status, String storeId, String createUserId,
			List<ActivitySale> asList) throws Exception {
		List<String> rpcIdByStockList = new ArrayList<String>();
		List<String> rpcIdByBathSkuList = new ArrayList<String>();
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ids", ids);
			params.put("status", status);
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
				this.syncGoodsStockBatch(saleGoodsList, "", storeId, StockOperateEnum.ACTIVITY_END, rpcIdByStockList);
			}
		} catch (Exception e) {
			rollbackMQProducer.sendStockRollbackMsg(rpcIdByStockList);
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

	@Override
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
			goodsStoreSku.setActivityId("");
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
			rollbackMQProducer.sendStockRollbackMsg(rpcIdByStockList);
			rollbackMQProducer.sendSkuRollbackMsg(rpcIdBySkuList);
			throw e;
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateByTask() throws Exception {
		log.info("特惠活动定时器开始");
		// 未开始(改为进行中)的 和 进行中的(改为已过期)list
		List<ActivitySale> list = activitySaleMapper.listByTask();

		List<String> listIdNoStart = new ArrayList<String>();
		List<String> listIdIng = new ArrayList<String>();

		List<ActivitySale> listNoStart = new ArrayList<ActivitySale>();
		List<ActivitySale> listIng = new ArrayList<ActivitySale>();

		if (list != null && list.size() > 0) {
			for (ActivitySale a : list) {
				// 所有进行中的数据
				if (a.getStatus() == ActivitySaleStatus.ing.getValue()) {
					listIdIng.add(a.getId());
					listIng.add(a);
				}
				// 所有未开始的数据
				else if (a.getStatus() == ActivitySaleStatus.noStart.getValue()) {
					listIdNoStart.add(a.getId());
					listNoStart.add(a);
				}
			}
			// 所有未开始的改为进行中
			if (listIdNoStart != null && listIdNoStart.size() > 0) {
				updateBatchStatus(listIdNoStart, ActivitySaleStatus.ing.getValue(), "job", "job", listNoStart);
			}
			// 所有进行中的的改为已经结束
			if (listIdIng != null && listIdIng.size() > 0) {
				updateBatchStatus(listIdIng, ActivitySaleStatus.end.getValue(), "job", "job", listIng);
			}
		}
		log.info("特惠活动定时器结束");
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

	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("保护环境"); // 向列表中添加数据
		list.add("爱护地球"); // 向列表中添加数据
		list.add("从我做起"); // 向列表中添加数据
		List<String> list1 = new ArrayList<String>();
		list1.add("保护环境"); // 向列表中添加数据
		list1.add("爱护地球"); // 向列表中添加数据
		list1.add("爱护地球ddd"); // 向列表中添加数据
		boolean ret = list.removeAll(list1); // 从list中移除与list1相同的元素
		Iterator<String> it = list.iterator(); // 创建迭代器
		while (it.hasNext()) { // 循环遍历迭代器
			System.out.println(it.next()); // 输出集合中元素
		}
	}
}