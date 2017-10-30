package com.okdeer.mall.order.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.AopTestUtils;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
import com.okdeer.archive.store.entity.StoreBranches;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.service.StoreBranchesServiceApi;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.archive.system.service.SysUserLoginLogServiceApi;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.base.MockUtils;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.mock.MockFilePath;
import com.okdeer.mall.order.api.model.OrderModel;
import com.okdeer.mall.order.builder.TradeOrderBuilder;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.enums.PlaceOrderTypeEnum;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.handler.impl.CheckFavourServiceImpl;
import com.okdeer.mall.order.handler.impl.CheckGroupSkuServiceImpl;
import com.okdeer.mall.order.handler.impl.CheckServSkuServiceImpl;
import com.okdeer.mall.order.handler.impl.CheckSkuServiceImpl;
import com.okdeer.mall.order.handler.impl.CheckStoreServiceImpl;
import com.okdeer.mall.order.handler.impl.PlaceOrderServiceImpl;
import com.okdeer.mall.order.handler.impl.PlaceSeckillOrderServiceImpl;
import com.okdeer.mall.order.service.GetPreferentialService;
import com.okdeer.mall.order.service.OrderReturnCouponsService;
import com.okdeer.mall.order.service.PlaceOrderApi;
import com.okdeer.mall.order.service.TradeMessageService;

@RunWith(Parameterized.class)
public class PlaceOrderApiImplTest extends BaseServiceTest implements MockFilePath{
	
	private static final Logger logger = LoggerFactory.getLogger(PlaceOrderApiImplTest.class);
	
	private static final JsonMapper JSONMAPPER = JsonMapper.nonDefaultMapper();
	
	@Resource
	private PlaceOrderApi placeOrderApi;
	
	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto>  placeOrderService;
	
	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto>  placeSeckillOrderService;
	
	@Mock
	private StoreInfoServiceApi storeInfoServiceApi;
	
	@Mock
	private StoreBranchesServiceApi storeBranchesApi;
	
	@Mock
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;
	
	@Mock
	private GoodsStoreSkuStockApi goodsStoreSkuStockApi;
	
	@Mock
	private StoreSkuApi storeSkuApi;
	
	@Mock
	private GoodsNavigateCategoryServiceApi goodsNavigateCategoryServiceApi;
	
	@Mock
	private OrderReturnCouponsService orderReturnCouponsService;
	
	@Mock
	private SysUserLoginLogServiceApi sysUserLoginLogServiceApi;
	
	private OrderModel orderModel;
	
	/**
	 * mock店铺资料
	 */
	private List<StoreInfo> storeMock;
	
	/**
	 * mock店铺地址信息
	 */
	private StoreInfo storeAddrMock;
	
	private StoreBranches storeBranchMock;
	
	/**
	 * mock商品列表
	 */
	private List<GoodsStoreSku> storeSkuList;
	
