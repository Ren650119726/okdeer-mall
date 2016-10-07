/** 
 *@Project: yschome-mall-activity 
 *@Author: lijun
 *@Date: 2016年7月12日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */

package com.okdeer.mall.activity.seckill.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.enums.IsActivity;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.archive.stock.service.StockManagerServiceApi;
import com.okdeer.archive.stock.vo.AdjustDetailVo;
import com.okdeer.archive.stock.vo.StockAdjustVo;
import com.okdeer.archive.store.enums.StoreActivityTypeEnum;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckill;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckillRange;
import com.okdeer.mall.activity.seckill.enums.SeckillStatusEnum;
import com.okdeer.mall.activity.seckill.mapper.ActivitySeckillMapper;
import com.okdeer.mall.activity.seckill.mapper.ActivitySeckillRangeMapper;
import com.okdeer.mall.activity.seckill.mapper.SeckillRemindeMapper;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillService;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillServiceApi;
import com.okdeer.mall.activity.seckill.vo.ActivitySeckillFormVo;
import com.okdeer.mall.activity.seckill.vo.ActivitySeckillItemVo;
import com.okdeer.mall.activity.seckill.vo.ActivitySeckillListPageVo;
import com.okdeer.mall.activity.seckill.vo.ActivitySeckillQueryFilterVo;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.common.enums.RangeTypeEnum;
import com.okdeer.mall.common.utils.DateUtils;
import com.okdeer.mall.system.mq.RollbackMQProducer;
import com.okdeer.mcm.service.IAppMsgApi;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.common.consts.RedisKeyConstants;

