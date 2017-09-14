package com.okdeer.mall.order.handler.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Maps;
import com.okdeer.archive.goods.dto.StoreSkuComponentDto;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuStock;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.base.MockUtils;
import com.okdeer.mall.mock.MockFilePath;
import com.okdeer.mall.order.bo.CurrentStoreSkuBo;
import com.okdeer.mall.order.bo.StoreSkuParserBo;

import net.sf.json.JSONObject;

public class MockStoreSkuParserBo implements MockFilePath {

	public static final JsonMapper JSONMAPPER = JsonMapper.nonDefaultMapper();

	@SuppressWarnings("unused")
	public static StoreSkuParserBo mockFromFile() throws Exception {
		StoreSkuParserBo parserBo = instance();
		// 读取JSON文件
		JSONObject json = JSONObject.fromObject(MockUtils.getMockData(MOCK_CHECK_STOCK_PARSER_BO_PATH).get(0));
		// 活动商品映射关系
		Map<String, List<String>> activitySkuMap = JSONMAPPER.fromJson(json.optString("activitySkuMap"),
				JSONMAPPER.contructMapType(HashMap.class, String.class, List.class));
		// 活动商品列表
		List<ActivitySale> activityList = JSONMAPPER.fromJson(json.optString("activityList"),
				JSONMAPPER.contructCollectionType(List.class, ActivitySale.class));
		Map<String, ActivitySaleGoods> currentActivitySkuMap = JSONMAPPER.fromJson(
				json.optString("currentActivitySkuMap"),
				JSONMAPPER.contructMapType(HashMap.class, String.class, ActivitySaleGoods.class));
		Map<String, CurrentStoreSkuBo> currentSkuMap = JSONMAPPER.fromJson(json.optString("currentSkuMap"),
				JSONMAPPER.contructMapType(HashMap.class, String.class, CurrentStoreSkuBo.class));
		List<GoodsStoreSku> currentSkuList = JSONMAPPER.fromJson(json.optString("currentSkuList"),
				JSONMAPPER.contructCollectionType(List.class, GoodsStoreSku.class));
		List<String> comboSkuIdList = JSONMAPPER.fromJson(json.optString("comboSkuIdList"),
				JSONMAPPER.contructCollectionType(List.class, String.class));
		List<String> componentSkuIdList = JSONMAPPER.fromJson(json.optString("componentSkuIdList"),
				JSONMAPPER.contructCollectionType(List.class, String.class));
		List<StoreSkuComponentDto> componentList = JSONMAPPER.fromJson(
				json.getJSONObject("componentSkuMap").optString("8a8080db5e55e692015e5602bac2000e"),
				JSONMAPPER.contructCollectionType(List.class, StoreSkuComponentDto.class));
		Map<String, List<StoreSkuComponentDto>> componentSkuMap = Maps.newHashMap();
		componentSkuMap.put("8a8080db5e55e692015e5602bac2000e", componentList);
		List<String> skuIdList = JSONMAPPER.fromJson(json.optString("skuIdList"),
				JSONMAPPER.contructCollectionType(List.class, String.class));
		Map<String, Integer> skuActNumMap = JSONMAPPER.fromJson(json.optString("skuActNumMap"),
				JSONMAPPER.contructMapType(HashMap.class, String.class, Integer.class));
		Set<String> categoryIdSet = JSONMAPPER.fromJson(json.optString("categoryIdSet"),
				JSONMAPPER.contructCollectionType(Set.class, String.class));
		Map<String, GoodsStoreSkuStock> bindStockMap = JSONMAPPER.fromJson(json.optString("bindStockMap"),
				JSONMAPPER.contructMapType(HashMap.class, String.class, GoodsStoreSkuStock.class));
		Set<String> storeIdSet = JSONMAPPER.fromJson(json.optString("storeIdSet"),
				JSONMAPPER.contructCollectionType(Set.class, String.class));

		parserBo.setActivitySkuMap(activitySkuMap);
		parserBo.loadActivityList(activityList);
		parserBo.setCurrentActivitySkuMap(currentActivitySkuMap);
		parserBo.setCurrentSkuMap(currentSkuMap);
		parserBo.setCurrentSkuList(currentSkuList);
		parserBo.setComponentSkuIdList(componentSkuIdList);
		parserBo.setComponentSkuMap(componentSkuMap);
		parserBo.setSkuIdList(skuIdList);
		parserBo.setCategoryIdSet(categoryIdSet);
		ReflectionTestUtils.setField(parserBo, "skuActNumMap", skuActNumMap);
		ReflectionTestUtils.setField(parserBo, "bindStockMap", bindStockMap);
		ReflectionTestUtils.setField(parserBo, "storeIdSet", storeIdSet);

		return parserBo;
	}