	/**
	 * mock服务商品列表
	 */
	private List<GoodsStoreSku> servSkuList;
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
	}
	
	/**
	 * @Description: mock店铺信息   
	 * @author maojj
	 * @date 2017年7月27日
	 */
	private void initMockStore(){
		this.storeMock = MockUtils.getMockListData(MOCK_ORDER_STORE_INFO, StoreInfo.class);
		this.storeBranchMock = MockUtils.getMockSingleData(MOCK_STORE_BREANCHES_PATH, StoreBranches.class);
		this.storeAddrMock = MockUtils.getMockSingleData(MOCK_STORE_ADDR_INFO_PATH, StoreInfo.class);
	}
	
	private void initMockSkuAndStock(){
		this.storeSkuList = MockUtils.getMockListData(MOCK_CHECK_SKU_LIST_PATH, GoodsStoreSku.class);
		this.servSkuList = MockUtils.getMockListData(MOCK_SERV_SKU_LIST_PATH, GoodsStoreSku.class);
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
		CheckServSkuServiceImpl checkServSkuService = this.applicationContext.getBean(CheckServSkuServiceImpl.class);
		CheckGroupSkuServiceImpl checkGroupSkuService  = this.applicationContext.getBean(CheckGroupSkuServiceImpl.class);
		CheckFavourServiceImpl checkFavourService =  this.applicationContext.getBean(CheckFavourServiceImpl.class);
		ActivityDiscountService activityDiscountService = this.applicationContext.getBean(ActivityDiscountService.class);
		GetPreferentialService getPreferentialService = this.applicationContext.getBean(GetPreferentialService.class);
		TradeOrderBuilder tradeOrderBuilder = this.applicationContext.getBean(TradeOrderBuilder.class);
		TradeMessageService tradeMessageService = this.applicationContext.getBean(TradeMessageService.class);
		
		ReflectionTestUtils.setField(checkStoreService, "storeInfoServiceApi", storeInfoServiceApi);
		ReflectionTestUtils.setField(checkSkuService, "goodsStoreSkuServiceApi", goodsStoreSkuServiceApi);
		ReflectionTestUtils.setField(checkSkuService, "goodsStoreSkuStockApi", goodsStoreSkuStockApi);
		ReflectionTestUtils.setField(checkSkuService, "storeSkuApi", storeSkuApi);
		
		ReflectionTestUtils.setField(checkServSkuService, "goodsStoreSkuServiceApi", goodsStoreSkuServiceApi);
		ReflectionTestUtils.setField(checkServSkuService, "goodsStoreSkuStockApi", goodsStoreSkuStockApi);
		
		ReflectionTestUtils.setField(checkGroupSkuService, "goodsStoreSkuServiceApi", goodsStoreSkuServiceApi);
		
		ReflectionTestUtils.setField(tradeOrderBuilder, "storeBranchesApi", storeBranchesApi);
		ReflectionTestUtils.setField(tradeOrderBuilder, "storeInfoServiceApi", storeInfoServiceApi);
		
		ReflectionTestUtils.setField(checkFavourService, "goodsNavigateCategoryServiceApi", goodsNavigateCategoryServiceApi);
		ReflectionTestUtils.setField(getPreferentialService, "goodsNavigateCategoryServiceApi", goodsNavigateCategoryServiceApi);
		ReflectionTestUtils.setField((ActivityDiscountService) AopTestUtils.getTargetObject(activityDiscountService),
				"storeInfoServiceApi", storeInfoServiceApi);
		ReflectionTestUtils.setField((PlaceOrderServiceImpl) AopTestUtils.getTargetObject(placeOrderService),
				"goodsStoreSkuStockApi", goodsStoreSkuStockApi);
		ReflectionTestUtils.setField((PlaceOrderServiceImpl) AopTestUtils.getTargetObject(placeOrderService),
				"goodsStoreSkuServiceApi", goodsStoreSkuServiceApi);
		ReflectionTestUtils.setField((PlaceOrderServiceImpl) AopTestUtils.getTargetObject(placeOrderService),
				"orderReturnCouponsService", orderReturnCouponsService);
		
		ReflectionTestUtils.setField((PlaceSeckillOrderServiceImpl) AopTestUtils.getTargetObject(placeSeckillOrderService),
				"goodsStoreSkuStockApi", goodsStoreSkuStockApi);
		
		ReflectionTestUtils.setField(tradeMessageService, "sysUserLoginLogApi", sysUserLoginLogServiceApi);
		
		given(sysUserLoginLogServiceApi.findAllByUserId(anyString(), anyInt(), any(), any())).willReturn(null);
		
		given(storeInfoServiceApi.getStoreInfoById(anyString())).willAnswer((invocation) ->{
			String arg = (String) invocation.getArguments()[0];
			return storeMock.stream().filter(item -> arg.equals(item.getId())).findFirst().get();
		});
		given(storeInfoServiceApi.findById(anyString())).willAnswer((invocation) ->{
			String arg = (String) invocation.getArguments()[0];
			return storeMock.stream().filter(item -> arg.equals(item.getId())).findFirst().get();
		});
		given(storeInfoServiceApi.selectDefaultAddressById(anyString())).willReturn(this.storeAddrMock);
		
		given(storeBranchesApi.findBranches(anyString())).willReturn(this.storeBranchMock);
		given(goodsStoreSkuServiceApi.findStoreSkuForOrder(anyList())).willAnswer((invocation) -> {
			List<String> arg = (List<String>) invocation.getArguments()[0];
			return storeSkuList.stream().filter(item -> arg.contains(item.getId())).collect(Collectors.toList());
		});
		given(goodsStoreSkuServiceApi.selectSkuByIds(anyList())).willAnswer((invocation) -> {
			List<String> arg = (List<String>) invocation.getArguments()[0];
			return servSkuList.stream().filter(item -> arg.contains(item.getId())).collect(Collectors.toList());
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
		logger.info("确认订单请求参数：{}",JSONMAPPER.toJson(orderModel.getConfirmReq()));
		Response<PlaceOrderDto> resp = placeOrderApi.confirmOrder(orderModel.getConfirmReq());
		logger.info("确认订单返回结果：{}",JSONMAPPER.toJson(resp));
		assertEquals(orderModel.getConfirmExpiredCode(), resp.getCode());
	}
		
	@Test
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Rollback
	public void testSubmitOrder() throws Exception {
		// 驱动事务回滚，方法上增加事务监听
		beforeMethod(this, "testSubmitOrder");
		logger.info("提交订单请求参数：{}",JSONMAPPER.toJson(orderModel.getSubmitReq()));
		PlaceOrderParamDto paramDto = orderModel.getSubmitReq().getData();
		if(paramDto.getOrderType() != PlaceOrderTypeEnum.CVS_ORDER && StringUtils.isNotEmpty(paramDto.getPickTime())){
			// 如果不是便利店订单，且有提货时间的，需要自己mock时间。
			paramDto.setPickTime(mockPickUpTime());
		}
		Response<PlaceOrderDto> resp = placeOrderApi.submitOrder(orderModel.getSubmitReq());
		logger.info("确认订单返回结果：{}",JSONMAPPER.toJson(resp));
		assertEquals(orderModel.getSubmitExpiredCode(), resp.getCode());
		afterTestMethod(this,"testSubmitOrder");
		
	}

	private String mockPickUpTime(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		// mock 提货时间为下一天的11点。
		cal.add(Calendar.DAY_OF_YEAR, 1);
		cal.set(Calendar.HOUR_OF_DAY, 11);
		cal.set(Calendar.MINUTE, 0);
		return DateUtils.formatDate(cal.getTime(), "yyyy-MM-dd HH:mm");
	}
}
