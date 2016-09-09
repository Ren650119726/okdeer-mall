
package com.okdeer.mall.order.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuStock;
import com.okdeer.archive.stock.service.StockManagerJxcServiceApi;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleRecord;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleGoodsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleRecordMapper;
import com.okdeer.mall.order.constant.OrderTipMsgConstant;
import com.okdeer.mall.order.enums.OrderOptTypeEnum;
import com.okdeer.mall.order.service.StockCheckService;
import com.okdeer.mall.order.utils.CodeStatistical;
import com.okdeer.mall.order.vo.TradeOrderContext;
import com.okdeer.mall.order.vo.TradeOrderGoodsItem;
import com.okdeer.mall.order.vo.TradeOrderReq;
import com.okdeer.mall.order.vo.TradeOrderReqDto;
import com.okdeer.mall.order.vo.TradeOrderRespDto;
import com.yschome.base.common.exception.ServiceException;

/**
 * @author maojj
 * @date 2016年7月1日 上午9:12:39
 * @version 1.0.0
 * @DESC: 
 * @copyright ©2015-2020 yschome.com Inc. All rights reserved
 */
/**
 * ClassName: StockCheckServiceImpl 
 * @Description: 检查店铺
 * @author maojj
 * @date 2016年7月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构V4.1			2016-07-14			maojj			检查店铺
 *		Bug:12572	    2016-08-10		 	maojj			添加结算校验失败的提示语
 */
@Service
public class StockCheckServiceImpl implements StockCheckService {

	private static final Logger logger = LoggerFactory.getLogger(StockCheckServiceImpl.class);

	/**
	 * 特惠活动Mapper
	 */
	@Resource
	private ActivitySaleMapper activitySaleMapper;

	/**
	 * 特惠活动商品Mapper
	 */
	@Resource
	private ActivitySaleGoodsMapper activitySaleGoodsMapper;

	/**
	 * 特惠活动记录Mapper
	 */
	@Resource
	private ActivitySaleRecordMapper activitySaleRecordMapper;

	/**
	 * 库存管理Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StockManagerJxcServiceApi stockManagerService;

	/**
	 * 校验商品库存
	 */
	@Override
	public void process(TradeOrderReqDto reqDto, TradeOrderRespDto respDto) throws Exception {
		TradeOrderReq req = reqDto.getData();
		TradeOrderContext context = reqDto.getContext();

		if (context.isExistsActivityGoods()) {
			// 构建查询条件
			Map<String, Object> queryCondition = buildQueryCondition(reqDto);
			// 查询用户购买特惠商品记录
			List<ActivitySaleRecord> buyRecordList = activitySaleRecordMapper.countSaleRecord(queryCondition);
			// 检查是否超过限款
			if (isOutOfLimitKind(context, buyRecordList)) {
				respDto.setFlag(false);
				respDto.setMessage(OrderTipMsgConstant.SIZE_IS_OVER);
				respDto.getResp().setLimit(0);
				respDto.getResp().setCurrentGoodsAndStock(context);
				return;
			}
			if (isOutOfLimitBuy(reqDto, buyRecordList)) {
				respDto.setFlag(false);
				respDto.setMessage(OrderTipMsgConstant.BUY_IS_OVER);
				respDto.getResp().setIsBuy(0);
				respDto.getResp().setCurrentGoodsAndStock(context);
				return;
			}
		}

		// Begin 1.0.Z add by zengj
		// 店铺商品ID 集合
		List<String> storeSkuIdList = new ArrayList<String>();
		// 循环加入
		for (TradeOrderGoodsItem goodsItem : req.getList()) {
			storeSkuIdList.add(goodsItem.getSkuId());
		}
		// 查询库存集合
		List<GoodsStoreSkuStock> stockList = stockManagerService.findGoodsStockInfoList(storeSkuIdList);
		// End 1.0.Z add by zengj

		for (TradeOrderGoodsItem goodsItem : req.getList()) {
			int currentStock = findStock(context, goodsItem, stockList);
			// 如果库存不足，直接返回
			if (currentStock < goodsItem.getSkuNum()) {
				respDto.setFlag(false);
				respDto.setMessage(OrderTipMsgConstant.STOCK_NOT_ENOUGH);
				// Begin modified by maojj 2016-08-10 Bug:12572
				if (reqDto.getOrderOptType() == OrderOptTypeEnum.ORDER_SETTLEMENT) {
					respDto.setMessage(OrderTipMsgConstant.STOCK_NOT_ENOUGH_SETTLEMENT);
				} else {
					respDto.setMessage(OrderTipMsgConstant.STOCK_NOT_ENOUGH);
				}
				// End modified by maojj 2016-08-10
				respDto.getResp().setIsStock(0);
				respDto.getResp().setCurrentGoodsAndStock(context);
				return;
			}
		}
	}

