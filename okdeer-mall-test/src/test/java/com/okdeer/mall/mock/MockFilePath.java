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
	
	String MOCK_ORDER_STORE_INFO = BASE_MOCK_PATH + "mock-order-store-info.json";
	
	String MOCK_STORE_BREANCHES_PATH = BASE_MOCK_PATH + "mock-store-branches-info.json";
	
	/**
	 * 店铺数据模拟文件路径
	 */
	String MOCK_ORDER_STORE_PATH = BASE_MOCK_PATH + "mock-check-store-info.json";
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
	 * 服务店商品信息模拟文件路径
	 */
	String MOCK_SERV_SKU_LIST_PATH = BASE_MOCK_PATH + "mock-serv-sku-list.json";
	
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
	
	/**
	 * 检查库存请求数据模拟文件路径
	 */
	String MOCK_CHECK_STOCK_REQ_PATH = BASE_MOCK_PATH + "mock-check-stock-req.json";
	
	/**
	 * 检查库存商品解析结果模拟文件路径
	 */
	String MOCK_CHECK_STOCK_PARSER_BO_PATH = BASE_MOCK_PATH + "mock-check-stock-parser-bo.json";
	
	/**
	 * 店铺地址信息模拟文件路径
	 */
	String MOCK_STORE_ADDR_INFO_PATH = BASE_MOCK_PATH + "mock-store-addr-info.json";
}
