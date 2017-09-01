
package com.okdeer.mall.activity.coupons.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
import com.okdeer.archive.store.enums.StoreActivityTypeEnum;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.common.utils.ListUtil;
import com.okdeer.jxc.sale.enums.ActivityType;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoodsBo;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleGoodsMapper;
import com.okdeer.mall.activity.coupons.service.ActivitySaleGoodsService;
import com.okdeer.mall.activity.coupons.service.ActivitySaleGoodsServiceApi;
import com.okdeer.mall.activity.dto.ActivitySaleGoodsParamDto;
import com.okdeer.mall.system.mq.RollbackMQProducer;

/**
 * 
 * 特惠活动的商品
 * @project yschome-mall
 * @author zhulq
 * @date 2016年4月27日 上午9:37:58
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivitySaleGoodsServiceApi")
public class ActivitySaleGoodsServiceImp implements ActivitySaleGoodsServiceApi, ActivitySaleGoodsService {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ActivitySaleServiceImpl.class);

	@Autowired
	private ActivitySaleGoodsMapper activitySaleGoodsMapper;

	/**
	 * 回滚MQ
	 */
	@Autowired
	RollbackMQProducer rollbackMQProducer;

	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;

	@Override
	public ActivitySaleGoods findById(String id) {
		return activitySaleGoodsMapper.get(id);
	}
	
	@Override
	public ActivitySaleGoods selectByObject(ActivitySaleGoods activitySaleGoods) {
		return activitySaleGoodsMapper.selectByObject(activitySaleGoods);
	}

	@Override
	public ActivitySaleGoods selectActivitySaleByParams(Map<String, Object> params) {
		return activitySaleGoodsMapper.selectActivitySaleByParams(params);
	}

	@Override
	public List<ActivitySaleGoodsBo> findSaleGoodsByParams(ActivitySaleGoodsParamDto param) {
		return activitySaleGoodsMapper.findSaleGoodsByParams(param);
	}

	@Override
	public PageUtils<ActivitySaleGoodsBo> findSaleGoodsByParams(ActivitySaleGoodsParamDto param, Integer pageSize,
			Integer pageNum) {
		List<ActivitySaleGoodsBo> list = activitySaleGoodsMapper.findSaleGoodsByParams(param);
		PageHelper.startPage(pageNum, pageSize, true, false);
		PageUtils<ActivitySaleGoodsBo> page = new PageUtils<ActivitySaleGoodsBo>(list);
		return page;
	}

	@Override
	public ActivitySaleGoods selectBySkuId(String storeSkuId) {
		ActivitySaleGoods goods = new ActivitySaleGoods();
		goods.setStoreSkuId(storeSkuId);
		return activitySaleGoodsMapper.selectByObject(goods);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateActivitySaleGoods(ActivitySaleGoods activitySaleGoods) throws Exception {
		activitySaleGoodsMapper.updateById(activitySaleGoods);		
	}

	@Override
	public void saveBatch(List<ActivitySaleGoods> list) {
		activitySaleGoodsMapper.saveBatch(list);
		
	}

	@Override
	public void addActivitySaleGoodsList(ActivitySale sale, List<ActivitySaleGoods> activitySaleGoodsList)  throws Exception{
		//批量添加
		saveBatch(activitySaleGoodsList);
		//把商品表的商品修改掉活动信息
		for(ActivitySaleGoods asg : activitySaleGoodsList){
			GoodsStoreSku sku = new GoodsStoreSku();
			sku.setId(asg.getStoreSkuId());
			sku.setActivityId(sale.getId());
			sku.setActivityName(sale.getName());
			sku.setIsActivity(IsActivity.ATTEND);
			//两个活动类型分别对应不同的两个枚举类........., 要转换
			if(ActivityTypeEnum.LOW_PRICE == sale.getType()){
				sku.setActivityType(StoreActivityTypeEnum.LOW_PRICE);
			} else if (ActivityTypeEnum.SALE_ACTIVITIES == sale.getType()){
				sku.setActivityType(StoreActivityTypeEnum.PRIVLIEGE);
			}
			sku.setUpdateTime(new Date());
			goodsStoreSkuServiceApi.updateByPrimaryKeySelective(sku);
		}
	}
}
