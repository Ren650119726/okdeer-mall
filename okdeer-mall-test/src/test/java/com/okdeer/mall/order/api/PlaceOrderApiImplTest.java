package com.okdeer.mall.order.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.AopTestUtils;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.okdeer.archive.goods.base.service.GoodsNavigateCategoryServiceApi;
import com.okdeer.archive.goods.dto.StoreSkuComponentDto;
import com.okdeer.archive.goods.dto.StoreSkuComponentParamDto;
import com.okdeer.archive.goods.dto.StoreSkuParamDto;
import com.okdeer.archive.goods.service.StoreSkuApi;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuStock;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.stock.service.GoodsStoreSkuStockApi;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.base.MockUtils;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.mock.MockFilePath;
import com.okdeer.mall.order.api.model.OrderModel;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.handler.impl.CheckFavourServiceImpl;
import com.okdeer.mall.order.handler.impl.CheckSkuServiceImpl;
import com.okdeer.mall.order.handler.impl.CheckStoreServiceImpl;
import com.okdeer.mall.order.handler.impl.PlaceOrderServiceImpl;
import com.okdeer.mall.order.service.PlaceOrderApi;

@RunWith(Parameterized.class)
public class PlaceOrderApiImplTest extends BaseServiceTest implements MockFilePath{
	
	private static final Logger logger = LoggerFactory.getLogger(PlaceOrderApiImplTest.class);
	
	private static final JsonMapper JSONMAPPER = JsonMapper.nonDefaultMapper();
	
	@Resource
	private PlaceOrderApi placeOrderApi;
	
	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto>  placeOrderService;
	
	@Mock
	private StoreInfoServiceApi storeInfoServiceApi;
	
	@Mock
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;
	
	@Mock
	private GoodsStoreSkuStockApi goodsStoreSkuStockApi;
	
	@Mock
	private StoreSkuApi storeSkuApi;
	
	@Mock
	private GoodsNavigateCategoryServiceApi goodsNavigateCategoryServiceApi;
	
	private OrderModel orderModel;
	
	/**
	 * mock店铺资料
	 */
	private StoreInfo storeMock;
	
	/**
	 * mock商品列表
	 */
	private List<GoodsStoreSku> storeSkuList;
	
	/**
	 * mock库存列表
	 */
	private List<GoodsStoreSkuStock> skuStockList;
	
	private List<StoreSkuComponentDto> bindRelList;
	
	public PlaceOrderApiImplTest(OrderModel orderModel) {
		this.orderModel = orderModel;
	}

	@Parameters
	public static Collection<Object[]> initParam() throws Exception {
		// 申明变量
		Collection<Object[]> initParams = new ArrayList<Object[]>();
		// 获取mock的json数据列表
		List<String> mockStrList = MockUtils.getMockData(MOCK_ORDER_REQ);
		for (String mockStr : mockStrList) {
			initParams.add(new Object[] {JSONMAPPER.fromJson(mockStr, new TypeReference<OrderModel>(){})});
		}
		return initParams;
	}
	
	@Override
	public void initMocks() throws Exception{
		// mock店铺信息
		initMockStore();
		// mocks商品信息
		initMockSkuAndStock();
		// mock dubbo服务
		try {
			initMockDubbo();
		} catch (Exception e) {
			logger.error("dubbo服务mock失败：",e);
		}
		this.bindRelList = MockUtils.getMockListData(MOCK_CHECK_SKU_BIND_REL_PATH, StoreSkuComponentDto.class);
		// 驱动事务回滚，方法上增加事务监听
		beforeMethod(this, "testSubmitOrder");
	}
	
	/**
	 * @Description: mock店铺信息   
	 * @author maojj
	 * @date 2017年7月27日
	 */
	private void initMockStore(){
		this.storeMock = MockUtils.getMockSingleData(MOCK_ORDER_STORE_INFO, StoreInfo.class);
	}
	
	private void initMockSkuAndStock(){
		this.storeSkuList = MockUtils.getMockListData(MOCK_CHECK_SKU_LIST_PATH, GoodsStoreSku.class);
		this.skuStockList = MockUtils.getMockListData(MOCK_CHECK_SKU_STOCK_PATH, GoodsStoreSkuStock.class);
	}
	
