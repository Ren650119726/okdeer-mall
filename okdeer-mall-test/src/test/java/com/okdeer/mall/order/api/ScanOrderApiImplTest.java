/** 
 *@Project: okdeer-mall-test 
 *@Author: guocp
 *@Date: 2017年10月18日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.order.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.AopTestUtils;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuPictureServiceApi;
import com.okdeer.base.common.model.RequestParams;
import com.okdeer.base.common.model.ResponseData;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.redis.IRedisTemplateWrapper;
import com.okdeer.jxc.branch.entity.Branches;
import com.okdeer.jxc.branch.service.BranchesServiceApi;
import com.okdeer.jxc.common.result.RespSelfJson;
import com.okdeer.jxc.common.result.SelfPayResultEnums;
import com.okdeer.jxc.pos.service.SelfPayOrderServiceApi;
import com.okdeer.jxc.pos.vo.SelfPayTradeInfoVo;
import com.okdeer.jxc.sale.goods.entity.PosGoods;
import com.okdeer.jxc.sale.goods.service.PosGoodsService;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.dto.ScanOrderDto;
import com.okdeer.mall.order.dto.ScanOrderParamDto;
import com.okdeer.mall.order.dto.ScanPosStoreDto;
import com.okdeer.mall.order.dto.ScanSkuParamDto;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.service.ScanOrderApi;
import com.okdeer.mall.order.service.ScanOrderService;
import com.okdeer.mall.order.service.TradeOrderService;

/**
 * ClassName: ScanOrderApiImplTest 
 * @Description: 扫码购测试用例
 * @author guocp
 * @date 2017年10月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class ScanOrderApiImplTest extends BaseServiceTest {

	private ScanOrderApi scanOrderApi;

	private ScanOrderService scanOrderService;
	
	@Mock
	private GoodsStoreSkuPictureServiceApi goodsStoreSkuPictureService;

	@Mock
	private SelfPayOrderServiceApi selfPayOrderServiceApi;

	@Mock
	private PosGoodsService posGoodsService;

	@Mock
	private BranchesServiceApi branchesServiceApi;

	@Mock
	private IRedisTemplateWrapper<String, Object> redisTemplateWrapper;

	@Mock
	RequestHandler<PlaceOrderParamDto, PlaceOrderDto> checkFavourService;

	@Mock
	RequestHandler<PlaceOrderParamDto, PlaceOrderDto> checkPinMoneyService;
    
    @Mock
	private TradeOrderService tradeOrderService;
    
    @Mock
    private ActivityCouponsRecordMapper activityCouponsRecordMapper;
    
    @Mock
    private RocketMQProducer rocketMQProducer;
	
	protected void initMocks() throws Exception {
		// 初始化引用
		setUp();
	}

	/**
	 * Before
	 * @author guocp
	 * @date 2017年7月31日
	 */
	public void setUp() {
		scanOrderApi = (ScanOrderApi) AopTestUtils
				.getTargetObject((ScanOrderApi) this.applicationContext.getBean(ScanOrderApi.class));
		ReflectionTestUtils.setField(scanOrderApi, "goodsStoreSkuPictureService", goodsStoreSkuPictureService);
		ReflectionTestUtils.setField(scanOrderApi, "selfPayOrderServiceApi", selfPayOrderServiceApi);
		ReflectionTestUtils.setField(scanOrderApi, "posGoodsService", posGoodsService);
		ReflectionTestUtils.setField(scanOrderApi, "branchesServiceApi", branchesServiceApi);
		ReflectionTestUtils.setField(scanOrderApi, "redisTemplateWrapper", redisTemplateWrapper);
		
		scanOrderService =(ScanOrderService) AopTestUtils
				.getTargetObject((ScanOrderService) this.applicationContext.getBean(ScanOrderService.class));
		ReflectionTestUtils.setField(scanOrderService, "tradeOrderService", tradeOrderService);
		ReflectionTestUtils.setField(scanOrderService, "activityCouponsRecordMapper", activityCouponsRecordMapper);
		ReflectionTestUtils.setField(scanOrderService, "rocketMQProducer", rocketMQProducer);
	
	}


	@Test
	public void testConfirmOrder() throws Exception {
		String jsonString = "{\"branchId\":\"5592971b276511e6aaff00163e010eb1\",\"userId\":\"143213006656192befa3a55b42eda438\",\"orderResource\":\"4\",\"amount\":1.50,\"list\":[{\"skuId\":\"d437ed87276311e6aaff00163e010eb1\",\"price\":1.50,\"quantity\":1.0}]}";
		String resultJsonString = "{\"list\":[{\"id\":\"9a79222a55bf4c40a83ed2db9ddc45f9\",\"orderId\":\"9e86addf64664ceb85d442ba4687e556\",\"rowNo\":1,\"skuId\":\"d437ed87276311e6aaff00163e010eb1\",\"skuCode\":\"240292\",\"skuName\":\"阿华田串串装机智豆7.5g\",\"saleType\":\"A\",\"saleNum\":1.0,\"originalPrice\":1.5000,\"salePrice\":1.5000,\"totalAmount\":1.50,\"saleAmount\":1.50,\"discountAmount\":0.00,\"platDiscountAmount\":0,\"costPrice\":0.0000,\"supplierId\":\"081c1000456211e689250060569e27f8\",\"supplierType\":\"A\",\"supplierRate\":0.0000,\"activityType\":0,\"activityId\":\"0\",\"createTime\":1510292265863,\"pricingType\":0,\"allowActivity\":true,\"split\":true,\"index\":0,\"posPriceType\":0,\"posSalePrice\":0}],\"totalAmount\":1.50,\"saleAmount\":1.50,\"discountAmount\":0.00,\"subZeroAmount\":0.00,\"paymentAmount\":1.5,\"platDiscountAmount\":0,\"pinAmount\":0,\"orderId\":\"9e86addf64664ceb85d442ba4687e556\",\"orderNo\":\"XS990179917111000027\",\"ticketNo\":\"XS990179917111055422\",\"userId\":\"143213006656192befa3a55b42eda438\",\"branchId\":\"5592971b276511e6aaff00163e010eb1\",\"serialNum\":\"2017111013345572389256193\",\"tradeNum\":\"2017111013374589580321527\",\"notAllowDiscountsAmount\":0,\"allowDiscountsAmount\":1.50}";
		ScanOrderParamDto scanOrderDto = JsonMapper.nonDefaultMapper().fromJson(jsonString, ScanOrderParamDto.class);
		RespSelfJson respSelf = RespSelfJson.resultError(
				JsonMapper.nonDefaultMapper().fromJson(resultJsonString, SelfPayTradeInfoVo.class),
				SelfPayResultEnums.SUCCESS);
		given(selfPayOrderServiceApi.settlementOrder(any())).willReturn(respSelf);
		ResponseData<ScanOrderDto> resp = scanOrderApi.confirmOrder(scanOrderDto, getRequestParams());
		assertNotNull(resp.getCode());
	}

	@Test
	public void testSubmitOrder() throws Exception {
		String reqDtoParamJson = "{\"userId\":\"143213006656192befa3a55b42eda438\",\"lng\":0.0,\"lat\":0.0,\"isCheckTime\":0,\"activityId\":\"\",\"activityItemId\":\"\",\"couponsType\":0,\"recordId\":\"\",\"payType\":0,\"isUsePinMoney\":false,\"pinMoney\":\"0.00\",\"orderId\":\"677cb90a7ebd4b01a1c8f35fb76132fa\",\"orderNo\":\"XS990179917111000028\",\"cacheMap\":{},\"totalAmount\":0,\"enjoyFavourTotalAmount\":0,\"checkTime\":false}";
		PlaceOrderParamDto data = JsonMapper.nonDefaultMapper().fromJson(reqDtoParamJson,PlaceOrderParamDto.class);
		data.setActivityType("0");
		Request<PlaceOrderParamDto> reqDtoParam = new Request<PlaceOrderParamDto>();
		reqDtoParam.setData(data);
		reqDtoParam.setOrderResource(OrderResourceEnum.SWEEP.ordinal());
		reqDtoParam.setComplete(false);
		
		Response<PlaceOrderDto> respParam = new Response<PlaceOrderDto>();
		String resultJsonString = "{\"list\":[{\"id\":\"00456899e8df494b912a15bec06b069e\",\"orderId\":\"677cb90a7ebd4b01a1c8f35fb76132fa\",\"rowNo\":1,\"skuId\":\"d437ed87276311e6aaff00163e010eb1\",\"skuCode\":\"240292\",\"skuName\":\"阿华田串串装机智豆7.5g\",\"saleType\":\"A\",\"saleNum\":1.0,\"originalPrice\":1.5000,\"salePrice\":1.5000,\"totalAmount\":1.50,\"saleAmount\":1.50,\"discountAmount\":0.00,\"platDiscountAmount\":0,\"costPrice\":0.0000,\"supplierId\":\"081c1000456211e689250060569e27f8\",\"supplierType\":\"A\",\"supplierRate\":0.0000,\"activityType\":0,\"activityId\":\"0\",\"createTime\":1510294194071,\"pricingType\":0,\"allowActivity\":true,\"split\":true,\"index\":0,\"posPriceType\":0,\"posSalePrice\":0}],\"totalAmount\":1.50,\"saleAmount\":1.50,\"discountAmount\":0.00,\"subZeroAmount\":0.00,\"paymentAmount\":1.50,\"platDiscountAmount\":0.0,\"pinAmount\":0.00,\"orderId\":\"677cb90a7ebd4b01a1c8f35fb76132fa\",\"orderNo\":\"XS990179917111000028\",\"ticketNo\":\"XS990179917111065382\",\"userId\":\"143213006656192befa3a55b42eda438\",\"branchId\":\"5592971b276511e6aaff00163e010eb1\",\"serialNum\":\"2017111014095406683409199\",\"tradeNum\":\"2017111014095411836132447\",\"notAllowDiscountsAmount\":0,\"allowDiscountsAmount\":1.50}";
		RespSelfJson respSelf = RespSelfJson.resultError(
				JsonMapper.nonDefaultMapper().fromJson(resultJsonString , SelfPayTradeInfoVo.class),
				SelfPayResultEnums.SUCCESS);
		given(selfPayOrderServiceApi.getOrderInfo(any())).willReturn(respSelf);
		
		String orderDetialJson = "{\"branchId\":\"5592971b276511e6aaff00163e010eb1\",\"userId\":\"143213006656192befa3a55b42eda438\",\"paymentAmount\":1.5,\"platDiscountAmount\":0,\"pinMoneyAmount\":0.00,\"limitTime\":0,\"couponList\":[],\"list\":[{\"id\":\"34f0804851654581b7e45207414b7dff\",\"orderId\":\"435fd3f322d749138c1e671055503975\",\"skuId\":\"d437ed87276311e6aaff00163e010eb1\",\"skuCode\":\"240292\",\"skuName\":\"阿华田串串装机智豆7.5g\",\"saleNum\":1.0,\"originalPrice\":1.5000,\"salePrice\":1.5000,\"totalAmount\":1.50,\"saleAmount\":1.50,\"discountAmount\":0.00,\"platDiscountAmount\":0,\"createTime\":1510296748326,\"pricingType\":0,\"allowActivity\":true}],\"orderId\":\"435fd3f322d749138c1e671055503975\",\"orderNo\":\"XS990179917111000030\",\"serialNum\":\"201711101452283297258156\",\"tradeNum\":\"2017111014522834295175266\",\"totalAmount\":1.50,\"saleAmount\":1.50,\"discountAmount\":0.00,\"subZeroAmount\":0.00,\"notAllowDiscountsAmount\":0,\"allowDiscountsAmount\":1.50,\"userDiscount\":false}";
		ScanOrderDto orderDetail = JsonMapper.nonDefaultMapper().fromJson(orderDetialJson, ScanOrderDto.class);
		orderDetail.setOrderResource(OrderResourceEnum.SWEEP);
		given(redisTemplateWrapper.get(anyString())).willReturn(orderDetail);
		Response<PlaceOrderDto> resp = scanOrderApi.submitOrder(reqDtoParam, respParam, getRequestParams());
		assertNotNull(resp.getCode());
	}

	@Test
	public void testScanGoods() {
		// mock scanOrderApi.scanGoods
		String json = "{\"id\":\"4028f71b5c0a30aa015c0aa52a870018\",\"skuId\":\"2c9380af59f5836e015a0877a81e18be\",\"skuName\":\"农夫山泉 4L（箱）5555\",\"skuCode\":\"120921\",\"barCode\":\"6921168559173\",\"originalPrice\":37.00,\"vipPrice\":0.0100,\"type\":0,\"status\":0,\"isbind\":0,\"pricingType\":0,\"spec\":\"4L*6\",\"categoryId\":\"237b3ea483fe11e6823127f894e357cf\",\"categoryCode\":\"100001\",\"allowActivity\":true,\"branchId\":\"5592971b276511e6aaff00163e010eb1\",\"branchName\":\"开心小卖部（测试）\",\"branchType\":\"5\"}";
		PosGoods posGood = JsonMapper.nonDefaultMapper().fromJson(json, PosGoods.class);
		List<PosGoods> posGoods = Lists.newArrayList(posGood);
		// mock getGoodsInfo
		given(posGoodsService.getGoodsInfo(anyObject())).willReturn(posGoods);
		// when(posGoods.get(anyInt())).thenReturn(posGood);
		ScanSkuParamDto scanSkuParam = new ScanSkuParamDto();
		scanSkuParam.setBarCode("6921168559173");
		scanSkuParam.setBranchId("5592971b276511e6aaff00163e010eb1");
		ScanPosStoreDto scanPosStoreDto = scanOrderApi.scanGoods(scanSkuParam, getRequestParams());
		assertNotNull(scanPosStoreDto);
	}

	@Test
	public void testGetJxcStoreId() {
		// mock getBranchInfoById
		Branches branches = mock(Branches.class);
		branches.setBranchTypeStr("typeStr");
		branches.setBranchId("5592971b276511e6aaff00163e010eb1");
		branches.setBranchName("开心小卖铺");
		given(branchesServiceApi.getBranchInfoById(anyString())).willReturn(branches);
		when(branches.getBranchId()).thenReturn("5592971b276511e6aaff00163e010eb1");

		ScanSkuParamDto scanSkuParam = new ScanSkuParamDto();
		scanSkuParam.setBranchId("5592971b276511e6aaff00163e010eb1");
		ScanPosStoreDto dto = scanOrderApi.getJxcStoreId(scanSkuParam.getBranchId());
		assertEquals(scanSkuParam.getBranchId(), dto.getBranchId());
	}
	
//	@Test
//	public void testUpdateActivityCoupons() throws Exception {
// 		when(activityCouponsRecordMapper.updateActivityCouponsStatus(anyMapOf(String.class, Object.class))).thenReturn(Integer.valueOf(1));
//		scanOrderService.updateActivityCoupons(anyString(), anyString(), anyString(), anyString());
//	}

	private RequestParams getRequestParams() {
		RequestParams requestParams = new RequestParams();
		requestParams.setScreen("750x1334");
		requestParams.setMachineCode("05E1FAB8-F9BE-44C9-873F-E36E827120DD");
		return requestParams;
	}
	
}
