package com.okdeer.mall.activity.coupons.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.AopTestUtils;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.okdeer.archive.goods.base.service.GoodsNavigateCategoryServiceApi;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.redis.IRedisTemplateWrapper;
import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.coupons.dto.ActivityCouponsRecordDto;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecordVo;
import com.okdeer.mall.activity.coupons.entity.CouponsFindVo;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsType;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.dto.ActivityCouponsRecordQueryParamDto;
import com.okdeer.mall.activity.service.impl.BldCouponsFilterStrategy;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.base.MockUtils;
import com.okdeer.mall.common.enums.UseClientType;
import com.okdeer.mall.common.enums.UseUserType;
import com.okdeer.mall.mock.MockFilePath;
import com.okdeer.mall.order.dto.PlaceOrderItemDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.service.TradeOrderServiceApi;
import com.okdeer.mall.order.vo.Coupons;
import com.okdeer.mall.system.service.SysBuyerUserServiceApi;

/**
 * ClassName: ActivityCouponsRecordServiceImplTest 
 * @Description: 代金券领取的单元测试用例
 * @author tuzhd
 * @date 2017年10月31日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@RunWith(Parameterized.class)
public class ActivityCouponsRecordServiceImplTest extends BaseServiceTest implements MockFilePath{
	
	private static final Logger LOG = LoggerFactory.getLogger(ActivityCouponsRecordServiceImplTest.class);
	@Resource
	private ActivityCouponsRecordService activityCouponsRecordService;
	
	@Resource
	private ActivityCouponsService activityCouponsService;
	
	@Resource
	private ActivityCouponsRecordServiceApi activityCouponsRecordServiceApi;
	
	@Mock
	private ActivityCouponsRecordMapper activityCouponsRecordMapper;
	
	@Resource
	private ActivityCouponsRecordMapper activityRecordMapper;
	
	@Mock
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;
	@Mock
	private SysBuyerUserServiceApi buyserUserService;
	@Mock
	private TradeOrderServiceApi tradeOrderService;
	@Mock
	private StoreInfoServiceApi storeInfoServiceApi;
	@Mock
	private GoodsNavigateCategoryServiceApi goodsNavigateCategoryServiceApi;
	
	@Resource
	private IRedisTemplateWrapper<String, Object> redisTemplateWrapper;
	
	private String ids;
	
	@Override
	public void initMocks(){
		// mock dubbo服务
		try {
			initMockDubbo();
		} catch (Exception e) {
			LOG.error("初始化数据异常：",e);
		}
	}
	
	private void initMockDubbo() throws Exception{
		// mock dubbo服务
		ActivityCouponsRecordService activityCouponsRecordServiceImpl = AopTestUtils.getTargetObject(this.applicationContext.getBean("activityCouponsRecordServiceImpl"));
		ReflectionTestUtils.setField(activityCouponsRecordServiceImpl, "goodsStoreSkuServiceApi", goodsStoreSkuServiceApi);
		ReflectionTestUtils.setField(activityCouponsRecordServiceImpl, "tradeOrderService", tradeOrderService);
		
		BldCouponsFilterStrategy bldCouponsFilterStrategy  = this.applicationContext.getBean(BldCouponsFilterStrategy.class);
		ReflectionTestUtils.setField(bldCouponsFilterStrategy, "storeInfoServiceApi", storeInfoServiceApi);
		ReflectionTestUtils.setField(bldCouponsFilterStrategy, "goodsNavigateCategoryServiceApi", goodsNavigateCategoryServiceApi);
		List<StoreInfo> storeMock = MockUtils.getMockListData(MOCK_ORDER_STORE_INFO, StoreInfo.class);
		BDDMockito.given(storeInfoServiceApi.findById(anyString())).willAnswer(invocation -> {
			String arg = (String) invocation.getArguments()[0];
			return storeMock.stream().filter(item -> arg.equals(item.getId())).findFirst().get();
		});
		BDDMockito.given(goodsNavigateCategoryServiceApi.findNavigateCategoryByCouponId(anyString())).willReturn(Arrays.asList(new String[]{
				"52df6f8e276a11e6a672518bbc616d82"
		}));
	}
	public ActivityCouponsRecordServiceImplTest(String  ids) {
		this.ids = ids;
	}
	
	
	
	@Parameters
	public static Collection<Object[]> initParam() throws Exception {
		Collection<Object[]> initParams = new ArrayList<Object[]>();
		initParams.add(new Object[] { "111"});
		return initParams;
	}
	 

	
	/**
	 * @Description: 优惠券查询
	 * @param userId  用户id
	 * @param deviceId 设备id
	 * @author tuzhd
	 * @throws Exception 
	 * @date 2017年8月9日
	 */
	@Test
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Rollback
	public void couponsRecordsTest() throws Exception{
		beforeMethod(this, "couponsRecordsTest");
		
		activityCouponsRecordService.findCouponsCountByUser(UseUserType.ALLOW_All, "141577260798e5eb9e1b8a0645b486c7");
		
		ActivityCouponsRecordQueryParamDto param = new ActivityCouponsRecordQueryParamDto();
		param.setStatus(0);
		activityCouponsRecordService.selectCountByParams(param);
		ActivityCouponsRecord record = new ActivityCouponsRecord();
		record.setCollectUserId("141577260798e5eb9e1b8a0645b486c7");
		record.setStoreId("4028bb2f5d9ba282015d9ba3d41d0001");
		activityCouponsRecordService.selectCouponsDetailByStoreId(record);
		CouponsFindVo vo = new CouponsFindVo();
		vo.setActivityId("402809815dcb3b31015dcb3c45160001");
		vo.setActivityItemId("402809815dcb3b31015dcb3b326a0000");
		activityCouponsRecordService.selectCouponsItem(vo);
		ActivityCouponsRecordVo vo2 = new ActivityCouponsRecordVo();
		activityCouponsRecordService.getAllRecords(vo2, 1, 10);
		//添加时间区间
		   vo2.setStartTime(new Date());
		   vo2.setEndTime(new Date());
		activityCouponsRecordService.getAllRecords(vo2, 1, 20);
		
		Map<String,Object> paraMap = new HashMap<>();
		paraMap.put("name", "活动");
		activityCouponsRecordService.getRecordExportData(paraMap);
		activityCouponsRecordService.procesRecordNoticeJob();
		
		activityCouponsRecordServiceApi.findCouponsRemain("141577260798e5eb9e1b8a0645b486c7", "402809815dcb3b31015dcb3b326a0000");
		activityCouponsRecordServiceApi.findInviteInfoByInviteUserId("141577260798e5eb9e1b8a0645b486c7");
		activityCouponsRecordServiceApi.findStatusCountByUserId("141577260798e5eb9e1b8a0645b486c7");
		paraMap.put("userId", "141577260798e5eb9e1b8a0645b486c7");
		paraMap.put("orderAmount", 10);
		paraMap.put("machineCode", "CBEB94C3-5521-40E9-ABBF-B1CD668D3A10");
		activityCouponsRecordServiceApi.findValidRechargeCoupons(paraMap);
		activityCouponsRecordServiceApi.selectActivityCouponsRecord(record);
		activityCouponsRecordServiceApi.selectByParams(paraMap);
		
		afterTestMethod(this, "couponsRecordsTest");
	}
	
	/**
	 * @Description: 修改代金券信息
	 * @param userId  用户id
	 * @param deviceId 设备id
	 * @author tuzhd
	 * @throws Exception 
	 * @date 2017年8月9日
	 */
	@Test
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Rollback
	public void couponsUpdateRecordsTest() throws Exception{
		beforeMethod(this, "couponsUpdateRecordsTest");
		
		Map<String,Object> paraMap = new HashMap<>();
		
		TradeOrder order  = new TradeOrder();
		order.setId("402801605d9795ab015da24d5af603b0");
		activityCouponsRecordService.releaseConpons(order);
		
		paraMap.put("recDate", "2017-08-02");
		paraMap.put("orderId", "402801605d9795ab015da24d5af603b0");
		paraMap.put("device_id", "CBEB94C3-5521-40E9-ABBF-B1CD668D3A10");
		paraMap.put("id", "402801605d9795ab015d9d22e2c60259");
		activityCouponsRecordService.updateActivityCouponsStatus(paraMap);
		List<ActivityCouponsRecordVo> list = Lists.newArrayList();
		ActivityCouponsRecordVo vo2 = new ActivityCouponsRecordVo();
		vo2.setFaceValue(5);
		vo2.setCreateUserId("141577260798e5eb9e1b8a0645b486c7");
		list.add(vo2);
		activityCouponsRecordService.updateRefundStatus(list, "141577260798e5eb9e1b8a0645b486c7");
		
		activityCouponsRecordService.updateUseStatus("402801605d9795ab015da24d5af603b0");
		
		//查询过期及生效的代金券
		List<ActivityCouponsRecordStatusEnum> listStatus=Lists.newArrayList();
		listStatus.add(ActivityCouponsRecordStatusEnum.USED);
		listStatus.add(ActivityCouponsRecordStatusEnum.UNUSED);
		List<ActivityCouponsRecordDto> lists = activityCouponsRecordServiceApi.findMyCouponsDetailByParams(listStatus, "141577260798e5eb9e1b8a0645b486c7");
		List<ActivityCouponsRecord> listRecordsList = BeanMapper.mapList(lists, ActivityCouponsRecord.class);
		List<ActivityCouponsRecord> listRecordsList2 = BeanMapper.mapList(lists, ActivityCouponsRecord.class);
		listRecordsList2.forEach(e->{
			e.setValidTime(e.getEffectTime());
			listRecordsList.add(e);
		});
		ActivityCouponsRecordService activityCouponsRecordServiceImpl = AopTestUtils.getTargetObject(this.applicationContext.getBean("activityCouponsRecordServiceImpl"));
		ReflectionTestUtils.setField(activityCouponsRecordServiceImpl, "activityCouponsRecordMapper", activityCouponsRecordMapper);
		given(activityCouponsRecordMapper.findForJob(any())).willReturn(listRecordsList);
		activityCouponsRecordService.updateStatusByJob();
		
		ReflectionTestUtils.setField(activityCouponsRecordServiceImpl, "activityCouponsRecordMapper", activityRecordMapper);
		
		
		afterTestMethod(this, "couponsUpdateRecordsTest");
	}
	
	/**
	 * @Description: 测试领券功能	
	 * @param userId  用户id
	 * @param deviceId 设备id
	 * @author tuzhd
	 * @throws Exception 
	 * @date 2017年11月18日
	 */
	@Test
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Rollback
	public void testFailVaild() throws Exception{
		beforeMethod(this, "testFailVaild");
		
		activityCouponsRecordServiceApi.addBeforeRecords("8a94e7465f5656b2015f5656b2ee0000", "13723770909", "141577260798e5eb9e1b8a0645b486c7", "testtest");
		//已经结束的活动
		activityCouponsRecordService.addRecordsByCollectId("2c9380c25bad2406015bad65eeaa000a", "141577260798e5eb9e1b8a0645b486c7");
		//限制新人领取 start
		activityCouponsRecordService.addRecordsByCollectId("2c9380bd5aaa2176015ad1833e330022", "141577260798e5eb9e1b8a0645b486c7");
		activityCouponsRecordServiceApi.addBeforeRecords("2c9380bd5aaa2176015ad1833e330022", "13723770909", "141577260798e5eb9e1b8a0645b486c7", "testtest");
		activityCouponsRecordServiceApi.addBeforeRecords("2c9380bd5aaa2176015ad1833e330022", "13723770509", "141577260798e5eb9e1b8a0645b486c7", "testtest");
		//限制新人领取end
		activityCouponsRecordService.addRecordsByCollectId("8a94e7465f5656b2015f5656b2ee0000", "141577260798e5eb9e1b8a0645b486c7", "141577260798e5eb9e1b8a0645b486c7", true);
		
		
		activityCouponsRecordService.addBeforeRecordsForWechatActivity("8a94e7465f5656b2015f5656b2ee0000", "13723770909","testtest");
		activityCouponsRecordService.addInviteUserHandler("141577260798e5eb9e1b8a0645b486c7", new String[]{"8a94e7465f5656b2015f5656b2ee0000"});
		Map<String,Object> params = new HashMap<>();
		params.put("exchangeCode", "11111");
		activityCouponsRecordService.addRecordForExchangeCode(params, "11111", "141577260798e5eb9e1b8a0645b486c7", ActivityCouponsType.advert_coupons);
		//错误的优惠码
		activityCouponsRecordService.addRecordForExchangeCode(params, "33332X", "141577260798e5eb9e1b8a0645b486c7", ActivityCouponsType.advert_coupons);
		activityCouponsRecordService.addRecordForRecevie("8a8080e95f387883015f387cfa300004", "141577260798e5eb9e1b8a0645b486c7", ActivityCouponsType.advert_coupons);
		params.put("randCode", "t6dcck4j");
		activityCouponsRecordServiceApi.addRecordForRandCode(params, "t6dcck4j", "141577260798e5eb9e1b8a0645b486c7", ActivityCouponsType.coupons);
		//错误的随机码
		params.put("randCode", "xxxxxxtt");
		activityCouponsRecordServiceApi.addRecordForRandCode(params, "xxxxxxtt", "141577260798e5eb9e1b8a0645b486c7", ActivityCouponsType.coupons);
		
		ActivityCouponsRecord activityCouponsRecord = activityCouponsRecordService.selectByPrimaryKey("8a8080fa5f76ab23015f779a6e95003e");
		activityCouponsRecord.setId(UuidUtils.getUuid());
		activityCouponsRecord.setStatus(ActivityCouponsRecordStatusEnum.UNUSED);
		activityCouponsRecordService.add(activityCouponsRecord);
		activityCouponsRecord.setId(UuidUtils.getUuid());
		activityCouponsRecordServiceApi.insertSelective(activityCouponsRecord);
		
		List<ActivityCoupons> lstActivityCoupons = Lists.newArrayList();
		lstActivityCoupons.add(activityCouponsService.selectByPrimaryKey("4028bb185e281bb1015e281bb1480000"));
		activityCouponsRecordService.drawCouponsRecord(lstActivityCoupons,  ActivityCouponsType.coupons, "141577260798e5eb9e1b8a0645b486c7");
		activityCouponsRecordServiceApi.insertCopyRecords("141577260798e5eb9e1b8a0645b486c7", "13545052325", "11111");
		//测试重复
		afterTestMethod(this, "testFailVaild");
	}
	
	/**
	 * @Description: 测试查询用户有效的代金券信息
	 * @throws Exception   
	 * @author maojj
	 * @date 2017年11月15日
	 */
	@Test
	public void testFindValidCoupons() throws Exception {
		FavourParamBO paramBo = new FavourParamBO();
		paramBo.setUserId("14527626891242d4d00a207c4d69bd80");
		paramBo.setStoreId("8a8080d15e4bffe6015e50ec13ce207a");
		paramBo.setTotalAmount(BigDecimal.valueOf(70));
		paramBo.setClientType(UseClientType.ONlY_APP_USE);
		paramBo.setChannel(OrderResourceEnum.CVSAPP);
		paramBo.setDeviceId("1AC43255-5E6C-4591-AF2D-FF675898229E");
		paramBo.setOrderType(OrderTypeEnum.PHYSICAL_ORDER);
		paramBo.setClientVersion("V2.6.2_A04");
		PlaceOrderItemDto goodsItem = new PlaceOrderItemDto();
		goodsItem.setStoreSkuId("8a8080db5e55e692015e5602babf000c");
		goodsItem.setQuantity(1);
		goodsItem.setSkuPrice(BigDecimal.valueOf(68));
		goodsItem.setSpuCategoryId("52df6f8e276a11e6a672518bbc616d82");
		PlaceOrderItemDto goodsItem1 = new PlaceOrderItemDto();
		goodsItem1.setStoreSkuId("8a8080835e50ec28015e50f7b2780013");
		goodsItem1.setQuantity(1);
		goodsItem1.setSkuPrice(BigDecimal.valueOf(2));
		goodsItem1.setSpuCategoryId("52ed0373276a11e6a672518bbc616d82");
		paramBo.setGoodsList(Arrays.asList(new PlaceOrderItemDto[]{
				goodsItem,goodsItem1
		}));
		
		List<Coupons> couponsList = activityCouponsRecordService.findValidCoupons(paramBo);
		assertEquals(12, couponsList.size());
	}
}
