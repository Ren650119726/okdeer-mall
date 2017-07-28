package com.okdeer.mall.order.api.mock;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuStock;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.common.consts.LogConstants;
import com.okdeer.mall.base.MockUtils;

public class StoreSkuAndStockMock {
	
	private static final Logger logger = LoggerFactory.getLogger(StoreSkuAndStockMock.class);
	
	public static void main(String[] args){
		mockStock();
	}
	
	public static List<List<GoodsStoreSku>> mockSku(){
		List<List<GoodsStoreSku>> mockList = Lists.newArrayList();
		List<GoodsStoreSku> mockData = null;
		JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();
		try {
			List<String> dataList = MockUtils.getMockData("/com/okdeer/mall/order/api/mock/mock-sku.txt");
			for(String data : dataList){
				mockData = jsonMapper.fromJson(data, jsonMapper.contructCollectionType(List.class, GoodsStoreSku.class));
				mockList.add(mockData);
			}
		} catch (IOException e) {
			logger.error(LogConstants.ERROR_EXCEPTION,e);
		}
		
		return mockList;
	}
	
	public static List<List<GoodsStoreSkuStock>> mockStock(){
		List<List<GoodsStoreSkuStock>> mockList = Lists.newArrayList();
		List<GoodsStoreSkuStock> mockData = null;
		JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();
		try {
			List<String> dataList = MockUtils.getMockData("/com/okdeer/mall/order/api/mock/mock-stock.txt");
			for(String data : dataList){
				mockData = jsonMapper.fromJson(data, jsonMapper.contructCollectionType(List.class, GoodsStoreSkuStock.class));
				mockList.add(mockData);
			}
		} catch (IOException e) {
			logger.error(LogConstants.ERROR_EXCEPTION,e);
		}
		
		return mockList;
	}
}
