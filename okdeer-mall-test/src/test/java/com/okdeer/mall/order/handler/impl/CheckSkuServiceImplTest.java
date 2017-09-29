package com.okdeer.mall.order.handler.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okdeer.archive.goods.dto.StoreSkuComponentDto;
import com.okdeer.archive.goods.dto.StoreSkuComponentParamDto;
import com.okdeer.archive.goods.dto.StoreSkuParamDto;
import com.okdeer.archive.goods.service.StoreSkuApi;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuStock;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.stock.service.GoodsStoreSkuStockApi;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.mall.base.BaseModel;
import com.okdeer.mall.base.MockUtils;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.mock.MockFilePath;
import com.okdeer.mall.mock.StoreMock;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.handler.RequestHandler;

@RunWith(Parameterized.class)
public class CheckSkuServiceImplTest extends AbstractHandlerTest implements MockFilePath{

	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto> checkSkuService;

	private List<GoodsStoreSku> storeSkuList = null;
	
	private List<GoodsStoreSkuStock> stockList = null;

	private StoreInfo storeInfo = null;
	
	private List<StoreSkuComponentDto> bindRelList = null;

	/**
	 * 下单请求Dto
	 */
	private Request<PlaceOrderParamDto> req;
	
	/**
	 * 测试预期响应code
	 */
	private int expectedCode;

	@Mock
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;

	@Mock
	private GoodsStoreSkuStockApi goodsStoreSkuStockApi;
	
	@Mock
	private StoreSkuApi storeSkuApi;

	public CheckSkuServiceImplTest(Request<PlaceOrderParamDto> req,int expectedCode) {
		this.req = req;
		this.expectedCode = expectedCode;
	}

	@Override
	public void initMocks() {
		CheckSkuServiceImpl checkSkuService = this.applicationContext.getBean(CheckSkuServiceImpl.class);
		ReflectionTestUtils.setField(checkSkuService, "goodsStoreSkuServiceApi", goodsStoreSkuServiceApi);
		ReflectionTestUtils.setField(checkSkuService, "goodsStoreSkuStockApi", goodsStoreSkuStockApi);
		ReflectionTestUtils.setField(checkSkuService, "storeSkuApi", storeSkuApi);

		// mock商品列表
		this.storeSkuList = MockUtils.getMockListData(MOCK_CHECK_SKU_LIST_PATH, GoodsStoreSku.class);
		// mock库存列表
		this.stockList = MockUtils.getMockListData(MOCK_CHECK_SKU_STOCK_PATH, GoodsStoreSkuStock.class);
		this.storeInfo = StoreMock.initCvs();
		this.bindRelList = MockUtils.getMockListData(MOCK_CHECK_SKU_BIND_REL_PATH, StoreSkuComponentDto.class);
	}

	@Parameters
	public static Collection<Object[]> initParam() throws Exception {
		// 申明变量
		Collection<Object[]> initParams = new ArrayList<Object[]>();
		// 获取mock的json数据列表
		List<String> mockStrList = MockUtils.getMockData(MOCK_CHECK_SKU_REQ_PATH);
		// 解析JSON
		BaseModel<Request<PlaceOrderParamDto>> model = null;
		for (String mockStr : mockStrList) {
			model = JSONMAPPER.fromJson(mockStr, new TypeReference<BaseModel<Request<PlaceOrderParamDto>>>(){});
			initParams.add(new Object[] { model.getReq(), model.getCode() });
		}
		return initParams;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProcess() throws Exception {
		req.getData().put("storeInfo", this.storeInfo);
		Response<PlaceOrderDto> resp = initRespInstance();
		given(goodsStoreSkuServiceApi.findStoreSkuForOrder(anyList())).willAnswer((invocation) -> {
			List<String> arg = (List<String>) invocation.getArguments()[0];
			return storeSkuList.stream().filter(item -> arg.contains(item.getId())).collect(Collectors.toList());
		});
		given(goodsStoreSkuStockApi.findByStoreSkuIdList(anyList())).willAnswer((invocation) -> {
			List<String> arg = (List<String>) invocation.getArguments()[0];
			return stockList.stream().filter(item -> arg.contains(item.getStoreSkuId())).collect(Collectors.toList());
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
		this.checkSkuService.process(this.req, resp);
		assertEquals(this.expectedCode, resp.getCode());
	}
}