	/**
	 * @Description: 初始化mock dubbo服务   
	 * @author maojj
	 * @throws Exception 
	 * @date 2017年7月27日
	 */
	@SuppressWarnings("unchecked")
	public void initMockDubbo() throws Exception{
		CheckStoreServiceImpl checkStoreService = this.applicationContext.getBean(CheckStoreServiceImpl.class);
		CheckSkuServiceImpl checkSkuService = this.applicationContext.getBean(CheckSkuServiceImpl.class);
		CheckFavourServiceImpl checkFavourService =  this.applicationContext.getBean(CheckFavourServiceImpl.class);
		ActivityDiscountService activityDiscountService = this.applicationContext.getBean(ActivityDiscountService.class);
		ReflectionTestUtils.setField(checkStoreService, "storeInfoServiceApi", storeInfoServiceApi);
		ReflectionTestUtils.setField(checkSkuService, "goodsStoreSkuServiceApi", goodsStoreSkuServiceApi);
		ReflectionTestUtils.setField(checkSkuService, "goodsStoreSkuStockApi", goodsStoreSkuStockApi);
		ReflectionTestUtils.setField(checkSkuService, "storeSkuApi", storeSkuApi);
		ReflectionTestUtils.setField(checkFavourService, "goodsNavigateCategoryServiceApi", goodsNavigateCategoryServiceApi);
		ReflectionTestUtils.setField((ActivityDiscountService) AopTestUtils.getTargetObject(activityDiscountService),
				"storeInfoServiceApi", storeInfoServiceApi);
		ReflectionTestUtils.setField((PlaceOrderServiceImpl) AopTestUtils.getTargetObject(placeOrderService),
				"goodsStoreSkuStockApi", goodsStoreSkuStockApi);
		ReflectionTestUtils.setField((PlaceOrderServiceImpl) AopTestUtils.getTargetObject(placeOrderService),
				"goodsStoreSkuServiceApi", goodsStoreSkuServiceApi);
		
		given(storeInfoServiceApi.getStoreInfoById(anyString())).willReturn(this.storeMock);
		given(storeInfoServiceApi.findById(anyString())).willReturn(this.storeMock);
		given(goodsStoreSkuServiceApi.findStoreSkuForOrder(anyList())).willAnswer((invocation) -> {
			List<String> arg = (List<String>) invocation.getArguments()[0];
			return storeSkuList.stream().filter(item -> arg.contains(item.getId())).collect(Collectors.toList());
		});
		given(goodsStoreSkuStockApi.findByStoreSkuIdList(anyList())).willAnswer((invocation) -> {
			List<String> arg = (List<String>) invocation.getArguments()[0];
			return skuStockList.stream().filter(item -> arg.contains(item.getStoreSkuId())).collect(Collectors.toList());
		});
		given(storeSkuApi.findComponentByParam(any(StoreSkuComponentParamDto.class))).willAnswer((invocation) -> {
			StoreSkuComponentParamDto paramDto = (StoreSkuComponentParamDto)invocation.getArguments()[0];
			List<String> storeSkuIds = paramDto.getStoreSkuIds();
			return bindRelList.stream().filter(item -> storeSkuIds.contains(item.getStoreSkuId())).collect(Collectors.toList());
		});
		given(storeSkuApi.findCompositeGoodsActualStockByIds(any())).willAnswer((invocation) -> {
			StoreSkuParamDto paramDto = (StoreSkuParamDto)invocation.getArguments()[0];
			List<String> storeSkuIds = paramDto.getStoreSkuIds();
			return storeSkuIds.stream().map(e -> 100).collect(Collectors.toList());
		});
		given(goodsNavigateCategoryServiceApi.findNavigateCategoryByCouponId(anyString())).willAnswer((invocation) -> {
			String arg = (String) invocation.getArguments()[0];
			if("8a8080e15ebd79e7015ebd8099900004".equals(arg)){
				return Arrays.asList(new String[]{
						"52fabf5f276a11e6a672518bbc616d82",
						"52fc6d1a276a11e6a672518bbc616d82",
						"52fe1ad5276a11e6a672518bbc616d82",
						"52ffc890276a11e6a672518bbc616d82",
						"53017648276a11e6a672518bbc616d82",
						"530323fe276a11e6a672518bbc616d82",
						"5304f8c7276a11e6a672518bbc616d82",
						"5306a681276a11e6a672518bbc616d82",
						"5308543b276a11e6a672518bbc616d82",
						"530a01f1276a11e6a672518bbc616d82",
						"0002d6d7c59842d1be5e23b9acd9efc3",
						"52f8ea93276a11e6a672518bbc616d82",
						"52f73cd9276a11e6a672518bbc616d82",
						"52eeb12b276a11e6a672518bbc616d82",
						"52f085f4276a11e6a672518bbc616d82",
						"52f233af276a11e6a672518bbc616d82",
						"52f3e16b276a11e6a672518bbc616d82",
						"52f58f23276a11e6a672518bbc616d82",
						"52e9a7fb276a11e6a672518bbc616d82",
						"52eb55b6276a11e6a672518bbc616d82",
						"52ed0373276a11e6a672518bbc616d82",
						"52e62572276a11e6a672518bbc616d82",
						"52e7d32e276a11e6a672518bbc616d82"
				});
			}else{
				return Lists.newArrayList();
			}
		});
		given(goodsStoreSkuServiceApi.updateByPrimaryKeySelective(any())).willReturn(0);
	}
	
	@Test
	public void testConfirmOrder() throws Exception {
		Response<PlaceOrderDto> resp = placeOrderApi.confirmOrder(orderModel.getConfirmReq());
		assertEquals(orderModel.getConfirmExpiredCode(), resp.getCode());
	}
		
	@Test
	public void testSubmitOrder() throws Exception {
		Response<PlaceOrderDto> resp = placeOrderApi.submitOrder(orderModel.getSubmitReq());
		assertEquals(orderModel.getSubmitExpiredCode(), resp.getCode());
	}

}