	public static StoreSkuParserBo mock() {
		StoreSkuParserBo parserBo = instance();
		parserBo.parseCurrentSku();
		// 初始化商品购买数量
		initBuyNum(parserBo.getCurrentSkuMap());
		return parserBo;
	}

	public static StoreSkuParserBo instance() {
		return new StoreSkuParserBo(MockUtils.getMockListData(MOCK_CHECK_SKU_LIST_PATH, GoodsStoreSku.class));
	}

	/**
	 * @Description: 初始化商品购买数量
	 * @return   
	 * @author maojj
	 * @date 2017年9月14日
	 */
	public static void initBuyNum(Map<String, CurrentStoreSkuBo> currentSkuMap) {
		currentSkuMap.forEach((storeSkuId, storeSku) -> {
			// 初始化每一个商品购买数量为2
			storeSku.setQuantity(2);
		});
	}

	/**
	 * @Description: 初始化特价不限购商品
	 * @param parserBo   
	 * @author maojj
	 * @date 2017年9月14日
	 */
	public static void initLowUnLimit(StoreSkuParserBo parserBo) {
		clearActivityGoods(parserBo);
		// 特价不限购
		ActivitySaleGoods lowUnLimit = new ActivitySaleGoods();
		lowUnLimit.setStoreSkuId("8a8080835e50ec28015e50f2f799000a");
		lowUnLimit.setTradeMax(0);
		parserBo.getCurrentActivitySkuMap().put("8a8080835e50ec28015e50f2f799000a", lowUnLimit);
		parserBo.getCurrentSkuMap().get("8a8080835e50ec28015e50f2f799000a")
				.setActivityType(ActivityTypeEnum.LOW_PRICE.ordinal());
	}

	/**
	 * @Description: 初始化特价未超出限购
	 * @param parserBo   
	 * @author maojj
	 * @date 2017年9月14日
	 */
	public static void initLowInLimit(StoreSkuParserBo parserBo) {
		clearActivityGoods(parserBo);
		// 特价限购2件，且已购1件
		ActivitySaleGoods lowInLimit = new ActivitySaleGoods();
		lowInLimit.setStoreSkuId("8a8080835e50ec28015e50f7b2750011");
		lowInLimit.setTradeMax(2);
		parserBo.getCurrentActivitySkuMap().put("8a8080835e50ec28015e50f7b2750011", lowInLimit);
		parserBo.getBuyGoodsCount().put("8a8080835e50ec28015e50f7b2750011", 1);
		parserBo.getCurrentSkuMap().get("8a8080835e50ec28015e50f7b2750011")
				.setActivityType(ActivityTypeEnum.LOW_PRICE.ordinal());
	}

	/**
	 * @Description: 初始化特价超出限购
	 * @param parserBo   
	 * @author maojj
	 * @date 2017年9月14日
	 */
	public static void initLowOutLimit(StoreSkuParserBo parserBo) {
		clearActivityGoods(parserBo);
		// 特价限购2件，且已购2件
		ActivitySaleGoods lowOutLimit = new ActivitySaleGoods();
		lowOutLimit.setStoreSkuId("8a8080835e50ec28015e50f2f799000a");
		lowOutLimit.setTradeMax(2);
		parserBo.getCurrentActivitySkuMap().put("8a8080835e50ec28015e50f2f799000a", lowOutLimit);
		parserBo.getBuyGoodsCount().put("8a8080835e50ec28015e50f2f799000a", 2);
		parserBo.getCurrentSkuMap().get("8a8080835e50ec28015e50f2f799000a")
				.setActivityType(ActivityTypeEnum.LOW_PRICE.ordinal());
	}

