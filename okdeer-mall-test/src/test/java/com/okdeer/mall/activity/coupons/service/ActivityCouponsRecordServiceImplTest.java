package com.okdeer.mall.activity.coupons.service;

import java.util.ArrayList;
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
import org.mockito.Mock;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.AopTestUtils;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.redis.IRedisTemplateWrapper;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecordVo;
import com.okdeer.mall.activity.coupons.entity.CouponsFindVo;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsType;
import com.okdeer.mall.activity.dto.ActivityCouponsRecordQueryParamDto;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.common.enums.UseUserType;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.service.TradeOrderServiceApi;
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
public class ActivityCouponsRecordServiceImplTest extends BaseServiceTest {
	@Resource
	private ActivityCouponsRecordService activityCouponsRecordService;
	
	@Resource
	private ActivityCouponsService activityCouponsService;
	
	@Resource
	private ActivityCouponsRecordServiceApi activityCouponsRecordServiceApi;
	
	@Mock
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;
	@Mock
	private SysBuyerUserServiceApi buyserUserService;
	@Mock
	private TradeOrderServiceApi tradeOrderService;
	
	@Resource
	private IRedisTemplateWrapper<String, Object> redisTemplateWrapper;
	
	private String ids;
	
	@Override
	public void initMocks(){
		// mock dubbo服务
		initMockDubbo();
		
	}
	
	private void initMockDubbo(){
		// mock dubbo服务
		ActivityCouponsRecordService activityCouponsRecordServiceImpl = AopTestUtils.getTargetObject(this.applicationContext.getBean("activityCouponsRecordServiceImpl"));
		ReflectionTestUtils.setField(activityCouponsRecordServiceImpl, "goodsStoreSkuServiceApi", goodsStoreSkuServiceApi);
		ReflectionTestUtils.setField(activityCouponsRecordServiceImpl, "tradeOrderService", tradeOrderService);
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
	 * @Description: 获取会员卡信息接口
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
//		activityCouponsRecordService.findMyCouponsDetailByParams(ActivityCouponsRecordStatusEnum.USED, "141577260798e5eb9e1b8a0645b486c7", true);
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
		vo2.setStartTime(new Date());
		vo2.setEndTime(new Date());;
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
		
		
		TradeOrder order  = new TradeOrder();
		order.setId("402801605d9795ab015da24d5af603b0");
		activityCouponsRecordService.releaseConpons(order);
		
		paraMap.put("recDate", "2017-08-02");
		paraMap.put("orderId", "402801605d9795ab015da24d5af603b0");
		paraMap.put("device_id", "CBEB94C3-5521-40E9-ABBF-B1CD668D3A10");
		paraMap.put("id", "402801605d9795ab015d9d22e2c60259");
		activityCouponsRecordService.updateActivityCouponsStatus(paraMap);
		List<ActivityCouponsRecordVo> list = Lists.newArrayList();
		vo2.setFaceValue(5);
		vo2.setCreateUserId("141577260798e5eb9e1b8a0645b486c7");
		list.add(vo2);
		activityCouponsRecordService.updateRefundStatus(list, "141577260798e5eb9e1b8a0645b486c7");
		activityCouponsRecordService.updateStatusByJob();
		activityCouponsRecordService.updateUseStatus("402801605d9795ab015da24d5af603b0");
		
		activityCouponsRecordServiceApi.addBeforeRecords("8a94e7465f5656b2015f5656b2ee0000", "13723770909", null, "testtest");
		activityCouponsRecordService.addRecordsByCollectId("8a94e7465f5656b2015f5656b2ee0000", "141577260798e5eb9e1b8a0645b486c7");
		activityCouponsRecordService.addRecordsByCollectId("8a94e7465f5656b2015f5656b2ee0000", "141577260798e5eb9e1b8a0645b486c7", "141577260798e5eb9e1b8a0645b486c7", true);
		
		
		activityCouponsRecordService.addBeforeRecordsForWechatActivity("8a94e7465f5656b2015f5656b2ee0000", "13723770909","testtest");
		activityCouponsRecordService.addInviteUserHandler("141577260798e5eb9e1b8a0645b486c7", new String[]{"8a94e7465f5656b2015f5656b2ee0000"});
		Map<String,Object> params = new HashMap<>();
		params.put("exchangeCode", "11111");
		activityCouponsRecordService.addRecordForExchangeCode(params, "11111", "141577260798e5eb9e1b8a0645b486c7", ActivityCouponsType.advert_coupons);
		activityCouponsRecordService.addRecordForRecevie("8a8080e95f387883015f387cfa300004", "141577260798e5eb9e1b8a0645b486c7", ActivityCouponsType.advert_coupons);
		params.put("randCode", "t6dcck4j");
		activityCouponsRecordServiceApi.addRecordForRandCode(params, "t6dcck4j", "141577260798e5eb9e1b8a0645b486c7", ActivityCouponsType.coupons);
		
		ActivityCouponsRecord activityCouponsRecord = activityCouponsRecordService.selectByPrimaryKey("8a8080fa5f76ab23015f779a6e95003e");
		activityCouponsRecord.setId(UuidUtils.getUuid());
		activityCouponsRecord.setStatus(ActivityCouponsRecordStatusEnum.UNUSED);
		activityCouponsRecordService.add(activityCouponsRecord);
		activityCouponsRecord.setId(UuidUtils.getUuid());
		activityCouponsRecordServiceApi.insertSelective(activityCouponsRecord);
		
		List<ActivityCoupons> lstActivityCoupons = Lists.newArrayList();
		lstActivityCoupons.add(activityCouponsService.selectByPrimaryKey("4028bb185e281bb1015e281bb1480000"));
		activityCouponsRecordService.drawCouponsRecord(lstActivityCoupons,  ActivityCouponsType.coupons, "141577260798e5eb9e1b8a0645b486c7");
		activityCouponsRecordServiceApi.insertCopyRecords("141577260798e5eb9e1b8a0645b486c7", "13723770909", "11111");
		
		afterTestMethod(this, "couponsRecordsTest");
	}
	
	
}