/**
 * ClassName: ActivitySeckillServiceImpl 
 * @Description: 秒杀活动service实现类
 * @author lijun
 * @date 2016年7月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *      重构 4.1         2016年7月13日                                 lijun               新增
 *      重构 4.1         2016年7月14日                                 zengj               根据主键获取秒杀活动信息
 *      重构 4.1         2016年7月16日                                 zengj               查询定位地址是否在秒杀活动范围
 *		重构 4.1         2016年7月20日                                 luosm               新增方法
 *		重构 4.1         2016年7月22日                                 luosm               优化方法
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.seckill.service.ActivitySeckillServiceApi",timeout=60000)
public class ActivitySeckillServiceImpl implements ActivitySeckillService, ActivitySeckillServiceApi {

	/**
	 * 日志输出
	 */
	private static final Logger logger = LoggerFactory.getLogger(ActivitySeckillServiceImpl.class);

	/**
	 * 注入秒杀活动Mapper接口
	 */
	@Autowired
	ActivitySeckillMapper activitySeckillMapper;

	/**
	 * 秒杀活动区域信息Mapper接口
	 */
	@Autowired
	ActivitySeckillRangeMapper activitySeckillRangeMapper;

	/**
	 * 注入店铺商品API接口
	 */
	@Reference(version = "1.0.0", check = false)
	GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;

	/**
	* 注入库存API接口
	*/
	@Reference(version = "1.0.0", check = false)
	StockManagerServiceApi stockManagerServiceApi;

	/**
	 * 回滚MQ
	 */
	@Autowired
	RollbackMQProducer rollbackMQProducer;
	
	/**
	 * 秒杀提醒设置Mapper
	 */
	@Autowired
	SeckillRemindeMapper seckillRemindeMapper;
	
	/**
	 * Redis模板注入
	 */
	@Autowired
	//private IRedisTemplateWrapper<String,Integer> redisTemplateWrapper;
	private StringRedisTemplate stringRedisTemplate;
	
	
	/**
	 * iAppMsgApi 消息中心的接口
	 */
	@Reference(version="1.0.0", check = false)
	private IAppMsgApi appMsgApi;

	@Override
	public PageUtils<ActivitySeckillListPageVo> findSeckillListPageByFilter(ActivitySeckillQueryFilterVo queryFilterVo,
			Integer pageNumber, Integer pageSize) throws Exception {
		// 分页组件
		PageHelper.startPage(pageNumber, pageSize, true);
		// 秒杀活动列表页查询
		List<ActivitySeckillListPageVo> seckillList = activitySeckillMapper.findListPageByFilter(queryFilterVo);
		// 分页
		return new PageUtils<ActivitySeckillListPageVo>(seckillList);
	}

	@Override
	public ActivitySeckill findSeckillById(String id) throws Exception {
		return activitySeckillMapper.findByPrimaryKey(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveByCloseSeckill(ActivitySeckill activitySeckill) throws Exception {
		List<String> rpcIdByStockList = new ArrayList<String>();
		List<String> rpcIdBySkuList = new ArrayList<String>();
		try {
			// 修改活动状态为已关闭
			activitySeckillMapper.updateSeckillStatus(activitySeckill.getId(), SeckillStatusEnum.closed);
			// 解除商品关联关系
			this.removeSkuRelation(activitySeckill.getStoreSkuId(), rpcIdBySkuList);
			// 释放库存数
			this.releaseActivitySkuStock(activitySeckill.getStoreSkuId(), rpcIdByStockList);
			// 修改秒杀提醒设置状态
			seckillRemindeMapper.updateRemindeStatus(activitySeckill.getId(), Constant.ZERO);
			// 取消消息中心秒杀提醒
			appMsgApi.deleteMsgByInfoId(activitySeckill.getId());
		} catch (Exception e) {
			rollbackMQProducer.sendStockRollbackMsg(rpcIdByStockList);
			rollbackMQProducer.sendSkuRollbackMsg(rpcIdBySkuList);
			throw e;
		}
	}

	@Override
	public ActivitySeckillListPageVo findSeckillDetailById(String id) throws Exception {
		return activitySeckillMapper.findDetailByPrimaryKey(id);
	}

	@Override
	public List<ActivitySeckill> findActivitySeckillByStatus(Map<String, Object> param) throws Exception {
		return activitySeckillMapper.findActivitySeckillByStatus(param);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveActivitySeckill(ActivitySeckillFormVo activitySeckillFormVo) throws Exception {
		List<String> rpcIdByStockList = new ArrayList<String>();
		List<String> rpcIdBySkuList = new ArrayList<String>();
		try {
			// 操作时间
			Date date = new Date();
			String activityId = UuidUtils.getUuid();

			// 保存秒杀活动表
			this.addActivitySeckill(activitySeckillFormVo, activityId, date);
			// 保存秒杀活动区域信息
			this.addActivitySeckillRange(activitySeckillFormVo, activityId);
			// 保存秒杀活动商品关系信息
			this.addSkuRelation(activitySeckillFormVo.getStoreSkuId(), activityId, rpcIdBySkuList);
			// 锁定活动库存信息
			this.lockActivitySkuStock(activitySeckillFormVo, rpcIdByStockList);
			
			// 设置redis缓存信息
			Long millisecond = DateUtils.getMilliseconds(activitySeckillFormVo.getStartTime(), activitySeckillFormVo.getEndTime());
			stringRedisTemplate.boundValueOps(RedisKeyConstants.SECKILL_STOCK + activitySeckillFormVo.getStoreSkuId())
					.set(String.valueOf(activitySeckillFormVo.getSeckillNum()), millisecond, TimeUnit.MILLISECONDS);	
		} catch (Exception e) {
			rollbackMQProducer.sendStockRollbackMsg(rpcIdByStockList);
			rollbackMQProducer.sendSkuRollbackMsg(rpcIdBySkuList);
			throw e;
		}
	}
	

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateActivitySeckill(ActivitySeckillFormVo activitySeckillFormVo) throws Exception {
		List<String> rpcIdByStockList = new ArrayList<String>();
		List<String> rpcIdBySkuList = new ArrayList<String>();
		try {
			// 操作时间
			Date date = new Date();

			String redisKeyByDel = null;
			ActivitySeckill activitySeckill = activitySeckillMapper.findByPrimaryKey(activitySeckillFormVo.getId());
			if (activitySeckill.getStoreSkuId().equals(activitySeckillFormVo.getStoreSkuId())) {
				// 修改秒杀活动库存
				this.lockActivitySkuStock(activitySeckillFormVo, rpcIdByStockList);
				// 设置需要删除的redisKey
				redisKeyByDel = RedisKeyConstants.SECKILL_STOCK + activitySeckillFormVo.getStoreSkuId();
			} else {
				// 释放原来商品活动库存
				this.releaseActivitySkuStock(activitySeckill.getStoreSkuId(), rpcIdByStockList);
				// 解除原商品关联关系
				this.removeSkuRelation(activitySeckill.getStoreSkuId(), rpcIdBySkuList);
				// 锁定新商品活动库存信息
				this.lockActivitySkuStock(activitySeckillFormVo, rpcIdByStockList);
				// 保存秒杀活动商品关系信息
				this.addSkuRelation(activitySeckillFormVo.getStoreSkuId(), activitySeckill.getId(), rpcIdBySkuList);
				// 设置需要删除的redisKey
				redisKeyByDel = RedisKeyConstants.SECKILL_STOCK + activitySeckill.getStoreSkuId();
			}

			// 更新秒杀活动表
			this.updateActivitySeckillByForm(activitySeckillFormVo, date);
			// 更新秒杀活动区域信息
			this.updateActivitySeckillRange(activitySeckillFormVo);
			
			// 设置Redis缓存
			stringRedisTemplate.delete(redisKeyByDel);
			// 设置redis缓存信息
			Long millisecond = DateUtils.getMilliseconds(activitySeckillFormVo.getStartTime(), activitySeckillFormVo.getEndTime());
			stringRedisTemplate.boundValueOps(RedisKeyConstants.SECKILL_STOCK + activitySeckillFormVo.getStoreSkuId())
					.set(String.valueOf(activitySeckillFormVo.getSeckillNum()), millisecond, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			rollbackMQProducer.sendStockRollbackMsg(rpcIdByStockList);
			rollbackMQProducer.sendSkuRollbackMsg(rpcIdBySkuList);
			throw e;
		}
	}

	@Override
	public Boolean checkActivitySeckill(ActivitySeckillFormVo activitySeckillFormVo) throws Exception {
		// 新增id为null 为了修改的时候排除自己
		String id = activitySeckillFormVo.getId();
		Date startTime = activitySeckillFormVo.getStartTime();
		Date endTime = activitySeckillFormVo.getEndTime();

		// 获取所有的区域判断是否有重复
		List<ActivitySeckillRange> areaIdList = activitySeckillFormVo.getActivitySeckillRangeList();
		List<String> areaIds = new ArrayList<String>();
		if (areaIdList != null) {
			for (ActivitySeckillRange activitySeckillRange : areaIdList) {
				areaIds.add(activitySeckillRange.getCityId());
			}
		}
		RangeTypeEnum rangeType = activitySeckillFormVo.getSeckillRangeType();

		// map参数
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("startTime", startTime);
		params.put("endTime", endTime);
		params.put("areaIds", areaIds);
		params.put("rangeType", rangeType);

		Boolean flag = true;
		if (RangeTypeEnum.area == rangeType) {
			// 当前区域是否存在有秒杀活动
			Integer countOne = activitySeckillMapper.findSeckillCountByRange(params);
			// 同一个时间是否有秒杀活动存在
			params.put("areaIds", null);
			params.put("rangeType", RangeTypeEnum.national);
			// 排除选择全国的秒杀活动
			params.put("isFlag", "Y");
			Integer countTwo = activitySeckillMapper.findSeckillCountByRange(params);
			flag = countOne > 0 || countTwo > 0 ? false : true;
		} else {
			// 同一个时间是否有秒杀活动存在（全国）
			Integer count = activitySeckillMapper.findSeckillCountByRange(params);
			flag = count > 0 ? false : true;
		}
		return flag;
	}

	@Override
	public Integer updateSeckillStatus(String id, SeckillStatusEnum status) throws Exception {
		return activitySeckillMapper.updateSeckillStatus(id, status);
	}

	@Override
	public void updateSeckillByEnd(ActivitySeckill activitySeckill) throws Exception {
		List<String> rpcIdByStockList = new ArrayList<String>();
		List<String> rpcIdBySkuList = new ArrayList<String>();
		try {
			// 更新活动状态
			activitySeckillMapper.updateSeckillStatus(activitySeckill.getId(), SeckillStatusEnum.end);
			// 解除商品关联关系
			this.removeSkuRelation(activitySeckill.getStoreSkuId(), rpcIdBySkuList);
			// 释放库存数
			this.releaseActivitySkuStock(activitySeckill.getStoreSkuId(), rpcIdByStockList);
		} catch (Exception e) {
			rollbackMQProducer.sendStockRollbackMsg(rpcIdByStockList);
			rollbackMQProducer.sendSkuRollbackMsg(rpcIdBySkuList);
			throw e;
		}
	}

	// Begin 重构4.1 add by zengj
	/**
	 * @Description: 查询定位地址是否在秒杀活动范围
	 * @param params 查询参数
	 * @return ActivitySeckill 秒杀活动实体  
	 * @author zengj
	 * @date 2016年7月16日
	 */
	@Override
	public ActivitySeckill findSecKillByCityId(Map<String, Object> params) {
		return activitySeckillMapper.findSecKillByCityId(params);
	}

	/**
	 * @Description: 查询商品在定位地址是否存在秒杀活动
	 * @param params 查询参数
	 * @return ActivitySeckill 秒杀活动实体  
	 * @author zengj
	 * @date 2016年7月18日
	 */
	@Override
	public ActivitySeckill findSecKillByGoodsId(Map<String, Object> params) {
		return activitySeckillMapper.findSecKillByGoodsId(params);
	}
	// End 重构4.1 add by zengj

	/**
	 * @Description: 解除商品与活动的关联关系，更新店铺SKU表
	 * @param skuId 店铺skuId
	 * @throws Exception 抛出异常
	 * @author lijun
	 * @date 2016年7月15日
	 */
	private void removeSkuRelation(String skuId, List<String> rpcIdBySkuList) throws Exception {
		GoodsStoreSku goodsStoreSku = new GoodsStoreSku();
		goodsStoreSku.setId(skuId);
		goodsStoreSku.setActivityId("0");
		goodsStoreSku.setActivityName("");
		goodsStoreSku.setIsActivity(IsActivity.ABSTENTION);
		goodsStoreSku.setActivityType(StoreActivityTypeEnum.NONE);
		goodsStoreSku.setUpdateTime(new Date());

		String rpcId = UuidUtils.getUuid();
		rpcIdBySkuList.add(rpcId);
		goodsStoreSku.setRpcId(rpcId);
		goodsStoreSku.setMethodName(this.getClass().getName() + ".removeSkuRelation");
		goodsStoreSkuServiceApi.updateActivityStatus(goodsStoreSku);
	}

	/**
	 * @Description: 添加商品与活动的关联关系，更新店铺SKU表
	 * @param skuId 店铺skuId
	 * @param acvitityId 活动id
	 * @param rpcIdBySkuList RPCID
	 * @throws Exception 抛出异常
	 * @author lijun
	 * @date 2016年7月15日
	 */
	private void addSkuRelation(String skuId, String acvitityId, List<String> rpcIdBySkuList) throws Exception {
		GoodsStoreSku storeSku = goodsStoreSkuServiceApi.getById(skuId);
		storeSku.setActivityId(acvitityId);
		storeSku.setActivityName("秒杀活动");
		storeSku.setIsActivity(IsActivity.ATTEND);
		storeSku.setActivityType(StoreActivityTypeEnum.SECKILL);
		storeSku.setUpdateTime(new Date());

		// RPCID
		String rpcId = UuidUtils.getUuid();
		rpcIdBySkuList.add(rpcId);
		storeSku.setRpcId(rpcId);
		storeSku.setMethodName(this.getClass().getName() + ".addSkuRelation");
		goodsStoreSkuServiceApi.updateActivityStatus(storeSku);
	}

	/**
	 * @Description: 新增活动库存
	 * @param activitySeckillFormVo 页面Form对象
	 * @param rpcIdByStockList RPCID集合
	 * @throws Exception 抛出异常
	 * @author lijun
	 * @date 2016年7月26日
	 */
	private void lockActivitySkuStock(ActivitySeckillFormVo activitySeckillFormVo, List<String> rpcIdByStockList)
			throws Exception {
		// 获取店铺sku信息
		GoodsStoreSku storeSku = goodsStoreSkuServiceApi.getById(activitySeckillFormVo.getStoreSkuId());

		List<AdjustDetailVo> adjustDetailList = new ArrayList<AdjustDetailVo>();
		AdjustDetailVo detailVo = new AdjustDetailVo();
		detailVo.setStoreSkuId(activitySeckillFormVo.getStoreSkuId());
		detailVo.setGoodsName(storeSku.getName());
		detailVo.setNum(activitySeckillFormVo.getSeckillNum());
		adjustDetailList.add(detailVo);

		StockAdjustVo stockAdjustVo = new StockAdjustVo();
		stockAdjustVo.setAdjustDetailList(adjustDetailList);
		stockAdjustVo.setStockOperateEnum(StockOperateEnum.ACTIVITY_STOCK);
		stockAdjustVo.setStoreId(storeSku.getStoreId());

		// 保存RPCID
		String rpcId = UuidUtils.getUuid();
		rpcIdByStockList.add(rpcId);
		stockAdjustVo.setRpcId(rpcId);
		stockAdjustVo.setMethodName(this.getClass().getName() + ".addActivitySkuStock");
		stockManagerServiceApi.updateStock(stockAdjustVo);
	}

	/**
	 * @Description: 释放活动库存
	 * @param storeSkuId 店铺skuId
	 * @param rpcIdByStockList rpcid集合
	 * @throws Exception 抛出异常
	 * @author lijun
	 * @date 2016年7月15日
	 */
	private void releaseActivitySkuStock(String storeSkuId, List<String> rpcIdByStockList) throws Exception {

		// 获取店铺sku信息
		GoodsStoreSku storeSku = goodsStoreSkuServiceApi.getById(storeSkuId);

		List<AdjustDetailVo> adjustDetailList = new ArrayList<AdjustDetailVo>();
		AdjustDetailVo detailVo = new AdjustDetailVo();
		detailVo.setStoreSkuId(storeSkuId);
		detailVo.setGoodsName(storeSku.getName());
		detailVo.setNum(0);
		adjustDetailList.add(detailVo);

		StockAdjustVo stockAdjustVo = new StockAdjustVo();
		stockAdjustVo.setAdjustDetailList(adjustDetailList);
		stockAdjustVo.setStockOperateEnum(StockOperateEnum.ACTIVITY_END);
		stockAdjustVo.setStoreId(storeSku.getStoreId());

		// 保存RPCID
		String rpcId = UuidUtils.getUuid();
		rpcIdByStockList.add(rpcId);
		stockAdjustVo.setRpcId(rpcId);
		stockAdjustVo.setMethodName(this.getClass().getName() + ".releaseActivitySkuStock");
		stockManagerServiceApi.updateStock(stockAdjustVo);
	}

	/**
	 * @Description: 添加秒杀活动区域信息
	 * @param activitySeckillFormVo 页面请求formVo类
	 * @param activityId 活动id
	 * @throws Exception 抛出异常
	 * @author lijun
	 * @date 2016年7月17日
	 */
	private void addActivitySeckillRange(ActivitySeckillFormVo activitySeckillFormVo, String activityId)
			throws Exception {
		if (RangeTypeEnum.area == activitySeckillFormVo.getSeckillRangeType()) {
			// 保存区域信息表
			List<ActivitySeckillRange> seckillRangeList = activitySeckillFormVo.getActivitySeckillRangeList();

			// 设置主键id和活动关联id
			for (ActivitySeckillRange range : seckillRangeList) {
				range.setId(UuidUtils.getUuid());
				range.setActivitySeckillId(activityId);
			}

			// 批量保存区域信息
			if (CollectionUtils.isNotEmpty(seckillRangeList)) {
				activitySeckillRangeMapper.addByBatch(seckillRangeList);
			}
		}
	}

	/**
	 * @Description: 添加秒杀活动信息
	 * @param activitySeckillFormVo 页面请求formVo类
	 * @param activityId 活动id
	 * @param date 操作时间
	 * @throws Exception 抛出异常
	 * @author lijun
	 * @date 2016年7月17日
	 */
	private void addActivitySeckill(ActivitySeckillFormVo activitySeckillFormVo, String activityId, Date date)
			throws Exception {
		// 保存秒杀活动表
		ActivitySeckill activitySeckill = new ActivitySeckill();
		activitySeckill.setId(activityId);
		activitySeckill.setCreateTime(date);
		activitySeckill.setCreateUserId(activitySeckillFormVo.getCreateUserId());
		activitySeckill.setDisabled(Disabled.valid);
		activitySeckill.setEndTime(activitySeckillFormVo.getEndTime());
		activitySeckill.setPicUrl(activitySeckillFormVo.getPicUrl());
		activitySeckill.setSeckillAlias(activitySeckillFormVo.getSeckillAlias());
		activitySeckill.setSeckillName(activitySeckillFormVo.getSeckillName());
		activitySeckill.setSeckillNum(activitySeckillFormVo.getSeckillNum());
		activitySeckill.setSeckillPrice(activitySeckillFormVo.getSeckillPrice());
		activitySeckill.setSeckillRangeType(activitySeckillFormVo.getSeckillRangeType());
		activitySeckill.setSeckillStatus(SeckillStatusEnum.noStart);
		activitySeckill.setStartNum(activitySeckillFormVo.getStartNum());
		activitySeckill.setStartTime(activitySeckillFormVo.getStartTime());
		activitySeckill.setStoreSkuId(activitySeckillFormVo.getStoreSkuId());
		activitySeckill.setUpdateTime(date);
		activitySeckill.setUpdateUserId(activitySeckillFormVo.getUpdateUserId());
		activitySeckillMapper.add(activitySeckill);
	}

	/**
	 * @Description: 更新秒杀活动信息
	 * @param activitySeckillFormVo 页面表单form类
	 * @param date 操作时间
	 * @throws Exception 抛出异常
	 * @author lijun
	 * @date 2016年7月18日
	 */
	private void updateActivitySeckillByForm(ActivitySeckillFormVo activitySeckillFormVo, Date date) throws Exception {
		// 保存秒杀活动表
		ActivitySeckill activitySeckill = new ActivitySeckill();
		activitySeckill.setId(activitySeckillFormVo.getId());
		activitySeckill.setEndTime(activitySeckillFormVo.getEndTime());
		activitySeckill.setPicUrl(activitySeckillFormVo.getPicUrl());
		activitySeckill.setSeckillAlias(activitySeckillFormVo.getSeckillAlias());
		activitySeckill.setSeckillName(activitySeckillFormVo.getSeckillName());
		activitySeckill.setSeckillNum(activitySeckillFormVo.getSeckillNum());
		activitySeckill.setSeckillPrice(activitySeckillFormVo.getSeckillPrice());
		activitySeckill.setSeckillRangeType(activitySeckillFormVo.getSeckillRangeType());
		activitySeckill.setStartNum(activitySeckillFormVo.getStartNum());
		activitySeckill.setStartTime(activitySeckillFormVo.getStartTime());
		activitySeckill.setStoreSkuId(activitySeckillFormVo.getStoreSkuId());
		activitySeckill.setUpdateTime(date);
		activitySeckillMapper.updateByPrimaryKeySelective(activitySeckill);
	}

	/**
	 * @Description: 更新秒杀活动区域范围信息
	 * @param activitySeckillFormVo 页面表单form对象
	 * @throws Exception 抛出异常
	 * @author lijun
	 * @date 2016年7月18日
	 */
	private void updateActivitySeckillRange(ActivitySeckillFormVo activitySeckillFormVo) throws Exception {
		// 清空所有的区域信息
		activitySeckillRangeMapper.deleteByActivityId(activitySeckillFormVo.getId());

		if (RangeTypeEnum.area == activitySeckillFormVo.getSeckillRangeType()) {
			// 保存区域信息表
			List<ActivitySeckillRange> seckillRangeList = activitySeckillFormVo.getActivitySeckillRangeList();
			// 设置主键id和活动关联id
			for (ActivitySeckillRange range : seckillRangeList) {
				range.setId(UuidUtils.getUuid());
				range.setActivitySeckillId(activitySeckillFormVo.getId());
			}

			// 批量保存区域信息
			if (seckillRangeList.size() > 0) {
				activitySeckillRangeMapper.addByBatch(seckillRangeList);
			}
		}
	}

	// begin update by luosm 2016-07-22
	/***
	 * 
	 * @Description: 根据城市id查询当前区域是否有秒杀活动
	 * @param cityId 城市id
	 * @return List 秒杀信息
	 * @throws Exception 抛出异常
	 * @author luosm
	 * @date 2016年7月20日
	 */
	@Override
	public List<ActivitySeckill> findByUserAppSecKillByCityId(String cityId) throws Exception {
		return activitySeckillMapper.findAppUserSecKillByCityId(cityId);
	}
	// end update by luosm 2016-07-22

	// begin update by luosm 2016-07-26
	/**
	 * 
	 * @Description: 通过秒杀活动id查询，秒杀活动详情
	 * @param id id
	 * @return ActivitySeckillItemVo 秒杀信息
	 * @throws Exception 抛出异常
	 * @author luosm
	 * @date 2016年7月20日
	 */
	@Override
	public ActivitySeckillItemVo findAppUserSecKillBySeckill(String id) throws Exception {
		return activitySeckillMapper.findAppUserSecKillBySeckill(id);
	}
	// end update by luosm 2016-07-26

	@Override
	public List<ActivitySeckill> findByUserAppSecKillListByCityId(String cityId) throws Exception {
	
		return null;
	}
}