	/**
	 * @Description: 构建请求查询条件
	 * @param reqDto 订单请求对象
	 * @return Map 查询条件
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private Map<String, Object> buildQueryCondition(TradeOrderReqDto reqDto) {
		// 订单请求的店铺ID
		String storeId = reqDto.getData().getStoreId();
		// 订单请求的用户ID
		String userId = reqDto.getData().getUserId();
		String activityId = reqDto.getContext().getActivityId();
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("storeId", storeId);
		condition.put("userId", userId);
		condition.put("activityId", activityId);
		return condition;
	}

	/**
	 * @Description: 检查是否超出限款
	 * @param context 订单请求上下文对象
	 * @param buyRecordList 用户已购买特惠商品记录
	 * @return boolean  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private boolean isOutOfLimitKind(TradeOrderContext context, List<ActivitySaleRecord> buyRecordList) {
		boolean isOutOfLimitKind = false;
		// 特惠活动商品购买款数限制数量
		int limit = context.getActivitySale().getLimit().intValue();
		// 活动如果限款，则判定用户已购买款项加上当前购买款项是否超出限额
		if (limit > 0) {
			int kindNumOfAdd = countAddKindNum(context.getActivitySkuIds(), buyRecordList);
			if (kindNumOfAdd > (limit - buyRecordList.size())) {
				// 购买限款数大于特惠活动限款数量
				isOutOfLimitKind = true;
			}
		}
		return isOutOfLimitKind;
	}

	/**
	 * @Description: 计算特惠商品新增款项
	 * @param activitySkuIds 特惠商品id
	 * @param buyRecordList 购买记录列表
	 * @return int  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private int countAddKindNum(List<String> activitySkuIds, List<ActivitySaleRecord> buyRecordList) {
		int kindNumOfAdd = 0;
		for (String skuId : activitySkuIds) {
			if (!isExistsBuyRecord(skuId, buyRecordList)) {
				kindNumOfAdd++;
			}
		}
		return kindNumOfAdd;
	}

	/**
	 * @Description: 判断商品ID是否存在于已购买记录中
	 * @param skuId 特惠商品ID
	 * @param buyRecordList 购买记录
	 * @return boolean  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private boolean isExistsBuyRecord(String skuId, List<ActivitySaleRecord> buyRecordList) {
		boolean isExists = false;
		if (queryBuyRecord(skuId, buyRecordList) != null) {
			isExists = true;
		}
		return isExists;
	}

	/**
	 * @Description: 根据商品ID查找已购记录
	 * @param skuId 特惠商品ID
	 * @param buyRecordList 用户购买记录
	 * @return ActivitySaleRecord  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private ActivitySaleRecord queryBuyRecord(String skuId, List<ActivitySaleRecord> buyRecordList) {
		ActivitySaleRecord findResult = null;
		if (CollectionUtils.isEmpty(buyRecordList)) {
			return null;
		}
		for (ActivitySaleRecord saleRecord : buyRecordList) {
			if (skuId.equals(saleRecord.getSaleGoodsId())) {
				findResult = saleRecord;
				break;
			}
		}
		return findResult;
	}

	/**
	 * @Description: 特惠商品检查购买是否超额
	 * @param reqDto 订单请求对象
	 * @param buyRecordList 用户购买记录
	 * @return boolean  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private boolean isOutOfLimitBuy(TradeOrderReqDto reqDto, List<ActivitySaleRecord> buyRecordList)
			throws ServiceException {
		boolean isOutOfLimit = false;
		for (TradeOrderGoodsItem goodsItem : reqDto.getData().getList()) {
			if (!goodsItem.isPrivilege()) {
				continue;
			}
			String skuId = goodsItem.getSkuId();
			ActivitySaleGoods activityGoods = reqDto.getContext().getActivityGoods(skuId);
			if (activityGoods == null) {
				logger.error("根据店铺商品ID{}查询特惠商品信息为空-------->{}", skuId, CodeStatistical.getLineInfo());
				throw new ServiceException("根据店铺商品ID" + skuId + "查询特惠商品信息为空-------->" + CodeStatistical.getLineInfo());
			}
			// 限购数量
			int tradeMax = activityGoods.getTradeMax();
			// 限购 判定该用户购买的该店铺的该商品是否超过限定数量
			if (tradeMax > 0) {
				ActivitySaleRecord buyRecord = queryBuyRecord(skuId, buyRecordList);
				int boughtNum = buyRecord == null ? 0 : buyRecord.getSaleGoodsNum();
				if (goodsItem.getSkuNum() > (tradeMax - boughtNum)) {
					isOutOfLimit = true;
					break;
				}
			}
		}
		return isOutOfLimit;
	}

	/**
	 * @Description: 根据店铺商品ID获取库存对象
	 * @param context 请求处理缓存对象
	 * @param goodsItem 请求商品列表
	 * @param stockList 库存列表
	 * @return int 当前库存
	 * @throws ServiceException 服务异常  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private int findStock(TradeOrderContext context, TradeOrderGoodsItem goodsItem, List<GoodsStoreSkuStock> stockList)
			throws ServiceException {
		int currentStock = 0;
		GoodsStoreSkuStock storeSkuStock = null;
		for (GoodsStoreSkuStock tmp : stockList) {
			if (goodsItem.getSkuId().equals(tmp.getStoreSkuId())) {
				storeSkuStock = tmp;
				break;
			}
		}
		if (storeSkuStock == null) {
			logger.error("未查到店铺商品Id为{}的库存", goodsItem.getSkuId());
			throw new ServiceException("未查到店铺商品Id为" + goodsItem.getSkuId() + "的库存");
		}

		// 1:特惠活动商品 0:正常商品
		if (goodsItem.isPrivilege()) {
			currentStock = storeSkuStock.getLocked();
		} else {
			currentStock = storeSkuStock.getSellable();
		}
		return currentStock;
	}
}