	/**
	 * @Description: 初始化特惠限购未超出限购
	 * @param parserBo   
	 * @author maojj
	 * @date 2017年9月14日
	 */
	public static void initFavourInLimit(StoreSkuParserBo parserBo) {
		clearActivityGoods(parserBo);
		// 特惠限购5件，且已购3件
		ActivitySaleGoods favourInLimit = new ActivitySaleGoods();
		favourInLimit.setStoreSkuId("8a8080835e50ec28015e50f7b272000f");
		favourInLimit.setTradeMax(5);
		parserBo.getCurrentActivitySkuMap().put("8a8080835e50ec28015e50f7b272000f", favourInLimit);
		parserBo.getBuyGoodsCount().put("8a8080835e50ec28015e50f7b272000f", 3);
		parserBo.getCurrentSkuMap().get("8a8080835e50ec28015e50f7b272000f")
				.setActivityType(ActivityTypeEnum.SALE_ACTIVITIES.ordinal());
	}

	/**
	 * @Description: 初始化特惠限购超出限购
	 * @param parserBo   
	 * @author maojj
	 * @date 2017年9月14日
	 */
	public static void initFavourOutLimit(StoreSkuParserBo parserBo) {
		clearActivityGoods(parserBo);
		// 特惠限购5件，且已购4件
		ActivitySaleGoods favourInLimit = new ActivitySaleGoods();
		favourInLimit.setStoreSkuId("8a8080835e50ec28015e50f7b2780013");
		favourInLimit.setTradeMax(5);
		parserBo.getCurrentActivitySkuMap().put("8a8080835e50ec28015e50f7b2780013", favourInLimit);
		parserBo.getBuyGoodsCount().put("8a8080835e50ec28015e50f7b2780013", 4);
		parserBo.getCurrentSkuMap().get("8a8080835e50ec28015e50f7b2780013")
				.setActivityType(ActivityTypeEnum.SALE_ACTIVITIES.ordinal());
	}

	public static void clearActivityGoods(StoreSkuParserBo parserBo) {
		// 清空活动商品记录
		parserBo.getCurrentActivitySkuMap().clear();
	}

	/**
	 * @Description: 初始化不限款活动
	 * @param parserBo   
	 * @author maojj
	 * @date 2017年9月14日
	 */
	public static void initUnLimitKind(StoreSkuParserBo parserBo) {
		ActivitySale actSale = new ActivitySale();
		actSale.setLimit(0);
		actSale.setId("8a8080f65e50f996015e5193308d000e");
		parserBo.loadActivityList(Arrays.asList(new ActivitySale[] { actSale }));
	}

	/**
	 * @Description: 限款但是未超出限款
	 * @param parserBo   
	 * @author maojj
	 * @date 2017年9月14日
	 */
	public static void initInLimitKind(StoreSkuParserBo parserBo) {
		ActivitySale actSale = new ActivitySale();
		actSale.setLimit(1);
		actSale.setId("8a8080f65e50f996015e51929c8d000a");
		parserBo.loadActivityList(Arrays.asList(new ActivitySale[] { actSale }));
		Map<String, List<String>> activitySkuMap = Maps.newHashMap();
		activitySkuMap.put("8a8080f65e50f996015e51929c8d000a",
				Arrays.asList(new String[] { "8a8080835e50ec28015e50f7b272000f" }));
		parserBo.setActivitySkuMap(activitySkuMap);
	}

	/**
	 * @Description: 限款但是未超出限款
	 * @param parserBo   
	 * @author maojj
	 * @date 2017年9月14日
	 */
	public static void initOutLimitKind(StoreSkuParserBo parserBo) {
		ActivitySale actSale = new ActivitySale();
		actSale.setLimit(1);
		actSale.setId("8a8080f65e50f996015e51929c8d000a");
		parserBo.loadActivityList(Arrays.asList(new ActivitySale[] { actSale }));
		Map<String, List<String>> activitySkuMap = Maps.newHashMap();
		activitySkuMap.put("8a8080f65e50f996015e51929c8d000a",
				Arrays.asList(new String[] { "8a8080835e50ec28015e50f7b272000f" }));
		parserBo.setActivitySkuMap(activitySkuMap);
		parserBo.getBuyKindCount().put("8a8080f65e50f996015e51929c8d000a", 1);
	}
}
