package com.okdeer.mall.order.handler.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.dto.StoreSkuParamDto;
import com.okdeer.archive.goods.service.StoreSkuApi;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuStock;
import com.okdeer.archive.goods.store.enums.BSSC;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.stock.service.GoodsStoreSkuStockApi;
import com.okdeer.archive.stock.service.StockManagerJxcServiceApi;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.entity.StoreInfoExt;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleGoodsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleMapper;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.bo.CurrentStoreSkuBo;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderItemDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.enums.OrderOptTypeEnum;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.PickUpTypeEnum;
import com.okdeer.mall.order.handler.RequestHandler;

/**
 * ClassName: CheckSkuServiceImpl 
 * @Description: 检查商品信息
 * @author maojj
 * @date 2017年1月5日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.0 			2017年1月5日				maojj
 */
@Service("checkSkuService")
public class CheckSkuServiceImpl implements RequestHandler<PlaceOrderParamDto, PlaceOrderDto>{

	/**
	 * 店铺商品Api
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;

	/**
	 * 特惠活动Mapper
	 */
	@Resource
	private ActivitySaleMapper activitySaleMapper;

	/**
	 * 特惠商品Mapper
	 */
	@Resource
	private ActivitySaleGoodsMapper activitySaleGoodsMapper;
	
	/**
	 * 库存管理Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StockManagerJxcServiceApi stockManagerJxcServiceApi;
	
	/**
	 * 店铺商品库存Service
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuStockApi goodsStoreSkuStockApi;
	
	@Reference(version = "1.0.0", check = false)
	private StoreSkuApi storeSkuApi;
	

	@Override
	public void process(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		PlaceOrderParamDto paramDto = req.getData();
		// 提取下单商品ID清单
		List<String> skuIdList = extractSkuId(paramDto.getSkuList());
		// 查询当前商品信息
		List<GoodsStoreSku> currentSkuList = findCurrentSkuList(skuIdList);
		// 判断商品列表与请求清单是否一致
		if (currentSkuList.size() != skuIdList.size()) {
			resp.setResult(ResultCodeEnum.GOODS_IS_CHANGE);
			return;
		}
		// 解析当前店铺商品列表。获取当前商品的价格、库存、限购数量
		StoreSkuParserBo parserBo = parseCurrentSkuList(currentSkuList,paramDto.getChannel());
		// 检查请求商品与店铺是否一致
		Set<String> storeIdSet = parserBo.getStoreIdSet();
		if(storeIdSet.size() > 1 || !storeIdSet.contains(paramDto.getStoreId())){
			resp.setResult(ResultCodeEnum.STORE_SKU_INCONSISTENT);
			return;
		}
		parserBo.setSkuIdList(skuIdList);
		parserBo.loadBuySkuList(paramDto.getSkuList());
		// 缓存商品解析结果
		paramDto.put("parserBo", parserBo);
		// 查询组合商品库存
		if(CollectionUtils.isNotEmpty(parserBo.getComboSkuIdList())){
			StoreSkuParamDto skuParamDto = new StoreSkuParamDto();
			skuParamDto.setStoreSkuIds(parserBo.getComboSkuIdList());
			List<Integer> comboStockList = storeSkuApi.findCompositeGoodsActualStockByIds(skuParamDto);
			parserBo.refreshComboStockList(comboStockList);
		}
		
		List<GoodsStoreSkuStock> stockList = goodsStoreSkuStockApi.findByStoreSkuIdList(skuIdList);
		parserBo.loadStockList(stockList);
		
		// 检查商品信息是否发生变化
		ResultCodeEnum checkResult = isChange(paramDto, parserBo);
		if (checkResult != ResultCodeEnum.SUCCESS) {
			resp.setResult(checkResult);
		}
		// 计算运费
		calculateFare(paramDto,parserBo,resp);
	}

	public List<GoodsStoreSku> findCurrentSkuList(List<String> skuIdList) {
		return goodsStoreSkuServiceApi.findStoreSkuForOrder(skuIdList);
	}

	private List<String> extractSkuId(List<PlaceOrderItemDto> itemList) {
		List<String> skuIdList = new ArrayList<String>();
		for (PlaceOrderItemDto item : itemList) {
			skuIdList.add(item.getStoreSkuId());
		}
		return skuIdList;
	}

	private StoreSkuParserBo parseCurrentSkuList(List<GoodsStoreSku> storeSkuList,OrderResourceEnum channel)
			throws ServiceException {
		StoreSkuParserBo parserBo = new StoreSkuParserBo(storeSkuList);
		// 从商品列表中提取正在进行中的商品活动关系
		Map<String, List<String>> activitySkuMap = extractSkuActivityRelation(storeSkuList, parserBo,channel);
		List<ActivitySaleGoods> activitySkuList = new ArrayList<ActivitySaleGoods>();
		if (activitySkuMap != null) {
			// 存在活动商品则获取正在进行中的活动商品信息
			for (Map.Entry<String, List<String>> entry : activitySkuMap.entrySet()) {
				activitySkuList.addAll(activitySaleGoodsMapper.findActivityGoodsList(entry.getKey(), entry.getValue()));
			}
		}
		parserBo.loadSaleGoodsList(activitySkuList);
		parserBo.setActivitySkuMap(activitySkuMap);
		// 解析当前商品信息。商品存在活动则获取活动价格。不存在活动则获取线上销售价
		parserBo.parseCurrentSku();
		return parserBo;
	}

	/**
	 * @Description: 提取正在进行中的商品活动关系
	 * @param storeSkuList 当前商品列表
	 * @return String
	 * @throws ServiceException 服务异常
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private Map<String, List<String>> extractSkuActivityRelation(List<GoodsStoreSku> storeSkuList,
			StoreSkuParserBo parserBo,OrderResourceEnum channel) throws ServiceException {
		// 存放活动Id和商品的对应关系
		Map<String, List<String>> activitySkuMap = new HashMap<String, List<String>>();
		// 商品参加的活动Id
		String activityId = null;
		for (GoodsStoreSku storeSku : storeSkuList) {
			activityId = storeSku.getActivityId();
			if (StringUtils.isNotEmpty(activityId)) {
				if (activitySkuMap.get(activityId) == null) {
					activitySkuMap.put(activityId, new ArrayList<String>());
				}
				activitySkuMap.get(activityId).add(storeSku.getId());
			}
		}
		// 已绑定的活动列表
		Set<String> boundActivityIds = activitySkuMap.keySet();
		if (CollectionUtils.isEmpty(boundActivityIds)) {
			// 不存在任何活动绑定关系
			return null;
		}
		// 查询正在进行中的活动信息
		List<ActivitySale> activityList = activitySaleMapper.findBySaleIds(boundActivityIds);
		// 正在进行中的活动商品映射关系
		parserBo.loadActivityList(activityList);
		if (CollectionUtils.isEmpty(activityList)) {
			// 不存在进行中的活动
			return null;
		}
		Map<String, List<String>> resultMap = new HashMap<String, List<String>>();
		for (ActivitySale sale : activityList) {
			if(sale.getType() == ActivityTypeEnum.LOW_PRICE && channel == OrderResourceEnum.WECHAT){
				// 微信不支持低价活动。如果商品参与了低价活动，但是是微信下单，则按照普通商品进行处理
				continue;
			}
			resultMap.put(sale.getId(), activitySkuMap.get(sale.getId()));
		}
		return resultMap;
	}
	
	public ResultCodeEnum isChange(PlaceOrderParamDto req,StoreSkuParserBo parserBo){
		ResultCodeEnum checkResult = ResultCodeEnum.SUCCESS;
		// 购买商品款数
		int kindSize = req.getSkuList().size();
		// 检查商品信息是否发生变化
		for (PlaceOrderItemDto item : req.getSkuList()) {
			CurrentStoreSkuBo currentSku = parserBo.getCurrentStoreSkuBo(item.getStoreSkuId());
			// 检查是否下架
			if (currentSku.getOnline() == BSSC.UNSHELVE) {
				if(kindSize > 1){
					checkResult = ResultCodeEnum.PART_GOODS_IS_CHANGE;
				}else{
					checkResult = ResultCodeEnum.GOODS_IS_CHANGE;
				}
			} else if (currentSku.getOnlinePrice().compareTo(item.getSkuPrice()) == 1) {
				if(kindSize > 1){
					checkResult = ResultCodeEnum.PART_GOODS_IS_CHANGE;
				}else{
					checkResult = ResultCodeEnum.GOODS_IS_CHANGE;
				}
			} else if (!currentSku.getUpdateTime().equals(item.getUpdateTime())) {
				if(kindSize > 1){
					checkResult = ResultCodeEnum.PART_GOODS_IS_CHANGE;
				}else{
					checkResult = ResultCodeEnum.GOODS_IS_CHANGE;
				}
			}
			
			if(checkResult != ResultCodeEnum.SUCCESS){
				break;
			}
		}
		return checkResult;
	}
	
	private void calculateFare(PlaceOrderParamDto paramDto,StoreSkuParserBo parserBo,Response<PlaceOrderDto> resp){
		if(paramDto.getOrderOptType() == OrderOptTypeEnum.ORDER_SUBMIT && paramDto.getPickType() == PickUpTypeEnum.TO_STORE_PICKUP){
			// 如果是提交订单，且是到店自提，不计算运费
			return;
		}
		StoreInfoExt storeExt = ((StoreInfo)paramDto.get("storeInfo")).getStoreInfoExt();
		// 店铺起送价
		BigDecimal startPrice = storeExt.getStartPrice() == null ? BigDecimal.valueOf(0.00) : storeExt.getStartPrice();
		// 店铺免配送费起送价
		BigDecimal freeFarePrice = storeExt.getFreeFreightPrice() == null ? BigDecimal.valueOf(0.0) : storeExt.getFreeFreightPrice();
		// 店铺运费
		BigDecimal fare = storeExt.getFreight() == null ? new BigDecimal(0.0) : storeExt.getFreight();
		// 获取订单总金额
		BigDecimal totalAmount = parserBo.getTotalItemAmount();
		if (totalAmount.compareTo(freeFarePrice) >= 0){
			parserBo.setFare(BigDecimal.valueOf(0.00));
		}else if(totalAmount.compareTo(startPrice) >= 0){
			parserBo.setFare(fare);
		}else{
			// 未达到起送价，提交订单失败。
			resp.setResult(ResultCodeEnum.SERV_ORDER_AMOUT_NOT_ENOUGH);
		}
	}
}
