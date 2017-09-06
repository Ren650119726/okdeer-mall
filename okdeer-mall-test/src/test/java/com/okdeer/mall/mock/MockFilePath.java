package com.okdeer.mall.mock;  


public interface MockFilePath {

	/**
	 * 数据模拟基础文件路径
	 */
	String BASE_MOCK_PATH = "/com/okdeer/mall/mock/";
	
	/**
	 * 下单黑盒测试请求数据模拟文件路径
	 */
	String MOCK_ORDER_REQ = BASE_MOCK_PATH + "mock-order-req.json";
	
	/**
	 * 店铺数据模拟文件路径
	 */
	String MOCK_ORDER_STORE_PATH = BASE_MOCK_PATH + "mock-order-store.json";
	/**
	 * 下单黑盒测试商品数据模拟文件路径
	 */
	String MOCK_ORDER_SKU_PATH = BASE_MOCK_PATH + "mock-order-sku.json";
	
	/**
	 * 下单黑盒测试商品库存数据模拟文件路径
	 */
	String MOCK_ORDER_STOCK_PATH = BASE_MOCK_PATH + "mock-order-stock.json";
	
	/**
	 * 检查店铺请求数据模拟文件路径
	 */
	String MOCK_CHECK_STORE_REQ_PATH = BASE_MOCK_PATH + "mock-check-store-req.json";
	
	/**
	 * 下单请求检查商品数据模拟文件路径
	 */
	String MOCK_CHECK_SKU_REQ_PATH = BASE_MOCK_PATH + "mock-check-sku-req.json";

	/**
	 * 下单请求商品列表数据模拟文件路径
	 */
	String MOCK_CHECK_SKU_LIST_PATH = BASE_MOCK_PATH + "mock-check-sku-list.json";

	/**
	 * 下单请求商品库存数据模拟文件路径
	 */
	String MOCK_CHECK_SKU_STOCK_PATH = BASE_MOCK_PATH + "mock-check-sku-stock.json";
	
	/**
	 * 捆绑商品关系数据模拟文件路径
	 */
	String MOCK_CHECK_SKU_BIND_REL_PATH = BASE_MOCK_PATH + "mock-check-sku-bind-rel.json";
	
}
