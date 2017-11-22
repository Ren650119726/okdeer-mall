package com.okdeer.mall.order.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuPictureServiceApi;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.base.MockUtils;
import com.okdeer.mall.mock.MockFilePath;
import com.okdeer.mall.order.dto.GroupJoinUserDto;
import com.okdeer.mall.order.dto.TradeOrderGroupDetailDto;
import com.okdeer.mall.order.dto.TradeOrderQueryParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.service.TradeOrderGroupService;
import com.okdeer.mall.order.service.TradeOrderServiceApi;

/**
 * ClassName: TradeOrderSendServiceImplTest 
 * @Description: 寄送服务订单测试类
 * @author zhangkn
 * @date 2017年11月20日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.3			 2017年11月20日 			zhagnkn
 */
public class TradeOrderSendServiceImplTest extends BaseServiceTest implements MockFilePath{
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Resource
	private TradeOrderGroupService tradeOrderGroupService;
	@Resource
	private TradeOrderServiceApi tradeOrderServiceApi;
	
	@Mock
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;
	
	@Mock
	private GoodsStoreSkuPictureServiceApi goodsStoreSkuPictureServiceApi;
	
	/**
	 * mock服务商品列表
	 */
	private List<GoodsStoreSku> servSkuList;
	
	@Override
	public void initMocks() throws Exception{
//		this.servSkuList = MockUtils.getMockListData(MOCK_SERV_SKU_LIST_PATH, GoodsStoreSku.class);
//		
//		ReflectionTestUtils.setField(tradeOrderGroupService, "goodsStoreSkuServiceApi", goodsStoreSkuServiceApi);
//		ReflectionTestUtils.setField(tradeOrderGroupService, "goodsStoreSkuPictureServiceApi", goodsStoreSkuPictureServiceApi);
//		
//		given(goodsStoreSkuServiceApi.selectByPrimaryKey(anyString())).willAnswer((invocation) -> {
//			String arg = (String) invocation.getArguments()[0];
//			return servSkuList.stream().filter(item -> arg.equals(item.getId())).findFirst().get();
//		});
//		given(goodsStoreSkuPictureServiceApi.findMainPicByStoreSkuId(anyString())).willAnswer((invocation) -> {
//			String arg = (String) invocation.getArguments()[0];
//			return servSkuList.stream().filter(item -> arg.equals(item.getId())).findFirst().get().getGoodsStoreSkuPicture();
//		});
	}
	
	@Test
	public void findListForSend() throws Exception {
		TradeOrderQueryParamDto param = new TradeOrderQueryParamDto();		
		param.setStartTime("2017-01-01 00:00:00");
		param.setEndTime("2017-12-01 00:00:00");
		param.setType(OrderTypeEnum.SERVICE_EXPRESS_ORDER.ordinal());
		PageUtils<TradeOrder> page = tradeOrderServiceApi.findListForSend(param, 0,50);
		System.out.println("寄送服务订单测试列表方法："+page.getRows().size());
	}
	
}
