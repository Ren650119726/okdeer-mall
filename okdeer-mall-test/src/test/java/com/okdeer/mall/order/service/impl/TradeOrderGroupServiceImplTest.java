package com.okdeer.mall.order.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuPictureServiceApi;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.base.MockUtils;
import com.okdeer.mall.mock.MockFilePath;
import com.okdeer.mall.order.dto.GroupJoinUserDto;
import com.okdeer.mall.order.dto.TradeOrderGroupDetailDto;
import com.okdeer.mall.order.service.TradeOrderGroupService;


public class TradeOrderGroupServiceImplTest extends BaseServiceTest implements MockFilePath{
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Resource
	private TradeOrderGroupService tradeOrderGroupService;
	
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
		this.servSkuList = MockUtils.getMockListData(MOCK_SERV_SKU_LIST_PATH, GoodsStoreSku.class);
		
		ReflectionTestUtils.setField(tradeOrderGroupService, "goodsStoreSkuServiceApi", goodsStoreSkuServiceApi);
		ReflectionTestUtils.setField(tradeOrderGroupService, "goodsStoreSkuPictureServiceApi", goodsStoreSkuPictureServiceApi);
		
		given(goodsStoreSkuServiceApi.selectByPrimaryKey(anyString())).willAnswer((invocation) -> {
			String arg = (String) invocation.getArguments()[0];
			return servSkuList.stream().filter(item -> arg.equals(item.getId())).findFirst().get();
		});
		given(goodsStoreSkuPictureServiceApi.findMainPicByStoreSkuId(anyString())).willAnswer((invocation) -> {
			String arg = (String) invocation.getArguments()[0];
			return servSkuList.stream().filter(item -> arg.equals(item.getId())).findFirst().get().getGoodsStoreSkuPicture();
		});
	}
	
	@Test
	public void testFindGroupJoinUserListNullId() throws ServiceException {
		// 测试订单id不能为空
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("团购订单Id不能为空");
		tradeOrderGroupService.findGroupJoinUserList(null, "");
	}
	
	@Test
	public void testFindGroupJoinUserListNullScreen() throws ServiceException {
		// 测试屏幕分辨率不能为空
		thrown.expectMessage("分辨率不能为空");
		tradeOrderGroupService.findGroupJoinUserList("8a8080b35f2fa30b015f322fd2610009", null);
	}
	
	@Test
	public void testFindGroupJoinUserList() throws ServiceException {
		// 测试正常团购订单
		List<GroupJoinUserDto> joinUserList = tradeOrderGroupService.findGroupJoinUserList("8a8080b35f2fa30b015f322fd2610009", "640");
		assertEquals(5, joinUserList.size());
	}

	@Test
	public void testFindGroupJoinDetailIdNotExsists() throws ServiceException{
		// 测试团购订单id不存在
		thrown.expect(ServiceException.class);
		thrown.expectMessage("非法的请求参数");
		tradeOrderGroupService.findGroupJoinDetail("8a8080b35f2fa30b015f322fd2611119", "640");
	}
	
	@Test
	public void testFindGroupJoinDetail() throws ServiceException{
		// 测试正常团购订单
		TradeOrderGroupDetailDto detailDto = tradeOrderGroupService.findGroupJoinDetail("8a94e7545f145575015f145575ca0000", "640");
		assertEquals("8a94e7545f145575015f145575ca0000", detailDto.getGroupOrderId());
	}
}
