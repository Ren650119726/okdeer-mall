package com.okdeer.mall.refunds;


import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
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

import com.google.common.collect.Lists;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.stock.service.GoodsStoreSkuStockApi;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.archive.system.pos.service.PosShiftExchangeServiceApi;
import com.okdeer.archive.system.service.SysUserLoginLogServiceApi;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.common.consts.DescriptConstants;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.base.MockUtils;
import com.okdeer.mall.order.api.PlaceOrderApiImplTest;
import com.okdeer.mall.order.dto.OrderRefundQueryParamDto;
import com.okdeer.mall.order.dto.OrderRefundsDto;
import com.okdeer.mall.order.dto.PhysOrderApplyRefundParamDto;
import com.okdeer.mall.order.dto.PhysicalOrderApplyDto;
import com.okdeer.mall.order.dto.PhysicalOrderApplyParamDto;
import com.okdeer.mall.order.dto.RefundsMoneyDto;
import com.okdeer.mall.order.dto.StoreConsumerApplyDto;
import com.okdeer.mall.order.dto.StoreConsumerApplyParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.enums.ConsumeStatusEnum;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.RefundOrderTypeEnum;
import com.okdeer.mall.order.mapper.TradeOrderItemDetailMapper;
import com.okdeer.mall.order.mapper.TradeOrderItemMapper;
import com.okdeer.mall.order.mapper.TradeOrderMapper;
import com.okdeer.mall.order.service.StoreConsumeOrderService;
import com.okdeer.mall.order.service.TradeOrderItemDetailService;
import com.okdeer.mall.order.service.TradeOrderRefundsApi;
import com.okdeer.mall.order.service.impl.JxcSynTradeorderRefundProcessLister;
import com.okdeer.mall.order.service.impl.StockOperateServiceImpl;
import com.okdeer.mall.order.service.impl.TradeMessageServiceImpl;
import com.okdeer.mall.order.service.impl.TradeOrderRefundsServiceImpl;

