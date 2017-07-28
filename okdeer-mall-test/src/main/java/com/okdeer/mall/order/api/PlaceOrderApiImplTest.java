package com.okdeer.mall.order.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuStock;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.stock.service.GoodsStoreSkuStockApi;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.common.consts.LogConstants;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.activity.discount.service.impl.ActivityDiscountServiceImpl;
import com.okdeer.mall.base.AopTargetUtils;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.api.mock.StoreMock;
import com.okdeer.mall.order.api.mock.StoreSkuAndStockMock;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.handler.impl.CheckSkuServiceImpl;
import com.okdeer.mall.order.handler.impl.CheckStoreServiceImpl;
import com.okdeer.mall.order.service.PlaceOrderApi;

@RunWith(Parameterized.class)
public class PlaceOrderApiImplTest extends BaseServiceTest {
	
	private static final Logger logger = LoggerFactory.getLogger(PlaceOrderApiImplTest.class);

	@Resource
	private PlaceOrderApi placeOrderApi;
	
	private int index;

	private Request<PlaceOrderParamDto> confirmReq;

	private Request<PlaceOrderParamDto> submitReq;
	
	@Mock
	private StoreInfoServiceApi storeInfoServiceApi;
	
	@Mock
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;
	
	@Mock
	private GoodsStoreSkuStockApi goodsStoreSkuStockApi;
	
	/**
	 * mock店铺资料
	 */
	private List<StoreInfo> storeMockList;
	
	/**
	 * mock商品列表
	 */
	private List<GoodsStoreSku> storeSkuList;
	
	/**
	 * mock库存列表
	 */
	private List<GoodsStoreSkuStock> skuStockList;
	
	/**
	 * 返回code码
	 */
	private int[] codeArr;

	public PlaceOrderApiImplTest(int index,Request<PlaceOrderParamDto> confirmReq, Request<PlaceOrderParamDto> submitReq) {
		this.index = index;
		this.confirmReq = confirmReq;
		this.submitReq = submitReq;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConfirmOrder() throws Exception {
		given(storeInfoServiceApi.getStoreInfoById("4028b1e05cbf666b015cc3723f7d40a8")).willReturn(this.storeMockList.get(this.index));
		given(storeInfoServiceApi.findById("4028b1e05cbf666b015cc3723f7d40a8")).willReturn(this.storeMockList.get(this.index));
		given(goodsStoreSkuServiceApi.findStoreSkuForOrder(anyList())).willReturn(this.storeSkuList);
		given(goodsStoreSkuStockApi.findByStoreSkuIdList(anyList())).willReturn(this.skuStockList);
		Response<PlaceOrderDto> resp = placeOrderApi.confirmOrder(confirmReq);
		if(this.index == 5){
			assertEquals(ResultCodeEnum.SUCCESS.getCode(), resp.getCode());
		}else{
			assertEquals(this.codeArr[this.index], resp.getCode());
		}
	}
	
	@Test
	public void testSubmitOrder() throws Exception {
		Response<PlaceOrderDto> resp = placeOrderApi.submitOrder(submitReq);
	}
	
	@Parameters
	public static Collection<Object[]> initParam() throws Exception {
		Map<String, List<String>> dataMap = readReqData();
		Collection<Object[]> initParams = new ArrayList<Object[]>();
		List<String> confirmList = dataMap.get("confirm");
		List<String> submitList = dataMap.get("submit");
		int size = confirmList.size() > submitList.size() ? submitList.size() : confirmList.size();
		for (int i = 0; i < size; i++) {
			initParams.add(new Object[] { i,parseObject(confirmList.get(i)), parseObject(submitList.get(i)) });
		}
		return initParams;
	}
	
	@Override
	public void initMocks(){
		// mock返回消息码
		initMockCode();
		// mock店铺信息
		initMockStore();
		// mocks商品信息
		initMockSkuAndStock();
		// mock dubbo服务
		initMockDubbo();
	}
	
	private void initMockCode(){
		this.codeArr = new int[]{
				ResultCodeEnum.SERVER_STORE_NOT_EXISTS.getCode(),
				ResultCodeEnum.STORE_IS_CLOSED.getCode(),
				ResultCodeEnum.CVS_IS_PAUSE.getCode(),
				ResultCodeEnum.CVS_IS_PAUSE.getCode(),
				ResultCodeEnum.CVS_IS_PAUSE.getCode(),
				ResultCodeEnum.CVS_NOT_SUPPORT_TO_STORE.getCode()
				
		};
	}
	
	/**
	 * @Description: mock店铺信息   
	 * @author maojj
	 * @date 2017年7月27日
	 */
	private void initMockStore(){
		this.storeMockList = StoreMock.mock();
	}
	
	private void initMockSkuAndStock(){
		List<List<GoodsStoreSku>> mockSkuList = StoreSkuAndStockMock.mockSku();
		this.storeSkuList = mockSkuList.get(0);
		List<List<GoodsStoreSkuStock>> mockStockList = StoreSkuAndStockMock.mockStock();
		this.skuStockList = mockStockList.get(0);
	}
	
	/**
	 * @Description: 初始化mock dubbo服务   
	 * @author maojj
	 * @throws Exception 
	 * @date 2017年7月27日
	 */
	public void initMockDubbo(){
		CheckStoreServiceImpl checkStoreService = this.ac.getBean(CheckStoreServiceImpl.class);
		CheckSkuServiceImpl checkSkuService = this.ac.getBean(CheckSkuServiceImpl.class);
		ActivityDiscountService activityDiscountService = this.ac.getBean(ActivityDiscountService.class);
		ReflectionTestUtils.setField(checkStoreService, "storeInfoServiceApi", storeInfoServiceApi);
		ReflectionTestUtils.setField(checkSkuService, "goodsStoreSkuServiceApi", goodsStoreSkuServiceApi);
		ReflectionTestUtils.setField(checkSkuService, "goodsStoreSkuStockApi", goodsStoreSkuStockApi);
		ReflectionTestUtils.setField(AopTargetUtils.getTarget(activityDiscountService), "storeInfoServiceApi", storeInfoServiceApi);
	}

	private static Map<String, List<String>> readReqData() throws IOException {
		Map<String, List<String>> reqDataMap = new HashMap<String, List<String>>();
		reqDataMap.put("confirm", new ArrayList<String>());
		reqDataMap.put("submit", new ArrayList<String>());

		ClassPathResource resource = new ClassPathResource("/com/okdeer/mall/order/api/orderReq.txt");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(resource.getFile()));
			StringBuilder sb = null;
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("confirm start---") || line.startsWith("submit start---")) {
					sb = new StringBuilder();
				} else if (line.startsWith("confirm end---")) {
					reqDataMap.get("confirm").add(sb.toString());
				} else if (line.startsWith("submit start---")) {
					sb = new StringBuilder();
				} else if (line.startsWith("submit end---")) {
					reqDataMap.get("submit").add(sb.toString());
				} else {
					sb.append(line);
				}
			}
		} catch (Exception e) {
			logger.error(LogConstants.ERROR_EXCEPTION,e);
		} finally{
			if(reader != null){
				reader.close();
			}
		}
		return reqDataMap;
	}

	private static Request<PlaceOrderParamDto> parseObject(String reqData) {
		return JsonMapper.nonDefaultMapper().fromJson(reqData, new TypeReference<Request<PlaceOrderParamDto>>() {
		});
	}


}