/**
 * ClassName: TradeOrderRefundsApiImplTest 
 * @Description: 退款接口类退款单元测试用例
 * @author tuzhd
 * @date 2017年7月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@RunWith(Parameterized.class)
public class TradeOrderRefundsApiImplTest extends BaseServiceTest {
	private static final Logger logger = LoggerFactory.getLogger(PlaceOrderApiImplTest.class);

	@Resource
	public TradeOrderRefundsApi tradeOrderRefundsApiImpl;
	
	private PhysicalOrderApplyParamDto physicalParam ;
	
	@Mock
	private PosShiftExchangeServiceApi posShiftExchangeService;
	@Mock
	private SysUserLoginLogServiceApi sysUserLoginLogApi;
	
	@Mock
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;
	@Mock
	TradeOrderItemDetailService tradeOrderItemDetailService;
	@Mock
	private StoreInfoServiceApi storeInfoService;
	@Mock
	private GoodsStoreSkuStockApi goodsStoreSkuStockApi;
	@Mock
	private TradeOrderItemDetailMapper tradeOrderItemDetailMapper;
	
	/**
	 * mock订单项列表
	 */
	private List<TradeOrderItemDetail> tradeOrderItemDetailList;
	
	@Resource
	private TradeOrderMapper tradeOrderMapper;
	@Resource
	private TradeOrderItemMapper tradeOrderItemMapper;
	
	private StoreConsumerApplyParamDto storeParamDto;
	private int index;
	
	@Override
	public void initMocks(){
		// mock dubbo服务
		initMockDubbo();
		
	}
	
	private void initMockDubbo(){
		// mock dubbo服务TradeMessageServiceImpl
		TradeMessageServiceImpl tradeMessageService = this.applicationContext.getBean(TradeMessageServiceImpl.class);
		JxcSynTradeorderRefundProcessLister jxcSynTradeorderRefundProcessLister = this.applicationContext.getBean(JxcSynTradeorderRefundProcessLister.class);
		StoreConsumeOrderService storeConsumeOrderService =  AopTestUtils.getTargetObject(this.applicationContext.getBean("storeConsumeOrderServiceImpl"));
		StockOperateServiceImpl stockOperateService = AopTestUtils.getTargetObject(this.applicationContext.getBean("stockOperateServiceImpl"));
		TradeOrderRefundsServiceImpl tradeOrderRefundsService = AopTestUtils.getTargetObject(this.applicationContext.getBean("tradeOrderRefundsServiceImpl"));
		
		ReflectionTestUtils.setField(tradeMessageService, "posShiftExchangeService", posShiftExchangeService);
		ReflectionTestUtils.setField(tradeMessageService, "sysUserLoginLogApi", sysUserLoginLogApi);
		ReflectionTestUtils.setField(jxcSynTradeorderRefundProcessLister, "goodsStoreSkuServiceApi", goodsStoreSkuServiceApi);
		
		ReflectionTestUtils.setField(stockOperateService, "goodsStoreSkuStockApi", goodsStoreSkuStockApi);
		ReflectionTestUtils.setField(storeConsumeOrderService, "storeInfoService", storeInfoService);
		ReflectionTestUtils.setField(storeConsumeOrderService, "tradeOrderItemDetailMapper", tradeOrderItemDetailMapper);
		
		ReflectionTestUtils.setField(tradeOrderRefundsService, "storeInfoService", storeInfoService);
		ReflectionTestUtils.setField(tradeOrderRefundsApiImpl, "tradeOrderItemDetailService", tradeOrderItemDetailService);
		ReflectionTestUtils.setField(tradeOrderRefundsApiImpl, "storeInfoService", storeInfoService);
		tradeOrderItemDetailList = MockUtils
				.getMockData("/com/okdeer/mall/refunds/params/mock-store-item.json", TradeOrderItemDetail.class).get(0);
	}
	public TradeOrderRefundsApiImplTest(int index,PhysicalOrderApplyParamDto physicalParam,StoreConsumerApplyParamDto storeParamDto) {
		this.index= index;
		this.physicalParam = physicalParam;
		this.storeParamDto = storeParamDto;
	}
	
	@Parameters
	public static Collection<Object[]> initParam() throws Exception {
		Collection<Object[]> initParams = new ArrayList<Object[]>();
		List<List<PhysicalOrderApplyParamDto>> refundQueryParamList = MockUtils
				.getMockData("/com/okdeer/mall/refunds/params/mock-physical.json", PhysicalOrderApplyParamDto.class);
		
		List<List<StoreConsumerApplyParamDto>> storeConsumerList = MockUtils
				.getMockData("/com/okdeer/mall/refunds/params/mock-storeConsumer.json", StoreConsumerApplyParamDto.class);
		for (int i = 0; i < refundQueryParamList.size(); i++) {
			for (int j = 0; j < 1; j++) {
				initParams.add(new Object[] { j,refundQueryParamList.get(i).get(j),storeConsumerList.get(i).get(j)});
			}
		}
		return initParams;
	}
	
	@Test
	@Rollback
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void physicalOrderApplyRefunds() throws Exception {
		beforeMethod(this, "physicalOrderApplyRefunds");
		//查询充值退款列表（用于财务系统，不分页）
		PhysicalOrderApplyDto dto = tradeOrderRefundsApiImpl.physicalOrderApplyRefunds(physicalParam);
		Assert.assertNotNull(ResultCodeEnum.SUCCESS.getDesc(),dto.getRefundId());
		afterTestMethod(this, "physicalOrderApplyRefunds");
	}
	
	@Test
	@Rollback(true)
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void storeConsumerApplyRefunds() throws Exception {
		beforeMethod(this, "storeConsumerApplyRefunds");
		given(tradeOrderItemDetailService.findById("8a80808558d780180158d7cebaaa000c")).willReturn(tradeOrderItemDetailList.get(index));
		
		given(tradeOrderItemDetailMapper.updateStatusWithRefundById("8a80808558d780180158d7cebaaa000c")).willReturn(1);
		
		
		//查询充值退款列表（用于财务系统，不分页）
		StoreConsumerApplyDto dto = tradeOrderRefundsApiImpl.storeConsumerApplyRefunds(storeParamDto);
		if(tradeOrderItemDetailList.get(index).getStatus() != ConsumeStatusEnum.noConsume){
			Assert.assertEquals(ResultCodeEnum.SUCCESS.getDesc(), DescriptConstants.CONSUME_CODE_INVALID, dto.getMsg());
		}else{
			Assert.assertNotNull(ResultCodeEnum.SUCCESS.getDesc(),dto.getRefundId());
		}
		afterTestMethod(this, "storeConsumerApplyRefunds");
	}
	
	@Test
	@Rollback(true)
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void updateRefundsStatus() throws Exception {
		beforeMethod(this, "updateRefundsStatus");
		OrderRefundQueryParamDto orderRefundQueryParamDto=new OrderRefundQueryParamDto();
		List<Integer> list = Lists.newArrayList();
		list.add(8);
		orderRefundQueryParamDto.setRefundStatusList(list);
		orderRefundQueryParamDto.setpSize(1);
		orderRefundQueryParamDto.setpNum(1);
		//查询充值退款列表（用于财务系统，不分页）
		PageUtils<OrderRefundsDto> result = tradeOrderRefundsApiImpl.orderRefund(orderRefundQueryParamDto);
		if(result != null  && CollectionUtils.isNotEmpty(result.getList())){
			//退款单待退款数统计
			boolean count = tradeOrderRefundsApiImpl.updateRefundsStatus(result.getList().get(0).getId(), "9", result.getList().get(0).getBuyerUserId());
			Assert.assertTrue(count);
		}
		afterTestMethod(this, "updateRefundsStatus");
	}
	
	@Test
	@Rollback(true)
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void applyRefund() throws Exception {
		beforeMethod(this, "applyRefund");
		PhysOrderApplyRefundParamDto applyRefundParamDto=new PhysOrderApplyRefundParamDto();
		Map<String,Object> param  = new HashMap<String,Object>();
		param.put("storeId", "56583c03276511e6aaff00163e010eb1");
		param.put("status", 5);
		List<TradeOrder> order = tradeOrderMapper.selectByParams(param);
		List<TradeOrderItem> items = tradeOrderItemMapper.selectOrderItemListById(order.get(0).getId());
		order.get(0).setUserId("141577260798e5eb9e1b8a0645b486c7");
		applyRefundParamDto.setTradeOrder(order.get(0));
		applyRefundParamDto.setTradeOrderItem(items.get(0));
		applyRefundParamDto.setMemo("1111");
		applyRefundParamDto.setReason("22222");
		applyRefundParamDto.setOrderResource(OrderResourceEnum.YSCAPP);
		//查询充值退款列表
		Assert.assertNotNull(tradeOrderRefundsApiImpl.applyRefund(applyRefundParamDto, RefundOrderTypeEnum.PHYSICAL_ORDER));
		afterTestMethod(this, "applyRefund");
	}
	
	@Test
	@Rollback(true)
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void refundsInfo() throws Exception{
		beforeMethod(this, "refundsInfo");
		OrderRefundQueryParamDto orderRefundQueryParamDto = new OrderRefundQueryParamDto();
		orderRefundQueryParamDto.setStoreId("5592971b276511e6aaff00163e010eb1");
		Map<String, Object> result = tradeOrderRefundsApiImpl.statisRefundsByParams(orderRefundQueryParamDto);
		List<Integer> list = Lists.newArrayList();
		list.add(6);
		list.add(13);
		orderRefundQueryParamDto.setRefundStatusList(list);
		orderRefundQueryParamDto.setpSize(10);
		orderRefundQueryParamDto.setpNum(1);
		//查询充值退款列表（用于财务系统，不分页）
		PageUtils<OrderRefundsDto> page = tradeOrderRefundsApiImpl.orderRefund(orderRefundQueryParamDto);
		tradeOrderRefundsApiImpl.orderRefundExport(orderRefundQueryParamDto);
		if(CollectionUtils.isNotEmpty(page.getList())){
			String id = page.getList().get(0).getId();
			RefundsMoneyDto dto = tradeOrderRefundsApiImpl.refundsInfo(id);
			Assert.assertNotNull(ResultCodeEnum.SUCCESS.getDesc(),dto);
			
			StoreInfo storeInfo = new StoreInfo();
			storeInfo.setAddress("1111");
			storeInfo.setStoreName("2222");
			storeInfo.setMobile("13723770509");
			storeInfo.setType(StoreTypeEnum.CLOUD_STORE);
			given(storeInfoService.findById(any())).willReturn(storeInfo);
			Assert.assertNotNull(ResultCodeEnum.SUCCESS.getDesc(),tradeOrderRefundsApiImpl.refundsDetail(id));
			List<String> ids = Lists.newArrayList();
			page.getList().forEach(e -> {
				ids.add(e.getId());
			});
			List<RefundsMoneyDto> dtoList = tradeOrderRefundsApiImpl.refundsInfo(ids);
			Assert.assertNotNull(ResultCodeEnum.SUCCESS.getDesc(),dtoList);
		}
		afterTestMethod(this, "refundsInfo");
	}
}
